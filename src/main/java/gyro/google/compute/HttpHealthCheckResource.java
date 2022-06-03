/*
 * Copyright 2020, Perfect Sense, Inc.
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
import java.util.List;
import java.util.Set;

import com.google.api.gax.rpc.NotFoundException;
import com.google.cloud.compute.v1.DeleteHealthCheckRequest;
import com.google.cloud.compute.v1.GetHealthCheckRequest;
import com.google.cloud.compute.v1.HTTPHealthCheck;
import com.google.cloud.compute.v1.HealthCheck;
import com.google.cloud.compute.v1.HealthChecksClient;
import com.google.cloud.compute.v1.InsertHealthCheckRequest;
import com.google.cloud.compute.v1.Operation;
import com.google.cloud.compute.v1.PatchHealthCheckRequest;
import gyro.core.GyroUI;
import gyro.core.Type;
import gyro.core.resource.Id;
import gyro.core.resource.Output;
import gyro.core.resource.Resource;
import gyro.core.resource.Updatable;
import gyro.core.scope.State;
import gyro.core.validation.Regex;
import gyro.core.validation.Required;
import gyro.core.validation.ValidationError;
import gyro.google.Copyable;

/**
 * Creates a legacy http health check. Only use this when needed for network load balancing.
 *
 * Examples
 * --------
 *
 * .. code-block:: gyro
 *
 *      google::compute-http-health-check http-health-check-example
 *          name: "http-health-check-example"
 *          check-interval-sec: 30
 *          description: "Health check description."
 *          healthy-threshold: 8
 *          timeout-sec: 29
 *          unhealthy-threshold: 6
 *          port: 8080
 *          request-path: "/myapp"
 *      end
 */
@Type("compute-http-health-check")
public class HttpHealthCheckResource extends ComputeResource implements Copyable<HealthCheck> {

    private String name;
    private String description;
    private Integer checkIntervalSec;
    private String host;
    private Integer port;
    private String requestPath;
    private Integer timeoutSec;
    private Integer healthyThreshold;
    private Integer unhealthyThreshold;

    // Read-only
    private String selfLink;

    /**
     * How often (in seconds) to send a health check. Defaults to ``5`` seconds.
     */
    @Updatable
    public Integer getCheckIntervalSec() {
        return checkIntervalSec;
    }

    public void setCheckIntervalSec(Integer checkIntervalSec) {
        this.checkIntervalSec = checkIntervalSec;
    }

    /**
     * An optional description of this resource.
     */
    @Updatable
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * A so-far unhealthy instance will be marked healthy after this many consecutive successes. Defaults to ``2``.
     */
    @Updatable
    public Integer getHealthyThreshold() {
        return healthyThreshold;
    }

    public void setHealthyThreshold(Integer healthyThreshold) {
        this.healthyThreshold = healthyThreshold;
    }

    /**
     * The value of the host header in the HTTP health check request. If left empty (default value), the public IP on behalf of which this health check is performed will be used.
     */
    @Updatable
    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    /**
     * The name of the http health check.
     */
    @Required
    @Regex(value = "[a-z](?:[-a-z0-9]{0,61}[a-z0-9])?", message = "a string 1-63 characters long and the first character must be a lowercase letter, and all following characters must be a dash, lowercase letter, or digit, except the last character, which cannot be a dash")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * The TCP port number for the HTTP health check request. Defaults to ``80``.
     */
    @Updatable
    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    /**
     * The request path of the HTTP health check request. Defaults to ``/``.
     */
    @Updatable
    public String getRequestPath() {
        return requestPath;
    }

    public void setRequestPath(String requestPath) {
        this.requestPath = requestPath;
    }

    /**
     * The time (in seconds) to wait before claiming failure. Must be less than or equal to ``check-interval-sec``. Defaults to ``5`` seconds.
     */
    @Updatable
    public Integer getTimeoutSec() {
        return timeoutSec;
    }

    public void setTimeoutSec(Integer timeoutSec) {
        this.timeoutSec = timeoutSec;
    }

    /**
     * A so-far healthy instance will be marked unhealthy after this many consecutive failures. Defaults to ``2``.
     */
    @Updatable
    public Integer getUnhealthyThreshold() {
        return unhealthyThreshold;
    }

    public void setUnhealthyThreshold(Integer unhealthyThreshold) {
        this.unhealthyThreshold = unhealthyThreshold;
    }

    /**
     * Server-defined URL for the resource.
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
    public void copyFrom(HealthCheck model) throws Exception {
        setName(model.getName());
        setSelfLink(model.getSelfLink());
        setDescription(model.getDescription());
        setCheckIntervalSec(model.getCheckIntervalSec());
        setTimeoutSec(model.getTimeoutSec());
        setHealthyThreshold(model.getHealthyThreshold());
        setUnhealthyThreshold(model.getUnhealthyThreshold());

        if (model.hasHttpHealthCheck()) {
            setHost(model.getHttpHealthCheck().getHost());
            setPort(model.getHttpHealthCheck().getPort());
            setRequestPath(model.getHttpHealthCheck().getRequestPath());
        }
    }

    @Override
    protected boolean doRefresh() throws Exception {
        try (HealthChecksClient client = createClient(HealthChecksClient.class)) {
            HealthCheck healthCheck = getHealthCheck(client);

            if (healthCheck == null) {
                return false;
            }

            copyFrom(healthCheck);

            return true;
        }
    }

    @Override
    protected void doCreate(GyroUI ui, State state) throws Exception {
        try (HealthChecksClient client = createClient(HealthChecksClient.class)) {
            HealthCheck healthCheck = getHttpHealthCheck(null);
            Operation operation = client.insertCallable().call(InsertHealthCheckRequest.newBuilder()
                .setProject(getProjectId())
                .setHealthCheckResource(healthCheck)
                .build());

            waitForCompletion(operation);
        }

        refresh();
    }

    @Override
    public void doUpdate(
        GyroUI ui, State state, Resource current, Set<String> changedFieldNames) throws Exception {
        try (HealthChecksClient client = createClient(HealthChecksClient.class)) {
            HealthCheck healthCheck = getHttpHealthCheck(changedFieldNames);
            Operation operation = client.patchCallable().call(PatchHealthCheckRequest.newBuilder()
                .setProject(getProjectId())
                .setHealthCheck(getName())
                .setHealthCheckResource(healthCheck)
                .build());

            waitForCompletion(operation);
        }
    }

    @Override
    public void doDelete(GyroUI ui, State state) throws Exception {
        try (HealthChecksClient client = createClient(HealthChecksClient.class)) {
            Operation operation = client.deleteCallable().call(DeleteHealthCheckRequest.newBuilder()
                .setProject(getProjectId())
                .setHealthCheck(getName())
                .build());

            waitForCompletion(operation);
        }
    }

    public HealthCheck getHttpHealthCheck(Set<String> changedFieldNames) {
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

        if (!isUpdate || changedFieldNames.contains("host")) {
            HTTPHealthCheck.Builder builder = HTTPHealthCheck.newBuilder();

            if (getHost() != null) {
                builder.setHost(getHost());
            }

            if (!isUpdate || changedFieldNames.contains("port")) {
                builder.setPort(getPort());
            }

            if (!isUpdate || changedFieldNames.contains("request-path")) {
                builder.setRequestPath(getRequestPath());
            }

            healthCheck.setHttpHealthCheck(builder.build());
        }

        if (!isUpdate || changedFieldNames.contains("timeout-sec")) {
            healthCheck.setTimeoutSec(getTimeoutSec());
        }

        if (!isUpdate || changedFieldNames.contains("unhealthy-threshold")) {
            healthCheck.setUnhealthyThreshold(getUnhealthyThreshold());
        }

        healthCheck.setType("HTTP");

        return healthCheck.build();
    }

    @Override
    public List<ValidationError> validate(Set<String> configuredFields) {
        List<ValidationError> errors = new ArrayList<>();

        if (getTimeoutSec() != null && getCheckIntervalSec() != null && getTimeoutSec() > getCheckIntervalSec()) {
            errors.add(new ValidationError(
                this,
                "timeout-sec",
                "'timeout-sec' must be less than or equal to 'check-interval-sec'!"));
        }
        return errors;
    }

    private HealthCheck getHealthCheck(HealthChecksClient client) {
        HealthCheck healthCheck = null;

        try {
            healthCheck = client.get(GetHealthCheckRequest.newBuilder()
                .setProject(getProjectId())
                .setHealthCheck(getName())
                .build());

        } catch (NotFoundException ex) {
            // ignore
        }

        return healthCheck;
    }
}
