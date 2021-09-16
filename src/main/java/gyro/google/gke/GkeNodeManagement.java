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

import com.google.container.v1.NodeManagement;
import gyro.core.resource.Diffable;
import gyro.core.resource.Output;
import gyro.google.Copyable;

public class GkeNodeManagement extends Diffable implements Copyable<NodeManagement> {

    private Boolean autoUpgrade;
    private Boolean autoRepair;
    private GkeAutoUpgradeOptions upgradeOptions;

    public Boolean getAutoUpgrade() {
        return autoUpgrade;
    }

    public void setAutoUpgrade(Boolean autoUpgrade) {
        this.autoUpgrade = autoUpgrade;
    }

    public Boolean getAutoRepair() {
        return autoRepair;
    }

    public void setAutoRepair(Boolean autoRepair) {
        this.autoRepair = autoRepair;
    }

    @Output
    public GkeAutoUpgradeOptions getUpgradeOptions() {
        return upgradeOptions;
    }

    public void setUpgradeOptions(GkeAutoUpgradeOptions upgradeOptions) {
        this.upgradeOptions = upgradeOptions;
    }

    @Override
    public String primaryKey() {
        return "";
    }

    @Override
    public void copyFrom(NodeManagement model) {
        setAutoRepair(model.getAutoRepair());
        setAutoUpgrade(model.getAutoUpgrade());

        setUpgradeOptions(null);
        if (model.hasUpgradeOptions()) {
            GkeAutoUpgradeOptions options = newSubresource(GkeAutoUpgradeOptions.class);
            options.copyFrom(model.getUpgradeOptions());
            setUpgradeOptions(options);
        }
    }

    NodeManagement toNodeManagement() {
        return NodeManagement.newBuilder().setAutoRepair(getAutoRepair()).setAutoUpgrade(getAutoUpgrade()).build();
    }
}
