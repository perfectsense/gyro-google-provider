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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.compute.Compute;
import com.google.api.services.compute.model.HealthCheck;
import com.google.api.services.compute.model.HealthCheckList;
import gyro.core.GyroException;
import gyro.core.Type;
import gyro.google.GoogleFinder;

/**
 * Query for health checks.
 *
 * ========
 * Examples
 * ========
 *
 * Example find all global health checks for project.
 * -------
 *
 * .. code-block:: gyro
 *
 *    compute-health-check: $(external-query google::compute-health-check)
 *
 * Example find the global health check with a name equal to 'health-check-example'..
 * -------
 *
 * .. code-block:: gyro
 *
 *     compute-health-check: $(external-query google::compute-health-check { name: "health-check-example" })
 *
 */
@Type("compute-health-check")
public class HealthCheckFinder extends GoogleFinder<Compute, HealthCheck, HealthCheckResource> {

    private String name;

    /**
     * The name of the health check.
     */
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    protected List<HealthCheck> findAllGoogle(Compute client) {
        try {
            List<HealthCheck> healthChecks = new ArrayList<>();
            HealthCheckList healthCheckList;
            String nextPageToken = null;
            do {
                healthCheckList = client.healthChecks().list(getProjectId()).setPageToken(nextPageToken).execute();
                healthChecks.addAll(healthCheckList.getItems());
                nextPageToken = healthCheckList.getNextPageToken();
            } while (nextPageToken != null);
            return healthChecks;
        } catch (GoogleJsonResponseException je) {
            throw new GyroException(je.getDetails().getMessage());
        } catch (IOException ex) {
            throw new GyroException(ex);
        }
    }

    @Override
    protected List<HealthCheck> findGoogle(Compute client, Map<String, String> filters) {
        if (filters.containsKey("name")) {
            try {
                List<HealthCheck> healthChecks = new ArrayList<>();

                healthChecks.add(client.healthChecks()
                    .get(getProjectId(), filters.get("name"))
                    .execute());

                return healthChecks;
            } catch (GoogleJsonResponseException e) {
                if (e.getDetails().getCode() == 404) {
                    return new ArrayList<>();
                } else {
                    throw new GyroException(e.getDetails().getMessage());
                }
            } catch (IOException ex) {
                throw new GyroException(ex);
            }
        }

        return Collections.emptyList();
    }
}
