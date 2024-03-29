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
import java.util.stream.Collectors;

import com.google.api.gax.rpc.NotFoundException;
import com.google.cloud.compute.v1.AggregatedListNetworkEndpointGroupsRequest;
import com.google.cloud.compute.v1.ListNetworkEndpointGroupsRequest;
import com.google.cloud.compute.v1.NetworkEndpointGroup;
import com.google.cloud.compute.v1.NetworkEndpointGroupAggregatedList;
import com.google.cloud.compute.v1.NetworkEndpointGroupList;
import com.google.cloud.compute.v1.NetworkEndpointGroupsClient;
import com.google.cloud.compute.v1.NetworkEndpointGroupsScopedList;
import gyro.core.Type;
import gyro.google.GoogleFinder;
import org.apache.commons.lang3.StringUtils;

/**
 * Query network-endpoint-group.
 *
 * Example
 * -------
 *
 * .. code-block:: gyro
 *
 *    network-endpoint-group: $(external-query google::compute-network-endpoint-group { name: 'network-endpoint-group-example', zone: 'us-east1-b'})
 */
@Type("compute-network-endpoint-group")
public class NetworkEndpointGroupFinder
    extends GoogleFinder<NetworkEndpointGroupsClient, NetworkEndpointGroup, NetworkEndpointGroupResource> {

    private String name;
    private String zone;

    /**
     * The name of the network endpoint group.
     */
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * The zone of the network endpoint group.
     */
    public String getZone() {
        return zone;
    }

    public void setZone(String zone) {
        this.zone = zone;
    }

    @Override
    protected List<NetworkEndpointGroup> findAllGoogle(NetworkEndpointGroupsClient client) throws Exception {
        try {
            return getNetworkEndpointGroups(client);
        } finally {
            client.close();
        }
    }

    @Override
    protected List<NetworkEndpointGroup> findGoogle(NetworkEndpointGroupsClient client, Map<String, String> filters)
        throws Exception {
        List<NetworkEndpointGroup> networkEndpointGroups = new ArrayList<>();

        try {
            if (filters.containsKey("name") && filters.containsKey("zone")) {
                networkEndpointGroups = Collections.singletonList(client.get(
                    getProjectId(),
                    filters.get("zone"),
                    filters.get("name")));
            } else if (filters.containsKey("zone")) {
                NetworkEndpointGroupList networkEndpointGroupList;
                String nextPageToken = null;

                do {
                    ListNetworkEndpointGroupsRequest.Builder builder = ListNetworkEndpointGroupsRequest.newBuilder()
                        .setZone(filters.get("zone"));

                    if (nextPageToken != null) {
                        builder.setPageToken(nextPageToken);
                    }

                    NetworkEndpointGroupsClient.ListPagedResponse pagedResponse = client.list(builder.setProject(
                        getProjectId()).build());
                    networkEndpointGroupList = pagedResponse.getPage().getResponse();
                    nextPageToken = pagedResponse.getNextPageToken();

                    networkEndpointGroups.addAll(networkEndpointGroupList.getItemsList()
                        .stream()
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList()));
                } while (!StringUtils.isEmpty(nextPageToken));
            } else {
                networkEndpointGroups.addAll(getNetworkEndpointGroups(client));
                networkEndpointGroups.removeIf(d -> !d.getName().equals(filters.get("name")));
            }
        } catch (NotFoundException ex) {
            // ignore
        } finally {
            client.close();
        }

        return networkEndpointGroups;
    }

    private List<NetworkEndpointGroup> getNetworkEndpointGroups(NetworkEndpointGroupsClient client) {
        List<NetworkEndpointGroup> networkEndpointGroups = new ArrayList<>();
        NetworkEndpointGroupAggregatedList networkEndpointGroupAggregatedList;
        String nextPageToken = null;

        do {
            AggregatedListNetworkEndpointGroupsRequest.Builder builder = AggregatedListNetworkEndpointGroupsRequest.newBuilder()
                .setProject(getProjectId());

            if (nextPageToken != null) {
                builder.setPageToken(nextPageToken);
            }

            NetworkEndpointGroupsClient.AggregatedListPagedResponse listPagedResponse = client.aggregatedList(builder.build());
            networkEndpointGroupAggregatedList = listPagedResponse.getPage().getResponse();
            nextPageToken = listPagedResponse.getNextPageToken();

            networkEndpointGroups.addAll(networkEndpointGroupAggregatedList.getItemsMap()
                .values()
                .stream()
                .map(NetworkEndpointGroupsScopedList::getNetworkEndpointGroupsList)
                .flatMap(Collection::stream)
                .collect(Collectors.toList()));
        } while (!StringUtils.isEmpty(nextPageToken));

        return networkEndpointGroups;
    }
}
