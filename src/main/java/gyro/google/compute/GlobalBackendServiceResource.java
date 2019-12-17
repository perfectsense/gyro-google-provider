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

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.api.services.compute.Compute;
import com.google.api.services.compute.model.BackendService;
import com.google.api.services.compute.model.Operation;
import gyro.core.GyroUI;
import gyro.core.Type;
import gyro.core.resource.Resource;
import gyro.core.scope.State;

@Type("compute-backend-service")
public class GlobalBackendServiceResource extends BackendServiceResource {

    @Override
    protected boolean doRefresh() throws Exception {
        Compute client = createComputeClient();
        BackendService response = client.backendServices().get(getProjectId(), getName()).execute();
        copyFrom(response);
        return true;
    }

    @Override
    protected void doCreate(GyroUI ui, State state) throws Exception {
        BackendService backendService = new BackendService();
        backendService.setAffinityCookieTtlSec(getAffinityCookieTtlSec());
        List<ComputeBackend> backend = getBackend();

        if (!backend.isEmpty()) {
            backendService.setBackends(backend.stream().map(ComputeBackend::copyTo).collect(Collectors.toList()));
        }
        ComputeConnectionDraining connectionDraining = getConnectionDraining();

        if (connectionDraining != null) {
            backendService.setConnectionDraining(connectionDraining.copyTo());
        }
        Map<String, String> customRequestHeaders = getCustomRequestHeaders();

        if (!customRequestHeaders.isEmpty()) {
            backendService.setCustomRequestHeaders(customRequestHeaders.entrySet()
                .stream()
                .map(e -> String.format("%s:%s", e.getKey(), e.getValue()))
                .collect(
                    Collectors.toList()));
        }
        backendService.setDescription(getDescription());
        backendService.setEnableCDN(getEnableCDN());
        backendService.setFingerprint(getFingerprint());
        List<HealthCheckResource> healthCheck = getHealthCheck();

        if (!healthCheck.isEmpty()) {
            backendService.setHealthChecks(healthCheck
                .stream()
                .map(HealthCheckResource::getSelfLink)
                .collect(Collectors.toList()));
        }
        backendService.setLoadBalancingScheme(getLoadBalancingScheme());
        backendService.setLocalityLbPolicy(getLocalityLbPolicy());
        backendService.setName(getName());
        backendService.setPortName(getPortName());
        backendService.setProtocol(getProtocol());
        backendService.setSessionAffinity(getSessionAffinity());
        backendService.setTimeoutSec(getTimeoutSec());

        Compute client = createComputeClient();
        Operation response = client.backendServices().insert(getProjectId(), backendService).execute();
        waitForCompletion(client, response);
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
        Operation response = client.backendServices().delete(getProjectId(), getName()).execute();
        waitForCompletion(client, response);
    }
}
