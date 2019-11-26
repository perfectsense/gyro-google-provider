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
import java.util.Set;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.compute.Compute;
import com.google.api.services.compute.model.Network;
import com.google.api.services.compute.model.NetworkRoutingConfig;
import com.google.api.services.compute.model.Operation;
import gyro.core.GyroException;
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
 *     google::network network-example
 *         name: "vpc-example"
 *         description: "vpc-example-desc"
 *         routing-mode: "GLOBAL"
 *     end
 */
@Type("network")
public class NetworkResource extends ComputeResource implements Copyable<Network> {

    private String name;
    private String description;
    private String routingMode;

    // Read-only
    private String id;

    /**
     * The name of the network. (Required)
     */
    @Id
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
     * The routing mode for the network. Valid values are ``GLOBAL`` or ``REGIONAL``.
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

    @Override
    public void copyFrom(Network network) {
        setId(network.getId().toString());
        setRoutingMode(network.getRoutingConfig().getRoutingMode());
        setDescription(network.getDescription());
        setName(network.getName());
    }

    @Override
    public boolean refresh() {
        Compute client = createComputeClient();

        try {
            Network network = client.networks().get(getProjectId(), getName()).execute();
            copyFrom(network);

            return true;
        } catch (GoogleJsonResponseException je) {
            if (je.getDetails().getMessage().matches("The resource (.*) was not found")) {
                return false;
            } else {
                throw new GyroException(je.getDetails().getMessage());
            }
        } catch (IOException ex) {
            throw new GyroException(ex.getMessage(), ex.getCause());
        }
    }

    @Override
    public void create(GyroUI ui, State state) {
        Compute client = createComputeClient();

        Network network = new Network();
        network.setName(getName());
        network.setDescription(getDescription());
        network.setAutoCreateSubnetworks(false);

        NetworkRoutingConfig networkRoutingConfig = new NetworkRoutingConfig();
        networkRoutingConfig.setRoutingMode(getRoutingMode());
        network.setRoutingConfig(networkRoutingConfig);

        try {
            Compute.Networks.Insert insert = client.networks().insert(getProjectId(), network);
            Operation operation = insert.execute();
            Operation.Error error = waitForCompletion(client, operation);
            if (error != null) {
                throw new GyroException(error.toPrettyString());
            }

            refresh();
        } catch (Exception ex) {
            throw new GyroException(ex.getMessage(), ex.getCause());
        }
    }

    @Override
    public void update(GyroUI ui, State state, Resource current, Set<String> changedFieldNames) {
        Compute client = createComputeClient();

        try {
            NetworkRoutingConfig networkRoutingConfig = new NetworkRoutingConfig();
            networkRoutingConfig.setRoutingMode(getRoutingMode());

            Network network = client.networks().get(getProjectId(), getName()).execute();
            network.setRoutingConfig(networkRoutingConfig);

            client.networks().patch(getProjectId(), getName(), network).execute();
            refresh();
        } catch (IOException ex) {
            throw new GyroException(ex.getMessage(), ex.getCause());
        }
    }

    @Override
    public void delete(GyroUI ui, State state) {
        Compute compute = createComputeClient();

        try {
            compute.networks().delete(getProjectId(), getName()).execute();
        } catch (IOException ex) {
            throw new GyroException(ex.getMessage(), ex.getCause());
        }
    }
}
