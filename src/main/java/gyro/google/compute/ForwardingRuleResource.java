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
import gyro.core.validation.Required;
import gyro.core.validation.ValidStrings;
import gyro.google.Copyable;

public abstract class ForwardingRuleResource extends ComputeResource implements Copyable<ForwardingRule> {

    private String ipAddress;

    private String ipProtocol;

    private Boolean allPorts;

    /**
     * This field is only used for INTERNAL load balancing.
     *
     * For internal load balancing, this field identifies the BackendService resource to receive the
     * matched traffic.
     *
     private String backendService;
     */
    private String description;

    private String ipVersion;

    private String loadBalancingScheme;

    /**
     * Opaque filter criteria used by Loadbalancer to restrict routing configuration to a limited set
     * xDS compliant clients. In their xDS requests to Loadbalancer, xDS clients present node
     * metadata. If a match takes place, the relevant routing configuration is made available to those
     * proxies. For each metadataFilter in this list, if its filterMatchCriteria is set to MATCH_ANY,
     * at least one of the filterLabels must match the corresponding label provided in the metadata.
     * If its filterMatchCriteria is set to MATCH_ALL, then all of its filterLabels must match with
     * corresponding labels in the provided metadata. metadataFilters specified here can be overridden
     * by those specified in the UrlMap that this ForwardingRule references. metadataFilters only
     * applies to Loadbalancers that have their loadBalancingScheme set to INTERNAL_SELF_MANAGED.
     *
     private List<MetadataFilter> metadataFilters;
     */
    private String name;

    /**
     * This field is not used for external load balancing.
     *
     * For INTERNAL and INTERNAL_SELF_MANAGED load balancing, this field identifies the network that
     * the load balanced IP should belong to for this Forwarding Rule. If this field is not specified,
     * the default network will be used.
     *
     private String network;
     */
    private String networkTier;

    // TODO: provider better UI.
    private String portRange;

    private List<String> ports;

    private String selfLink;

    private String serviceLabel;

    private String serviceName;

    /**
     * This field is only used for INTERNAL load balancing.
     *
     * For internal load balancing, this field identifies the subnetwork that the load balanced IP
     * should belong to for this Forwarding Rule.
     *
     * If the network specified is in auto subnet mode, this field is optional. However, if the
     * network is in custom subnet mode, a subnetwork must be specified.
     *
     private String subnetwork;
     */
    /**
     * The URL of the target resource to receive the matched traffic. For regional forwarding rules,
     * this target must live in the same region as the forwarding rule. For global forwarding rules,
     * this target must be a global load balancing resource. The forwarded traffic must be of a type
     * appropriate to the target object. For INTERNAL_SELF_MANAGED load balancing, only HTTP and HTTPS
     * targets are valid.
     */
    // TODO: target can be any target resources.
    //    private String target;
    protected abstract void doCopyFrom(ForwardingRule model);

    /**
     * IP address that this forwarding rule serves. When a client sends traffic to this IP address,
     * the forwarding rule directs the traffic to the target that you specify in the forwarding rule.
     *
     * If you don't specify a reserved IP address, an ephemeral IP address is assigned. Methods for
     * specifying an IP address:
     *
     * * IPv4 dotted decimal, as in `100.1.2.3` * Full URL, as in
     * https://www.googleapis.com/compute/v1/projects/project_id/regions/region/addresses/address-name
     * * Partial URL or by name, as in: * projects/project_id/regions/region/addresses/address-name *
     * regions/region/addresses/address-name * global/addresses/address-name * address-name
     *
     * The loadBalancingScheme and the forwarding rule's target determine the type of IP address that
     * you can use. For detailed information, refer to [IP address specifications](/load-
     * balancing/docs/forwarding-rule-concepts#ip_address_specifications).
     */
    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    /**
     * The IP protocol to which this rule applies. Valid options are TCP, UDP, ESP, AH, SCTP or ICMP.
     *
     * When the load balancing scheme is INTERNAL, only TCP and UDP are valid. When the load balancing
     * scheme is INTERNAL_SELF_MANAGED, only TCPis valid.
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
     * This field is used along with the backend_service field for internal load balancing or with the
     * target field for internal TargetInstance. This field cannot be used with port or portRange
     * fields.
     *
     * When the load balancing scheme is INTERNAL and protocol is TCP/UDP, specify this field to allow
     * packets addressed to any ports will be forwarded to the backends configured with this
     * forwarding rule.
     */
    public Boolean getAllPorts() {
        return allPorts;
    }

    public void setAllPorts(Boolean allPorts) {
        this.allPorts = allPorts;
    }

    /**
     * An optional description of this resource. Provide this property when you create the resource.
     */
    @Updatable
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * The IP Version that will be used by this forwarding rule. Valid options are IPV4 or IPV6. This
     * can only be specified for an external global forwarding rule.
     */
    public String getIpVersion() {
        return ipVersion;
    }

    public void setIpVersion(String ipVersion) {
        this.ipVersion = ipVersion;
    }

    /**
     * This signifies what the ForwardingRule will be used for and can only take the following values:
     * INTERNAL, INTERNAL_SELF_MANAGED, EXTERNAL. The value of INTERNAL means that this will be used
     * for Internal Network Load Balancing (TCP, UDP). The value of INTERNAL_SELF_MANAGED means that
     * this will be used for Internal Global HTTP(S) LB. The value of EXTERNAL means that this will be
     * used for External Load Balancing (HTTP(S) LB, External TCP/UDP LB, SSL Proxy)
     */
    @ValidStrings({ "INTERNAL", "INTERNAL_SELF_MANAGED", "EXTERNAL" })
    public String getLoadBalancingScheme() {
        return loadBalancingScheme;
    }

    public void setLoadBalancingScheme(String loadBalancingScheme) {
        this.loadBalancingScheme = loadBalancingScheme;
    }

    /**
     * Name of the resource; provided by the client when the resource is created. The name must be
     * 1-63 characters long, and comply with RFC1035. Specifically, the name must be 1-63 characters
     * long and match the regular expression `[a-z]([-a-z0-9]*[a-z0-9])?` which means the first
     * character must be a lowercase letter, and all following characters must be a dash, lowercase
     * letter, or digit, except the last character, which cannot be a dash.
     */
    @Required
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * This signifies the networking tier used for configuring this load balancer and can only take
     * the following values: PREMIUM , STANDARD.
     *
     * For regional ForwardingRule, the valid values are PREMIUM and STANDARD. For
     * GlobalForwardingRule, the valid value is PREMIUM.
     *
     * If this field is not specified, it is assumed to be PREMIUM. If IPAddress is specified, this
     * value must be equal to the networkTier of the Address.
     */
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
     * List of comma-separated ports. The forwarding rule forwards packets with matching destination
     * ports. If the forwarding rule's loadBalancingScheme is EXTERNAL, and the forwarding rule
     * references a target pool, specifying ports is optional. You can specify an unlimited number of
     * ports, but they must be contiguous. If you omit ports, GCP forwards traffic on any port of the
     * forwarding rule's protocol.
     *
     * If the forwarding rule's loadBalancingScheme is EXTERNAL, and the forwarding rule references a
     * target HTTP proxy, target HTTPS proxy, target TCP proxy, target SSL proxy, or target VPN
     * gateway, you must specify ports using the following constraints:
     *
     *   - TargetHttpProxy: 80, 8080  - TargetHttpsProxy: 443  - TargetTcpProxy: 25, 43, 110, 143,
     * 195, 443, 465, 587, 700, 993, 995, 1688, 1883, 5222  - TargetSslProxy: 25, 43, 110, 143, 195,
     * 443, 465, 587, 700, 993, 995, 1688, 1883, 5222  - TargetVpnGateway: 500, 4500
     *
     * If the forwarding rule's loadBalancingScheme is INTERNAL, you must specify ports in one of the
     * following ways:
     *
     * * A list of up to five ports, which can be non-contiguous * Keyword ALL, which causes the
     * forwarding rule to forward traffic on any port of the forwarding rule's protocol.
     *
     * The ports field is used along with the target field for TargetHttpProxy, TargetHttpsProxy,
     * TargetSslProxy, TargetTcpProxy, TargetVpnGateway, TargetPool, TargetInstance.
     *
     * Applicable only when IPProtocol is TCP, UDP, or SCTP. Forwarding rules with the same
     * [IPAddress, IPProtocol] pair must have disjoint port ranges.
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
     * Server-defined URL for the resource.
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
     * An optional prefix to the service name for this Forwarding Rule. If specified, the prefix is
     * the first label of the fully qualified service name.
     *
     * The label must be 1-63 characters long, and comply with RFC1035. Specifically, the label must
     * be 1-63 characters long and match the regular expression `[a-z]([-a-z0-9]*[a-z0-9])?` which
     * means the first character must be a lowercase letter, and all following characters must be a
     * dash, lowercase letter, or digit, except the last character, which cannot be a dash.
     *
     * This field is only used for internal load balancing.
     */
    public String getServiceLabel() {
        return serviceLabel;
    }

    public void setServiceLabel(String serviceLabel) {
        this.serviceLabel = serviceLabel;
    }

    /**
     * The internal fully qualified service name for this Forwarding Rule.
     *
     * This field is only used for internal load balancing.
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
        setIpAddress(model.getIPAddress());
        setIpProtocol(model.getIPProtocol());
        setAllPorts(model.getAllPorts());
        setDescription(model.getDescription());
        setIpProtocol(model.getIPProtocol());
        setLoadBalancingScheme(model.getLoadBalancingScheme());
        setName(model.getName());
        setNetworkTier(model.getNetworkTier());
        setPorts(model.getPorts());
        setSelfLink(model.getSelfLink());
        setServiceLabel(model.getServiceLabel());
        setServiceName(model.getServiceName());

        doCopyFrom(model);
    }
}
