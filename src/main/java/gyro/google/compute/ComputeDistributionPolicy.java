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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.google.cloud.compute.v1.DistributionPolicy;
import com.google.cloud.compute.v1.DistributionPolicyZoneConfiguration;
import gyro.core.resource.Diffable;
import gyro.core.validation.Required;
import gyro.google.Copyable;

public class ComputeDistributionPolicy extends Diffable implements Copyable<DistributionPolicy> {

    private List<ComputeDistributionPolicyZoneConfiguration> zone;

    /**
     * Zones where the regional managed instance group will create and manage instances.
     *
     * @subresource gyro.google.compute.ComputeDistributionPolicyZoneConfiguration
     */
    @Required
    public List<ComputeDistributionPolicyZoneConfiguration> getZone() {
        if (zone == null) {
            zone = new ArrayList<>();
        }

        return zone;
    }

    public void setZone(List<ComputeDistributionPolicyZoneConfiguration> zone) {
        this.zone = zone;
    }

    public DistributionPolicy copyTo() {
        DistributionPolicy.Builder builder = DistributionPolicy.newBuilder();
        List<ComputeDistributionPolicyZoneConfiguration> zone = getZone();

        if (!zone.isEmpty()) {
            builder.addAllZones(zone.stream()
                .map(ComputeDistributionPolicyZoneConfiguration::copyTo)
                .collect(Collectors.toList()));
        }

        return builder.build();
    }

    @Override
    public void copyFrom(DistributionPolicy model) {
        List<ComputeDistributionPolicyZoneConfiguration> diffableZone = null;
        List<DistributionPolicyZoneConfiguration> zones = model.getZonesList();

        if (zones != null && !zones.isEmpty()) {
            diffableZone = zones
                .stream()
                .map(zone -> {
                    ComputeDistributionPolicyZoneConfiguration computeDistributionPolicyZoneConfiguration = newSubresource(
                        ComputeDistributionPolicyZoneConfiguration.class);
                    computeDistributionPolicyZoneConfiguration.copyFrom(zone);

                    return computeDistributionPolicyZoneConfiguration;
                }).collect(Collectors.toList());
        }

        setZone(diffableZone);
    }

    @Override
    public String primaryKey() {
        return "";
    }
}
