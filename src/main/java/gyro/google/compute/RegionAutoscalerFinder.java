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
import java.util.stream.Collectors;

import com.google.api.gax.rpc.InvalidArgumentException;
import com.google.api.gax.rpc.NotFoundException;
import com.google.cloud.compute.v1.Autoscaler;
import com.google.cloud.compute.v1.GetRegionAutoscalerRequest;
import com.google.cloud.compute.v1.ListRegionAutoscalersRequest;
import com.google.cloud.compute.v1.ListRegionsRequest;
import com.google.cloud.compute.v1.Region;
import com.google.cloud.compute.v1.RegionAutoscalersClient;
import com.google.cloud.compute.v1.RegionsClient;
import com.psddev.dari.util.StringUtils;
import gyro.core.GyroException;
import gyro.core.Type;
import gyro.google.GoogleCredentials;
import gyro.google.GoogleFinder;
import gyro.google.util.Utils;

/**
 * Query a region autoscaler.
 *
 * You can provide an expression that filters resources. The expression must specify the field name, and the value that you want to use for filtering.
 *
 * Please see :doc:`compute-region-autoscaler` resource for available fields.
 *
 * Example
 * -------
 *
 * .. code-block:: gyro
 *
 *    region-autoscaler: $(external-query google::compute-region-autoscaler { name: 'compute-region-autoscaler-example', region: 'us-central1' })
 */
@Type("compute-region-autoscaler")
public class RegionAutoscalerFinder
    extends GoogleFinder<RegionAutoscalersClient, Autoscaler, RegionAutoscalerResource> {

    @Override
    protected List<Autoscaler> findAllGoogle(RegionAutoscalersClient client) throws Exception {
        return getRegionAutoscalers(client, null);
    }

    @Override
    protected List<Autoscaler> findGoogle(RegionAutoscalersClient client, Map<String, String> filters) {
        List<Autoscaler> autoscalers = new ArrayList<>();
        String region = filters.remove("region");
        String name = filters.remove("name");
        String filter = Utils.convertToFilters(filters);

        try {
            if (filters.containsKey("zone")) {
                throw new GyroException("For zonal autoscaler, use 'compute-autoscaler' instead.");
            }

            if (region != null && name != null) {
                autoscalers.add(client.get(GetRegionAutoscalerRequest.newBuilder().setAutoscaler(name)
                    .setProject(getProjectId()).setRegion(region).build()));

            } else {
                if (region != null) {
                    autoscalers.addAll(getAutoscalers(client, filter, region));

                } else if (name != null) {
                    List<String> regions = getRegions();

                    for (String r : regions) {
                        try {
                            autoscalers.add(client.get(GetRegionAutoscalerRequest.newBuilder().setAutoscaler(name)
                                .setProject(getProjectId()).setRegion(r).build()));

                        } catch (NotFoundException | InvalidArgumentException ex) {
                            // ignore
                        }
                    }
                } else {
                    autoscalers.addAll(getRegionAutoscalers(client, filter));
                }
            }
        } finally {
            client.close();
        }

        return autoscalers;
    }

    private List<Autoscaler> getRegionAutoscalers(RegionAutoscalersClient client, String filter) {
        List<String> regionList = getRegions();

        List<Autoscaler> autoscalers = new ArrayList<>();

        try {
            for (String region : regionList) {
                autoscalers.addAll(getAutoscalers(client, filter, region));
            }

        } catch (NotFoundException | InvalidArgumentException ex) {
            // ignore

        } finally {
            client.close();
        }

        return autoscalers;
    }

    private List<Autoscaler> getAutoscalers(RegionAutoscalersClient client, String filter, String region) {
        String pageToken = null;

        List<Autoscaler> autoscalers = new ArrayList<>();

        do {
            ListRegionAutoscalersRequest.Builder builder = ListRegionAutoscalersRequest.newBuilder()
                .setProject(getProjectId()).setRegion(region);

            if (pageToken != null) {
                builder.setPageToken(pageToken);
            }

            if (filter != null) {
                builder.setFilter(filter);
            }

            RegionAutoscalersClient.ListPagedResponse response = client.list(builder.build());
            pageToken = response.getNextPageToken();

            if (response.getPage() != null && response.getPage().getResponse() != null) {
                autoscalers.addAll(response.getPage().getResponse().getItemsList());
            }

        } while (!StringUtils.isEmpty(pageToken));

        return autoscalers;
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
