/*
 * Copyright 2019, Perfect Sense, Inc.
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

import com.google.cloud.compute.v1.AliasIpRange;
import gyro.core.resource.Diffable;
import gyro.google.Copyable;

public class InstanceAliasIpRange extends Diffable implements Copyable<AliasIpRange> {

    private String ipCidrRange;
    private String subnetworkRangeName;

    /**
     * The IP alias ranges to allocate for this interface. This IP CIDR range must belong to the specified subnetwork and cannot contain IP addresses reserved by system or used by other network interfaces. This range may be a single IP address (e.g. 10.2.3.4), a netmask (e.g. /24) or a CIDR-formatted string (e.g. 10.1.2.0/24).
     */
    public String getIpCidrRange() {
        return ipCidrRange;
    }

    public void setIpCidrRange(String ipCidrRange) {
        this.ipCidrRange = ipCidrRange;
    }

    /**
     * Name given to a subnetwork secondary IP range for use in allocating the IP alias range. Unspecified the primary range of the subnetwork is used.
     */
    public String getSubnetworkRangeName() {
        return subnetworkRangeName;
    }

    public void setSubnetworkRangeName(String subnetworkRangeName) {
        this.subnetworkRangeName = subnetworkRangeName;
    }

    @Override
    public String primaryKey() {
        return "";
    }

    @Override
    public void copyFrom(AliasIpRange model) {
        setIpCidrRange(model.getIpCidrRange());
        setSubnetworkRangeName(model.getSubnetworkRangeName());
    }

    public AliasIpRange copyTo() {
        return new AliasIpRange()
            .setIpCidrRange(getIpCidrRange())
            .setSubnetworkRangeName(getSubnetworkRangeName());
    }
}
