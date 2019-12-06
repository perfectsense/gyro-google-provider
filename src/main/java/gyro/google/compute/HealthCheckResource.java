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

@Type("compute-health-check")
public class HealthCheckResource extends AbstractHealthCheckResource {

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
            Operation operation = client.healthChecks().delete(getProjectId(), healthCheck.getName()).execute();
            Operation.Error error = waitForCompletion(client, operation);
            if (error != null) {
                throw new GyroException(error.toPrettyString());
            }
        } catch (GoogleJsonResponseException e) {
            throw new GyroException(String.format(
                "Unable to delete Health Check: %s, Google error: %s",
                getName(),
                e.getMessage()));
        }
    }
}
