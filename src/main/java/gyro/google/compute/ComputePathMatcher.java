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
import java.util.Optional;
import java.util.stream.Collectors;

import com.google.api.services.compute.model.PathMatcher;
import com.google.api.services.compute.model.PathRule;
import gyro.core.resource.Diffable;
import gyro.core.resource.DiffableType;
import gyro.core.resource.Updatable;
import gyro.core.validation.ConflictsWith;
import gyro.core.validation.Required;
import gyro.core.validation.ValidationError;
import gyro.google.Copyable;

public class ComputePathMatcher extends Diffable implements Copyable<PathMatcher> {

    private BackendBucketResource defaultBackendBucket;

    private GlobalBackendServiceResource defaultBackendService;

    /**
     * When when none of the specified pathRules or routeRules match, the request is redirected to a
     * URL specified by defaultUrlRedirect. If defaultUrlRedirect is specified, defaultService or
     * defaultRouteAction must not be set.
     *
     private ComputeHttpRedirectAction defaultUrlRedirect;
     */
    private String description;

    /**
     * Specifies changes to request and response headers that need to take effect for the selected
     * backendService. HeaderAction specified here are applied after the matching HttpRouteRule
     * HeaderAction and before the HeaderAction in the UrlMap
     *
     private ComputeHttpHeaderAction headerAction;
     */
    private String name;

    private List<ComputePathRule> pathRule;

    /**
     * The backend bucket resource. This will be used if none of the
     * pathRules or routeRules defined by this PathMatcher are matched. For example, the following are
     * all valid URLs to a BackendService resource: -
     * https://www.googleapis.com/compute/v1/projects/project/global/backendServices/backendService  -
     * compute/v1/projects/project/global/backendServices/backendService  -
     * global/backendServices/backendService  If defaultRouteAction is additionally specified,
     * advanced routing actions like URL Rewrites, etc. take effect prior to sending the request to
     * the backend. However, if defaultService is specified, defaultRouteAction cannot contain any
     * weightedBackendServices. Conversely, if defaultRouteAction specifies any
     * weightedBackendServices, defaultService must not be specified. Only one of defaultService,
     * defaultUrlRedirect  or defaultRouteAction.weightedBackendService must be set. Authorization
     * requires one or more of the following Google IAM permissions on the specified resource
     * default_service:   - compute.backendBuckets.use  - compute.backendServices.use
     */
    @ConflictsWith("default-backend-service")
    public BackendBucketResource getDefaultBackendBucket() {
        return defaultBackendBucket;
    }

    public void setDefaultBackendBucket(BackendBucketResource defaultBackendBucket) {
        this.defaultBackendBucket = defaultBackendBucket;
    }

    /**
     * The backend service resource. This will be used if none of the
     * pathRules or routeRules defined by this PathMatcher are matched. For example, the following are
     * all valid URLs to a BackendService resource: -
     * https://www.googleapis.com/compute/v1/projects/project/global/backendServices/backendService  -
     * compute/v1/projects/project/global/backendServices/backendService  -
     * global/backendServices/backendService  If defaultRouteAction is additionally specified,
     * advanced routing actions like URL Rewrites, etc. take effect prior to sending the request to
     * the backend. However, if defaultService is specified, defaultRouteAction cannot contain any
     * weightedBackendServices. Conversely, if defaultRouteAction specifies any
     * weightedBackendServices, defaultService must not be specified. Only one of defaultService,
     * defaultUrlRedirect  or defaultRouteAction.weightedBackendService must be set. Authorization
     * requires one or more of the following Google IAM permissions on the specified resource
     * default_service:   - compute.backendBuckets.use  - compute.backendServices.use
     */
    @ConflictsWith("default-backend-bucket")
    public GlobalBackendServiceResource getDefaultBackendService() {
        return defaultBackendService;
    }

    public void setDefaultBackendService(GlobalBackendServiceResource defaultBackendService) {
        this.defaultBackendService = defaultBackendService;
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
     * The name to which this PathMatcher is referred by the HostRule.
     */
    @Required
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * The list of path rules. Use this list instead of routeRules when routing based on simple path
     * matching is all that's required. The order by which path rules are specified does not matter.
     * Matches are always done on the longest-path-first basis. For example: a pathRule with a path
     * /a/b/c will match before /a/b irrespective of the order in which those paths appear in this
     * list. Within a given pathMatcher, only one of pathRules or routeRules must be set.
     */
    public List<ComputePathRule> getPathRule() {
        if (pathRule == null) {
            pathRule = new ArrayList();
        }
        return pathRule;
    }

    public void setPathRule(List<ComputePathRule> pathRule) {
        this.pathRule = pathRule;
    }

    /**
     * The list of ordered HTTP route rules. Use this list instead of pathRules when advanced route
     * matching and routing actions are desired. The order of specifying routeRules matters: the first
     * rule that matches will cause its specified routing action to take effect. Within a given
     * pathMatcher, only one of pathRules or routeRules must be set. routeRules are not supported in
     * UrlMaps intended for External Load balancers.
     *
     private List<HttpRouteRule> routeRules;
     */
    @Override
    public void copyFrom(PathMatcher model) {
        BackendBucketResource backendBucket = null;
        GlobalBackendServiceResource backendService = null;
        String service = model.getDefaultService();

        if (service != null) {
            BackendBucketResource possibleBackendBucket = findById(BackendBucketResource.class, service);

            if (possibleBackendBucket.getName() != null) {
                backendBucket = possibleBackendBucket;
            } else {
                GlobalBackendServiceResource possibleBackendService = findById(
                    GlobalBackendServiceResource.class,
                    service);

                if (possibleBackendService.getName() != null) {
                    backendService = possibleBackendService;
                }
            }
        }
        setDefaultBackendBucket(backendBucket);
        setDefaultBackendService(backendService);
        setDescription(getDescription());
        setName(getName());
        List<ComputePathRule> computePathRules = null;
        List<PathRule> pathRules = model.getPathRules();

        if (pathRules != null) {
            computePathRules = pathRules
                .stream()
                .map(pathRule -> {
                    ComputePathRule diffablePathRule = getPathRule()
                        .stream()
                        .filter(e -> e.isEqualTo(pathRule))
                        .findFirst()
                        .orElse(newSubresource(ComputePathRule.class));
                    diffablePathRule.copyFrom(pathRule);
                    return diffablePathRule;
                })
                .collect(Collectors.toList());
        }
        setPathRule(computePathRules);
    }

    @Override
    public String primaryKey() {
        return String.format(
            "%s::%s",
            DiffableType.getInstance(getClass()).getName(),
            getName() == null ? "" : getName());
    }

    @Override
    public List<ValidationError> validate() {
        List<ValidationError> errors = new ArrayList<>();

        // make 'default-backend-bucket' or 'default-backend-service' effectively required.
        if (getDefaultBackendBucket() == null && getDefaultBackendService() == null) {
            errors.add(new ValidationError(
                this,
                "default-backend-bucket",
                "Either 'default-backend-bucket' or 'default-backend-service' is required!"));
            errors.add(new ValidationError(
                this,
                "default-backend-service",
                "Either 'default-backend-bucket' or 'default-backend-service' is required!"));
        }
        return errors;
    }

    public PathMatcher copyTo() {
        PathMatcher pathMatcher = new PathMatcher();

        String defaultService = null;
        BackendBucketResource backendBucket = getDefaultBackendBucket();

        if (backendBucket != null) {
            defaultService = backendBucket.getSelfLink();
        } else {
            GlobalBackendServiceResource backendService = getDefaultBackendService();

            if (backendService != null) {
                defaultService = backendService.getSelfLink();
            } else {
                // TODO: throw
            }
        }
        pathMatcher.setDefaultService(defaultService);
        pathMatcher.setDescription(getDescription());
        pathMatcher.setName(getName());
        List<ComputePathRule> pathRule = getPathRule();

        if (!pathRule.isEmpty()) {
            pathMatcher.setPathRules(pathRule.stream().map(ComputePathRule::copyTo).collect(Collectors.toList()));
        }
        return pathMatcher;
    }

    protected boolean isEqualTo(PathMatcher model) {
        return Optional.ofNullable(model)
            .map(PathMatcher::getName)
            .filter(hosts -> hosts.equals(getName()))
            .isPresent();
    }
}
