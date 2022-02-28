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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.api.gax.rpc.NotFoundException;
import com.google.cloud.compute.v1.AddSignedUrlKeyBackendServiceRequest;
import com.google.cloud.compute.v1.BackendService;
import com.google.cloud.compute.v1.BackendServicesClient;
import com.google.cloud.compute.v1.DeleteBackendServiceRequest;
import com.google.cloud.compute.v1.DeleteSignedUrlKeyBackendServiceRequest;
import com.google.cloud.compute.v1.GetBackendServiceRequest;
import com.google.cloud.compute.v1.HealthStatus;
import com.google.cloud.compute.v1.InsertBackendServiceRequest;
import com.google.cloud.compute.v1.Operation;
import com.google.cloud.compute.v1.PatchBackendServiceRequest;
import com.google.cloud.compute.v1.ResourceGroupReference;
import com.google.cloud.compute.v1.SecurityPolicyReference;
import com.google.cloud.compute.v1.SetSecurityPolicyBackendServiceRequest;
import gyro.core.GyroUI;
import gyro.core.Type;
import gyro.core.resource.Resource;
import gyro.core.resource.Updatable;
import gyro.core.scope.State;
import gyro.core.validation.ValidationError;

/**
 * Creates a backend service.
 *
 * Example
 * -------
 *
 * .. code-block:: gyro
 *
 *     google::compute-backend-service backend-service-example
 *         name: 'backend-service-example'
 *         description: 'backend-service-example-desc'
 *
 *         backend
 *             group
 *                 instance-group: $(google::compute-instance-group instance-group-example-regional-backend-service)
 *             end
 *         end
 *
 *         health-check: [ $(google::compute-health-check health-check-example-backend-service) ]
 *
 *         security-policy: $(google::compute-security-policy security-policy-example-backend-service)
 *
 *         connection-draining
 *             draining-timeout-sec: 30
 *         end
 *
 *         load-balancing-scheme: "EXTERNAL"
 *
 *         enable-cdn: false
 *         protocol: "HTTPS"
 *         session-affinity: "NONE"
 *         port-name: "http"
 *
 *         cdn-policy
 *             signed-url-max-age: 30000
 *
 *             cache-key-policy
 *                 include-host: true
 *                 include-protocol: true
 *             end
 *         end
 *
 *         signed-url-key
 *             key: "xyz"
 *             value: "ZWVsbG8gZnJvbSBHb29nbA=="
 *         end
 *     end
 */
@Type("compute-backend-service")
public class BackendServiceResource extends AbstractBackendServiceResource {

    private List<BackendSignedUrlKey> signedUrlKey;
    private String portName;
    private SecurityPolicyResource securityPolicy;

    /**
     * Signed Url key configuration for the backend service.
     */
    @Updatable
    public List<BackendSignedUrlKey> getSignedUrlKey() {
        if (signedUrlKey == null) {
            signedUrlKey = new ArrayList<>();
        }

        return signedUrlKey;
    }

    public void setSignedUrlKey(List<BackendSignedUrlKey> signedUrlKey) {
        this.signedUrlKey = signedUrlKey;
    }

    /**
     * A named port on a backend instance group representing the port for communication to the backend instances.
     */
    @Updatable
    public String getPortName() {
        return portName;
    }

    public void setPortName(String portName) {
        this.portName = portName;
    }

    /**
     * The security policy associated with this backend service. This can only be added when ``enableCdn`` is ``false``.
     */
    @Updatable
    public SecurityPolicyResource getSecurityPolicy() {
        return securityPolicy;
    }

    public void setSecurityPolicy(SecurityPolicyResource securityPolicy) {
        this.securityPolicy = securityPolicy;
    }

    @Override
    public void copyFrom(BackendService model) {
        super.copyFrom(model);

        if (model.hasPortName()) {
            setPortName(model.getPortName());
        }

        if (model.hasSecurityPolicy()) {
            setSecurityPolicy(findById(SecurityPolicyResource.class, model.getSecurityPolicy()));
        }

        if (model.hasCdnPolicy()) {
            // add any new keys not configured through gyro
            Set<String> keys = getSignedUrlKey().stream()
                .map(BackendSignedUrlKey::getKey)
                .collect(Collectors.toSet());
            for (String key : getCdnPolicy().getSignedUrlKeyNames()) {
                if (!keys.contains(key)) {
                    BackendSignedUrlKey urlKey = newSubresource(BackendSignedUrlKey.class);
                    urlKey.setKey(key);
                    urlKey.setValue("hidden");
                    getSignedUrlKey().add(urlKey);
                }
            }

            // remove any keys configured through gyro but removed
            HashSet<String> keysStored = new HashSet<>(getCdnPolicy().getSignedUrlKeyNames());
            getSignedUrlKey().removeIf(o -> !keysStored.contains(o.getKey()));
        }
    }

    static boolean isBackendService(String selfLink) {
        return selfLink != null && selfLink.contains("global") && selfLink.contains("backendServices");
    }

    @Override
    protected boolean doRefresh() throws Exception {
        try (BackendServicesClient client = createClient(BackendServicesClient.class)) {
            BackendService response = getBackendServiceResource(client);

            if (response == null) {
                return false;
            }

            copyFrom(response);

            return true;
        }
    }

    private BackendService getBackendServiceResource(BackendServicesClient client) {
        BackendService backendService = null;

        try {
            backendService = client.get(GetBackendServiceRequest.newBuilder()
                .setProject(getProjectId())
                .setBackendService(getName())
                .build());

        } catch (NotFoundException ex) {
            // ignore
        }

        return backendService;
    }

    @Override
    protected void doCreate(GyroUI ui, State state) throws Exception {
        try (BackendServicesClient client = createClient(BackendServicesClient.class)) {

            BackendService.Builder backendService = getBackendService(null);

            if (getPortName() != null) {
                backendService.setPortName(getPortName());
            }

            Operation operation = client.insertCallable().call(InsertBackendServiceRequest.newBuilder()
                .setProject(getProject())
                .setBackendServiceResource(backendService)
                .build());

            waitForCompletion(operation);

            if (getSecurityPolicy() != null) {
                state.save();

                saveSecurityPolicy(client);
            }

            state.save();

            if (!getSignedUrlKey().isEmpty()) {
                for (BackendSignedUrlKey urlKey : getSignedUrlKey()) {
                    waitForCompletion(client.addSignedUrlKeyOperationCallable().call(
                        AddSignedUrlKeyBackendServiceRequest.newBuilder()
                            .setProject(getProject())
                            .setBackendService(getName())
                            .setSignedUrlKeyResource(urlKey.toSignedUrlKey())
                            .build()));
                }
            }
        }

        refresh();
    }

    @Override
    public void doUpdate(GyroUI ui, State state, Resource current, Set<String> changedFieldNames) throws Exception {

        try (BackendServicesClient client = createClient(BackendServicesClient.class)) {

            BackendServiceResource currentBackendResource = (BackendServiceResource) current;

            boolean securityPolicyUpdated = false;

            if (changedFieldNames.contains("enable-cdn") && Boolean.TRUE.equals(getEnableCdn())
                && currentBackendResource.getSecurityPolicy() != null) {
                saveSecurityPolicy(client);
                securityPolicyUpdated = true;
            }

            BackendService.Builder backendService = getBackendService(changedFieldNames);
            Operation operation = client.patchCallable().call(PatchBackendServiceRequest.newBuilder()
                .setProject(getProject())
                .setBackendService(getName())
                .build());
            waitForCompletion(operation);

            if (changedFieldNames.contains("port-name")) {
                backendService.setPortName(getPortName());
            }

            if (changedFieldNames.contains("signed-url-key")) {
                // delete old keys
                List<String> deleteSignedUrlKeys = currentBackendResource.getSignedUrlKey().stream().map(
                    BackendSignedUrlKey::getKey).collect(
                    Collectors.toList());

                for (String urlKey : deleteSignedUrlKeys) {
                    waitForCompletion(client.deleteSignedUrlKeyCallable().call(
                        DeleteSignedUrlKeyBackendServiceRequest.newBuilder()
                            .setProject(getProject())
                            .setBackendService(getName())
                            .setKeyName(urlKey)
                            .build()));
                }

                // add new keys
                for (BackendSignedUrlKey urlKey : getSignedUrlKey()) {
                    waitForCompletion(client.addSignedUrlKeyOperationCallable().call(
                        AddSignedUrlKeyBackendServiceRequest.newBuilder()
                            .setProject(getProject())
                            .setBackendService(getName())
                            .setSignedUrlKeyResource(urlKey.toSignedUrlKey())
                            .build()));
                }
            }

            if (changedFieldNames.contains("security-policy") && !securityPolicyUpdated) {
                saveSecurityPolicy(client);
            }
        }
    }

    @Override
    public void doDelete(GyroUI ui, State state) throws Exception {
        try (BackendServicesClient client = createClient(BackendServicesClient.class)) {
            Operation response = client.deleteCallable().call(DeleteBackendServiceRequest.newBuilder()
                .setProject(getProject())
                .setBackendService(getName())
                .build());
            waitForCompletion(response);
        }
    }

    @Override
    public List<ValidationError> validate(Set<String> configuredFields) {
        List<ValidationError> errors = new ArrayList<>();
        if (getEnableCdn() != null && getEnableCdn().equals(Boolean.TRUE) && getSecurityPolicy() != null) {
            errors.add(new ValidationError(
                this,
                "enable-cdn",
                "'enable-cdn' can't be true when a security policy is provided."));
        }

        return errors;
    }

    private void saveSecurityPolicy(BackendServicesClient client) throws Exception {
        SecurityPolicyResource securityPolicyResource = getSecurityPolicy();
        SecurityPolicyReference securityPolicyReference = null;

        if (securityPolicyResource != null) {
            securityPolicyReference = SecurityPolicyReference.newBuilder()
                .setSecurityPolicy(securityPolicyResource.getSelfLink()).build();
        }

        Operation securityPolicyOperation = client.setSecurityPolicyCallable().call(
            SetSecurityPolicyBackendServiceRequest.newBuilder()
                .setProject(getProject())
                .setBackendService(getName())
                .setSecurityPolicyReferenceResource(securityPolicyReference)
                .build());
        waitForCompletion(securityPolicyOperation);
    }

    public Map<String, Map<String, Integer>> instanceHealth() {
        Map<String, Map<String, Integer>> healthMap = new HashMap<>();
        Map<String, Integer> allHealthMap = new HashMap<>();
        healthMap.put("all", allHealthMap);
        int allTotal = 0;

        try (BackendServicesClient client = createClient(BackendServicesClient.class)) {

            for (ComputeBackend backend : getBackend()) {
                int backendTotal = 0;
                ResourceGroupReference.Builder builder = ResourceGroupReference.newBuilder();
                builder.setGroup(backend.getGroup().referenceLink());
                List<HealthStatus> healthStatuses;
                Map<String, Integer> backendHealthMap = new HashMap<>();
                healthMap.put(backend.getGroup().primaryKey(), backendHealthMap);

                healthStatuses = Optional.ofNullable(client
                    .getHealth(getProjectId(), getName(), builder.build())
                    .getHealthStatusList())
                    .orElse(new ArrayList<>());

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

            return healthMap;

        }
    }
}
