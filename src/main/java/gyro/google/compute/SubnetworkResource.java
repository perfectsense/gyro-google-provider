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
import java.util.Set;
import java.util.stream.Collectors;

import com.google.api.services.compute.Compute;
import com.google.api.services.compute.model.Operation;
import com.google.api.services.compute.model.Subnetwork;
import com.google.api.services.compute.model.SubnetworksSetPrivateIpGoogleAccessRequest;
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
    private List<SubnetworkSecondaryRange> secondaryIpRange;

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
     * The secondary IP ranges that may be allocated for this subnet.
     *
     * @subresource gyro.google.compute.SubnetworkSecondaryRange
     */
    @Updatable
    public List<SubnetworkSecondaryRange> getSecondaryIpRange() {
        if (secondaryIpRange == null) {
            secondaryIpRange = new ArrayList<>();
        }

        return secondaryIpRange;
    }

    public void setSecondaryIpRange(List<SubnetworkSecondaryRange> secondaryIpRanges) {
        this.secondaryIpRange = secondaryIpRanges;
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
        setId(subnetwork.getId().toString());
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

        getSecondaryIpRange().clear();
        List<com.google.api.services.compute.model.SubnetworkSecondaryRange> secondaryIpRanges = subnetwork.getSecondaryIpRanges();
        if (secondaryIpRanges != null) {
            secondaryIpRanges.forEach(ipRange -> {
                SubnetworkSecondaryRange secondaryRange = newSubresource(SubnetworkSecondaryRange.class);
                secondaryRange.copyFrom(ipRange);
                getSecondaryIpRange().add(secondaryRange);
            });
        }
    }

    @Override
    public boolean doRefresh() throws Exception {
        Compute client = createComputeClient();

        Subnetwork subnetwork = client.subnetworks().get(getProjectId(), getRegion(), getName()).execute();
        copyFrom(subnetwork);

        return true;
    }

    @Override
    public void doCreate(GyroUI ui, State state) throws Exception {
        Compute client = createComputeClient();

        Subnetwork subnetwork = new Subnetwork();
        subnetwork.setName(getName());
        subnetwork.setNetwork(getNetwork().getSelfLink());
        subnetwork.setDescription(getDescription());
        subnetwork.setIpCidrRange(getIpCidrRange());
        subnetwork.setEnableFlowLogs(getEnableFlowLogs());
        subnetwork.setPrivateIpGoogleAccess(getPrivateIpGoogleAccess());

        if (!getSecondaryIpRange().isEmpty()) {
            subnetwork.setSecondaryIpRanges(getSecondaryIpRange().stream()
                .map(SubnetworkSecondaryRange::toSecondaryIpRange)
                .collect(Collectors.toList()));
        }

        Compute.Subnetworks.Insert insert = client.subnetworks().insert(getProjectId(), getRegion(), subnetwork);
        Operation operation = insert.execute();
        waitForCompletion(client, operation);

        refresh();
    }

    @Override
    public void doUpdate(GyroUI ui, State state, Resource current, Set<String> changedFieldNames) throws Exception {
        Compute client = createComputeClient();

        Operation operation;
        if (changedFieldNames.contains("enable-flow-logs")) {
            Subnetwork subnetwork = client.subnetworks().get(getProjectId(), getRegion(), getName()).execute();
            subnetwork.setEnableFlowLogs(getEnableFlowLogs());
            operation = client.subnetworks().patch(getProjectId(), getRegion(), getName(), subnetwork).execute();
            waitForCompletion(client, operation);
        }

        if (changedFieldNames.contains("private-ip-google-access")) {
            SubnetworksSetPrivateIpGoogleAccessRequest flag = new SubnetworksSetPrivateIpGoogleAccessRequest();
            flag.setPrivateIpGoogleAccess(getPrivateIpGoogleAccess());
            operation = client.subnetworks().setPrivateIpGoogleAccess(getProjectId(), getRegion(), getName(), flag).execute();
            waitForCompletion(client, operation);
        }

        if (changedFieldNames.contains("secondary-ip-range")) {
            Subnetwork subnetwork = client.subnetworks().get(getProjectId(), getRegion(), getName()).execute();
            subnetwork.setSecondaryIpRanges(getSecondaryIpRange().stream()
                .map(SubnetworkSecondaryRange::toSecondaryIpRange)
                .collect(Collectors.toList()));
            operation = client.subnetworks().patch(getProjectId(), getRegion(), getName(), subnetwork).execute();
            waitForCompletion(client, operation);
        }
    }

    @Override
    public void doDelete(GyroUI ui, State state) throws Exception {
        Compute client = createComputeClient();

        Operation operation = client.subnetworks().delete(getProjectId(), getRegion(), getName()).execute();
        waitForCompletion(client, operation);
    }
}
