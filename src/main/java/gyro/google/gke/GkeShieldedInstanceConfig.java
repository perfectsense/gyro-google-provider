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

import com.google.container.v1.ShieldedInstanceConfig;
import gyro.core.resource.Diffable;
import gyro.google.Copyable;

public class GkeShieldedInstanceConfig extends Diffable implements Copyable<ShieldedInstanceConfig> {

    private Boolean enableSecureBoot;
    private Boolean enableIntegrityMonitoring;

    public Boolean getEnableSecureBoot() {
        return enableSecureBoot;
    }

    public void setEnableSecureBoot(Boolean enableSecureBoot) {
        this.enableSecureBoot = enableSecureBoot;
    }

    public Boolean getEnableIntegrityMonitoring() {
        return enableIntegrityMonitoring;
    }

    public void setEnableIntegrityMonitoring(Boolean enableIntegrityMonitoring) {
        this.enableIntegrityMonitoring = enableIntegrityMonitoring;
    }

    @Override
    public String primaryKey() {
        return "";
    }

    @Override
    public void copyFrom(ShieldedInstanceConfig model) throws Exception {
        setEnableIntegrityMonitoring(model.getEnableIntegrityMonitoring());
        setEnableSecureBoot(model.getEnableSecureBoot());
    }

    ShieldedInstanceConfig toShieldedInstanceConfig() {
        return ShieldedInstanceConfig.newBuilder().setEnableIntegrityMonitoring(getEnableIntegrityMonitoring())
            .setEnableSecureBoot(getEnableSecureBoot()).build();
    }
}
