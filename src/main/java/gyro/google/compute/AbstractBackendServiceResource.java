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
import com.google.api.services.compute.model.Backend;
import com.google.api.services.compute.model.BackendService;
import com.google.api.services.compute.model.ConnectionDraining;
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

    private Integer affinityCookieTtlSec;
    private List<ComputeBackend> backend;
    private ComputeConnectionDraining connectionDraining;
    private Map<String, String> customRequestHeaders;
    private String description;
    private Boolean enableCdn;
    private List<HealthCheckResource> healthCheck;
    private String loadBalancingScheme;
    private String localityLbPolicy;
    private String name;
    private String protocol;
    private String selfLink;
    private String sessionAffinity;
    private Integer timeoutSec;
    private BackendServiceCdnPolicy cdnPolicy;

    /**
     * If set to 0, the cookie is non-persistent and lasts only until the end of the browser session (or equivalent). Valid values are ``0`` to ``86400``. Defaults to ``0``.
     */
    @Updatable
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
        if (backend == null) {
            backend = new ArrayList<>();
        }
        return backend;
    }

    public void setBackend(List<ComputeBackend> backend) {
        this.backend = backend;
    }

    /**
     * Connection draining configuration for the backend service.
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
     * A list of health check for the backend service. Currently only one health check is supported. (Required)
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
     * Indicates whether the backend service will be used with internal or external load balancing.Valid values are ``INTERNAL``, ``INTERNAL_MANAGED`, ``INTERNAL_SELF_MANAGED`` or ``EXTERNAL``. Defaults to ``EXTERNAL``.
     */
    @ValidStrings({"INTERNAL", "INTERNAL_MANAGED", "INTERNAL_SELF_MANAGED", "EXTERNAL"})
    public String getLoadBalancingScheme() {
        return loadBalancingScheme;
    }

    public void setLoadBalancingScheme(String loadBalancingScheme) {
        this.loadBalancingScheme = loadBalancingScheme;
    }

    /**
     * The load balancing algorithm used within the scope of the locality. Valid values are ``ROUND_ROBIN``, ``LEAST_REQUEST``, ``RING_HASH``, ``RANDOM``, ``ORIGINAL_DESTINATION`` or ``MAGLEV``.
     */
    @Updatable
    @ValidStrings({ "ROUND_ROBIN", "LEAST_REQUEST", "RING_HASH", "RANDOM", "ORIGINAL_DESTINATION", "MAGLEV" })
    public String getLocalityLbPolicy() {
        return localityLbPolicy;
    }

    public void setLocalityLbPolicy(String localityLbPolicy) {
        this.localityLbPolicy = localityLbPolicy;
    }

    /**
     * The name of the backend service. The name must be 1-63 characters long and the first character must be a lowercase letter, and all following characters must be a dash, lowercase letter, or digit, except the last character, which cannot be a dash. (Required)
     */
    @Required
    @Regex("(?:(?:[-a-z0-9]{1,63}\\.)*(?:[a-z](?:[-a-z0-9]{0,61}[a-z0-9])?):)?(?:[0-9]{1,19}|(?:[a-z0-9](?:[-a-z0-9]{0,61}[a-z0-9])?))")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * The protocol this backend service uses to communicate. Valid values are ``HTTP``, ``HTTPS``, ``TCP``, ``SSL``, or ``UDP``. Default to ``HTTPS``
     */
    @Updatable
    @ValidStrings({ "HTTP", "HTTPS", "TCP", "SSL", "UDP" })
    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
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
        "NONE",
        "CLIENT_IP",
        "GENERATED_COOKIE",
        "CLIENT_IP_PROTO",
        "CLIENT_IP_PORT_PROTO",
        "HEADER_FIELD",
        "HTTP_COOKIE" })
    public String getSessionAffinity() {
        return sessionAffinity;
    }

    public void setSessionAffinity(String sessionAffinity) {
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
        setAffinityCookieTtlSec(model.getAffinityCookieTtlSec());
        List<ComputeBackend> diffableBackends = null;
        List<Backend> backends = model.getBackends();

        if (backends != null && !backends.isEmpty()) {
            diffableBackends = backends
                .stream()
                .map(backend -> {
                    ComputeBackend diffableBackend = newSubresource(ComputeBackend.class);
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
        setEnableCdn(model.getEnableCDN());
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
        setProtocol(model.getProtocol());
        setSelfLink(model.getSelfLink());
        setSessionAffinity(model.getSessionAffinity());
        setTimeoutSec(model.getTimeoutSec());

        if (model.getCdnPolicy() != null) {
            BackendServiceCdnPolicy cdnPolicy = newSubresource(BackendServiceCdnPolicy.class);
            cdnPolicy.copyFrom(model.getCdnPolicy());
            setCdnPolicy(cdnPolicy);
        } else {
            setCdnPolicy(null);
        }
    }

    protected BackendService getBackendService(Set<String> changedFieldNames) {
        boolean isCreate = changedFieldNames == null;

        BackendService backendService = new BackendService();

        if (isCreate) {
            backendService.setLoadBalancingScheme(getLoadBalancingScheme());
            backendService.setName(getName());
        }

        if (isCreate || changedFieldNames.contains("affinity-cookie-ttl-sec")) {
            backendService.setAffinityCookieTtlSec(getAffinityCookieTtlSec());
        }

        if (isCreate || changedFieldNames.contains("backend")) {
            backendService.setBackends(getBackend().stream()
                .map(ComputeBackend::toBackend)
                .collect(Collectors.toList()));
        }

        if (isCreate || changedFieldNames.contains("connection-draining")) {
            backendService.setConnectionDraining(getConnectionDraining() != null
                ? getConnectionDraining().toConnectionDraining()
                : Data.nullOf(ConnectionDraining.class));
        }

        if (isCreate || changedFieldNames.contains("custom-request-headers")) {
            backendService.setCustomRequestHeaders(!getCustomRequestHeaders().isEmpty()
                ? getCustomRequestHeaders().entrySet()
                .stream()
                .map(e -> String.format("%s:%s", e.getKey(), e.getValue()))
                .collect(
                    Collectors.toList()) : Data.nullOf(ArrayList.class));
        }

        if (isCreate || changedFieldNames.contains("description")) {
            backendService.setDescription(getDescription());
        }

        if (isCreate || changedFieldNames.contains("enable-cdn")) {
            backendService.setEnableCDN(getEnableCdn());
        }

        if (isCreate || changedFieldNames.contains("health-check")) {
            backendService.setHealthChecks(getHealthCheck()
                .stream()
                .map(HealthCheckResource::getSelfLink)
                .collect(Collectors.toList()));
        }

        if (isCreate || changedFieldNames.contains("locality-lb-policy")) {
            backendService.setLocalityLbPolicy(getLocalityLbPolicy());
        }

        if (isCreate || changedFieldNames.contains("protocol")) {
            backendService.setProtocol(getProtocol());
        }

        if (isCreate || changedFieldNames.contains("session-affinity")) {
            backendService.setSessionAffinity(getSessionAffinity());
        }

        if (isCreate || changedFieldNames.contains("timeout-sec")) {
            backendService.setTimeoutSec(getTimeoutSec());
        }

        if (isCreate || changedFieldNames.contains("cdn-policy")) {
            if (isCreate) {
                backendService.setCdnPolicy(getCdnPolicy() != null
                    ? getCdnPolicy().toBackendServiceCdnPolicy()
                    : Data.nullOf(com.google.api.services.compute.model.BackendServiceCdnPolicy.class));
            } else {
                if (getCdnPolicy() == null) {
                    throw new GyroException("'cdn-policy' cannot be unset once set.");
                } else {
                    backendService.setCdnPolicy(getCdnPolicy().toBackendServiceCdnPolicy());
                }
            }

        }

        return backendService;
    }

    String getProject() {
        return getProjectId();
    }
}
