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

import java.util.Set;

import com.google.api.services.compute.Compute;
import com.google.api.services.compute.model.Operation;
import com.google.api.services.compute.model.UrlMap;
import gyro.core.GyroUI;
import gyro.core.Type;
import gyro.core.resource.Resource;
import gyro.core.scope.State;
import gyro.core.validation.Required;

/**
 * Creates a region URL map.
 *
 * Example
 * -------
 *
 * .. code-block:: gyro
 *
 *     google::compute-region-url-map region-url-map-example
 *         name: "region-url-map-example"
 *         region: "us-central1"
 *         description: "Region URL map description."
 *         default-region-backend-service: $(google::compute-region-backend-service region-backend-service-example)
 *
 *         host-rule
 *             hosts: [ "example.com" ]
 *             path-matcher: "abc-path"
 *         end
 *
 *         path-matcher
 *             name: "abc-path"
 *             default-region-backend-service: $(google::compute-region-backend-service region-backend-service-example)
 *             path-rule
 *                 region-backend-service: $(google::compute-region-backend-service region-backend-service-example)
 *                 paths: [ "/abc" ]
 *             end
 *         end
 *     end
 */
@Type("compute-region-url-map")
public class RegionUrlMapResource extends AbstractUrlMapResource {

    private String region;
    private RegionBackendServiceResource defaultRegionBackendService;

    /**
     * The region for the URL map. (Required)
     */
    @Required
    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region != null ? region.substring(region.lastIndexOf("/") + 1) : null;
    }

    /**
     * The default region backend service resource to which traffic is directed if none of the host rules match. (Required)
     */
    @Required
    public RegionBackendServiceResource getDefaultRegionBackendService() {
        return defaultRegionBackendService;
    }

    public void setDefaultRegionBackendService(RegionBackendServiceResource defaultRegionBackendService) {
        this.defaultRegionBackendService = defaultRegionBackendService;
    }

    @Override
    public void copyFrom(UrlMap urlMap) {
        super.copyFrom(urlMap);

        setRegion(urlMap.getRegion());

        String defaultService = urlMap.getDefaultService();

        setDefaultRegionBackendService(null);
        if (RegionBackendServiceResource.isRegionBackendService(defaultService)) {
            setDefaultRegionBackendService(findById(RegionBackendServiceResource.class, defaultService));
        }
    }

    @Override
    protected boolean doRefresh() throws Exception {
        Compute client = createComputeClient();
        copyFrom(client.regionUrlMaps().get(getProjectId(), getRegion(), getName()).execute());
        return true;
    }

    @Override
    protected void doCreate(GyroUI ui, State state) throws Exception {
        Compute client = createComputeClient();

        UrlMap urlMap = toUrlMap(null);
        urlMap.setRegion(getRegion());
        urlMap.setDefaultService(getDefaultRegionBackendService().getSelfLink());

        Operation response = client.regionUrlMaps().insert(getProjectId(), getRegion(), urlMap).execute();
        waitForCompletion(client, response);

        refresh();
    }

    @Override
    public void doUpdate(
        GyroUI ui, State state, Resource current, Set<String> changedFieldNames) throws Exception {
        Compute client = createComputeClient();

        UrlMap urlMap = toUrlMap(changedFieldNames);
        Operation operation = client.regionUrlMaps().patch(getProjectId(), getRegion(), getName(), urlMap).execute();
        waitForCompletion(client, operation);

        refresh();
    }

    @Override
    public void doDelete(GyroUI ui, State state) throws Exception {
        Compute client = createComputeClient();
        Operation response = client.regionUrlMaps().delete(getProjectId(), getRegion(), getName()).execute();
        waitForCompletion(client, response);
    }
}
