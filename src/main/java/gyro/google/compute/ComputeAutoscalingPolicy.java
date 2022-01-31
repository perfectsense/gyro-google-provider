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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.cloud.compute.v1.AutoscalingPolicy;
import com.google.cloud.compute.v1.AutoscalingPolicyCustomMetricUtilization;
import gyro.core.resource.Diffable;
import gyro.core.resource.Updatable;
import gyro.core.validation.Min;
import gyro.core.validation.Required;
import gyro.core.validation.ValidStrings;
import gyro.core.validation.ValidationError;
import gyro.google.Copyable;

public class ComputeAutoscalingPolicy extends Diffable implements Copyable<AutoscalingPolicy> {

    private Integer coolDownPeriodSec;

    private ComputeAutoscalingPolicyCpuUtilization cpuUtilization;

    private List<ComputeAutoscalingPolicyCustomMetricUtilization> customMetricUtilization;

    private ComputeAutoscalingPolicyLoadBalancingUtilization loadBalancingUtilization;

    private Integer maxNumReplicas;

    private Integer minNumReplicas;

    private String mode;

    /**
     * The number of seconds that the autoscaler should wait before it starts collecting information from a new instance.
     * This prevents the autoscaler from collecting information when the instance is initializing, during which the collected usage would not be reliable. The default time autoscaler waits is ``60`` seconds. Virtual machine initialization times might vary because of numerous factors. We recommend that you test how long an instance may take to initialize. To do this, create an instance and time the startup process.
     */
    @Updatable
    public Integer getCoolDownPeriodSec() {
        return coolDownPeriodSec;
    }

    public void setCoolDownPeriodSec(Integer coolDownPeriodSec) {
        this.coolDownPeriodSec = coolDownPeriodSec;
    }

    /**
     * Defines the CPU utilization policy that allows the autoscaler to scale based on the average CPU utilization of a managed instance group.
     *
     * @subresource gyro.google.compute.ComputeAutoscalingPolicyCpuUtilization
     */
    @Updatable
    public ComputeAutoscalingPolicyCpuUtilization getCpuUtilization() {
        return cpuUtilization;
    }

    public void setCpuUtilization(ComputeAutoscalingPolicyCpuUtilization cpuUtilization) {
        this.cpuUtilization = cpuUtilization;
    }

    /**
     * Configuration parameters of autoscaling based on a custom metric.
     *
     * @subresource gyro.google.compute.ComputeAutoscalingPolicyCustomMetricUtilization
     */
    public List<ComputeAutoscalingPolicyCustomMetricUtilization> getCustomMetricUtilization() {
        if (customMetricUtilization == null) {
            customMetricUtilization = new ArrayList<>();
        }

        return customMetricUtilization;
    }

    public void setCustomMetricUtilization(
        List<ComputeAutoscalingPolicyCustomMetricUtilization> customMetricUtilization) {
        this.customMetricUtilization = customMetricUtilization;
    }

    /**
     * Configuration parameters of autoscaling based on load balancer.
     *
     * @subresource gyro.google.compute.ComputeAutoscalingPolicyLoadBalancingUtilization
     */
    @Updatable
    public ComputeAutoscalingPolicyLoadBalancingUtilization getLoadBalancingUtilization() {
        return loadBalancingUtilization;
    }

    public void setLoadBalancingUtilization(
        ComputeAutoscalingPolicyLoadBalancingUtilization loadBalancingUtilization) {
        this.loadBalancingUtilization = loadBalancingUtilization;
    }

    /**
     * The maximum number of instances that the autoscaler can scale up to.
     * The maximum number of replicas should not be lower than minimal number of replicas.
     */
    @Required
    @Updatable
    public Integer getMaxNumReplicas() {
        return maxNumReplicas;
    }

    public void setMaxNumReplicas(Integer maxNumReplicas) {
        this.maxNumReplicas = maxNumReplicas;
    }

    /**
     * The minimum number of replicas that the autoscaler can scale down to.
     * If not provided, autoscaler will choose a default value depending on maximum number of instances allowed.
     */
    @Min(0)
    @Updatable
    public Integer getMinNumReplicas() {
        return minNumReplicas;
    }

    public void setMinNumReplicas(Integer minNumReplicas) {
        this.minNumReplicas = minNumReplicas;
    }

    /**
     * Operating mode for this policy.
     */
    @ValidStrings({
        "OFF",
        "ON",
        "ONLY_UP"
    })
    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public AutoscalingPolicy copyTo() {
        AutoscalingPolicy.Builder builder = AutoscalingPolicy.newBuilder();

        if (getMaxNumReplicas() != null) {
            builder.setMaxNumReplicas(getMaxNumReplicas());
        }

        if (getMinNumReplicas() != null) {
            builder.setMinNumReplicas(getMinNumReplicas());
        }

        if (getCoolDownPeriodSec() != null) {
            builder.setCoolDownPeriodSec(getCoolDownPeriodSec());
        }

        if (getMode() != null) {
            builder.setMode(getMode());
        }

        if (getCpuUtilization() != null) {
            builder.setCpuUtilization(getCpuUtilization().copyTo());
        }

        if (getLoadBalancingUtilization() != null) {
            builder.setLoadBalancingUtilization(getLoadBalancingUtilization().copyTo());
        }

        builder.addAllCustomMetricUtilizations(getCustomMetricUtilization()
            .stream()
            .map(ComputeAutoscalingPolicyCustomMetricUtilization::copyTo)
            .collect(Collectors.toList()));

        return builder.build();
    }

    @Override
    public void copyFrom(AutoscalingPolicy model) {
        if (model.hasCoolDownPeriodSec()) {
            setCoolDownPeriodSec(model.getCoolDownPeriodSec());
        }

        if (model.hasMaxNumReplicas()) {
            setMaxNumReplicas(model.getMaxNumReplicas());
        }

        if (model.hasMinNumReplicas()) {
            setMinNumReplicas(model.getMinNumReplicas());
        }

        if (model.hasMode()) {
            setMode(model.getMode());
        }

        setCpuUtilization(null);
        if (model.hasLoadBalancingUtilization()) {
            ComputeAutoscalingPolicyCpuUtilization cpu =
                newSubresource(ComputeAutoscalingPolicyCpuUtilization.class);
            cpu.copyFrom(model.getCpuUtilization());

            setCpuUtilization(cpu);
        }

        setLoadBalancingUtilization(null);
        if (model.hasLoadBalancingUtilization()) {
            ComputeAutoscalingPolicyLoadBalancingUtilization lbu =
                newSubresource(ComputeAutoscalingPolicyLoadBalancingUtilization.class);
            lbu.copyFrom(model.getLoadBalancingUtilization());

            setLoadBalancingUtilization(lbu);
        }

        List<ComputeAutoscalingPolicyCustomMetricUtilization> diffableCustomMetricUtilization = null;
        List<AutoscalingPolicyCustomMetricUtilization> customMetricUtilizations = model.getCustomMetricUtilizationsList();
        if (!customMetricUtilizations.isEmpty()) {
            diffableCustomMetricUtilization = customMetricUtilizations
                .stream()
                .map(e -> {
                    ComputeAutoscalingPolicyCustomMetricUtilization cmu =
                        newSubresource(ComputeAutoscalingPolicyCustomMetricUtilization.class);
                    cmu.copyFrom(e);

                    return cmu;
                }).collect(Collectors.toList());
        }
        setCustomMetricUtilization(diffableCustomMetricUtilization);
    }

    @Override
    public String primaryKey() {
        return "";
    }

    @Override
    public List<ValidationError> validate(Set<String> configuredFields) {
        List<ValidationError> errors = new ArrayList<>();

        if (getMinNumReplicas() != null && getMaxNumReplicas() < getMinNumReplicas()) {
            errors.add(new ValidationError(this, "max-num-replicas",
                "'max-num-replicas' cannot be smaller than 'min-num-replicas'"));
        }
        return errors;
    }
}
