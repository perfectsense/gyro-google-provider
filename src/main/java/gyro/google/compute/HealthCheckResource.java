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
import com.google.api.client.util.Data;
import com.google.api.services.compute.Compute;
import com.google.api.services.compute.model.*;
import gyro.core.GyroException;
import gyro.core.GyroUI;
import gyro.core.Type;
import gyro.core.resource.Id;
import gyro.core.resource.Output;
import gyro.core.resource.Resource;
import gyro.core.resource.Updatable;
import gyro.core.scope.State;
import gyro.core.validation.Regex;
import gyro.core.validation.Required;
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
 *              request-path: "/myapp"
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
    private String selfLink;
    private Integer timeoutSec;
    private String type;
    private Integer unhealthyThreshold;

    /**
     * The name of the health check. (Required)
     */
    @Id
    @Required
    @Regex("(?:(?:[-a-z0-9]{1,63}\\.)*(?:[a-z](?:[-a-z0-9]{0,61}[a-z0-9])?):)?(?:[0-9]{1,19}|(?:[a-z0-9](?:[-a-z0-9]{0,61}[a-z0-9])?))")
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
     * The time (in seconds) for how often to send a health check.
     */
    @Updatable
    public Integer getCheckIntervalSec() {
        return checkIntervalSec;
    }

    public void setCheckIntervalSec(Integer checkIntervalSec) {
        this.checkIntervalSec = checkIntervalSec;
    }

    /**
     * The http Health Check type.
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
     * The https Health Check type.
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
     * The http2 Health Check type.
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
     * The ssl Health Check type.
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
     * The tcp Health Check type.
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
     * The time (in seconds) to wait before claiming failure.
     */
    @Updatable
    public Integer getTimeoutSec() {
        return timeoutSec;
    }

    public void setTimeoutSec(Integer timeoutSec) {
        this.timeoutSec = timeoutSec;
    }

    /**
     * The type of health check. Valid values are: ``TCP``, ``SSL``, ``HTTP``, ``HTTPS`` or ``HTTP2``.
     */
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    /**
     * The number of consecutive failures before marking unhealthy.
     */
    @Updatable
    public Integer getUnhealthyThreshold() {
        return unhealthyThreshold;
    }

    public void setUnhealthyThreshold(Integer unhealthyThreshold) {
        this.unhealthyThreshold = unhealthyThreshold;
    }

    /**
     * The number of consecutive successes before marking healthy.
     */
    @Updatable
    public Integer getHealthyThreshold() {
        return healthyThreshold;
    }

    public void setHealthyThreshold(Integer healthyThreshold) {
        this.healthyThreshold = healthyThreshold;
    }

    /**
     * The server-defined URL for the health check.
     */
    @Output
    public String getSelfLink() {
        return selfLink;
    }

    public void setSelfLink(String selfLink) {
        this.selfLink = selfLink;
    }

    @Override
    public void copyFrom(HealthCheck healthCheck) {
        setName(healthCheck.getName());
        setDescription(healthCheck.getDescription());
        setCheckIntervalSec(healthCheck.getCheckIntervalSec());
        setTimeoutSec(healthCheck.getTimeoutSec());
        setUnhealthyThreshold(healthCheck.getUnhealthyThreshold());
        setHealthyThreshold(healthCheck.getHealthyThreshold());
        setSelfLink(healthCheck.getSelfLink());
        setType(healthCheck.getType());

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
            if (je.getDetails().getCode() == 404) {
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
        healthCheck.setUnhealthyThreshold(getUnhealthyThreshold());
        healthCheck.setHealthyThreshold(getHealthyThreshold());

        if (getHttpHealthCheck() != null) {
            healthCheck.setType(getHttpHealthCheck().getType());
            healthCheck.setHttpHealthCheck(getHttpHealthCheck().toHttpHealthCheck());
        }

        if (getHttpsHealthCheck() != null) {
            healthCheck.setType(getHttpsHealthCheck().getType());
            healthCheck.setHttpsHealthCheck(getHttpsHealthCheck().toHttpsHealthCheck());
        }

        if (getHttp2HealthCheck() != null) {
            healthCheck.setType(getHttp2HealthCheck().getType());
            healthCheck.setHttp2HealthCheck(getHttp2HealthCheck().toHttp2HealthCheck());
        }

        if (getSslHealthCheck() != null) {
            healthCheck.setType(getSslHealthCheck().getType());
            healthCheck.setSslHealthCheck(getSslHealthCheck().toSslHealthCheck());
        }

        if (getTcpHealthCheck() != null) {
            healthCheck.setType(getTcpHealthCheck().getType());
            healthCheck.setTcpHealthCheck(getTcpHealthCheck().toTcpHealthCheck());
        }

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
            HealthCheck healthCheck = new HealthCheck();
            if (changedFieldNames.contains("check-interval-sec")) {
                healthCheck.setCheckIntervalSec(getCheckIntervalSec());
            }

            if (changedFieldNames.contains("description")) {
                healthCheck.setDescription(getDescription());
            }

            if (changedFieldNames.contains("healthy-threshold")) {
                healthCheck.setHealthyThreshold(getHealthyThreshold());
            }

            if (changedFieldNames.contains("timeout-sec")) {
                healthCheck.setTimeoutSec(getTimeoutSec());
            }

            if (changedFieldNames.contains("unhealthy-threshold")) {
                healthCheck.setUnhealthyThreshold(getUnhealthyThreshold());
            }

            healthCheck.setHttpsHealthCheck(Data.nullOf(HTTPSHealthCheck.class));
            healthCheck.setHttpHealthCheck(Data.nullOf(HTTPHealthCheck.class));
            healthCheck.setHttp2HealthCheck(Data.nullOf(HTTP2HealthCheck.class));
            healthCheck.setSslHealthCheck(Data.nullOf(SSLHealthCheck.class));
            healthCheck.setTcpHealthCheck(Data.nullOf(TCPHealthCheck.class));

            if (getHttpHealthCheck() != null) {
                healthCheck.setType(getHttpHealthCheck().getType());
                healthCheck.setHttpHealthCheck(getHttpHealthCheck().toHttpHealthCheck());
            }

            if (getHttpsHealthCheck() != null) {
                healthCheck.setType(getHttpsHealthCheck().getType());
                healthCheck.setHttpsHealthCheck(getHttpsHealthCheck().toHttpsHealthCheck());
            }

            if (getHttp2HealthCheck() != null) {
                healthCheck.setType(getHttp2HealthCheck().getType());
                healthCheck.setHttp2HealthCheck(getHttp2HealthCheck().toHttp2HealthCheck());
            }

            if (getSslHealthCheck() != null) {
                healthCheck.setType(getSslHealthCheck().getType());
                healthCheck.setSslHealthCheck(getSslHealthCheck().toSslHealthCheck());
            }

            if (getTcpHealthCheck() != null) {
                healthCheck.setType(getTcpHealthCheck().getType());
                healthCheck.setTcpHealthCheck(getTcpHealthCheck().toTcpHealthCheck());
            }

            Operation operation = client.healthChecks().patch(getProjectId(), getName(), healthCheck).execute();
            Operation.Error error = waitForCompletion(client, operation);
            if (error != null) {
                throw new GyroException(error.toPrettyString());
            }
        } catch (GoogleJsonResponseException ex) {
            throw new GyroException(ex.getMessage(), ex.getCause());
        }
    }

    @Override
    public void delete(GyroUI ui, State state) throws Exception {
        try {
            Compute client = createComputeClient();
            HealthCheck healthCheck = client.healthChecks().get(getProjectId(), getName()).execute();
            client.healthChecks().delete(getProjectId(), healthCheck.getName()).execute();
        } catch (GoogleJsonResponseException e) {
            throw new GyroException(String.format("Unable to delete Health Check: %s, Google error: %s", getName(), e.getMessage()));
        }
    }
}
