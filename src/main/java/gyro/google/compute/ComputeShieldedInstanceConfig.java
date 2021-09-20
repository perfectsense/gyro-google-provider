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

import com.google.cloud.compute.v1.ShieldedInstanceConfig;
import gyro.core.resource.Diffable;
import gyro.google.Copyable;

public class ComputeShieldedInstanceConfig extends Diffable implements Copyable<ShieldedInstanceConfig> {

    private Boolean enableIntegrityMonitoring;

    private Boolean enableSecureBoot;

    private Boolean enableVtpm;

    /**
     * Defines whether the instance has integrity monitoring enabled.
     */
    public Boolean getEnableIntegrityMonitoring() {
        return enableIntegrityMonitoring;
    }

    public void setEnableIntegrityMonitoring(Boolean enableIntegrityMonitoring) {
        this.enableIntegrityMonitoring = enableIntegrityMonitoring;
    }

    /**
     * Defines whether the instance has Secure Boot enabled.
     */
    public Boolean getEnableSecureBoot() {
        return enableSecureBoot;
    }

    public void setEnableSecureBoot(Boolean enableSecureBoot) {
        this.enableSecureBoot = enableSecureBoot;
    }

    /**
     * Defines whether the instance has the vTPM enabled.
     */
    public Boolean getEnableVtpm() {
        return enableVtpm;
    }

    public void setEnableVtpm(Boolean enableVtpm) {
        this.enableVtpm = enableVtpm;
    }

    @Override
    public void copyFrom(ShieldedInstanceConfig model) {
        setEnableIntegrityMonitoring(model.getEnableIntegrityMonitoring());
        setEnableSecureBoot(model.getEnableSecureBoot());
        setEnableVtpm(model.getEnableVtpm());
    }

    public ShieldedInstanceConfig toShieldedInstanceConfig() {
        ShieldedInstanceConfig shieldedInstanceConfig = new ShieldedInstanceConfig();
        shieldedInstanceConfig.setEnableIntegrityMonitoring(getEnableIntegrityMonitoring());
        shieldedInstanceConfig.setEnableSecureBoot(getEnableSecureBoot());
        shieldedInstanceConfig.setEnableVtpm(getEnableVtpm());
        return shieldedInstanceConfig;
    }

    @Override
    public String primaryKey() {
        return "";
    }
}
