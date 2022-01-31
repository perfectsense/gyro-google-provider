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

import com.google.cloud.compute.v1.PathMatcher;
import gyro.core.resource.Diffable;
import gyro.core.resource.Updatable;
import gyro.core.validation.ConflictsWith;
import gyro.core.validation.Required;
import gyro.core.validation.ValidationError;
import gyro.google.Copyable;

public class ComputePathMatcher extends Diffable implements Copyable<PathMatcher> {

    private String name;
    private String description;
    private BackendBucketResource defaultBackendBucket;
    private BackendServiceResource defaultBackendService;
    private RegionBackendServiceResource defaultRegionBackendService;
    private List<ComputePathRule> pathRule;
    private HttpRedirectAction defaultUrlRedirect;

    /**
     * The name to which this path matcher is referred by the host rule.
     */
    @Required
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * An optional description of this path matcher.
     */
    @Updatable
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * The default backend bucket resource to which traffic is directed if none of the host rules match.
     */
    @ConflictsWith({ "default-backend-service", "default-region-backend-service" })
    public BackendBucketResource getDefaultBackendBucket() {
        return defaultBackendBucket;
    }

    public void setDefaultBackendBucket(BackendBucketResource defaultBackendBucket) {
        this.defaultBackendBucket = defaultBackendBucket;
    }

    /**
     * The default backend service resource to which traffic is directed if none of the host rules match.
     */
    @ConflictsWith({ "default-backend-bucket", "default-region-backend-service" })
    public BackendServiceResource getDefaultBackendService() {
        return defaultBackendService;
    }

    public void setDefaultBackendService(BackendServiceResource defaultBackendService) {
        this.defaultBackendService = defaultBackendService;
    }

    /**
     * The default region backend service resource to which traffic is directed if none of the host rules match.
     */
    @ConflictsWith({ "default-backend-bucket", "default-backend-service" })
    public RegionBackendServiceResource getDefaultRegionBackendService() {
        return defaultRegionBackendService;
    }

    public void setDefaultRegionBackendService(RegionBackendServiceResource defaultRegionBackendService) {
        this.defaultRegionBackendService = defaultRegionBackendService;
    }

    /**
     * The list of path rules.
     *
     * @subresource gyro.google.compute.ComputePathRule
     */
    @Updatable
    public List<ComputePathRule> getPathRule() {
        if (pathRule == null) {
            pathRule = new ArrayList<>();
        }
        return pathRule;
    }

    public void setPathRule(List<ComputePathRule> pathRule) {
        this.pathRule = pathRule;
    }

    /**
     * The default url redirect configuration.
     *
     * @subresource gyro.google.compute.HttpRedirectAction
     */
    public HttpRedirectAction getDefaultUrlRedirect() {
        return defaultUrlRedirect;
    }

    public void setDefaultUrlRedirect(HttpRedirectAction defaultUrlRedirect) {
        this.defaultUrlRedirect = defaultUrlRedirect;
    }

    @Override
    public void copyFrom(PathMatcher model) {
        setName(model.getName());

        if (model.hasDescription()) {
            setDescription(model.getDescription());
        }

        String defaultService = model.getDefaultService();
        setDefaultBackendBucket(null);
        if (BackendBucketResource.isBackendBucket(defaultService)) {
            setDefaultBackendBucket(findById(BackendBucketResource.class, defaultService));
        }

        setDefaultBackendService(null);
        if (BackendServiceResource.isBackendService(defaultService)) {
            setDefaultBackendService(findById(BackendServiceResource.class, defaultService));
        }

        setDefaultRegionBackendService(null);
        if (RegionBackendServiceResource.isRegionBackendService(defaultService)) {
            setDefaultRegionBackendService(findById(RegionBackendServiceResource.class, defaultService));
        }

        setPathRule(null);
        if (!model.getPathRulesList().isEmpty()) {
            List<ComputePathRule> computePathRules = model.getPathRulesList().stream()
                .map(pathRule -> {
                    ComputePathRule computePathRule = newSubresource(ComputePathRule.class);
                    computePathRule.copyFrom(pathRule);
                    return computePathRule;
                }).collect(Collectors.toList());

            setPathRule(computePathRules);
        }

        setDefaultUrlRedirect(null);
        if (model.hasDefaultUrlRedirect()) {
            HttpRedirectAction redirectAction = newSubresource(HttpRedirectAction.class);
            redirectAction.copyFrom(model.getDefaultUrlRedirect());

            setDefaultUrlRedirect(redirectAction);
        }
    }

    @Override
    public String primaryKey() {
        return getName();
    }

    @Override
    public List<ValidationError> validate() {
        List<ValidationError> errors = new ArrayList<>();

        if (getDefaultBackendBucket() == null && getDefaultBackendService() == null
            && getDefaultRegionBackendService() == null && getDefaultUrlRedirect() != null) {
            errors.add(new ValidationError(
                this,
                null,
                "Either 'default-backend-bucket', 'default-backend-service', 'default-region-backend-service', or 'default-url-redirect' is required!"));
        }
        return errors;
    }

    public PathMatcher copyTo() {
        PathMatcher.Builder builder = PathMatcher.newBuilder().setName(getName());

        if (getDescription() != null) {
            builder.setDescription(getDescription());
        }

        String defaultService = "";
        if (getDefaultBackendBucket() != null) {
            defaultService = getDefaultBackendBucket().getSelfLink();
        } else if (getDefaultBackendService() != null) {
            defaultService = getDefaultBackendService().getSelfLink();
        } else if (getDefaultRegionBackendService() != null) {
            defaultService = getDefaultRegionBackendService().getSelfLink();
        }

        if (getDefaultUrlRedirect() != null) {
            builder.setDefaultUrlRedirect(getDefaultUrlRedirect().toHttpRedirectAction());
        } else {
            builder.setDefaultService(defaultService);
        }

        List<ComputePathRule> pathRule = getPathRule();
        if (!pathRule.isEmpty()) {
            builder.addAllPathRules(pathRule.stream().map(ComputePathRule::copyTo).collect(Collectors.toList()));
        }

        return builder.build();
    }
}
