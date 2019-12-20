package gyro.google.compute;

import java.util.Set;

import com.google.api.services.compute.Compute;
import com.google.api.services.compute.model.BackendService;
import com.google.api.services.compute.model.Operation;
import gyro.core.GyroUI;
import gyro.core.Type;
import gyro.core.resource.Resource;
import gyro.core.scope.State;
import gyro.core.validation.Required;

/**
 * Creates a regional backend service.
 *
 * Example
 * -------
 *
 * .. code-block:: gyro
 *
 *     google::compute-region-backend-service regional-backend-service-example
 *         name: 'regional-backend-service-example'
 *         region: "us-central1"
 *         description: 'regional-backend-service-example-desc'
 *
 *         backend
 *             group: $(google::compute-instance-group instance-group-example-regional-backend-service)
 *             balancing-mode: "UTILIZATION"
 *         end
 *
 *         health-check: [ $(google::compute-health-check health-check-example-regional-backend-service) ]
 *
 *         connection-draining
 *             draining-timeout-sec: 41
 *         end
 *
 *         load-balancing-scheme: "INTERNAL"
 *
 *         protocol: "HTTPS"
 *         session-affinity: "NONE"
 *     end
 */
@Type("compute-region-backend-service")
public class RegionBackendServiceResource extends AbstractBackendServiceResource {

    private String region;

    /**
     * The region for the backend service.
     */
    @Required
    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    @Override
    protected boolean doRefresh() throws Exception {
        Compute client = createComputeClient();
        BackendService response = client.regionBackendServices().get(getProjectId(), getRegion(), getName()).execute();
        copyFrom(response);
        return true;
    }

    @Override
    protected void doCreate(GyroUI ui, State state) throws Exception {
        Compute client = createComputeClient();

        BackendService backendService = getBackendService(null);

        Operation operation = client.regionBackendServices().insert(getProjectId(), getRegion(), backendService).execute();
        waitForCompletion(client, operation);

        refresh();
    }

    @Override
    public void doUpdate(
        GyroUI ui, State state, Resource current, Set<String> changedFieldNames) throws Exception {

        Compute client = createComputeClient();

        BackendService backendService = getBackendService(changedFieldNames);

        Operation operation = client.regionBackendServices().patch(getProjectId(), getRegion(), getName(), backendService).execute();
        waitForCompletion(client, operation);
    }

    @Override
    public void doDelete(GyroUI ui, State state) throws Exception {
        Compute client = createComputeClient();
        Operation response = client.regionBackendServices().delete(getProjectId(), getRegion(), getName()).execute();
        waitForCompletion(client, response);
    }
}
