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

import com.google.api.services.compute.model.GuestOsFeature;
import gyro.core.resource.Diffable;
import gyro.core.resource.Updatable;
import gyro.core.validation.ValidStrings;
import gyro.google.Copyable;

public class InstanceGuestOsFeature extends Diffable implements Copyable<GuestOsFeature> {

    private String type;

    /**
     * Enables one or more features for VM instances that use the image for their boot disks. See `Enabling guest operating system features <https://cloud.google.com/compute/docs/images/create-delete-deprecate-private-images?authuser=1#guest-os-features/>`_.
     */
    @Updatable
    @ValidStrings({
        "FEATURE_TYPE_UNSPECIFIED",
        "MULTI_IP_SUBNET",
        "SECURE_BOOT",
        "UEFI_COMPATIBLE",
        "VIRTIO_SCSI_MULTIQUEUE",
        "WINDOWS" })
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String primaryKey() {
        if (getType() != null) {
            return String.format("type = %s", getType());
        }

        return "";
    }

    @Override
    public void copyFrom(GuestOsFeature model) {
        setType(model.getType());
    }

    public GuestOsFeature copyTo() {
        return new GuestOsFeature().setType(getType());
    }
}
