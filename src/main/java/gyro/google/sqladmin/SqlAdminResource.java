/*
 * Copyright 2024, Brightspot.
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

package gyro.google.sqladmin;

import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.google.api.services.sqladmin.SQLAdmin;
import com.google.api.services.sqladmin.model.Operation;
import com.google.api.services.sqladmin.model.OperationError;
import com.google.api.services.sqladmin.model.OperationErrors;
import gyro.core.GyroException;
import gyro.core.Waiter;
import gyro.google.GoogleResource;

public abstract class SqlAdminResource extends GoogleResource {

    private static final long DEFAULT_WAIT_DURATION = 1L;

    private static final TimeUnit DEFAULT_WAIT_TIME_UNIT = TimeUnit.MINUTES;

    protected static void waitForCompletion(Operation operation, long duration, TimeUnit unit, String projectId, SqlAdminResource resource) {
        SQLAdmin client = resource.createClient(SQLAdmin.class);
        waitForCompletion(operation, duration, unit, projectId, client);
    }

    protected static void waitForCompletion(Operation operation, String projectId, SQLAdmin client) {
        waitForCompletion(operation, 0L, null, projectId, client);
    }

    protected static void waitForCompletion(Operation operation, long duration, TimeUnit unit, String projectId, SQLAdmin client) {
        if (operation != null) {
            Waiter waiter = new Waiter().prompt(false);

            if (duration > 0 && unit != null) {
                waiter.atMost(duration, unit);
            } else {
                waiter.atMost(DEFAULT_WAIT_DURATION, DEFAULT_WAIT_TIME_UNIT);
            }

            waiter.until(() -> {
                Operation response = client.operations().get(projectId, operation.getName()).execute();

                if (response != null && !response.getError().isEmpty()) {
                    throw new GyroException(formatOperationErrorMessage(response.getError()));
                }

                if (response != null && response.getStatus() != null) {
                    System.out.println("Response -->" + response.getStatus());
                }
                return response != null && "".equalsIgnoreCase(response.getStatus());
            });
        }

    }

    private static String formatOperationErrorMessage(OperationErrors error) {
        return error.getErrors().stream()
            .map(OperationError::getMessage)
            .collect(Collectors.joining("\n"));
    }
}
