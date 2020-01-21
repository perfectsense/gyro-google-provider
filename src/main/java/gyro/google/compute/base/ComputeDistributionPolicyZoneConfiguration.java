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

package gyro.google.compute.base;

import com.google.api.services.compute.model.DistributionPolicyZoneConfiguration;
import gyro.core.resource.Diffable;
import gyro.core.validation.Required;
import gyro.google.Copyable;

public class ComputeDistributionPolicyZoneConfiguration extends Diffable
    implements Copyable<DistributionPolicyZoneConfiguration> {

    private String zone;

    /**
     * The URL of the zone. The zone must exist in the region where the managed instance group is located.
     */
    @Required
    public String getZone() {
        return zone;
    }

    public void setZone(String zone) {
        this.zone = zone;
    }

    public DistributionPolicyZoneConfiguration copyTo() {
        DistributionPolicyZoneConfiguration distributionPolicyZoneConfiguration = new DistributionPolicyZoneConfiguration();
        distributionPolicyZoneConfiguration.setZone(getZone());
        return distributionPolicyZoneConfiguration;
    }

    @Override
    public void copyFrom(DistributionPolicyZoneConfiguration model) {
        setZone(model.getZone());
    }

    @Override
    public String primaryKey() {
        return getZone();
    }
}
