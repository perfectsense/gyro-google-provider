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

import java.util.stream.Collectors;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.compute.Compute;
import com.google.api.services.compute.model.Operation;
import gyro.core.GyroException;
import gyro.google.GoogleResource;

public abstract class ComputeResource extends GoogleResource {

    public void waitForCompletion(Compute compute, Operation operation) throws Exception {
        waitForCompletion(compute, operation, 60000);
    }

    public void waitForCompletion(Compute compute, Operation operation, long timeout) throws Exception {
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
                    Compute.ZoneOperations.Get get = compute.zoneOperations()
                        .get(getProjectId(), zone, operation.getName());
                    operation = get.execute();
                } else {
                    Compute.GlobalOperations.Get get = compute.globalOperations()
                        .get(getProjectId(), operation.getName());
                    operation = get.execute();
                }
            }
        } catch (GoogleJsonResponseException ex) {
            if (ex.getStatusCode() != 404) {
                throw ex;
            }
        }

        if (operation != null && operation.getError() != null) {
            throw new GyroException(formatOperationErrorMessage(operation.getError()));
        }
    }

    protected static String formatOperationErrorMessage(Operation.Error error) {
        return error.getErrors().stream()
            .map(Operation.Error.Errors::getMessage)
            .collect(Collectors.joining("\n"));
    }

    protected Compute createComputeClient() {
        return createClient(Compute.class);
    }
}
