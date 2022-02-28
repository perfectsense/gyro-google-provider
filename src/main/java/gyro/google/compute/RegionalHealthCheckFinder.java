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
import java.util.stream.Collectors;

import com.google.api.gax.rpc.NotFoundException;
import com.google.cloud.compute.v1.GetRegionHealthCheckRequest;
import com.google.cloud.compute.v1.HealthCheck;
import com.google.cloud.compute.v1.ListRegionHealthChecksRequest;
import com.google.cloud.compute.v1.ListRegionsRequest;
import com.google.cloud.compute.v1.Region;
import com.google.cloud.compute.v1.RegionHealthChecksClient;
import com.google.cloud.compute.v1.RegionsClient;
import com.psddev.dari.util.StringUtils;
import gyro.core.GyroException;
import gyro.core.Type;
import gyro.google.GoogleCredentials;
import gyro.google.GoogleFinder;
import gyro.google.util.Utils;

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
public class RegionalHealthCheckFinder
    extends GoogleFinder<RegionHealthChecksClient, HealthCheck, RegionalHealthCheckResource> {

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
    protected List<HealthCheck> findAllGoogle(RegionHealthChecksClient client) throws Exception {
        return getRegionHealthChecks(client, null);
    }

    @Override
    protected List<HealthCheck> findGoogle(RegionHealthChecksClient client, Map<String, String> filters) {
        List<HealthCheck> healthChecks = new ArrayList<>();
        String region = filters.remove("region");
        String name = filters.remove("name");
        String filter = Utils.convertToFilters(filters);

        try {
            if (filters.containsKey("zone")) {
                throw new GyroException("For zonal healthCheck, use 'compute-healthCheck' instead.");
            }

            if (region != null && name != null) {
                healthChecks.add(client.get(GetRegionHealthCheckRequest.newBuilder().setHealthCheck(name)
                    .setProject(getProjectId()).setRegion(region).build()));

            } else {
                if (region != null) {
                    healthChecks.addAll(getHealthChecks(client, filter, region));

                } else if (name != null) {
                    List<String> regions = getRegions();

                    for (String r : regions) {
                        try {
                            healthChecks.add(client.get(GetRegionHealthCheckRequest.newBuilder().setHealthCheck(name)
                                .setProject(getProjectId()).setRegion(r).build()));

                        } catch (NotFoundException ex) {
                            // ignore
                        }
                    }
                } else {
                    healthChecks.addAll(getRegionHealthChecks(client, filter));
                }
            }
        } finally {
            client.close();
        }

        return healthChecks;
    }

    private List<HealthCheck> getRegionHealthChecks(RegionHealthChecksClient client, String filter) {
        List<String> regionList = getRegions();

        List<HealthCheck> healthChecks = new ArrayList<>();

        try {
            for (String region : regionList) {
                healthChecks.addAll(getHealthChecks(client, filter, region));
            }

        } catch (NotFoundException ex) {
            // ignore

        } finally {
            client.close();
        }

        return healthChecks;
    }

    private List<HealthCheck> getHealthChecks(RegionHealthChecksClient client, String filter, String region) {
        String pageToken = null;

        List<HealthCheck> healthChecks = new ArrayList<>();

        do {
            ListRegionHealthChecksRequest.Builder builder = ListRegionHealthChecksRequest.newBuilder()
                .setProject(getProjectId()).setRegion(region);

            if (pageToken != null) {
                builder.setPageToken(pageToken);
            }

            if (filter != null) {
                builder.setFilter(filter);
            }

            RegionHealthChecksClient.ListPagedResponse response = client.list(builder.build());
            pageToken = response.getNextPageToken();

            if (response.getPage() != null && response.getPage().getResponse() != null) {
                healthChecks.addAll(response.getPage().getResponse().getItemsList());
            }

        } while (!StringUtils.isEmpty(pageToken));

        return healthChecks;
    }

    private List<String> getRegions() {
        String pageToken = null;
        List<String> regionList = new ArrayList<>();

        try (RegionsClient regionsClient = credentials(GoogleCredentials.class).createClient(RegionsClient.class)) {
            do {
                ListRegionsRequest.Builder builder = ListRegionsRequest.newBuilder()
                    .setProject(getProjectId());

                if (pageToken != null) {
                    builder.setPageToken(pageToken);
                }

                RegionsClient.ListPagedResponse list = regionsClient.list(builder.build());
                pageToken = list.getNextPageToken();
                regionList.addAll(list.getPage().getResponse().getItemsList()
                    .stream().map(Region::getName).collect(Collectors.toList()));

            } while (!StringUtils.isEmpty(pageToken));
        }

        return regionList;
    }
}
