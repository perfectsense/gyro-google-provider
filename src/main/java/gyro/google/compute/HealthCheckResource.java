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
 * Creates a health check in the Global scope.
 * Note: Regional scopes are not currently supported in the v1 API.
 *
 * Example
 * -------
 *
 * .. code-block:: gyro
 *
 *     google::healthcheck healthcheck-example
 *         check-interval-sec: 30
 *         description: "The description goes here."
 *         healthy-threshold: 8
 *         name: "foo"
 *         timeout-sec: 29
 *         type: "HTTPS"
 *         unhealthy-threshold: 6
 *         https-health-check
 *             host: "myapp.example.com"
 *             port: 440
 *             port-specification: "USE_FIXED_PORT"
 *             proxy-header: "PROXY_V1"
 *             request-path: "/myapp"
 *             response: "okay"
 *         end
 *     end
 */
@Type("healthcheck")
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
     * Name of the resource. Provided by the client when the resource is created. The name must be 1-63 characters long,
     * and comply with RFC1035. Specifically, the name must be 1-63 characters long and match the regular expression
     * [a-z]([-a-z0-9]*[a-z0-9])? which means the first character must be a lowercase letter, and all following
     * characters must be a dash, lowercase letter, or digit, except the last character, which cannot be a dash. (Required)
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
     * An optional description of the health check.
     */
    @Updatable
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * How often (in seconds) to send a health check. The default value is 5 seconds.
     */
    @Updatable
    public Integer getCheckIntervalSec() {
        return checkIntervalSec;
    }

    public void setCheckIntervalSec(Integer checkIntervalSec) {
        this.checkIntervalSec = checkIntervalSec;
    }

    /**
     * The HTTP Health Check.
     *
     *  @subresource gyro.google.compute.HealthCheckHttpHealthCheck
     */
    @Updatable
    public HealthCheckHttpHealthCheck getHttpHealthCheck() {
        return httpHealthCheck;
    }

    public void setHttpHealthCheck(HealthCheckHttpHealthCheck httpHealthCheck) {
        this.httpHealthCheck = httpHealthCheck;
    }

    /**
     * The HTTPS Health Check.
     *
     *  @subresource gyro.google.compute.HealthCheckHttpsHealthCheck
     */
    @Updatable
    public HealthCheckHttpsHealthCheck getHttpsHealthCheck() {
        return httpsHealthCheck;
    }

    public void setHttpsHealthCheck(HealthCheckHttpsHealthCheck httpsHealthCheck) {
        this.httpsHealthCheck = httpsHealthCheck;
    }

    /**
     * The HTTP2 Health Check.
     *
     *  @subresource gyro.google.compute.HealthCheckHttp2HealthCheck
     */
    @Updatable
    public HealthCheckHttp2HealthCheck getHttp2HealthCheck() {
        return http2HealthCheck;
    }

    public void setHttp2HealthCheck(HealthCheckHttp2HealthCheck http2HealthCheck) {
        this.http2HealthCheck = http2HealthCheck;
    }

    /**
     * The SSL Health Check.
     *
     *  @subresource gyro.google.compute.HealthCheckSslHealthCheck
     */
    @Updatable
    public HealthCheckSslHealthCheck getSslHealthCheck() {
        return sslHealthCheck;
    }

    public void setSslHealthCheck(HealthCheckSslHealthCheck sslHealthCheck) {
        this.sslHealthCheck = sslHealthCheck;
    }

    /**
     * The TCP Health Check.
     *
     *  @subresource gyro.google.compute.HealthCheckTcpHealthCheck
     */
    @Updatable
    public HealthCheckTcpHealthCheck getTcpHealthCheck() {
        return tcpHealthCheck;
    }

    public void setTcpHealthCheck(HealthCheckTcpHealthCheck tcpHealthCheck) {
        this.tcpHealthCheck = tcpHealthCheck;
    }

    /**
     * How long (in seconds) to wait before claiming failure. The default value is 5 seconds.
     * It is invalid for timeoutSec to have greater value than checkIntervalSec.
     */
    @Updatable
    public Integer getTimeoutSec() {
        return timeoutSec;
    }

    public void setTimeoutSec(Integer timeoutSec) {
        this.timeoutSec = timeoutSec;
    }

    /**
     * Specifies the type of the healthCheck, either TCP, SSL, HTTP, HTTPS or HTTP2. If not specified, the default is TCP.
     * Exactly one of the protocol-specific health check field must be specified, which must match type field. (Required)
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
     * A so-far healthy instance will be marked unhealthy after this many consecutive failures. The default value is 2.
     */
    @Updatable
    public Integer getUnhealthyThreshold() {
        return unhealthyThreshold;
    }

    public void setUnhealthyThreshold(Integer unhealthyThreshold) {
        this.unhealthyThreshold = unhealthyThreshold;
    }

    /**
     * A so-far unhealthy instance will be marked healthy after this many consecutive successes. The default value is 2.
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
        if (getHttpHealthCheck() != null) {
            HealthCheckHttpHealthCheck httpHealthCheck = newSubresource(HealthCheckHttpHealthCheck.class);
            httpHealthCheck.copyFrom(healthCheck);
            setHttpHealthCheck(httpHealthCheck);
        }
        if (getHttpsHealthCheck() != null) {
            HealthCheckHttpsHealthCheck httpsHealthCheck = newSubresource(HealthCheckHttpsHealthCheck.class);
            httpsHealthCheck.copyFrom(healthCheck);
            setHttpsHealthCheck(httpsHealthCheck);
        }
        if (getHttp2HealthCheck() != null) {
            HealthCheckHttp2HealthCheck http2HealthCheck = newSubresource(HealthCheckHttp2HealthCheck.class);
            http2HealthCheck.copyFrom(healthCheck);
            setHttp2HealthCheck(http2HealthCheck);
        }
        if (getSslHealthCheck() != null) {
            HealthCheckSslHealthCheck sslHealthCheck = newSubresource(HealthCheckSslHealthCheck.class);
            sslHealthCheck.copyFrom(healthCheck);
            setSslHealthCheck(sslHealthCheck);
        }
        if (getTcpHealthCheck() != null) {
            HealthCheckTcpHealthCheck tcpHealthCheck = newSubresource(HealthCheckTcpHealthCheck.class);
            tcpHealthCheck.copyFrom(healthCheck);
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
            client.healthChecks().update(getProjectId(), getName(), healthCheck).execute();
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
