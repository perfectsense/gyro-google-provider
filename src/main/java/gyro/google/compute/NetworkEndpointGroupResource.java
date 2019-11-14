package gyro.google.compute;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.compute.Compute;
import com.google.api.services.compute.model.NetworkEndpointGroup;
import com.google.api.services.compute.model.NetworkEndpointGroupsAttachEndpointsRequest;
import com.google.api.services.compute.model.NetworkEndpointGroupsDetachEndpointsRequest;
import com.google.api.services.compute.model.NetworkEndpointGroupsListEndpointsRequest;
import com.google.api.services.compute.model.NetworkEndpointWithHealthStatus;
import com.google.api.services.compute.model.Operation;
import com.google.cloud.compute.v1.ProjectGlobalNetworkName;
import gyro.core.GyroException;
import gyro.core.GyroUI;
import gyro.core.Type;
import gyro.core.resource.Output;
import gyro.core.resource.Resource;
import gyro.core.resource.Updatable;
import gyro.core.scope.State;
import gyro.core.validation.Required;
import gyro.google.Copyable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Creates a network-endpoint-group.
 *
 * Example
 * -------
 *
 * .. code-block:: gyro
 *
 *     google::network-endpoint-group network-endpoint-group-example
 *         name: "network-endpoint-group-example"
 *         network: $(google::network network-example-network-endpoint-group)
 *         subnet: $(google::subnet subnet-example-network-endpoint-group)
 *         default-port: 80
 *         zone: "us-east1-b"
 *     end
 */
@Type("network-endpoint-group")
public class NetworkEndpointGroupResource extends ComputeResource implements Copyable<NetworkEndpointGroup> {
    private String name;
    private String zone;
    private Integer defaultPort;
    private String description;
    private NetworkResource network;
    private SubnetworkResource subnet;
    private String type;
    private List<NetworkEndpointResource> endpoint;

    // Read-only
    private String id;
    private String selfLink;
    private Integer size;

    /**
     * The name of the network endpoint group. (Required)
     */
    @Required
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * The zone where the network endpoint group will reside. The zone needs to belong to the region of the subnet attached. (Required)
     */
    @Required
    public String getZone() {
        return zone;
    }

    public void setZone(String zone) {
        this.zone = zone;
    }

    /**
     * The default port for the network endpoint group. This port is used if no port is specified in the attached network endpoint. (Required)
     */
    @Required
    public Integer getDefaultPort() {
        return defaultPort;
    }

    public void setDefaultPort(Integer defaultPort) {
        this.defaultPort = defaultPort;
    }

    /**
     * The description for the network endpoint group.
     */
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * The network to create the network endpoint group in. (Required)
     */
    @Required
    public NetworkResource getNetwork() {
        return network;
    }

    public void setNetwork(NetworkResource network) {
        this.network = network;
    }

    /**
     * The subnet to attach with the network endpoint group. All attached network endpoint needs to belong to this subnet. (Required)
     */
    @Required
    public SubnetworkResource getSubnet() {
        return subnet;
    }

    public void setSubnet(SubnetworkResource subnet) {
        this.subnet = subnet;
    }

    /**
     * The type of the network endpoint group. Currently only supported value is ``GCE_VM_IP_PORT``. Defaults to ``GCE_VM_IP_PORT``.
     */
    public String getType() {
        if (type == null) {
            type = "GCE_VM_IP_PORT";
        }

        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    /**
     * A set of network endpoint to attach to the network endpoint group.
     *
     * @subresource gyro.google.compute.NetworkEndpointResource
     */
    @Updatable
    public List<NetworkEndpointResource> getEndpoint() {
        if (endpoint == null) {
            endpoint = new ArrayList<>();
        }

        return endpoint;
    }

    public void setEndpoint(List<NetworkEndpointResource> endpoint) {
        this.endpoint = endpoint;
    }

    /**
     * The Id of the network endpoint group
     */
    @Output
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    /**
     * The fully qualified url of the network endpoint group.
     */
    @Output
    public String getSelfLink() {
        return selfLink;
    }

    public void setSelfLink(String selfLink) {
        this.selfLink = selfLink;
    }

    /**
     * The size of the network endpoint group. Specifies how many network endpoints are part of this group.
     */
    @Output
    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    @Override
    public void copyFrom(NetworkEndpointGroup networkEndpointGroup) {
        setDefaultPort(networkEndpointGroup.getDefaultPort());
        setDescription(networkEndpointGroup.getDescription());
        setId(networkEndpointGroup.getId().toString());
        setName(networkEndpointGroup.getName());
        setNetwork(findById(NetworkResource.class, networkEndpointGroup.getNetwork().substring(networkEndpointGroup.getNetwork().lastIndexOf("/") + 1)));
        setSubnet(findById(SubnetworkResource.class, networkEndpointGroup.getSubnetwork().substring(networkEndpointGroup.getSubnetwork().lastIndexOf("/") + 1)));
        setType(networkEndpointGroup.getNetworkEndpointType());
        setSelfLink(networkEndpointGroup.getSelfLink());
        setSize(networkEndpointGroup.getSize());
        setZone(networkEndpointGroup.getZone().substring(networkEndpointGroup.getZone().lastIndexOf("/") + 1));
        setEndpoint(getNetworkEndpoint());
    }

    @Override
    public boolean refresh() {
        Compute client = createComputeClient();

        try {
            NetworkEndpointGroup networkEndpointGroup = client.networkEndpointGroups().get(getProjectId(), getZone(), getName()).execute();
            copyFrom(networkEndpointGroup);

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

        NetworkEndpointGroup networkEndpointGroup = new NetworkEndpointGroup();
        networkEndpointGroup.setName(getName());
        networkEndpointGroup.setNetwork(ProjectGlobalNetworkName.format(getNetwork().getName(), getProjectId()));
        networkEndpointGroup.setSubnetwork(getSubnet().getSelfLink());
        networkEndpointGroup.setNetworkEndpointType(getType());
        networkEndpointGroup.setDefaultPort(getDefaultPort());
        networkEndpointGroup.setDescription(getDescription());

        try {
            Operation operation = client.networkEndpointGroups().insert(getProjectId(), getZone(), networkEndpointGroup).execute();
            Operation.Error error = waitForCompletion(client, operation);
            if (error != null) {
                throw new GyroException(error.toPrettyString());
            }

            // Saves the state before trying to save the endpoints
            // Calling refresh resets values to what is present in the cloud
            // So temporarily save the current endpoints so that, post refresh it can be set and saved
            // Refresh is needed to save endpoint and size which change based on how many endpoints are added or removed
            if (!getEndpoint().isEmpty()) {
                List<NetworkEndpointResource> endpoint = new ArrayList<>(getEndpoint());
                refresh();
                state.save();
                setEndpoint(endpoint);
                saveNetworkEndpoint(client, Collections.emptyList());
            }
            refresh();
        } catch (GoogleJsonResponseException je) {
                throw new GyroException(je.getDetails().getMessage());
        } catch (Exception ex) {
            throw new GyroException(ex.getMessage(), ex.getCause());
        }
    }

    @Override
    public void update(GyroUI ui, State state, Resource current, Set<String> changedFieldNames) {
        Compute client = createComputeClient();

        NetworkEndpointGroupResource oldResource = (NetworkEndpointGroupResource) current;
        saveNetworkEndpoint(client, oldResource.getEndpoint());
    }

    @Override
    public void delete(GyroUI ui, State state) {
        Compute client = createComputeClient();

        try {
            Operation operation = client.networkEndpointGroups().delete(getProjectId(), getZone(), getName()).execute();
            Operation.Error error = waitForCompletion(client, operation);
            if (error != null) {
                throw new GyroException(error.toPrettyString());
            }
        } catch (GoogleJsonResponseException je) {
            throw new GyroException(je.getDetails().getMessage());
        } catch (Exception ex) {
            throw new GyroException(ex.getMessage(), ex.getCause());
        }
    }

    private List<NetworkEndpointResource> getNetworkEndpoint() {
        Compute client = createComputeClient();

        try {
            NetworkEndpointGroupsListEndpointsRequest request = new NetworkEndpointGroupsListEndpointsRequest();
            request.setHealthStatus("SHOW");
            List<NetworkEndpointWithHealthStatus> endpoints = client.networkEndpointGroups().listNetworkEndpoints(getProjectId(), getZone(), getName(), request).execute().getItems();

            getEndpoint().clear();
            return endpoints != null ? endpoints.stream().map(endpoint -> {
                NetworkEndpointResource endpointResource = newSubresource(NetworkEndpointResource.class);
                endpointResource.copyFrom(endpoint);
                return endpointResource;
            }).collect(Collectors.toList()) : Collections.emptyList();
        } catch (GoogleJsonResponseException je) {
            throw new GyroException(je.getDetails().getMessage());
        } catch (Exception ex) {
            throw new GyroException(ex.getMessage(), ex.getCause());
        }

    }

    private void saveNetworkEndpoint(Compute client, List<NetworkEndpointResource> oldEndpoints) {
        try {
            Operation operation;
            Operation.Error error;

            if (!oldEndpoints.isEmpty()) {
                NetworkEndpointGroupsDetachEndpointsRequest detachRequest = new NetworkEndpointGroupsDetachEndpointsRequest();
                detachRequest.setNetworkEndpoints(oldEndpoints.stream().map(NetworkEndpointResource::toNetworkEndpoint).collect(Collectors.toList()));

                operation = client.networkEndpointGroups().detachNetworkEndpoints(getProjectId(), getZone(), getName(), detachRequest).execute();
                error = waitForCompletion(client, operation);
                if (error != null) {
                    throw new GyroException(error.toPrettyString());
                }
            }

            if (!getEndpoint().isEmpty()) {
                NetworkEndpointGroupsAttachEndpointsRequest attachRequest = new NetworkEndpointGroupsAttachEndpointsRequest();
                attachRequest.setNetworkEndpoints(getEndpoint().stream().map(NetworkEndpointResource::toNetworkEndpoint).collect(Collectors.toList()));

                operation = client.networkEndpointGroups().attachNetworkEndpoints(getProjectId(), getZone(), getName(), attachRequest).execute();
                error = waitForCompletion(client, operation);
                if (error != null) {
                    throw new GyroException(error.toPrettyString());
                }
            }
        } catch (GoogleJsonResponseException je) {
            throw new GyroException(je.getDetails().getMessage());
        } catch (Exception ex) {
            throw new GyroException(ex.getMessage(), ex.getCause());
        }
    }
}
