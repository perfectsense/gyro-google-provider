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

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.google.api.gax.rpc.NotFoundException;
import com.google.cloud.compute.v1.BackendService;
import com.google.cloud.compute.v1.DeleteRegionBackendServiceRequest;
import com.google.cloud.compute.v1.GetRegionBackendServiceRequest;
import com.google.cloud.compute.v1.HealthStatus;
import com.google.cloud.compute.v1.InsertRegionBackendServiceRequest;
import com.google.cloud.compute.v1.Operation;
import com.google.cloud.compute.v1.PatchRegionBackendServiceRequest;
import com.google.cloud.compute.v1.RegionBackendServicesClient;
import com.google.cloud.compute.v1.ResourceGroupReference;
import gyro.core.GyroException;
import gyro.core.GyroUI;
import gyro.core.Type;
import gyro.core.resource.Resource;
import gyro.core.scope.State;
import gyro.core.validation.Required;
import gyro.google.util.Utils;

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
    public void copyFrom(BackendService model) {
        super.copyFrom(model);

        if (model.hasRegion()) {
            setRegion(Utils.extractName(model.getRegion()));
        }
    }

    @Override
    protected boolean doRefresh() throws Exception {
        try (RegionBackendServicesClient client = createClient(RegionBackendServicesClient.class)) {
            BackendService response = fetchBackendService(client);

            if (response == null) {
                return false;
            }

            copyFrom(response);

            return true;
        }
    }

    @Override
    protected void doCreate(GyroUI ui, State state) throws Exception {
        try (RegionBackendServicesClient client = createClient(RegionBackendServicesClient.class)) {
            BackendService.Builder builder = getBackendService(null);

            builder.setRegion(getRegion());

            Operation operation = client.insertCallable().call(InsertRegionBackendServiceRequest.newBuilder()
                .setProject(getProject())
                .setRegion(getRegion())
                .setBackendServiceResource(builder.build())
                .build());

            waitForCompletion(operation);
        }

        refresh();
    }

    @Override
    public void doUpdate(GyroUI ui, State state, Resource current, Set<String> changedFieldNames) throws Exception {
        try (RegionBackendServicesClient client = createClient(RegionBackendServicesClient.class)) {
            BackendService.Builder builder = getBackendService(changedFieldNames);

            Operation operation = client.patchCallable().call(PatchRegionBackendServiceRequest.newBuilder()
                .setProject(getProject())
                .setRegion(getRegion())
                .setBackendService(getName())
                .setBackendServiceResource(builder.build())
                .build());

            waitForCompletion(operation);
        }
    }

    @Override
    public void doDelete(GyroUI ui, State state) throws Exception {
        try (RegionBackendServicesClient client = createClient(RegionBackendServicesClient.class)) {
            Operation operation = client.deleteCallable().call(DeleteRegionBackendServiceRequest.newBuilder()
                .setProject(getProject())
                .setRegion(getRegion())
                .setBackendService(getName())
                .build());
            waitForCompletion(operation);
        }
    }

    public Map<String, Map<String, Integer>> instanceHealth() {
        Map<String, Map<String, Integer>> healthMap = new HashMap<>();
        Map<String, Integer> allHealthMap = new HashMap<>();
        healthMap.put("all", allHealthMap);
        int allTotal = 0;

        try (RegionBackendServicesClient client = createClient(RegionBackendServicesClient.class)) {
            for (ComputeBackend backend : getBackend()) {
                int backendTotal = 0;
                ResourceGroupReference.Builder builder = ResourceGroupReference.newBuilder();
                builder.setGroup(backend.getGroup().referenceLink());
                List<HealthStatus> healthStatuses;
                Map<String, Integer> backendHealthMap = new HashMap<>();
                healthMap.put(backend.getGroup().primaryKey(), backendHealthMap);

                healthStatuses = Optional.ofNullable(client
                    .getHealth(getProjectId(), getRegion(), getName(), builder.build())
                    .getHealthStatusList())
                    .orElse(Collections.emptyList());

                for (HealthStatus healthStatus : healthStatuses) {
                    int backendCount = backendHealthMap.getOrDefault(healthStatus.getHealthState(), 0);
                    backendHealthMap.put(healthStatus.getHealthState(), backendCount + 1);
                    backendTotal++;

                    int allCount = allHealthMap.getOrDefault(healthStatus.getHealthState(), 0);
                    allHealthMap.put(healthStatus.getHealthState(), allCount + 1);
                    allTotal++;
                }

                backendHealthMap.put("Total", backendTotal);
            }

            allHealthMap.put("Total", allTotal);

        } catch (NotFoundException ex) {
            throw new GyroException("Failed getting backend statuses!", ex);
        }

        return healthMap;
    }

    static boolean isRegionBackendService(String selfLink) {
        return selfLink != null && selfLink.contains("regions") && selfLink.contains("backendServices");
    }

    private BackendService fetchBackendService(RegionBackendServicesClient client) {
        BackendService backendService = null;

        try {
            backendService = client.get(GetRegionBackendServiceRequest.newBuilder()
                .setProject(getProjectId())
                .setRegion(getRegion())
                .setBackendService(getName())
                .build());

        } catch (NotFoundException ex) {
            // ignore
        }

        return backendService;
    }
}
