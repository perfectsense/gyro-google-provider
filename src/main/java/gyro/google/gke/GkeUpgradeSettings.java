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

import com.google.container.v1.NodePool;
import gyro.core.resource.Diffable;
import gyro.google.Copyable;

public class GkeUpgradeSettings extends Diffable implements Copyable<NodePool.UpgradeSettings> {

    private Integer maxSurge;
    private Integer maxUnavailable;

    public Integer getMaxSurge() {
        return maxSurge;
    }

    public void setMaxSurge(Integer maxSurge) {
        this.maxSurge = maxSurge;
    }

    public Integer getMaxUnavailable() {
        return maxUnavailable;
    }

    public void setMaxUnavailable(Integer maxUnavailable) {
        this.maxUnavailable = maxUnavailable;
    }

    @Override
    public String primaryKey() {
        return "";
    }

    @Override
    public void copyFrom(NodePool.UpgradeSettings model) throws Exception {
        setMaxSurge(model.getMaxSurge());
        setMaxUnavailable(model.getMaxUnavailable());
    }

    NodePool.UpgradeSettings toUpgradeSettings() {
        return NodePool.UpgradeSettings.newBuilder().setMaxSurge(getMaxSurge())
            .setMaxUnavailable(getMaxUnavailable()).build();
    }
}
