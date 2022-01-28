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

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.google.api.client.util.Data;
import com.google.api.gax.rpc.InvalidArgumentException;
import com.google.api.gax.rpc.NotFoundException;
import com.google.cloud.compute.v1.AddResourcePoliciesDiskRequest;
import com.google.cloud.compute.v1.CustomerEncryptionKey;
import com.google.cloud.compute.v1.DeleteDiskRequest;
import com.google.cloud.compute.v1.Disk;
import com.google.cloud.compute.v1.DisksAddResourcePoliciesRequest;
import com.google.cloud.compute.v1.DisksClient;
import com.google.cloud.compute.v1.DisksRemoveResourcePoliciesRequest;
import com.google.cloud.compute.v1.DisksResizeRequest;
import com.google.cloud.compute.v1.GetDiskRequest;
import com.google.cloud.compute.v1.InsertDiskRequest;
import com.google.cloud.compute.v1.Operation;
import com.google.cloud.compute.v1.RemoveResourcePoliciesDiskRequest;
import com.google.cloud.compute.v1.ResizeDiskRequest;
import com.google.cloud.compute.v1.SetLabelsDiskRequest;
import com.google.cloud.compute.v1.ZoneSetLabelsRequest;
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
 *         resource-policy: $(google::compute-resource-policy example-policy-disk-alpha)
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
     * The zone where the disk resides.
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
        this.type = type;
    }

    /**
     * The source image used to create this disk.
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
        setSourceImage(findById(ImageResource.class, disk.getSourceImage()));
    }

    @Override
    public boolean doRefresh() throws Exception {
        try (DisksClient client = createClient(DisksClient.class)) {

            Disk disk = getDisk(client);

            if (disk == null) {
                return false;
            }

            copyFrom(disk);

            return true;
        }
    }

    @Override
    public void doCreate(GyroUI ui, State state) throws Exception {
        try (DisksClient client = createClient(DisksClient.class)) {
            Disk.Builder disk = toDisk().toBuilder();
            disk.setSourceImageEncryptionKey(getSourceImageEncryptionKey() != null
                ? getSourceImageEncryptionKey().toCustomerEncryptionKey() : Data.nullOf(CustomerEncryptionKey.class));

            if (getType() != null) {
                disk.setType(getType());
            }

            if (getSourceImage() != null) {
                disk.setSourceImage(getSourceImage().getSelfLink());
            }

            disk.addAllResourcePolicies(getResourcePolicy().stream().map(ResourcePolicyResource::getSelfLink)
                .collect(Collectors.toList()));

            Operation operation = client.insertCallable().call(InsertDiskRequest.newBuilder()
                    .setProject(getProjectId())
                    .setZone(getZone())
                    .setDiskResource(disk)
                .buildPartial());

            waitForCompletion(operation, 30, TimeUnit.SECONDS);
        }

        refresh();
    }

    @Override
    public void doUpdate(GyroUI ui, State state, Resource current, Set<String> changedFieldNames) throws Exception {

        try (DisksClient client = createClient(DisksClient.class)) {

            if (changedFieldNames.contains("size-gb")) {
                saveSizeGb(client, (DiskResource) current);
            }

            if (changedFieldNames.contains("labels")) {
                saveLabels(client);
            }

            if (changedFieldNames.contains("resource-policy")) {
                saveResourcePolicies(client, (DiskResource) current);
            }
        }

        refresh();
    }

    @Override
    public void doDelete(GyroUI ui, State state) throws Exception {
        try (DisksClient client = createClient(DisksClient.class)) {
            Operation operation = client.deleteCallable().call(DeleteDiskRequest.newBuilder()
                .setProject(getProjectId())
                .setZone(getZone())
                .setDisk(getName())
                .build());

            waitForCompletion(operation);
        }
    }

    private void saveSizeGb(DisksClient client, DiskResource oldDiskResource) throws Exception {
        if (getSizeGb() < oldDiskResource.getSizeGb()) {
            throw new GyroException(String.format(
                "Size of the disk cannot be decreased once set. Current size %s.", oldDiskResource.getSizeGb()));
        }

        DisksResizeRequest.Builder builder = DisksResizeRequest.newBuilder().setSizeGb(getSizeGb());

        Operation operation = client.resizeCallable().call(
            ResizeDiskRequest.newBuilder()
                .setProject(getProjectId())
                .setZone(getZone())
                .setDisk(getName())
                .setDisksResizeRequestResource(builder)
                .build());

        waitForCompletion(operation);
    }

    private void saveLabels(DisksClient client) throws Exception {
        ZoneSetLabelsRequest.Builder builder = ZoneSetLabelsRequest.newBuilder();
        builder.putAllLabels(getLabels());
        builder.setLabelFingerprint(getLabelFingerprint());
        Operation operation = client.setLabelsCallable().call(SetLabelsDiskRequest.newBuilder()
            .setProject(getProjectId())
            .setZone(getZone())
            .setResource(getName())
            .setZoneSetLabelsRequestResource(builder)
            .build());

        waitForCompletion(operation);
    }

    private void saveResourcePolicies(DisksClient client, DiskResource current) throws Exception {
        List<String> removed = current.getResourcePolicy().stream()
            .filter(policy -> !getResourcePolicy().contains(policy))
            .map(ResourcePolicyResource::getSelfLink)
            .collect(Collectors.toList());
        List<String> added = getResourcePolicy().stream()
            .filter(policy -> !current.getResourcePolicy().contains(policy))
            .map(ResourcePolicyResource::getSelfLink)
            .collect(Collectors.toList());

        if (!removed.isEmpty()) {
            DisksRemoveResourcePoliciesRequest.Builder builder = DisksRemoveResourcePoliciesRequest.newBuilder()
                .addAllResourcePolicies(removed);

            Operation operation = client.removeResourcePoliciesCallable().call(
                RemoveResourcePoliciesDiskRequest.newBuilder()
                    .setProject(getProjectId())
                    .setZone(getZone())
                    .setDisk(getName())
                    .setDisksRemoveResourcePoliciesRequestResource(builder)
                    .build());

            waitForCompletion(operation);
        }

        if (!added.isEmpty()) {
            DisksAddResourcePoliciesRequest.Builder builder = DisksAddResourcePoliciesRequest.newBuilder()
                .addAllResourcePolicies(added);

            Operation operation = client.addResourcePoliciesCallable().call(
                AddResourcePoliciesDiskRequest.newBuilder()
                    .setProject(getProjectId())
                    .setZone(getZone())
                    .setDisk(getName())
                    .setDisksAddResourcePoliciesRequestResource(builder)
                    .build());

            waitForCompletion(operation);
        }
    }

    private Disk getDisk(DisksClient client) {
        Disk disk = null;

        try {
            disk = client.get(GetDiskRequest.newBuilder().setProject(getProjectId()).setZone(getZone())
                .setDisk(getName()).build());

        } catch (NotFoundException | InvalidArgumentException ex) {
            // ignore
        }

        return disk;
    }
}
