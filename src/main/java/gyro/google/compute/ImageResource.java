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
import com.google.api.services.compute.Compute;
import com.google.api.services.compute.model.CustomerEncryptionKey;
import com.google.api.services.compute.model.GlobalSetLabelsRequest;
import com.google.api.services.compute.model.Image;
import com.google.api.services.compute.model.Operation;
import com.google.cloud.compute.v1.ProjectGlobalImageFamilyName;
import com.google.cloud.compute.v1.ProjectGlobalImageName;
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
     * The name of the image. Must be 1-63 characters long, and the first character must be a lowercase letter. All other characters must be a lowercase letter, digit, or ``-``, except the last character, which cannot be a ``-``. (Required)
     */
    @Required
    @Regex("(?:[a-z](?:[-a-z0-9]{0,61}[a-z0-9])?)")
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
     * The name of the image family to which this image belongs. Must be 1-63 characters long, and the first character must be a lowercase letter. All other characters must be a lowercase letter, digit, or ``-``, except the last character, which cannot be a ``-``.
     */
    @Regex("(?:[a-z](?:[-a-z0-9]{0,61}[a-z0-9])?)")
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
     * The parameters of the raw disk image. Conflicts with ``source-snapshot``, ``source-disk``, and ``source-image``.
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
     * The source disk used to create this image. Regional disks are not yet supported when creating images. Images can not be created from disks that are currently in use by an instance. Conflicts with ``raw-disk``, ``source-snapshot``, and ``source-image``.
     */
    @ConflictsWith({ "raw-disk", "source-snapshot", "source-image" })
    public DiskResource getSourceDisk() {
        return sourceDisk;
    }

    public void setSourceDisk(DiskResource sourceDisk) {
        this.sourceDisk = sourceDisk;
    }

    /**
     * The encryption key of the source disk. This is required if the source disk is protected by a customer-supplied encryption key. Conflicts with ``source-snapshot-encryption-key`` and ``source-image-encryption-key``.
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
     * The source image used to create this image. Conflicts with ``raw-disk``, ``source-disk``, and ``source-snapshot``.
     */
    @ConflictsWith({ "raw-disk", "source-disk", "source-snapshot" })
    public ImageResource getSourceImage() {
        return sourceImage;
    }

    public void setSourceImage(ImageResource sourceImage) {
        this.sourceImage = sourceImage;
    }

    /**
     * The customer-supplied encryption key of the source image. Required if the source image is protected by a customer-supplied encryption key. Conflicts with ``source-snapshot-encryption-key`` and ``source-disk-encryption-key``.
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
     * The source snapshot used to create this image. Conflicts with ``raw-disk``, ``source-disk``, and ``source-image``.
     */
    @ConflictsWith({ "raw-disk", "source-disk", "source-image" })
    public SnapshotResource getSourceSnapshot() {
        return sourceSnapshot;
    }

    public void setSourceSnapshot(SnapshotResource sourceSnapshot) {
        this.sourceSnapshot = sourceSnapshot;
    }

    /**
     * The customer-supplied encryption key of the source snapshot. Required if the source snapshot is protected by a customer-supplied encryption key. Conflicts with ``source-disk-encryption-key`` and ``source-image-encryption-key``.
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
        this.selfLink = selfLink != null ? toImageUrl(getProjectId(), selfLink) : null;
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
    public void copyFrom(Image image) {
        setName(image.getName());
        setDescription(image.getDescription());
        setFamily(image.getFamily());
        setLabels(image.getLabels());
        setArchiveSizeBytes(image.getArchiveSizeBytes());
        setDiskSizeGb(image.getDiskSizeGb());
        setLabelFingerprint(image.getLabelFingerprint());
        setSelfLink(image.getSelfLink());
        setSourceDiskId(image.getSourceDiskId());
        setSourceImageId(image.getSourceImageId());
        setSourceSnapshotId(image.getSourceSnapshotId());
        setStatus(image.getStatus());

        // Image doesn't currently have an API for storageLocations so manually get it
        setStorageLocations(null);
        if (image.get("storageLocations") instanceof List) {
            setStorageLocations((List<String>) image.get("storageLocations"));
        }

        setSourceDisk(null);
        if (DiskResource.parseDisk(getProjectId(), image.getSourceDisk()) != null) {
            setSourceDisk(findById(DiskResource.class, image.getSourceDisk()));
        }

        setSourceImage(null);
        if (parseImage(getProjectId(), image.getSourceImage()) != null
            || parseFamilyImage(getProjectId(), image.getSourceImage()) != null) {
            setSourceImage(findById(ImageResource.class, image.getSourceImage()));
        }

        setSourceSnapshot(null);
        if (SnapshotResource.parseSnapshot(getProjectId(), image.getSourceSnapshot()) != null) {
            setSourceSnapshot(findById(SnapshotResource.class, image.getSourceSnapshot()));
        }
    }

    @Override
    protected boolean doRefresh() throws Exception {
        Compute client = createComputeClient();

        Image image = client.images().get(getProjectId(), getName()).execute();
        copyFrom(image);

        return true;
    }

    @Override
    protected void doCreate(GyroUI ui, State state) throws Exception {
        Compute client = createComputeClient();

        Image image = new Image();
        image.setName(getName());
        image.setDescription(getDescription());
        image.setFamily(getFamily());
        image.setLabels(getLabels());
        image.setRawDisk(getRawDisk() != null ? getRawDisk().toRawDisk() : Data.nullOf(Image.RawDisk.class));
        image.setImageEncryptionKey(getImageEncryptionKey() != null
            ? getImageEncryptionKey().toCustomerEncryptionKey()
            : Data.nullOf(CustomerEncryptionKey.class));
        image.setSourceDisk(getSourceDisk() != null ? getSourceDisk().getSelfLink() : null);
        image.setSourceDiskEncryptionKey(getSourceDiskEncryptionKey() != null
            ? getSourceDiskEncryptionKey().toCustomerEncryptionKey()
            : Data.nullOf(CustomerEncryptionKey.class));
        image.setSourceImage(getSourceImage() != null ? getSourceImage().getSelfLink() : null);
        image.setSourceImageEncryptionKey(getSourceImageEncryptionKey() != null
            ? getSourceImageEncryptionKey().toCustomerEncryptionKey()
            : Data.nullOf(CustomerEncryptionKey.class));
        image.setSourceSnapshot(getSourceSnapshot() != null ? getSourceSnapshot().getSelfLink() : null);
        image.setSourceSnapshotEncryptionKey(getSourceSnapshotEncryptionKey() != null
            ? getSourceSnapshotEncryptionKey().toCustomerEncryptionKey()
            : Data.nullOf(CustomerEncryptionKey.class));

        // Image doesn't currently have an API for storageLocations so manually set it
        image.set("storageLocations", getStorageLocations());

        Operation operation = client.images().insert(getProjectId(), image).execute();
        // Images are slow to complete so wait max of 3 minutes for completion
        waitForCompletion(client, operation, 3, TimeUnit.MINUTES);

        refresh();
    }

    @Override
    public void doUpdate(
        GyroUI ui, State state, Resource current, Set<String> changedFieldNames) throws Exception {
        Compute client = createComputeClient();

        GlobalSetLabelsRequest labelsRequest = new GlobalSetLabelsRequest();
        labelsRequest.setLabels(getLabels());
        labelsRequest.setLabelFingerprint(getLabelFingerprint());
        Operation operation = client.images().setLabels(getProjectId(), getName(), labelsRequest).execute();
        waitForCompletion(client, operation);

        refresh();
    }

    @Override
    public void doDelete(GyroUI ui, State state) throws Exception {
        Compute client = createComputeClient();

        Operation operation = client.images().delete(getProjectId(), getName()).execute();
        waitForCompletion(client, operation);
    }

    @Override
    public List<ValidationError> validate() {
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

    static ProjectGlobalImageFamilyName parseFamilyImage(String projectId, String selfLink) {
        if (selfLink == null) {
            return null;
        }

        String parseFamilyImage = formatResource(projectId, selfLink);
        if (ProjectGlobalImageFamilyName.isParsableFrom(parseFamilyImage)) {
            return ProjectGlobalImageFamilyName.parse(parseFamilyImage);
        }
        return null;
    }

    static ProjectGlobalImageName parseImage(String projectId, String selfLink) {
        if (selfLink == null) {
            return null;
        }

        String parseImage = formatResource(projectId, selfLink);
        if (ProjectGlobalImageName.isParsableFrom(parseImage)) {
            return ProjectGlobalImageName.parse(parseImage);
        }
        return null;
    }

    static String toImageUrl(String projectId, String image) {
        String parseImage = formatResource(projectId, image);
        if (ProjectGlobalImageName.isParsableFrom(parseImage)) {
            return ProjectGlobalImageName.parse(parseImage).toString();
        }
        if (ProjectGlobalImageFamilyName.isParsableFrom(parseImage)) {
            return ProjectGlobalImageFamilyName.parse(parseImage).toString();
        }
        return image;
    }
}

