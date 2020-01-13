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
import gyro.core.validation.ValidStrings;
import gyro.google.Copyable;

/**
 * Creates an instance.
 *
 * Example
 * -------
 *
 * .. code-block:: gyro
 *
 *      zone: "us-west1-a"
 *
 *      google::compute-instance gyro-dev-1
 *          name: "gyro-development"
 *          description: "Testing for Gyro"
 *          zone: $zone
 *          machine-type: "zones/$(zone)/machineTypes/n1-standard-1"
 *
 *          network-interface
 *              network: $(external-query google::compute-network {name: "default"})
 *          end
 *
 *          initialize-disk
 *              boot: true
 *
 *              initialize-params
 *                  disk-name: "gyro-boot-disk"
 *                  source-image: "projects/debian-cloud/global/images/family/debian-9"
 *              end
 *          end
 *
 *          initialize-disk
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
@Type("compute-instance")
public class InstanceResource extends ComputeResource implements Copyable<Instance> {

    private String name;
    private String zone;
    private String description;
    private String machineType;
    private List<InstanceNetworkInterface> networkInterface;
    private List<InstanceAttachedDisk> disk;
    private String selfLink;
    private Map<String, String> labels;
    private String labelFingerprint;
    private List<InstanceAttachedDisk> initializeDisk;
    private String status;

    /**
     * The name of the resource when initially creating the resource. Must be 1-63 characters, first character must be a lowercase letter and all following characters must be a dash, lowercase letter, or digit, except the last character, which cannot be a dash.
     */
    @Regex("^[a-z]([-a-z0-9]{1,61}[a-z0-9])?")
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
        this.machineType = machineType != null ? machineType.substring(machineType.lastIndexOf("zones/")) : null;
    }

    /**
     * List of network configurations for this instance. These specify how interfaces are configured to interact with other network services, such as connecting to the internet. Multiple interfaces are supported.
     *
     * @subresource gyro.google.compute.InstanceNetworkInterface
     */
    public List<InstanceNetworkInterface> getNetworkInterface() {
        if (networkInterface == null) {
            networkInterface = new ArrayList<>();
        }
        return networkInterface;
    }

    public void setNetworkInterface(List<InstanceNetworkInterface> networkInterface) {
        this.networkInterface = networkInterface;
    }

    /**
     * List of disks associated with this instance.
     */
    @Output
    public List<InstanceAttachedDisk> getDisk() {
        if (disk == null) {
            disk = new ArrayList<>();
        }
        return disk;
    }

    public void setDisk(List<InstanceAttachedDisk> disk) {
        this.disk = disk;
    }

    /**
     * URL of the instance.
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
     * Map of key-value pairs to apply to the instance.
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
     *
     * @subresource gyro.google.compute.InstanceAttachedDisk
     */
    public List<InstanceAttachedDisk> getInitializeDisk() {
        if (initializeDisk == null) {
            initializeDisk = new ArrayList<>();
        }

        return initializeDisk;
    }

    public void setInitializeDisk(List<InstanceAttachedDisk> initializeDisk) {
        this.initializeDisk = initializeDisk;
    }

    /**
     * The status of the instance. Setting a value of `TERMINATED`` will stop the instance while setting the value to ``RUNNING`` will start an instance. See also `instance status <https://cloud.google.com/compute/docs/instances/instance-life-cycle#instance_statuses/>`_.
     */
    @Updatable
    @ValidStrings({ "RUNNING", "TERMINATED" })
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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
        content.setNetworkInterfaces(getNetworkInterface().stream()
            .map(InstanceNetworkInterface::copyTo)
            .collect(Collectors.toList()));
        content.setLabels(getLabels());
        content.setDisks(getInitializeDisk().stream()
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
        InstanceResource currentResource = (InstanceResource) current;

        if (changedFieldNames.contains("labels")) {
            // Always use the currentResoure#labelFingerprint in case updated via console. API will neither error or
            // update if an older fingerprint is used.
            waitForCompletion(
                client,
                client.instances()
                    .setLabels(
                        getProjectId(),
                        getZone(),
                        getName(),
                        new InstancesSetLabelsRequest()
                            .setLabelFingerprint(currentResource.getLabelFingerprint())
                            .setLabels(getLabels()))
                    .execute());
        }

        if (changedFieldNames.contains("status")) {
            if ("RUNNING".equals(getStatus())) {
                waitForCompletion(client, client.instances().start(getProjectId(), getZone(), getName()).execute());
            } else if ("TERMINATED".equals(getStatus())) {
                // These take a considerable amount of time so don't wait.
                client.instances().stop(getProjectId(), getZone(), getName()).execute();
            }
        }

        refresh();
    }

    @Override
    public void doDelete(GyroUI ui, State state) throws Exception {
        Compute client = createComputeClient();
        waitForCompletion(client, client.instances().delete(getProjectId(), getZone(), getName()).execute());
    }

    @Override
    public void copyFrom(Instance model) {
        setName(model.getName());
        setZone(model.getZone().substring(model.getZone().lastIndexOf("/") + 1));
        setDescription(model.getDescription());
        setMachineType(model.getMachineType());
        setSelfLink(model.getSelfLink());
        setLabelFingerprint(model.getLabelFingerprint());
        setLabels(model.getLabels());

        // There are other intermediary steps between RUNNING and TERMINATED while moving between states.
        if ("RUNNING".equals(getStatus()) || "TERMINATED".equals(getStatus())) {
            setStatus(model.getStatus());
        }

        getNetworkInterface().clear();
        if (model.getNetworkInterfaces() != null) {
            setNetworkInterface(model.getNetworkInterfaces().stream()
                .map(networkInterface -> {
                    InstanceNetworkInterface newNetworkInterface = newSubresource(InstanceNetworkInterface.class);
                    newNetworkInterface.copyFrom(networkInterface);
                    return newNetworkInterface;
                })
                .collect(Collectors.toList()));
        }

        getDisk().clear();
        if (model.getDisks() != null) {
            setDisk(model.getDisks().stream()
                .map(disk -> {
                    InstanceAttachedDisk instanceAttachedDisk = newSubresource(InstanceAttachedDisk.class);
                    instanceAttachedDisk.copyFrom(disk);
                    return instanceAttachedDisk;
                })
                .collect(Collectors.toList()));
        }
    }
}
