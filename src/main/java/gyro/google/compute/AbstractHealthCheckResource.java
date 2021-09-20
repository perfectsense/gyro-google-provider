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

import com.google.api.client.util.Data;
import com.google.cloud.compute.v1.HTTP2HealthCheck;
import com.google.cloud.compute.v1.HTTPHealthCheck;
import com.google.cloud.compute.v1.HTTPSHealthCheck;
import com.google.cloud.compute.v1.HealthCheck;
import com.google.cloud.compute.v1.SSLHealthCheck;
import com.google.cloud.compute.v1.TCPHealthCheck;
import gyro.core.resource.Id;
import gyro.core.resource.Output;
import gyro.core.resource.Updatable;
import gyro.core.validation.ConflictsWith;
import gyro.core.validation.Regex;
import gyro.core.validation.Required;
import gyro.core.validation.ValidStrings;
import gyro.google.Copyable;

public abstract class AbstractHealthCheckResource extends ComputeResource implements Copyable<HealthCheck> {

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
    private HealthCheck.Type type;
    private Integer unhealthyThreshold;

    /**
     * The name of the health check.
     */
    @Required
    @Regex(value = "(?:(?:[-a-z0-9]{1,63}\\.)*(?:[a-z](?:[-a-z0-9]{0,61}[a-z0-9])?):)?(?:[0-9]{1,19}|(?:[a-z0-9](?:[-a-z0-9]{0,61}[a-z0-9])?))", message = "a string 1-63 characters long and the first character must be a lowercase letter, and all following characters must be a dash, lowercase letter, or digit, except the last character, which cannot be a dash")
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
     * The http health check type.
     *
     * @subresource gyro.google.compute.HealthCheckHttpHealthCheck
     */
    @Updatable
    @ConflictsWith({ "https-health-check", "http2-health-check", "ssl-health-check", "tcp-health-check" })
    public HealthCheckHttpHealthCheck getHttpHealthCheck() {
        return httpHealthCheck;
    }

    public void setHttpHealthCheck(HealthCheckHttpHealthCheck httpHealthCheck) {
        this.httpHealthCheck = httpHealthCheck;
    }

    /**
     * The https health check type.
     *
     * @subresource gyro.google.compute.HealthCheckHttpsHealthCheck
     */
    @Updatable
    @ConflictsWith({ "http-health-check", "http2-health-check", "ssl-health-check", "tcp-health-check" })
    public HealthCheckHttpsHealthCheck getHttpsHealthCheck() {
        return httpsHealthCheck;
    }

    public void setHttpsHealthCheck(HealthCheckHttpsHealthCheck httpsHealthCheck) {
        this.httpsHealthCheck = httpsHealthCheck;
    }

    /**
     * The http2 health check type.
     *
     * @subresource gyro.google.compute.HealthCheckHttp2HealthCheck
     */
    @Updatable
    @ConflictsWith({ "https-health-check", "http-health-check", "ssl-health-check", "tcp-health-check" })
    public HealthCheckHttp2HealthCheck getHttp2HealthCheck() {
        return http2HealthCheck;
    }

    public void setHttp2HealthCheck(HealthCheckHttp2HealthCheck http2HealthCheck) {
        this.http2HealthCheck = http2HealthCheck;
    }

    /**
     * The ssl health check type.
     *
     * @subresource gyro.google.compute.HealthCheckSslHealthCheck
     */
    @Updatable
    @ConflictsWith({ "https-health-check", "http2-health-check", "http-health-check", "tcp-health-check" })
    public HealthCheckSslHealthCheck getSslHealthCheck() {
        return sslHealthCheck;
    }

    public void setSslHealthCheck(HealthCheckSslHealthCheck sslHealthCheck) {
        this.sslHealthCheck = sslHealthCheck;
    }

    /**
     * The tcp health check type.
     *
     * @subresource gyro.google.compute.HealthCheckTcpHealthCheck
     */
    @Updatable
    @ConflictsWith({ "https-health-check", "http2-health-check", "ssl-health-check", "http-health-check" })
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
     * The type of health check.
     */
    @ValidStrings({ "TCP", "SSL", "HTTP", "HTTPS", "HTTP2" })
    public HealthCheck.Type getType() {
        return type;
    }

    public void setType(HealthCheck.Type type) {
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
    @Id
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

        setHttpHealthCheck(null);
        if (healthCheck.getHttpHealthCheck() != null) {
            HealthCheckHttpHealthCheck httpHealthCheck = newSubresource(HealthCheckHttpHealthCheck.class);
            httpHealthCheck.copyFrom(healthCheck.getHttpHealthCheck());
            setHttpHealthCheck(httpHealthCheck);
        }

        setHttpsHealthCheck(null);
        if (healthCheck.getHttpsHealthCheck() != null) {
            HealthCheckHttpsHealthCheck httpsHealthCheck = newSubresource(HealthCheckHttpsHealthCheck.class);
            httpsHealthCheck.copyFrom(healthCheck.getHttpsHealthCheck());
            setHttpsHealthCheck(httpsHealthCheck);
        }

        setHttp2HealthCheck(null);
        if (healthCheck.getHttp2HealthCheck() != null) {
            HealthCheckHttp2HealthCheck http2HealthCheck = newSubresource(HealthCheckHttp2HealthCheck.class);
            http2HealthCheck.copyFrom(healthCheck.getHttp2HealthCheck());
            setHttp2HealthCheck(http2HealthCheck);
        }

        setSslHealthCheck(null);
        if (healthCheck.getSslHealthCheck() != null) {
            HealthCheckSslHealthCheck sslHealthCheck = newSubresource(HealthCheckSslHealthCheck.class);
            sslHealthCheck.copyFrom(healthCheck.getSslHealthCheck());
            setSslHealthCheck(sslHealthCheck);
        }

        setTcpHealthCheck(null);
        if (healthCheck.getTcpHealthCheck() != null) {
            HealthCheckTcpHealthCheck tcpHealthCheck = newSubresource(HealthCheckTcpHealthCheck.class);
            tcpHealthCheck.copyFrom(healthCheck.getTcpHealthCheck());
            setTcpHealthCheck(tcpHealthCheck);
        }
    }

    public HealthCheck getHealthCheck(Set<String> changedFieldNames) {
        boolean isUpdate = changedFieldNames != null && (changedFieldNames.size() > 0);

        HealthCheck.Builder healthCheck = HealthCheck.newBuilder();

        if (!isUpdate) {
            healthCheck.setName(getName());
        }

        if (!isUpdate || changedFieldNames.contains("check-interval-sec")) {
            healthCheck.setCheckIntervalSec(getCheckIntervalSec());
        }

        if (!isUpdate || changedFieldNames.contains("description")) {
            healthCheck.setDescription(getDescription());
        }

        if (!isUpdate || changedFieldNames.contains("healthy-threshold")) {
            healthCheck.setHealthyThreshold(getHealthyThreshold());
        }

        if (!isUpdate || changedFieldNames.contains("timeout-sec")) {
            healthCheck.setTimeoutSec(getTimeoutSec());
        }

        if (!isUpdate || changedFieldNames.contains("unhealthy-threshold")) {
            healthCheck.setUnhealthyThreshold(getUnhealthyThreshold());
        }

        healthCheck.setHttpHealthCheck(Data.nullOf(HTTPHealthCheck.class));
        if (getHttpHealthCheck() != null) {
            healthCheck.setType(getHttpHealthCheck().getType());
            healthCheck.setHttpHealthCheck(getHttpHealthCheck().toHttpHealthCheck());
        }

        healthCheck.setHttpsHealthCheck(Data.nullOf(HTTPSHealthCheck.class));
        if (getHttpsHealthCheck() != null) {
            healthCheck.setType(getHttpsHealthCheck().getType());
            healthCheck.setHttpsHealthCheck(getHttpsHealthCheck().toHttpsHealthCheck());
        }

        healthCheck.setHttp2HealthCheck(Data.nullOf(HTTP2HealthCheck.class));
        if (getHttp2HealthCheck() != null) {
            healthCheck.setType(getHttp2HealthCheck().getType());
            healthCheck.setHttp2HealthCheck(getHttp2HealthCheck().toHttp2HealthCheck());
        }

        healthCheck.setSslHealthCheck(Data.nullOf(SSLHealthCheck.class));
        if (getSslHealthCheck() != null) {
            healthCheck.setType(getSslHealthCheck().getType());
            healthCheck.setSslHealthCheck(getSslHealthCheck().toSslHealthCheck());
        }

        healthCheck.setTcpHealthCheck(Data.nullOf(TCPHealthCheck.class));
        if (getTcpHealthCheck() != null) {
            healthCheck.setType(getTcpHealthCheck().getType());
            healthCheck.setTcpHealthCheck(getTcpHealthCheck().toTcpHealthCheck());
        }

        return healthCheck.build();
    }
}
