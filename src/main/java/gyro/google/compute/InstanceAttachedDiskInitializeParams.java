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
import java.util.stream.Collectors;

import com.google.api.services.compute.model.AttachedDiskInitializeParams;
import gyro.core.resource.Diffable;
import gyro.core.validation.Regex;
import gyro.google.Copyable;

public class InstanceAttachedDiskInitializeParams extends Diffable implements Copyable<AttachedDiskInitializeParams> {

    private String sourceImage;
    private String description;
    private String diskName;
    private Long diskSizeGb;
    private String diskType;
    private Map<String, String> labels;
    private EncryptionKey sourceImageEncryptionKey;
    private EncryptionKey sourceSnapshotEncryptionKey;

    /**
     * The source image to create this disk in the form of a URL path. See `Images <https://cloud.google.com/compute/docs/images/>`_.
     */
    public String getSourceImage() {
        return sourceImage;
    }

    public void setSourceImage(String sourceImage) {
        this.sourceImage = sourceImage;
    }

    /**
     * An optional description. Provide this property when creating the disk.
     */
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * The disk name. Unspecified, it will use the name of the instance. If the disk with the instance name exists already in the given zone/region a new name will be automatically generated.
     */
    @Regex("(?:[a-z](?:[-a-z0-9]{0,61}[a-z0-9])?)")
    public String getDiskName() {
        return diskName;
    }

    public void setDiskName(String diskName) {
        this.diskName = diskName;
    }

    /**
     * Size of the disk in base-2 GB. Unspecified, the disk will be the same size as the image (usually 10GB). If specified, the size must be equal to or larger than 10GB.
     */
    public Long getDiskSizeGb() {
        return diskSizeGb;
    }

    public void setDiskSizeGb(Long diskSizeGb) {
        this.diskSizeGb = diskSizeGb;
    }

    /**
     * The disk type to use to create the instance. Unspecified, the default is pd-standard, specified using the full URL (e.g. https://www.googleapis.com/compute/v1/projects/project/zones/zone/diskTypes/pd-standard). Other values include pd-ssd and local-ssd. Provide either the full or partial URL. Note that for ``InstanceTemplate``, this is the name of the disk type, not URL.
     */
    public String getDiskType() {
        return diskType;
    }

    public void setDiskType(String diskType) {
        this.diskType = diskType;
    }

    /**
     * Labels to apply to this disk. Only applicable for persistent disks.
     */
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
     * Encryption key of the source image. Required if the source image is protected by a customer-supplied encryption key. Instance templates do not store customer-supplied encryption keys, so you cannot create disks or instances in a managed instance group if the source images are encrypted with your own keys.
     */
    public EncryptionKey getSourceImageEncryptionKey() {
        return sourceImageEncryptionKey;
    }

    public void setSourceImageEncryptionKey(EncryptionKey sourceImageEncryptionKey) {
        this.sourceImageEncryptionKey = sourceImageEncryptionKey;
    }

    /**
     * Encryption key of the source snapshot.
     */
    public EncryptionKey getSourceSnapshotEncryptionKey() {
        return sourceSnapshotEncryptionKey;
    }

    public void setSourceSnapshotEncryptionKey(EncryptionKey sourceSnapshotEncryptionKey) {
        this.sourceSnapshotEncryptionKey = sourceSnapshotEncryptionKey;
    }

    @Override
    public String primaryKey() {
        List<String> key = new ArrayList<>();

        if (getDiskName() != null) {
            key.add(String.format("disk-name=%s", getDiskName()));
        }

        if (getDescription() != null) {
            key.add(String.format("description=%s", getDescription()));
        }

        if (getSourceImage() != null) {
            key.add(String.format("source-image=%s", getSourceImage()));
        }

        return key.stream().collect(Collectors.joining(", "));
    }

    @Override
    public void copyFrom(AttachedDiskInitializeParams model) {
        setSourceImage(model.getSourceImage());
        setDescription(model.getDescription());
        setDiskName(model.getDiskName());
        setDiskSizeGb(model.getDiskSizeGb());
        setDiskType(model.getDiskType());

        getLabels().clear();
        if (model.getLabels() != null) {
            setLabels(model.getLabels());
        }

        setSourceImageEncryptionKey(null);
        if (model.getSourceImageEncryptionKey() != null) {
            EncryptionKey imageEncryptionKey = newSubresource(EncryptionKey.class);
            imageEncryptionKey.copyFrom(model.getSourceImageEncryptionKey());
            setSourceImageEncryptionKey(imageEncryptionKey);
        }

        setSourceSnapshotEncryptionKey(null);
        if (model.getSourceSnapshotEncryptionKey() != null) {
            EncryptionKey snapshotEncryptionKey = newSubresource(EncryptionKey.class);
            snapshotEncryptionKey.copyFrom(model.getSourceImageEncryptionKey());
            setSourceSnapshotEncryptionKey(snapshotEncryptionKey);
        }
    }

    public AttachedDiskInitializeParams copyTo() {
        AttachedDiskInitializeParams initializeParams = new AttachedDiskInitializeParams();
        initializeParams.setSourceImage(getSourceImage());
        initializeParams.setDescription(getDescription());
        initializeParams.setDiskName(getDiskName());
        initializeParams.setDiskSizeGb(getDiskSizeGb());
        initializeParams.setDiskType(getDiskType());
        initializeParams.setSourceImageEncryptionKey(
            getSourceImageEncryptionKey() != null ? getSourceImageEncryptionKey().toCustomerEncryptionKey() : null);
        initializeParams.setSourceSnapshotEncryptionKey(
            getSourceSnapshotEncryptionKey() != null
                ? getSourceSnapshotEncryptionKey().toCustomerEncryptionKey()
                : null);

        return initializeParams;
    }
}
