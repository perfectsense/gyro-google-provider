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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.google.container.v1.ClusterAutoscaling;
import gyro.core.resource.Diffable;
import gyro.core.resource.Updatable;
import gyro.core.validation.Required;
import gyro.google.Copyable;

public class GkeClusterAutoscaling extends Diffable implements Copyable<ClusterAutoscaling> {

    private Boolean enableNodeAutoprovisioning;
    private List<GkeResourceLimit> resourceLimit;
    private GkeAutoprovisioningNodePoolDefaults autoprovisioningNodePoolDefaults;
    private List<String> autoprovisioningLocations;

    /**
     * When set to ``true``, node pools are created and deleted automatically.
     */
    @Required
    @Updatable
    public Boolean getEnableNodeAutoprovisioning() {
        return enableNodeAutoprovisioning;
    }

    public void setEnableNodeAutoprovisioning(Boolean enableNodeAutoprovisioning) {
        this.enableNodeAutoprovisioning = enableNodeAutoprovisioning;
    }

    /**
     * The list of global constraints regarding minimum and maximum amount of resources in the cluster.
     */
    @Updatable
    public List<GkeResourceLimit> getResourceLimit() {
        if (resourceLimit == null) {
            resourceLimit = new ArrayList<>();
        }
        return resourceLimit;
    }

    public void setResourceLimit(List<GkeResourceLimit> resourceLimit) {
        this.resourceLimit = resourceLimit;
    }

    /**
     *  The defaults for a node pool created by NAP.
     */
    @Updatable
    public GkeAutoprovisioningNodePoolDefaults getAutoprovisioningNodePoolDefaults() {
        return autoprovisioningNodePoolDefaults;
    }

    public void setAutoprovisioningNodePoolDefaults(GkeAutoprovisioningNodePoolDefaults autoprovisioningNodePoolDefaults) {
        this.autoprovisioningNodePoolDefaults = autoprovisioningNodePoolDefaults;
    }

    /**
     * The list of Google Compute Engine in which the NodePool's nodes can be created by NAP.
     */
    @Updatable
    public List<String> getAutoprovisioningLocations() {
        if (autoprovisioningLocations == null) {
            autoprovisioningLocations = new ArrayList<>();
        }
        return autoprovisioningLocations;
    }

    public void setAutoprovisioningLocations(List<String> autoprovisioningLocations) {
        this.autoprovisioningLocations = autoprovisioningLocations;
    }

    @Override
    public String primaryKey() {
        return "";
    }

    @Override
    public void copyFrom(ClusterAutoscaling model) throws Exception {
        setEnableNodeAutoprovisioning(model.getEnableNodeAutoprovisioning());

        setAutoprovisioningLocations(null);
        if (model.getAutoprovisioningLocationsCount() > 0) {
            setAutoprovisioningLocations(model.getAutoprovisioningLocationsList());
        }

        setResourceLimit(null);
        if (model.getResourceLimitsCount() > 0) {
            setResourceLimit(model.getResourceLimitsList().stream().map(r -> {
                GkeResourceLimit limit = newSubresource(GkeResourceLimit.class);
                limit.copyFrom(r);
                return limit;
            }).collect(Collectors.toList()));
        }

        setAutoprovisioningNodePoolDefaults(null);
        if (model.hasAutoprovisioningNodePoolDefaults()) {
            GkeAutoprovisioningNodePoolDefaults defaults = newSubresource(GkeAutoprovisioningNodePoolDefaults.class);
            defaults.copyFrom(model.getAutoprovisioningNodePoolDefaults());
            setAutoprovisioningNodePoolDefaults(defaults);
        }
    }

    ClusterAutoscaling toClusterAutoscaling() {
        ClusterAutoscaling.Builder builder = ClusterAutoscaling.newBuilder()
            .setEnableNodeAutoprovisioning(getEnableNodeAutoprovisioning());

        if (!getResourceLimit().isEmpty()) {
            builder.addAllResourceLimits(getResourceLimit().stream()
                .map(GkeResourceLimit::toResourceLimit)
                .collect(Collectors.toList()));
        }

        if (getAutoprovisioningNodePoolDefaults() != null) {
            builder.setAutoprovisioningNodePoolDefaults(getAutoprovisioningNodePoolDefaults()
                .toAutoprovisioningNodePoolDefaults());
        }

        if (!getAutoprovisioningLocations().isEmpty()) {
            builder.addAllAutoprovisioningLocations(getAutoprovisioningLocations());
        }

        return builder.build();
    }
}
