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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.cloud.compute.v1.HealthCheck;
import com.google.cloud.compute.v1.HealthCheckList;
import com.google.cloud.compute.v1.HealthChecksClient;
import com.google.cloud.compute.v1.ListHealthChecksRequest;
import gyro.core.Type;
import gyro.google.GoogleFinder;

/**
 * Query for health checks.
 *
 * Example
 * -------
 *
 * .. code-block:: gyro
 *
 *     compute-health-check: $(external-query google::compute-health-check { name: "health-check-example" })
 *
 */
@Type("compute-health-check")
public class HealthCheckFinder extends GoogleFinder<HealthChecksClient, HealthCheck, HealthCheckResource> {

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
    protected List<HealthCheck> findAllGoogle(HealthChecksClient client) throws Exception {
        List<HealthCheck> healthChecks = new ArrayList<>();
        HealthCheckList healthCheckList;
        String nextPageToken = null;

        try {
            do {
                ListHealthChecksRequest.Builder builder = ListHealthChecksRequest.newBuilder()
                    .setProject(getProjectId());

                if (nextPageToken != null) {
                    builder.setPageToken(nextPageToken);
                }

                healthCheckList = client.list(builder.build()).getPage().getResponse();
                nextPageToken = healthCheckList.getNextPageToken();

                if (healthCheckList.getItemsList() != null) {
                    healthChecks.addAll(healthCheckList.getItemsList());
                }
            } while (nextPageToken != null);

            return healthChecks;

        } finally {
            client.close();
        }
    }

    @Override
    protected List<HealthCheck> findGoogle(HealthChecksClient client, Map<String, String> filters) throws Exception {
        List<HealthCheck> healthChecks = new ArrayList<>();

        try {
            if (filters.containsKey("name")) {
                healthChecks.add(client.get(getProjectId(), filters.get("name")));
            }

            return healthChecks;

        } finally {
            client.close();
        }
    }
}
