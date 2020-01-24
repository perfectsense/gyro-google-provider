/*
 * Copyright 2020, Perfect Sense, Inc.
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import com.google.api.services.compute.model.AcceleratorConfig;
import com.google.api.services.compute.model.AttachedDisk;
import com.google.api.services.compute.model.InstanceProperties;
import com.google.api.services.compute.model.Metadata;
import com.google.api.services.compute.model.NetworkInterface;
import com.google.api.services.compute.model.ReservationAffinity;
import com.google.api.services.compute.model.Scheduling;
import com.google.api.services.compute.model.ServiceAccount;
import com.google.api.services.compute.model.ShieldedInstanceConfig;
import com.google.api.services.compute.model.Tags;
import gyro.core.resource.Diffable;
import gyro.core.validation.Required;
import gyro.google.Copyable;

public class ComputeInstanceProperties extends Diffable implements Copyable<InstanceProperties> {

    private Boolean canIpForward;

    private String description;

    private List<InstanceAttachedDisk> disk;

    private List<ComputeAcceleratorConfig> guestAccelerator;

    private Map<String, String> labels;

    private String machineType;

    private List<ProjectMetadataItemResource> metadata;

    private String minCpuPlatform;

    private List<InstanceNetworkInterface> networkInterface;

    private ComputeReservationAffinity reservationAffinity;

    private ComputeScheduling scheduling;

    private List<ComputeServiceAccount> serviceAccount;

    private ComputeShieldedInstanceConfig shieldedInstanceConfig;

    private ComputeTags tags;

    /**
     * Enables instances created based on this template to send packets with source IP addresses other than their own and receive packets with destination IP addresses other than their own.
     * If these instances will be used as an IP gateway or it will be set as the next-hop in a Route resource, specify true.
     */
    public Boolean getCanIpForward() {
        return canIpForward;
    }

    public void setCanIpForward(Boolean canIpForward) {
        this.canIpForward = canIpForward;
    }

    /**
     * Text description for the instances that are created from this instance template.
     */
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * List of disks that are associated with the instances that are created from this template.
     *
     * @subresource gyro.google.compute.InstanceAttachedDisk
     */
    @Required
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
     * List of guest accelerator cards' type and count to use for instances created from the instance template.
     *
     * @subresource gyro.google.compute.ComputeAcceleratorConfig
     */
    public List<ComputeAcceleratorConfig> getGuestAccelerator() {
        if (guestAccelerator == null) {
            guestAccelerator = new ArrayList<>();
        }
        return guestAccelerator;
    }

    public void setGuestAccelerator(List<ComputeAcceleratorConfig> guestAccelerator) {
        this.guestAccelerator = guestAccelerator;
    }

    /**
     * Labels to apply to instances that are created from this template.
     */
    public Map<String, String> getLabels() {
        if (labels == null) {
            labels = new HashMap();
        }
        return labels;
    }

    public void setLabels(Map<String, String> labels) {
        this.labels = labels;
    }

    /**
     * The machine type to use for instances that are created from this template.
     */
    @Required
    public String getMachineType() {
        return machineType;
    }

    public void setMachineType(String machineType) {
        this.machineType = machineType;
    }

    /**
     * The metadata key/value pairs to assign to instances that are created from this template.
     *
     * @resource gyro.google.compute.ProjectMetadataItemResource
     */
    public List<ProjectMetadataItemResource> getMetadata() {
        if (metadata == null) {
            metadata = new ArrayList<>();
        }
        return metadata;
    }

    public void setMetadata(List<ProjectMetadataItemResource> metadata) {
        this.metadata = metadata;
    }

    /**
     * Minimum cpu/platform to be used by this instance.
     * The instance may be scheduled on the specified or newer cpu/platform.
     * Applicable values are the friendly names of CPU platforms, such as ``Intel Haswell`` or ``Intel Sandy Bridge``.
     */
    public String getMinCpuPlatform() {
        return minCpuPlatform;
    }

    public void setMinCpuPlatform(String minCpuPlatform) {
        this.minCpuPlatform = minCpuPlatform;
    }

    /**
     * List of network access configurations for this interface.
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
     * Specifies the reservations that this instance can consume from.
     *
     * @subresource gyro.google.compute.ComputeReservationAffinity
     */
    public ComputeReservationAffinity getReservationAffinity() {
        return reservationAffinity;
    }

    public void setReservationAffinity(ComputeReservationAffinity reservationAffinity) {
        this.reservationAffinity = reservationAffinity;
    }

    /**
     * Specifies the scheduling options for the instances that are created from this template.
     *
     * @subresource gyro.google.compute.ComputeScheduling
     */
    public ComputeScheduling getScheduling() {
        return scheduling;
    }

    public void setScheduling(ComputeScheduling scheduling) {
        this.scheduling = scheduling;
    }

    /**
     * List of service accounts with specified scopes.
     * Access tokens for these service accounts are available to the instances that are created from this template.
     * Use metadata queries to obtain the access tokens for these instances.
     *
     * @subresource gyro.google.compute.ComputeServiceAccount
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
     * Configuration for shielded instance.
     *
     * @subresource gyro.google.compute.ComputeShieldedInstanceConfig
     */
    public ComputeShieldedInstanceConfig getShieldedInstanceConfig() {
        return shieldedInstanceConfig;
    }

    public void setShieldedInstanceConfig(ComputeShieldedInstanceConfig shieldedInstanceConfig) {
        this.shieldedInstanceConfig = shieldedInstanceConfig;
    }

    /**
     * Tags to apply to the instances that are created from this template.
     * The tags identify valid sources or targets for network firewalls.
     *
     * @subresource gyro.google.compute.ComputeTags
     */
    public ComputeTags getTags() {
        return tags;
    }

    public void setTags(ComputeTags tags) {
        this.tags = tags;
    }

    @Override
    public String primaryKey() {
        return "";
    }

    @Override
    public void copyFrom(InstanceProperties model) {
        setCanIpForward(model.getCanIpForward());
        setDescription(model.getDescription());
        List<InstanceAttachedDisk> diffableAttachedDisks = null;
        List<AttachedDisk> disks = model.getDisks();

        if (disks != null && !disks.isEmpty()) {
            diffableAttachedDisks = disks
                .stream()
                .map(attachedDisk -> {
                    // TODO: isEqualTo
                    InstanceAttachedDisk diffableAttachedDisk = newSubresource(InstanceAttachedDisk.class);
                    diffableAttachedDisk.copyFrom(attachedDisk);
                    return diffableAttachedDisk;
                })
                .collect(Collectors.toList());
        }
        setDisk(diffableAttachedDisks);

        List<ComputeAcceleratorConfig> diffableGuestAccelerators = null;
        List<AcceleratorConfig> guestAccelerators = model.getGuestAccelerators();

        if (guestAccelerators != null && !guestAccelerators.isEmpty()) {
            diffableGuestAccelerators = guestAccelerators
                .stream()
                .map(acceleratorConfig -> {
                    // TODO: isEqualTo
                    ComputeAcceleratorConfig diffableAcceleratorConfig = newSubresource(ComputeAcceleratorConfig.class);
                    diffableAcceleratorConfig.copyFrom(acceleratorConfig);
                    return diffableAcceleratorConfig;
                })
                .collect(Collectors.toList());
        }
        setGuestAccelerator(diffableGuestAccelerators);
        setLabels(model.getLabels());
        setMachineType(model.getMachineType());

        List<ProjectMetadataItemResource> diffableMetadataItemResource = null;
        Metadata metadata = model.getMetadata();

        if (metadata != null) {
            List<Metadata.Items> items = metadata.getItems();

            if (items != null) {
                diffableMetadataItemResource = items
                    .stream()
                    .map(networkInterface -> {
                        // TODO: isEqualTo
                        ProjectMetadataItemResource diffableNetworkInterface = newSubresource(
                            ProjectMetadataItemResource.class);
                        diffableNetworkInterface.copyFrom(networkInterface);
                        return diffableNetworkInterface;
                    })
                    .collect(Collectors.toList());
            }
        }
        setMetadata(diffableMetadataItemResource);
        setMinCpuPlatform(model.getMinCpuPlatform());

        List<InstanceNetworkInterface> diffableNetworkInterfaces = null;
        List<NetworkInterface> networkInterfaces = model.getNetworkInterfaces();

        if (networkInterfaces != null && !networkInterfaces.isEmpty()) {
            diffableNetworkInterfaces = networkInterfaces
                .stream()
                .map(networkInterface -> {
                    // TODO: isEqualTo
                    InstanceNetworkInterface diffableNetworkInterface = newSubresource(InstanceNetworkInterface.class);
                    diffableNetworkInterface.copyFrom(networkInterface);
                    return diffableNetworkInterface;
                })
                .collect(Collectors.toList());
        }
        setNetworkInterface(diffableNetworkInterfaces);

        ComputeReservationAffinity diffableReservationAffinity = null;
        ReservationAffinity reservationAffinity = model.getReservationAffinity();

        if (reservationAffinity != null) {
            diffableReservationAffinity = Optional.ofNullable(getReservationAffinity())
                .orElse(newSubresource(ComputeReservationAffinity.class));
            diffableReservationAffinity.copyFrom(reservationAffinity);
        }
        setReservationAffinity(diffableReservationAffinity);

        ComputeScheduling diffableScheduling = null;
        Scheduling scheduling = model.getScheduling();

        if (scheduling != null) {
            diffableScheduling = Optional.ofNullable(getScheduling())
                .orElse(newSubresource(ComputeScheduling.class));
            diffableScheduling.copyFrom(scheduling);
        }
        setScheduling(diffableScheduling);

        List<ComputeServiceAccount> diffableServiceAccounts = null;
        List<ServiceAccount> serviceAccounts = model.getServiceAccounts();

        if (serviceAccounts != null && !serviceAccounts.isEmpty()) {
            diffableServiceAccounts = serviceAccounts
                .stream()
                .map(serviceAccount -> {
                    // TODO: isEqualTo
                    ComputeServiceAccount diffableServiceAccount = newSubresource(ComputeServiceAccount.class);
                    diffableServiceAccount.copyFrom(serviceAccount);
                    return diffableServiceAccount;
                })
                .collect(Collectors.toList());
        }
        setServiceAccount(diffableServiceAccounts);

        ComputeShieldedInstanceConfig diffableShieldedInstanceConfig = null;
        ShieldedInstanceConfig shieldedInstanceConfig = model.getShieldedInstanceConfig();

        if (shieldedInstanceConfig != null) {
            diffableShieldedInstanceConfig = Optional.ofNullable(getShieldedInstanceConfig())
                .orElse(newSubresource(ComputeShieldedInstanceConfig.class));
            diffableShieldedInstanceConfig.copyFrom(shieldedInstanceConfig);
        }
        setShieldedInstanceConfig(diffableShieldedInstanceConfig);

        ComputeTags diffableTags = null;
        Tags tags = model.getTags();

        if (tags != null) {
            diffableTags = Optional.ofNullable(getTags())
                .orElse(newSubresource(ComputeTags.class));
            diffableTags.copyFrom(tags);
        }
        setTags(diffableTags);
    }

    public InstanceProperties toInstanceProperties() {
        InstanceProperties instanceProperties = new InstanceProperties();
        instanceProperties.setCanIpForward(getCanIpForward());
        instanceProperties.setDescription(getDescription());
        List<InstanceAttachedDisk> disk = getDisk();

        if (!disk.isEmpty()) {
            instanceProperties.setDisks(disk
                .stream()
                .map(InstanceAttachedDisk::copyTo)
                .collect(Collectors.toList()));
        }
        List<ComputeAcceleratorConfig> guestAccelerator = getGuestAccelerator();

        if (!guestAccelerator.isEmpty()) {
            instanceProperties.setGuestAccelerators(guestAccelerator
                .stream()
                .map(ComputeAcceleratorConfig::toAcceleratorConfig)
                .collect(Collectors.toList()));
        }
        instanceProperties.setLabels(getLabels());
        instanceProperties.setMachineType(getMachineType());
        List<ProjectMetadataItemResource> projectMetadata = getMetadata();

        if (!projectMetadata.isEmpty()) {
            Metadata metadata = new Metadata();
            metadata.setItems(projectMetadata
                .stream()
                .map(e -> {
                    Metadata.Items items = new Metadata.Items();
                    items.setKey(e.getKey());
                    items.setValue(e.getValue());
                    return items;
                })
                .collect(Collectors.toList()));
        }
        instanceProperties.setMinCpuPlatform(getMinCpuPlatform());
        List<InstanceNetworkInterface> networkInterface = getNetworkInterface();

        if (!networkInterface.isEmpty()) {
            instanceProperties.setNetworkInterfaces(networkInterface
                .stream()
                .map(InstanceNetworkInterface::copyTo)
                .collect(Collectors.toList()));
        }
        ComputeReservationAffinity reservationAffinity = getReservationAffinity();

        if (reservationAffinity != null) {
            instanceProperties.setReservationAffinity(reservationAffinity.toReservationAffinity());
        }
        ComputeScheduling scheduling = getScheduling();

        if (scheduling != null) {
            instanceProperties.setScheduling(scheduling.toScheduling());
        }
        List<ComputeServiceAccount> serviceAccount = getServiceAccount();

        if (!serviceAccount.isEmpty()) {
            instanceProperties.setServiceAccounts(serviceAccount
                .stream()
                .map(ComputeServiceAccount::toServiceAccount)
                .collect(Collectors.toList()));
        }
        ComputeShieldedInstanceConfig shieldedInstanceConfig = getShieldedInstanceConfig();

        if (shieldedInstanceConfig != null) {
            instanceProperties.setShieldedInstanceConfig(shieldedInstanceConfig.toShieldedInstanceConfig());
        }
        ComputeTags tags = getTags();

        if (tags != null) {
            instanceProperties.setTags(tags.toTags());
        }
        return instanceProperties;
    }
}
