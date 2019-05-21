package gyro.google.compute;

import com.google.api.services.compute.Compute;
import com.google.api.services.compute.model.Network;
import com.google.api.services.compute.model.NetworkRoutingConfig;
import com.google.api.services.compute.model.Operation;
import com.psddev.dari.util.ObjectUtils;
import gyro.core.GyroException;
import gyro.core.resource.Resource;
import gyro.core.resource.Updatable;
import gyro.core.resource.ResourceType;
import gyro.core.resource.Output;

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
 *         network-name: "vpc-example"
 *         description: "vpc-example-desc"
 *         global-dynamic-routing: false
 *     end
 */
@ResourceType("network")
public class NetworkResource extends ComputeResource {
    private String networkName;
    private String description;
    private Boolean globalDynamicRouting;

    // Read-only
    private String networkId;

    /**
     * The name of the network. (Required)
     */
    public String getNetworkName() {
        return networkName;
    }

    public void setNetworkName(String networkName) {
        this.networkName = networkName;
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
     * Enable/Disable global dynamic routing. Defaults to disabled.
     */
    @Updatable
    public Boolean getGlobalDynamicRouting() {
        if (globalDynamicRouting == null) {
            globalDynamicRouting = false;
        }

        return globalDynamicRouting;
    }

    public void setGlobalDynamicRouting(Boolean globalDynamicRouting) {
        this.globalDynamicRouting = globalDynamicRouting;
    }

    /**
     * The Id of the network.
     */
    @Output
    public String getNetworkId() {
        return networkId;
    }

    public void setNetworkId(String networkId) {
        this.networkId = networkId;
    }

    @Override
    public boolean refresh() {
        Compute client = creatClient(Compute.class);

        try {
            Network network = client.networks().get(getProjectId(), getNetworkName()).execute();

            setNetworkId(network.getId().toString());
            setGlobalDynamicRouting(network.getRoutingConfig().getRoutingMode().equals("GLOBAL"));
            setDescription(network.getDescription());
        } catch (IOException ex) {
            return false;
        }

        return true;
    }

    @Override
    public void create() {
        Compute client = creatClient(Compute.class);

        Network network = new Network();
        network.setName(getNetworkName());
        network.setDescription(getDescription());
        network.setAutoCreateSubnetworks(false);

        NetworkRoutingConfig networkRoutingConfig = new NetworkRoutingConfig();
        networkRoutingConfig.setRoutingMode(getGlobalDynamicRouting() ? "GLOBAL" : "REGIONAL");
        network.setRoutingConfig(networkRoutingConfig);

        try {
            Compute.Networks.Insert insert = client.networks().insert(getProjectId(), network);

            Operation operation = insert.execute();
            Operation.Error error = waitForCompletion(client, operation);
            if (error != null) {
                throw new GyroException(error.toPrettyString());
            }
        } catch (Exception ex) {
            throw new GyroException(ex.getMessage(), ex.getCause());
        }

        refresh();
    }

    @Override
    public void update(Resource current, Set<String> changedFieldNames) {
        Compute client = creatClient(Compute.class);

        try {
            Network network = client.networks().get(getProjectId(), getNetworkName()).execute();
            if (changedFieldNames.contains("global-dynamic-routing")) {

                NetworkRoutingConfig networkRoutingConfig = new NetworkRoutingConfig();
                networkRoutingConfig.setRoutingMode(getGlobalDynamicRouting() ? "GLOBAL" : "REGIONAL");
                network.setRoutingConfig(networkRoutingConfig);
                client.networks().patch(getProjectId(), getNetworkName(), network).execute();
            }
        } catch (IOException ex) {
            throw new GyroException(ex.getMessage(), ex.getCause());
        }
    }

    @Override
    public void delete() {
        Compute compute = creatClient(Compute.class);

        try {
            compute.networks().delete(getProjectId(), getNetworkName()).execute();
        } catch (IOException ex) {
            throw new GyroException(ex.getMessage(), ex.getCause());
        }
    }

    @Override
    public String toDisplayString() {

        StringBuilder sb = new StringBuilder();

        sb.append("network");

        if (!ObjectUtils.isBlank(getNetworkName())) {
            sb.append(" ( ").append(getNetworkName()).append(" )");
        }

        if (!ObjectUtils.isBlank(getNetworkId())) {
            sb.append(" - ").append(getNetworkId());
        }

        return sb.toString();
    }
}
