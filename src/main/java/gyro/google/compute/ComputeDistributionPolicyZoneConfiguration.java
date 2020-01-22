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

import com.google.api.services.compute.model.DistributionPolicyZoneConfiguration;
import gyro.core.resource.Diffable;
import gyro.core.resource.Output;
import gyro.core.validation.Required;
import gyro.google.Copyable;

public class ComputeDistributionPolicyZoneConfiguration extends Diffable
    implements Copyable<DistributionPolicyZoneConfiguration> {

    private String zoneOutput;

    private String zoneUrl;

    /**
     * The full URL of the zone.
     */
    @Output
    public String getZoneOutput() {
        return zoneOutput;
    }

    public void setZoneOutput(String zoneOutput) {
        this.zoneOutput = zoneOutput;
    }

    /**
     * The URL of the zone. The zone must exist in the region where the managed instance group is located.
     */
    @Required
    public String getZoneUrl() {
        return zoneUrl;
    }

    public void setZoneUrl(String zoneUrl) {
        this.zoneUrl = zoneUrl;
    }

    public DistributionPolicyZoneConfiguration copyTo() {
        DistributionPolicyZoneConfiguration distributionPolicyZoneConfiguration = new DistributionPolicyZoneConfiguration();
        distributionPolicyZoneConfiguration.setZone(getZoneUrl());
        return distributionPolicyZoneConfiguration;
    }

    @Override
    public void copyFrom(DistributionPolicyZoneConfiguration model) {
        setZoneOutput(model.getZone());
    }

    @Override
    public String primaryKey() {
        String primaryKey = getZoneUrl();

        if (primaryKey == null) {
            return getZoneOutput();
        }
        return primaryKey;
    }
}
