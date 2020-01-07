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
import java.util.stream.Collectors;

import com.google.api.services.compute.model.HostRule;
import com.google.api.services.compute.model.PathMatcher;
import com.google.api.services.compute.model.UrlMap;
import gyro.core.GyroException;
import gyro.core.resource.Id;
import gyro.core.resource.Output;
import gyro.core.resource.Updatable;
import gyro.core.validation.ConflictsWith;
import gyro.core.validation.Regex;
import gyro.core.validation.Required;
import gyro.core.validation.ValidationError;
import gyro.google.Copyable;

public abstract class AbstractUrlMap extends ComputeResource implements Copyable<UrlMap> {

    private String name;
    private BackendBucketResource defaultBackendBucket;
    private BackendServiceResource defaultBackendService;
    private RegionBackendServiceResource defaultRegionBackendService;
    private String description;
    private List<ComputeHostRule> hostRule;
    private List<ComputePathMatcher> pathMatcher;

    // Read-only
    private String selfLink;
    private String fingerprint;

    /**
     * The name of the URL map. Must be 1-63 characters long, and the first character must be a lowercase letter. All other characters must be a lowercase letter, digit, or ``-``, except the last character, which cannot be a ``-``. (Required)
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
     * The default backend bucket resource to which traffic is directed if none of the host rules match. Conflicts with ``default-backend-service`` and ``default-region-backend-service``.
     */
    @ConflictsWith({ "default-backend-service", "default-region-backend-service" })
    public BackendBucketResource getDefaultBackendBucket() {
        return defaultBackendBucket;
    }

    public void setDefaultBackendBucket(BackendBucketResource defaultBackendBucket) {
        this.defaultBackendBucket = defaultBackendBucket;
    }

    /**
     * The default backend service resource to which traffic is directed if none of the host rules match. Conflicts with ``default-backend-bucket`` and ``default-region-backend-service``.
     */
    @ConflictsWith({ "default-backend-bucket", "default-region-backend-service" })
    public BackendServiceResource getDefaultBackendService() {
        return defaultBackendService;
    }

    public void setDefaultBackendService(BackendServiceResource defaultBackendService) {
        this.defaultBackendService = defaultBackendService;
    }

    /**
     * The default region backend service resource to which traffic is directed if none of the host rules match. Conflicts with ``default-backend-bucket`` and ``default-backend-service``.
     */
    @ConflictsWith({ "default-backend-bucket", "default-backend-service" })
    public RegionBackendServiceResource getDefaultRegionBackendService() {
        return defaultRegionBackendService;
    }

    public void setDefaultRegionBackendService(RegionBackendServiceResource defaultRegionBackendService) {
        this.defaultRegionBackendService = defaultRegionBackendService;
    }

    /**
     * An optional description of this URL map.
     */
    @Updatable
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * The list of host rules to use against the URL.
     *
     * @subresource gyro.google.compute.ComputeHostRule
     */
    public List<ComputeHostRule> getHostRule() {
        if (hostRule == null) {
            hostRule = new ArrayList();
        }
        return hostRule;
    }

    public void setHostRule(List<ComputeHostRule> hostRule) {
        this.hostRule = hostRule;
    }

    /**
     * The list of named path matchers to use against the URL.
     *
     * @subresource gyro.google.compute.ComputePathMatcher
     */
    public List<ComputePathMatcher> getPathMatcher() {
        if (pathMatcher == null) {
            pathMatcher = new ArrayList();
        }
        return pathMatcher;
    }

    public void setPathMatcher(List<ComputePathMatcher> pathMatcher) {
        this.pathMatcher = pathMatcher;
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
     * The fingerprint for the URL map, which is a hash of the contents of the resource.
     */
    @Output
    public String getFingerprint() {
        return fingerprint;
    }

    public void setFingerprint(String fingerprint) {
        this.fingerprint = fingerprint;
    }

    @Override
    public void copyFrom(UrlMap model) {
        setName(model.getName());
        setDescription(model.getDescription());
        setSelfLink(model.getSelfLink());
        setFingerprint(model.getFingerprint());

        String defaultService = model.getDefaultService();
        setDefaultBackendBucket(null);
        if (BackendBucketResource.parseBackendBucket(getProjectId(), defaultService) != null) {
            setDefaultBackendBucket(findById(BackendBucketResource.class, defaultService));
        }

        setDefaultBackendService(null);
        if (BackendServiceResource.parseBackendService(getProjectId(), defaultService) != null) {
            setDefaultBackendService(findById(BackendServiceResource.class, defaultService));
        }

        setDefaultRegionBackendService(null);
        if (RegionBackendServiceResource.parseRegionBackendService(getProjectId(), defaultService) != null) {
            setDefaultRegionBackendService(findById(RegionBackendServiceResource.class, defaultService));
        }

        List<ComputeHostRule> computeHostRules = null;
        List<HostRule> hostRules = model.getHostRules();

        if (hostRules != null && !hostRules.isEmpty()) {
            computeHostRules = hostRules
                .stream()
                .map(hostRule -> {
                    ComputeHostRule diffableHostRules = getHostRule()
                        .stream()
                        .filter(e -> e.isEqualTo(hostRule))
                        .findFirst()
                        .orElse(newSubresource(ComputeHostRule.class));
                    diffableHostRules.copyFrom(hostRule);
                    return diffableHostRules;
                })
                .collect(Collectors.toList());
        }
        setHostRule(computeHostRules);

        List<ComputePathMatcher> computePathMatchers = null;
        List<PathMatcher> pathMatchers = model.getPathMatchers();

        if (pathMatchers != null && !pathMatchers.isEmpty()) {
            computePathMatchers = pathMatchers
                .stream()
                .map(pathMatcher -> {
                    ComputePathMatcher diffableHostRules = getPathMatcher()
                        .stream()
                        .filter(e -> e.isEqualTo(pathMatcher))
                        .findFirst()
                        .orElse(newSubresource(ComputePathMatcher.class));
                    diffableHostRules.copyFrom(pathMatcher);
                    return diffableHostRules;
                })
                .collect(Collectors.toList());
        }
        setPathMatcher(computePathMatchers);
    }

    protected UrlMap toUrlMap() {
        UrlMap urlMap = new UrlMap();
        urlMap.setName(getName());
        urlMap.setDescription(getDescription());

        String defaultService;
        if (getDefaultBackendBucket() != null) {
            defaultService = getDefaultBackendBucket().getSelfLink();
        } else if (getDefaultBackendService() != null) {
            defaultService = getDefaultBackendService().getSelfLink();
        } else if (getDefaultRegionBackendService() != null) {
            defaultService = getDefaultRegionBackendService().getSelfLink();
        } else {
            throw new GyroException(
                "Either 'default-backend-bucket', 'default-backend-service', or 'default-region-backend-service' is required!");
        }
        urlMap.setDefaultService(defaultService);

        List<ComputeHostRule> hostRules = getHostRule();
        if (!hostRules.isEmpty()) {
            urlMap.setHostRules(hostRules.stream().map(ComputeHostRule::copyTo).collect(Collectors.toList()));
        }

        List<ComputePathMatcher> pathMatchers = getPathMatcher();
        if (!pathMatchers.isEmpty()) {
            urlMap.setPathMatchers(pathMatchers.stream().map(ComputePathMatcher::copyTo).collect(Collectors.toList()));
        }

        return urlMap;
    }

    @Override
    public List<ValidationError> validate() {
        List<ValidationError> errors = new ArrayList<>();

        if (getDefaultBackendBucket() == null && getDefaultBackendService() == null
            && getDefaultRegionBackendService() == null) {
            errors.add(new ValidationError(
                this,
                null,
                "Either 'default-backend-bucket', 'default-backend-service', or 'default-region-backend-service' is required!"));
        }
        return errors;
    }
}
