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

import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.google.api.services.compute.Compute;
import com.google.api.services.compute.model.Operation;
import gyro.core.GyroException;
import gyro.core.Wait;
import gyro.google.GoogleResource;

public abstract class ComputeResource extends GoogleResource {

    public void waitForCompletion(Compute compute, Operation operation) {
        Wait.atMost(60, TimeUnit.SECONDS)
            .until(() -> {
                    Operation response = null;
                    String zone = operation.getZone();

                    if (zone != null) {
                        String[] bits = zone.split("/");
                        zone = bits[bits.length - 1];
                        Compute.ZoneOperations.Get get = compute.zoneOperations()
                            .get(getProjectId(), zone, operation.getName());
                        response = get.execute();
                    } else {
                        String region = operation.getRegion();

                        if (region != null) {
                            region = region.substring(region.lastIndexOf("/") + 1);
                            Compute.RegionOperations.Get get = compute.regionOperations()
                                .get(getProjectId(), region, operation.getName());
                            response = get.execute();
                        } else {
                            Compute.GlobalOperations.Get get = compute.globalOperations()
                                .get(getProjectId(), operation.getName());
                            response = get.execute();
                        }
                    }

                    if (response != null && response.getError() != null) {
                        throw new GyroException(formatOperationErrorMessage(response.getError()));
                    }
                    return response != null && response.getStatus().equals("DONE");
                }
            );
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
