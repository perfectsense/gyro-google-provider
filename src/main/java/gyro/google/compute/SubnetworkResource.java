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
import com.google.cloud.compute.v1.GetSubnetworkRequest;
import com.google.cloud.compute.v1.Operation;
import com.google.cloud.compute.v1.Subnetwork;
import com.google.cloud.compute.v1.SubnetworksClient;
import com.google.cloud.compute.v1.SubnetworksSetPrivateIpGoogleAccessRequest;
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
 * Creates a subnet.
 *
 * Example
 * -------
 *
 * .. code-block:: gyro
 *
 *     google::compute-subnet subnet-example
 *         name: "subnet-example"
 *         description: "subnet-example-description"
 *         ip-cidr-range: "10.0.0.0/16"
 *         network: $(google::compute-network network-example-subnet)
 *         region: "us-east1"
 *     end
 */
@Type("compute-subnet")
public class SubnetworkResource extends ComputeResource implements Copyable<Subnetwork> {

    private String name;
    private String description;
    private String ipCidrRange;
    private NetworkResource network;
    private String region;
    private Boolean enableFlowLogs;
    private Boolean privateIpGoogleAccess;

    // Read-only
    private String id;
    private String selfLink;

    /**
     * The name of the subnet.
     */
    @Required
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * The description of the subnet.
     */
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * The IPv4 network range for the subnet, in CIDR notation.
     */
    @Required
    public String getIpCidrRange() {
        return ipCidrRange;
    }

    public void setIpCidrRange(String ipCidrRange) {
        this.ipCidrRange = ipCidrRange;
    }

    /**
     * The network to create this subnet in.
     */
    @Required
    public NetworkResource getNetwork() {
        return network;
    }

    public void setNetwork(NetworkResource network) {
        this.network = network;
    }

    /**
     * The region to create this subnet in.
     */
    @Required
    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    /**
     * When true, enables flow logs. Defaults to ``false``.
     */
    @Updatable
    public Boolean getEnableFlowLogs() {
        if (enableFlowLogs == null) {
            enableFlowLogs = false;
        }

        return enableFlowLogs;
    }

    public void setEnableFlowLogs(Boolean enableFlowLogs) {
        this.enableFlowLogs = enableFlowLogs;
    }

    /**
     * When true, allows virtual machines in this subnet that only have private IPs to access Google APIs and services. See `Configuring Private Google Access <https://cloud.google.com/vpc/docs/configure-private-google-access>`_. Defaults to ``false``.
     */
    @Updatable
    public Boolean getPrivateIpGoogleAccess() {
        if (privateIpGoogleAccess == null) {
            privateIpGoogleAccess = false;
        }

        return privateIpGoogleAccess;
    }

    public void setPrivateIpGoogleAccess(Boolean privateIpGoogleAccess) {
        this.privateIpGoogleAccess = privateIpGoogleAccess;
    }

    /**
     * The Id of the subnet.
     */
    @Output
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    /**
     * The fully-qualified URL linking back to the subnetwork.
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
    public void copyFrom(Subnetwork subnetwork) {
        setId(String.valueOf(subnetwork.getId()));
        setSelfLink(subnetwork.getSelfLink());
        setDescription(subnetwork.getDescription());
        setIpCidrRange(subnetwork.getIpCidrRange());
        setEnableFlowLogs(subnetwork.getEnableFlowLogs());
        setPrivateIpGoogleAccess(subnetwork.getPrivateIpGoogleAccess());
        setName(subnetwork.getName());
        setNetwork(findById(
            NetworkResource.class,
            subnetwork.getNetwork()));
        setRegion(subnetwork.getRegion().substring(subnetwork.getRegion().lastIndexOf("/") + 1));
        setSelfLink(subnetwork.getSelfLink());
    }

    @Override
    public boolean doRefresh() throws Exception {
        try (SubnetworksClient client = createClient(SubnetworksClient.class)) {
            Subnetwork subnetwork = getSubnetwork(client);

            if (subnetwork == null) {
                return false;
            }

            copyFrom(subnetwork);

            return true;
        }
    }

    @Override
    public void doCreate(GyroUI ui, State state) throws Exception {
        Subnetwork subnetwork = Subnetwork.newBuilder().setName(getName()).setNetwork(getNetwork().getSelfLink())
            .setDescription(getDescription()).setIpCidrRange(getIpCidrRange()).setEnableFlowLogs(getEnableFlowLogs())
            .setPrivateIpGoogleAccess(getPrivateIpGoogleAccess()).build();

        try (SubnetworksClient client = createClient(SubnetworksClient.class)) {
            Operation operation = client.insert(getProjectId(), getRegion(), subnetwork);

            waitForCompletion(operation);
        }

        refresh();
    }

    @Override
    public void doUpdate(GyroUI ui, State state, Resource current, Set<String> changedFieldNames) throws Exception {
        Operation operation;

        if (changedFieldNames.contains("enable-flow-logs")) {
            try (SubnetworksClient client = createClient(SubnetworksClient.class)) {
                Subnetwork.Builder builder = Subnetwork.newBuilder(getSubnetwork(client));
                builder.setEnableFlowLogs(getEnableFlowLogs());
                operation = client.patch(getProjectId(), getRegion(), getName(), builder.build());

                waitForCompletion(operation);
            }
        }

        if (changedFieldNames.contains("private-ip-google-access")) {
            try (SubnetworksClient client = createClient(SubnetworksClient.class)) {
                SubnetworksSetPrivateIpGoogleAccessRequest flag = SubnetworksSetPrivateIpGoogleAccessRequest.newBuilder()
                    .setPrivateIpGoogleAccess(getPrivateIpGoogleAccess()).build();
                operation = client.setPrivateIpGoogleAccess(getProjectId(), getRegion(), getName(), flag);

                waitForCompletion(operation);
            }
        }
    }

    @Override
    public void doDelete(GyroUI ui, State state) {
        try (SubnetworksClient client = createClient(SubnetworksClient.class)) {
            Operation operation = client.delete(getProjectId(), getRegion(), getName());

            waitForCompletion(operation);
        }
    }

    private Subnetwork getSubnetwork(SubnetworksClient client) {
        Subnetwork subnetwork = null;

        try {
            subnetwork = client.get(GetSubnetworkRequest.newBuilder()
                .setProject(getProjectId())
                .setSubnetwork(getName())
                .setRegion(getRegion())
                .build());

        } catch (NotFoundException | InvalidArgumentException ex) {
            // ignore
        }

        return subnetwork;
    }
}