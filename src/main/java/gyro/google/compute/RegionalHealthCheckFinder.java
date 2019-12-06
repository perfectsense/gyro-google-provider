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
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.compute.Compute;
import com.google.api.services.compute.model.HealthCheck;
import com.google.api.services.compute.model.HealthCheckList;
import com.google.api.services.compute.model.HealthChecksAggregatedList;
import com.google.api.services.compute.model.HealthChecksScopedList;
import gyro.core.GyroException;
import gyro.core.Type;
import gyro.google.GoogleFinder;

/**
 * Query for regional health checks.
 *
 * Example
 * -------
 *
 * .. code-block:: gyro
 *
 *     compute-regional-health-check: $(external-query google::compute-regional-health-check { region: "us-east1" })
 *
 */
@Type("compute-regional-health-check")
public class RegionalHealthCheckFinder extends GoogleFinder<Compute, HealthCheck, RegionalHealthCheckResource> {

    private String name;
    private String region;

    /**
     * The name of the health check.
     */
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * The name of the region for this request. Not applicable to global health checks.
     */
    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    @Override
    protected List<HealthCheck> findAllGoogle(Compute client) {
        try {
            List<HealthCheck> healthChecks = new ArrayList<>();
            HealthChecksAggregatedList healthChecksAggregatedList;
            String nextPageToken = null;

            do {
                healthChecksAggregatedList = client.healthChecks()
                    .aggregatedList(getProjectId())
                    .setPageToken(nextPageToken)
                    .execute();
                healthChecks.addAll(healthChecksAggregatedList
                    .getItems().values().stream()
                    .map(HealthChecksScopedList::getHealthChecks)
                    .filter(Objects::nonNull)
                    .flatMap(Collection::stream)
                    .filter(healthCheck -> healthCheck.getRegion() != null)
                    .collect(Collectors.toList()));
                nextPageToken = healthChecksAggregatedList.getNextPageToken();
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
        try {
            List<HealthCheck> healthChecks = new ArrayList<>();
            HealthCheckList healthCheckList;
            String nextPageToken = null;

            if (filters.containsKey("region") && filters.containsKey("name")) {
                healthChecks.add(client.regionHealthChecks()
                    .get(getProjectId(), filters.get("region"), filters.get("name"))
                    .execute());

                return healthChecks;
            }

            if (filters.containsKey("region")) {
                do {
                    healthCheckList = client.regionHealthChecks()
                        .list(getProjectId(), filters.get("region"))
                        .setPageToken(nextPageToken)
                        .execute();

                    if (healthCheckList != null && healthCheckList.getItems() != null) {
                        nextPageToken = healthCheckList.getNextPageToken();
                        healthChecks.addAll(healthCheckList.getItems());
                    } else {
                        break;
                    }
                } while (nextPageToken != null);

                return healthChecks;
            }

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
}
