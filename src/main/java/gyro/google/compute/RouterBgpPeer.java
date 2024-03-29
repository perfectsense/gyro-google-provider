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
import gyro.core.validation.Regex;
import gyro.core.validation.Required;
import gyro.core.validation.ValidStrings;
import gyro.core.validation.ValidationError;
import gyro.google.Copyable;

public class RouterBgpPeer extends Diffable implements Copyable<com.google.cloud.compute.v1.RouterBgpPeer> {

    private String name;
    private String interfaceName;
    private String ipAddress;
    private String peerIpAddress;
    private Long peerAsn;
    private Long advertisedRoutePriority;
    private String advertiseMode;
    private List<String> advertisedGroups;
    private List<RouterIpRange> ipRange;

    /**
     * The name of the BGP peer.
     */
    @Required
    @Regex(value = "^[a-z]([-a-z0-9]*[a-z0-9])?$", message = "a string starting with a lowercase letter, followed by hyphens, lowercase letters, or digits, except the last character, which cannot be a hyphen.")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * The name of the interface the BGP peer is associated with.
     */
    @Updatable
    public String getInterfaceName() {
        return interfaceName;
    }

    public void setInterfaceName(String interfaceName) {
        this.interfaceName = interfaceName;
    }

    /**
     * The IP address of the interface.
     */
    @Updatable
    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    /**
     * The IP address of the peer BGP interface.
     */
    @Updatable
    public String getPeerIpAddress() {
        return peerIpAddress;
    }

    public void setPeerIpAddress(String peerIpAddress) {
        this.peerIpAddress = peerIpAddress;
    }

    /**
     * Peer BGP Autonomous System Number (ASN). Valid values belong in between ``64512`` to ``65534`` for a 16-bit ASN or between ``4200000000`` to ``4294967294`` for a 32-bit ASN.
     *
     * @no-doc Range, Ranges
     */
    @Required
    @Range(min = 64512, max = 65534)
    @Range(min = 4200000000L, max = 4294967294L)
    public Long getPeerAsn() {
        return peerAsn;
    }

    public void setPeerAsn(Long peerAsn) {
        this.peerAsn = peerAsn;
    }

    /**
     * The priority of routes advertised to this BGP peer.
     */
    @Updatable
    public Long getAdvertisedRoutePriority() {
        return advertisedRoutePriority;
    }

    public void setAdvertisedRoutePriority(Long advertisedRoutePriority) {
        this.advertisedRoutePriority = advertisedRoutePriority;
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
    @ValidStrings({ "ALL_SUBNETS", "ALL_VPC_SUBNETS", "ALL_PEER_VPC_SUBNETS" })
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
        return getName();
    }

    @Override
    public void copyFrom(com.google.cloud.compute.v1.RouterBgpPeer model) throws Exception {
        setName(model.getName());
        setAdvertisedGroups(model.getAdvertisedGroupsList());

        if (model.hasAdvertisedRoutePriority()) {
            setAdvertisedRoutePriority((long) model.getAdvertisedRoutePriority());
        }

        if (model.hasAdvertiseMode()) {
            setAdvertiseMode(model.getAdvertiseMode());
        }

        if (model.hasInterfaceName()) {
            setInterfaceName(model.getInterfaceName());
        }

        if (model.hasIpAddress()) {
            setIpAddress(model.getIpAddress());
        }

        if (model.hasPeerAsn()) {
            setPeerAsn((long) model.getPeerAsn());
        }

        if (model.hasPeerIpAddress()) {
            setPeerIpAddress(model.getPeerIpAddress());
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

    com.google.cloud.compute.v1.RouterBgpPeer toRouterBgpPeer() {
        com.google.cloud.compute.v1.RouterBgpPeer.Builder builder = com.google.cloud.compute.v1.RouterBgpPeer.newBuilder();
        builder.setName(getName());
        builder.setAdvertisedRoutePriority(getAdvertisedRoutePriority().intValue());
        builder.setPeerAsn(getPeerAsn().intValue());

        if (getAdvertiseMode() != null) {
            builder.setAdvertiseMode(getAdvertiseMode());
        }

        if (getInterfaceName() != null) {
            builder.setInterfaceName(getInterfaceName());
        }

        if (getIpAddress() != null) {
            builder.setIpAddress(getIpAddress());
        }

        if (getPeerIpAddress() != null) {
            builder.setPeerIpAddress(getPeerIpAddress());
        }

        if (getAdvertisedGroups() != null) {
            builder.addAllAdvertisedGroups(getAdvertisedGroups());
        }

        if (getIpRange() != null) {
            builder.addAllAdvertisedIpRanges(getIpRange().stream()
                .map(RouterIpRange::toRouterAdvertisedIpRange)
                .collect(Collectors.toList()));
        }

        return builder.build();
    }

    @Override
    public List<ValidationError> validate(Set<String> configuredFields) {
        List<ValidationError> errors = new ArrayList<>();

        if (!configuredFields.contains("interface-name") && !configuredFields.contains("ip-address")
            && !configuredFields.contains("peer-ip-address")) {
            errors.add(new ValidationError(
                this,
                null,
                "At least one of 'interface-name' or 'ip-address' or 'peer-ip-address' is required"));
        }

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
