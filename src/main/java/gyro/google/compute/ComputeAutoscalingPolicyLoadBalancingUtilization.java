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

import com.google.api.services.compute.model.AutoscalingPolicyLoadBalancingUtilization;
import gyro.core.resource.Diffable;
import gyro.core.resource.Updatable;
import gyro.core.validation.Range;
import gyro.core.validation.Required;
import gyro.google.Copyable;

public class ComputeAutoscalingPolicyLoadBalancingUtilization extends Diffable
    implements Copyable<AutoscalingPolicyLoadBalancingUtilization> {

    private Double utilizationTarget;

    /**
     * Fraction of backend capacity utilization (set in HTTP(S) load balancing configuration) that autoscaler should maintain.
     * Must be a positive float value. If not defined, the default is ``0.8``.
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

    public AutoscalingPolicyLoadBalancingUtilization copyTo() {
        AutoscalingPolicyLoadBalancingUtilization autoscalingPolicyLoadBalancingUtilization = new AutoscalingPolicyLoadBalancingUtilization();
        autoscalingPolicyLoadBalancingUtilization.setUtilizationTarget(getUtilizationTarget());
        return autoscalingPolicyLoadBalancingUtilization;
    }

    @Override
    public void copyFrom(AutoscalingPolicyLoadBalancingUtilization model) {
        setUtilizationTarget(model.getUtilizationTarget());
    }

    @Override
    public String primaryKey() {
        return "";
    }
}
