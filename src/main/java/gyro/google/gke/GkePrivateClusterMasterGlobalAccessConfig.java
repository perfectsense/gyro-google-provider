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

import com.google.container.v1.PrivateClusterMasterGlobalAccessConfig;
import gyro.core.resource.Diffable;
import gyro.core.resource.Updatable;
import gyro.core.validation.Required;
import gyro.google.Copyable;

public class GkePrivateClusterMasterGlobalAccessConfig extends Diffable
    implements Copyable<PrivateClusterMasterGlobalAccessConfig> {

    private Boolean enabled;

    /**
     * When set to ``true`` master is accessible globally.
     */
    @Required
    @Updatable
    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public String primaryKey() {
        return "";
    }

    @Override
    public void copyFrom(PrivateClusterMasterGlobalAccessConfig model) throws Exception {
        setEnabled(model.getEnabled());
    }

    PrivateClusterMasterGlobalAccessConfig toPrivateClusterMasterGlobalAccessConfig() {
        return PrivateClusterMasterGlobalAccessConfig.newBuilder().setEnabled(getEnabled()).build();
    }
}
