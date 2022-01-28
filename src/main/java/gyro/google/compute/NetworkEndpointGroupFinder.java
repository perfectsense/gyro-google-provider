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

import com.google.api.gax.rpc.InvalidArgumentException;
import com.google.api.gax.rpc.NotFoundException;
import com.google.api.gax.rpc.UnaryCallable;
import com.google.cloud.compute.v1.AggregatedListNetworkEndpointGroupsRequest;
import com.google.cloud.compute.v1.ListNetworkEndpointGroupsRequest;
import com.google.cloud.compute.v1.NetworkEndpointGroup;
import com.google.cloud.compute.v1.NetworkEndpointGroupAggregatedList;
import com.google.cloud.compute.v1.NetworkEndpointGroupList;
import com.google.cloud.compute.v1.NetworkEndpointGroupsClient;
import com.google.cloud.compute.v1.NetworkEndpointGroupsScopedList;
import com.psddev.dari.util.StringUtils;
import gyro.core.Type;
import gyro.google.GoogleFinder;

/**
 * Query network-endpoint-group.
 *
 * Example
 * -------
 *
 * .. code-block:: gyro
 *
 *    network-endpoint-group: $(external-query google::compute-network-endpoint-group { name: 'network-endpoint-group-example', region: 'us-east1-b'})
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
        //        List<NetworkEndpointGroup> networkEndpointGroups = new ArrayList<>();
        //        NetworkEndpointGroupAggregatedList networkEndpointGroupList;
        //        String nextPageToken = null;
        //
        //        do {
        //            networkEndpointGroupList = client.networkEndpointGroups()
        //                .aggregatedList(getProjectId())
        //                .setPageToken(nextPageToken)
        //                .execute();
        //            networkEndpointGroups.addAll(networkEndpointGroupList.getItems().values().stream()
        //                .map(NetworkEndpointGroupsScopedList::getNetworkEndpointGroups)
        //                .filter(Objects::nonNull)
        //                .flatMap(Collection::stream)
        //                .collect(Collectors.toList()));
        //            nextPageToken = networkEndpointGroupList.getNextPageToken();
        //        } while (nextPageToken != null);
        //
        //        return networkEndpointGroups;
        try {
            return getNetworkEndpointGroups(client);
        } finally {
            client.close();
        }
    }

    @Override
    protected List<NetworkEndpointGroup> findGoogle(NetworkEndpointGroupsClient client, Map<String, String> filters)
        throws Exception {
        //        return Collections.singletonList(client.networkEndpointGroups()
        //            .get(getProjectId(), filters.get("zone"), filters.get("name"))
        //            .execute());
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
                    UnaryCallable<ListNetworkEndpointGroupsRequest, NetworkEndpointGroupsClient.ListPagedResponse> callable = client
                        .listPagedCallable();

                    ListNetworkEndpointGroupsRequest.Builder builder = ListNetworkEndpointGroupsRequest.newBuilder()
                        .setZone(filters.get("zone"));

                    if (nextPageToken != null) {
                        builder.setPageToken(nextPageToken);
                    }

                    NetworkEndpointGroupsClient.ListPagedResponse pagedResponse = callable.call(builder.setProject(
                        getProjectId())
                        .build());
                    networkEndpointGroupList = pagedResponse.getPage().getResponse();
                    nextPageToken = pagedResponse.getNextPageToken();

                    if (networkEndpointGroupList.getItemsList() != null) {
                        networkEndpointGroups.addAll(networkEndpointGroupList.getItemsList()
                            .stream()
                            .filter(Objects::nonNull)
                            .filter(networkEndpointGroup -> networkEndpointGroup.getZone() != null)
                            .collect(Collectors.toList()));
                    }

                } while (!StringUtils.isEmpty(nextPageToken));

                return networkEndpointGroups;

            } else {
                getNetworkEndpointGroups(client).removeIf(d -> !d.getName().equals(filters.get("name")));
            }
        } catch (NotFoundException | InvalidArgumentException ex) {
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

            if (networkEndpointGroupAggregatedList.getItemsMap() != null) {
                networkEndpointGroups.addAll(networkEndpointGroupAggregatedList.getItemsMap()
                    .values()
                    .stream()
                    .map(NetworkEndpointGroupsScopedList::getNetworkEndpointGroupsList)
                    .flatMap(Collection::stream)
                    .collect(Collectors.toList()));
            }

        } while (!StringUtils.isEmpty(nextPageToken));

        return networkEndpointGroups;
    }
}
