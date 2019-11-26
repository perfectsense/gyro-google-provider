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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.compute.Compute;
import com.google.api.services.compute.model.Network;
import com.google.api.services.compute.model.NetworkList;
import gyro.core.GyroException;
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
 *    network: $(external-query google::network { name: 'network-example'})
 */
@Type("network")
public class NetworkFinder extends GoogleFinder<Compute, Network, NetworkResource> {

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
    protected List<Network> findAllGoogle(Compute client) {
        try {
            List<Network> networks = new ArrayList<>();
            NetworkList networkList;
            String nextPageToken = null;

            do {
                networkList = client.networks().list(getProjectId()).setPageToken(nextPageToken).execute();
                networks.addAll(networkList.getItems());
                nextPageToken = networkList.getNextPageToken();
            } while (nextPageToken != null);

            return networks;
        } catch (GoogleJsonResponseException je) {
            throw new GyroException(je.getDetails().getMessage());
        } catch (IOException ex) {
            throw new GyroException(ex);
        }
    }

    @Override
    protected List<Network> findGoogle(Compute client, Map<String, String> filters) {
        Network network = null;

        try {
            network = client.networks().get(getProjectId(), filters.get("name")).execute();
        } catch (GoogleJsonResponseException je) {
            if (!je.getDetails().getMessage().matches("The resource (.*) was not found")) {
                throw new GyroException(je.getDetails().getMessage());
            }
        } catch (IOException ex) {
            throw new GyroException(ex);
        }

        if (network != null) {
            return Collections.singletonList(network);
        } else {
            return Collections.emptyList();
        }
    }
}
