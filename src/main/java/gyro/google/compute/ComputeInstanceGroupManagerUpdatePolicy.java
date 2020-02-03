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

import java.util.Optional;

import com.google.api.services.compute.model.InstanceGroupManagerUpdatePolicy;
import gyro.core.resource.Diffable;
import gyro.core.validation.ValidStrings;
import gyro.google.Copyable;

public class ComputeInstanceGroupManagerUpdatePolicy extends Diffable
    implements Copyable<InstanceGroupManagerUpdatePolicy> {

    private String instanceRedistributionType;

    private ComputeFixedOrPercent maxSurge;

    private ComputeFixedOrPercent maxUnavailable;

    private String minimalAction;

    private String type;

    /**
     * The instance redistribution policy for regional managed instance groups.
     * Valid values are:
     *
     * - ``PROACTIVE`` (default): The group attempts to maintain an even distribution of VM instances across zones in the region.
     * - ``NONE``: For non-autoscaled groups, proactive redistribution is disabled.
     */
    @ValidStrings({
        "NONE",
        "PROACTIVE"
    })
    public String getInstanceRedistributionType() {
        return instanceRedistributionType;
    }

    public void setInstanceRedistributionType(String instanceRedistributionType) {
        this.instanceRedistributionType = instanceRedistributionType;
    }

    /**
     * The maximum number of instances that can be created above the specified target size during the update process.
     * By default, a fixed value of 1 is used. This value can be either a fixed number or a percentage if the instance group has 10 or more instances. If you set a percentage, the number of instances will be rounded up if necessary. At least one of either ``max-surge`` or ``max-unavailable`` must be greater than 0.
     *
     * @subresource gyro.google.compute.ComputeFixedOrPercent
     */
    public ComputeFixedOrPercent getMaxSurge() {
        return maxSurge;
    }

    public void setMaxSurge(ComputeFixedOrPercent maxSurge) {
        this.maxSurge = maxSurge;
    }

    /**
     * The maximum number of instances that can be unavailable during the update process.
     * An instance is considered available if all of the following conditions are satisfied:
     *
     * - The instance's status is RUNNING.
     * - If there is a health check on the instance group, the instance's liveness health check result must be HEALTHY at least once. If there is no health check on the group, then the instance only needs to have a status of RUNNING to be considered available.
     *
     * By default, a fixed value of 1 is used. This value can be either a fixed number or a percentage if the instance group has 10 or more instances. If you set a percentage, the number of instances will be rounded up if necessary.
     *
     * At least one of either ``max-surge`` or ``max-unavailable`` must be greater than 0.
     *
     * @subresource gyro.google.compute.ComputeFixedOrPercent
     */
    public ComputeFixedOrPercent getMaxUnavailable() {
        return maxUnavailable;
    }

    public void setMaxUnavailable(ComputeFixedOrPercent maxUnavailable) {
        this.maxUnavailable = maxUnavailable;
    }

    /**
     * Minimal action to be taken on an instance.
     * You can specify either ``RESTART`` to restart existing instances or ``REPLACE`` to delete and create new instances from the target template. If you specify a ``RESTART``, the Updater will attempt to perform that action only. However, if the Updater determines that the minimal action you specify is not enough to perform the update, it might perform a more disruptive action.
     */
    @ValidStrings({
        "REPLACE",
        "RESTART"
    })
    public String getMinimalAction() {
        return minimalAction;
    }

    public void setMinimalAction(String minimalAction) {
        this.minimalAction = minimalAction;
    }

    /**
     * The type of update process.
     * You can specify either ``PROACTIVE`` so that the instance group manager proactively executes actions in order to bring instances to their target versions or ``OPPORTUNISTIC`` so that no action is proactively executed but the update will be performed as part of other actions.
     */
    @ValidStrings({
        "OPPORTUNISTIC",
        "PROACTIVE"
    })
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public InstanceGroupManagerUpdatePolicy copyTo() {
        InstanceGroupManagerUpdatePolicy instanceGroupManagerUpdatePolicy = new InstanceGroupManagerUpdatePolicy();
        // instanceRedistributionType is not available from Google SDK yet.
        instanceGroupManagerUpdatePolicy.set("instanceRedistributionType", getInstanceRedistributionType());
        Optional.ofNullable(getMaxSurge())
            .map(ComputeFixedOrPercent::copyTo)
            .ifPresent(instanceGroupManagerUpdatePolicy::setMaxSurge);
        Optional.ofNullable(getMaxUnavailable())
            .map(ComputeFixedOrPercent::copyTo)
            .ifPresent(instanceGroupManagerUpdatePolicy::setMaxUnavailable);
        instanceGroupManagerUpdatePolicy.setMinimalAction(getMinimalAction());
        instanceGroupManagerUpdatePolicy.setType(getType());
        return instanceGroupManagerUpdatePolicy;
    }

    @Override
    public void copyFrom(InstanceGroupManagerUpdatePolicy model) {
        // instanceRedistributionType is not available from Google SDK yet.
        setInstanceRedistributionType(Optional.ofNullable(model.get("instanceRedistributionType"))
            .filter(String.class::isInstance)
            .map(String.class::cast)
            .orElse(null));
        setMaxSurge(
            Optional.ofNullable(model.getMaxSurge())
                .map(e -> {
                    ComputeFixedOrPercent computeFixedOrPercent = newSubresource(ComputeFixedOrPercent.class);
                    computeFixedOrPercent.copyFrom(e);
                    return computeFixedOrPercent;
                })
                .orElse(null));
        setMaxUnavailable(
            Optional.ofNullable(model.getMaxUnavailable())
                .map(e -> {
                    ComputeFixedOrPercent computeFixedOrPercent = newSubresource(ComputeFixedOrPercent.class);
                    computeFixedOrPercent.copyFrom(e);
                    return computeFixedOrPercent;
                })
                .orElse(null));
        setMinimalAction(model.getMinimalAction());
        setType(model.getType());
    }

    @Override
    public String primaryKey() {
        return "";
    }
}
