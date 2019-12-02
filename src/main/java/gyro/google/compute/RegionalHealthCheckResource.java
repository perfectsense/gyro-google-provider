package gyro.google.compute;

import java.io.IOException;
import java.util.Set;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
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

@Type("compute-regional-health-check")
public class RegionalHealthCheckResource extends AbstractHealthCheckResource {

    private String region;

    @Required
    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    @Override
    public void copyFrom(HealthCheck healthCheck) {
        setRegion(healthCheck.getRegion());
        super.copyFrom(healthCheck);
    }

    @Override
    public boolean refresh() {
        Compute client = createComputeClient();
        try {
            String region = getRegion();
            region = region.substring(region.lastIndexOf("/") + 1);
            HealthCheck healthCheck = client.regionHealthChecks().get(getProjectId(), region, getName()).execute();
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

        try {
            Compute.RegionHealthChecks.Insert insert = client.regionHealthChecks()
                .insert(getProjectId(), healthCheck.getRegion(), healthCheck);
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
            String region = getRegion();
            region = region.substring(region.lastIndexOf("/") + 1);
            HealthCheck healthCheck = client.regionHealthChecks().get(getProjectId(), region, getName()).execute();
            client.regionHealthChecks().delete(getProjectId(), region, healthCheck.getName()).execute();
        } catch (GoogleJsonResponseException e) {
            throw new GyroException(String.format(
                "Unable to delete Health Check: %s, Google error: %s",
                getName(),
                e.getMessage()));
        }
    }
}
