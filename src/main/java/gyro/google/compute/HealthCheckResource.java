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

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.compute.Compute;
import com.google.api.services.compute.model.HealthCheck;
import com.google.api.services.compute.model.Operation;
import gyro.core.GyroException;
import gyro.core.GyroUI;
import gyro.core.Type;
import gyro.core.resource.Id;
import gyro.core.resource.Resource;
import gyro.core.resource.Updatable;
import gyro.core.scope.State;
import gyro.core.validation.Required;
import gyro.core.validation.ValidStrings;
import gyro.google.Copyable;

import java.io.IOException;
import java.util.Set;

/**
 * Creates a health check resource.
 * Health checks are scoped Globally by default.
 * Note: Regional scopes are not yet supported in the V1 API.
 *
 * ========
 * Examples
 * ========
 *
 * Basic Http Health Check
 * -----------------------
 *
 * .. code-block:: gyro
 *
 *      google::compute-health-check health-check-example
 *          name: "http-basic"
 *          type: "HTTP"
 *
 *          http-health-check
 *          end
 *      end
 *
 * Advanced TCP Health Check
 * -------------------------
 *
 * .. code-block:: gyro
 *
 *      google::compute-health-check health-check-example
 *          check-interval-sec: 30
 *          description: "The description goes here."
 *          healthy-threshold: 8
 *          name: "tcp-advanced"
 *          timeout-sec: 29
 *          type: "TCP"
 *          unhealthy-threshold: 6
 *
 *          tcp-health-check
 *              proxy-header: "PROXY_V1"
 *              request-path: "/myapp"
 *              response: "okay"
 *          end
 *      end
 *
 * Advanced TCP Health Check with Custom Port Name
 * -----------------------------------------------
 *
 * .. code-block:: gyro
 *
 *      google::compute-health-check health-check-example
 *          check-interval-sec: 30
 *          description: "The description goes here."
 *          healthy-threshold: 8
 *          name: "tcp-advanced"
 *          timeout-sec: 29
 *          type: "TCP"
 *          unhealthy-threshold: 6
 *
 *          tcp-health-check
 *              port: 501
 *              port-name: "custom-port"
 *              proxy-header: "PROXY_V1"
 *              request-path: "/myapp"
 *              response: "okay"
 *          end
 *      end
 */
@Type("compute-health-check")
public class HealthCheckResource extends ComputeResource implements Copyable<HealthCheck> {
    private Integer checkIntervalSec;
    private String description;
    private HealthCheckHttpHealthCheck httpHealthCheck;
    private HealthCheckHttpsHealthCheck httpsHealthCheck;
    private HealthCheckHttp2HealthCheck http2HealthCheck;
    private HealthCheckSslHealthCheck sslHealthCheck;
    private HealthCheckTcpHealthCheck tcpHealthCheck;
    private Integer healthyThreshold;
    private String name;
    private Integer timeoutSec;
    private String type;
    private Integer unhealthyThreshold;

    /**
     * The name of the health check. The name must be 1-63 characters long. See `RFC1035 <https://www.ietf.org/rfc/rfc1035.txt/>`_. (Required)
     */
    @Id
    @Required
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * The description of the health check.
     */
    @Updatable
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * The time (in seconds) for how often to send a health check. The default value is 5 seconds.
     */
    @Updatable
    public Integer getCheckIntervalSec() {
        return checkIntervalSec;
    }

    public void setCheckIntervalSec(Integer checkIntervalSec) {
        this.checkIntervalSec = checkIntervalSec;
    }

    /**
     * The HTTP Health Check type.
     *
     * @subresource gyro.google.compute.HealthCheckHttpHealthCheck
     */
    @Updatable
    public HealthCheckHttpHealthCheck getHttpHealthCheck() {
        return httpHealthCheck;
    }

    public void setHttpHealthCheck(HealthCheckHttpHealthCheck httpHealthCheck) {
        this.httpHealthCheck = httpHealthCheck;
    }

    /**
     * The HTTPS Health Check type.
     *
     * @subresource gyro.google.compute.HealthCheckHttpsHealthCheck
     */
    @Updatable
    public HealthCheckHttpsHealthCheck getHttpsHealthCheck() {
        return httpsHealthCheck;
    }

    public void setHttpsHealthCheck(HealthCheckHttpsHealthCheck httpsHealthCheck) {
        this.httpsHealthCheck = httpsHealthCheck;
    }

    /**
     * The HTTP2 Health Check type.
     *
     * @subresource gyro.google.compute.HealthCheckHttp2HealthCheck
     */
    @Updatable
    public HealthCheckHttp2HealthCheck getHttp2HealthCheck() {
        return http2HealthCheck;
    }

    public void setHttp2HealthCheck(HealthCheckHttp2HealthCheck http2HealthCheck) {
        this.http2HealthCheck = http2HealthCheck;
    }

    /**
     * The SSL Health Check type.
     *
     * @subresource gyro.google.compute.HealthCheckSslHealthCheck
     */
    @Updatable
    public HealthCheckSslHealthCheck getSslHealthCheck() {
        return sslHealthCheck;
    }

    public void setSslHealthCheck(HealthCheckSslHealthCheck sslHealthCheck) {
        this.sslHealthCheck = sslHealthCheck;
    }

    /**
     * The TCP Health Check type.
     *
     * @subresource gyro.google.compute.HealthCheckTcpHealthCheck
     */
    @Updatable
    public HealthCheckTcpHealthCheck getTcpHealthCheck() {
        return tcpHealthCheck;
    }

    public void setTcpHealthCheck(HealthCheckTcpHealthCheck tcpHealthCheck) {
        this.tcpHealthCheck = tcpHealthCheck;
    }

    /**
     * The time (in seconds) to wait before claiming failure. The default value is 5 seconds.
     */
    @Updatable
    public Integer getTimeoutSec() {
        return timeoutSec;
    }

    public void setTimeoutSec(Integer timeoutSec) {
        this.timeoutSec = timeoutSec;
    }

    /**
     * The type of health check. Valid values are: ``TCP, SSL, HTTP, HTTPS or HTTP2``. If not specified, the default is ``TCP``. (Required)
     */
    @Required
    @Updatable
    @ValidStrings({"HTTP", "HTTPS", "HTTP2", "SSL", "TCP"})
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    /**
     * The number of consecutive failures before marking unhealthy. The default value is 2.
     */
    @Updatable
    public Integer getUnhealthyThreshold() {
        return unhealthyThreshold;
    }

    public void setUnhealthyThreshold(Integer unhealthyThreshold) {
        this.unhealthyThreshold = unhealthyThreshold;
    }

    /**
     * The number of consecutive successes before marking healthy. The default value is 2.
     */
    @Updatable
    public Integer getHealthyThreshold() {
        return healthyThreshold;
    }

    public void setHealthyThreshold(Integer healthyThreshold) {
        this.healthyThreshold = healthyThreshold;
    }

    @Override
    public void copyFrom(HealthCheck healthCheck) {
        setName(healthCheck.getName());
        setDescription(healthCheck.getDescription());
        setCheckIntervalSec(healthCheck.getCheckIntervalSec());
        setTimeoutSec(healthCheck.getTimeoutSec());
        setType(healthCheck.getType());
        setUnhealthyThreshold(healthCheck.getUnhealthyThreshold());
        setHealthyThreshold(healthCheck.getHealthyThreshold());
        if (healthCheck.getHttpHealthCheck() != null) {
            HealthCheckHttpHealthCheck httpHealthCheck = newSubresource(HealthCheckHttpHealthCheck.class);
            httpHealthCheck.copyFrom(healthCheck.getHttpHealthCheck());
            setHttpHealthCheck(httpHealthCheck);
        }
        if (healthCheck.getHttpsHealthCheck() != null) {
            HealthCheckHttpsHealthCheck httpsHealthCheck = newSubresource(HealthCheckHttpsHealthCheck.class);
            httpsHealthCheck.copyFrom(healthCheck.getHttpsHealthCheck());
            setHttpsHealthCheck(httpsHealthCheck);
        }
        if (healthCheck.getHttp2HealthCheck() != null) {
            HealthCheckHttp2HealthCheck http2HealthCheck = newSubresource(HealthCheckHttp2HealthCheck.class);
            http2HealthCheck.copyFrom(healthCheck.getHttp2HealthCheck());
            setHttp2HealthCheck(http2HealthCheck);
        }
        if (healthCheck.getSslHealthCheck() != null) {
            HealthCheckSslHealthCheck sslHealthCheck = newSubresource(HealthCheckSslHealthCheck.class);
            sslHealthCheck.copyFrom(healthCheck.getSslHealthCheck());
            setSslHealthCheck(sslHealthCheck);
        }
        if (healthCheck.getTcpHealthCheck() != null) {
            HealthCheckTcpHealthCheck tcpHealthCheck = newSubresource(HealthCheckTcpHealthCheck.class);
            tcpHealthCheck.copyFrom(healthCheck.getTcpHealthCheck());
            setTcpHealthCheck(tcpHealthCheck);
        }
    }

    @Override
    public boolean refresh() {
        Compute client = createComputeClient();
        try {
            HealthCheck healthCheck = client.healthChecks().get(getProjectId(), getName()).execute();
            copyFrom(healthCheck);
            return true;
        } catch (GoogleJsonResponseException je) {
            if (je.getDetails().getMessage().matches("The resource (.*) was not found")) {
                return false;
            } else {
                throw new GyroException(je.getDetails().getMessage());
            }
        } catch (IOException ex) {
            throw new GyroException(ex.getMessage(), ex.getCause());
        }
    }

    @Override
    public void create(GyroUI ui, State state) throws Exception {
        Compute client = createComputeClient();
        HealthCheck healthCheck = new HealthCheck();
        healthCheck.setName(getName());
        healthCheck.setDescription(getDescription());
        healthCheck.setCheckIntervalSec(getCheckIntervalSec());
        healthCheck.setTimeoutSec(getTimeoutSec());
        healthCheck.setType(getType());
        healthCheck.setUnhealthyThreshold(getUnhealthyThreshold());
        healthCheck.setHealthyThreshold(getHealthyThreshold());
        healthCheck.setHttpHealthCheck(getHttpHealthCheck() == null ? null : getHttpHealthCheck().toHttpHealthCheck());
        healthCheck.setHttpsHealthCheck(getHttpsHealthCheck() == null ? null : getHttpsHealthCheck().toHttpsHealthCheck());
        healthCheck.setHttp2HealthCheck(getHttp2HealthCheck() == null ? null : getHttp2HealthCheck().toHttp2HealthCheck());
        healthCheck.setSslHealthCheck(getSslHealthCheck() == null ? null : getSslHealthCheck().toSslHealthCheck());
        healthCheck.setTcpHealthCheck(getTcpHealthCheck() == null ? null : getTcpHealthCheck().toTcpHealthCheck());

        try {
            Compute.HealthChecks.Insert insert = client.healthChecks().insert(getProjectId(), healthCheck);
            Operation operation = insert.execute();
            Operation.Error error = waitForCompletion(client, operation);
            if (error != null) {
                throw new GyroException(error.toPrettyString());
            }

            refresh();
        } catch (Exception ex) {
            throw new GyroException(ex.getMessage(), ex.getCause());
        }
    }

    @Override
    public void update(GyroUI ui, State state, Resource current, Set<String> changedFieldNames) throws Exception {
        Compute client = createComputeClient();
        try {
            HealthCheck healthCheck = client.healthChecks().get(getProjectId(), getName()).execute();
            healthCheck.setDescription(getDescription());
            healthCheck.setCheckIntervalSec(getCheckIntervalSec());
            healthCheck.setTimeoutSec(getTimeoutSec());
            healthCheck.setType(getType());
            healthCheck.setUnhealthyThreshold(getUnhealthyThreshold());
            healthCheck.setHealthyThreshold(getHealthyThreshold());
            healthCheck.setHttpHealthCheck(getHttpHealthCheck() == null ? null : getHttpHealthCheck().toHttpHealthCheck());
            healthCheck.setHttpsHealthCheck(getHttpsHealthCheck() == null ? null : getHttpsHealthCheck().toHttpsHealthCheck());
            healthCheck.setHttp2HealthCheck(getHttp2HealthCheck() == null ? null : getHttp2HealthCheck().toHttp2HealthCheck());
            healthCheck.setSslHealthCheck(getSslHealthCheck() == null ? null : getSslHealthCheck().toSslHealthCheck());
            healthCheck.setTcpHealthCheck(getTcpHealthCheck() == null ? null : getTcpHealthCheck().toTcpHealthCheck());
            client.healthChecks().patch(getProjectId(), getName(), healthCheck).execute();
        } catch (IOException ex) {
            throw new GyroException(ex.getMessage(), ex.getCause());
        }
    }

    @Override
    public void delete(GyroUI ui, State state) throws Exception {
        try {
            Compute client = createComputeClient();
            HealthCheck healthCheck = client.healthChecks().get(getProjectId(), getName()).execute();
            client.healthChecks().delete(getProjectId(), healthCheck.getName()).execute();
        } catch (IOException e) {
            throw new GyroException(String.format("Unable to delete Health Check: %s, Google error: %s", getName(), e.getMessage()));
        }
    }
}
