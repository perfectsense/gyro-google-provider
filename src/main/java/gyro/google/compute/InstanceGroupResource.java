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
import com.google.api.services.compute.model.InstanceGroup;
import com.google.api.services.compute.model.InstanceGroupsSetNamedPortsRequest;
import com.google.api.services.compute.model.Operation;
import gyro.core.GyroUI;
import gyro.core.Type;
import gyro.core.resource.Id;
import gyro.core.resource.Output;
import gyro.core.resource.Resource;
import gyro.core.resource.Updatable;
import gyro.core.scope.State;
import gyro.core.validation.Regex;
import gyro.core.validation.Required;
import gyro.google.Copyable;

/**
 * Creates an instance group.
 *
 * Example
 * -------
 *
 * .. code-block:: gyro
 *
 *     google::compute-instance-group instance-group-named-ports-example
 *          name: "instance-group-named-ports-example"
 *          description: "instance-group-named-ports-example-description"
 *          zone: "us-central1-a"
 *
 *          named-port
 *              name: "port-a"
 *              port: 123
 *          end
 *
 *          named-port
 *              name: "port-b"
 *              port: 300
 *          end
 *     end
 *
 */
@Type("compute-instance-group")
public class InstanceGroupResource extends ComputeResource implements Copyable<InstanceGroup> {

    private String name;
    private String description;
    private List<InstanceGroupNamedPort> namedPort;
    private NetworkResource network;
    private String zone;

    // Read-only
    private String region;
    private String selfLink;
    private String subnetwork;

    /**
     * The name of the instance group. The name must be 1-63 characters long and the first character must be a lowercase letter, and all following characters must be a dash, lowercase letter, or digit, except the last character, which cannot be a dash. (Required)
     */
    @Id
    @Required
    @Regex("(?:(?:[-a-z0-9]{1,63}\\.)*(?:[a-z](?:[-a-z0-9]{0,61}[a-z0-9])?):)?(?:[0-9]{1,19}|(?:[a-z0-9](?:[-a-z0-9]{0,61}[a-z0-9])?))")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * The description of the instance group.
     */
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * The named port of the instance group.
     */
    @Updatable
    public List<InstanceGroupNamedPort> getNamedPort() {
        if (namedPort == null) {
            namedPort = new ArrayList<>();
        }

        return namedPort;
    }

    public void setNamedPort(List<InstanceGroupNamedPort> instanceGroupNamedPort) {
        this.namedPort = instanceGroupNamedPort;
    }

    /**
     * The network of the instance group.
     */
    public NetworkResource getNetwork() {
        return network;
    }

    public void setNetwork(NetworkResource network) {
        this.network = network;
    }

    /**
     * The zone of the instance group.
     */
    @Required
    public String getZone() {
        return zone;
    }

    public void setZone(String zone) {
        zone = zone.substring(zone.lastIndexOf("/") + 1);
        this.zone = zone;
    }

    /**
     * The fully-qualified URL of the region where the instance group is located.
     */
    @Output
    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    /**
     * The fully-qualified URL linking back to the instance group.
     */
    @Output
    public String getSelfLink() {
        return selfLink;
    }

    public void setSelfLink(String selfLink) {
        this.selfLink = selfLink;
    }

    /**
     * The fully-qualified URL of the subnetwork of which this instance group belongs.
     */
    @Output
    public String getSubnetwork() {
        return subnetwork;
    }

    public void setSubnetwork(String subnetwork) {
        this.subnetwork = subnetwork;
    }

    @Override
    public boolean doRefresh() throws Exception {
        Compute client = createComputeClient();
        InstanceGroup instanceGroup = client.instanceGroups().get(getProjectId(), getZone(), getName()).execute();
        copyFrom(instanceGroup);

        return true;
    }

    @Override
    public void doCreate(GyroUI ui, State state) throws Exception {
        Compute client = createComputeClient();
        Operation operation = client.instanceGroups().insert(getProjectId(), getZone(), toInstanceGroup()).execute();
        waitForCompletion(client, operation);

        state.save();

        saveNamedPort(client);

        refresh();
    }

    @Override
    public void doUpdate(
        GyroUI ui, State state, Resource current, Set<String> changedFieldNames) throws Exception {
        Compute client = createComputeClient();
        saveNamedPort(client);

        refresh();
    }

    @Override
    public void doDelete(GyroUI ui, State state) throws Exception {
        Compute client = createComputeClient();
        Operation operation = client.instanceGroups().delete(getProjectId(), getZone(), getName()).execute();
        waitForCompletion(client, operation);
    }

    @Override
    public void copyFrom(InstanceGroup instanceGroup) {
        setName(instanceGroup.getName());
        setDescription(instanceGroup.getDescription());
        setZone(instanceGroup.getZone());
        setRegion(instanceGroup.getRegion());
        setSelfLink(instanceGroup.getSelfLink());
        setSubnetwork(instanceGroup.getSubnetwork());

        if (instanceGroup.getNetwork() != null) {
            setNetwork(findById(
                NetworkResource.class,
                instanceGroup.getNetwork()));
        }

        if (instanceGroup.getNamedPorts() != null) {
            setNamedPort(instanceGroup.getNamedPorts().stream().map(rule -> {
                InstanceGroupNamedPort namedPort = newSubresource(InstanceGroupNamedPort.class);
                namedPort.copyFrom(rule);
                return namedPort;
            }).collect(Collectors.toList()));
        }
    }

    private InstanceGroup toInstanceGroup() {
        InstanceGroup instanceGroup = new InstanceGroup();
        instanceGroup.setName(getName());
        instanceGroup.setDescription(getDescription());
        instanceGroup.setZone(getZone());
        instanceGroup.setRegion(getRegion());
        instanceGroup.setSelfLink(getSelfLink());
        instanceGroup.setSubnetwork(getSubnetwork());

        if (getNetwork() != null) {
            instanceGroup.setNetwork(getNetwork().getSelfLink());
        }

        instanceGroup.setNamedPorts(getNamedPort().stream()
            .map(InstanceGroupNamedPort::toNamedPort)
            .collect(Collectors.toList()));

        return instanceGroup;
    }

    private void saveNamedPort(Compute client) throws Exception {
        InstanceGroupsSetNamedPortsRequest namedPortsRequest = new InstanceGroupsSetNamedPortsRequest();
        namedPortsRequest.setNamedPorts(getNamedPort().stream()
            .map(InstanceGroupNamedPort::toNamedPort)
            .collect(Collectors.toList()));
        Operation operation = client.instanceGroups()
            .setNamedPorts(getProjectId(), getZone(), getName(), namedPortsRequest)
            .execute();
        waitForCompletion(client, operation);
    }
}