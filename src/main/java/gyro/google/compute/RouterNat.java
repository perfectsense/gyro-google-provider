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
import gyro.core.validation.Regex;
import gyro.core.validation.Required;
import gyro.core.validation.ValidStrings;
import gyro.core.validation.ValidationError;
import gyro.google.Copyable;

public class RouterNat extends Diffable implements Copyable<com.google.cloud.compute.v1.RouterNat> {

    private Integer icmpIdleTimeoutSec;
    private RouterNatLogConfig logConfig;
    private Integer minPortsPerVm;
    private String name;
    private String ipAllocationOption;
    private List<AddressResource> natIp;
    private String sourceSubnetworkIpRangesToNat;
    private List<RouterNatSubnetworkToNat> subnet;
    private Integer tcpEstablishedIdleTimeoutSec;
    private Integer tcpTransitoryIdleTimeoutSec;
    private Integer udpIdleTimeoutSec;

    /**
     * The timeout for ICMP connections. Defaults to ``30``.
     */
    @Updatable
    public Integer getIcmpIdleTimeoutSec() {
        return icmpIdleTimeoutSec;
    }

    public void setIcmpIdleTimeoutSec(Integer icmpIdleTimeoutSec) {
        this.icmpIdleTimeoutSec = icmpIdleTimeoutSec;
    }

    /**
     * Configuration options for logging on the NAT Gateway.
     *
     * @subresource gyro.google.compute.RouterNatLogConfig
     */
    @Updatable
    public RouterNatLogConfig getLogConfig() {
        return logConfig;
    }

    public void setLogConfig(RouterNatLogConfig logConfig) {
        this.logConfig = logConfig;
    }

    /**
     * The minimum number of ports allocated to a VM from this NAT config.
     */
    @Updatable
    public Integer getMinPortsPerVm() {
        return minPortsPerVm;
    }

    public void setMinPortsPerVm(Integer minPortsPerVm) {
        this.minPortsPerVm = minPortsPerVm;
    }

    /**
     * The name of the NAT gateway.
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
     * The NAT gateway IP allocation option.
     */
    @ValidStrings({ "MANUAL_ONLY", "AUTO_ONLY" })
    @Updatable
    public String getIpAllocationOption() {
        return ipAllocationOption;
    }

    public void setIpAllocationOption(String ipAllocationOption) {
        this.ipAllocationOption = ipAllocationOption;
    }

    /**
     * The list address resources used for this NAT service.
     */
    @Updatable
    public List<AddressResource> getNatIp() {
        if (natIp == null) {
            natIp = new ArrayList<>();
        }
        return natIp;
    }

    public void setNatIp(List<AddressResource> natIp) {
        this.natIp = natIp;
    }

    /**
     * The option for the type of subnet IP ranges.
     */
    @ValidStrings({
        "ALL_SUBNETWORKS_ALL_IP_RANGES",
        "ALL_SUBNETWORKS_ALL_PRIMARY_IP_RANGES",
        "LIST_OF_SUBNETWORKS",
        "SUBNETWORK_IP_RANGE_TO_NAT_OPTION_UNSPECIFIED" })
    @Updatable
    public String getSourceSubnetworkIpRangesToNat() {
        return sourceSubnetworkIpRangesToNat;
    }

    public void setSourceSubnetworkIpRangesToNat(String sourceSubnetworkIpRangesToNat) {
        this.sourceSubnetworkIpRangesToNat = sourceSubnetworkIpRangesToNat;
    }

    /**
     * The list of subnets whose traffic should be translated by the NAT gateway.
     *
     * @subresource gyro.google.compute.RouterNatSubnetworkToNat
     */
    @Updatable
    public List<RouterNatSubnetworkToNat> getSubnet() {
        if (subnet == null) {
            subnet = new ArrayList<>();
        }

        return subnet;
    }

    public void setSubnet(List<RouterNatSubnetworkToNat> subnet) {
        this.subnet = subnet;
    }

    /**
     * The timeout for TCP established connections. Defaults to ``1200``.
     */
    @Updatable
    public Integer getTcpEstablishedIdleTimeoutSec() {
        return tcpEstablishedIdleTimeoutSec;
    }

    public void setTcpEstablishedIdleTimeoutSec(Integer tcpEstablishedIdleTimeoutSec) {
        this.tcpEstablishedIdleTimeoutSec = tcpEstablishedIdleTimeoutSec;
    }

    /**
     * The timeout for TCP transitory connections. Defaults to ``30``.
     */
    @Updatable
    public Integer getTcpTransitoryIdleTimeoutSec() {
        return tcpTransitoryIdleTimeoutSec;
    }

    public void setTcpTransitoryIdleTimeoutSec(Integer tcpTransitoryIdleTimeoutSec) {
        this.tcpTransitoryIdleTimeoutSec = tcpTransitoryIdleTimeoutSec;
    }

    /**
     * The timeout for UDP connections. Defaults to ``30``.
     */
    @Updatable
    public Integer getUdpIdleTimeoutSec() {
        return udpIdleTimeoutSec;
    }

    public void setUdpIdleTimeoutSec(Integer udpIdleTimeoutSec) {
        this.udpIdleTimeoutSec = udpIdleTimeoutSec;
    }

    @Override
    public String primaryKey() {
        return getName();
    }

    @Override
    public void copyFrom(com.google.cloud.compute.v1.RouterNat model) throws Exception {
        setName(model.getName());

        if (model.hasIcmpIdleTimeoutSec()) {
            setIcmpIdleTimeoutSec(model.getIcmpIdleTimeoutSec());
        }

        if (model.hasMinPortsPerVm()) {
            setMinPortsPerVm(model.getMinPortsPerVm());
        }

        if (model.hasNatIpAllocateOption()) {
            setIpAllocationOption(model.getNatIpAllocateOption());
        }

        if (model.hasSourceSubnetworkIpRangesToNat()) {
            setSourceSubnetworkIpRangesToNat(model.getSourceSubnetworkIpRangesToNat());
        }

        if (model.hasTcpEstablishedIdleTimeoutSec()) {
            setTcpEstablishedIdleTimeoutSec(model.getTcpEstablishedIdleTimeoutSec());
        }

        if (model.hasTcpTransitoryIdleTimeoutSec()) {
            setTcpTransitoryIdleTimeoutSec(model.getTcpTransitoryIdleTimeoutSec());
        }

        if (model.hasUdpIdleTimeoutSec()) {
            setUdpIdleTimeoutSec(model.getUdpIdleTimeoutSec());
        }

        setLogConfig(null);
        if (model.hasLogConfig()) {
            RouterNatLogConfig natLogConfig = newSubresource(RouterNatLogConfig.class);
            natLogConfig.copyFrom(model.getLogConfig());

            setLogConfig(natLogConfig);
        }

        setNatIp(null);
        if (!model.getNatIpsList().isEmpty()) {
            setNatIp(model.getNatIpsList()
                .stream()
                .map(ip -> findById(AddressResource.class, ip))
                .collect(Collectors.toList()));
        }

        setSubnet(null);
        if (!model.getSubnetworksList().isEmpty()) {
            setSubnet(model.getSubnetworksList().stream().map(s -> {
                RouterNatSubnetworkToNat routerNatSubnetworkToNat = newSubresource(RouterNatSubnetworkToNat.class);
                routerNatSubnetworkToNat.copyFrom(s);
                return routerNatSubnetworkToNat;
            }).collect(Collectors.toList()));
        }
    }

    com.google.cloud.compute.v1.RouterNat toRouterNat() {
        com.google.cloud.compute.v1.RouterNat.Builder builder = com.google.cloud.compute.v1.RouterNat.newBuilder();
        builder.setName(getName());

        if (getIcmpIdleTimeoutSec() != null) {
            builder.setIcmpIdleTimeoutSec(getIcmpIdleTimeoutSec());
        }

        if (getMinPortsPerVm() != null) {
            builder.setMinPortsPerVm(getMinPortsPerVm());
        }

        if (getIpAllocationOption() != null) {
            builder.setNatIpAllocateOption(getIpAllocationOption());
        }

        if (getNatIp() != null) {
            builder.addAllNatIps(getNatIp().stream().map(AbstractAddressResource::getSelfLink)
                .collect(Collectors.toList()));
        }

        if (getSourceSubnetworkIpRangesToNat() != null) {
            builder.setSourceSubnetworkIpRangesToNat(getSourceSubnetworkIpRangesToNat());
        }

        if (getSubnet() != null && !getSubnet().isEmpty()) {
            builder.addAllSubnetworks(getSubnet().stream().map(RouterNatSubnetworkToNat::toRouterNatSubnetworkToNat)
                .collect(Collectors.toList()));
        }

        if (getTcpEstablishedIdleTimeoutSec() != null) {
            builder.setTcpEstablishedIdleTimeoutSec(getTcpEstablishedIdleTimeoutSec());
        }

        if (getTcpTransitoryIdleTimeoutSec() != null) {
            builder.setTcpTransitoryIdleTimeoutSec(getTcpTransitoryIdleTimeoutSec());
        }

        if (getUdpIdleTimeoutSec() != null) {
            builder.setUdpIdleTimeoutSec(getUdpIdleTimeoutSec());
        }

        if (getLogConfig() != null) {
            builder.setLogConfig(getLogConfig().toRouterNatLogConfig());
        }

        return builder.build();
    }

    @Override
    public List<ValidationError> validate(Set<String> configuredFields) {
        ArrayList<ValidationError> errors = new ArrayList<>();

        if (configuredFields.contains("subnets") && (getSourceSubnetworkIpRangesToNat() == null
            || !getSourceSubnetworkIpRangesToNat().contains("LIST_OF_SUBNETWORKS"))) {
            errors.add(new ValidationError(
                this,
                "subnets",
                "'subnets' can only be set if 'source-subnetwork-ip-ranges-to-nat' is set to 'LIST_OF_SUBNETWORKS'"));
        }

        return errors;
    }
}
