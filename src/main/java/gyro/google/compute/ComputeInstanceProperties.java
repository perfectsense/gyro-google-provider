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
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.google.cloud.compute.v1.AcceleratorConfig;
import com.google.cloud.compute.v1.AttachedDisk;
import com.google.cloud.compute.v1.InstanceProperties;
import com.google.cloud.compute.v1.Items;
import com.google.cloud.compute.v1.Metadata;
import com.google.cloud.compute.v1.Tags;
import gyro.core.resource.Diffable;
import gyro.core.validation.CollectionMax;
import gyro.core.validation.Regex;
import gyro.core.validation.Required;
import gyro.core.validation.ValidationError;
import gyro.google.Copyable;

public class ComputeInstanceProperties extends Diffable implements Copyable<InstanceProperties> {

    private Boolean canIpForward;

    private String description;

    private List<InstanceAttachedDisk> disk;

    private List<ComputeAcceleratorConfig> guestAccelerator;

    private Map<String, String> labels;

    private String machineType;

    private Map<String, String> metadata;

    private String minCpuPlatform;

    private List<InstanceNetworkInterface> networkInterface;

    private ComputeReservationAffinity reservationAffinity;

    private ComputeScheduling scheduling;

    private List<ComputeServiceAccount> serviceAccount;

    private ComputeShieldedInstanceConfig shieldedInstanceConfig;

    private List<String> tags;

    /**
     * When set to ``true`` instances are created based on this template to send packets with source IP addresses other than their own and receive packets with destination IP addresses other than their own.
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
     * List of guest accelerator card type and count to use for instances created from the instance template.
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
     * Keys may only contain alphanumeric characters, dashes, and underscores, and must be 1-128 characters in length.
     * Values must be 0-262144 characters in length.
     */
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
     */
    @CollectionMax(64)
    @Regex(value = "^[a-z]([-a-z0-9]{0,61}[a-z0-9]$)?", message = "a string with only dashes, lowercase letters, or digits. The first character must be a lowercase letter, and the last character cannot be a dash. Each tag must be 1-63 characters.")
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
    public String primaryKey() {
        return "";
    }

    @Override
    public void copyFrom(InstanceProperties model) {
        setCanIpForward(model.getCanIpForward());
        setDescription(model.getDescription());
        setMachineType(model.getMachineType());
        setMinCpuPlatform(model.getMinCpuPlatform());

        if (model.hasTags()) {
            setTags(model.getTags().getItemsList());
        }

        setLabels(model.getLabelsMap());

        List<InstanceAttachedDisk> diffableAttachedDisks = null;
        List<AttachedDisk> disks = model.getDisksList();
        if (!disks.isEmpty()) {
            diffableAttachedDisks = disks
                .stream()
                .map(attachedDisk -> {
                    InstanceAttachedDisk diffableAttachedDisk = newSubresource(InstanceAttachedDisk.class);
                    diffableAttachedDisk.copyFrom(attachedDisk);
                    return diffableAttachedDisk;
                })
                .collect(Collectors.toList());
        }
        setDisk(diffableAttachedDisks);

        List<ComputeAcceleratorConfig> diffableGuestAccelerators = null;
        List<AcceleratorConfig> guestAccelerators = model.getGuestAcceleratorsList();
        if (!guestAccelerators.isEmpty()) {
            diffableGuestAccelerators = guestAccelerators
                .stream()
                .map(acceleratorConfig -> {
                    ComputeAcceleratorConfig diffableAcceleratorConfig = newSubresource(ComputeAcceleratorConfig.class);
                    diffableAcceleratorConfig.copyFrom(acceleratorConfig);
                    return diffableAcceleratorConfig;
                })
                .collect(Collectors.toList());
        }
        setGuestAccelerator(diffableGuestAccelerators);

        Map<String, String> copiedMetadata = model.getMetadata().getItemsList().stream()
            .collect(Collectors.toMap(Items::getKey, Items::getValue));
        setMetadata(copiedMetadata);

        List<InstanceNetworkInterface> diffableNetworkInterfaces = null;
        if (!model.getNetworkInterfacesList().isEmpty()) {
            diffableNetworkInterfaces = model.getNetworkInterfacesList()
                .stream()
                .map(ni -> {
                    InstanceNetworkInterface diffableNetworkInterface = newSubresource(InstanceNetworkInterface.class);
                    diffableNetworkInterface.copyFrom(ni);

                    return diffableNetworkInterface;
                })
                .collect(Collectors.toList());
        }
        setNetworkInterface(diffableNetworkInterfaces);

        setReservationAffinity(null);
        if (model.hasReservationAffinity()) {
            ComputeReservationAffinity diffableReservationAffinity = newSubresource(ComputeReservationAffinity.class);
            diffableReservationAffinity.copyFrom(model.getReservationAffinity());

            setReservationAffinity(diffableReservationAffinity);
        }

        setScheduling(null);
        if (model.hasScheduling()) {
            ComputeScheduling diffableScheduling = newSubresource(ComputeScheduling.class);
            diffableScheduling.copyFrom(model.getScheduling());

            setScheduling(diffableScheduling);
        }

        setServiceAccount(null);
        if (!model.getServiceAccountsList().isEmpty()) {
            List<ComputeServiceAccount> diffableServiceAccounts = model.getServiceAccountsList()
                .stream()
                .map(serviceAccount -> {
                    ComputeServiceAccount diffableServiceAccount = newSubresource(ComputeServiceAccount.class);
                    diffableServiceAccount.copyFrom(serviceAccount);
                    return diffableServiceAccount;
                })
                .collect(Collectors.toList());

            setServiceAccount(diffableServiceAccounts);
        }

        setShieldedInstanceConfig(null);
        if (model.hasShieldedInstanceConfig()) {
            ComputeShieldedInstanceConfig diffableShieldedInstanceConfig =
                newSubresource(ComputeShieldedInstanceConfig.class);
            diffableShieldedInstanceConfig.copyFrom(model.getShieldedInstanceConfig());

            setShieldedInstanceConfig(diffableShieldedInstanceConfig);
        }
    }

    public InstanceProperties toInstanceProperties() {
        InstanceProperties.Builder builder = InstanceProperties.newBuilder();

        if (getCanIpForward() != null) {
            builder.setCanIpForward(getCanIpForward());
        }

        if (getDescription() != null) {
            builder.setDescription(getDescription());
        }

        if (getLabels() != null) {
            builder.putAllLabels(getLabels());
        }

        if (getMachineType() != null) {
            builder.setMachineType(getMachineType());
        }

        if (getMetadata() != null) {
            builder.setMetadata(buildMetadata());
        }

        if (getMinCpuPlatform() != null) {
            builder.setMinCpuPlatform(getMinCpuPlatform());
        }

        List<InstanceAttachedDisk> disk = getDisk();

        if (!disk.isEmpty()) {
            builder.addAllDisks(disk.stream().map(InstanceAttachedDisk::copyTo).collect(Collectors.toList()));
        }

        List<ComputeAcceleratorConfig> guestAccelerator = getGuestAccelerator();

        if (!guestAccelerator.isEmpty()) {
            builder.addAllGuestAccelerators(guestAccelerator.stream().map(ComputeAcceleratorConfig::toAcceleratorConfig)
                .collect(Collectors.toList()));
        }

        List<InstanceNetworkInterface> networkInterface = getNetworkInterface();

        if (!networkInterface.isEmpty()) {
            builder.addAllNetworkInterfaces(networkInterface.stream().map(InstanceNetworkInterface::copyTo)
                .collect(Collectors.toList()));
        }

        ComputeReservationAffinity reservationAffinity = getReservationAffinity();

        if (reservationAffinity != null) {
            builder.setReservationAffinity(reservationAffinity.toReservationAffinity());
        }

        ComputeScheduling scheduling = getScheduling();

        if (scheduling != null) {
            builder.setScheduling(scheduling.toScheduling());
        }

        List<ComputeServiceAccount> serviceAccount = getServiceAccount();

        if (!serviceAccount.isEmpty()) {
            builder.addAllServiceAccounts(serviceAccount
                .stream()
                .map(ComputeServiceAccount::toServiceAccount)
                .collect(Collectors.toList()));
        }

        ComputeShieldedInstanceConfig shieldedInstanceConfig = getShieldedInstanceConfig();

        if (shieldedInstanceConfig != null) {
            builder.setShieldedInstanceConfig(shieldedInstanceConfig.toShieldedInstanceConfig());
        }

        if (getTags() != null) {
            builder.setTags(buildTags());
        }

        return builder.build();
    }

    private Tags buildTags() {
        return Tags.newBuilder().addAllItems(getTags()).build();
    }

    private Metadata buildMetadata() {
        Metadata.Builder builder = Metadata.newBuilder();
        builder.addAllItems(
            getMetadata().entrySet().stream()
                .map(e -> {
                    Items.Builder item = Items.newBuilder().setKey(e.getKey()).setValue(e.getValue());
                    return item.build();
                })
                .collect(Collectors.toList())
        );

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
}
