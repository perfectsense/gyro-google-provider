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

import com.google.container.v1.ResourceUsageExportConfig;
import gyro.core.resource.Diffable;
import gyro.google.Copyable;

public class GkeResourceUsageExportConfig extends Diffable implements Copyable<ResourceUsageExportConfig> {

    private Boolean enableNetworkEgressMetering;
    private GkeBigQueryDestination bigqueryDestination;
    private GkeConsumptionMeteringConfig consumptionMeteringConfig;

    public Boolean getEnableNetworkEgressMetering() {
        return enableNetworkEgressMetering;
    }

    public void setEnableNetworkEgressMetering(Boolean enableNetworkEgressMetering) {
        this.enableNetworkEgressMetering = enableNetworkEgressMetering;
    }

    public GkeBigQueryDestination getBigqueryDestination() {
        return bigqueryDestination;
    }

    public void setBigqueryDestination(GkeBigQueryDestination bigqueryDestination) {
        this.bigqueryDestination = bigqueryDestination;
    }

    public GkeConsumptionMeteringConfig getConsumptionMeteringConfig() {
        return consumptionMeteringConfig;
    }

    public void setConsumptionMeteringConfig(GkeConsumptionMeteringConfig consumptionMeteringConfig) {
        this.consumptionMeteringConfig = consumptionMeteringConfig;
    }

    @Override
    public String primaryKey() {
        return "";
    }

    @Override
    public void copyFrom(ResourceUsageExportConfig model) throws Exception {
        setEnableNetworkEgressMetering(model.getEnableNetworkEgressMetering());

        if (model.hasBigqueryDestination()) {
            GkeBigQueryDestination destination = newSubresource(GkeBigQueryDestination.class);
            destination.copyFrom(model.getBigqueryDestination());
            setBigqueryDestination(destination);
        }

        if (model.hasConsumptionMeteringConfig()) {
            GkeConsumptionMeteringConfig config = newSubresource(GkeConsumptionMeteringConfig.class);
            config.copyFrom(model.getConsumptionMeteringConfig());
            setConsumptionMeteringConfig(config);
        }
    }

    ResourceUsageExportConfig toResourceUsageExportConfig() {
        ResourceUsageExportConfig.Builder builder = ResourceUsageExportConfig.newBuilder();
        if (getEnableNetworkEgressMetering()) {
            builder.setEnableNetworkEgressMetering(getEnableNetworkEgressMetering());
        }

        if (getBigqueryDestination() != null) {
            builder.setBigqueryDestination(getBigqueryDestination().toBigQueryDestination());
        }

        if (getConsumptionMeteringConfig() != null) {
            builder.setConsumptionMeteringConfig(getConsumptionMeteringConfig().toConsumptionMeteringConfig());
        }

        return builder.build();
    }
}
