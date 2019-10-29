package gyro.google.compute;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.compute.Compute;
import com.google.api.services.compute.model.Network;
import com.google.api.services.compute.model.NetworkRoutingConfig;
import com.google.api.services.compute.model.Operation;
import gyro.core.GyroException;
import gyro.core.GyroUI;
import gyro.core.resource.Id;
import gyro.core.resource.Resource;
import gyro.core.resource.Updatable;
import gyro.core.Type;
import gyro.core.resource.Output;
import gyro.core.scope.State;
import gyro.core.validation.Required;
import gyro.core.validation.ValidStrings;
import gyro.google.Copyable;

import java.io.IOException;
import java.util.Set;

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
    @ValidStrings({"GLOBAL", "REGIONAL"})
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
        Compute client = creatClient(Compute.class);

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
        Compute client = creatClient(Compute.class);

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
        Compute client = creatClient(Compute.class);

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
        Compute compute = creatClient(Compute.class);

        try {
            compute.networks().delete(getProjectId(), getName()).execute();
        } catch (IOException ex) {
            throw new GyroException(ex.getMessage(), ex.getCause());
        }
    }
}
