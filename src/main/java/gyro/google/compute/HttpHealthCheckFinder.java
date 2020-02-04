/*
 * Copyright 2020, Perfect Sense, Inc.
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
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.google.api.services.compute.Compute;
import com.google.api.services.compute.model.HttpHealthCheck;
import com.google.api.services.compute.model.HttpHealthCheckList;
import gyro.core.Type;
import gyro.google.GoogleFinder;

/**
 * Query for http health checks.
 *
 * Example
 * -------
 *
 * .. code-block:: gyro
 *
 *     compute-http-health-check: $(external-query google::compute-http-health-check { name: "http-health-check-example" })
 */
@Type("compute-http-health-check")
public class HttpHealthCheckFinder extends GoogleFinder<Compute, HttpHealthCheck, HttpHealthCheckResource> {

    private String name;

    /**
     * The name of the http health check.
     */
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    protected List<HttpHealthCheck> findAllGoogle(Compute client) throws Exception {
        List<HttpHealthCheck> healthChecks = new ArrayList<>();
        HttpHealthCheckList healthCheckList;
        String nextPageToken = null;
        do {
            healthCheckList = client.httpHealthChecks().list(getProjectId()).setPageToken(nextPageToken).execute();
            nextPageToken = healthCheckList.getNextPageToken();

            if (healthCheckList.getItems() != null) {
                healthChecks.addAll(healthCheckList.getItems());
            }
        } while (nextPageToken != null);

        return healthChecks;
    }

    @Override
    protected List<HttpHealthCheck> findGoogle(Compute client, Map<String, String> filters) throws Exception {
        return Collections.singletonList(client.httpHealthChecks().get(getProjectId(), filters.get("name")).execute());
    }
}
