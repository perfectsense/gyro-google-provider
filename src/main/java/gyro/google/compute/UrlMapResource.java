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
import java.util.Set;
import java.util.stream.Collectors;

import com.google.api.services.compute.Compute;
import com.google.api.services.compute.model.HostRule;
import com.google.api.services.compute.model.Operation;
import com.google.api.services.compute.model.PathMatcher;
import com.google.api.services.compute.model.UrlMap;
import gyro.core.GyroException;
import gyro.core.GyroUI;
import gyro.core.Type;
import gyro.core.resource.Id;
import gyro.core.resource.Output;
import gyro.core.resource.Resource;
import gyro.core.resource.Updatable;
import gyro.core.scope.State;
import gyro.core.validation.ConflictsWith;
import gyro.core.validation.Required;
import gyro.core.validation.ValidationError;
import gyro.google.Copyable;

@Type("compute-url-map")
public class UrlMapResource extends ComputeResource implements Copyable<UrlMap> {

    private BackendBucketResource defaultBackendBucket;

    private GlobalBackendServiceResource defaultBackendService;

    private ComputeHttpRouteAction defaultRouteAction;

    private ComputeHttpRedirectAction defaultUrlRedirect;

    private String description;

    private String fingerprint;

    private ComputeHttpHeaderAction headerAction;

    private List<ComputeHostRule> hostRule;

    private String name;

    private List<ComputePathMatcher> pathMatcher;

    private String region;

    private String selfLink;

    /**
     * The default backend bucket resource to which traffic is directed if none of
     * the hostRules match. If defaultRouteAction is additionally specified, advanced routing actions
     * like URL Rewrites, etc. take effect prior to sending the request to the backend. However, if
     * defaultService is specified, defaultRouteAction cannot contain any weightedBackendServices.
     * Conversely, if routeAction specifies any weightedBackendServices, service must not be
     * specified. Only one of defaultService, defaultUrlRedirect  or
     * defaultRouteAction.weightedBackendService must be set.
     */
    @ConflictsWith("default-backend-service")
    public BackendBucketResource getDefaultBackendBucket() {
        return defaultBackendBucket;
    }

    public void setDefaultBackendBucket(BackendBucketResource defaultBackendBucket) {
        this.defaultBackendBucket = defaultBackendBucket;
    }

    /**
     * The default backend service resource to which traffic is directed if none of
     * the hostRules match. If defaultRouteAction is additionally specified, advanced routing actions
     * like URL Rewrites, etc. take effect prior to sending the request to the backend. However, if
     * defaultService is specified, defaultRouteAction cannot contain any weightedBackendServices.
     * Conversely, if routeAction specifies any weightedBackendServices, service must not be
     * specified. Only one of defaultService, defaultUrlRedirect  or
     * defaultRouteAction.weightedBackendService must be set.
     */
    @ConflictsWith("default-backend-bucket")
    public GlobalBackendServiceResource getDefaultBackendService() {
        return defaultBackendService;
    }

    public void setDefaultBackendService(GlobalBackendServiceResource defaultBackendService) {
        this.defaultBackendService = defaultBackendService;
    }

    /**
     * defaultRouteAction takes effect when none of the  hostRules match. The load balancer performs
     * advanced routing actions like URL rewrites, header transformations, etc. prior to forwarding
     * the request to the selected backend. If defaultRouteAction specifies any
     * weightedBackendServices, defaultService must not be set. Conversely if defaultService is set,
     * defaultRouteAction cannot contain any  weightedBackendServices. Only one of defaultRouteAction
     * or defaultUrlRedirect must be set.
     */
    public ComputeHttpRouteAction getDefaultRouteAction() {
        return defaultRouteAction;
    }

    public void setDefaultRouteAction(ComputeHttpRouteAction defaultRouteAction) {
        this.defaultRouteAction = defaultRouteAction;
    }

    /**
     * When none of the specified hostRules match, the request is redirected to a URL specified by
     * defaultUrlRedirect. If defaultUrlRedirect is specified, defaultService or defaultRouteAction
     * must not be set.
     */
    public ComputeHttpRedirectAction getDefaultUrlRedirect() {
        return defaultUrlRedirect;
    }

    public void setDefaultUrlRedirect(ComputeHttpRedirectAction defaultUrlRedirect) {
        this.defaultUrlRedirect = defaultUrlRedirect;
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
     * Fingerprint of this resource. A hash of the contents stored in this object. This field is used
     * in optimistic locking. This field will be ignored when inserting a UrlMap. An up-to-date
     * fingerprint must be provided in order to update the UrlMap, otherwise the request will fail
     * with error 412 conditionNotMet.
     *
     * To see the latest fingerprint, make a get() request to retrieve a UrlMap.
     */
    public String getFingerprint() {
        return fingerprint;
    }

    public void setFingerprint(String fingerprint) {
        this.fingerprint = fingerprint;
    }

    /**
     * Specifies changes to request and response headers that need to take effect for the selected
     * backendService. The headerAction specified here take effect after headerAction specified under
     * pathMatcher.
     */
    public ComputeHttpHeaderAction getHeaderAction() {
        return headerAction;
    }

    public void setHeaderAction(ComputeHttpHeaderAction headerAction) {
        this.headerAction = headerAction;
    }

    /**
     * The list of HostRules to use against the URL.
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
     * The list of named PathMatchers to use against the URL.
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
     * URL of the region where the regional URL map resides. This field is not
     * applicable to global URL maps. You must specify this field as part of the HTTP request URL. It
     * is not settable as a field in the request body.
     */
    @Output
    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
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
     * The list of expected URL mapping tests. Request to update this UrlMap will succeed only if all
     * of the test cases pass. You can specify a maximum of 100 tests per UrlMap.
     *
     private List<UrlMapTest> tests;
     */
    @Override
    public boolean doRefresh() throws Exception {
        Compute client = createComputeClient();
        copyFrom(client.urlMaps().get(getProjectId(), getName()).execute());
        return true;
    }

    @Override
    public void doCreate(GyroUI ui, State state) throws Exception {
        UrlMap urlMap = new UrlMap();
        BackendBucketResource defaultBackendBucket = getDefaultBackendBucket();

        if (defaultBackendBucket != null) {
            urlMap.setDefaultService(defaultBackendBucket.getSelfLink());
        } else {
            GlobalBackendServiceResource defaultBackendService = getDefaultBackendService();

            if (defaultBackendService != null) {
                urlMap.setDefaultService(defaultBackendService.getSelfLink());
            } else {
                // TODO: throw exception
            }
        }
        urlMap.setDescription(getDescription());
        List<ComputeHostRule> hostRule = getHostRule();

        if (!hostRule.isEmpty()) {
            urlMap.setHostRules(hostRule.stream().map(ComputeHostRule::copyTo).collect(Collectors.toList()));
        }
        urlMap.setName(getName());
        List<ComputePathMatcher> pathMatcher = getPathMatcher();

        if (!pathMatcher.isEmpty()) {
            urlMap.setPathMatchers(pathMatcher.stream().map(ComputePathMatcher::copyTo).collect(Collectors.toList()));
        }

        Compute client = createComputeClient();
        Operation response = client.urlMaps().insert(getProjectId(), urlMap).execute();
        Operation.Error error = waitForCompletion(client, response);

        if (error != null) {
            throw new GyroException(error.toPrettyString());
        }
        refresh();
    }

    @Override
    public void doUpdate(
        GyroUI ui, State state, Resource current, Set<String> changedFieldNames) throws Exception {
        // TODO:
    }

    @Override
    public void doDelete(GyroUI ui, State state) throws Exception {
        Compute client = createComputeClient();
        Operation response = client.urlMaps().delete(getProjectId(), getName()).execute();
        Operation.Error error = waitForCompletion(client, response);

        if (error != null) {
            throw new GyroException(error.toPrettyString());
        }
    }

    @Override
    public void copyFrom(UrlMap model) {
        String defaultService = model.getDefaultService();

        if (defaultService != null) {
            BackendBucketResource backendBucket = findById(BackendBucketResource.class, defaultService);

            if (backendBucket.getName() != null) {
                setDefaultBackendBucket(backendBucket);
            } else {
                GlobalBackendServiceResource backendService = findById(
                    GlobalBackendServiceResource.class,
                    defaultService);

                if (backendService.getName() != null) {
                    setDefaultBackendService(backendService);
                } else {
                    // TODO: throw exception
                }
            }
        }
        setDescription(model.getDescription());
        setFingerprint(model.getFingerprint());
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
        setName(model.getName());

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
        setRegion(model.getRegion());
        setSelfLink(model.getSelfLink());
    }

    @Override
    public List<ValidationError> validate() {
        List<ValidationError> errors = new ArrayList<>();

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
}
