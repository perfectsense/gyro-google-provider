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
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.api.services.compute.Compute;
import com.google.api.services.compute.model.Instance;
import com.google.api.services.compute.model.InstancesSetLabelsRequest;
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
 * Creates a network.
 *
 * Example
 * -------
 *
 * .. code-block:: gyro
 *
 *      google::instance gyro-dev-1
 *          name: "gyro-development"
 *          description: "Testing for Gyro"
 *          zone: "us-west1-a"
 *          machine-type: "https://www.googleapis.com/compute/v1/projects/aerobic-lock-236714/zones/us-west1-a/machineTypes/n1-standard-2"
 *
 *          network-interfaces
 *              network: "https://www.googleapis.com/compute/v1/projects/aerobic-lock-236714/global/networks/default"
 *          end
 *
 *          initialize-disks
 *              boot: true
 *
 *              initialize-params
 *                  disk-name: "gyro-boot-disk"
 *                  source-image: "projects/debian-cloud/global/images/family/debian-9"
 *              end
 *          end
 *
 *          initialize-disks
 *              initialize-params
 *                  disk-name: "gyro-secondary-disk"
 *                  source-image: "projects/debian-cloud/global/images/family/debian-9"
 *              end
 *          end
 *
 *          labels: {
 *              "gyro": "install"
 *          }
 *      end
 */
@Type("instance")
public class InstanceResource extends ComputeResource implements Copyable<Instance> {

    private String name;
    private String zone;
    private String description;
    private String machineType;
    private List<InstanceNetworkInterface> networkInterfaces;
    private List<InstanceAttachedDisk> disks;
    private String selfLink;
    private Map<String, String> labels;
    private String labelFingerprint;

    private List<InstanceAttachedDisk> initializeDisks;

    /**
     * The name of the resource when initially creating the resource. Must be 1-63 characters, first character must be a lowercase letter and all following characters must be a dash, lowercase letter, or digit, except the last character, which cannot be a dash.
     */
    @Regex("[a-z]([-a-z0-9]*[a-z0-9])?")
    @Required
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * Name of zone the where the instance resides, i.e., ``us-central1-a``. See `available region and zones<https://cloud.google.com/compute/docs/regions-zones/#available/>`_. for the current list of zones.
     */
    @Required
    public String getZone() {
        return zone;
    }

    public void setZone(String zone) {
        this.zone = zone;
    }

    /**
     * An optional description for the resource.
     */
    @Output
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Full or partial URL of the machine type resource to use for this instance, in the format: zones/zone/machineTypes/machine-type. See `creating custom machine types <https://cloud.google.com/compute/docs/instances/creating-instance-with-custom-machine-type#specifications/>`_.
     */
    public String getMachineType() {
        return machineType;
    }

    public void setMachineType(String machineType) {
        this.machineType = machineType;
    }

    /**
     * List of network configurations for this instance. These specify how interfaces are configured to interact with other network services, such as connecting to the internet. Multiple interfaces are supported.
     */
    @Updatable
    public List<InstanceNetworkInterface> getNetworkInterfaces() {
        if (networkInterfaces == null) {
            networkInterfaces = new ArrayList<>();
        }
        return networkInterfaces;
    }

    public void setNetworkInterfaces(List<InstanceNetworkInterface> networkInterfaces) {
        this.networkInterfaces = networkInterfaces;
    }

    /**
     * List of disks associated with this instance. Persistent disks must be created before you can assign them.
     */
    @Output
    public List<InstanceAttachedDisk> getDisks() {
        if (disks == null) {
            disks = new ArrayList<>();
        }
        return disks;
    }

    public void setDisks(List<InstanceAttachedDisk> disks) {
        this.disks = disks;
    }

    /**
     * URL to the instance.
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
     * Map of key-value pairs to apply to this instance. May be updated after creation.
     */
    @Updatable
    public Map<String, String> getLabels() {
        return labels;
    }

    public void setLabels(Map<String, String> labels) {
        this.labels = labels;
    }

    /**
     * Hash of the label's contents used for locking.
     */
    @Output
    public String getLabelFingerprint() {
        return labelFingerprint;
    }

    public void setLabelFingerprint(String labelFingerprint) {
        this.labelFingerprint = labelFingerprint;
    }

    /**
     * Parameters for a new disk that will be created alongside the new instance. Use to create boot disks or local SSDs attached to the new instance.
     */
    public List<InstanceAttachedDisk> getInitializeDisks() {
        if (initializeDisks == null) {
            initializeDisks = new ArrayList<>();
        }

        return initializeDisks;
    }

    public void setInitializeDisks(List<InstanceAttachedDisk> initializeDisks) {
        this.initializeDisks = initializeDisks;
    }

    @Override
    public boolean doRefresh() throws Exception {
        Compute client = createComputeClient();
        Instance instance = client.instances().get(getProjectId(), getZone(), getName()).execute();

        copyFrom(instance);

        return true;
    }

    @Override
    public void doCreate(GyroUI ui, State state) throws Exception {
        Compute client = createComputeClient();
        Instance content = new Instance();

        content.setName(getName());
        content.setDescription(getDescription());
        content.setMachineType(getMachineType());
        content.setNetworkInterfaces(getNetworkInterfaces().stream()
            .map(InstanceNetworkInterface::copyTo)
            .collect(Collectors.toList()));
        content.setLabels(getLabels());

        content.setDisks(getInitializeDisks().stream()
            .map(InstanceAttachedDisk::copyTo)
            .collect(Collectors.toList()));

        waitForCompletion(
            client,
            client.instances().insert(getProjectId(), getZone(), content).execute());

        refresh();
    }

    @Override
    public void doUpdate(GyroUI ui, State state, Resource current, Set<String> changedFieldNames) throws Exception {
        Compute client = createComputeClient();

        if (changedFieldNames.contains("labels")) {
            waitForCompletion(
                client,
                client.instances()
                    .setLabels(
                        getProjectId(),
                        getZone(),
                        getName(),
                        new InstancesSetLabelsRequest()
                            .setLabelFingerprint(getLabelFingerprint())
                            .setLabels(getLabels()))
                    .execute());
        }

        refresh();
    }

    @Override
    public void doDelete(GyroUI ui, State state) throws Exception {
        Compute client = createComputeClient();
        client.instances().delete(getProjectId(), getZone(), getName()).execute();
    }

    @Override
    public void copyFrom(Instance model) {
        setName(model.getName());
        setZone(model.getZone().substring(model.getZone().lastIndexOf("/") + 1));
        setDescription(model.getDescription());
        setMachineType(model.getMachineType());
        setSelfLink(model.getSelfLink());
        setLabelFingerprint(model.getLabelFingerprint());

        getNetworkInterfaces().clear();
        if (model.getNetworkInterfaces() != null) {
            setNetworkInterfaces(model.getNetworkInterfaces().stream()
                .map(networkInterface -> {
                    InstanceNetworkInterface newNetworkInterface = newSubresource(InstanceNetworkInterface.class);
                    newNetworkInterface.copyFrom(networkInterface);
                    return newNetworkInterface;
                })
                .collect(Collectors.toList()));
        }

        getDisks().clear();
        if (model.getDisks() != null) {
            setDisks(model.getDisks().stream()
                .map(disk -> {
                    InstanceAttachedDisk instanceAttachedDisk = newSubresource(InstanceAttachedDisk.class);
                    instanceAttachedDisk.copyFrom(disk);
                    return instanceAttachedDisk;
                })
                .collect(Collectors.toList()));
        }
    }
}
