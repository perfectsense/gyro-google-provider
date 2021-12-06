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

import com.google.container.v1.AutoUpgradeOptions;
import gyro.core.resource.Diffable;
import gyro.core.resource.Output;
import gyro.google.Copyable;

public class GkeAutoUpgradeOptions extends Diffable implements Copyable<AutoUpgradeOptions> {

    private String autoUpgradeStartTime;
    private String description;

    /**
     * The approximate start time for the upgrades.
     */
    @Output
    public String getAutoUpgradeStartTime() {
        return autoUpgradeStartTime;
    }

    public void setAutoUpgradeStartTime(String autoUpgradeStartTime) {
        this.autoUpgradeStartTime = autoUpgradeStartTime;
    }

    /**
     * The description of the upgrade.
     */
    @Output
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String primaryKey() {
        return "";
    }

    @Override
    public void copyFrom(AutoUpgradeOptions model) {
        setAutoUpgradeStartTime(model.getAutoUpgradeStartTime());
        setDescription(model.getDescription());
    }
}
