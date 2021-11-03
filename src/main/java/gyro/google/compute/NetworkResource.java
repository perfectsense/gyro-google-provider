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

import java.util.Set;

import com.google.api.gax.rpc.InvalidArgumentException;
import com.google.api.gax.rpc.NotFoundException;
import com.google.cloud.compute.v1.GetNetworkRequest;
import com.google.cloud.compute.v1.InsertNetworkRequest;
import com.google.cloud.compute.v1.Network;
import com.google.cloud.compute.v1.NetworkRoutingConfig;
import com.google.cloud.compute.v1.NetworksClient;
import com.google.cloud.compute.v1.Operation;
import com.google.cloud.compute.v1.PatchNetworkRequest;
import gyro.core.GyroUI;
import gyro.core.Type;
import gyro.core.resource.Id;
import gyro.core.resource.Output;
import gyro.core.resource.Resource;
import gyro.core.resource.Updatable;
import gyro.core.scope.State;
import gyro.core.validation.Required;
import gyro.core.validation.ValidStrings;
import gyro.google.Copyable;

/**
 * Creates a network.
 *
 * Example
 * -------
 *
 * .. code-block:: gyro
 *
 *     google::compute-network network-example
 *         name: "vpc-example"
 *         description: "vpc-example-desc"
 *         routing-mode: "GLOBAL"
 *     end
 */
@Type("compute-network")
public class NetworkResource extends ComputeResource implements Copyable<Network> {

    private String name;
    private String description;
    private String routingMode;

    // Read-only
    private String id;
    private String selfLink;

    /**
     * The name of the network.
     */
    @Required
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * The description of the network.
     */
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * The routing mode for the network.
     */
    @Required
    @ValidStrings({ "GLOBAL", "REGIONAL" })
    @Updatable
    public String getRoutingMode() {
        return routingMode != null ? routingMode.toUpperCase() : null;
    }

    public void setRoutingMode(String routingMode) {
        this.routingMode = routingMode;
    }

    /**
     * The Id of the network.
     */
    @Output
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    /**
     * The fully-qualified URL linking back to the network.
     */
    @Id
    @Output
    public String getSelfLink() {
        return selfLink;
    }

    public void setSelfLink(String selfLink) {
        this.selfLink = selfLink;
    }

    @Override
    public void copyFrom(Network network) {
        setId(String.valueOf(network.getId()));
        setSelfLink(network.getSelfLink());
        setRoutingMode(network.getRoutingConfig().getRoutingMode().toString());
        setDescription(network.getDescription());
        setName(network.getName());
    }

    @Override
    public boolean doRefresh() throws Exception {
        try (NetworksClient client = createClient(NetworksClient.class)) {
            Network network = getNetwork(client);

            if (network == null) {
                return false;
            }

            copyFrom(network);

            return true;
        }
    }

    @Override
    public void doCreate(GyroUI ui, State state) throws Exception {
        Network network = Network.newBuilder().setName(getName()).setDescription(getDescription())
            .setAutoCreateSubnetworks(false)
            .setRoutingConfig(NetworkRoutingConfig.newBuilder()
                .setRoutingMode(NetworkRoutingConfig.RoutingMode.valueOf(getRoutingMode()))
                .build())
            .build();

        try (NetworksClient client = createClient(NetworksClient.class)) {
            Operation operation = client.insert(InsertNetworkRequest.newBuilder()
                .setNetworkResource(network)
                .setProject(getProjectId())
                .build());

            state.save();
            waitForCompletion(operation);
        }

        refresh();
    }

    @Override
    public void doUpdate(GyroUI ui, State state, Resource current, Set<String> changedFieldNames) {
        NetworkRoutingConfig.Builder builder = NetworkRoutingConfig.newBuilder();
        builder.setRoutingMode(NetworkRoutingConfig.RoutingMode.valueOf(getRoutingMode()));

        try (NetworksClient client = createClient(NetworksClient.class)) {
            Network.Builder network = Network.newBuilder(getNetwork(client));
            network.setRoutingConfig(builder.build());

            Operation operation = client.patch(PatchNetworkRequest.newBuilder().setNetwork(getName())
                .setNetworkResource(network.build()).setProject(getProjectId()).build());

            state.save();
            waitForCompletion(operation);
        }

        refresh();
    }

    @Override
    public void doDelete(GyroUI ui, State state) {
        try (NetworksClient client = createClient(NetworksClient.class)) {
            Operation operation = client.delete(getProjectId(), getName());

            waitForCompletion(operation);
        }
    }

    private Network getNetwork(NetworksClient client) {
        Network network = null;

        try {
            network = client.get(GetNetworkRequest.newBuilder()
                .setProject(getProjectId())
                .setNetwork(getName())
                .build());

        } catch (NotFoundException | InvalidArgumentException ex) {
            // ignore
        }

        return network;
    }
}
