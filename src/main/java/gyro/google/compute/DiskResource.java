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

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.compute.Compute;
import com.google.api.services.compute.model.Disk;
import com.google.api.services.compute.model.DisksResizeRequest;
import com.google.api.services.compute.model.Operation;
import com.google.api.services.compute.model.ZoneSetLabelsRequest;
import gyro.core.GyroException;
import gyro.core.GyroUI;
import gyro.core.Type;
import gyro.core.resource.Output;
import gyro.core.resource.Resource;
import gyro.core.scope.State;
import gyro.core.validation.ConflictsWith;
import gyro.core.validation.Required;
import java.io.IOException;
import java.util.Set;

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
 *     end
 */
@Type("compute-disk")
public class DiskResource extends AbstractDiskResource {
    private String zone;
    private String sourceImage;
    private EncryptionKey sourceImageEncryptionKey;

    // Read-only
    private String sourceImageId;

    /**
     * The zone where the disk resides. (Required)
     */
    @Required
    public String getZone() {
        return zone;
    }

    public void setZone(String zone) {
        this.zone = zone;
    }

    /**
     * The source image used to create this disk.
     */
    @ConflictsWith("source-snapshot")
    public String getSourceImage() {
        return sourceImage;
    }

    public void setSourceImage(String sourceImage) {
        this.sourceImage = sourceImage;
    }

    /**
     * The encryption key of the source image. This is required if the source image is protected by a customer-supplied encryption key.
     *
     * @subresource gyro.google.compute.EncryptionKey
     */
    @ConflictsWith({"source-disk-encryption-key", "source-snapshot-encryption-key"})
    public EncryptionKey getSourceImageEncryptionKey() {
        return sourceImageEncryptionKey;
    }

    public void setSourceImageEncryptionKey(EncryptionKey sourceImageEncryptionKey) {
        this.sourceImageEncryptionKey = sourceImageEncryptionKey;
    }

    /**
     * The unique ID of the image used to create the disk.
     */
    @Output
    public String getSourceImageId() {
        return sourceImageId;
    }

    public void setSourceImageId(String sourceImageId) {
        this.sourceImageId = sourceImageId;
    }

    @Override
    public void copyMore(Disk disk) {
        setZone(disk.getZone().substring(disk.getZone().lastIndexOf("/") + 1));
        setSourceImage(disk.getSourceImage());
        setSourceImageId(disk.getSourceImageId());

        if (disk.getSourceImageEncryptionKey() != null) {
            EncryptionKey sourceImageEncryption = newSubresource(EncryptionKey.class);
            sourceImageEncryption.copyFrom(disk.getSourceImageEncryptionKey());
            setSourceImageEncryptionKey(sourceImageEncryption);
        }
    }

    @Override
    public void doCreate(GyroUI ui, State state, Disk disk) {
        Compute client = createComputeClient();

        disk.setZone(getZone());
        disk.setSourceImage(getSourceImage());
        disk.setSourceImageEncryptionKey(getSourceImageEncryptionKey() != null ? getSourceImageEncryptionKey().toCustomerEncryptionKey() : null);

        try {
            Compute.Disks.Insert insert = client.disks().insert(getProjectId(), getZone(), disk);
            Operation operation = insert.execute();
            Operation.Error error = waitForCompletion(client, operation);
            if (error != null) {
                throw new GyroException(error.toPrettyString());
            }

            refresh();
        } catch (Exception ex) {
            throw new GyroException(ex.getMessage(), ex.getCause());
        }
    }

    @Override
    public boolean refresh() {
        Compute client = createComputeClient();

        try {
            Disk disk = client.disks().get(getProjectId(), getZone(), getName()).execute();
            copyFrom(disk);

            return true;
        } catch (GoogleJsonResponseException je) {
            if (je.getDetails().getMessage().matches("The resource (.*) was not found")) {
                return false;
            } else {
                throw new GyroException(je.getDetails().getMessage());
            }
        } catch (IOException ex) {
            throw new GyroException(ex.getMessage(), ex.getCause());
        }
    }

    @Override
    public void delete(GyroUI ui, State state) {
        Compute compute = createComputeClient();

        try {
            compute.disks().delete(getProjectId(), getZone(), getName()).execute();
        } catch (IOException ex) {
            throw new GyroException(ex.getMessage(), ex.getCause());
        }
    }

    @Override
    public void update(GyroUI ui, State state, Resource current, Set<String> changedFieldNames) {
        Compute client = createComputeClient();

        if (changedFieldNames.contains("size-gb")) {
            saveSizeGb(client);
        }

        if (changedFieldNames.contains("labels")) {
            saveLabels(client);
        }

        if (changedFieldNames.contains("resource-policies")) {
            saveResourcePolicies(client, (RegionDiskResource) current);
        }

        refresh();
    }

    private void saveSizeGb(Compute client) {
        try {
            DisksResizeRequest resizeRequest = new DisksResizeRequest();
            resizeRequest.setSizeGb(getSizeGb());
            client.disks().resize(getProjectId(), getZone(), getName(), resizeRequest).execute();
        } catch (IOException ex) {
            throw new GyroException(ex.getMessage(), ex.getCause());
        }
    }

    private void saveLabels(Compute client) {
        try {
            ZoneSetLabelsRequest labelsRequest = new ZoneSetLabelsRequest();
            labelsRequest.setLabels(getLabels());
            labelsRequest.setLabelFingerprint(getLabelFingerprint());
            client.disks().setLabels(getProjectId(), getZone(), getName(), labelsRequest).execute();
        } catch (IOException ex) {
            throw new GyroException(ex.getMessage(), ex.getCause());
        }
    }

    private void saveResourcePolicies(Compute client, RegionDiskResource oldRegionDiskResource) {
    }
}
