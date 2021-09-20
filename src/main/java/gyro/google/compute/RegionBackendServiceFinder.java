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
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import com.google.api.services.compute.Compute;
import com.google.cloud.compute.v1.BackendService;
import com.google.cloud.compute.v1.BackendServiceAggregatedList;
import com.google.cloud.compute.v1.BackendServicesScopedList;
import gyro.core.Type;
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
public class RegionBackendServiceFinder extends GoogleFinder<Compute, BackendService, RegionBackendServiceResource> {

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
    protected List<BackendService> findAllGoogle(Compute client) throws Exception {
        List<BackendService> backendServices = new ArrayList<>();
        BackendServiceAggregatedList backendServiceList;
        String nextPageToken = null;

        do {
            backendServiceList = client.backendServices().aggregatedList(getProjectId()).execute();
            backendServices.addAll(backendServiceList
                .getItems().values().stream()
                .map(BackendServicesScopedList::getBackendServices)
                .filter(Objects::nonNull)
                .flatMap(Collection::stream)
                .filter(backendService -> backendService.getRegion() != null)
                .collect(Collectors.toList()));
            nextPageToken = backendServiceList.getNextPageToken();
        } while (nextPageToken != null);

        return backendServices;
    }

    @Override
    protected List<BackendService> findGoogle(
        Compute client, Map<String, String> filters) throws Exception {
        if (filters.containsKey("name")) {
            return Collections.singletonList(client.regionBackendServices()
                .get(getProjectId(), filters.get("region"), filters.get("name"))
                .execute());
        } else {
            return Optional.ofNullable(client.regionBackendServices()
                .list(getProjectId(), filters.get("region"))
                .execute()
                .getItems())
                .orElse(new ArrayList<>());
        }
    }
}
