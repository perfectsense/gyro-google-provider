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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.api.gax.rpc.InvalidArgumentException;
import com.google.api.gax.rpc.NotFoundException;
import com.google.cloud.compute.v1.CreateSnapshotDiskRequest;
import com.google.cloud.compute.v1.CreateSnapshotRegionDiskRequest;
import com.google.cloud.compute.v1.DeleteSnapshotRequest;
import com.google.cloud.compute.v1.DisksClient;
import com.google.cloud.compute.v1.GetSnapshotRequest;
import com.google.cloud.compute.v1.GlobalSetLabelsRequest;
import com.google.cloud.compute.v1.Operation;
import com.google.cloud.compute.v1.RegionDisksClient;
import com.google.cloud.compute.v1.SetLabelsSnapshotRequest;
import com.google.cloud.compute.v1.Snapshot;
import com.google.cloud.compute.v1.SnapshotsClient;
import gyro.core.GyroUI;
import gyro.core.Type;
import gyro.core.resource.Id;
import gyro.core.resource.Output;
import gyro.core.resource.Resource;
import gyro.core.resource.Updatable;
import gyro.core.scope.State;
import gyro.core.validation.ConflictsWith;
import gyro.core.validation.Regex;
import gyro.core.validation.Required;
import gyro.core.validation.ValidationError;
import gyro.google.Copyable;

/**
 * Creates a snapshot of a disk.
 *
 * Example
 * -------
 *
 * .. code-block:: gyro
 *
 *     google::compute-snapshot snapshot-example
 *         name: "snapshot-example"
 *         description: "snapshot-example-desc"
 *         source-disk: $(google::compute-disk disk-example)
 *         labels: {
 *             label-key: 'label-value'
 *         }
 *
 *         source-disk-encryption-key
 *             raw-key: "SGVsbG8gZnJvbSBHb29nbGUgQ2xvdWQgUGxhdGZvcm0="
 *         end
 *
 *         snapshot-encryption-key
 *             raw-key: "AGVsbG8gZnJvbSBHb29nbGUgQ2xvdWQgUGxhdGZvcm0="
 *         end
 *
 *         storage-locations: [
 *             "us-west1"
 *         ]
 *     end
 *
 * .. code-block:: gyro
 *
 *     google::compute-snapshot region-snapshot-example
 *         name: "region-snapshot-example"
 *         source-region-disk: $(google::compute-region-disk region-disk-example)
 *
 *         storage-locations: [
 *             "us"
 *         ]
 *     end
 */

@Type("compute-snapshot")
public class SnapshotResource extends ComputeResource implements Copyable<Snapshot> {

    private String name;
    private String description;
    private DiskResource sourceDisk;
    private RegionDiskResource sourceRegionDisk;
    private EncryptionKey snapshotEncryptionKey;
    private EncryptionKey sourceDiskEncryptionKey;
    private Map<String, String> labels;
    private List<String> storageLocations;

    // Read-only
    private String status;
    private String sourceDiskId;
    private Long diskSizeGb;
    private Long storageBytes;
    private String selfLink;
    private String labelFingerprint;

    /**
     * The name of the snapshot.
     */
    @Required
    @Regex(value = "[a-z](?:[-a-z0-9]{0,61}[a-z0-9])?", message = "a string 1-63 characters long and the first character must be a lowercase letter, and all following characters must be a dash, lowercase letter, or digit, except the last character, which cannot be a dash")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * The description of the snapshot.
     */
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * The source disk used to create this snapshot.
     */
    @ConflictsWith("region-source-disk")
    public DiskResource getSourceDisk() {
        return sourceDisk;
    }

    public void setSourceDisk(DiskResource sourceDisk) {
        this.sourceDisk = sourceDisk;
    }

    /**
     * The regional source disk used to create this snapshot.
     */
    @ConflictsWith("source-disk")
    public RegionDiskResource getSourceRegionDisk() {
        return sourceRegionDisk;
    }

    public void setSourceRegionDisk(RegionDiskResource sourceRegionDisk) {
        this.sourceRegionDisk = sourceRegionDisk;
    }

    /**
     * The encryption key used to encrypt the snapshot. If you do not provide an encryption key when creating the snapshot, the snapshot will be encrypted using an automatically generated key.
     *
     * @subresource gyro.google.compute.EncryptionKey
     */
    public EncryptionKey getSnapshotEncryptionKey() {
        return snapshotEncryptionKey;
    }

    public void setSnapshotEncryptionKey(EncryptionKey snapshotEncryptionKey) {
        this.snapshotEncryptionKey = snapshotEncryptionKey;
    }

    /**
     * The encryption key of the source disk. This is required if the source disk is protected by a customer-supplied encryption key.
     *
     * @subresource gyro.google.compute.EncryptionKey
     */
    public EncryptionKey getSourceDiskEncryptionKey() {
        return sourceDiskEncryptionKey;
    }

    public void setSourceDiskEncryptionKey(EncryptionKey sourceDiskEncryptionKey) {
        this.sourceDiskEncryptionKey = sourceDiskEncryptionKey;
    }

    /**
     * Optional labels (key-value pairs) that can be applied to the snapshot. The only characters allowed are lowercase characters, international characters, numbers, ``-``, and ``_``. Key and value must be under 64 characters.
     */
    @Updatable
    public Map<String, String> getLabels() {
        if (labels == null) {
            labels = new HashMap<>();
        }

        return labels;
    }

    public void setLabels(Map<String, String> labels) {
        this.labels = labels;
    }

    /**
     * Storage location of the snapshot. Can be regional (eg. ``us-central1``) or multi-regional (eg. ``us``).
     */
    public List<String> getStorageLocations() {
        if (storageLocations == null) {
            storageLocations = new ArrayList<>();
        }
        return storageLocations;
    }

    public void setStorageLocations(List<String> storageLocations) {
        this.storageLocations = storageLocations;
    }

    /**
     * The status of snapshot creation. Values can be: ``CREATING``, ``UPLOADING``, ``FAILED``, ``READY``, or ``DELETING``.
     */
    @Output
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * The ID value of the disk used to create the snapshot.
     */
    @Output
    public String getSourceDiskId() {
        return sourceDiskId;
    }

    public void setSourceDiskId(String sourceDiskId) {
        this.sourceDiskId = sourceDiskId;
    }

    /**
     * The size of the disk used to create the snapshot, specified in GB.
     */
    @Output
    public Long getDiskSizeGb() {
        return diskSizeGb;
    }

    public void setDiskSizeGb(Long diskSizeGb) {
        this.diskSizeGb = diskSizeGb;
    }

    /**
     * The size of the storage used by the snapshot. This is expected to change with snapshot creation/deletion as snapshots share storage.
     */
    @Output
    public Long getStorageBytes() {
        return storageBytes;
    }

    public void setStorageBytes(Long storageBytes) {
        this.storageBytes = storageBytes;
    }

    /**
     * The fully-qualified URL linking back to the snapshot.
     */
    @Id
    @Output
    public String getSelfLink() {
        return selfLink;
    }

    public void setSelfLink(String selfLink) {
        this.selfLink = selfLink;
    }

    /**
     * The fingerprint for the labels being applied to the snapshot, which is updated any time the labels change.
     */
    @Output
    public String getLabelFingerprint() {
        return labelFingerprint;
    }

    public void setLabelFingerprint(String labelFingerprint) {
        this.labelFingerprint = labelFingerprint;
    }

    @Override
    public void copyFrom(Snapshot snapshot) {
        setName(snapshot.getName());

        if (snapshot.hasSelfLink()) {
            setSelfLink(snapshot.getSelfLink());
        }

        if (snapshot.hasDescription()) {
            setDescription(snapshot.getDescription());
        }

        if (snapshot.hasStatus()) {
            setStatus(snapshot.getStatus());
        }

        if (snapshot.hasSourceDiskId()) {
            setSourceDiskId(snapshot.getSourceDiskId());
        }

        if (snapshot.hasDiskSizeGb()) {
            setDiskSizeGb(snapshot.getDiskSizeGb());
        }

        if (snapshot.hasStorageBytes()) {
            setStorageBytes(snapshot.getStorageBytes());
        }

        if (snapshot.hasLabelFingerprint()) {
            setLabelFingerprint(snapshot.getLabelFingerprint());
        }

        if (snapshot.hasSourceDisk()) {
            setSourceDisk(findById(DiskResource.class, snapshot.getSourceDisk()));
            setSourceRegionDisk(findById(RegionDiskResource.class, snapshot.getSourceDisk()));
        }

        setLabels(snapshot.getLabelsMap());
        setStorageLocations(snapshot.getStorageLocationsList());
    }

    @Override
    public boolean doRefresh() throws Exception {
        try (SnapshotsClient client = createClient(SnapshotsClient.class)) {
            Snapshot snapshot = getSnapshot(client);

            if (snapshot == null) {
                return false;
            }

            copyFrom(snapshot);

            return true;
        }
    }

    public void doCreateZoneSnapshot(GyroUI ui, State state) throws Exception {
        try (DisksClient client = createClient(DisksClient.class)) {
            Snapshot.Builder builder = Snapshot.newBuilder().setName(getName());
            builder.putAllLabels(getLabels());
            builder.addAllStorageLocations(getStorageLocations());

            if (getDescription() != null) {
                builder.setDescription(getDescription());
            }

            if (getSourceDiskEncryptionKey() != null) {
                builder.setSourceDiskEncryptionKey(getSourceDiskEncryptionKey().toCustomerEncryptionKey());
            }

            if (getSnapshotEncryptionKey() != null) {
                builder.setSnapshotEncryptionKey(getSnapshotEncryptionKey().toCustomerEncryptionKey());
            }

            builder.setSourceDisk(getSourceDisk().getSelfLink());

            Operation operation = client.createSnapshotCallable().call(
                CreateSnapshotDiskRequest.newBuilder()
                    .setProject(getProjectId())
                    .setDisk(getSourceDisk().getName())
                    .setSnapshotResource(builder)
                    .build());

            waitForCompletion(operation);
        }
    }

    public void doCreateRegionSnapshot(GyroUI ui, State state) throws Exception {
        try (RegionDisksClient client = createClient(RegionDisksClient.class)) {
            Snapshot.Builder builder = Snapshot.newBuilder().setName(getName());
            builder.putAllLabels(getLabels());
            builder.addAllStorageLocations(getStorageLocations());

            if (getDescription() != null) {
                builder.setDescription(getDescription());
            }

            if (getSourceDiskEncryptionKey() != null) {
                builder.setSourceDiskEncryptionKey(getSourceDiskEncryptionKey().toCustomerEncryptionKey());
            }

            if (getSnapshotEncryptionKey() != null) {
                builder.setSnapshotEncryptionKey(getSnapshotEncryptionKey().toCustomerEncryptionKey());
            }

            builder.setSourceDisk(getSourceRegionDisk().getSelfLink());

            Operation operation = client.createSnapshotCallable().call(
                    CreateSnapshotRegionDiskRequest.newBuilder()
                        .setProject(getProjectId())
                        .setDisk(getSourceRegionDisk().getName())
                        .setSnapshotResource(builder)
                        .setRegion(getSourceRegionDisk().getRegion())
                        .build());

            waitForCompletion(operation);
        }
    }

    @Override
    public void doCreate(GyroUI ui, State state) throws Exception {
        if (getSourceDisk() != null) {
            doCreateZoneSnapshot(ui, state);
        } else {
            doCreateRegionSnapshot(ui, state);
        }

        refresh();
    }

    @Override
    public void doUpdate(GyroUI ui, State state, Resource current, Set<String> changedFieldNames) throws Exception {
        try (SnapshotsClient client = createClient(SnapshotsClient.class)) {

            GlobalSetLabelsRequest.Builder builder = GlobalSetLabelsRequest.newBuilder();
            builder.putAllLabels(getLabels());

            if (getLabelFingerprint() != null) {
                builder.setLabelFingerprint(getLabelFingerprint());
            }

            Operation operation = client.setLabelsCallable().call(SetLabelsSnapshotRequest.newBuilder()
                .setProject(getProjectId())
                .setResource(getName())
                .setGlobalSetLabelsRequestResource(builder)
                .build());

            waitForCompletion(operation);
        }

        refresh();
    }

    @Override
    public void doDelete(GyroUI ui, State state) throws Exception {
        try (SnapshotsClient client = createClient(SnapshotsClient.class)) {
            Operation operation = client.deleteCallable().call(DeleteSnapshotRequest.newBuilder()
                .setProject(getProjectId())
                .setSnapshot(getName())
                .build());

            waitForCompletion(operation);
        }
    }

    @Override
    public List<ValidationError> validate(Set<String> configuredFields) {
        List<ValidationError> errors = new ArrayList<>();

        if (getSourceDisk() == null && getSourceRegionDisk() == null) {
            errors.add(new ValidationError(
                this,
                null,
                "Either a 'source-disk' or 'source-region-disk' is required when creating a snapshot."));
        }

        if (getStorageLocations().size() > 1) {
            errors.add(new ValidationError(
                this,
                "storage-locations",
                "Attaching more than one storage location is not supported."));
        }

        if (getSourceDiskEncryptionKey() != null && getSnapshotEncryptionKey() == null) {
            errors.add(new ValidationError(
                this,
                null,
                "A 'snapshot-encryption-key' is required when providing a 'source-disk-encryption-key'."));
        }

        return errors;
    }

    private Snapshot getSnapshot(SnapshotsClient client) {
        Snapshot snapshot = null;

        try {
            snapshot = client.get(GetSnapshotRequest.newBuilder().setProject(getProjectId())
                .setSnapshot(getName()).build());

        } catch (NotFoundException | InvalidArgumentException ex) {
            // ignore
        }

        return snapshot;
    }
}
