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

import com.google.container.v1.AddonsConfig;
import gyro.core.resource.Diffable;
import gyro.google.Copyable;

public class GkeAddonsConfig extends Diffable implements Copyable<AddonsConfig> {

    private GkeHttpLoadBalancing httpLoadBalancing;
    private GkeHorizontalPodAutoscaling horizontalPodAutoscaling;
    private GkeNetworkPolicyConfig networkPolicyConfig;
    private GkeCloudRunConfig cloudRunConfig;
    private GkeDnsCacheConfig dnsCacheConfig;
    private GkeConfigConnectorConfig configConnectorConfig;

    public GkeHttpLoadBalancing getHttpLoadBalancing() {
        return httpLoadBalancing;
    }

    public void setHttpLoadBalancing(GkeHttpLoadBalancing httpLoadBalancing) {
        this.httpLoadBalancing = httpLoadBalancing;
    }

    public GkeHorizontalPodAutoscaling getHorizontalPodAutoscaling() {
        return horizontalPodAutoscaling;
    }

    public void setHorizontalPodAutoscaling(GkeHorizontalPodAutoscaling horizontalPodAutoscaling) {
        this.horizontalPodAutoscaling = horizontalPodAutoscaling;
    }

    public GkeNetworkPolicyConfig getNetworkPolicyConfig() {
        return networkPolicyConfig;
    }

    public void setNetworkPolicyConfig(GkeNetworkPolicyConfig networkPolicyConfig) {
        this.networkPolicyConfig = networkPolicyConfig;
    }

    public GkeCloudRunConfig getCloudRunConfig() {
        return cloudRunConfig;
    }

    public void setCloudRunConfig(GkeCloudRunConfig cloudRunConfig) {
        this.cloudRunConfig = cloudRunConfig;
    }

    public GkeDnsCacheConfig getDnsCacheConfig() {
        return dnsCacheConfig;
    }

    public void setDnsCacheConfig(GkeDnsCacheConfig dnsCacheConfig) {
        this.dnsCacheConfig = dnsCacheConfig;
    }

    public GkeConfigConnectorConfig getConfigConnectorConfig() {
        return configConnectorConfig;
    }

    public void setConfigConnectorConfig(GkeConfigConnectorConfig configConnectorConfig) {
        this.configConnectorConfig = configConnectorConfig;
    }

    @Override
    public String primaryKey() {
        return "";
    }

    @Override
    public void copyFrom(AddonsConfig model) throws Exception {
        if (model.hasHttpLoadBalancing()) {
            GkeHttpLoadBalancing config = newSubresource(GkeHttpLoadBalancing.class);
            config.copyFrom(model.getHttpLoadBalancing());
            setHttpLoadBalancing(config);
        }
        if (model.hasHorizontalPodAutoscaling()) {
            GkeHorizontalPodAutoscaling config = newSubresource(GkeHorizontalPodAutoscaling.class);
            config.copyFrom(model.getHorizontalPodAutoscaling());
            setHorizontalPodAutoscaling(config);
        }
        if (model.hasNetworkPolicyConfig()) {
            GkeNetworkPolicyConfig config = newSubresource(GkeNetworkPolicyConfig.class);
            config.copyFrom(model.getNetworkPolicyConfig());
            setNetworkPolicyConfig(config);
        }
        if (model.hasCloudRunConfig()) {
            GkeCloudRunConfig config = newSubresource(GkeCloudRunConfig.class);
            config.copyFrom(model.getCloudRunConfig());
            setCloudRunConfig(config);
        }
        if (model.hasDnsCacheConfig()) {
            GkeDnsCacheConfig config = newSubresource(GkeDnsCacheConfig.class);
            config.copyFrom(model.getDnsCacheConfig());
            setDnsCacheConfig(config);
        }
        if (model.hasConfigConnectorConfig()) {
            GkeConfigConnectorConfig config = newSubresource(GkeConfigConnectorConfig.class);
            config.copyFrom(model.getConfigConnectorConfig());
            setConfigConnectorConfig(config);
        }
    }

    AddonsConfig toAddonsConfig() {
        AddonsConfig.Builder builder = AddonsConfig.newBuilder();
        if (getHttpLoadBalancing() != null) {
            builder.setHttpLoadBalancing(getHttpLoadBalancing().toHttpLoadBalancing());
        }
        if (getHorizontalPodAutoscaling() != null) {
            builder.setHorizontalPodAutoscaling(getHorizontalPodAutoscaling().toHorizontalPodAutoscaling());
        }
        if (getNetworkPolicyConfig() != null) {
            builder.setNetworkPolicyConfig(getNetworkPolicyConfig().toNetworkPolicyConfig());
        }
        if (getCloudRunConfig() != null) {
            builder.setCloudRunConfig(getCloudRunConfig().toCloudRunConfig());
        }
        if (getDnsCacheConfig() != null) {
            builder.setDnsCacheConfig(getDnsCacheConfig().toDnsCacheConfig());
        }
        if (getConfigConnectorConfig() != null) {
            builder.setConfigConnectorConfig(getConfigConnectorConfig().toConfigConnectorConfig());
        }

        return builder.build();
    }
}
