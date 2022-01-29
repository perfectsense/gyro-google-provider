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
import java.util.concurrent.TimeUnit;

import com.google.api.client.util.Data;
import com.google.api.gax.rpc.InvalidArgumentException;
import com.google.api.gax.rpc.NotFoundException;
import com.google.cloud.compute.v1.CustomerEncryptionKey;
import com.google.cloud.compute.v1.DeleteImageRequest;
import com.google.cloud.compute.v1.GetImageRequest;
import com.google.cloud.compute.v1.GlobalSetLabelsRequest;
import com.google.cloud.compute.v1.Image;
import com.google.cloud.compute.v1.ImagesClient;
import com.google.cloud.compute.v1.InsertImageRequest;
import com.google.cloud.compute.v1.Operation;
import com.google.cloud.compute.v1.RawDisk;
import com.google.cloud.compute.v1.SetLabelsImageRequest;
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
 * Creates an image, which is used to create boot disks. You must provide either a source image, source snapshot, source disk, or raw disk when creating an image.
 *
 * Example
 * -------
 *
 * .. code-block:: gyro
 *
 *     google::compute-image image-disk-example
 *         name: "image-disk-example"
 *         description: "image-disk-example-desc"
 *         source-disk: $(google::compute-disk disk-example)
 *         family: "image-disk-example-family"
 *         labels: {
 *             label-key: 'label-value'
 *         }
 *
 *         source-disk-encryption-key
 *             raw-key: "SGVsbG8gZnJvbSBHb29nbGUgQ2xvdWQgUGxhdGZvcm0="
 *         end
 *
 *         storage-locations: [
 *             "us-central1"
 *         ]
 *     end
 *
 * .. code-block:: gyro
 *
 *     google::compute-image image-image-example
 *         name: "image-image-example"
 *         description: "image-image-example-desc"
 *         source-image: $(google::compute-image image-disk-example)
 *
 *         storage-locations: [
 *             "us"
 *         ]
 *     end
 */
@Type("compute-image")
public class ImageResource extends ComputeResource implements Copyable<Image> {

    private String name;
    private String description;
    private String family;
    private Map<String, String> labels;
    private ImageRawDisk rawDisk;
    private EncryptionKey imageEncryptionKey;
    private List<String> storageLocations;
    private DiskResource sourceDisk;
    private EncryptionKey sourceDiskEncryptionKey;
    private ImageResource sourceImage;
    private EncryptionKey sourceImageEncryptionKey;
    private SnapshotResource sourceSnapshot;
    private EncryptionKey sourceSnapshotEncryptionKey;

    // Read-only
    private Long archiveSizeBytes;
    private Long diskSizeGb;
    private String labelFingerprint;
    private String selfLink;
    private String sourceDiskId;
    private String sourceImageId;
    private String sourceSnapshotId;
    private String status;

    /**
     * The name of the image.
     */
    @Required
    @Regex(value = "(?:[a-z](?:[-a-z0-9]{0,61}[a-z0-9])?)", message = "a string 1-63 characters long and the first character must be a lowercase letter, and all following characters must be a dash, lowercase letter, or digit, except the last character, which cannot be a dash")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * The description of the image.
     */
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * The name of the image family to which this image belongs.
     */
    @Regex(value = "(?:[a-z](?:[-a-z0-9]{0,61}[a-z0-9])?)", message = "a string 1-63 characters long and the first character must be a lowercase letter, and all following characters must be a dash, lowercase letter, or digit, except the last character, which cannot be a dash")
    public String getFamily() {
        return family;
    }

    public void setFamily(String family) {
        this.family = family;
    }

    /**
     * Optional labels (key-value pairs) that can be applied to the image. The only characters allowed are lowercase characters, international characters, numbers, ``-``, and ``_``. Key and value must be under 64 characters.
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
     * The parameters of the raw disk image.
     *
     * @subresource gyro.google.compute.ImageRawDisk
     */
    @ConflictsWith({ "source-snapshot", "source-disk", "source-image" })
    public ImageRawDisk getRawDisk() {
        return rawDisk;
    }

    public void setRawDisk(ImageRawDisk rawDisk) {
        this.rawDisk = rawDisk;
    }

    /**
     * The encryption key used to encrypt the image. If you do not provide an encryption key when creating the image, the image will be encrypted using an automatically generated key.
     *
     * @subresource gyro.google.compute.EncryptionKey
     */
    public EncryptionKey getImageEncryptionKey() {
        return imageEncryptionKey;
    }

    public void setImageEncryptionKey(EncryptionKey imageEncryptionKey) {
        this.imageEncryptionKey = imageEncryptionKey;
    }

    /**
     * Storage location of the image. Can be regional (eg. ``us-central1``) or multi-regional (eg. ``us``). Defaults to storage location of the source.
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
     * The source disk used to create this image. Regional disks are not yet supported when creating images. Images can not be created from disks that are currently in use by an instance.
     */
    @ConflictsWith({ "raw-disk", "source-snapshot", "source-image" })
    public DiskResource getSourceDisk() {
        return sourceDisk;
    }

    public void setSourceDisk(DiskResource sourceDisk) {
        this.sourceDisk = sourceDisk;
    }

    /**
     * The encryption key of the source disk. This is required if the source disk is protected by a customer-supplied encryption key.
     *
     * @subresource gyro.google.compute.EncryptionKey
     */
    @ConflictsWith({ "source-snapshot-encryption-key", "source-image-encryption-key" })
    public EncryptionKey getSourceDiskEncryptionKey() {
        return sourceDiskEncryptionKey;
    }

    public void setSourceDiskEncryptionKey(EncryptionKey sourceDiskEncryptionKey) {
        this.sourceDiskEncryptionKey = sourceDiskEncryptionKey;
    }

    /**
     * The source image used to create this image.
     */
    @ConflictsWith({ "raw-disk", "source-disk", "source-snapshot" })
    public ImageResource getSourceImage() {
        return sourceImage;
    }

    public void setSourceImage(ImageResource sourceImage) {
        this.sourceImage = sourceImage;
    }

    /**
     * The customer-supplied encryption key of the source image. Required if the source image is protected by a customer-supplied encryption key.
     *
     * @subresource gyro.google.compute.EncryptionKey
     */
    @ConflictsWith({ "source-snapshot-encryption-key", "source-disk-encryption-key" })
    public EncryptionKey getSourceImageEncryptionKey() {
        return sourceImageEncryptionKey;
    }

    public void setSourceImageEncryptionKey(EncryptionKey sourceImageEncryptionKey) {
        this.sourceImageEncryptionKey = sourceImageEncryptionKey;
    }

    /**
     * The source snapshot used to create this image.
     */
    @ConflictsWith({ "raw-disk", "source-disk", "source-image" })
    public SnapshotResource getSourceSnapshot() {
        return sourceSnapshot;
    }

    public void setSourceSnapshot(SnapshotResource sourceSnapshot) {
        this.sourceSnapshot = sourceSnapshot;
    }

    /**
     * The customer-supplied encryption key of the source snapshot. Required if the source snapshot is protected by a customer-supplied encryption key.
     *
     * @subresource gyro.google.compute.EncryptionKey
     */
    @ConflictsWith({ "source-disk-encryption-key", "source-image-encryption-key" })
    public EncryptionKey getSourceSnapshotEncryptionKey() {
        return sourceSnapshotEncryptionKey;
    }

    public void setSourceSnapshotEncryptionKey(EncryptionKey sourceSnapshotEncryptionKey) {
        this.sourceSnapshotEncryptionKey = sourceSnapshotEncryptionKey;
    }

    /**
     * Size of the image archive stored in bytes.
     */
    @Output
    public Long getArchiveSizeBytes() {
        return archiveSizeBytes;
    }

    public void setArchiveSizeBytes(Long archiveSizeBytes) {
        this.archiveSizeBytes = archiveSizeBytes;
    }

    /**
     * The size of the image when restored onto a persistent disk, specified in GB.
     */
    @Output
    public Long getDiskSizeGb() {
        return diskSizeGb;
    }

    public void setDiskSizeGb(Long diskSizeGb) {
        this.diskSizeGb = diskSizeGb;
    }

    /**
     * The fingerprint for the labels being applied to the image, which is updated any time the labels change.
     */
    @Output
    public String getLabelFingerprint() {
        return labelFingerprint;
    }

    public void setLabelFingerprint(String labelFingerprint) {
        this.labelFingerprint = labelFingerprint;
    }

    /**
     * The fully-qualified URL linking back to the image.
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
     * The ID value of the disk used to create this image.
     */
    @Output
    public String getSourceDiskId() {
        return sourceDiskId;
    }

    public void setSourceDiskId(String sourceDiskId) {
        this.sourceDiskId = sourceDiskId;
    }

    /**
     * The ID value of the image used to create this image.
     */
    @Output
    public String getSourceImageId() {
        return sourceImageId;
    }

    public void setSourceImageId(String sourceImageId) {
        this.sourceImageId = sourceImageId;
    }

    /**
     * The ID value of the snapshot used to create this image.
     */
    @Output
    public String getSourceSnapshotId() {
        return sourceSnapshotId;
    }

    public void setSourceSnapshotId(String sourceSnapshotId) {
        this.sourceSnapshotId = sourceSnapshotId;
    }

    /**
     * The status of image creation. Values can be: ``PENDING``, ``FAILED``, or ``READY``.
     */
    @Output
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void copyFrom(Image model) {
        setName(model.getName());
        setLabels(model.getLabels());

        if (model.hasDescription()) {
            setDescription(model.getDescription());
        }

        if (model.hasSelfLink()) {
            setSelfLink(model.getSelfLink());
        }

        if (model.hasFamily()) {
            setFamily(model.getFamily());
        }

        if (model.hasArchiveSizeBytes()) {
            setArchiveSizeBytes(model.getArchiveSizeBytes());
        }

        if (model.hasDiskSizeGb()) {
            setDiskSizeGb(model.getDiskSizeGb());
        }

        if (model.hasLabelFingerprint()) {
            setLabelFingerprint(model.getLabelFingerprint());
        }

        if (model.hasSourceDiskId()) {
            setSourceDiskId(model.getSourceDiskId());
        }

        if (model.hasSourceImageId()) {
            setSourceImageId(model.getSourceImageId());
        }

        if (model.hasSourceSnapshotId()) {
            setSourceSnapshotId(model.getSourceSnapshotId());
        }

        if (model.hasStatus()) {
            setStatus(model.getStatus());
        }

        if (model.hasSourceDisk()) {
            setSourceDisk(findById(DiskResource.class, model.getSourceDisk()));
        }

        if (model.hasSourceImage()) {
            setSourceImage(findById(ImageResource.class, model.getSourceImage()));
        }

        if (model.hasSourceSnapshot()) {
            setSourceSnapshot(findById(SnapshotResource.class, model.getSourceSnapshot()));
        }

        // Image doesn't currently have an API for storageLocations so manually get it
        setStorageLocations(null);
        if (model.getStorageLocationsList() != null && !(model.getStorageLocationsList().isEmpty())) {
            setStorageLocations(model.getStorageLocationsList());
        }
    }

    @Override
    protected boolean doRefresh() throws Exception {
        try (ImagesClient client = createClient(ImagesClient.class)) {
            Image image = getImage(client);

            if (image == null) {
                return false;
            }

            copyFrom(image);

            return true;
        }
    }

    @Override
    protected void doCreate(GyroUI ui, State state) throws Exception {
        try (ImagesClient client = createClient(ImagesClient.class)) {

            Image.Builder builder = Image.newBuilder();
            builder.setName(getName());
            builder.putAllLabels(getLabels());

            if (getDescription() != null) {
                builder.setDescription(getDescription());
            }

            if (getFamily() != null) {
                builder.setFamily(getFamily());
            }

            if (getRawDisk() != null) {
                builder.setRawDisk(getRawDisk() != null ? getRawDisk().toRawDisk() : Data.nullOf(RawDisk.class));
            }

            if (getImageEncryptionKey() != null) {
                builder.setImageEncryptionKey(getImageEncryptionKey() != null
                    ? getImageEncryptionKey().toCustomerEncryptionKey()
                    : Data.nullOf(CustomerEncryptionKey.class));
            }

            if (getSourceDisk() != null) {
                builder.setSourceDisk(getSourceDisk() != null ? getSourceDisk().getSelfLink() : null);
            }

            if (getSourceDiskEncryptionKey() != null) {
                builder.setSourceDiskEncryptionKey(getSourceDiskEncryptionKey() != null
                    ? getSourceDiskEncryptionKey().toCustomerEncryptionKey()
                    : Data.nullOf(CustomerEncryptionKey.class));
            }

            if (getSourceImage() != null) {
                builder.setSourceImage(getSourceImage() != null ? getSourceImage().getSelfLink() : null);
            }

            if (getSourceImageEncryptionKey() != null) {
                builder.setSourceImageEncryptionKey(getSourceImageEncryptionKey() != null
                    ? getSourceImageEncryptionKey().toCustomerEncryptionKey()
                    : Data.nullOf(CustomerEncryptionKey.class));
            }

            if (getSourceSnapshot() != null) {
                builder.setSourceSnapshot(getSourceSnapshot() != null ? getSourceSnapshot().getSelfLink() : null);
            }

            if (getSourceSnapshotEncryptionKey() != null) {
                builder.setSourceSnapshotEncryptionKey(getSourceSnapshotEncryptionKey() != null
                    ? getSourceSnapshotEncryptionKey().toCustomerEncryptionKey()
                    : Data.nullOf(CustomerEncryptionKey.class));
            }

            // Image doesn't currently have an API for storageLocations so manually set it
            builder.addAllStorageLocations(getStorageLocations());

            Operation operation = client.insertCallable().call(InsertImageRequest.newBuilder()
                .setProject(getProjectId())
                .setImageResource(builder)
                .build());

            // Images are slow to complete so wait max of 3 minutes for completion
            waitForCompletion(operation, 3, TimeUnit.MINUTES);
        }

        refresh();
    }

    @Override
    public void doUpdate(GyroUI ui, State state, Resource current, Set<String> changedFieldNames) throws Exception {
        try (ImagesClient client = createClient(ImagesClient.class)) {
            GlobalSetLabelsRequest.Builder builder = GlobalSetLabelsRequest.newBuilder();
            builder.putAllLabels(getLabels());
            builder.setLabelFingerprint(getLabelFingerprint());
            Operation operation = client.setLabelsCallable().call(SetLabelsImageRequest.newBuilder()
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
        try (ImagesClient client = createClient(ImagesClient.class)) {
            Operation operation = client.deleteCallable().call(DeleteImageRequest.newBuilder()
                .setProject(getProjectId())
                .setImage(getName())
                .build());

            waitForCompletion(operation);
        }
    }

    @Override
    public List<ValidationError> validate(Set<String> configuredFields) {
        List<ValidationError> errors = new ArrayList<>();

        if (getSourceDisk() == null && getRawDisk() == null && getSourceImage() == null
            && getSourceSnapshot() == null) {
            errors.add(new ValidationError(
                this,
                null,
                "Either a 'source-disk', 'raw-disk', 'source-image', or 'source-snapshot' is required when creating an image."));
        }

        if (getStorageLocations().size() > 1) {
            errors.add(new ValidationError(
                this,
                "storage-locations",
                "Attaching more than one storage location is not supported."));
        }

        return errors;
    }

    private Image getImage(ImagesClient client) {
        Image image = null;

        try {
            image = client.get(GetImageRequest.newBuilder().setProject(getProjectId()).setImage(getName()).build());

        } catch (NotFoundException | InvalidArgumentException ex) {
            // ignore
        }

        return image;
    }
}

