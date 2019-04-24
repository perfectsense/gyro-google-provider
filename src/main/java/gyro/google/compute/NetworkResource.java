package gyro.google.compute;

import com.google.cloud.compute.v1.Network;
import com.google.cloud.compute.v1.NetworkClient;
import com.google.cloud.compute.v1.NetworkRoutingConfig;
import com.google.cloud.compute.v1.Operation;
import com.google.cloud.compute.v1.ProjectGlobalNetworkName;
import com.psddev.dari.util.ObjectUtils;
import gyro.core.resource.Resource;
import gyro.core.resource.ResourceDiffProperty;
import gyro.core.resource.ResourceName;
import gyro.google.GoogleResource;

import java.util.Collections;
import java.util.Set;

@ResourceName("network")
public class NetworkResource extends GoogleResource {
    private String networkName;
    private String description;
    private Boolean globalDynamicRouting;

    // Read-only
    private String networkId;

    public String getNetworkName() {
        return networkName;
    }

    public void setNetworkName(String networkName) {
        this.networkName = networkName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @ResourceDiffProperty(updatable = true)
    public Boolean getGlobalDynamicRouting() {
        if (globalDynamicRouting == null) {
            globalDynamicRouting = false;
        }

        return globalDynamicRouting;
    }

    public void setGlobalDynamicRouting(Boolean globalDynamicRouting) {
        this.globalDynamicRouting = globalDynamicRouting;
    }

    public String getNetworkId() {
        return networkId;
    }

    public void setNetworkId(String networkId) {
        this.networkId = networkId;
    }

    @Override
    public boolean refresh() {
        NetworkClient networkClient = creatClient(NetworkClient.class);

        Network network = networkClient.getNetwork(ProjectGlobalNetworkName.of(getNetworkName(),getProjectId()));
        setNetworkId(network.getId());
        setDescription(network.getDescription());
        setGlobalDynamicRouting(network.getRoutingConfig().getRoutingMode().equals("GLOBAL"));
        return true;
    }

    @Override
    public void create() {
        NetworkClient networkClient = creatClient(NetworkClient.class);

        Network network = Network.newBuilder()
            .setAutoCreateSubnetworks(false)
            .setName(getNetworkName())
            .setDescription(getDescription())
            .setRoutingConfig(
                NetworkRoutingConfig.newBuilder()
                    .setRoutingMode(getGlobalDynamicRouting() ? "GLOBAL" : "REGIONAL")
                    .build()
            ).build();

        Operation operation = networkClient.insertNetwork(getProjectId(), network);
        setNetworkId(operation.getId());
    }

    @Override
    public void update(Resource current, Set<String> changedProperties) {
        NetworkClient networkClient = creatClient(NetworkClient.class);

        Network network = networkClient.getNetwork(ProjectGlobalNetworkName.of(getNetworkName(),getProjectId()));

        Network network1 = network.toBuilder().setRoutingConfig(
            NetworkRoutingConfig.newBuilder()
                .setRoutingMode(getGlobalDynamicRouting() ? "GLOBAL" : "REGIONAL")
                .build()
        ).build();

        //primary option
        networkClient.patchNetwork(ProjectGlobalNetworkName.of(getNetworkName(),getProjectId()),
            network1,
            Collections.emptyList());


        //secondary option
        /*PatchNetworkHttpRequest r = PatchNetworkHttpRequest.newBuilder()
            .setNetwork(ProjectGlobalNetworkName.of(getNetworkName(),getProjectId()).toString())
            .setNetworkResource(network1)
            .addFieldMask("routingConfig.routingMode")
            .build();

        networkClient.patchNetwork(r);*/
    }

    @Override
    public void delete() {
        NetworkClient networkClient = creatClient(NetworkClient.class);

        networkClient.deleteNetwork(ProjectGlobalNetworkName.of(getNetworkName(),getProjectId()));
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
