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

import com.google.cloud.compute.v1.AutoscalingPolicyCustomMetricUtilization;
import gyro.core.resource.Diffable;
import gyro.core.resource.Updatable;
import gyro.core.validation.Min;
import gyro.core.validation.Required;
import gyro.core.validation.ValidStrings;
import gyro.google.Copyable;

public class ComputeAutoscalingPolicyCustomMetricUtilization extends Diffable
    implements Copyable<AutoscalingPolicyCustomMetricUtilization> {

    private String metric;

    private Double utilizationTarget;

    private String utilizationTargetType;

    /**
     * The identifier (type) of the Stackdriver Monitoring metric.
     * The metric cannot have negative values. The metric must have a value type of INT64 or DOUBLE.
     */
    @Required
    public String getMetric() {
        return metric;
    }

    public void setMetric(String metric) {
        this.metric = metric;
    }

    /**
     * The target value of the metric that autoscaler should maintain.
     * A utilization metric scales number of virtual machines handling requests to increase or decrease proportionally to the metric. For example, a good metric to use as a utilization_target is compute.googleapis.com/instance/network/received_bytes_count. The autoscaler will work to keep this value constant for each of the instances.
     */
    @Min(0)
    @Required
    @Updatable
    public Double getUtilizationTarget() {
        return utilizationTarget;
    }

    public void setUtilizationTarget(Double utilizationTarget) {
        this.utilizationTarget = utilizationTarget;
    }

    /**
     * Defines how target utilization value is expressed for a Stackdriver Monitoring metric.
     * Either
     *
     * - ``GAUGE``
     * - ``DELTA_PER_SECOND``
     * - ``DELTA_PER_MINUTE``
     */
    @Updatable
    @ValidStrings({
        "DELTA_PER_MINUTE",
        "DELTA_PER_SECOND",
        "GAUGE"
    })
    public String getUtilizationTargetType() {
        return utilizationTargetType;
    }

    public void setUtilizationTargetType(String utilizationTargetType) {
        this.utilizationTargetType = utilizationTargetType;
    }

    public AutoscalingPolicyCustomMetricUtilization copyTo() {
        AutoscalingPolicyCustomMetricUtilization.Builder builder = AutoscalingPolicyCustomMetricUtilization.newBuilder();
        builder.setMetric(getMetric());
        builder.setUtilizationTarget(getUtilizationTarget());

        if (getUtilizationTargetType() != null) {
            builder.setUtilizationTargetType(getUtilizationTargetType());
        }

        return builder.build();
    }

    @Override
    public void copyFrom(AutoscalingPolicyCustomMetricUtilization model) {
        setMetric(model.getMetric());
        setUtilizationTarget(model.getUtilizationTarget());
        setUtilizationTargetType(model.getUtilizationTargetType());
    }

    @Override
    public String primaryKey() {
        return getMetric();
    }
}
