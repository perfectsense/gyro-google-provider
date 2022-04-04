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

import com.google.cloud.compute.v1.Error;
import com.google.cloud.compute.v1.Errors;
import com.google.cloud.compute.v1.GetGlobalOperationRequest;
import com.google.cloud.compute.v1.GetRegionOperationRequest;
import com.google.cloud.compute.v1.GetZoneOperationRequest;
import com.google.cloud.compute.v1.GlobalOperationsClient;
import com.google.cloud.compute.v1.Operation;
import com.google.cloud.compute.v1.RegionOperationsClient;
import com.google.cloud.compute.v1.ZoneOperationsClient;
import gyro.core.GyroException;
import gyro.core.Waiter;
import gyro.google.GoogleResource;
import org.apache.commons.lang3.StringUtils;

public abstract class ComputeResource extends GoogleResource {

    private static final long DEFAULT_WAIT_DURATION = 1L;

    private static final TimeUnit DEFAULT_WAIT_TIME_UNIT = TimeUnit.MINUTES;

    protected static String formatOperationErrorMessage(Error error) {
        return error.getErrorsList().stream()
            .map(Errors::getMessage)
            .collect(Collectors.joining("\n"));
    }

    public void waitForCompletion(Operation operation) {
        waitForCompletion(operation, 0, null);
    }

    public void waitForCompletion(Operation operation, long duration, TimeUnit unit) {
        if (operation != null) {
            Waiter waiter = new Waiter().prompt(false);

            if (duration > 0 && unit != null) {
                waiter.atMost(duration, unit);
            } else {
                waiter.atMost(DEFAULT_WAIT_DURATION, DEFAULT_WAIT_TIME_UNIT);
            }

            waiter.until(() -> {
                Operation response = null;
                String zone = operation.getZone();

                if (!StringUtils.isEmpty(zone)) {
                    try (ZoneOperationsClient zoneOperationsClient = createClient(ZoneOperationsClient.class)) {
                        String[] bits = zone.split("/");
                        zone = bits[bits.length - 1];
                        response = zoneOperationsClient.get(GetZoneOperationRequest.newBuilder()
                            .setOperation(operation.getName()).setProject(getProjectId()).setZone(zone).build());
                    }
                } else {
                    String region = operation.getRegion();

                    if (!StringUtils.isEmpty(region)) {
                        try (RegionOperationsClient regionOperationsClient = createClient(RegionOperationsClient.class)) {
                            region = region.substring(region.lastIndexOf("/") + 1);
                            response = regionOperationsClient.get(GetRegionOperationRequest.newBuilder()
                                .setOperation(operation.getName()).setProject(getProjectId()).setRegion(region).build());
                        }
                    } else {
                        try (GlobalOperationsClient globalOperationsClient = createClient(GlobalOperationsClient.class)) {
                            response = globalOperationsClient.get(GetGlobalOperationRequest.newBuilder()
                                .setOperation(operation.getName()).setProject(getProjectId()).build());
                        }
                    }
                }

                if (response != null && response.getError().getErrorsCount() > 0) {
                    throw new GyroException(formatOperationErrorMessage(response.getError()));
                }

                return response != null && response.getStatus().equals(Operation.Status.DONE);
            });
        }
    }
}
