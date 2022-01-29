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

import java.util.ArrayList;
import java.util.List;

import com.google.cloud.compute.v1.ForwardingRule;
import gyro.core.resource.Id;
import gyro.core.resource.Output;
import gyro.core.validation.Regex;
import gyro.core.validation.Required;
import gyro.core.validation.ValidStrings;
import gyro.google.Copyable;

public abstract class AbstractForwardingRuleResource extends ComputeResource implements Copyable<ForwardingRule> {

    private String ipAddress;
    private ForwardingRule.IPProtocolEnum ipProtocol;
    private Boolean allPorts;
    private String description;
    private ForwardingRule.IpVersion ipVersion;
    private ForwardingRule.LoadBalancingScheme loadBalancingScheme;
    private String name;
    private ForwardingRule.NetworkTier networkTier;
    private String portRange;
    private List<String> ports;
    private String serviceLabel;

    // Read-only
    private String selfLink;
    private String serviceName;

    /**
     * IP address that this forwarding rule serves.
     */
    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    /**
     * The IP protocol for the forwarding rule.
     */
    @Required
    @ValidStrings({ "TCP", "UDP", "ESP", "AH", "SCTP", "ICMP" })
    public ForwardingRule.IPProtocolEnum getIpProtocol() {
        return ipProtocol;
    }

    public void setIpProtocol(ForwardingRule.IPProtocolEnum ipProtocol) {
        this.ipProtocol = ipProtocol;
    }

    /**
     * Enabling this allows packets to be forwarded to the backends configured with this forwarding rule over all the configured ports.
     */
    public Boolean getAllPorts() {
        return allPorts;
    }

    public void setAllPorts(Boolean allPorts) {
        this.allPorts = allPorts;
    }

    /**
     * Description of the forwarding rule.
     */
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * The IP Version that will be used by this forwarding rule.
     */
    @ValidStrings({ "IPV4", "IPV6" })
    public ForwardingRule.IpVersion getIpVersion() {
        return ipVersion;
    }

    public void setIpVersion(ForwardingRule.IpVersion ipVersion) {
        this.ipVersion = ipVersion;
    }

    /**
     * This signifies what the forwarding rule will be used for.
     */
    @ValidStrings({ "INTERNAL", "INTERNAL_SELF_MANAGED", "EXTERNAL" })
    public ForwardingRule.LoadBalancingScheme getLoadBalancingScheme() {
        return loadBalancingScheme;
    }

    public void setLoadBalancingScheme(ForwardingRule.LoadBalancingScheme loadBalancingScheme) {
        this.loadBalancingScheme = loadBalancingScheme;
    }

    /**
     * The name of the forwarding rule.
     */
    @Required
    @Regex(value = "[a-z](?:[-a-z0-9]{0,61}[a-z0-9])?", message = "a string 1-63 characters long and match the regular expression `[a-z](?:[-a-z0-9]{0,61}[a-z0-9])?` which means the first character must be a lowercase letter, and all following characters must be a dash, lowercase letter, or digit, except the last character, which cannot be a dash")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * The networking tier used for configuring the load balancer with this forwarding rule.
     */
    @ValidStrings({ "PREMIUM", "STANDARD" })
    public ForwardingRule.NetworkTier getNetworkTier() {
        return networkTier;
    }

    public void setNetworkTier(ForwardingRule.NetworkTier networkTier) {
        this.networkTier = networkTier;
    }

    /**
     * The port or port numbers to be used for this forwarding rule.
     */
    public String getPortRange() {
        return portRange;
    }

    public void setPortRange(String portRange) {
        this.portRange = portRange;
    }

    /**
     * List of ports that the forwarding rule forwards packets with matching destination ports.
     */
    public List<String> getPorts() {
        if (ports == null) {
            ports = new ArrayList<>();
        }
        return ports;
    }

    public void setPorts(List<String> ports) {
        this.ports = ports;
    }

    /**
     * A prefix to the service name for this forwarding rule.
     */
    @Regex(value = "[a-z](?:[-a-z0-9]{0,61}[a-z0-9])?", message = "a string 1-63 characters long and match the regular expression `[a-z](?:[-a-z0-9]{0,61}[a-z0-9])?` which means the first character must be a lowercase letter, and all following characters must be a dash, lowercase letter, or digit, except the last character, which cannot be a dash")
    public String getServiceLabel() {
        return serviceLabel;
    }

    public void setServiceLabel(String serviceLabel) {
        this.serviceLabel = serviceLabel;
    }

    /**
     * Server-defined URL for the forwarding rule.
     */
    @Id
    @Output
    public String getSelfLink() {
        return selfLink;
    }

    public void setSelfLink(String selfLink) {
        this.selfLink = selfLink;
    }

    /**
     * The fully qualified service name for this forwarding rule.
     */
    @Output
    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    @Override
    public void copyFrom(ForwardingRule model) {
        setName(model.getName());

        if (model.hasDescription()) {
            setDescription(model.getDescription());
        }

        if (model.hasSelfLink()) {
            setSelfLink(model.getSelfLink());
        }

        if (model.hasIPAddress()) {
            setIpAddress(model.getIPAddress());
        }

        if (model.hasAllPorts()) {
            setAllPorts(model.getAllPorts());
        }

        if (model.hasPortRange()) {
            setPortRange(model.getPortRange());
        }

        if (model.hasServiceLabel()) {
            setServiceLabel(model.getServiceLabel());
        }

        if (model.hasServiceName()) {
            setServiceName(model.getServiceName());
        }

        if (model.hasIPProtocol()) {
            setIpProtocol(ForwardingRule.IPProtocolEnum.valueOf(model.getIPProtocol()));
        }

        if (model.hasIpVersion()) {
            setIpVersion(ForwardingRule.IpVersion.valueOf(model.getIpVersion()));
        }

        if (model.hasLoadBalancingScheme()) {
            setLoadBalancingScheme(ForwardingRule.LoadBalancingScheme.valueOf(model.getLoadBalancingScheme()));
        }

        if (model.hasNetworkTier()) {
            setNetworkTier(ForwardingRule.NetworkTier.valueOf(model.getNetworkTier()));
        }

        setPorts(model.getPortsList());
    }

    ForwardingRule toForwardingRule() {
        ForwardingRule.Builder forwardingRule = ForwardingRule.newBuilder();
        forwardingRule.setName(getName());
        forwardingRule.setAllPorts(Boolean.TRUE.equals(getAllPorts()));

        if (getDescription() != null) {
            forwardingRule.setDescription(getDescription());
        }

        // This should be used even though the docs says it's deprecated as setting port is not working.
        if (getPortRange() != null) {
            forwardingRule.setPortRange(getPortRange());
        }

        if (getIpVersion() != null) {
            forwardingRule.setIPProtocol(getIpProtocol().name());
        }

        if (getIpVersion() != null) {
            forwardingRule.setIpVersion(getIpVersion().name());
        }

        if (getNetworkTier() != null) {
            forwardingRule.setNetworkTier(getNetworkTier().name());
        }

        if (getLoadBalancingScheme() != null) {
            forwardingRule.setLoadBalancingScheme(getLoadBalancingScheme().name());
        }

        if (getIpAddress() != null) {
            forwardingRule.setIPAddress(getIpAddress());
        }

        if (getServiceLabel() != null) {
            forwardingRule.setServiceLabel(getServiceLabel());
        }

        List<String> ports = getPorts();
        if (!ports.isEmpty()) {
            forwardingRule.addAllPorts(ports);
        }

        return forwardingRule.build();
    }
}
