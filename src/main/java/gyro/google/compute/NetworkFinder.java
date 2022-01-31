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

import com.google.api.gax.rpc.InvalidArgumentException;
import com.google.api.gax.rpc.NotFoundException;
import com.google.api.gax.rpc.UnaryCallable;
import com.google.cloud.compute.v1.ListNetworksRequest;
import com.google.cloud.compute.v1.Network;
import com.google.cloud.compute.v1.NetworkList;
import com.google.cloud.compute.v1.NetworksClient;
import com.psddev.dari.util.StringUtils;
import gyro.core.Type;
import gyro.google.GoogleFinder;

/**
 * Query network.
 *
 * Example
 * -------
 *
 * .. code-block:: gyro
 *
 *    network: $(external-query google::compute-network { name: 'network-example'})
 */
@Type("compute-network")
public class NetworkFinder extends GoogleFinder<NetworksClient, Network, NetworkResource> {

    private String name;

    /**
     * The name of the network.
     */
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    protected List<Network> findAllGoogle(NetworksClient client) throws Exception {
        List<Network> networks = new ArrayList<>();
        NetworkList networkList;
        String nextPageToken = null;

        try {
            do {
                UnaryCallable<ListNetworksRequest, NetworksClient.ListPagedResponse> callable = client
                    .listPagedCallable();
                ListNetworksRequest.Builder builder = ListNetworksRequest.newBuilder();

                if (nextPageToken != null) {
                    builder.setPageToken(nextPageToken);
                }

                NetworksClient.ListPagedResponse listPagedResponse = callable.call(builder
                    .setProject(getProjectId()).build());
                networkList = listPagedResponse.getPage().getResponse();
                nextPageToken = listPagedResponse.getNextPageToken();

                networks.addAll(networkList.getItemsList());
            } while (!StringUtils.isEmpty(nextPageToken));

        } finally {
            client.close();
        }

        return networks;
    }

    @Override
    protected List<Network> findGoogle(NetworksClient client, Map<String, String> filters) {
        ArrayList<Network> networks = new ArrayList<>();

        try {
            networks.add(client.get(getProjectId(), filters.get("name")));
        } catch (NotFoundException | InvalidArgumentException ex) {
            // ignore
        } finally {
            client.close();
        }

        return networks;
    }
}
