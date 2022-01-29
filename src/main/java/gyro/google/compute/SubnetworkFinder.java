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
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.api.gax.rpc.InvalidArgumentException;
import com.google.api.gax.rpc.NotFoundException;
import com.google.api.gax.rpc.UnaryCallable;
import com.google.cloud.compute.v1.AggregatedListSubnetworksRequest;
import com.google.cloud.compute.v1.ListSubnetworksRequest;
import com.google.cloud.compute.v1.Subnetwork;
import com.google.cloud.compute.v1.SubnetworkAggregatedList;
import com.google.cloud.compute.v1.SubnetworkList;
import com.google.cloud.compute.v1.SubnetworksClient;
import com.google.cloud.compute.v1.SubnetworksScopedList;
import com.psddev.dari.util.StringUtils;
import gyro.core.Type;
import gyro.google.GoogleFinder;

/**
 * Query subnet.
 *
 * Example
 * -------
 *
 * .. code-block:: gyro
 *
 *    subnet: $(external-query google::compute-subnet { name: 'subnet-example', region: 'us-east1'})
 */
@Type("compute-subnet")
public class SubnetworkFinder extends GoogleFinder<SubnetworksClient, Subnetwork, SubnetworkResource> {

    private String name;
    private String region;

    /**
     * The name of the subnet.
     */
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * The region of the subnet.
     */
    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    @Override
    protected List<Subnetwork> findAllGoogle(SubnetworksClient client) throws Exception {
        List<Subnetwork> subnetworks = new ArrayList<>();
        String nextPageToken = null;

        try {
            do {
                UnaryCallable<AggregatedListSubnetworksRequest, SubnetworkAggregatedList> callable = client
                    .aggregatedListCallable();
                AggregatedListSubnetworksRequest.Builder builder = AggregatedListSubnetworksRequest.newBuilder();

                if (nextPageToken != null) {
                    builder.setPageToken(nextPageToken);
                }

                SubnetworkAggregatedList aggregatedList = callable.call(builder
                    .setProject(getProjectId()).build());
                nextPageToken = aggregatedList.getNextPageToken();

                subnetworks.addAll(aggregatedList.getItemsMap().values().stream()
                    .map(SubnetworksScopedList::getSubnetworksList)
                    .flatMap(Collection::stream)
                    .collect(Collectors.toList()));
            } while (!StringUtils.isEmpty(nextPageToken));

        } finally {
            client.close();
        }

        return subnetworks;
    }

    @Override
    protected List<Subnetwork> findGoogle(SubnetworksClient client, Map<String, String> filters) throws Exception {
        List<Subnetwork> subnetworks = new ArrayList<>();

        try {
            if (filters.containsKey("region")) {
                if (filters.containsKey("name")) {
                    subnetworks.add(client.get(getProjectId(), filters.get("region"), filters.get("name")));

                } else {
                    SubnetworkList subnetworkList;
                    String nextPageToken;

                    do {
                        UnaryCallable<ListSubnetworksRequest, SubnetworksClient.ListPagedResponse> callable = client
                            .listPagedCallable();
                        SubnetworksClient.ListPagedResponse listPagedResponse = callable.call(ListSubnetworksRequest.newBuilder()
                            .setProject(getProjectId()).setRegion(filters.get("region")).build());
                        subnetworkList = listPagedResponse.getPage().getResponse();
                        nextPageToken = listPagedResponse.getNextPageToken();

                        if (subnetworkList.getItemsList() != null) {
                            subnetworks.addAll(subnetworkList.getItemsList());
                        }

                    } while (!StringUtils.isEmpty(nextPageToken));
                }
            }

        } catch (NotFoundException | InvalidArgumentException ex) {
            // ignore
        } finally {
            client.close();
        }

        return subnetworks;
    }
}
