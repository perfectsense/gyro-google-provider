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
import java.util.List;
import java.util.stream.Collectors;

import com.google.api.services.compute.model.AttachedDisk;
import gyro.core.resource.Diffable;
import gyro.core.validation.ConflictsWith;
import gyro.core.validation.ValidStrings;
import gyro.google.Copyable;

public class InstanceAttachedDisk extends Diffable implements Copyable<AttachedDisk> {

    private Boolean autoDelete;
    private Boolean boot;
    private String deviceName;
    private InstanceCustomerEncryptionKey diskEncryptionKey;
    private List<InstanceGuestOsFeature> guestOsFeatures;
    private InstanceAttachedDiskInitializeParams initializeParams;
    private String diskInterface; // model name is reserved 'interface'
    private String mode;
    private DiskResource source;
    private String type;

    /**
     * Whether the disk will be auto-deleted when the instance is deleted, but not when the disk is detached from the instance.
     */
    public Boolean getAutoDelete() {
        return autoDelete;
    }

    public void setAutoDelete(Boolean autoDelete) {
        this.autoDelete = autoDelete;
    }

    /**
     * This is a boot disk the virtual machine will use the first partition of the disk for its root filesystem.
     */
    public Boolean getBoot() {
        return boot;
    }

    public void setBoot(Boolean boot) {
        this.boot = boot;
    }

    /**
     * Only for persistent disks, the unique device name reflected into the /dev/disk/by-id/google-* tree of a Linux operating system running within the instance. The name can then be used to reference the device for mounting, resizing, etc... from within the instance. Unspecified the server chooses a default name in the form of ``persistent-disk-x``, where ``x`` is a number assigned by Google Compute Engine.
     */
    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    /**
     * When creating a new disk this field encrypts the new disk using the supplied encryption key. If attaching an existing disk already encrypted, this decrypts the disk using the supplied encryption key.||If you encrypt a disk using a customer-supplied key, you must provide the same key again when you attempt to use this resource at a later time.||If you do not provide an encryption key, then the disk will be encrypted using an automatically generated key and you do not need to provide a key to use the disk later. Instance templates do not store customer-supplied encryption keys, so you cannot use your own keys to encrypt disks in a managed instance group.
     */
    public InstanceCustomerEncryptionKey getDiskEncryptionKey() {
        return diskEncryptionKey;
    }

    public void setDiskEncryptionKey(InstanceCustomerEncryptionKey diskEncryptionKey) {
        this.diskEncryptionKey = diskEncryptionKey;
    }

    /**
     * List of features to enable on the guest operating system. Applicable only for bootable images. See `enabling guest operating system features<https://cloud.google.com/compute/docs/images/create-delete-deprecate-private-images#guest-os-features/>`_.
     */
    public List<InstanceGuestOsFeature> getGuestOsFeatures() {
        if (guestOsFeatures == null) {
            guestOsFeatures = new ArrayList<>();
        }
        return guestOsFeatures;
    }

    public void setGuestOsFeatures(List<InstanceGuestOsFeature> guestOsFeatures) {
        this.guestOsFeatures = guestOsFeatures;
    }

    /**
     * Parameters for a new disk that will be created alongside the new instance. Use initialization parameters to create boot disks or local SSDs attached to the new instance. This property is mutually exclusive with the source property; you can only define one or the other, but not both.
     */
    @ConflictsWith("source")
    public InstanceAttachedDiskInitializeParams getInitializeParams() {
        return initializeParams;
    }

    public void setInitializeParams(InstanceAttachedDiskInitializeParams initializeParams) {
        this.initializeParams = initializeParams;
    }

    /**
     * Disk interface to use for attaching this disk. Valid values are ``SCSI`` or ``NVME``. Default is ``SCSI``. Persistent disks must always use ``SCSI`` and the request will fail if you attempt to attach a persistent disk in any other format than ``SCSI``.
     */
    @ValidStrings({ "SCSI", "NVME" })
    public String getDiskInterface() {
        return diskInterface;
    }

    public void setDiskInterface(String diskInterface) {
        this.diskInterface = diskInterface;
    }

    /**
     * The mode in which to attach this disk. Valid values are ``READ_WRITE`` or ``READ_ONLY``. Default is ``READ_WRITE``.
     */
    @ValidStrings({ "READ_WRITE", "READ_ONLY" })
    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    /**
     * The Persistent Disk resource.
     */
    @ConflictsWith("initializeParams")
    public DiskResource getSource() {
        return source;
    }

    public void setSource(DiskResource source) {
        this.source = source;
    }

    /**
     * Type of the disk, valid values are ``SCRATCH`` or ``PERSISTENT``. Default is ``PERSISTENT``.
     */
    @ValidStrings({ "SCRATCH", "PERSISTENT" })
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String primaryKey() {
        if (getInitializeParams() != null) {
            List<String> key = new ArrayList<>();

            if (getDeviceName() != null) {
                key.add(String.format("device-name::%s", getDeviceName()));
            }

            if (getBoot() != null) {
                key.add(String.format("boot::%s", getBoot()));
            }

            if (getAutoDelete() != null) {
                key.add(String.format("auto-delete::%s", getAutoDelete()));
            }

            if (getSource() != null) {
                key.add(String.format("source::%s", getSource()));
            }

            return key.stream().collect(Collectors.joining(","));
        }

        return getSource() != null ? getSource().getSelfLink() : "";
    }

    @Override
    public void copyFrom(AttachedDisk model) {
        setAutoDelete(model.getAutoDelete());
        setBoot(model.getBoot());
        setDeviceName(model.getDeviceName());
        setDiskInterface(model.getInterface());
        setMode(model.getMode());
        setSource(findById(DiskResource.class, model.getSource()));
        setType(model.getType());

        setDiskEncryptionKey(null);
        if (model.getDiskEncryptionKey() != null) {
            InstanceCustomerEncryptionKey newDiskEncryptionKey = newSubresource(InstanceCustomerEncryptionKey.class);
            newDiskEncryptionKey.copyFrom(model.getDiskEncryptionKey());
            setDiskEncryptionKey(newDiskEncryptionKey);
        }

        getGuestOsFeatures().clear();
        if (model.getGuestOsFeatures() != null) {
            setGuestOsFeatures(model.getGuestOsFeatures().stream()
                .map(feature -> {
                    InstanceGuestOsFeature newFeature = newSubresource(InstanceGuestOsFeature.class);
                    newFeature.copyFrom(feature);
                    return newFeature;
                })
                .collect(Collectors.toList())
            );
        }
    }

    public AttachedDisk copyTo() {
        AttachedDisk disk = new AttachedDisk();
        disk.setAutoDelete(getAutoDelete());
        disk.setBoot(getBoot());
        disk.setDeviceName(getDeviceName());
        disk.setInterface(getDiskInterface());
        disk.setMode(getMode());
        disk.setSource(getSource() != null ? getSource().getSelfLink() : null);
        disk.setType(getType());
        disk.setDiskEncryptionKey(getDiskEncryptionKey() != null ? getDiskEncryptionKey().copyTo() : null);
        disk.setGuestOsFeatures(getGuestOsFeatures() != null ? getGuestOsFeatures().stream()
            .map(InstanceGuestOsFeature::copyTo)
            .collect(Collectors.toList()) : null);
        disk.setInitializeParams(getInitializeParams() != null ? getInitializeParams().copyTo() : null);

        return disk;
    }
}
