/*
 * Copyright 2019, Perfect Sense, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package gyro.google.compute;

import java.util.Set;

import com.google.api.client.util.Data;
import com.google.api.services.compute.Compute;
import com.google.api.services.compute.model.CustomerEncryptionKey;
import com.google.api.services.compute.model.Disk;
import com.google.api.services.compute.model.DisksResizeRequest;
import com.google.api.services.compute.model.Operation;
import com.google.api.services.compute.model.ZoneSetLabelsRequest;
import com.google.cloud.compute.v1.ProjectZoneDiskName;
import com.google.cloud.compute.v1.ProjectZoneDiskTypeName;
import gyro.core.GyroException;
import gyro.core.GyroUI;
import gyro.core.Type;
import gyro.core.resource.Resource;
import gyro.core.scope.State;
import gyro.core.validation.ConflictsWith;
import gyro.core.validation.Required;

/**
 * Creates a zonal disk.
 *
 * Example
 * -------
 *
 * .. code-block:: gyro
 *
 *     google::compute-disk disk-example
 *         name: "disk-example"
 *         description: "disk-example-desc"
 *         zone: "us-central1-a"
 *         size-gb: 32
 *         type: "pd-standard"
 *         labels: {
 *             label-key: 'label-value'
 *         }
 *         physical-block-size-bytes: 4096
 *     end
 *
 * .. code-block:: gyro
 *
 *     google::compute-disk disk-image-example
 *         name: "disk-image-example"
 *         description: "disk-image-example-desc"
 *         zone: "us-west1-a"
 *         source-image: $(google::compute-image image-example)
 *
 *         source-image-encryption-key
 *             raw-key: "SGVsbG8gZnJvbSBHb29nbGUgQ2xvdWQgUGxhdGZvcm0="
 *         end
 *     end
 */
@Type("compute-disk")
public class DiskResource extends AbstractDiskResource {

    private String zone;
    private String type;
    private ImageResource sourceImage;
    private EncryptionKey sourceImageEncryptionKey;

    /**
     * The zone where the disk resides. (Required)
     */
    @Required
    public String getZone() {
        return zone;
    }

    public void setZone(String zone) {
        this.zone = zone != null ? zone.substring(zone.lastIndexOf("/") + 1) : null;
    }

    /**
     * The disk type used to create the disk.
     */
    public String getType() {
        return type;
    }

    public void setType(String type) {
        // Full URLs are required for type, so format the type to a full URL so it is accepted
        requires("zone");
        this.type = type != null ? toZoneDiskTypeUrl(getProjectId(), type, getZone()) : null;
    }

    /**
     * The source image used to create this disk. Conflicts with ``source-snapshot``.
     */
    @ConflictsWith("source-snapshot")
    public ImageResource getSourceImage() {
        return sourceImage;
    }

    public void setSourceImage(ImageResource sourceImage) {
        this.sourceImage = sourceImage;
    }

    /**
     * The encryption key of the source image. This is required if the source image is protected by a customer-supplied encryption key.
     *
     * @subresource gyro.google.compute.EncryptionKey
     */
    public EncryptionKey getSourceImageEncryptionKey() {
        return sourceImageEncryptionKey;
    }

    public void setSourceImageEncryptionKey(EncryptionKey sourceImageEncryptionKey) {
        this.sourceImageEncryptionKey = sourceImageEncryptionKey;
    }

    @Override
    public void copyFrom(Disk disk) {
        super.copyFrom(disk);

        setZone(disk.getZone());
        setType(disk.getType());

        setSourceImage(null);
        if (ImageResource.parseImage(getProjectId(), disk.getSourceImage()) != null
            || ImageResource.parseFamilyImage(getProjectId(), disk.getSourceImage()) != null) {
            setSourceImage(findById(ImageResource.class, disk.getSourceImage()));
        }
    }

    @Override
    public boolean doRefresh() throws Exception {
        Compute client = createComputeClient();

        Disk disk = client.disks().get(getProjectId(), getZone(), getName()).execute();
        copyFrom(disk);

        return true;
    }

    @Override
    public void doCreate(GyroUI ui, State state) throws Exception {
        Compute client = createComputeClient();

        Disk disk = toDisk();
        disk.setZone(getZone());
        disk.setType(getType());
        disk.setSourceImage(getSourceImage() != null ? getSourceImage().getSelfLink() : null);
        disk.setSourceImageEncryptionKey(getSourceImageEncryptionKey() != null
            ? getSourceImageEncryptionKey().toCustomerEncryptionKey()
            : Data.nullOf(CustomerEncryptionKey.class));

        Compute.Disks.Insert insert = client.disks().insert(getProjectId(), getZone(), disk);
        createDisk(client, insert);

        refresh();
    }

    @Override
    public void doUpdate(GyroUI ui, State state, Resource current, Set<String> changedFieldNames) throws Exception {
        Compute client = createComputeClient();

        if (changedFieldNames.contains("size-gb")) {
            saveSizeGb(client, (DiskResource) current);
        }

        if (changedFieldNames.contains("labels")) {
            saveLabels(client);
        }

        refresh();
    }

    @Override
    public void doDelete(GyroUI ui, State state) throws Exception {
        Compute client = createComputeClient();

        Operation operation = client.disks().delete(getProjectId(), getZone(), getName()).execute();
        waitForCompletion(client, operation);
    }

    private void saveSizeGb(Compute client, DiskResource oldDiskResource) throws Exception {
        if (getSizeGb() < oldDiskResource.getSizeGb()) {
            throw new GyroException(String.format(
                "Size of the disk cannot be decreased once set. Current size %s.",
                oldDiskResource.getSizeGb()));
        }

        DisksResizeRequest resizeRequest = new DisksResizeRequest();
        resizeRequest.setSizeGb(getSizeGb());
        Operation operation = client.disks().resize(getProjectId(), getZone(), getName(), resizeRequest).execute();
        waitForCompletion(client, operation);
    }

    private void saveLabels(Compute client) throws Exception {
        ZoneSetLabelsRequest labelsRequest = new ZoneSetLabelsRequest();
        labelsRequest.setLabels(getLabels());
        labelsRequest.setLabelFingerprint(getLabelFingerprint());
        Operation operation = client.disks().setLabels(getProjectId(), getZone(), getName(), labelsRequest).execute();
        waitForCompletion(client, operation);
    }

    static ProjectZoneDiskName parseDisk(String projectId, String selfLink) {
        if (selfLink == null) {
            return null;
        }

        String parseDiskName = formatResource(projectId, selfLink);
        if (ProjectZoneDiskName.isParsableFrom(parseDiskName)) {
            return ProjectZoneDiskName.parse(parseDiskName);
        }
        return null;
    }

    static String toZoneDiskTypeUrl(String projectId, String type, String zone) {
        String parseDiskType = formatResource(projectId, type);
        if (ProjectZoneDiskTypeName.isParsableFrom(parseDiskType)) {
            return ProjectZoneDiskTypeName.parse(parseDiskType).toString();
        }
        return ProjectZoneDiskTypeName.format(type, projectId, zone);
    }
}
