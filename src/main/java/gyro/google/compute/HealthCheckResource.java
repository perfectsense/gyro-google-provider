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

import com.google.api.gax.rpc.InvalidArgumentException;
import com.google.api.gax.rpc.NotFoundException;
import com.google.cloud.compute.v1.HealthCheck;
import com.google.cloud.compute.v1.HealthChecksClient;
import com.google.cloud.compute.v1.Operation;
import gyro.core.GyroUI;
import gyro.core.Type;
import gyro.core.resource.Resource;
import gyro.core.scope.State;

/**
 * Creates a global health check.
 *
 * Examples
 * --------
 *
 * .. code-block:: gyro
 *
 *      google::compute-health-check health-check-example-http
 *          name: "health-check-example-http"
 *
 *          http-health-check
 *              request-path: "/myapp"
 *          end
 *      end
 *
 * .. code-block:: gyro
 *
 *      google::compute-health-check health-check-example-https
 *          name: "health-check-example-https"
 *          check-interval-sec: 30
 *          description: "health-check-example-description"
 *          healthy-threshold: 8
 *          timeout-sec: 29
 *          unhealthy-threshold: 6
 *
 *          https-health-check
 *              request-path: "/myapp"
 *              response: "okay"
 *          end
 *      end
 *
 * .. code-block:: gyro
 *
 *      google::compute-health-check health-check-example-http2
 *          name: "health-check-example-http2"
 *          check-interval-sec: 30
 *          description: "health-check-example-description"
 *          healthy-threshold: 8
 *          timeout-sec: 29
 *          unhealthy-threshold: 6
 *
 *          http2-health-check
 *              proxy-header: "PROXY_V1"
 *              request-path: "/myapp"
 *              response: "okay"
 *          end
 *      end
 *
 * .. code-block:: gyro
 *
 *      google::compute-health-check health-check-example-tcp
 *          name: "health-check-example-tcp"
 *          check-interval-sec: 30
 *          description: "health-check-example-description"
 *          healthy-threshold: 8
 *          timeout-sec: 29
 *          unhealthy-threshold: 6
 *
 *          tcp-health-check
 *              proxy-header: "PROXY_V1"
 *              request-path: "/myapp"
 *              response: "okay"
 *          end
 *      end
 *
 * .. code-block:: gyro
 *
 *      google::compute-health-check health-check-example-ssh
 *          name: "health-check-example-ssh"
 *          check-interval-sec: 30
 *          description: "health-check-example-description"
 *          healthy-threshold: 8
 *          timeout-sec: 29
 *          unhealthy-threshold: 6
 *
 *          ssh-health-check
 *              port: 501
 *              port-name: "custom-port"
 *              proxy-header: "PROXY_V1"
 *              request-path: "/myapp"
 *              response: "okay"
 *          end
 *      end
 */
@Type("compute-health-check")
public class HealthCheckResource extends AbstractHealthCheckResource {

    @Override
    public boolean doRefresh() throws Exception {
        HealthChecksClient client = createClient(HealthChecksClient.class);
        HealthCheck healthCheck = getHealthCheckResource(client);

        if (healthCheck == null) {
            return false;
        }

        copyFrom(healthCheck);
        client.close();
        return true;
    }

    @Override
    public void doCreate(GyroUI ui, State state) throws Exception {
        try (HealthChecksClient client = createClient(HealthChecksClient.class)) {
            HealthCheck healthCheck = getHealthCheck(null);
            Operation operation = client.insert(getProjectId(), healthCheck);
            waitForCompletion(operation);
        }

        refresh();
    }

    @Override
    public void doUpdate(GyroUI ui, State state, Resource current, Set<String> changedFieldNames) throws Exception {
        try (HealthChecksClient client = createClient(HealthChecksClient.class)) {
            HealthCheck healthCheck = getHealthCheck(changedFieldNames);
            Operation operation = client.patch(getProjectId(), getName(), healthCheck);
            waitForCompletion(operation);
        }

        refresh();
    }

    @Override
    public void doDelete(GyroUI ui, State state) throws Exception {
        try (HealthChecksClient client = createClient(HealthChecksClient.class)) {
            HealthCheck healthCheck = client.get(getProjectId(), getName());
            Operation operation = client.delete(getProjectId(), healthCheck.getName());
            waitForCompletion(operation);
        }
    }

    private HealthCheck getHealthCheckResource(HealthChecksClient client) {
        HealthCheck healthCheck = null;

        try {
            healthCheck = client.get(getProjectId(), getName());
        } catch (NotFoundException | InvalidArgumentException ex) {
            // ignore
        }

        return healthCheck;
    }
}
