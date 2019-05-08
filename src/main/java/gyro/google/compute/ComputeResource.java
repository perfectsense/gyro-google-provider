package gyro.google.compute;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.compute.Compute;
import com.google.api.services.compute.model.Operation;
import gyro.google.GoogleResource;

public abstract class ComputeResource extends GoogleResource {

    public Operation.Error waitForCompletion(Compute compute, Operation operation) throws Exception {
        return waitForCompletion(compute, operation, 60000);
    }

    public Operation.Error waitForCompletion(Compute compute, Operation operation, long timeout) throws Exception {
        long start = System.currentTimeMillis();
        final long pollInterval = 1000;

        String zone = operation.getZone();
        if (zone != null) {
            String[] bits = zone.split("/");
            zone = bits[bits.length - 1];
        }

        try {
            while (operation != null && !operation.getStatus().equals("DONE")) {
                Thread.sleep(pollInterval);

                long elapsed = System.currentTimeMillis() - start;
                if (elapsed >= timeout) {
                    throw new InterruptedException("Timed out waiting for operation to complete");
                }

                if (zone != null) {
                    Compute.ZoneOperations.Get get = compute.zoneOperations().get(getProjectId(), zone, operation.getName());
                    operation = get.execute();
                } else {
                    Compute.GlobalOperations.Get get = compute.globalOperations().get(getProjectId(), operation.getName());
                    operation = get.execute();
                }
            }
        } catch (GoogleJsonResponseException ex) {
            if (ex.getStatusCode() == 404) {
                return null;
            }

            throw ex;
        }

        return operation == null ? null : operation.getError();
    }

}
