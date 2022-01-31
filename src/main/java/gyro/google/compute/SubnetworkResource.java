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

import com.google.api.gax.longrunning.OperationFuture;
import com.google.api.gax.rpc.InvalidArgumentException;
import com.google.api.gax.rpc.NotFoundException;
import com.google.cloud.compute.v1.GetSubnetworkRequest;
import com.google.cloud.compute.v1.InsertSubnetworkRequest;
import com.google.cloud.compute.v1.Operation;
import com.google.cloud.compute.v1.PatchSubnetworkRequest;
import com.google.cloud.compute.v1.SetPrivateIpGoogleAccessSubnetworkRequest;
import com.google.cloud.compute.v1.Subnetwork;
import com.google.cloud.compute.v1.SubnetworksClient;
import com.google.cloud.compute.v1.SubnetworksSetPrivateIpGoogleAccessRequest;
import gyro.core.GyroException;
import gyro.core.GyroUI;
import gyro.core.Type;
import gyro.core.resource.Id;
import gyro.core.resource.Output;
import gyro.core.resource.Resource;
import gyro.core.resource.Updatable;
import gyro.core.scope.State;
import gyro.core.validation.DependsOn;
import gyro.core.validation.Required;
import gyro.core.validation.ValidationError;
import gyro.google.Copyable;
import gyro.google.util.Utils;

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
    private SubnetworkLogConfig logConfig;

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
     * The log config for this subnetwork.
     */
    @Updatable
    @DependsOn("enable-flow-logs")
    public SubnetworkLogConfig getLogConfig() {
        return logConfig;
    }

    public void setLogConfig(SubnetworkLogConfig logConfig) {
        this.logConfig = logConfig;
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
    public void copyFrom(Subnetwork model) {
        setName(model.getName());

        if (model.hasId()) {
            setId(String.valueOf(model.getId()));
        }

        if (model.hasSelfLink()) {
            setSelfLink(model.getSelfLink());
        }

        if (model.hasDescription()) {
            setDescription(model.getDescription());
        }

        if (model.hasIpCidrRange()) {
            setIpCidrRange(model.getIpCidrRange());
        }

        if (model.hasEnableFlowLogs()) {
            setEnableFlowLogs(model.getEnableFlowLogs());
        }

        if (model.hasPrivateIpGoogleAccess()) {
            setPrivateIpGoogleAccess(model.getPrivateIpGoogleAccess());
        }

        if (model.hasNetwork()) {
            setNetwork(findById(
                NetworkResource.class,
                model.getNetwork()));
        }

        if (model.hasRegion()) {
            setRegion(Utils.extractName(model.getRegion()));
        }

        getSecondaryIpRange().clear();
        if (!model.getSecondaryIpRangesList().isEmpty()) {
            List<com.google.cloud.compute.v1.SubnetworkSecondaryRange> secondaryIpRanges = model.getSecondaryIpRangesList();
            secondaryIpRanges.forEach(ipRange -> {
                SubnetworkSecondaryRange secondaryRange = newSubresource(SubnetworkSecondaryRange.class);
                secondaryRange.copyFrom(ipRange);

                getSecondaryIpRange().add(secondaryRange);
            });
        }

        setLogConfig(null);
        if (model.hasLogConfig()) {
            SubnetworkLogConfig config = newSubresource(SubnetworkLogConfig.class);
            config.copyFrom(model.getLogConfig());
            setLogConfig(config);
        }
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
        Subnetwork.Builder builder = Subnetwork.newBuilder()
            .setName(getName())
            .setNetwork(getNetwork().getSelfLink())
            .setIpCidrRange(getIpCidrRange())
            .setEnableFlowLogs(getEnableFlowLogs());

        if (getDescription() != null) {
            builder.setDescription(getDescription());
        }

        if (getPrivateIpGoogleAccess() != null) {
            builder.setPrivateIpGoogleAccess(getPrivateIpGoogleAccess());
        }

        if (!getSecondaryIpRange().isEmpty()) {
            getSecondaryIpRange().forEach(range -> builder.addSecondaryIpRanges(range.toSecondaryIpRange()));
        }

        if (getLogConfig() != null) {
            builder.setLogConfig(getLogConfig().toSubnetworkLogConfig());
        }

        try (SubnetworksClient client = createClient(SubnetworksClient.class)) {
            Subnetwork subnetwork = builder.build();
            Operation operation = client.insertCallable().call(InsertSubnetworkRequest.newBuilder()
                .setProject(getProjectId())
                .setRegion(getRegion())
                .setSubnetworkResource(subnetwork)
                .build());

            waitForCompletion(operation);
        }

        refresh();
    }

    @Override
    public void doUpdate(GyroUI ui, State state, Resource current, Set<String> changedFieldNames) throws Exception {
        if (changedFieldNames.contains("enable-flow-logs") || changedFieldNames.contains("log-config")) {
            try (SubnetworksClient client = createClient(SubnetworksClient.class)) {
                Subnetwork.Builder builder = Subnetwork.newBuilder(getSubnetwork(client))
                    .clearLogConfig().clearEnableFlowLogs();

                if (getEnableFlowLogs() && getLogConfig() != null) {
                    builder.setLogConfig(getLogConfig().toSubnetworkLogConfig());
                } else {
                    builder.setLogConfig(com.google.cloud.compute.v1.SubnetworkLogConfig.newBuilder()
                        .setEnable(getEnableFlowLogs()).build());
                }

                Operation operation = client.patchCallable().call(PatchSubnetworkRequest.newBuilder()
                    .setProject(getProjectId())
                    .setRegion(getRegion())
                    .setSubnetwork(getName())
                    .setSubnetworkResource(builder.build())
                    .build());

                waitForCompletion(operation);
            } catch (Exception ex) {
                throw new GyroException(ex);
            }
        }

        if (changedFieldNames.contains("private-ip-google-access")) {
            try (SubnetworksClient client = createClient(SubnetworksClient.class)) {
                SubnetworksSetPrivateIpGoogleAccessRequest flag = SubnetworksSetPrivateIpGoogleAccessRequest.newBuilder()
                    .setPrivateIpGoogleAccess(getPrivateIpGoogleAccess())
                    .build();

                Operation operation = client.setPrivateIpGoogleAccessCallable()
                    .call(SetPrivateIpGoogleAccessSubnetworkRequest.newBuilder()
                        .setProject(getProjectId())
                        .setRegion(getRegion())
                        .setSubnetwork(getName())
                        .setSubnetworksSetPrivateIpGoogleAccessRequestResource(flag)
                        .build());

                waitForCompletion(operation);
            }
        }

        if (changedFieldNames.contains("secondary-ip-range")) {
            try (SubnetworksClient client = createClient(SubnetworksClient.class)) {
                Subnetwork.Builder builder = Subnetwork.newBuilder(getSubnetwork(client));
                getSecondaryIpRange().forEach(range -> builder.addSecondaryIpRanges(range.toSecondaryIpRange()));

                Operation operation = client.patchCallable().call(PatchSubnetworkRequest.newBuilder()
                    .setProject(getProjectId())
                    .setRegion(getRegion())
                    .setSubnetwork(getName())
                    .setSubnetworkResource(builder)
                    .build());

                waitForCompletion(operation);
            }
        }
    }

    @Override
    public void doDelete(GyroUI ui, State state) {
        try (SubnetworksClient client = createClient(SubnetworksClient.class)) {
            OperationFuture<Operation, Operation> future = client.deleteAsync(getProjectId(), getRegion(), getName());
            Operation operation = future.get();

            waitForCompletion(operation);
        } catch (Exception ex) {
            throw new GyroException(ex);
        }
    }

    @Override
    public List<ValidationError> validate(Set<String> configuredFields) {
        ArrayList<ValidationError> errors = new ArrayList<>();

        if (configuredFields.contains("log-config") && Boolean.FALSE.equals(getEnableFlowLogs())) {
            errors.add(new ValidationError(
                this, null, "'log-config' cannot be set unless 'enable-flow-logs' is set to 'true'"));
        }

        return errors;
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

    public static String selfLinkForName(String projectId, String region, String name) {
        return String.format("https://www.googleapis.com/compute/v1/projects/%s/regions/%s/subnetworks/%s",
            projectId, region, name);
    }
}
