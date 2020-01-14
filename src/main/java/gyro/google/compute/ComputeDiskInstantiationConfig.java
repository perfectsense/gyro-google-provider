/*
 * Copyright 2020, Perfect Sense, Inc.
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

import com.google.api.services.compute.model.DiskInstantiationConfig;
import gyro.core.resource.Diffable;
import gyro.core.validation.Required;
import gyro.google.Copyable;

public class ComputeDiskInstantiationConfig extends Diffable implements Copyable<DiskInstantiationConfig> {

    private Boolean autoDelete;

    private String customImage;

    private String deviceName;

    private String instantiateFrom;

    /**
     * Specifies whether the disk will be auto-deleted when the instance is deleted, but not when the disk is detached from the instance.
     */
    public Boolean getAutoDelete() {
        return autoDelete;
    }

    public void setAutoDelete(Boolean autoDelete) {
        this.autoDelete = autoDelete;
    }

    /**
     * The custom source image to be used to restore this disk when instantiating this instance template.
     */
    public String getCustomImage() {
        return customImage;
    }

    public void setCustomImage(String customImage) {
        this.customImage = customImage;
    }

    /**
     * Specifies the device name of the disk to which the configurations apply to.
     */
    @Required
    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    /**
     * Specifies whether to include the disk and what image to use.
     * Possible values are:
     *
     * - ``source-image``: to use the same image that was used to create the source instance's corresponding disk. Applicable to the boot disk and additional read-write disks.
     * - ``source-image-family``: to use the same image family that was used to create the source instance's corresponding disk. Applicable to the boot disk and additional read-write disks.
     * - ``custom-image``: to use a user-provided image url for disk creation. Applicable to the boot disk and additional read-write disks.
     * - ``attach-read-only``: to attach a read-only disk. Applicable to read-only disks.
     * - ``do-not-include``: to exclude a disk from the template. Applicable to additional read-write disks, local SSDs, and read-only disks.
     */
    public String getInstantiateFrom() {
        return instantiateFrom;
    }

    public void setInstantiateFrom(String instantiateFrom) {
        this.instantiateFrom = instantiateFrom;
    }

    @Override
    public void copyFrom(DiskInstantiationConfig model) {
        setAutoDelete(model.getAutoDelete());
        setCustomImage(model.getCustomImage());
        setDeviceName(model.getDeviceName());
        setInstantiateFrom(model.getInstantiateFrom());
    }

    public DiskInstantiationConfig toDiskInstantiationConfig() {
        DiskInstantiationConfig diskInstantiationConfig = new DiskInstantiationConfig();
        diskInstantiationConfig.setAutoDelete(getAutoDelete());
        diskInstantiationConfig.setCustomImage(getCustomImage());
        diskInstantiationConfig.setDeviceName(getDeviceName());
        diskInstantiationConfig.setInstantiateFrom(getInstantiateFrom());
        return diskInstantiationConfig;
    }

    @Override
    public String primaryKey() {
        return getDeviceName();
    }
}
