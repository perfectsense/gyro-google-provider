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
import java.util.Set;

import gyro.core.resource.Diffable;
import gyro.core.resource.Updatable;
import gyro.core.validation.Required;
import gyro.core.validation.ValidStrings;
import gyro.core.validation.ValidationError;
import gyro.google.Copyable;

public class RouterNatSubnetworkToNat extends Diffable
    implements Copyable<com.google.api.services.compute.model.RouterNatSubnetworkToNat> {

    private SubnetworkResource subnet;
    private List<String> sourceIpRangesToNat;
    private List<String> secondaryIpRangeNames;

    /**
     * The subnet that will use this NAT gateway. (Required)
     */
    @Required
    @Updatable
    public SubnetworkResource getSubnet() {
        return subnet;
    }

    public void setSubnet(SubnetworkResource subnet) {
        this.subnet = subnet;
    }

    /**
     * The options to select which IP ranges of the subnet are allowed to the NAT gateway. Defaults ``ALL_IP_RANGES``.
     */
    @ValidStrings({
        "ALL_IP_RANGES",
        "PRIMARY_IP_RANGE",
        "LIST_OF_SECONDARY_IP_RANGES",
        "NAT_IP_RANGE_OPTION_UNSPECIFIED" })
    @Updatable
    public List<String> getSourceIpRangesToNat() {
        return sourceIpRangesToNat;
    }

    public void setSourceIpRangesToNat(List<String> sourceIpRangesToNat) {
        this.sourceIpRangesToNat = sourceIpRangesToNat;
    }

    /**
     * A list of secondary IP ranges of the subnet that are allowed to use the NAT gateway.
     */
    @Updatable
    public List<String> getSecondaryIpRangeNames() {
        return secondaryIpRangeNames;
    }

    public void setSecondaryIpRangeNames(List<String> secondaryIpRangeNames) {
        this.secondaryIpRangeNames = secondaryIpRangeNames;
    }

    @Override
    public String primaryKey() {
        return String.format("Subnet: %s", getSubnet().getSelfLink());
    }

    @Override
    public void copyFrom(com.google.api.services.compute.model.RouterNatSubnetworkToNat model) {
        setSubnet(findById(SubnetworkResource.class, model.getName()));
        setSourceIpRangesToNat(model.getSourceIpRangesToNat());
        setSecondaryIpRangeNames(model.getSecondaryIpRangeNames());
    }

    com.google.api.services.compute.model.RouterNatSubnetworkToNat toRouterNatSubnetworkToNat() {
        com.google.api.services.compute.model.RouterNatSubnetworkToNat routerNatSubnetworkToNat = new com.google.api.services.compute.model.RouterNatSubnetworkToNat();
        routerNatSubnetworkToNat.setName(subnet.getSelfLink());

        if (getSecondaryIpRangeNames() != null) {
            routerNatSubnetworkToNat.setSecondaryIpRangeNames(getSecondaryIpRangeNames());
        }

        if (getSourceIpRangesToNat() != null) {
            routerNatSubnetworkToNat.setSourceIpRangesToNat(getSourceIpRangesToNat());
        }

        return routerNatSubnetworkToNat;
    }

    @Override
    public List<ValidationError> validate(Set<String> configuredFields) {
        ArrayList<ValidationError> errors = new ArrayList<>();

        if (configuredFields.contains("secondary-ip-range-names") && (getSourceIpRangesToNat() == null
            || !getSourceIpRangesToNat().contains("LIST_OF_SECONDARY_IP_RANGES"))) {
            errors.add(new ValidationError(
                this,
                "secondary-ip-range-names",
                "'secondary-ip-range-names' can only be set if 'source-ip-ranges-to-nat' is set to 'LIST_OF_SECONDARY_IP_RANGES'"));
        }

        return errors;
    }
}
