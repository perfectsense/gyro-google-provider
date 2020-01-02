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

import com.google.api.services.compute.model.ForwardingRule;
import gyro.core.resource.Id;
import gyro.core.resource.Output;
import gyro.core.resource.Updatable;
import gyro.core.validation.Regex;
import gyro.core.validation.Required;
import gyro.core.validation.ValidStrings;
import gyro.google.Copyable;

public abstract class AbstractForwardingRuleResource extends ComputeResource implements Copyable<ForwardingRule> {

    private String ipAddress;
    private String ipProtocol;
    private Boolean allPorts;
    private String description;
    private String ipVersion;
    private String loadBalancingScheme;
    private String name;
    private String networkTier;
    private String portRange;
    private List<String> ports;
    private String selfLink;
    private String serviceLabel;
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
     * The IP protocol for the forwarding rule. Valid values are ``TCP``, ``UDP``, ``ESP``, ``AH``, ``SCTP`` or ``ICMP``.
     */
    @Required
    @ValidStrings({ "TCP", "UDP", "ESP", "AH", "SCTP", "ICMP" })
    public String getIpProtocol() {
        return ipProtocol;
    }

    public void setIpProtocol(String ipProtocol) {
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
    @Updatable
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * The IP Version that will be used by this forwarding rule. Valid values are ``IPV4`` or ``IPV6``.
     */
    public String getIpVersion() {
        return ipVersion;
    }

    public void setIpVersion(String ipVersion) {
        this.ipVersion = ipVersion;
    }

    /**
     * This signifies what the forwarding rule will be used for. Valid values are ``INTERNAL``, ``INTERNAL_SELF_MANAGED`` or ``EXTERNAL``.
     */
    @ValidStrings({ "INTERNAL", "INTERNAL_SELF_MANAGED", "EXTERNAL" })
    public String getLoadBalancingScheme() {
        return loadBalancingScheme;
    }

    public void setLoadBalancingScheme(String loadBalancingScheme) {
        this.loadBalancingScheme = loadBalancingScheme;
    }

    /**
     * The name of the forwarding proxy. Must be 1-63 characters, first character must be a lowercase letter and all following characters must be a dash, lowercase letter, or digit, except the last character, which cannot be a dash.
     */
    @Required
    @Regex("^[a-z]([-a-z0-9]{1,61}[a-z0-9])?")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * The networking tier used for configuring the load balancer with this forwarding rule. Valid values are ``PREMIUM`` or ``STANDARD``.
     */
    @ValidStrings({"PREMIUM", "STANDARD"})
    public String getNetworkTier() {
        return networkTier;
    }

    public void setNetworkTier(String networkTier) {
        this.networkTier = networkTier;
    }

    /**
     * This field is deprecated. See the port field.
     */
    public String getPortRange() {
        return portRange;
    }

    public void setPortRange(String portRange) {
        this.portRange = portRange;
    }

    /**
     * List of comma-separated ports that the forwarding rule forwards packets with matching destination ports.
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
     * A prefix to the service name for this forwarding rule. If specified, the prefix is the first label of the fully qualified service name. Must be 1-63 characters, first character must be a lowercase letter and all following characters must be a dash, lowercase letter, or digit, except the last character, which cannot be a dash.
     */
    @Regex("^[a-z]([-a-z0-9]{1,61}[a-z0-9])?")
    public String getServiceLabel() {
        return serviceLabel;
    }

    public void setServiceLabel(String serviceLabel) {
        this.serviceLabel = serviceLabel;
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
    public void copyFrom(ForwardingRule forwardingRule) {
        setIpAddress(forwardingRule.getIPAddress());
        setIpProtocol(forwardingRule.getIPProtocol());
        setAllPorts(forwardingRule.getAllPorts());
        setDescription(forwardingRule.getDescription());
        setIpProtocol(forwardingRule.getIPProtocol());
        setLoadBalancingScheme(forwardingRule.getLoadBalancingScheme());
        setName(forwardingRule.getName());
        setNetworkTier(forwardingRule.getNetworkTier());
        setPorts(forwardingRule.getPorts());
        setSelfLink(forwardingRule.getSelfLink());
        setServiceLabel(forwardingRule.getServiceLabel());
        setServiceName(forwardingRule.getServiceName());
        setIpVersion(forwardingRule.getIpVersion());
    }

    ForwardingRule toForwardingRule() {
        ForwardingRule forwardingRule = new ForwardingRule();
        forwardingRule.setIpVersion(getIpVersion());
        forwardingRule.setIPAddress(getIpAddress());
        forwardingRule.setIPProtocol(getIpProtocol());
        forwardingRule.setAllPorts(getAllPorts());
        forwardingRule.setDescription(getDescription());
        forwardingRule.setIPProtocol(getIpProtocol());
        forwardingRule.setLoadBalancingScheme(getLoadBalancingScheme());
        forwardingRule.setName(getName());
        forwardingRule.setNetworkTier(getNetworkTier());
        // This should be used even though the docs says it's deprecated as setting port is not working.
        forwardingRule.setPortRange(getPortRange());
        List<String> ports = getPorts();

        if (!ports.isEmpty()) {
            forwardingRule.setPorts(ports);
        }
        forwardingRule.setServiceLabel(getServiceLabel());

        return forwardingRule;
    }
}
