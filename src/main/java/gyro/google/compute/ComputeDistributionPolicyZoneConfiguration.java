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

import com.google.cloud.compute.v1.DistributionPolicyZoneConfiguration;
import gyro.core.resource.Diffable;
import gyro.core.resource.Immutable;
import gyro.core.validation.Required;
import gyro.google.Copyable;
import gyro.google.util.Utils;

public class ComputeDistributionPolicyZoneConfiguration extends Diffable
    implements Copyable<DistributionPolicyZoneConfiguration> {

    private String zoneLink;

    /**
     * The URL of the zone. The zone must exist in the region where the managed instance group is located.
     */
    @Immutable
    @Required
    public String getZoneLink() {
        return zoneLink;
    }

    public void setZoneLink(String zoneLink) {
        this.zoneLink = zoneLink;
    }

    public DistributionPolicyZoneConfiguration copyTo() {
        DistributionPolicyZoneConfiguration.Builder builder = DistributionPolicyZoneConfiguration.newBuilder();
        if (getZoneLink() != null) {
            builder.setZone(getZoneLink());
        }

        return builder.build();
    }

    @Override
    public void copyFrom(DistributionPolicyZoneConfiguration model) {
        if (model.hasZone()) {
            setZoneLink(model.getZone());
        }
    }

    @Override
    public String primaryKey() {
        return Utils.extractName(getZoneLink());
    }
}
