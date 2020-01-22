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
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.api.services.compute.model.InstanceGroupManager;
import com.google.api.services.compute.model.InstanceGroupManagerAutoHealingPolicy;
import com.google.api.services.compute.model.InstanceGroupManagerVersion;
import com.google.api.services.compute.model.NamedPort;
import gyro.core.resource.Output;
import gyro.core.validation.ConflictsWith;
import gyro.core.validation.Regex;
import gyro.core.validation.Required;
import gyro.core.validation.ValidationError;
import gyro.google.Copyable;

public abstract class AbstractInstanceGroupManagerResource extends ComputeResource
    implements Copyable<InstanceGroupManager> {

    private String baseInstanceName;

    private String name;

    private Integer targetSize;

    private List<ComputeInstanceGroupManagerAutoHealingPolicy> autoHealingPolicy;

    private String description;

    private String fingerprint;

    private InstanceTemplateResource instanceTemplate;

    private List<ComputeNamedPort> namedPort;

    // TODO: target pool resource
    // https://github.com/perfectsense/gyro-google-provider/issues/79
    //    private List<AbstractTargetPoolResource> targetPools;

    private ComputeInstanceGroupManagerUpdatePolicy updatePolicy;

    private List<ComputeInstanceGroupManagerVersion> version;

    private ComputeInstanceGroupManagerActionsSummary currentActions;

    private String id;

    private String instanceGroup;

    private String selfLink;

    private ComputeInstanceGroupManagerStatus status;

    /**
     * The base instance name to use for instances in this group. The value must be 1-58 characters long. Instances are named by appending a hyphen and a random four-character string to the base instance name. The base instance name must comply with RFC1035.
     */
    @Required
    @Regex("[a-z][-a-z0-9]{0,57}")
    public String getBaseInstanceName() {
        return baseInstanceName;
    }

    public void setBaseInstanceName(String baseInstanceName) {
        this.baseInstanceName = baseInstanceName;
    }

    /**
     * The name of the managed instance group. The name must be 1-63 characters long, and comply with RFC1035.
     */
    @Required
    @Regex("[a-z](?:[-a-z0-9]{0,61}[a-z0-9])?")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * The target number of running instances for this managed instance group. Deleting or abandoning instances reduces this number. Resizing the group changes this number.
     */
    @Required
    public Integer getTargetSize() {
        return targetSize;
    }

    public void setTargetSize(Integer targetSize) {
        this.targetSize = targetSize;
    }

    /**
     * The autohealing policy for this managed instance group. You can specify only one value.
     *
     * @subresource gyro.google.compute.ComputeInstanceGroupManagerAutoHealingPolicy
     */
    public List<ComputeInstanceGroupManagerAutoHealingPolicy> getAutoHealingPolicy() {
        if (autoHealingPolicy == null) {
            autoHealingPolicy = new ArrayList();
        }
        return autoHealingPolicy;
    }

    public void setAutoHealingPolicy(List<ComputeInstanceGroupManagerAutoHealingPolicy> autoHealingPolicy) {
        this.autoHealingPolicy = autoHealingPolicy;
    }

    /**
     * An optional description of this resource. Provide this property when you create the resource.
     */
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Fingerprint of this resource. This field may be used in optimistic locking. It will be ignored when inserting an InstanceGroupManager. An up-to-date fingerprint must be provided in order to update the InstanceGroupManager, otherwise the request will fail with error 412 conditionNotMet.To see the latest fingerprint, make a get() request to retrieve an InstanceGroupManager.
     */
    public String getFingerprint() {
        return fingerprint;
    }

    public void setFingerprint(String fingerprint) {
        this.fingerprint = fingerprint;
    }

    /**
     * The URL of the instance template that is specified for this managed instance group. The group uses this template to create all new instances in the managed instance group.
     *
     * @resource gyro.google.compute.InstanceTemplateResource
     */
    @ConflictsWith("version")
    public InstanceTemplateResource getInstanceTemplate() {
        return instanceTemplate;
    }

    public void setInstanceTemplate(InstanceTemplateResource instanceTemplate) {
        this.instanceTemplate = instanceTemplate;
    }

    /**
     * Named ports configured for the Instance Groups complementary to this Instance Group Manager.
     *
     * @subresource gyro.google.compute.ComputeNamedPort
     */
    public List<ComputeNamedPort> getNamedPort() {
        if (namedPort == null) {
            namedPort = new ArrayList<>();
        }

        return namedPort;
    }

    public void setNamedPort(List<ComputeNamedPort> namedPort) {
        this.namedPort = namedPort;
    }

    // TODO: target pool resource
    // https://github.com/perfectsense/gyro-google-provider/issues/79
    //    /**
    //     * The URLs for all TargetPool resources to which instances in the instanceGroup field are added. The target pools automatically apply to all of the instances in the managed instance group.
    //     */
    //    public List<AbstractTargetPoolResource> getTargetPools() {
    //        if (targetPools == null) {
    //            targetPools = new ArrayList<>();
    //        }
    //
    //        return targetPools;
    //    }
    //
    //    public void setTargetPools(List<AbstractTargetPoolResource> targetPools) {
    //        this.targetPools = targetPools;
    //    }

    /**
     * The update policy for this managed instance group.
     *
     * @subresource gyro.google.compute.ComputeInstanceGroupManagerUpdatePolicy
     */
    public ComputeInstanceGroupManagerUpdatePolicy getUpdatePolicy() {
        return updatePolicy;
    }

    public void setUpdatePolicy(ComputeInstanceGroupManagerUpdatePolicy updatePolicy) {
        this.updatePolicy = updatePolicy;
    }

    /**
     * Specifies the instance templates used by this managed instance group to create instances.Each version is defined by an instanceTemplate and a name. Every version can appear at most once per instance group. This field overrides the top-level instanceTemplate field. Read more about the relationships between these fields. Exactly one version must leave the targetSize field unset. That version will be applied to all remaining instances. For more information, read about canary updates.
     *
     * @subresource gyro.google.compute.ComputeInstanceGroupManagerVersion
     */
    @ConflictsWith("instance-template")
    public List<ComputeInstanceGroupManagerVersion> getVersion() {
        if (version == null) {
            version = new ArrayList<>();
        }

        return version;
    }

    public void setVersion(List<ComputeInstanceGroupManagerVersion> version) {
        this.version = version;
    }

    /**
     * The list of instance actions and the number of instances in this managed instance group that are scheduled for each of those actions.
     *
     * @subresource gyro.google.compute.ComputeInstanceGroupManagerActionsSummary
     */
    @Output
    public ComputeInstanceGroupManagerActionsSummary getCurrentActions() {
        return currentActions;
    }

    public void setCurrentActions(ComputeInstanceGroupManagerActionsSummary currentActions) {
        this.currentActions = currentActions;
    }

    /**
     * A unique identifier for this resource type. The server generates this identifier.
     */
    @Output
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    /**
     * The URL of the Instance Group resource.
     */
    @Output
    public String getInstanceGroup() {
        return instanceGroup;
    }

    public void setInstanceGroup(String instanceGroup) {
        this.instanceGroup = instanceGroup;
    }

    /**
     * The URL for this managed instance group. The server defines this URL.
     */
    @Output
    public String getSelfLink() {
        return selfLink;
    }

    public void setSelfLink(String selfLink) {
        this.selfLink = selfLink;
    }

    /**
     * The status of this managed instance group.
     *
     * @subresource gyro.google.compute.ComputeInstanceGroupManagerStatus
     */
    @Output
    public ComputeInstanceGroupManagerStatus getStatus() {
        return status;
    }

    public void setStatus(ComputeInstanceGroupManagerStatus status) {
        this.status = status;
    }

    @Override
    public List<ValidationError> validate(Set<String> configuredFields) {
        List<ValidationError> errors = new ArrayList<>();

        if ((!configuredFields.contains("instance-template") && !configuredFields.contains("version"))
            || (getInstanceTemplate() == null && getVersion().isEmpty())) {
            errors.add(new ValidationError(
                this,
                "instance-template",
                "Either 'instance-template' or 'version' is required!"));
            errors.add(new ValidationError(
                this,
                "version",
                "Either 'instance-template' or 'version' is required!"));
        }
        return errors;
    }

    @Override
    public void copyFrom(InstanceGroupManager model) {
        setBaseInstanceName(model.getBaseInstanceName());
        setName(model.getName());
        setTargetSize(model.getTargetSize());
        List<ComputeInstanceGroupManagerAutoHealingPolicy> diffableAutoHealingPolicies = null;
        List<InstanceGroupManagerAutoHealingPolicy> autoHealingPolicies = model.getAutoHealingPolicies();

        if (autoHealingPolicies != null && !autoHealingPolicies.isEmpty()) {
            diffableAutoHealingPolicies = autoHealingPolicies
                .stream()
                .map(e -> {
                    ComputeInstanceGroupManagerAutoHealingPolicy computeInstanceGroupManagerAutoHealingPolicy = newSubresource(
                        ComputeInstanceGroupManagerAutoHealingPolicy.class);
                    computeInstanceGroupManagerAutoHealingPolicy.copyFrom(e);
                    return computeInstanceGroupManagerAutoHealingPolicy;
                })
                .collect(Collectors.toList());
        }
        setAutoHealingPolicy(diffableAutoHealingPolicies);
        setDescription(getDescription());
        setFingerprint(model.getFingerprint());
        setInstanceTemplate(Optional.ofNullable(model.getInstanceTemplate())
            .map(e -> findById(InstanceTemplateResource.class, e))
            .orElse(null));
        List<ComputeNamedPort> diffableNamedPorts = null;
        List<NamedPort> namedPorts = model.getNamedPorts();

        if (namedPorts != null && !namedPorts.isEmpty()) {
            diffableNamedPorts = namedPorts
                .stream()
                .map(e -> {
                    ComputeNamedPort computeNamedPort = newSubresource(ComputeNamedPort.class);
                    computeNamedPort.copyFrom(e);
                    return computeNamedPort;
                })
                .collect(Collectors.toList());
        }
        setNamedPort(diffableNamedPorts);
        // TODO: https://github.com/perfectsense/gyro-google-provider/issues/79
        //        List<AbstractTargetPoolResource> diffableTargetPools = null;
        //        List<String> targetPools = model.getTargetPools();
        //
        //        if (targetPools != null && !targetPools.isEmpty()) {
        //            diffableTargetPools = targetPools
        //                .stream()
        //                .map(e -> findById(AbstractTargetPoolResource.class, e))
        //                .collect(Collectors.toList());
        //        }
        //        setTargetPools(diffableTargetPools);
        setUpdatePolicy(Optional.ofNullable(model.getUpdatePolicy())
            .map(e -> {
                ComputeInstanceGroupManagerUpdatePolicy updatePolicy = newSubresource(
                    ComputeInstanceGroupManagerUpdatePolicy.class);
                updatePolicy.copyFrom(e);
                return updatePolicy;
            })
            .orElse(null));
        List<ComputeInstanceGroupManagerVersion> diffableInstanceGroupManagerVersion = null;
        List<InstanceGroupManagerVersion> versions = model.getVersions();

        if (versions != null && !versions.isEmpty()) {
            diffableInstanceGroupManagerVersion = versions
                .stream()
                .map(e -> {
                    ComputeInstanceGroupManagerVersion computeVersion = newSubresource(
                        ComputeInstanceGroupManagerVersion.class);
                    computeVersion.copyFrom(e);
                    return computeVersion;
                })
                .collect(Collectors.toList());
        }
        setVersion(diffableInstanceGroupManagerVersion);
        setCurrentActions(Optional.ofNullable(model.getCurrentActions())
            .map(e -> {
                ComputeInstanceGroupManagerActionsSummary instanceGroupManagerActionsSummary = newSubresource(
                    ComputeInstanceGroupManagerActionsSummary.class);
                instanceGroupManagerActionsSummary.copyFrom(e);
                return instanceGroupManagerActionsSummary;
            })
            .orElse(null));
        setInstanceGroup(model.getInstanceGroup());
        setSelfLink(model.getSelfLink());
        setStatus(Optional.ofNullable(model.getStatus())
            .map(e -> {
                ComputeInstanceGroupManagerStatus computeInstanceGroupManagerStatus = newSubresource(
                    ComputeInstanceGroupManagerStatus.class);
                computeInstanceGroupManagerStatus.copyFrom(e);
                return computeInstanceGroupManagerStatus;
            })
            .orElse(null));
    }

    protected InstanceGroupManager createInstanceGroupManager() {
        InstanceGroupManager instanceGroupManager = new InstanceGroupManager();
        instanceGroupManager.setBaseInstanceName(getBaseInstanceName());
        instanceGroupManager.setName(getName());
        instanceGroupManager.setTargetSize(getTargetSize());
        instanceGroupManager.setAutoHealingPolicies(getAutoHealingPolicy()
            .stream()
            .map(ComputeInstanceGroupManagerAutoHealingPolicy::copyTo)
            .collect(Collectors.toList()));
        instanceGroupManager.setDescription(getDescription());
        instanceGroupManager.setFingerprint(getFingerprint());
        Optional.ofNullable(getInstanceTemplate())
            .map(InstanceTemplateResource::getSelfLink)
            .ifPresent(instanceGroupManager::setInstanceTemplate);
        instanceGroupManager.setNamedPorts(getNamedPort().stream()
            .map(ComputeNamedPort::copyTo)
            .collect(Collectors.toList()));
        // TODO: https://github.com/perfectsense/gyro-google-provider/issues/79
        //        instanceGroupManager.setTargetPools(getTargetPools()
        //            .stream()
        //            .map(AbstractTargetPoolResource::getSelfLink)
        //            .collect(Collectors.toList()));
        Optional.ofNullable(getUpdatePolicy())
            .map(ComputeInstanceGroupManagerUpdatePolicy::copyTo)
            .ifPresent(instanceGroupManager::setUpdatePolicy);
        instanceGroupManager.setVersions(getVersion()
            .stream()
            .map(ComputeInstanceGroupManagerVersion::copyTo)
            .collect(Collectors.toList()));

        return instanceGroupManager;
    }

}