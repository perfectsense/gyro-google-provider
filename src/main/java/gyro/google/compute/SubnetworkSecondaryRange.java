/*
 * Copyright 2022, Brightspot.
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

import gyro.core.resource.Diffable;
import gyro.core.validation.Required;
import gyro.google.Copyable;

public class SubnetworkSecondaryRange extends Diffable implements Copyable<com.google.cloud.compute.v1.SubnetworkSecondaryRange> {

    private String ipCidrRange;
    private String name;

    /**
     * The cidr for the ip range.
     */
    @Required
    public String getIpCidrRange() {
        return ipCidrRange;
    }

    public void setIpCidrRange(String ipCidrRange) {
        this.ipCidrRange = ipCidrRange;
    }

    /**
     * The name for the ip range.
     */
    @Required
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String primaryKey() {
        return getName();
    }

    @Override
    public void copyFrom(com.google.cloud.compute.v1.SubnetworkSecondaryRange model) {
        setName(model.getRangeName());

        if (model.hasIpCidrRange()) {
            setIpCidrRange(model.getIpCidrRange());
        }
    }

    protected com.google.cloud.compute.v1.SubnetworkSecondaryRange toSecondaryIpRange() {
        return com.google.cloud.compute.v1.SubnetworkSecondaryRange.newBuilder()
                .setIpCidrRange(getIpCidrRange())
                .setRangeName(getName()).build();
    }
}
