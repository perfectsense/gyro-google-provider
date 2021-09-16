/*
 * Copyright 2021, Brightspot.
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

package gyro.google.gke;

import com.google.container.v1.AcceleratorConfig;
import gyro.core.resource.Diffable;
import gyro.core.validation.Required;
import gyro.core.validation.ValidStrings;
import gyro.google.Copyable;

public class GkeAcceleratorConfig extends Diffable implements Copyable<AcceleratorConfig> {

    private Long acceleratorCount;
    private String acceleratorType;

    /**
     * The number of the accelerator cards exposed to an instance.
     */
    public Long getAcceleratorCount() {
        return acceleratorCount;
    }

    public void setAcceleratorCount(Long acceleratorCount) {
        this.acceleratorCount = acceleratorCount;
    }

    /**
     * The accelerator type resource name. `List of supported accelerators <https://cloud.google.com/compute/docs/gpus>`_.
     */
    @Required
    @ValidStrings({
        "nvidia-tesla-k80", "nvidia-tesla-p100", "nvidia-tesla-p4", "nvidia-tesla-v100",
        "nvidia-tesla-t4", "nvidia-tesla-a100" })
    public String getAcceleratorType() {
        return acceleratorType;
    }

    public void setAcceleratorType(String acceleratorType) {
        this.acceleratorType = acceleratorType;
    }

    @Override
    public String primaryKey() {
        return getAcceleratorType();
    }

    @Override
    public void copyFrom(AcceleratorConfig model) {
        setAcceleratorCount(model.getAcceleratorCount());
        setAcceleratorType(model.getAcceleratorType());
    }

    AcceleratorConfig toAcceleratorConfig() {
        return AcceleratorConfig.newBuilder().setAcceleratorCount(getAcceleratorCount())
            .setAcceleratorType(getAcceleratorType()).build();
    }
}
