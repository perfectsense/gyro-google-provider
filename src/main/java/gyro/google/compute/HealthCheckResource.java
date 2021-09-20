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
import com.google.cloud.compute.v1.HealthCheck;
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
        Compute client = createComputeClient();
        HealthCheck healthCheck = client.healthChecks().get(getProjectId(), getName()).execute();
        copyFrom(healthCheck);

        return true;
    }

    @Override
    public void doCreate(GyroUI ui, State state) throws Exception {
        Compute client = createComputeClient();
        HealthCheck healthCheck = getHealthCheck(null);
        Compute.HealthChecks.Insert insert = client.healthChecks().insert(getProjectId(), healthCheck);
        Operation operation = insert.execute();
        waitForCompletion(client, operation);

        refresh();
    }

    @Override
    public void doUpdate(GyroUI ui, State state, Resource current, Set<String> changedFieldNames) throws Exception {
        Compute client = createComputeClient();
        HealthCheck healthCheck = getHealthCheck(changedFieldNames);
        Operation operation = client.healthChecks().patch(getProjectId(), getName(), healthCheck).execute();
        waitForCompletion(client, operation);

        refresh();
    }

    @Override
    public void doDelete(GyroUI ui, State state) throws Exception {
        Compute client = createComputeClient();
        HealthCheck healthCheck = client.healthChecks().get(getProjectId(), getName()).execute();
        Operation operation = client.healthChecks().delete(getProjectId(), healthCheck.getName()).execute();
        waitForCompletion(client, operation);
    }
}
