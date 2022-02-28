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
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import com.google.api.gax.rpc.NotFoundException;
import com.google.api.gax.rpc.UnaryCallable;
import com.google.cloud.compute.v1.BackendService;
import com.google.cloud.compute.v1.BackendServiceList;
import com.google.cloud.compute.v1.ListRegionBackendServicesRequest;
import com.google.cloud.compute.v1.ListRegionsRequest;
import com.google.cloud.compute.v1.Region;
import com.google.cloud.compute.v1.RegionBackendServicesClient;
import com.google.cloud.compute.v1.RegionsClient;
import com.psddev.dari.util.StringUtils;
import gyro.core.Type;
import gyro.google.GoogleCredentials;
import gyro.google.GoogleFinder;

/**
 * Query region backend service.
 *
 * Example
 * -------
 *
 * .. code-block:: gyro
 *
 *    compute-region-backend-service: $(external-query google::compute-region-backend-service { name: 'compute-region-backend-service-example'})
 */
@Type("compute-region-backend-service")
public class RegionBackendServiceFinder
    extends GoogleFinder<RegionBackendServicesClient, BackendService, RegionBackendServiceResource> {

    private String region;
    private String name;

    /**
     * The name of the backend service.
     */
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * The region of the backend service.
     */
    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    @Override
    protected List<BackendService> findAllGoogle(RegionBackendServicesClient client) throws Exception {
        try {
            return getBackendServices(client, getRegions());
        } finally {
            client.close();
        }
    }

    @Override
    protected List<BackendService> findGoogle(RegionBackendServicesClient client, Map<String, String> filters)
        throws Exception {
        List<BackendService> backendServices = new ArrayList<>();

        try {
            if (filters.containsKey("name") && filters.containsKey("region")) {
                backendServices.add(client.get(getProjectId(), filters.get("region"), filters.get("name")));
            } else if (filters.containsKey("region")) {
                backendServices.addAll(getBackendServices(client, Collections.singletonList(filters.get("region"))));
            } else {
                backendServices.addAll(getBackendServices(client, getRegions()));
                backendServices.removeIf(d -> !d.getName().equals(filters.get("name")));
            }
        } catch (NotFoundException ex) {
            // ignore
        } finally {
            client.close();
        }

        return backendServices;
    }

    private List<BackendService> getBackendServices(RegionBackendServicesClient client, List<String> regions) {
        List<BackendService> backendServices = new ArrayList<>();

        BackendServiceList backendServiceList;

        for (String region : regions) {
            String nextPageToken = null;
            do {
                UnaryCallable<ListRegionBackendServicesRequest, RegionBackendServicesClient.ListPagedResponse> callable = client
                    .listPagedCallable();

                ListRegionBackendServicesRequest.Builder builder = ListRegionBackendServicesRequest.newBuilder()
                    .setRegion(region);

                if (nextPageToken != null) {
                    builder.setPageToken(nextPageToken);
                }

                RegionBackendServicesClient.ListPagedResponse pagedResponse = callable.call(builder.setProject(
                    getProjectId()).build());
                backendServiceList = pagedResponse.getPage().getResponse();
                nextPageToken = pagedResponse.getNextPageToken();

                backendServices.addAll(backendServiceList.getItemsList().stream()
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList()));
            } while (!StringUtils.isEmpty(nextPageToken));
        }
        return backendServices;
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
