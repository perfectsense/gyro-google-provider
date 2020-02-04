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
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.api.services.compute.model.AutoscalingPolicy;
import com.google.api.services.compute.model.AutoscalingPolicyCustomMetricUtilization;
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
     * This cannot be less than ``0``. If not provided, autoscaler will choose a default value depending on maximum number of instances allowed.
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
     * Operating mode for this policy. Valid values are ``OFF``, ``ON`` or ``ONLY_UP``.
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
        AutoscalingPolicy autoscalingPolicy = new AutoscalingPolicy();
        autoscalingPolicy.setCoolDownPeriodSec(getCoolDownPeriodSec());
        Optional.ofNullable(getCpuUtilization())
            .map(ComputeAutoscalingPolicyCpuUtilization::copyTo)
            .ifPresent(autoscalingPolicy::setCpuUtilization);
        autoscalingPolicy.setCustomMetricUtilizations(getCustomMetricUtilization()
            .stream()
            .map(ComputeAutoscalingPolicyCustomMetricUtilization::copyTo)
            .collect(Collectors.toList()));
        Optional.ofNullable(getLoadBalancingUtilization())
            .map(ComputeAutoscalingPolicyLoadBalancingUtilization::copyTo)
            .ifPresent(autoscalingPolicy::setLoadBalancingUtilization);
        autoscalingPolicy.setMaxNumReplicas(getMaxNumReplicas());
        autoscalingPolicy.setMinNumReplicas(getMinNumReplicas());
        // `mode` is not available from Google SDK yet.
        autoscalingPolicy.set("mode", getMode());
        return autoscalingPolicy;
    }

    @Override
    public void copyFrom(AutoscalingPolicy model) {
        setCoolDownPeriodSec(model.getCoolDownPeriodSec());
        setCpuUtilization(Optional.ofNullable(model.getCpuUtilization())
            .map(e -> {
                ComputeAutoscalingPolicyCpuUtilization cpuUtilization = newSubresource(
                    ComputeAutoscalingPolicyCpuUtilization.class);
                cpuUtilization.copyFrom(e);
                return cpuUtilization;
            })
            .orElse(null));

        List<ComputeAutoscalingPolicyCustomMetricUtilization> diffableCustomMetricUtilization = null;
        List<AutoscalingPolicyCustomMetricUtilization> customMetricUtilizations = model.getCustomMetricUtilizations();

        if (customMetricUtilizations != null && !customMetricUtilizations.isEmpty()) {
            diffableCustomMetricUtilization = customMetricUtilizations
                .stream()
                .map(e -> {
                    ComputeAutoscalingPolicyCustomMetricUtilization customMetricUtilization = newSubresource(
                        ComputeAutoscalingPolicyCustomMetricUtilization.class);
                    customMetricUtilization.copyFrom(e);
                    return customMetricUtilization;
                })
                .collect(Collectors.toList());
        }
        setCustomMetricUtilization(diffableCustomMetricUtilization);

        setLoadBalancingUtilization(Optional.ofNullable(model.getLoadBalancingUtilization())
            .map(e -> {
                ComputeAutoscalingPolicyLoadBalancingUtilization loadBalancingUtilization = newSubresource(
                    ComputeAutoscalingPolicyLoadBalancingUtilization.class);
                loadBalancingUtilization.copyFrom(e);
                return loadBalancingUtilization;
            })
            .orElse(null));
        setMaxNumReplicas(model.getMaxNumReplicas());
        setMinNumReplicas(model.getMinNumReplicas());
        // `mode` is not available from Google SDK yet.
        setMode(Optional.ofNullable(model.get("mode"))
            .filter(String.class::isInstance)
            .map(String.class::cast)
            .orElse(null));
    }

    @Override
    public String primaryKey() {
        return "";
    }

    @Override
    public List<ValidationError> validate(Set<String> configuredFields) {
        List<ValidationError> errors = new ArrayList<>();
        Integer minNumReplicas = getMinNumReplicas();

        if (minNumReplicas != null && getMaxNumReplicas() < minNumReplicas) {
            errors.add(new ValidationError(
                this,
                "max-num-replicas",
                "'max-num-replicas' cannot be smaller than 'min-num-replicas'"));
        }
        return errors;
    }
}
