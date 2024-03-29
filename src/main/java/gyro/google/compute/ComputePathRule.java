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

import com.google.cloud.compute.v1.PathRule;
import gyro.core.resource.Diffable;
import gyro.core.resource.Updatable;
import gyro.core.validation.ConflictsWith;
import gyro.core.validation.ValidationError;
import gyro.google.Copyable;

public class ComputePathRule extends Diffable implements Copyable<PathRule> {

    private BackendBucketResource backendBucket;
    private BackendServiceResource backendService;
    private RegionBackendServiceResource regionBackendService;
    private List<String> paths;
    private HttpRedirectAction urlRedirect;

    /**
     * The backend bucket resource to which traffic is directed if this rule is matched.
     */
    @ConflictsWith({ "backend-service", "region-backend-service" })
    public BackendBucketResource getBackendBucket() {
        return backendBucket;
    }

    public void setBackendBucket(BackendBucketResource backendBucket) {
        this.backendBucket = backendBucket;
    }

    /**
     * The backend service resource to which traffic is directed if this rule is matched.
     */
    @ConflictsWith({ "backend-bucket", "region-backend-service" })
    public BackendServiceResource getBackendService() {
        return backendService;
    }

    public void setBackendService(BackendServiceResource backendService) {
        this.backendService = backendService;
    }

    /**
     * The region backend service resource to which traffic is directed if this rule is matched.
     */
    @ConflictsWith({ "backend-bucket", "backend-service" })
    public RegionBackendServiceResource getRegionBackendService() {
        return regionBackendService;
    }

    public void setRegionBackendService(RegionBackendServiceResource regionBackendService) {
        this.regionBackendService = regionBackendService;
    }

    /**
     * The list of path patterns to match. Each must start with ``/`` and the only place a ``*`` is allowed is at the end following a ``/``. The string fed to the path matcher does not include any text after the first ``?`` or ``#```, and those chars are not allowed here.
     */
    @Updatable
    public List<String> getPaths() {
        if (paths == null) {
            paths = new ArrayList<>();
        }
        return paths;
    }

    public void setPaths(List<String> paths) {
        this.paths = paths;
    }

    /**
     * The url redirect configuration.
     *
     * @subresource gyro.google.compute.HttpRedirectAction
     */
    public HttpRedirectAction getUrlRedirect() {
        return urlRedirect;
    }

    public void setUrlRedirect(HttpRedirectAction urlRedirect) {
        this.urlRedirect = urlRedirect;
    }

    @Override
    public void copyFrom(PathRule model) {
        setPaths(model.getPathsList());

        String service = model.getService();
        setBackendBucket(null);
        if (BackendBucketResource.isBackendBucket(service)) {
            setBackendBucket(findById(BackendBucketResource.class, service));
        }

        setBackendService(null);
        if (BackendServiceResource.isBackendService(service)) {
            setBackendService(findById(BackendServiceResource.class, service));
        }

        setRegionBackendService(null);
        if (RegionBackendServiceResource.isRegionBackendService(service)) {
            setRegionBackendService(findById(RegionBackendServiceResource.class, service));
        }

        setUrlRedirect(null);
        if (model.hasUrlRedirect()) {
            HttpRedirectAction redirectAction = newSubresource(HttpRedirectAction.class);
            redirectAction.copyFrom(model.getUrlRedirect());

            setUrlRedirect(redirectAction);
        }
    }

    @Override
    public String primaryKey() {
        return String.join(";", getPaths());
    }

    @Override
    public List<ValidationError> validate() {
        List<ValidationError> errors = new ArrayList<>();

        if (getBackendBucket() == null && getBackendService() == null
            && getRegionBackendService() == null && getUrlRedirect() == null) {
            errors.add(new ValidationError(
                this,
                null,
                "Either 'backend-bucket', 'backend-service', 'region-backend-service', or 'url-redirect' is required!"));
        }
        return errors;
    }

    public PathRule copyTo() {
        PathRule.Builder builder = PathRule.newBuilder();
        builder.addAllPaths(getPaths());

        String service = "";
        if (getBackendBucket() != null) {
            service = getBackendBucket().getSelfLink();
        } else if (getBackendService() != null) {
            service = getBackendService().getSelfLink();
        } else if (getRegionBackendService() != null) {
            service = getRegionBackendService().getSelfLink();
        }

        if (getUrlRedirect() != null) {
            builder.setUrlRedirect(getUrlRedirect().toHttpRedirectAction());
        } else {
            builder.setService(service);
        }

        return builder.build();
    }
}
