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

import com.google.api.services.compute.model.HealthCheck;
import gyro.core.resource.Id;
import gyro.core.resource.Output;
import gyro.core.resource.Updatable;
import gyro.core.validation.ConflictsWith;
import gyro.core.validation.Regex;
import gyro.core.validation.Required;
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
    private String type;
    private Integer unhealthyThreshold;

    /**
     * The name of the health check. The name must be 1-63 characters long and the first character must be a lowercase letter, and all following characters must be a dash, lowercase letter, or digit, except the last character, which cannot be a dash. (Required)
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
    @ConflictsWith({ "https-health-check", "http2-health-check", "ssl-health-check", "tcp-health-check" })
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
    @ConflictsWith({ "http-health-check", "http2-health-check", "ssl-health-check", "tcp-health-check" })
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
    @ConflictsWith({ "https-health-check", "http-health-check", "ssl-health-check", "tcp-health-check" })
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
    @ConflictsWith({ "https-health-check", "http2-health-check", "http-health-check", "tcp-health-check" })
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
}
