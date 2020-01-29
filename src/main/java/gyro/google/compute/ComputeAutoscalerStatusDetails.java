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

import com.google.api.services.compute.model.AutoscalerStatusDetails;
import gyro.core.resource.Diffable;
import gyro.core.resource.Output;
import gyro.core.validation.ValidStrings;
import gyro.google.Copyable;

public class ComputeAutoscalerStatusDetails extends Diffable implements Copyable<AutoscalerStatusDetails> {

    private String message;

    private String type;

    /**
     * The status message.
     */
    @Output
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * The type of error, warning, or notice returned.
     * Current set of possible values:
     *
     * - ``ALL_INSTANCES_UNHEALTHY`` (WARNING): All instances in the instance group are unhealthy (not in RUNNING state).
     * - ``BACKEND_SERVICE_DOES_NOT_EXIST`` (ERROR): There is no backend service attached to the instance group.
     * - ``CAPPED_AT_MAX_NUM_REPLICAS`` (WARNING): Autoscaler recommends a size greater than maxNumReplicas.
     * - ``CUSTOM_METRIC_DATA_POINTS_TOO_SPARSE`` (WARNING): The custom metric samples are not exported often enough to be a credible base for autoscaling.
     * - ``CUSTOM_METRIC_INVALID`` (ERROR): The custom metric that was specified does not exist or does not have the necessary labels.
     * - ``MIN_EQUALS_MAX`` (WARNING): The minNumReplicas is equal to maxNumReplicas. This means the autoscaler cannot add or remove instances from the instance group.
     * - ``MISSING_CUSTOM_METRIC_DATA_POINTS`` (WARNING): The autoscaler did not receive any data from the custom metric configured for autoscaling.
     * - ``MISSING_LOAD_BALANCING_DATA_POINTS`` (WARNING): The autoscaler is configured to scale based on a load balancing signal but the instance group has not received any requests from the load balancer.
     * - ``MODE_OFF`` (WARNING): Autoscaling is turned off. The number of instances in the group won't change automatically. The autoscaling configuration is preserved.
     * - ``MODE_ONLY_UP`` (WARNING): Autoscaling is in the "Autoscale only up" mode. The autoscaler can add instances but not remove any.
     * - ``MORE_THAN_ONE_BACKEND_SERVICE`` (ERROR): The instance group cannot be autoscaled because it has more than one backend service attached to it.
     * - ``NOT_ENOUGH_QUOTA_AVAILABLE`` (ERROR): There is insufficient quota for the necessary resources, such as CPU or number of instances.
     * - ``REGION_RESOURCE_STOCKOUT`` (ERROR): Shown only for regional autoscalers: there is a resource stockout in the chosen region.
     * - ``SCALING_TARGET_DOES_NOT_EXIST`` (ERROR): The target to be scaled does not exist.
     * - ``UNSUPPORTED_MAX_RATE_LOAD_BALANCING_CONFIGURATION`` (ERROR): Autoscaling does not work with an HTTP/S load balancer that has been configured for maxRate.
     * - ``ZONE_RESOURCE_STOCKOUT`` (ERROR): For zonal autoscalers: there is a resource stockout in the chosen zone. For regional autoscalers: in at least one of the zones you're using there is a resource stockout.
     */
    @ValidStrings({
        "ALL_INSTANCES_UNHEALTHY",
        "BACKEND_SERVICE_DOES_NOT_EXIST",
        "CAPPED_AT_MAX_NUM_REPLICAS",
        "CUSTOM_METRIC_DATA_POINTS_TOO_SPARSE",
        "CUSTOM_METRIC_INVALID",
        "MIN_EQUALS_MAX",
        "MISSING_CUSTOM_METRIC_DATA_POINTS",
        "MISSING_LOAD_BALANCING_DATA_POINTS",
        "MODE_OFF",
        "MODE_ONLY_UP",
        "MORE_THAN_ONE_BACKEND_SERVICE",
        "NOT_ENOUGH_QUOTA_AVAILABLE",
        "REGION_RESOURCE_STOCKOUT",
        "SCALING_TARGET_DOES_NOT_EXIST",
        "UNKNOWN",
        "UNSUPPORTED_MAX_RATE_LOAD_BALANCING_CONFIGURATION",
        "ZONE_RESOURCE_STOCKOUT"
    })
    @Output
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public void copyFrom(AutoscalerStatusDetails model) {
        setMessage(model.getMessage());
        setType(model.getType());
    }

    @Override
    public String primaryKey() {
        return "";
    }
}
