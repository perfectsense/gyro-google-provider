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

import com.google.cloud.compute.v1.AutoscalingPolicyCpuUtilization;
import gyro.core.resource.Diffable;
import gyro.core.resource.Updatable;
import gyro.core.validation.Range;
import gyro.core.validation.Required;
import gyro.google.Copyable;

public class ComputeAutoscalingPolicyCpuUtilization extends Diffable
    implements Copyable<AutoscalingPolicyCpuUtilization> {

    private Double utilizationTarget;

    /**
     * The target CPU utilization that the autoscaler should maintain.
     * Must be a float value in the range between 0 and 1. If not specified, the default is ``0.6``. If the CPU level is below the target utilization, the autoscaler scales down the number of instances until it reaches the minimum number of instances you specified or until the average CPU of your instances reaches the target utilization. If the average CPU is above the target utilization, the autoscaler scales up until it reaches the maximum number of instances you specified or until the average utilization reaches the target utilization.
     */
    @Range(min = 0, max = 1)
    @Required
    @Updatable
    public Double getUtilizationTarget() {
        return utilizationTarget;
    }

    public void setUtilizationTarget(Double utilizationTarget) {
        this.utilizationTarget = utilizationTarget;
    }

    public AutoscalingPolicyCpuUtilization copyTo() {
        AutoscalingPolicyCpuUtilization autoscalingPolicyCpuUtilization = new AutoscalingPolicyCpuUtilization();
        autoscalingPolicyCpuUtilization.setUtilizationTarget(getUtilizationTarget());
        return autoscalingPolicyCpuUtilization;
    }

    @Override
    public void copyFrom(AutoscalingPolicyCpuUtilization model) {
        setUtilizationTarget(model.getUtilizationTarget());
    }

    @Override
    public String primaryKey() {
        return "";
    }
}
