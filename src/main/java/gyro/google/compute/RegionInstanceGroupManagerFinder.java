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
import com.google.cloud.compute.v1.GetRegionInstanceGroupManagerRequest;
import com.google.cloud.compute.v1.InstanceGroupManager;
import com.google.cloud.compute.v1.ListRegionInstanceGroupManagersRequest;
import com.google.cloud.compute.v1.ListRegionsRequest;
import com.google.cloud.compute.v1.Region;
import com.google.cloud.compute.v1.RegionInstanceGroupManagersClient;
import com.google.cloud.compute.v1.RegionsClient;
import com.psddev.dari.util.StringUtils;
import gyro.core.GyroException;
import gyro.core.Type;
import gyro.google.GoogleCredentials;
import gyro.google.GoogleFinder;
import gyro.google.util.Utils;

/**
 * Query a region instance group manager.
 *
 * You can provide an expression that filters resources. The expression must specify the field name, and the value that you want to use for filtering.
 *
 * Please see :doc:`compute-region-instance-group-manager` resource for available fields.
 *
 * Example
 * -------
 *
 * .. code-block:: gyro
 *
 *    region-instance-group-manager: $(external-query google::compute-region-instance-group-manager { name: 'region-instance-group-manager-example', region: 'us-central1' })
 */
@Type("compute-region-instance-group-manager")
public class RegionInstanceGroupManagerFinder
    extends GoogleFinder<RegionInstanceGroupManagersClient, InstanceGroupManager, RegionInstanceGroupManagerResource> {

    @Override
    protected List<InstanceGroupManager> findAllGoogle(RegionInstanceGroupManagersClient client) throws Exception {
        return getRegionInstanceGroupManagers(client, null);
    }

    @Override
    protected List<InstanceGroupManager> findGoogle(
        RegionInstanceGroupManagersClient client, Map<String, String> filters) throws Exception {
        List<InstanceGroupManager> instanceGroupManagers = new ArrayList<>();
        String region = filters.remove("region");
        String name = filters.remove("name");
        String filter = Utils.convertToFilters(filters);

        try {
            if (filters.containsKey("zone")) {
                throw new GyroException("For zonal instanceGroupManager, use 'compute-instanceGroupManager' instead.");
            }

            if (region != null && name != null) {
                instanceGroupManagers.add(client.get(GetRegionInstanceGroupManagerRequest.newBuilder()
                    .setInstanceGroupManager(name).setProject(getProjectId()).setRegion(region).build()));

            } else {
                if (region != null) {
                    instanceGroupManagers.addAll(getInstanceGroupManagers(client, filter, region));

                } else if (name != null) {
                    List<String> regions = getRegions();

                    for (String r : regions) {
                        try {
                            instanceGroupManagers.add(client.get(GetRegionInstanceGroupManagerRequest.newBuilder()
                                .setInstanceGroupManager(name).setProject(getProjectId()).setRegion(r).build()));

                        } catch (NotFoundException | InvalidArgumentException ex) {
                            // ignore
                        }
                    }
                } else {
                    getRegionInstanceGroupManagers(client, filter);
                }
            }
        } finally {
            client.close();
        }

        return instanceGroupManagers;
    }

    private List<InstanceGroupManager> getRegionInstanceGroupManagers(
        RegionInstanceGroupManagersClient client,
        String filter) {
        List<String> regionList = getRegions();

        List<InstanceGroupManager> instanceGroupManagers = new ArrayList<>();

        try {
            for (String region : regionList) {
                instanceGroupManagers.addAll(getInstanceGroupManagers(client, filter, region));
            }

        } catch (NotFoundException | InvalidArgumentException ex) {
            // ignore

        } finally {
            client.close();
        }

        return instanceGroupManagers;
    }

    private List<InstanceGroupManager> getInstanceGroupManagers(
        RegionInstanceGroupManagersClient client, String filter, String region) {
        String pageToken = null;

        List<InstanceGroupManager> instanceGroupManagers = new ArrayList<>();

        do {
            ListRegionInstanceGroupManagersRequest.Builder builder = ListRegionInstanceGroupManagersRequest.newBuilder()
                .setProject(getProjectId()).setRegion(region);

            if (pageToken != null) {
                builder.setPageToken(pageToken);
            }

            if (filter != null) {
                builder.setFilter(filter);
            }

            RegionInstanceGroupManagersClient.ListPagedResponse response = client.list(builder.build());
            pageToken = response.getNextPageToken();

            if (response.getPage() != null && response.getPage().getResponse() != null) {
                instanceGroupManagers.addAll(response.getPage().getResponse().getItemsList());
            }

        } while (!StringUtils.isEmpty(pageToken));

        return instanceGroupManagers;
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
