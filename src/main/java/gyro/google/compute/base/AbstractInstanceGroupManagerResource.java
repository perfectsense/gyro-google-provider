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

package gyro.google.compute.base;

import java.util.ArrayList;
import java.util.List;

import com.google.api.services.compute.model.InstanceGroupManager;
import gyro.core.resource.Output;
import gyro.core.validation.ConflictsWith;
import gyro.core.validation.Regex;
import gyro.core.validation.Required;
import gyro.google.Copyable;
import gyro.google.GoogleResource;
import gyro.google.compute.InstanceTemplateResource;

public abstract class AbstractInstanceGroupManagerResource extends GoogleResource
    implements Copyable<InstanceGroupManager> {

    private String baseInstanceName;

    private String name;

    private Integer targetSize;

    private List<ComputeInstanceGroupManagerAutoHealingPolicy> autoHealingPolicy;

    private String description;

    private ComputeDistributionPolicy distributionPolicy;

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

    private String region;

    private String selfLink;

    private ComputeInstanceGroupManagerStatus status;

    private String zone;

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
     * @subresource gyro.google.compute.base.ComputeInstanceGroupManagerAutoHealingPolicy
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
     * Policy specifying intended distribution of instances in regional managed instance group.
     *
     * @subresource gyro.google.compute.base.ComputeDistributionPolicy
     */
    public ComputeDistributionPolicy getDistributionPolicy() {
        return distributionPolicy;
    }

    public void setDistributionPolicy(ComputeDistributionPolicy distributionPolicy) {
        this.distributionPolicy = distributionPolicy;
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
     * @subresource gyro.google.compute.base.ComputeNamedPort
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
     * @subresource gyro.google.compute.base.ComputeInstanceGroupManagerUpdatePolicy
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
     * @subresource gyro.google.compute.base.ComputeInstanceGroupManagerVersion
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
     * @subresource gyro.google.compute.base.ComputeInstanceGroupManagerActionsSummary
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
     * The URL of the region where the managed instance group resides (for regional resources).
     */
    @Output
    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
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
     * @subresource gyro.google.compute.base.ComputeInstanceGroupManagerStatus
     */
    @Output
    public ComputeInstanceGroupManagerStatus getStatus() {
        return status;
    }

    public void setStatus(ComputeInstanceGroupManagerStatus status) {
        this.status = status;
    }

    /**
     * The URL of the zone where the managed instance group is located (for zonal resources).
     */
    @Required
    public String getZone() {
        return zone;
    }

    public void setZone(String zone) {
        this.zone = zone;
    }
}
