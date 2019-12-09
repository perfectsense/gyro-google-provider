package gyro.google.compute;

import java.util.Set;

import com.google.api.client.util.Data;
import com.google.api.services.compute.Compute;
import com.google.api.services.compute.model.HTTP2HealthCheck;
import com.google.api.services.compute.model.HTTPHealthCheck;
import com.google.api.services.compute.model.HTTPSHealthCheck;
import com.google.api.services.compute.model.HealthCheck;
import com.google.api.services.compute.model.Operation;
import com.google.api.services.compute.model.SSLHealthCheck;
import com.google.api.services.compute.model.TCPHealthCheck;
import gyro.core.GyroException;
import gyro.core.GyroUI;
import gyro.core.Type;
import gyro.core.resource.Resource;
import gyro.core.scope.State;
import gyro.core.validation.Required;

/**
 * Creates a regional health check.
 *
 * Examples
 * --------
 *
 * .. code-block:: gyro
 *
 *      google::compute-regional-health-check regional-health-check-example-http
 *          name: "regional-health-check-example-http"
 *          region: "us-east1"
 *
 *          http-health-check
 *              request-path: "/myapp"
 *          end
 *      end
 *
 * .. code-block:: gyro
 *
 *      google::compute-regional-health-check regional-health-check-example-https
 *          name: "regional-health-check-example-https"
 *          check-interval-sec: 30
 *          description: "regional-health-check-example-description"
 *          healthy-threshold: 8
 *          timeout-sec: 29
 *          unhealthy-threshold: 6
 *          region: "us-east1"
 *
 *          https-health-check
 *              request-path: "/myapp"
 *              response: "okay"
 *          end
 *      end
 *
 * .. code-block:: gyro
 *
 *      google::compute-regional-health-check regional-health-check-example-http2
 *          name: "regional-health-check-example-http2"
 *          check-interval-sec: 30
 *          description: "regional-health-check-example-description"
 *          healthy-threshold: 8
 *          timeout-sec: 29
 *          unhealthy-threshold: 6
 *          region: "us-east1"
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
 *      google::compute-regional-health-check regional-health-check-example-tcp
 *          name: "regional-health-check-example-tcp"
 *          check-interval-sec: 30
 *          description: "regional-health-check-example-description"
 *          healthy-threshold: 8
 *          timeout-sec: 29
 *          unhealthy-threshold: 6
 *          region: "us-east1"
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
 *      google::compute-regional-health-check regional-health-check-example-ssh
 *          name: "regional-health-check-example-ssh"
 *          check-interval-sec: 30
 *          description: "regional-health-check-example-description"
 *          healthy-threshold: 8
 *          timeout-sec: 29
 *          unhealthy-threshold: 6
 *          region: "us-east1"
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
@Type("compute-regional-health-check")
public class RegionalHealthCheckResource extends AbstractHealthCheckResource {

    private String region;

    @Required
    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        region = region.substring(region.lastIndexOf("/") + 1);
        this.region = region;
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
        setRegion(healthCheck.getRegion());

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

    @Override
    public boolean doRefresh() throws Exception {
        Compute client = createComputeClient();
        HealthCheck healthCheck = client.regionHealthChecks().get(getProjectId(), getRegion(), getName()).execute();
        copyFrom(healthCheck);

        return true;
    }

    @Override
    public void doCreate(GyroUI ui, State state) throws Exception {
        Compute client = createComputeClient();
        HealthCheck healthCheck = new HealthCheck();
        healthCheck.setName(getName());
        healthCheck.setDescription(getDescription());
        healthCheck.setCheckIntervalSec(getCheckIntervalSec());
        healthCheck.setTimeoutSec(getTimeoutSec());
        healthCheck.setUnhealthyThreshold(getUnhealthyThreshold());
        healthCheck.setHealthyThreshold(getHealthyThreshold());
        healthCheck.setRegion(getRegion());

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

        Compute.RegionHealthChecks.Insert insert = client.regionHealthChecks()
            .insert(getProjectId(), healthCheck.getRegion(), healthCheck);
        Operation operation = insert.execute();
        Operation.Error error = waitForCompletion(client, operation);
        if (error != null) {
            throw new GyroException(error.toPrettyString());
        }

        refresh();
    }

    @Override
    public void doUpdate(GyroUI ui, State state, Resource current, Set<String> changedFieldNames) throws Exception {
        Compute client = createComputeClient();

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

        if (changedFieldNames.contains("region")) {
            healthCheck.setRegion(getRegion());
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

        Operation operation = client.regionHealthChecks()
            .patch(getProjectId(), getRegion(), getName(), healthCheck)
            .execute();
        Operation.Error error = waitForCompletion(client, operation);
        if (error != null) {
            throw new GyroException(error.toPrettyString());
        }

        refresh();
    }

    @Override
    public void doDelete(GyroUI ui, State state) throws Exception {
        Compute client = createComputeClient();
        HealthCheck healthCheck = client.regionHealthChecks().get(getProjectId(), getRegion(), getName()).execute();
        Operation operation = client.regionHealthChecks()
            .delete(getProjectId(), region, healthCheck.getName())
            .execute();
        Operation.Error error = waitForCompletion(client, operation);
        if (error != null) {
            throw new GyroException(error.toPrettyString());
        }
    }
}
