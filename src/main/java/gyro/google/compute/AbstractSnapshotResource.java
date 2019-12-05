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

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.compute.Compute;
import com.google.api.services.compute.model.GlobalSetLabelsRequest;
import com.google.api.services.compute.model.Snapshot;
import com.google.cloud.compute.v1.ProjectGlobalSnapshotName;
import gyro.core.GyroException;
import gyro.core.GyroUI;
import gyro.core.resource.Id;
import gyro.core.resource.Output;
import gyro.core.resource.Resource;
import gyro.core.resource.Updatable;
import gyro.core.scope.State;
import gyro.core.validation.ConflictsWith;
import gyro.core.validation.Regex;
import gyro.core.validation.Required;
import gyro.google.Copyable;

public abstract class AbstractSnapshotResource extends ComputeResource implements Copyable<Snapshot> {

    private String name;
    private String description;
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
     * The name of the snapshot. Must be 1-63 characters long, and the first character must be a lowercase letter. All other characters must be a lowercase letter, digit, or ``-``, except the last character, which cannot be a ``-``. (Required)
     */
    @Id
    @Required
    @Regex("[a-z](?:[-a-z0-9]{0,61}[a-z0-9])?")
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
     * The encryption key used to encrypt the snapshot. If you do not provide an encryption key when creating the snapshot, the snapshot will be encrypted using an automatically generated key.
     *
     * @subresource gyro.google.compute.EncryptionKey
     */
    @ConflictsWith("source-disk-encryption-key")
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
    @ConflictsWith("snapshot-encryption-key")
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
    @Output
    public String getSelfLink() {
        return selfLink;
    }

    public void setSelfLink(String selfLink) {
        this.selfLink = selfLink != null ? toSnapshotUrl(getProjectId(), selfLink) : null;
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
        setDescription(snapshot.getDescription());
        setLabels(snapshot.getLabels());
        setStorageLocations(snapshot.getStorageLocations());
        setStatus(snapshot.getStatus());
        setSourceDiskId(snapshot.getSourceDiskId());
        setDiskSizeGb(snapshot.getDiskSizeGb());
        setStorageBytes(snapshot.getStorageBytes());
        setSelfLink(snapshot.getSelfLink());
        setLabelFingerprint(snapshot.getLabelFingerprint());

        if (snapshot.getSourceDiskEncryptionKey() != null) {
            EncryptionKey sourceDiskEncryption = newSubresource(EncryptionKey.class);
            sourceDiskEncryption.copyFrom(snapshot.getSourceDiskEncryptionKey());
            setSourceDiskEncryptionKey(sourceDiskEncryption);
        }

        if (snapshot.getSnapshotEncryptionKey() != null) {
            EncryptionKey snapshotEncryption = newSubresource(EncryptionKey.class);
            snapshotEncryption.copyFrom(snapshot.getSnapshotEncryptionKey());
            setSnapshotEncryptionKey(snapshotEncryption);
        }
    }

    @Override
    public boolean refresh() {
        Compute client = createComputeClient();

        try {
            Snapshot snapshot = client.snapshots().get(getProjectId(), getName()).execute();
            copyFrom(snapshot);

            return true;
        } catch (GoogleJsonResponseException je) {
            if (je.getDetails().getCode() == 404) {
                return false;
            } else {
                throw new GyroException(je.getDetails().getMessage());
            }
        } catch (Exception ex) {
            throw new GyroException(ex.getMessage(), ex.getCause());
        }
    }

    protected Snapshot toSnapshot() {
        Snapshot snapshot = new Snapshot();
        snapshot.setName(getName());
        snapshot.setDescription(getDescription());
        snapshot.setLabels(getLabels());
        snapshot.setStorageLocations(getStorageLocations());
        snapshot.setSourceDiskEncryptionKey(
            getSourceDiskEncryptionKey() != null ? getSourceDiskEncryptionKey().toCustomerEncryptionKey() : null);
        snapshot.setSnapshotEncryptionKey(
            getSnapshotEncryptionKey() != null ? getSnapshotEncryptionKey().toCustomerEncryptionKey() : null);

        return snapshot;
    }

    @Override
    public void update(GyroUI ui, State state, Resource current, Set<String> changedFieldNames) {
        try {
            Compute client = createComputeClient();

            GlobalSetLabelsRequest labelsRequest = new GlobalSetLabelsRequest();
            labelsRequest.setLabels(getLabels());
            labelsRequest.setLabelFingerprint(getLabelFingerprint());
            client.snapshots().setLabels(getProjectId(), getName(), labelsRequest).execute();
        } catch (GoogleJsonResponseException je) {
            throw new GyroException(je.getDetails().getMessage());
        } catch (Exception ex) {
            throw new GyroException(ex.getMessage(), ex.getCause());
        }

        refresh();
    }

    @Override
    public void delete(GyroUI ui, State state) {
        Compute compute = createComputeClient();

        try {
            compute.snapshots().delete(getProjectId(), getName()).execute();
        } catch (GoogleJsonResponseException je) {
            throw new GyroException(je.getDetails().getMessage());
        } catch (Exception ex) {
            throw new GyroException(ex.getMessage(), ex.getCause());
        }
    }

    static String toSnapshotUrl(String projectId, String snapshot) {
        String parseSnapshot = ComputeUtils.formatResource(projectId, snapshot);
        if (ProjectGlobalSnapshotName.isParsableFrom(parseSnapshot)) {
            return ProjectGlobalSnapshotName.parse(parseSnapshot).toString();
        }
        return snapshot;
    }
}
