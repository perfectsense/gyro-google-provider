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
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.api.gax.rpc.InvalidArgumentException;
import com.google.api.gax.rpc.NotFoundException;
import com.google.cloud.compute.v1.AttachNetworkEndpointsNetworkEndpointGroupRequest;
import com.google.cloud.compute.v1.DeleteNetworkEndpointGroupRequest;
import com.google.cloud.compute.v1.DetachNetworkEndpointsNetworkEndpointGroupRequest;
import com.google.cloud.compute.v1.GetNetworkEndpointGroupRequest;
import com.google.cloud.compute.v1.InsertNetworkEndpointGroupRequest;
import com.google.cloud.compute.v1.NetworkEndpoint;
import com.google.cloud.compute.v1.NetworkEndpointGroup;
import com.google.cloud.compute.v1.NetworkEndpointGroupsAttachEndpointsRequest;
import com.google.cloud.compute.v1.NetworkEndpointGroupsClient;
import com.google.cloud.compute.v1.NetworkEndpointGroupsDetachEndpointsRequest;
import com.google.cloud.compute.v1.NetworkEndpointGroupsListEndpointsRequest;
import com.google.cloud.compute.v1.NetworkEndpointWithHealthStatus;
import com.google.cloud.compute.v1.Operation;
import gyro.core.GyroUI;
import gyro.core.Type;
import gyro.core.resource.Id;
import gyro.core.resource.Output;
import gyro.core.resource.Resource;
import gyro.core.resource.Updatable;
import gyro.core.scope.State;
import gyro.core.validation.Required;
import gyro.google.Copyable;

/**
 * Creates a network-endpoint-group.
 *
 * Example
 * -------
 *
 * .. code-block:: gyro
 *
 *     google::compute-network-endpoint-group network-endpoint-group-example
 *         name: "network-endpoint-group-example"
 *         network: $(google::network network-example-network-endpoint-group)
 *         subnet: $(google::subnet subnet-example-network-endpoint-group)
 *         default-port: 80
 *         zone: "us-east1-b"
 *
 *         endpoint
 *             instance: $(google::compute-instance gyro-network-endpoint-group-example-a)
 *         end
 *     end
 */
@Type("compute-network-endpoint-group")
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
     * The name of the network endpoint group.
     */
    @Required
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * The zone where the network endpoint group will reside. The zone needs to belong to the region of the subnet attached.
     */
    @Required
    public String getZone() {
        return zone;
    }

    public void setZone(String zone) {
        this.zone = zone;
    }

    /**
     * The default port for the network endpoint group. This port is used if no port is specified in the attached network endpoint.
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
     * The network to create the network endpoint group in.
     */
    @Required
    public NetworkResource getNetwork() {
        return network;
    }

    public void setNetwork(NetworkResource network) {
        this.network = network;
    }

    /**
     * The subnet to attach with the network endpoint group. All attached network endpoint needs to belong to this subnet.
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
    @Id
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
    public void copyFrom(NetworkEndpointGroup networkEndpointGroup) throws Exception {
        if (networkEndpointGroup.hasId()) {
            setId(String.valueOf(networkEndpointGroup.getId()));
        }

        if (networkEndpointGroup.hasName()) {
            setName(networkEndpointGroup.getName());
        }

        if (networkEndpointGroup.hasSelfLink()) {
            setSelfLink(networkEndpointGroup.getSelfLink());
        }

        if (networkEndpointGroup.hasNetworkEndpointType()) {
            setType(networkEndpointGroup.getNetworkEndpointType());
        }

        if (networkEndpointGroup.hasNetwork()) {
            setNetwork(findById(NetworkResource.class, networkEndpointGroup.getNetwork()));
        }

        if (networkEndpointGroup.hasSubnetwork()) {
            setSubnet(findById(SubnetworkResource.class, networkEndpointGroup.getSubnetwork()));
        }

        if (networkEndpointGroup.hasDefaultPort()) {
            setDefaultPort(networkEndpointGroup.getDefaultPort());
        }

        if (networkEndpointGroup.hasDescription()) {
            setDescription(networkEndpointGroup.getDescription());
        }

        if (networkEndpointGroup.hasSelfLink()) {
            setSize(networkEndpointGroup.getSize());
        }

        if (networkEndpointGroup.hasZone()) {
            setZone(networkEndpointGroup.getZone().substring(networkEndpointGroup.getZone().lastIndexOf("/") + 1));
        }

        setEndpoint(getNetworkEndpoint());
    }

    @Override
    public boolean doRefresh() throws Exception {
        try (NetworkEndpointGroupsClient client = createClient(NetworkEndpointGroupsClient.class)) {
            NetworkEndpointGroup networkEndpointGroup = getNetworkEndpointGroup(client);

            if (networkEndpointGroup == null) {
                return false;
            }

            copyFrom(networkEndpointGroup);

            return true;
        }
    }

    @Override
    public void doCreate(GyroUI ui, State state) throws Exception {
        try (NetworkEndpointGroupsClient client = createClient(NetworkEndpointGroupsClient.class)) {
            NetworkEndpointGroup.Builder builder = NetworkEndpointGroup.newBuilder();
            builder.setName(getName());
            builder.setDefaultPort(getDefaultPort());

            if (getNetwork() != null) {
                builder.setNetwork(getNetwork().getSelfLink());
            }

            if (getSubnet() != null) {
                builder.setSubnetwork(getSubnet().getSelfLink());
            }

            if (getType() != null) {
                builder.setNetworkEndpointType(getType());
            }

            if (getDescription() != null) {
                builder.setDescription(getDescription());
            }

            Operation operation = client.insertCallable().call(InsertNetworkEndpointGroupRequest.newBuilder()
                .setProject(getProjectId())
                .setZone(getZone())
                .setNetworkEndpointGroupResource(builder)
                .build());

            waitForCompletion(operation);

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
        }

        refresh();
    }

    @Override
    public void doUpdate(GyroUI ui, State state, Resource current, Set<String> changedFieldNames) throws Exception {
        try (NetworkEndpointGroupsClient client = createClient(NetworkEndpointGroupsClient.class)) {
            NetworkEndpointGroupResource oldResource = (NetworkEndpointGroupResource) current;
            saveNetworkEndpoint(client, oldResource.getEndpoint());
        }
    }

    @Override
    public void doDelete(GyroUI ui, State state) throws Exception {
        try (NetworkEndpointGroupsClient client = createClient(NetworkEndpointGroupsClient.class)) {
            Operation operation = client.deleteCallable().call(DeleteNetworkEndpointGroupRequest.newBuilder()
                .setProject(getProjectId())
                .setZone(getZone())
                .setNetworkEndpointGroup(getName())
                .build());

            waitForCompletion(operation);
        }
    }

    private List<NetworkEndpointResource> getNetworkEndpoint() {
        try (NetworkEndpointGroupsClient client = createClient(NetworkEndpointGroupsClient.class)) {
            NetworkEndpointGroupsListEndpointsRequest.Builder builder = NetworkEndpointGroupsListEndpointsRequest.newBuilder();
            builder.setHealthStatus(NetworkEndpointGroupsListEndpointsRequest.HealthStatus.SHOW.name());
            List<NetworkEndpointWithHealthStatus> endpoints = client.listNetworkEndpoints(getProjectId(), getZone(),
                getName(), builder.build()).getPage().getResponse().getItemsList();

            getEndpoint().clear();

            return endpoints.stream()
                .map(endpoint -> {
                    NetworkEndpointResource endpointResource = newSubresource(NetworkEndpointResource.class);
                    // API returns only the name not the instance self-link so reconstruct the self-link.
                    NetworkEndpoint.Builder endpointBuilder = endpoint.getNetworkEndpoint().toBuilder();
                    endpointBuilder.setInstance(getSelfLink().replaceFirst(
                        "/networkEndpointGroups/.*",
                        "/instances/" + endpoint.getNetworkEndpoint().getInstance()));

                    endpointResource.copyFrom(endpoint.toBuilder().setNetworkEndpoint(endpointBuilder.build()).build());
                    return endpointResource;
                }).collect(Collectors.toList());
        }
    }

    private void saveNetworkEndpoint(NetworkEndpointGroupsClient client, List<NetworkEndpointResource> oldEndpoints) {
        Operation operation;

        if (!oldEndpoints.isEmpty()) {
            NetworkEndpointGroupsDetachEndpointsRequest.Builder builder = NetworkEndpointGroupsDetachEndpointsRequest
                .newBuilder();
            builder.addAllNetworkEndpoints(oldEndpoints.stream()
                .map(NetworkEndpointResource::toNetworkEndpoint)
                .collect(Collectors.toList()));

            operation = client.detachNetworkEndpointsOperationCallable()
                .call(DetachNetworkEndpointsNetworkEndpointGroupRequest.newBuilder()
                    .setProject(getProjectId())
                    .setZone(getZone())
                    .setNetworkEndpointGroup(getName())
                    .setNetworkEndpointGroupsDetachEndpointsRequestResource(builder)
                    .build());

            waitForCompletion(operation);
        }

        if (!getEndpoint().isEmpty()) {
            NetworkEndpointGroupsAttachEndpointsRequest.Builder builder = NetworkEndpointGroupsAttachEndpointsRequest.newBuilder();
            builder.addAllNetworkEndpoints(getEndpoint().stream()
                .map(NetworkEndpointResource::toNetworkEndpoint)
                .collect(Collectors.toList()));

            operation = client.attachNetworkEndpointsCallable()
                .call(AttachNetworkEndpointsNetworkEndpointGroupRequest.newBuilder()
                    .setProject(getProjectId())
                    .setZone(getZone())
                    .setNetworkEndpointGroup(getName())
                    .setNetworkEndpointGroupsAttachEndpointsRequestResource(builder)
                    .build());

            waitForCompletion(operation);
        }
    }

    private NetworkEndpointGroup getNetworkEndpointGroup(NetworkEndpointGroupsClient client) {
        NetworkEndpointGroup instanceTemplate = null;

        try {
            instanceTemplate = client.get(GetNetworkEndpointGroupRequest.newBuilder()
                .setProject(getProjectId())
                .setNetworkEndpointGroup(getName())
                .setZone(getZone())
                .build());

        } catch (NotFoundException | InvalidArgumentException ex) {
            // ignore
        }

        return instanceTemplate;
    }
}
