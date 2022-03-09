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
import java.util.List;
import java.util.Map;

import com.google.api.gax.rpc.NotFoundException;
import com.google.cloud.compute.v1.HealthCheck;
import com.google.cloud.compute.v1.HealthCheckList;
import com.google.cloud.compute.v1.HealthChecksClient;
import com.google.cloud.compute.v1.ListHealthChecksRequest;
import com.psddev.dari.util.StringUtils;
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
public class HttpHealthCheckFinder extends GoogleFinder<HealthChecksClient, HealthCheck, HttpHealthCheckResource> {

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
    protected List<HealthCheck> findAllGoogle(HealthChecksClient client) throws Exception {
        List<HealthCheck> healthChecks = new ArrayList<>();
        HealthCheckList healthCheckList;
        String nextPageToken = null;

        try {
            do {
                ListHealthChecksRequest.Builder builder = ListHealthChecksRequest.newBuilder();

                if (nextPageToken != null) {
                    builder.setPageToken(nextPageToken);
                }

                HealthChecksClient.ListPagedResponse listPagedResponse = client.list(builder
                    .setProject(getProjectId()).build());
                healthCheckList = listPagedResponse.getPage().getResponse();
                nextPageToken = listPagedResponse.getNextPageToken();

                healthChecks.addAll(healthCheckList.getItemsList());
            } while (!StringUtils.isEmpty(nextPageToken));

        } finally {
            client.close();
        }

        return healthChecks;
    }

    @Override
    protected List<HealthCheck> findGoogle(HealthChecksClient client, Map<String, String> filters) throws Exception {
        ArrayList<HealthCheck> healthChecks = new ArrayList<>();

        try {
            healthChecks.add(client.get(getProjectId(), filters.get("name")));
        } catch (NotFoundException ex) {
            // ignore
        } finally {
            client.close();
        }

        return healthChecks;
    }
}
