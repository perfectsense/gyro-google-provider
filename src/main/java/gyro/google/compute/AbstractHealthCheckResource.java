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

import com.google.cloud.compute.v1.HealthCheck;
import gyro.core.resource.Id;
import gyro.core.resource.Output;
import gyro.core.resource.Updatable;
import gyro.core.validation.ConflictsWith;
import gyro.core.validation.Regex;
import gyro.core.validation.Required;
import gyro.core.validation.ValidStrings;
import gyro.google.Copyable;

public abstract class AbstractHealthCheckResource extends ComputeResource implements Copyable<HealthCheck> {

    private String name;
    private String description;
    private Integer checkIntervalSec;
    private Integer timeoutSec;
    private HealthCheck.Type type;
    private HealthCheckHttpHealthCheck httpHealthCheck;
    private HealthCheckHttpsHealthCheck httpsHealthCheck;
    private HealthCheckHttp2HealthCheck http2HealthCheck;
    private HealthCheckSslHealthCheck sslHealthCheck;
    private HealthCheckTcpHealthCheck tcpHealthCheck;
    private Integer healthyThreshold;
    private Integer unhealthyThreshold;

    // Read-only
    private String selfLink;

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
    public void copyFrom(HealthCheck model) {
        setName(model.getName());
        setDescription(model.getDescription());
        setSelfLink(model.getSelfLink());
        setCheckIntervalSec(model.getCheckIntervalSec());
        setTimeoutSec(model.getTimeoutSec());
        setUnhealthyThreshold(model.getUnhealthyThreshold());
        setHealthyThreshold(model.getHealthyThreshold());

        if (model.hasType()) {
            setType(HealthCheck.Type.valueOf(model.getType()));
        }

        setHttpHealthCheck(null);
        if (model.hasHttpHealthCheck()) {
            HealthCheckHttpHealthCheck httpHealthCheck = newSubresource(HealthCheckHttpHealthCheck.class);
            httpHealthCheck.copyFrom(model.getHttpHealthCheck());

            setHttpHealthCheck(httpHealthCheck);
        }

        setHttpsHealthCheck(null);
        if (model.hasHttpsHealthCheck()) {
            HealthCheckHttpsHealthCheck httpsHealthCheck = newSubresource(HealthCheckHttpsHealthCheck.class);
            httpsHealthCheck.copyFrom(model.getHttpsHealthCheck());

            setHttpsHealthCheck(httpsHealthCheck);
        }

        setHttp2HealthCheck(null);
        if (model.hasHttp2HealthCheck()) {
            HealthCheckHttp2HealthCheck http2HealthCheck = newSubresource(HealthCheckHttp2HealthCheck.class);
            http2HealthCheck.copyFrom(model.getHttp2HealthCheck());

            setHttp2HealthCheck(http2HealthCheck);
        }

        setSslHealthCheck(null);
        if (model.hasSslHealthCheck()) {
            HealthCheckSslHealthCheck sslHealthCheck = newSubresource(HealthCheckSslHealthCheck.class);
            sslHealthCheck.copyFrom(model.getSslHealthCheck());

            setSslHealthCheck(sslHealthCheck);
        }

        setTcpHealthCheck(null);
        if (model.hasTcpHealthCheck()) {
            HealthCheckTcpHealthCheck tcpHealthCheck = newSubresource(HealthCheckTcpHealthCheck.class);
            tcpHealthCheck.copyFrom(model.getTcpHealthCheck());

            setTcpHealthCheck(tcpHealthCheck);
        }
    }

    public HealthCheck getHealthCheck(Set<String> changedFieldNames, HealthCheck currentHealthCheck) {
        boolean isUpdate = changedFieldNames != null && (changedFieldNames.size() > 0);

        HealthCheck.Builder healthCheck;
        if (currentHealthCheck == null) {
            healthCheck = HealthCheck.newBuilder();
        } else {
            healthCheck = currentHealthCheck.toBuilder();
        }

        if (!isUpdate) {
            healthCheck.setName(getName());
        }

        if ((!isUpdate || changedFieldNames.contains("check-interval-sec"))) {
            if (getCheckIntervalSec() != null) {
                healthCheck.setCheckIntervalSec(getCheckIntervalSec());
            } else {
                healthCheck.clearCheckIntervalSec();
            }
        }

        if ((!isUpdate || changedFieldNames.contains("description"))) {
            if (getDescription() != null) {
                healthCheck.setDescription(getDescription());
            } else {
                healthCheck.clearDescription();
            }
        }

        if ((!isUpdate || changedFieldNames.contains("healthy-threshold"))) {
            if (getHealthyThreshold() != null) {
                healthCheck.setHealthyThreshold(getHealthyThreshold());
            } else {
                healthCheck.clearHealthyThreshold();
            }
        }

        if ((!isUpdate || changedFieldNames.contains("timeout-sec"))) {
            if (getTimeoutSec() != null) {
                healthCheck.setTimeoutSec(getTimeoutSec());
            } else {
                healthCheck.clearTimeoutSec();
            }
        }

        if ((!isUpdate || changedFieldNames.contains("unhealthy-threshold"))) {
            if (getUnhealthyThreshold() != null) {
                healthCheck.setUnhealthyThreshold(getUnhealthyThreshold());
            } else {
                healthCheck.clearUnhealthyThreshold();
            }
        }

        healthCheck.clearType();
        healthCheck.clearHttpHealthCheck();
        if (getHttpHealthCheck() != null) {
            healthCheck.setType(getHttpHealthCheck().getType().name());
            healthCheck.setHttpHealthCheck(getHttpHealthCheck().toHttpHealthCheck());
        }

        healthCheck.clearHttpsHealthCheck();
        if (getHttpsHealthCheck() != null) {
            healthCheck.setType(getHttpsHealthCheck().getType().name());
            healthCheck.setHttpsHealthCheck(getHttpsHealthCheck().toHttpsHealthCheck());
        }

        healthCheck.clearHttp2HealthCheck();
        if (getHttp2HealthCheck() != null) {
            healthCheck.setType(getHttp2HealthCheck().getType().name());
            healthCheck.setHttp2HealthCheck(getHttp2HealthCheck().toHttp2HealthCheck());
        }

        healthCheck.clearSslHealthCheck();
        if (getSslHealthCheck() != null) {
            healthCheck.setType(getSslHealthCheck().getType().name());
            healthCheck.setSslHealthCheck(getSslHealthCheck().toSslHealthCheck());
        }

        healthCheck.clearTcpHealthCheck();
        if (getTcpHealthCheck() != null) {
            healthCheck.setType(getTcpHealthCheck().getType().name());
            healthCheck.setTcpHealthCheck(getTcpHealthCheck().toTcpHealthCheck());
        }

        return healthCheck.build();
    }
}
