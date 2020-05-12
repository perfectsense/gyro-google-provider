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

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.google.api.services.compute.Compute;
import com.google.api.services.compute.model.BackendService;
import com.google.api.services.compute.model.HealthStatus;
import com.google.api.services.compute.model.Operation;
import com.google.api.services.compute.model.ResourceGroupReference;
import com.google.cloud.compute.v1.ProjectRegionBackendServiceName;
import gyro.core.GyroException;
import gyro.core.GyroUI;
import gyro.core.Type;
import gyro.core.resource.Resource;
import gyro.core.scope.State;
import gyro.core.validation.Required;

/**
 * Creates a regional backend service.
 *
 * Example
 * -------
 *
 * .. code-block:: gyro
 *
 *     google::compute-region-backend-service regional-backend-service-example
 *         name: 'regional-backend-service-example'
 *         region: "us-central1"
 *         description: 'regional-backend-service-example-desc'
 *
 *         backend
 *             group
 *                 instance-group: $(google::compute-instance-group instance-group-example-regional-backend-service)
 *             end
 *
 *             balancing-mode: "UTILIZATION"
 *         end
 *
 *         health-check: [ $(google::compute-health-check health-check-example-regional-backend-service) ]
 *
 *         connection-draining
 *             draining-timeout-sec: 41
 *         end
 *
 *         load-balancing-scheme: "INTERNAL"
 *
 *         protocol: "HTTPS"
 *         session-affinity: "NONE"
 *     end
 */
@Type("compute-region-backend-service")
public class RegionBackendServiceResource extends AbstractBackendServiceResource {

    private String region;

    /**
     * The region for the backend service.
     */
    @Required
    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    @Override
    protected boolean doRefresh() throws Exception {
        Compute client = createComputeClient();
        BackendService response = client.regionBackendServices().get(getProjectId(), getRegion(), getName()).execute();
        copyFrom(response);
        return true;
    }

    @Override
    protected void doCreate(GyroUI ui, State state) throws Exception {
        Compute client = createComputeClient();

        BackendService backendService = getBackendService(null);

        Operation operation = client.regionBackendServices().insert(getProjectId(), getRegion(), backendService).execute();
        waitForCompletion(client, operation);

        refresh();
    }

    @Override
    public void doUpdate(
        GyroUI ui, State state, Resource current, Set<String> changedFieldNames) throws Exception {

        Compute client = createComputeClient();

        BackendService backendService = getBackendService(changedFieldNames);

        Operation operation = client.regionBackendServices().patch(getProjectId(), getRegion(), getName(), backendService).execute();
        waitForCompletion(client, operation);
    }

    @Override
    public void doDelete(GyroUI ui, State state) throws Exception {
        Compute client = createComputeClient();
        Operation response = client.regionBackendServices().delete(getProjectId(), getRegion(), getName()).execute();
        waitForCompletion(client, response);
    }

    public Map<String, Integer> instanceHealth() {
        Map<String, Integer> healthMap = new HashMap<>();
        int total = 0;

        Compute client = createComputeClient();

        for (ComputeBackend backend : getBackend()) {
            ResourceGroupReference resourceGroupReference = new ResourceGroupReference();
            resourceGroupReference.setGroup(backend.getGroup().referenceLink());
            List<HealthStatus> healthStatuses;

            try {
                healthStatuses = Optional.ofNullable(client.regionBackendServices()
                    .getHealth(getProjectId(), getRegion(), getName(), resourceGroupReference)
                    .execute()
                    .getHealthStatus())
                    .orElse(Collections.emptyList());
            } catch (IOException ex) {
                throw new GyroException("Failed getting backend statuses!", ex);
            }

            for (HealthStatus healthStatus : healthStatuses) {
                int count = healthMap.getOrDefault(healthStatus.getHealthState(), 0);
                healthMap.put(healthStatus.getHealthState(), count + 1);
                total++;
            }
        }

        healthMap.put("Total", total);
        return healthMap;
    }

    static boolean isRegionBackendService(String selfLink) {
        return selfLink != null && (ProjectRegionBackendServiceName.isParsableFrom(formatResource(null, selfLink)));
    }
}
