package gyro.google.compute;

import java.util.Set;

import com.google.api.gax.rpc.NotFoundException;
import com.google.cloud.compute.v1.DeleteRegionHealthCheckRequest;
import com.google.cloud.compute.v1.GetRegionHealthCheckRequest;
import com.google.cloud.compute.v1.HealthCheck;
import com.google.cloud.compute.v1.InsertRegionHealthCheckRequest;
import com.google.cloud.compute.v1.Operation;
import com.google.cloud.compute.v1.RegionHealthChecksClient;
import com.google.cloud.compute.v1.UpdateRegionHealthCheckRequest;
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
 *          ssl-health-check
 *              port: 501
 *              port-name: "custom-port"
 *              proxy-header: "PROXY_V1"
 *              request: "/myapp"
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
    public void copyFrom(HealthCheck model) {
        super.copyFrom(model);

        setRegion(model.getRegion());
    }

    @Override
    public boolean doRefresh() throws Exception {
        try (RegionHealthChecksClient client = createClient(RegionHealthChecksClient.class)) {
            HealthCheck healthCheck = getRegionHealthCheck(client);

            if (healthCheck == null) {
                return false;
            }

            copyFrom(healthCheck);

            return true;
        }
    }

    @Override
    public void doCreate(GyroUI ui, State state) throws Exception {
        try (RegionHealthChecksClient client = createClient(RegionHealthChecksClient.class)) {
            HealthCheck.Builder builder = getHealthCheck(null, null).toBuilder();
            builder.setRegion(getRegion());

            Operation operation = client.insertCallable().call(InsertRegionHealthCheckRequest.newBuilder()
                .setProject(getProjectId())
                .setRegion(getRegion())
                .setHealthCheckResource(builder)
                .build());

            waitForCompletion(operation);
        }

        refresh();
    }

    @Override
    public void doUpdate(GyroUI ui, State state, Resource current, Set<String> changedFieldNames) throws Exception {
        try (RegionHealthChecksClient client = createClient(RegionHealthChecksClient.class)) {
            HealthCheck.Builder builder = getHealthCheck(changedFieldNames, getRegionHealthCheck(client)).toBuilder();

            if (changedFieldNames.contains("region")) {
                builder.setRegion(getRegion());
            }

            Operation operation = client.updateCallable().call(UpdateRegionHealthCheckRequest.newBuilder()
                .setProject(getProjectId())
                .setRegion(getRegion())
                .setHealthCheck(getName())
                .setHealthCheckResource(builder)
                .build());

            waitForCompletion(operation);
        }
    }

    @Override
    public void doDelete(GyroUI ui, State state) throws Exception {
        try (RegionHealthChecksClient client = createClient(RegionHealthChecksClient.class)) {
            Operation operation = client.deleteCallable().call(DeleteRegionHealthCheckRequest.newBuilder()
                .setProject(getProjectId())
                .setRegion(getRegion())
                .setHealthCheck(getName())
                .build());

            waitForCompletion(operation);
        }
    }

    private HealthCheck getRegionHealthCheck(RegionHealthChecksClient client) {
        HealthCheck healthCheck = null;

        try {
            healthCheck = client.get(GetRegionHealthCheckRequest.newBuilder()
                .setProject(getProjectId())
                .setRegion(getRegion())
                .setHealthCheck(getName())
                .build());

        } catch (NotFoundException ex) {
            // ignore
        }

        return healthCheck;
    }
}
