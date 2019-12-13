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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import com.google.api.services.compute.model.Backend;
import com.google.api.services.compute.model.BackendService;
import com.google.api.services.compute.model.ConnectionDraining;
import gyro.core.resource.Id;
import gyro.core.resource.Output;
import gyro.core.resource.Updatable;
import gyro.core.validation.Required;
import gyro.google.Copyable;

public abstract class BackendServiceResource extends ComputeResource implements Copyable<BackendService> {

    private Integer affinityCookieTtlSec;

    private List<ComputeBackend> backend;

    /**
     * Cloud CDN configuration for this BackendService.
     *
     private BackendServiceCdnPolicy cdnPolicy;
     */
    /**
     * Settings controlling the volume of connections to a backend service.
     *
     * This field is applicable to either:   - A regional backend service with the service_protocol
     * set to HTTP, HTTPS, or HTTP2, and load_balancing_scheme set to INTERNAL_MANAGED.  - A global
     * backend service with the load_balancing_scheme set to INTERNAL_SELF_MANAGED.
     *
     private CircuitBreakers circuitBreakers;
     */
    private ComputeConnectionDraining connectionDraining;

    /**
     * Consistent Hash-based load balancing can be used to provide soft session affinity based on HTTP
     * headers, cookies or other properties. This load balancing policy is applicable only for HTTP
     * connections. The affinity to a particular destination host will be lost when one or more hosts
     * are added/removed from the destination service. This field specifies parameters that control
     * consistent hashing. This field is only applicable when localityLbPolicy is set to MAGLEV or
     * RING_HASH.
     *
     * This field is applicable to either:   - A regional backend service with the service_protocol
     * set to HTTP, HTTPS, or HTTP2, and load_balancing_scheme set to INTERNAL_MANAGED.  - A global
     * backend service with the load_balancing_scheme set to INTERNAL_SELF_MANAGED.
     *
     private ConsistentHashLoadBalancerSettings consistentHash;
     */
    private Map<String, String> customRequestHeaders;

    private String description;

    private Boolean enableCDN;

    private String fingerprint;

    private List<HealthCheckResource> healthCheck;

    /**
     *
     private BackendServiceIAP iap;
     */
    private String loadBalancingScheme;

    private String localityLbPolicy;

    private String name;

    /**
     * Settings controlling eviction of unhealthy hosts from the load balancing pool. This field is
     * applicable to either: - A regional backend service with the service_protocol set to HTTP,
     * HTTPS, or HTTP2, and load_balancing_scheme set to INTERNAL_MANAGED.  - A global backend service
     * with the load_balancing_scheme set to INTERNAL_SELF_MANAGED.
     *
     private OutlierDetection outlierDetection;
     */
    //    private Integer port;

    private String portName;

    private String protocol;

    private String region;

    private String securityPolicy;

    private String selfLink;

    private String sessionAffinity;

    private Integer timeoutSec;

    /**
     * If set to 0, the cookie is non-persistent and lasts only until the end of the browser session
     * (or equivalent). The maximum allowed value is one day (86,400).
     */
    public Integer getAffinityCookieTtlSec() {
        return affinityCookieTtlSec;
    }

    public void setAffinityCookieTtlSec(Integer affinityCookieTtlSec) {
        this.affinityCookieTtlSec = affinityCookieTtlSec;
    }

    /**
     * The list of backends that serve this BackendService.
     */
    @Required
    public List<ComputeBackend> getBackend() {
        if (backend == null) {
            backend = new ArrayList<>();
        }
        return backend;
    }

    public void setBackend(List<ComputeBackend> backend) {
        this.backend = backend;
    }

    /**
     */
    public ComputeConnectionDraining getConnectionDraining() {
        return connectionDraining;
    }

    public void setConnectionDraining(ComputeConnectionDraining connectionDraining) {
        this.connectionDraining = connectionDraining;
    }

    /**
     * Headers that the HTTP/S load balancer should add to proxied requests.
     */
    public Map<String, String> getCustomRequestHeaders() {
        if (customRequestHeaders == null) {
            customRequestHeaders = new HashMap();
        }
        return customRequestHeaders;
    }

    public void setCustomRequestHeaders(Map<String, String> customRequestHeaders) {
        this.customRequestHeaders = customRequestHeaders;
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
     * If true, enables Cloud CDN for the backend service. Only applicable if the loadBalancingScheme
     * is EXTERNAL and the protocol is HTTP or HTTPS.
     */
    public Boolean getEnableCDN() {
        return enableCDN;
    }

    public void setEnableCDN(Boolean enableCDN) {
        this.enableCDN = enableCDN;
    }

    /**
     * Fingerprint of this resource. A hash of the contents stored in this object. This field is used
     * in optimistic locking. This field will be ignored when inserting a BackendService. An up-to-
     * date fingerprint must be provided in order to update the BackendService, otherwise the request
     * will fail with error 412 conditionNotMet.
     *
     * To see the latest fingerprint, make a get() request to retrieve a BackendService.
     */
    public String getFingerprint() {
        return fingerprint;
    }

    public void setFingerprint(String fingerprint) {
        this.fingerprint = fingerprint;
    }

    /**
     * The list of URLs to the HttpHealthCheck or HttpsHealthCheck resource for health checking this
     * BackendService. Currently at most one health check can be specified, and a health check is
     * required for Compute Engine backend services. A health check must not be specified for App
     * Engine backend and Cloud Function backend.
     *
     * For internal load balancing, a URL to a HealthCheck resource must be specified instead.
     */
    @Required
    public List<HealthCheckResource> getHealthCheck() {
        if (healthCheck == null) {
            healthCheck = new ArrayList<>();
        }
        return healthCheck;
    }

    public void setHealthCheck(List<HealthCheckResource> healthCheck) {
        this.healthCheck = healthCheck;
    }

    /**
     * Indicates whether the backend service will be used with internal or external load balancing. A
     * backend service created for one type of load balancing cannot be used with the other. Possible
     * values are INTERNAL and EXTERNAL.
     */
    public String getLoadBalancingScheme() {
        return loadBalancingScheme;
    }

    public void setLoadBalancingScheme(String loadBalancingScheme) {
        this.loadBalancingScheme = loadBalancingScheme;
    }

    /**
     * The load balancing algorithm used within the scope of the locality. The possible values are: -
     * ROUND_ROBIN: This is a simple policy in which each healthy backend is selected in round robin
     * order. This is the default.  - LEAST_REQUEST: An O(1) algorithm which selects two random
     * healthy hosts and picks the host which has fewer active requests.  - RING_HASH: The ring/modulo
     * hash load balancer implements consistent hashing to backends. The algorithm has the property
     * that the addition/removal of a host from a set of N hosts only affects 1/N of the requests.  -
     * RANDOM: The load balancer selects a random healthy host.  - ORIGINAL_DESTINATION: Backend host
     * is selected based on the client connection metadata, i.e., connections are opened to the same
     * address as the destination address of the incoming connection before the connection was
     * redirected to the load balancer.  - MAGLEV: used as a drop in replacement for the ring hash
     * load balancer. Maglev is not as stable as ring hash but has faster table lookup build times and
     * host selection times. For more information about Maglev, refer to
     * https://ai.google/research/pubs/pub44824
     *
     * This field is applicable to either:   - A regional backend service with the service_protocol
     * set to HTTP, HTTPS, or HTTP2, and load_balancing_scheme set to INTERNAL_MANAGED.  - A global
     * backend service with the load_balancing_scheme set to INTERNAL_SELF_MANAGED.
     */
    public String getLocalityLbPolicy() {
        return localityLbPolicy;
    }

    public void setLocalityLbPolicy(String localityLbPolicy) {
        this.localityLbPolicy = localityLbPolicy;
    }

    /**
     * Name of the resource. Provided by the client when the resource is created. The name must be
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
     * Deprecated in favor of portName. The TCP port to connect on the backend. The default value is
     * 80.
     *
     * This cannot be used if the loadBalancingScheme is INTERNAL (Internal TCP/UDP Load Balancing).
     */
    //    public Integer getPort() {
    //        return port;
    //    }
    //
    //    public void setPort(Integer port) {
    //        this.port = port;
    //    }

    /**
     * A named port on a backend instance group representing the port for communication to the backend
     * VMs in that group. Required when the loadBalancingScheme is EXTERNAL and the backends are
     * instance groups. The named port must be defined on each backend instance group. This parameter
     * has no meaning if the backends are NEGs.
     *
     * Must be omitted when the loadBalancingScheme is INTERNAL (Internal TCP/UDP Load Blaancing).
     */
    public String getPortName() {
        return portName;
    }

    public void setPortName(String portName) {
        this.portName = portName;
    }

    /**
     * The protocol this BackendService uses to communicate with backends.
     *
     * Possible values are HTTP, HTTPS, TCP, SSL, or UDP, depending on the chosen load balancer or
     * Traffic Director configuration. Refer to the documentation for the load balancer or for Traffic
     * director for more information.
     */
    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    /**
     * URL of the region where the regional backend service resides. This field is not
     * applicable to global backend services. You must specify this field as part of the HTTP request
     * URL. It is not settable as a field in the request body.
     */
    @Output
    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    /**
     * The resource URL for the security policy associated with this backend service.
     */
    @Output
    public String getSecurityPolicy() {
        return securityPolicy;
    }

    public void setSecurityPolicy(String securityPolicy) {
        this.securityPolicy = securityPolicy;
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
     * Type of session affinity to use. The default is NONE. Session affinity is not applicable if the
     * --protocol is UDP.
     *
     * When the loadBalancingScheme is EXTERNAL, possible values are NONE, CLIENT_IP, or
     * GENERATED_COOKIE. You can use GENERATED_COOKIE if the protocol is HTTP or HTTPS.
     *
     * When the loadBalancingScheme is INTERNAL, possible values are NONE, CLIENT_IP, CLIENT_IP_PROTO,
     * or CLIENT_IP_PORT_PROTO.
     *
     * When the loadBalancingScheme is INTERNAL_SELF_MANAGED, possible values are NONE, CLIENT_IP,
     * GENERATED_COOKIE, HEADER_FIELD, or HTTP_COOKIE.
     */
    public String getSessionAffinity() {
        return sessionAffinity;
    }

    public void setSessionAffinity(String sessionAffinity) {
        this.sessionAffinity = sessionAffinity;
    }

    /**
     * The backend service timeout has a different meaning depending on the type of load balancer. For
     * more information read,  Backend service settings The default is 30 seconds.
     */
    public Integer getTimeoutSec() {
        return timeoutSec;
    }

    public void setTimeoutSec(Integer timeoutSec) {
        this.timeoutSec = timeoutSec;
    }

    @Override
    public void copyFrom(BackendService model) {
        setAffinityCookieTtlSec(model.getAffinityCookieTtlSec());
        List<ComputeBackend> diffableBackends = null;
        List<Backend> backends = model.getBackends();

        if (backends != null && !backends.isEmpty()) {
            diffableBackends = backends
                .stream()
                .map(backend -> {
                    ComputeBackend diffableBackend = getBackend()
                        .stream()
                        .filter(e -> e.isEqualTo(backend))
                        .findFirst()
                        .orElse(newSubresource(ComputeBackend.class));
                    diffableBackend.copyFrom(backend);
                    return diffableBackend;
                })
                .collect(Collectors.toList());
        }
        setBackend(diffableBackends);
        ComputeConnectionDraining diffableConnectionDraining = null;
        ConnectionDraining connectionDraining = model.getConnectionDraining();

        if (connectionDraining != null) {
            diffableConnectionDraining = Optional.ofNullable(getConnectionDraining())
                .orElse(newSubresource(ComputeConnectionDraining.class));
            diffableConnectionDraining.copyFrom(connectionDraining);
        }
        setConnectionDraining(diffableConnectionDraining);
        Map<String, String> customHeaderMap = null;
        List<String> customRequestHeaders = model.getCustomRequestHeaders();

        if (customRequestHeaders != null) {
            customHeaderMap = customRequestHeaders
                .stream()
                .map(e -> e.split(":"))
                .filter(e -> e.length == 2)
                .collect(Collectors.toMap(e -> e[0], e -> e[1]));
        }
        setCustomRequestHeaders(customHeaderMap);
        setDescription(model.getDescription());
        setEnableCDN(model.getEnableCDN());
        setFingerprint(model.getFingerprint());
        List<HealthCheckResource> diffableHealthCheck = null;
        List<String> healthChecks = model.getHealthChecks();

        if (healthChecks != null) {
            diffableHealthCheck = healthChecks.stream()
                .map(e -> findById(HealthCheckResource.class, e))
                .collect(Collectors.toList());
        }
        setHealthCheck(diffableHealthCheck);
        setLoadBalancingScheme(model.getLoadBalancingScheme());
        setLocalityLbPolicy(model.getLocalityLbPolicy());
        setName(model.getName());
        setPortName(model.getPortName());
        setProtocol(model.getProtocol());
        setRegion(model.getRegion());
        setSecurityPolicy(model.getSecurityPolicy());
        setSelfLink(model.getSelfLink());
        setSessionAffinity(model.getSessionAffinity());
        setTimeoutSec(model.getTimeoutSec());
    }
}
