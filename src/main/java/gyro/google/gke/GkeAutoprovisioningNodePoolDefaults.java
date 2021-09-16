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

import com.google.container.v1.AutoprovisioningNodePoolDefaults;
import gyro.core.resource.Diffable;
import gyro.google.Copyable;

public class GkeAutoprovisioningNodePoolDefaults extends Diffable
    implements Copyable<AutoprovisioningNodePoolDefaults> {

    private List<String> oauthScopes;
    private String serviceAccount;
    private GkeUpgradeSettings upgradeSettings;
    private GkeNodeManagement management;
    private String minCpuPlatform;
    private Integer diskSizeGb;
    private String diskType;
    private GkeShieldedInstanceConfig shieldedInstanceConfig;
    private String bootDiskKmsKey;

    public List<String> getOauthScopes() {
        if (oauthScopes == null) {
            oauthScopes = new ArrayList<>();
        }
        return oauthScopes;
    }

    public void setOauthScopes(List<String> oauthScopes) {
        if (oauthScopes == null) {
            oauthScopes = new ArrayList<>();
        }

        this.oauthScopes = oauthScopes;
    }

    public String getServiceAccount() {
        return serviceAccount;
    }

    public void setServiceAccount(String serviceAccount) {
        this.serviceAccount = serviceAccount;
    }

    public GkeUpgradeSettings getUpgradeSettings() {
        return upgradeSettings;
    }

    public void setUpgradeSettings(GkeUpgradeSettings upgradeSettings) {
        this.upgradeSettings = upgradeSettings;
    }

    public GkeNodeManagement getManagement() {
        return management;
    }

    public void setManagement(GkeNodeManagement management) {
        this.management = management;
    }

    public String getMinCpuPlatform() {
        return minCpuPlatform;
    }

    public void setMinCpuPlatform(String minCpuPlatform) {
        this.minCpuPlatform = minCpuPlatform;
    }

    public Integer getDiskSizeGb() {
        return diskSizeGb;
    }

    public void setDiskSizeGb(Integer diskSizeGb) {
        this.diskSizeGb = diskSizeGb;
    }

    public String getDiskType() {
        return diskType;
    }

    public void setDiskType(String diskType) {
        this.diskType = diskType;
    }

    public GkeShieldedInstanceConfig getShieldedInstanceConfig() {
        return shieldedInstanceConfig;
    }

    public void setShieldedInstanceConfig(GkeShieldedInstanceConfig shieldedInstanceConfig) {
        this.shieldedInstanceConfig = shieldedInstanceConfig;
    }

    public String getBootDiskKmsKey() {
        return bootDiskKmsKey;
    }

    public void setBootDiskKmsKey(String bootDiskKmsKey) {
        this.bootDiskKmsKey = bootDiskKmsKey;
    }

    @Override
    public String primaryKey() {
        return "";
    }

    @Override
    public void copyFrom(AutoprovisioningNodePoolDefaults model) throws Exception {
        setUpgradeSettings(null);
        if (model.hasUpgradeSettings()) {
            GkeUpgradeSettings settings = newSubresource(GkeUpgradeSettings.class);
            settings.copyFrom(model.getUpgradeSettings());
            setUpgradeSettings(settings);
        }

        setManagement(null);
        if (model.hasManagement()) {
            GkeNodeManagement nodeManagement = newSubresource(GkeNodeManagement.class);
            nodeManagement.copyFrom(model.getManagement());
            setManagement(nodeManagement);
        }

        setShieldedInstanceConfig(null);
        if (model.hasShieldedInstanceConfig()) {
            GkeShieldedInstanceConfig config = newSubresource(GkeShieldedInstanceConfig.class);
            config.copyFrom(model.getShieldedInstanceConfig());
            setShieldedInstanceConfig(config);
        }

        setOauthScopes(model.getOauthScopesList());
        setServiceAccount(model.getServiceAccount());
        setMinCpuPlatform(model.getMinCpuPlatform());
        setDiskSizeGb(model.getDiskSizeGb());
        setDiskType(model.getDiskType());
        setBootDiskKmsKey(model.getBootDiskKmsKey());
    }

    AutoprovisioningNodePoolDefaults toAutoprovisioningNodePoolDefaults() {
        AutoprovisioningNodePoolDefaults.Builder builder = AutoprovisioningNodePoolDefaults.newBuilder();

        if (!getOauthScopes().isEmpty()) {
            builder.addAllOauthScopes(getOauthScopes());
        }

        if (getServiceAccount() != null) {
            builder.setServiceAccount(getServiceAccount());
        }

        if (getUpgradeSettings() != null) {
            builder.setUpgradeSettings(getUpgradeSettings().toUpgradeSettings());
        }

        if (getManagement() != null) {
            builder.setManagement(getManagement().toNodeManagement());
        }

        if (getMinCpuPlatform() != null) {
            builder.setMinCpuPlatform(getMinCpuPlatform());
        }

        if (getDiskSizeGb() != null) {
            builder.setDiskSizeGb(getDiskSizeGb());
        }

        if (getDiskType() != null) {
            builder.setDiskType(getDiskType());
        }

        if (getShieldedInstanceConfig() != null) {
            builder.setShieldedInstanceConfig(getShieldedInstanceConfig().toShieldedInstanceConfig());
        }

        if (getBootDiskKmsKey() != null) {
            builder.setBootDiskKmsKey(getBootDiskKmsKey());
        }

        return builder.build();
    }
}
