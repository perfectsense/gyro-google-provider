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
import java.util.stream.Collectors;

import gyro.core.resource.Diffable;
import gyro.core.resource.Updatable;
import gyro.core.validation.Range;
import gyro.core.validation.Required;
import gyro.core.validation.ValidStrings;
import gyro.core.validation.ValidationError;
import gyro.google.Copyable;

public class RouterBgp extends Diffable implements Copyable<com.google.cloud.compute.v1.RouterBgp> {

    private Long asn;
    private String advertiseMode;
    private List<String> advertisedGroups;
    private List<RouterIpRange> ipRange;

    /**
     * Local BGP Autonomous System Number (ASN). Valid values belong in between ``64512`` to ``65534`` for a 16-bit ASN or between ``4200000000`` to ``4294967294`` for a 32-bit ASN.
     *
     * @no-doc Range, Ranges
     */
    @Required
    @Updatable
    @Range(min = 64512, max = 65534)
    @Range(min = 4200000000L, max = 4294967294L)
    public Long getAsn() {
        return asn;
    }

    public void setAsn(Long asn) {
        this.asn = asn;
    }

    /**
     * The mode to use for advertisement.
     */
    @ValidStrings({ "DEFAULT", "CUSTOM" })
    @Updatable
    public String getAdvertiseMode() {
        return advertiseMode;
    }

    public void setAdvertiseMode(String advertiseMode) {
        this.advertiseMode = advertiseMode;
    }

    /**
     * The list of prefix groups when ``advertise-mode`` is set to ``CUSTOM``.
     */
    @ValidStrings("ALL_SUBNETS")
    @Updatable
    public List<String> getAdvertisedGroups() {
        if (advertisedGroups == null) {
            advertisedGroups = new ArrayList<>();
        }

        return advertisedGroups;
    }

    public void setAdvertisedGroups(List<String> advertisedGroups) {
        this.advertisedGroups = advertisedGroups;
    }

    /**
     * The list of individual IP ranges when ``advertise-mode`` is set to ``CUSTOM``.
     *
     * @subresource gyro.google.compute.RouterIpRange
     */
    @Updatable
    public List<RouterIpRange> getIpRange() {
        if (ipRange == null) {
            ipRange = new ArrayList<>();
        }

        return ipRange;
    }

    public void setIpRange(List<RouterIpRange> ipRange) {
        this.ipRange = ipRange;
    }

    @Override
    public String primaryKey() {
        return getAsn().toString();
    }

    @Override
    public void copyFrom(com.google.cloud.compute.v1.RouterBgp model) {
        setAdvertisedGroups(model.getAdvertisedGroupsList());

        if (model.hasAsn()) {
            setAsn((long) model.getAsn());
        }

        if (model.hasAdvertiseMode()) {
            setAdvertiseMode(model.getAdvertiseMode());
        }

        setIpRange(null);
        if (!model.getAdvertisedIpRangesList().isEmpty()) {
            setIpRange(model.getAdvertisedIpRangesList().stream().map(i -> {
                RouterIpRange routerIpRange = newSubresource(RouterIpRange.class);
                routerIpRange.copyFrom(i);

                return routerIpRange;
            }).collect(Collectors.toList()));
        }
    }

    com.google.cloud.compute.v1.RouterBgp toRouterBgp() {
        com.google.cloud.compute.v1.RouterBgp.Builder builder = com.google.cloud.compute.v1.RouterBgp.newBuilder();

        if (getAsn() != null) {
            builder.setAsn(getAsn().intValue());
        }

        if (getAdvertiseMode() != null) {
            builder.setAdvertiseMode(getAdvertiseMode());
        }

        if (getAdvertisedGroups() != null) {
            builder.addAllAdvertisedGroups(getAdvertisedGroups());
        }

        if (getIpRange() != null) {
            builder.addAllAdvertisedIpRanges(getIpRange().stream()
                .map(RouterIpRange::toRouterAdvertisedIpRange).collect(Collectors.toList()));
        }

        return builder.build();
    }

    @Override
    public List<ValidationError> validate(Set<String> configuredFields) {
        List<ValidationError> errors = new ArrayList<>();

        if (getAdvertiseMode() != null && !getAdvertiseMode().equals("CUSTOM") && (!getIpRange().isEmpty()
            || !getAdvertisedGroups().isEmpty())) {
            errors.add(new ValidationError(
                this,
                null,
                "'ip-ranges' and 'advertised-groups' can only be set if 'advertise-mode' is set to 'CUSTOM'"));
        }

        return errors;
    }
}
