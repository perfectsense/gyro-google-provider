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

import com.google.cloud.compute.v1.Disk;
import gyro.core.resource.Id;
import gyro.core.resource.Output;
import gyro.core.resource.Updatable;
import gyro.core.validation.ConflictsWith;
import gyro.core.validation.Range;
import gyro.core.validation.Regex;
import gyro.core.validation.Required;
import gyro.core.validation.ValidNumbers;
import gyro.core.validation.ValidStrings;
import gyro.google.Copyable;

public abstract class AbstractDiskResource extends ComputeResource implements Copyable<Disk> {

    private String name;
    private String description;
    private Long sizeGb;
    private SnapshotResource sourceSnapshot;
    private EncryptionKey diskEncryptionKey;
    private EncryptionKey sourceSnapshotEncryptionKey;
    private Map<String, String> labels;
    private Long physicalBlockSizeBytes;
    private List<ResourcePolicyResource> resourcePolicy;

    // Read-only
    private Disk.Status status;
    private String sourceSnapshotId;
    private String selfLink;
    private List<String> users;
    private String labelFingerprint;

    /**
     * The name of the disk.
     */
    @Required
    @Regex(value = "[a-z](?:[-a-z0-9]{0,61}[a-z0-9])?", message = "a string 1-63 characters long and match the regular expression `[a-z](?:[-a-z0-9]{0,61}[a-z0-9])?` which means the first character must be a lowercase letter, and all following characters must be a dash, lowercase letter, or digit, except the last character, which cannot be a dash")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * The description of the disk.
     */
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * The size of the persistent disk, specified in GB. Values must be in the range ``1`` to ``65536``, inclusive. The size can only be increased once it has been set. If you specify this field along with ``sourceSnapshot``, the value must not be less than the size of the snapshot.
     */
    @Updatable
    @Range(min = 1, max = 65536)
    public Long getSizeGb() {
        return sizeGb;
    }

    public void setSizeGb(Long sizeGb) {
        this.sizeGb = sizeGb;
    }

    /**
     * The source snapshot used to create the disk.
     */
    @ConflictsWith("source-image")
    public SnapshotResource getSourceSnapshot() {
        return sourceSnapshot;
    }

    public void setSourceSnapshot(SnapshotResource sourceSnapshot) {
        this.sourceSnapshot = sourceSnapshot;
    }

    /**
     * The encryption key used to encrypt the disk. Only use this if you have not specified a source snapshot. If you do not provide an encryption key when creating the disk, the disk will be encrypted using an automatically generated key.
     *
     * @subresource gyro.google.compute.EncryptionKey
     */
    public EncryptionKey getDiskEncryptionKey() {
        return diskEncryptionKey;
    }

    public void setDiskEncryptionKey(EncryptionKey diskEncryptionKey) {
        this.diskEncryptionKey = diskEncryptionKey;
    }

    /**
     * The encryption key of the source snapshot. This is required if the source snapshot is protected by a customer-supplied encryption key.
     *
     * @subresource gyro.google.compute.EncryptionKey
     */
    public EncryptionKey getSourceSnapshotEncryptionKey() {
        return sourceSnapshotEncryptionKey;
    }

    public void setSourceSnapshotEncryptionKey(EncryptionKey sourceSnapshotEncryptionKey) {
        this.sourceSnapshotEncryptionKey = sourceSnapshotEncryptionKey;
    }

    /**
     * Optional labels (key-value pairs) that can be applied to the disk. The only characters allowed are lowercase characters, international characters, numbers, ``-``, and ``_``. Key and value must be under 64 characters.
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
     * The physical block size of the disk, in bytes. Defaults to ``4096``.
     */
    @ValidNumbers({ 4096, 16384 })
    public Long getPhysicalBlockSizeBytes() {
        if (physicalBlockSizeBytes == null) {
            physicalBlockSizeBytes = 4096L;
        }

        return physicalBlockSizeBytes;
    }

    public void setPhysicalBlockSizeBytes(Long physicalBlockSizeBytes) {
        this.physicalBlockSizeBytes = physicalBlockSizeBytes;
    }

    /**
     * Adds an existing resource policy to a disk which will be applied to this disk for scheduling snapshot creation.
     */
    @Updatable
    public List<ResourcePolicyResource> getResourcePolicy() {
        if (resourcePolicy == null) {
            resourcePolicy = new ArrayList<>();
        }
        return resourcePolicy;
    }

    public void setResourcePolicy(List<ResourcePolicyResource> resourcePolicy) {
        this.resourcePolicy = resourcePolicy;
    }

    /**
     * The status of disk creation.
     */
    @Output
    @ValidStrings({ "CREATING", "RESTORING", "FAILED", "READY", "DELETING" })
    public Disk.Status getStatus() {
        return status;
    }

    public void setStatus(Disk.Status status) {
        this.status = status;
    }

    /**
     * The unique ID of the snapshot used to create the disk.
     */
    @Output
    public String getSourceSnapshotId() {
        return sourceSnapshotId;
    }

    public void setSourceSnapshotId(String sourceSnapshotId) {
        this.sourceSnapshotId = sourceSnapshotId;
    }

    /**
     * The fully-qualified URL linking back to the disk.
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
     * Links to the attached instances of the disk.
     */
    @Output
    public List<String> getUsers() {
        if (users == null) {
            users = new ArrayList<>();
        }
        return users;
    }

    public void setUsers(List<String> users) {
        this.users = users;
    }

    /**
     * The fingerprint for the labels being applied to the disk, which is updated any time the labels change.
     */
    @Output
    public String getLabelFingerprint() {
        return labelFingerprint;
    }

    public void setLabelFingerprint(String labelFingerprint) {
        this.labelFingerprint = labelFingerprint;
    }

    @Override
    public void copyFrom(Disk disk) {
        setName(disk.getName());
        setLabels(disk.getLabelsMap());
        setUsers(disk.getUsersList());
        setSelfLink(disk.getSelfLink());
        setDescription(disk.getDescription());
        setSizeGb(disk.getSizeGb());
        setPhysicalBlockSizeBytes(disk.getPhysicalBlockSizeBytes());

        if (disk.hasSourceSnapshot()) {
            setSourceSnapshot(findById(SnapshotResource.class, disk.getSourceSnapshot()));
        }

        if (disk.hasStatus()) {
            setStatus(Disk.Status.valueOf(disk.getStatus()));
        }

        if (disk.hasSourceSnapshotId()) {
            setSourceSnapshotId(disk.getSourceSnapshotId());
        }

        if (disk.hasLabelFingerprint()) {
            setLabelFingerprint(disk.getLabelFingerprint());
        }
    }

    protected Disk toDisk() {
        Disk.Builder disk = Disk.newBuilder()
            .setName(getName());

        if (getDescription() != null) {
            disk.setDescription(getDescription());
        }

        if (getLabels() != null) {
            disk.putAllLabels(getLabels());
        }

        if (getSizeGb() != null) {
            disk.setSizeGb(getSizeGb());
        }

        if (getPhysicalBlockSizeBytes() != null) {
            disk.setPhysicalBlockSizeBytes(getPhysicalBlockSizeBytes());
        }

        if (getDiskEncryptionKey() != null) {
            disk.setDiskEncryptionKey(getDiskEncryptionKey().toCustomerEncryptionKey());
        }

        if (getSourceSnapshotEncryptionKey() != null) {
            disk.setSourceSnapshotEncryptionKey(getSourceSnapshotEncryptionKey().toCustomerEncryptionKey());
        }

        if (getSourceSnapshot() != null) {
            disk.setSourceSnapshot(getSourceSnapshot().getSelfLink());
        }

        return disk.build();
    }
}
