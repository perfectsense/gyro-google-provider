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

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.google.api.gax.rpc.InvalidArgumentException;
import com.google.api.gax.rpc.NotFoundException;
import com.google.cloud.compute.v1.DeleteInstanceRequest;
import com.google.cloud.compute.v1.GetInstanceRequest;
import com.google.cloud.compute.v1.InsertInstanceRequest;
import com.google.cloud.compute.v1.Instance;
import com.google.cloud.compute.v1.InstancesClient;
import com.google.cloud.compute.v1.InstancesSetLabelsRequest;
import com.google.cloud.compute.v1.InstancesSetMachineTypeRequest;
import com.google.cloud.compute.v1.Items;
import com.google.cloud.compute.v1.Metadata;
import com.google.cloud.compute.v1.SetLabelsInstanceRequest;
import com.google.cloud.compute.v1.SetMachineTypeInstanceRequest;
import com.google.cloud.compute.v1.SetMetadataInstanceRequest;
import com.google.cloud.compute.v1.SetTagsInstanceRequest;
import com.google.cloud.compute.v1.StartInstanceRequest;
import com.google.cloud.compute.v1.StopInstanceRequest;
import com.google.cloud.compute.v1.Tags;
import gyro.core.GyroInstance;
import gyro.core.GyroUI;
import gyro.core.Type;
import gyro.core.Wait;
import gyro.core.resource.Id;
import gyro.core.resource.Output;
import gyro.core.resource.Resource;
import gyro.core.resource.Updatable;
import gyro.core.scope.State;
import gyro.core.validation.CollectionMax;
import gyro.core.validation.Regex;
import gyro.core.validation.Required;
import gyro.core.validation.ValidStrings;
import gyro.core.validation.ValidationError;
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
 *                  resource-policy: $(google::compute-resource-policy instance-attached-disk-example)
 *              end
 *          end
 *
 *          labels: {
 *              "gyro": "install"
 *          }
 *
 *          metadata: {
 *              test-key: "test-value"
 *          }
 *
 *          tags: ["test-tag"]
 *      end
 */
@Type("compute-instance")
public class InstanceResource extends ComputeResource implements GyroInstance, Copyable<Instance> {

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
    private Boolean canIpForward;
    private String status;
    private String hostName;
    private String creationDate;
    private String id;
    private String publicIp;
    private String privateIp;
    private List<ComputeServiceAccount> serviceAccount;
    private Map<String, String> metadata;
    private List<String> tags;

    /**
     * The name of the resource when initially creating the resource.
     */
    @Regex(value = "^[a-z]([-a-z0-9]{1,61}[a-z0-9])?", message = "a string 1-63 characters long and the first character must be a lowercase letter, and all following characters must be a dash, lowercase letter, or digit, except the last character, which cannot be a dash")
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
    @Updatable
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
    @Required
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
     * If enabled allows this instance to send and receive packets with non-matching destination or source IPs. Defaults to ``false``.
     */
    public Boolean getCanIpForward() {
        return canIpForward;
    }

    public void setCanIpForward(Boolean canIpForward) {
        this.canIpForward = canIpForward;
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

    /**
     * The hostname of the instance.
     */
    @Output
    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    /**
     * The creation date of the instance.
     */
    @Output
    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    /**
     * The id of the instance.
     */
    @Output
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    /**
     * The public ip of the instance.
     */
    @Output
    public String getPublicIp() {
        return publicIp;
    }

    public void setPublicIp(String publicIp) {
        this.publicIp = publicIp;
    }

    /**
     * The private ip of the instance.
     */
    @Output
    public String getPrivateIp() {
        return privateIp;
    }

    public void setPrivateIp(String privateIp) {
        this.privateIp = privateIp;
    }

    /**
     * The list of service accounts that are authorized for the instance.
     */
    public List<ComputeServiceAccount> getServiceAccount() {
        if (serviceAccount == null) {
            serviceAccount = new ArrayList<>();
        }

        return serviceAccount;
    }

    public void setServiceAccount(List<ComputeServiceAccount> serviceAccount) {
        this.serviceAccount = serviceAccount;
    }

    /**
     * The metadata of the instance, specified with key/value pairs.
     * Keys may only contain alphanumeric characters, dashes, and underscores, and must be 1-128 characters in length.
     * Values must be 0-262144 characters in length.
     */
    @Updatable
    public Map<String, String> getMetadata() {
        if (metadata == null) {
            metadata = new HashMap<>();
        }

        return metadata;
    }

    public void setMetadata(Map<String, String> metadata) {
        this.metadata = metadata;
    }

    /**
     * The set of tags for the instance.
     * All tags for an instance must be unique.
     */
    @Updatable
    @CollectionMax(64)
    @Regex(value = "^[a-z]([-a-z0-9]{0,61}[a-z0-9]$)?", message = "only dashes, lowercase letters, or digits. The first character must be a lowercase letter, and the last character cannot be a dash. Each tag must be 1-63 characters.")
    public List<String> getTags() {
        if (tags == null) {
            tags = new ArrayList<>();
        }

        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    @Override
    public boolean doRefresh() throws Exception {
        try (InstancesClient client = createClient(InstancesClient.class)) {
            Instance instance = getInstance(client);

            if (instance == null) {
                return false;
            }

            copyFrom(instance);

            return true;
        }
    }

    @Override
    public void doCreate(GyroUI ui, State state) throws Exception {
        try (InstancesClient client = createClient(InstancesClient.class)) {
            Instance.Builder builder = Instance.newBuilder();
            builder.setName(getName());
            builder.addAllNetworkInterfaces(getNetworkInterface().stream()
                .map(InstanceNetworkInterface::copyTo)
                .collect(Collectors.toList()));
            builder.putAllLabels(getLabels());
            builder.addAllDisks(getInitializeDisk().stream()
                .map(InstanceAttachedDisk::copyTo)
                .collect(Collectors.toList()));
            builder.setCanIpForward(getCanIpForward());
            builder.addAllServiceAccounts(getServiceAccount().stream()
                .map(ComputeServiceAccount::toServiceAccount)
                .collect(Collectors.toList()));
            builder.setTags(buildTags(null));
            builder.setMetadata(buildMetadata(null));

            builder.setDescription(getDescription());
            builder.setMachineType(getMachineType());

            waitForCompletion(client.insertCallable().call(InsertInstanceRequest.newBuilder()
                .setProject(getProjectId())
                .setZone(getZone())
                .setInstanceResource(builder)
                .build()));

            Wait.atMost(2, TimeUnit.MINUTES)
                .checkEvery(20, TimeUnit.SECONDS)
                .prompt(false)
                .until(() -> {
                    Instance instance = getInstance(client);
                    return Instance.Status.RUNNING.equals(instance.getStatus()) || Instance.Status.TERMINATED.equals(
                        instance.getStatus());
                });
        }

        refresh();
    }

    @Override
    public void doUpdate(GyroUI ui, State state, Resource current, Set<String> changedFieldNames) throws Exception {
        try (InstancesClient client = createClient(InstancesClient.class)) {
            InstanceResource currentResource = (InstanceResource) current;

            if (changedFieldNames.contains("labels")) {
                // Always use the currentResoure#labelFingerprint in case updated via console. API will neither error or
                // update if an older fingerprint is used.

                InstancesSetLabelsRequest.Builder builder = InstancesSetLabelsRequest.newBuilder()
                    .setLabelFingerprint(currentResource.getLabelFingerprint())
                    .putAllLabels(getLabels());

                waitForCompletion(
                    client.setLabelsCallable()
                        .call(SetLabelsInstanceRequest.newBuilder()
                            .setProject(getProjectId())
                            .setZone(getZone())
                            .setInstance(getName())
                            .setInstancesSetLabelsRequestResource(builder)
                            .build()));
            }

            if (changedFieldNames.contains("status")) {
                if ("RUNNING".equals(getStatus())) {
                    waitForCompletion(client.startCallable().call(StartInstanceRequest.newBuilder()
                        .setProject(getProjectId())
                        .setZone(getZone())
                        .setInstance(getName())
                        .build()));
                } else if ("TERMINATED".equals(getStatus())) {
                    // These take a considerable amount of time so don't wait.
                    client.stopCallable().call(StopInstanceRequest.newBuilder()
                        .setProject(getProjectId())
                        .setZone(getZone())
                        .setInstance(getName())
                        .build());
                }
            }

            if (changedFieldNames.contains("metadata")) {
                waitForCompletion(client.setMetadataCallable().call(
                    SetMetadataInstanceRequest.newBuilder()
                        .setProject(getProjectId())
                        .setZone(getZone())
                        .setInstance(getName())
                        .setMetadataResource(buildMetadata(getMetadataFingerprint()))
                        .build()));
            }

            if (changedFieldNames.contains("tags")) {
                waitForCompletion(client.setTagsCallable().call(
                    SetTagsInstanceRequest.newBuilder()
                        .setProject(getProjectId())
                        .setZone(getZone())
                        .setInstance(getName())
                        .setTagsResource(buildTags(getTagsFingerprint()))
                        .build()));
            }

            if (changedFieldNames.contains("machine-type")) {
                InstancesSetMachineTypeRequest.Builder builder = InstancesSetMachineTypeRequest.newBuilder()
                        .setMachineType(getMachineType());

                waitForCompletion(client.setMachineTypeCallable().call(
                    SetMachineTypeInstanceRequest.newBuilder()
                        .setProject(getProjectId())
                        .setZone(getZone())
                        .setInstance(getName())
                        .setInstancesSetMachineTypeRequestResource(builder)
                        .build()));
            }
        }

        refresh();
    }

    @Override
    public void doDelete(GyroUI ui, State state) throws Exception {
        try (InstancesClient client = createClient(InstancesClient.class)) {
            waitForCompletion(client.deleteCallable().call(DeleteInstanceRequest.newBuilder()
                .setProject(getProjectId())
                .setZone(getZone())
                .setInstance(getName())
                .build()));
        }
    }

    @Override
    public void copyFrom(Instance model) {
        setName(model.getName());
        setZone(model.getZone().substring(model.getZone().lastIndexOf("/") + 1));
        setDescription(model.getDescription());
        setMachineType(model.getMachineType());
        setSelfLink(model.getSelfLink());
        setLabelFingerprint(model.getLabelFingerprint());
        setLabels(model.getLabelsMap());
        setCanIpForward(model.getCanIpForward());

        getMetadata().clear();
        if (model.getMetadata() != null && model.getMetadata().getItemsList() != null) {
            setMetadata(model.getMetadata().getItemsList().stream()
                .collect(Collectors.toMap(Items::getKey, Items::getValue)));
        }

        getTags().clear();
        if (model.getTags() != null && model.getTags().getItemsList() != null) {
            setTags(model.getTags().getItemsList());
        }

        // There are other intermediary steps between RUNNING and TERMINATED while moving between states.
        if (Instance.Status.RUNNING.equals(model.getStatus()) || Instance.Status.TERMINATED.equals(model.getStatus())) {
            setStatus(model.hasStatus() ? model.getStatus().toString().toUpperCase() : null);
        }

        getNetworkInterface().clear();
        if (model.getNetworkInterfacesList() != null) {
            setNetworkInterface(model.getNetworkInterfacesList().stream()
                .map(networkInterface -> {
                    InstanceNetworkInterface newNetworkInterface = newSubresource(InstanceNetworkInterface.class);
                    newNetworkInterface.copyFrom(networkInterface);
                    return newNetworkInterface;
                })
                .collect(Collectors.toList()));
        }

        getDisk().clear();
        if (model.getDisksList() != null) {
            setDisk(model.getDisksList().stream()
                .map(disk -> {
                    InstanceAttachedDisk instanceAttachedDisk = newSubresource(InstanceAttachedDisk.class);
                    instanceAttachedDisk.copyFrom(disk);
                    return instanceAttachedDisk;
                })
                .collect(Collectors.toList()));
        }

        getServiceAccount().clear();
        if (model.getServiceAccountsList() != null) {
            setServiceAccount(model.getServiceAccountsList().stream()
                .map(sa -> {
                    ComputeServiceAccount serviceAccount = newSubresource(ComputeServiceAccount.class);
                    serviceAccount.copyFrom(sa);
                    return serviceAccount;
                })
                .collect(Collectors.toList()));
        }

        setCreationDate(model.getCreationTimestamp());
        setHostName(model.getHostname());
        setPrivateIp(getNetworkInterface().get(0).getNetworkIp());
        setId(String.valueOf(model.getId()));
        setPublicIp(null);
        getNetworkInterface().stream()
            .filter(o -> !o.getAccessConfig().isEmpty())
            .map(o -> o.getAccessConfig().get(0))
            .findFirst().ifPresent(accessConfigPublicIp -> setPublicIp(accessConfigPublicIp.getNatIp()));

    }

    private String getMetadataFingerprint() throws IOException {
        try (InstancesClient client = createClient(InstancesClient.class)) {
            String fingerprint = null;

            Instance instance = client.get(getProjectId(), getZone(), getName());
            if (instance.getMetadata() != null) {
                fingerprint = instance.getMetadata().getFingerprint();
            }

            return fingerprint;
        }
    }

    private Metadata buildMetadata(String fingerprint) {
        Metadata.Builder builder = Metadata.newBuilder();
        builder.addAllItems(
            getMetadata().entrySet().stream()
                .map(e -> {
                    Items.Builder builder1 = Items.newBuilder();
                    builder1.setKey(e.getKey());
                    builder1.setValue(e.getValue());
                    return builder1.build();
                })
                .collect(Collectors.toList())
        );

        if (fingerprint != null) {
            builder.setFingerprint(fingerprint);
        }

        return builder.build();
    }

    private String getTagsFingerprint() throws IOException {
        try (InstancesClient client = createClient(InstancesClient.class)) {
            String fingerprint = null;

            Instance instance = client.get(getProjectId(), getZone(), getName());
            if (instance.getTags() != null) {
                fingerprint = instance.getTags().getFingerprint();
            }

            return fingerprint;
        }
    }

    private Tags buildTags(String fingerprint) {
        Tags.Builder builder = Tags.newBuilder();
        builder.addAllItems(getTags());

        if (fingerprint != null) {
            builder.setFingerprint(fingerprint);
        }

        return builder.build();
    }

    @Override
    public List<ValidationError> validate(Set<String> configuredFields) {
        List<ValidationError> errors = new ArrayList<>();

        Pattern keyPattern = Pattern.compile("[a-zA-Z0-9-_]{1,128}");

        if (!getMetadata().isEmpty() && configuredFields.contains("metadata")) {
            for (String key : getMetadata().keySet()) {
                if (!keyPattern.matcher(key).matches()) {
                    errors.add(new ValidationError(
                        this,
                        "metadata",
                        "Keys may only contain alphanumeric characters, dashes, and underscores, and must be within 1-128 characters in length."));
                    break;
                }
            }

            for (String value : getMetadata().values()) {
                if (value.length() > 262144) {
                    errors.add(new ValidationError(
                        this,
                        "metadata",
                        "Values must be within 0-262144 characters in length."));
                    break;
                }
            }
        }

        return errors;
    }

    private Instance getInstance(InstancesClient client) {
        Instance instance = null;

        try {
            instance = client.get(GetInstanceRequest.newBuilder()
                .setProject(getProjectId())
                .setZone(getZone())
                .setInstance(getName())
                .build());

        } catch (NotFoundException | InvalidArgumentException ex) {
            // ignore
        }

        return instance;
    }

    @Override
    public String getGyroInstanceId() {
        return getId();
    }

    @Override
    public String getGyroInstanceState() {
        return getStatus();
    }

    @Override
    public String getGyroInstancePrivateIpAddress() {
        return getPrivateIp();
    }

    @Override
    public String getGyroInstancePublicIpAddress() {
        return getPublicIp();
    }

    @Override
    public String getGyroInstanceHostname() {
        return getHostName();
    }

    @Override
    public String getGyroInstanceName() {
        return getName();
    }

    @Override
    public String getGyroInstanceLaunchDate() {
        return getCreationDate() != null ? getCreationDate().split("T")[0] : null;
    }

    @Override
    public String getGyroInstanceLocation() {
        return getZone();
    }
}
