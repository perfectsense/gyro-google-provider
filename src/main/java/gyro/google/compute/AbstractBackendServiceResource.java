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
import java.util.Set;
import java.util.stream.Collectors;

import com.google.api.client.util.Data;
import com.google.cloud.compute.v1.Backend;
import com.google.cloud.compute.v1.BackendService;
import com.google.cloud.compute.v1.ConnectionDraining;
import com.google.cloud.compute.v1.InstanceGroupManagersClient;
import gyro.core.GyroException;
import gyro.core.resource.Id;
import gyro.core.resource.Output;
import gyro.core.resource.Updatable;
import gyro.core.validation.Range;
import gyro.core.validation.Regex;
import gyro.core.validation.Required;
import gyro.core.validation.ValidStrings;
import gyro.google.Copyable;

public abstract class AbstractBackendServiceResource extends ComputeResource implements Copyable<BackendService> {

    private String name;
    private String description;
    private String selfLink;
    private Integer timeoutSec;
    private Integer affinityCookieTtlSec;
    private Boolean enableCdn;
    private BackendServiceCdnPolicy cdnPolicy;
    private ComputeConnectionDraining connectionDraining;
    private List<ComputeBackend> backend;
    private Map<String, String> customRequestHeaders;
    private List<HealthCheckResource> healthCheck;
    private BackendService.LoadBalancingScheme loadBalancingScheme;
    private BackendService.LocalityLbPolicy localityLbPolicy;
    private BackendService.Protocol protocol;
    private BackendService.SessionAffinity sessionAffinity;

    /**
     * If set to ``0``, the cookie is non-persistent and lasts only until the end of the browser session (or equivalent). Defaults to ``0``.
     */
    @Updatable
    @Range(min = 0, max = 86400)
    public Integer getAffinityCookieTtlSec() {
        return affinityCookieTtlSec;
    }

    public void setAffinityCookieTtlSec(Integer affinityCookieTtlSec) {
        this.affinityCookieTtlSec = affinityCookieTtlSec;
    }

    /**
     * The list of backend that serve this backend service.
     */
    @Updatable
    @Required
    public List<ComputeBackend> getBackend() {
        return backend;
    }

    public void setBackend(List<ComputeBackend> backend) {
        this.backend = backend;
    }

    /**
     * Connection draining configuration for the backend service.
     *
     * @subresource gyro.google.compute.ComputeConnectionDraining
     */
    @Updatable
    public ComputeConnectionDraining getConnectionDraining() {
        return connectionDraining;
    }

    public void setConnectionDraining(ComputeConnectionDraining connectionDraining) {
        this.connectionDraining = connectionDraining;
    }

    /**
     * Headers that the HTTP/S load balancer should add to proxied requests. Can only be set when ``protocol`` is set as ``HTTP`` or ``HTTPS``.
     */
    @Updatable
    public Map<String, String> getCustomRequestHeaders() {
        if (customRequestHeaders == null) {
            customRequestHeaders = new HashMap<>();
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
     * If true, enables Cloud CDN for the backend service.
     */
    @Updatable
    public Boolean getEnableCdn() {
        return enableCdn;
    }

    public void setEnableCdn(Boolean enableCdn) {
        this.enableCdn = enableCdn;
    }

    /**
     * A list of health check for the backend service. Currently only one health check is supported.
     */
    @Required
    @Updatable
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
     * Indicates whether the backend service will be used with internal or external load balancing.Valid values are ``INTERNAL``, ``INTERNAL_MANAGED`, ``INTERNAL_SELF_MANAGED`` or ``EXTERNAL``. Defaults to ``EXTERNAL``. See `Balance Modes <https://cloud.google.com/load-balancing/docs/backend-service#connection_balancing_mode>`_
     *
     * @no-docs ValidStrings
     */
    @ValidStrings({ "INTERNAL", "INTERNAL_MANAGED", "INTERNAL_SELF_MANAGED", "EXTERNAL" })
    public BackendService.LoadBalancingScheme getLoadBalancingScheme() {
        return loadBalancingScheme;
    }

    public void setLoadBalancingScheme(BackendService.LoadBalancingScheme loadBalancingScheme) {
        this.loadBalancingScheme = loadBalancingScheme;
    }

    /**
     * The load balancing algorithm used within the scope of the locality.
     */
    @Updatable
    @ValidStrings({ "ROUND_ROBIN", "LEAST_REQUEST", "RING_HASH", "RANDOM", "ORIGINAL_DESTINATION", "MAGLEV" })
    public BackendService.LocalityLbPolicy getLocalityLbPolicy() {
        return localityLbPolicy;
    }

    public void setLocalityLbPolicy(BackendService.LocalityLbPolicy localityLbPolicy) {
        this.localityLbPolicy = localityLbPolicy;
    }

    /**
     * The name of the backend service.
     */
    @Required
    @Regex(value = "(?:(?:[-a-z0-9]{1,63}\\.)*(?:[a-z](?:[-a-z0-9]{0,61}[a-z0-9])?):)?(?:[0-9]{1,19}|(?:[a-z0-9](?:[-a-z0-9]{0,61}[a-z0-9])?))", message = "a string 1-63 characters long and the first character must be a lowercase letter, and all following characters must be a dash, lowercase letter, or digit, except the last character, which cannot be a dash")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * The protocol this backend service uses to communicate. Default to ``HTTPS``
     */
    @Updatable
    @ValidStrings({ "HTTP", "HTTPS", "TCP", "SSL", "UDP" })
    public BackendService.Protocol getProtocol() {
        return protocol;
    }

    public void setProtocol(BackendService.Protocol protocol) {
        this.protocol = protocol;
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
     * Type of session affinity to use. When the ``load-balancing-scheme`` is ``EXTERNAL``, valid values are ``NONE``, ``CLIENT_IP`` or ``GENERATED_COOKIE``,``GENERATED_COOKIE`` only if ``protocol`` is ``HTTP`` or ``HTTPS``. ``load-balancing-scheme`` is ``INTERNAL``, valid values are ``NONE``, ``CLIENT_IP``, ``CLIENT_IP_PROTO`` or ``CLIENT_IP_PORT_PROTO``. When the ``load-balancing-scheme`` is ``INTERNAL_SELF_MANAGED``, valid values are ``NONE``, ``CLIENT_IP``, ``GENERATED_COOKIE``, ``HEADER_FIELD``, or ``HTTP_COOKIE``. Defaults to ``NONE``.
     */
    @Updatable
    @ValidStrings({
        "NONE", "CLIENT_IP", "GENERATED_COOKIE", "CLIENT_IP_PROTO",
        "CLIENT_IP_PORT_PROTO", "HEADER_FIELD", "HTTP_COOKIE" })
    public BackendService.SessionAffinity getSessionAffinity() {
        return sessionAffinity;
    }

    public void setSessionAffinity(BackendService.SessionAffinity sessionAffinity) {
        this.sessionAffinity = sessionAffinity;
    }

    /**
     * The backend service timeout. Defaults to ``30`` seconds.
     */
    @Updatable
    public Integer getTimeoutSec() {
        return timeoutSec;
    }

    public void setTimeoutSec(Integer timeoutSec) {
        this.timeoutSec = timeoutSec;
    }

    /**
     * CDN configuration for this backend service.
     *
     * @subresource gyro.google.compute.BackendServiceCdnPolicy
     */
    @Updatable
    public BackendServiceCdnPolicy getCdnPolicy() {
        return cdnPolicy;
    }

    public void setCdnPolicy(BackendServiceCdnPolicy cdnPolicy) {
        this.cdnPolicy = cdnPolicy;
    }

    @Override
    public void copyFrom(BackendService model) {
        setName(model.getName());
        setSelfLink(model.getSelfLink());

        if (model.hasDescription()) {
            setDescription(model.getDescription());
        }

        if (model.hasTimeoutSec()) {
            setTimeoutSec(model.getTimeoutSec());
        }

        if (model.hasAffinityCookieTtlSec()) {
            setAffinityCookieTtlSec(model.getAffinityCookieTtlSec());
        }

        if (model.hasEnableCDN()) {
            setEnableCdn(model.getEnableCDN());
        }

        if (model.hasLocalityLbPolicy()) {
            setLocalityLbPolicy(BackendService.LocalityLbPolicy.valueOf(model.getLocalityLbPolicy()));
        }

        if (model.hasLoadBalancingScheme()) {
            setLoadBalancingScheme(BackendService.LoadBalancingScheme.valueOf(model.getLoadBalancingScheme()));
        }

        if (model.hasProtocol()) {
            setProtocol(BackendService.Protocol.valueOf(model.getProtocol()));
        }

        if (model.hasSessionAffinity()) {
            setSessionAffinity(BackendService.SessionAffinity.valueOf(model.getSessionAffinity()));
        }

        List<ComputeBackend> diffableBackends = null;
        List<Backend> backends = model.getBackendsList();
        if (!backends.isEmpty()) {
            diffableBackends = backends
                .stream()
                .map(b -> {
                    ComputeBackend diffableBackend = newSubresource(ComputeBackend.class);
                    diffableBackend.copyFrom(b);

                    return diffableBackend;
                })
                .collect(Collectors.toList());
        }
        setBackend(diffableBackends);

        setConnectionDraining(null);
        if (model.hasConnectionDraining()) {
            ComputeConnectionDraining diffableConnectionDraining =
                Optional.ofNullable(getConnectionDraining())
                .orElse(newSubresource(ComputeConnectionDraining.class));

            ConnectionDraining connectionDraining = model.getConnectionDraining();
            diffableConnectionDraining.copyFrom(connectionDraining);

            setConnectionDraining(diffableConnectionDraining);
        }

        Map<String, String> customHeaderMap = null;
        List<String> customRequestHeaders = model.getCustomRequestHeadersList();
        customHeaderMap = customRequestHeaders
            .stream()
            .map(e -> e.split(":"))
            .filter(e -> e.length == 2)
            .collect(Collectors.toMap(e -> e[0], e -> e[1]));
        setCustomRequestHeaders(customHeaderMap);

        List<HealthCheckResource> diffableHealthCheck = null;
        List<String> healthChecks = model.getHealthChecksList();
            diffableHealthCheck = healthChecks.stream()
                .map(e -> findById(HealthCheckResource.class, e))
                .collect(Collectors.toList());
        setHealthCheck(diffableHealthCheck);

        BackendServiceCdnPolicy cdnPolicy = newSubresource(BackendServiceCdnPolicy.class);
        cdnPolicy.copyFrom(model.getCdnPolicy());
        setCdnPolicy(cdnPolicy);
    }

    protected BackendService.Builder getBackendService(Set<String> changedFieldNames) {
        boolean isCreate = changedFieldNames == null || changedFieldNames.isEmpty();

        BackendService.Builder builder = BackendService.newBuilder();

        if (isCreate) {
            if (getLoadBalancingScheme() != null) {
                builder.setLoadBalancingScheme(getLoadBalancingScheme().name());
            }

            builder.setName(getName());
        }

        if ((isCreate || changedFieldNames.contains("affinity-cookie-ttl-sec")) && getAffinityCookieTtlSec() != null) {
            builder.setAffinityCookieTtlSec(getAffinityCookieTtlSec());
        }

        if (isCreate || changedFieldNames.contains("backend")) {
            builder.addAllBackends(getBackend().stream()
                .map(ComputeBackend::toBackend)
                .collect(Collectors.toList()));
        }

        if ((isCreate || changedFieldNames.contains("connection-draining")) && getConnectionDraining() != null) {
            builder.setConnectionDraining(getConnectionDraining().toConnectionDraining());
        }

        if (isCreate || changedFieldNames.contains("custom-request-headers")) {
            builder.addAllCustomRequestHeaders(!getCustomRequestHeaders().isEmpty()
                ? getCustomRequestHeaders().entrySet()
                .stream()
                .map(e -> String.format("%s:%s", e.getKey(), e.getValue()))
                .collect(
                    Collectors.toList()) : Data.nullOf(ArrayList.class));
        }

        if ((isCreate || changedFieldNames.contains("description")) && getDescription() != null) {
            builder.setDescription(getDescription());
        }

        if ((isCreate || changedFieldNames.contains("enable-cdn")) && getEnableCdn() != null) {
            builder.setEnableCDN(getEnableCdn());
        }

        if (isCreate || changedFieldNames.contains("health-check")) {
            builder.addAllHealthChecks(getHealthCheck()
                .stream()
                .map(HealthCheckResource::getSelfLink)
                .collect(Collectors.toList()));
        }

        if ((isCreate || changedFieldNames.contains("locality-lb-policy")) && getLocalityLbPolicy() != null) {
            builder.setLocalityLbPolicy(getLocalityLbPolicy().name());
        }

        if ((isCreate || changedFieldNames.contains("protocol")) && getProtocol() != null) {
            builder.setProtocol(getProtocol().name());
        }

        if ((isCreate || changedFieldNames.contains("session-affinity")) && getSessionAffinity() != null) {
            builder.setSessionAffinity(getSessionAffinity().name());
        }

        if ((isCreate || changedFieldNames.contains("timeout-sec")) && getTimeoutSec() != null) {
            builder.setTimeoutSec(getTimeoutSec());
        }

        if (isCreate || changedFieldNames.contains("cdn-policy")) {
            if (isCreate) {
                if (getCdnPolicy() != null) {
                    builder.setCdnPolicy(getCdnPolicy().toBackendServiceCdnPolicy());
                }
            } else {
                if (getCdnPolicy() == null) {
                    throw new GyroException("'cdn-policy' cannot be unset once set.");
                } else {
                    builder.setCdnPolicy(getCdnPolicy().toBackendServiceCdnPolicy());
                }
            }

        }

        return builder;
    }

    InstanceGroupManagersClient createInstanceManagersClient() {
        return createClient(InstanceGroupManagersClient.class);
    }

    String getProject() {
        return getProjectId();
    }
}
