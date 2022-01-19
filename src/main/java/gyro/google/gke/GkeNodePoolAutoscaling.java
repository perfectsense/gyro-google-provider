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
import java.util.Set;

import com.google.container.v1.NodePoolAutoscaling;
import gyro.core.resource.Diffable;
import gyro.core.resource.Updatable;
import gyro.core.validation.Min;
import gyro.core.validation.Required;
import gyro.core.validation.ValidationError;
import gyro.google.Copyable;

public class GkeNodePoolAutoscaling extends Diffable implements Copyable<NodePoolAutoscaling> {

    private Boolean autoprovisioned;
    private Boolean enabled;
    private Integer maxNodeCount;
    private Integer minNodeCount;

    /**
     * When set to ``true``, the node pool be deleted automatically.
     */
    @Updatable
    public Boolean getAutoprovisioned() {
        return autoprovisioned;
    }

    public void setAutoprovisioned(Boolean autoprovisioned) {
        this.autoprovisioned = autoprovisioned;
    }

    /**
     * When set to ``true``, autoscaling is enabled for this node pool.
     */
    @Required
    @Updatable
    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * The maximum number of nodes in the NodePool.
     */
    @Updatable
    @Min(1)
    public Integer getMaxNodeCount() {
        return maxNodeCount;
    }

    public void setMaxNodeCount(Integer maxNodeCount) {
        this.maxNodeCount = maxNodeCount;
    }

    /**
     * The minimum number of nodes in the NodePool.
     */
    @Updatable
    @Min(1)
    public Integer getMinNodeCount() {
        return minNodeCount;
    }

    public void setMinNodeCount(Integer minNodeCount) {
        this.minNodeCount = minNodeCount;
    }

    @Override
    public String primaryKey() {
        return "";
    }

    @Override
    public void copyFrom(NodePoolAutoscaling model) {
        setAutoprovisioned(model.getAutoprovisioned());
        setEnabled(model.getEnabled());
        setMaxNodeCount(model.getMaxNodeCount());
        setMinNodeCount(model.getMinNodeCount());
    }

    NodePoolAutoscaling toNodePoolAutoscaling() {
        NodePoolAutoscaling.Builder builder = NodePoolAutoscaling.newBuilder().setEnabled(getEnabled());

        if (getAutoprovisioned() != null) {
            builder.setAutoprovisioned(getAutoprovisioned());
        }

        if (getMaxNodeCount() != null) {
            builder.setMaxNodeCount(getMaxNodeCount());
        }

        if (getMinNodeCount() != null) {
            builder.setMinNodeCount(getMinNodeCount());
        }

        return builder.build();
    }

    @Override
    public List<ValidationError> validate(Set<String> configuredFields) {
        ArrayList<ValidationError> errors = new ArrayList<>();

        if (configuredFields.contains("autoprovisioned") && getAutoprovisioned() && configuredFields.contains(
            "min-node-count")) {
            errors.add(new ValidationError(
                this, null, "'min-node-count' cannot be set if 'autoprovisioned' is 'true'"));
        }

        if (getEnabled() && !configuredFields.contains("max-node-count")) {
            errors.add(new ValidationError(this, null, "'max-node-count' should be set if 'enabled' is 'true'"));
        }

        if (!getEnabled() && (configuredFields.contains("min-node-count")
            || configuredFields.contains("max-node-count"))) {
            errors.add(new ValidationError(this, null,
                "'min-node-count' and 'max-node-count' cannot be set if 'enabled' is 'false'"));
        }
        return errors;
    }
}
