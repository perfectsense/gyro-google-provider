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
import java.util.Set;
import java.util.stream.Collectors;

import com.google.api.services.compute.Compute;
import com.google.api.services.compute.model.BackendService;
import com.google.api.services.compute.model.Operation;
import com.google.api.services.compute.model.SecurityPolicyReference;
import com.google.cloud.compute.v1.ProjectGlobalBackendServiceName;
import gyro.core.GyroUI;
import gyro.core.Type;
import gyro.core.resource.Resource;
import gyro.core.resource.Updatable;
import gyro.core.scope.State;

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
 *             group: $(google::compute-instance-group instance-group-example-backend-service)
 *         end
 *
 *         health-check: [ $(google::compute-health-check health-check-example-backend-service) ]
 *
 *         connection-draining
 *             draining-timeout-sec: 30
 *         end
 *
 *         load-balancing-scheme: "EXTERNAL"
 *
 *         enable-cdn: true
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

    @Override
    public void copyFrom(BackendService model) {
        super.copyFrom(model);
        setPortName(model.getPortName());

        if (getCdnPolicy() != null) {
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

    @Override
    protected boolean doRefresh() throws Exception {
        Compute client = createComputeClient();
        BackendService response = client.backendServices().get(getProjectId(), getName()).execute();
        copyFrom(response);
        return true;
    }

    @Override
    protected void doCreate(GyroUI ui, State state) throws Exception {
        Compute client = createComputeClient();

        BackendService backendService = getBackendService(null);
        backendService.setPortName(getPortName());

        Operation operation = client.backendServices().insert(getProjectId(), backendService).execute();
        waitForCompletion(client, operation);

        if (getSecurityPolicy() != null) {
            state.save();

            saveSecurityPolicy(client);
        }

        if (!getSignedUrlKey().isEmpty()) {
            state.save();

            for (BackendSignedUrlKey urlKey : getSignedUrlKey()) {
                waitForCompletion(
                    client,
                    client.backendServices()
                        .addSignedUrlKey(getProjectId(), getName(), urlKey.toSignedUrlKey())
                        .execute());
            }
        }

        refresh();
    }

    @Override
    public void doUpdate(
        GyroUI ui, State state, Resource current, Set<String> changedFieldNames) throws Exception {

        Compute client = createComputeClient();

        BackendServiceResource currentBackendResource = (BackendServiceResource) current;
        boolean securityPolicyUpdated = false;

        if (changedFieldNames.contains("enable-cdn") && getEnableCdn()
            && currentBackendResource.getSecurityPolicy() != null) {
            saveSecurityPolicy(client);
            securityPolicyUpdated = true;
        }

        BackendService backendService = getBackendService(changedFieldNames);
        Operation operation = client.backendServices().patch(getProjectId(), getName(), backendService).execute();
        waitForCompletion(client, operation);

        if (changedFieldNames.contains("port-name")) {
            backendService.setPortName(getPortName());
        }

        if (changedFieldNames.contains("signed-url-key")) {
            // delete old keys
            List<String> deleteSignedUrlKeys = currentBackendResource.getSignedUrlKey().stream().map(
                BackendSignedUrlKey::getKey).collect(
                Collectors.toList());

            for (String urlKey : deleteSignedUrlKeys) {
                waitForCompletion(
                    client,
                    client.backendServices().deleteSignedUrlKey(getProjectId(), getName(), urlKey).execute());
            }

            // add new keys
            for (BackendSignedUrlKey urlKey : getSignedUrlKey()) {
                waitForCompletion(
                    client,
                    client.backendServices()
                        .addSignedUrlKey(getProjectId(), getName(), urlKey.toSignedUrlKey())
                        .execute());
            }
        }

        if (changedFieldNames.contains("security-policy") && !securityPolicyUpdated) {
            saveSecurityPolicy(client);
        }
    }

    @Override
    public void doDelete(GyroUI ui, State state) throws Exception {
        Compute client = createComputeClient();
        Operation response = client.backendServices().delete(getProjectId(), getName()).execute();
        waitForCompletion(client, response);
    }

    private void saveSecurityPolicy(Compute client) throws Exception {
        SecurityPolicyResource securityPolicyResource = getSecurityPolicy();
        SecurityPolicyReference securityPolicyReference = null;

        if (securityPolicyResource != null) {
            securityPolicyReference = new SecurityPolicyReference();
            securityPolicyReference.set("securityPolicy", securityPolicyResource.getSelfLink());
        }

        Operation securityPolicyOperation = client.backendServices()
            .setSecurityPolicy(getProjectId(), getName(), securityPolicyReference)
            .execute();
        waitForCompletion(client, securityPolicyOperation);
    }

    static boolean isBackendService(String selfLink) {
        return selfLink != null && (ProjectGlobalBackendServiceName.isParsableFrom(formatResource(null, selfLink)));
    }
}
