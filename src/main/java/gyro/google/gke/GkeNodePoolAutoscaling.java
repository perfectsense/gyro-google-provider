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

import com.google.container.v1.NodePoolAutoscaling;
import gyro.core.resource.Diffable;
import gyro.core.resource.Updatable;
import gyro.core.validation.Required;
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
        return NodePoolAutoscaling.newBuilder().setAutoprovisioned(getAutoprovisioned()).setEnabled(getEnabled())
            .setMaxNodeCount(getMaxNodeCount()).setMinNodeCount(getMinNodeCount()).build();
    }
}
