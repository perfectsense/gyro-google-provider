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

import com.google.api.services.compute.Compute;
import com.google.api.services.compute.model.Operation;
import com.google.api.services.compute.model.UrlMap;
import com.google.cloud.compute.v1.ProjectGlobalUrlMapName;
import gyro.core.GyroUI;
import gyro.core.Type;
import gyro.core.resource.Resource;
import gyro.core.scope.State;
import gyro.core.validation.ConflictsWith;
import gyro.core.validation.ValidationError;

/**
 * Creates a global URL map.
 *
 * Example
 * -------
 *
 * .. code-block:: gyro
 *
 *     google::compute-url-map url-map-example
 *         name: "url-map-example"
 *         description: "URL map description."
 *         default-backend-service: $(google::compute-backend-service backend-service-example-url-map)
 *
 *         host-rule
 *             hosts: [ "example.com" ]
 *             path-matcher: "abc-path"
 *         end
 *
 *         path-matcher
 *             name: "abc-path"
 *             default-backend-service: $(google::compute-backend-service backend-service-example-url-map)
 *             path-rule
 *                 backend-bucket: $(google::compute-backend-bucket backend-bucket-example)
 *                 paths: [ "/abc" ]
 *             end
 *         end
 *     end
 */
@Type("compute-url-map")
public class UrlMapResource extends AbstractUrlMapResource {

    private BackendBucketResource defaultBackendBucket;
    private BackendServiceResource defaultBackendService;
    private RegionBackendServiceResource defaultRegionBackendService;

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

    @Override
    public void copyFrom(UrlMap urlMap) {
        super.copyFrom(urlMap);

        String defaultService = urlMap.getDefaultService();
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
    }

    @Override
    public boolean doRefresh() throws Exception {
        Compute client = createComputeClient();
        copyFrom(client.urlMaps().get(getProjectId(), getName()).execute());
        return true;
    }

    @Override
    public void doCreate(GyroUI ui, State state) throws Exception {
        Compute client = createComputeClient();

        UrlMap urlMap = toUrlMap(null);
        String defaultService = "";
        if (getDefaultBackendBucket() != null) {
            defaultService = getDefaultBackendBucket().getSelfLink();
        } else if (getDefaultBackendService() != null) {
            defaultService = getDefaultBackendService().getSelfLink();
        } else if (getDefaultRegionBackendService() != null) {
            defaultService = getDefaultRegionBackendService().getSelfLink();
        }
        urlMap.setDefaultService(defaultService);

        Operation response = client.urlMaps().insert(getProjectId(), urlMap).execute();
        waitForCompletion(client, response);

        refresh();
    }

    @Override
    public void doUpdate(
        GyroUI ui, State state, Resource current, Set<String> changedFieldNames) throws Exception {
        Compute client = createComputeClient();

        UrlMap urlMap = toUrlMap(changedFieldNames);
        Operation operation = client.urlMaps().patch(getProjectId(), getName(), urlMap).execute();
        waitForCompletion(client, operation);

        refresh();
    }

    @Override
    public void doDelete(GyroUI ui, State state) throws Exception {
        Compute client = createComputeClient();
        Operation response = client.urlMaps().delete(getProjectId(), getName()).execute();
        waitForCompletion(client, response);
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

    static boolean isUrlMap(String selfLink) {
        return selfLink != null && ProjectGlobalUrlMapName.isParsableFrom(formatResource(null, selfLink));
    }
}
