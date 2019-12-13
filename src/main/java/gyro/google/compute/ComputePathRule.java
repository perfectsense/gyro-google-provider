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
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.google.api.services.compute.model.PathRule;
import gyro.core.resource.Diffable;
import gyro.core.resource.DiffableType;
import gyro.core.validation.ConflictsWith;
import gyro.core.validation.ValidationError;
import gyro.google.Copyable;

public class ComputePathRule extends Diffable implements Copyable<PathRule> {

    private BackendBucketResource backendBucket;

    private GlobalBackendServiceResource backendService;

    private List<String> paths;

    /**
     * In response to a matching path, the load balancer performs advanced routing actions like URL
     * rewrites, header transformations, etc. prior to forwarding the request to the selected backend.
     * If routeAction specifies any  weightedBackendServices, service must not be set. Conversely if
     * service is set, routeAction cannot contain any  weightedBackendServices. Only one of
     * routeAction or urlRedirect must be set.
     *
     private ComputeHttpRouteAction routeAction;
     */

    /**
     * When a path pattern is matched, the request is redirected to a URL specified by urlRedirect. If
     * urlRedirect is specified, service or routeAction must not be set.
     *
     private ComputeHttpRedirectAction urlRedirect;
     */

    /**
     * The backend bucket resource to which traffic is directed if this
     * rule is matched. If routeAction is additionally specified, advanced routing actions like URL
     * Rewrites, etc. take effect prior to sending the request to the backend. However, if service is
     * specified, routeAction cannot contain any weightedBackendService s. Conversely, if routeAction
     * specifies any  weightedBackendServices, service must not be specified. Only one of urlRedirect,
     * service or routeAction.weightedBackendService must be set.
     */
    @ConflictsWith("backend-service")
    public BackendBucketResource getBackendBucket() {
        return backendBucket;
    }

    public void setBackendBucket(BackendBucketResource backendBucket) {
        this.backendBucket = backendBucket;
    }

    /**
     * The backend service resource to which traffic is directed if this
     * rule is matched. If routeAction is additionally specified, advanced routing actions like URL
     * Rewrites, etc. take effect prior to sending the request to the backend. However, if service is
     * specified, routeAction cannot contain any weightedBackendService s. Conversely, if routeAction
     * specifies any  weightedBackendServices, service must not be specified. Only one of urlRedirect,
     * service or routeAction.weightedBackendService must be set.
     */
    @ConflictsWith("backend-bucket")
    public GlobalBackendServiceResource getBackendService() {
        return backendService;
    }

    public void setBackendService(GlobalBackendServiceResource backendService) {
        this.backendService = backendService;
    }

    /**
     * The list of path patterns to match. Each must start with / and the only place a * is allowed is
     * at the end following a /. The string fed to the path matcher does not include any text after
     * the first ? or #, and those chars are not allowed here.
     */
    public List<String> getPaths() {
        if (paths == null) {
            paths = new ArrayList();
        }
        return paths;
    }

    public void setPaths(List<String> paths) {
        this.paths = paths;
    }

    @Override
    public void copyFrom(PathRule model) {
        setPaths(model.getPaths());

        BackendBucketResource backendBucket = null;
        GlobalBackendServiceResource backendService = null;
        String service = model.getService();

        if (service != null) {
            BackendBucketResource possibleBackendBucket = findById(BackendBucketResource.class, service);

            if (possibleBackendBucket.getName() != null) {
                backendBucket = possibleBackendBucket;
            } else {
                GlobalBackendServiceResource possibleBackendService = findById(
                    GlobalBackendServiceResource.class,
                    service);

                if (possibleBackendBucket.getName() != null) {
                    backendService = possibleBackendService;
                }
            }
        }
        setBackendBucket(backendBucket);
        setBackendService(backendService);
    }

    @Override
    public String primaryKey() {
        return String.format(
            "%s::%s",
            DiffableType.getInstance(getClass()).getName(),
            getPaths().stream().collect(Collectors.joining(";")));
    }

    @Override
    public List<ValidationError> validate() {
        List<ValidationError> errors = new ArrayList<>();

        // make 'backend-bucket' or 'backend-service' effectively required.
        if (getBackendBucket() == null && getBackendService() == null) {
            errors.add(new ValidationError(
                this,
                "backend-bucket",
                "Either 'backend-bucket' or 'backend-service' is required!"));
            errors.add(new ValidationError(
                this,
                "backend-service",
                "Either 'backend-bucket' or 'backend-service' is required!"));
        }
        return errors;
    }

    public PathRule copyTo() {
        PathRule pathRule = new PathRule();
        pathRule.setPaths(getPaths());

        String service = null;
        BackendBucketResource backendBucket = getBackendBucket();

        if (backendBucket != null) {
            service = backendBucket.getSelfLink();
        } else {
            GlobalBackendServiceResource backendService = getBackendService();

            if (backendService != null) {
                service = backendService.getSelfLink();
            } else {
                // TODO: throw
            }
        }
        pathRule.setService(service);
        return pathRule;
    }

    protected boolean isEqualTo(PathRule model) {
        return Optional.ofNullable(model)
            .map(PathRule::getPaths)
            .map(HashSet::new)
            .filter(hosts -> hosts.equals(new HashSet<>(getPaths())))
            .isPresent();
    }
}
