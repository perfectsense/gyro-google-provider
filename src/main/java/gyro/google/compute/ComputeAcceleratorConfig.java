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

import com.google.cloud.compute.v1.AcceleratorConfig;
import gyro.core.resource.Diffable;
import gyro.core.validation.Required;
import gyro.google.Copyable;

public class ComputeAcceleratorConfig extends Diffable implements Copyable<AcceleratorConfig> {

    private Integer acceleratorCount;

    private String acceleratorType;

    /**
     * The number of the guest accelerator cards exposed to this instance.
     */
    @Required
    public Integer getAcceleratorCount() {
        return acceleratorCount;
    }

    public void setAcceleratorCount(Integer acceleratorCount) {
        this.acceleratorCount = acceleratorCount;
    }

    /**
     * Full or partial URL of the accelerator type resource to attach to this instance.
     * For example: ``projects/my-project/zones/us-central1-c/acceleratorTypes/nvidia-tesla-p100``
     * If you are creating an instance template, specify only the accelerator name.
     */
    @Required
    public String getAcceleratorType() {
        return acceleratorType;
    }

    public void setAcceleratorType(String acceleratorType) {
        this.acceleratorType = acceleratorType;
    }

    @Override
    public void copyFrom(AcceleratorConfig model) {
        if (model.hasAcceleratorCount()) {
            setAcceleratorCount(model.getAcceleratorCount());
        }

        if (model.hasAcceleratorType()) {
            setAcceleratorType(model.getAcceleratorType());
        }
    }

    public AcceleratorConfig toAcceleratorConfig() {
        return AcceleratorConfig.newBuilder()
            .setAcceleratorCount(getAcceleratorCount())
            .setAcceleratorType(getAcceleratorType())
            .build();
    }

    @Override
    public String primaryKey() {
        return getAcceleratorType();
    }
}
