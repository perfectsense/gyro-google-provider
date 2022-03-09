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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.api.client.util.Data;
import com.google.api.gax.rpc.ApiException;
import com.google.api.gax.rpc.StatusCode;
import com.google.cloud.compute.v1.Instance;
import com.google.cloud.compute.v1.InstanceGroupManager;
import com.google.cloud.compute.v1.InstanceGroupManagerAutoHealingPolicy;
import com.google.cloud.compute.v1.InstancesClient;
import gyro.core.GyroUI;
import gyro.core.resource.Id;
import gyro.core.resource.Output;
import gyro.core.resource.Resource;
import gyro.core.resource.Updatable;
import gyro.core.scope.State;
import gyro.core.validation.CollectionMax;
import gyro.core.validation.ConflictsWith;
import gyro.core.validation.Min;
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

    private List<TargetPoolResource> targetPools;

    private ComputeInstanceGroupManagerUpdatePolicy updatePolicy;

    private List<ComputeInstanceGroupManagerVersion> version;

    private ComputeInstanceGroupManagerActionsSummary currentActions;

    private String instanceGroupLink;

    private String selfLink;

    private ComputeInstanceGroupManagerStatus status;

    abstract void insert(InstanceGroupManager instanceGroupManager) throws Exception;

    abstract void patch(InstanceGroupManager instanceGroupManager) throws Exception;

    abstract void setInstanceTemplate() throws Exception;

    abstract void setTargetPools() throws Exception;

    /**
     * The base instance name to use for instances in this group.
     * The value must be 1-58 characters long. Instances are named by appending a hyphen and a random four-character string to the base instance name. The base instance name must comply with RFC1035.
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
     * The name of the managed instance group.
     */
    @Required
    @Regex(value = "[a-z](?:[-a-z0-9]{0,61}[a-z0-9])?", message = "a string 1-63 characters long and the first character must be a lowercase letter, and all following characters must be a dash, lowercase letter, or digit, except the last character, which cannot be a dash")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * The target number of running instances for this managed instance group.
     * Deleting or abandoning instances reduces this number. Resizing the group changes this number. Defaults to ``0``.
     */
    @Min(0)
    @Updatable
    public Integer getTargetSize() {
        if (targetSize == null) {
            return 0;
        }

        return targetSize;
    }

    public void setTargetSize(Integer targetSize) {
        this.targetSize = targetSize;
    }

    /**
     * The autohealing policy for this managed instance group.
     *
     * @subresource gyro.google.compute.ComputeInstanceGroupManagerAutoHealingPolicy
     */
    @CollectionMax(1)
    @Updatable
    public List<ComputeInstanceGroupManagerAutoHealingPolicy> getAutoHealingPolicy() {
        if (autoHealingPolicy == null) {
            autoHealingPolicy = new ArrayList<>();
        }
        return autoHealingPolicy;
    }

    public void setAutoHealingPolicy(List<ComputeInstanceGroupManagerAutoHealingPolicy> autoHealingPolicy) {
        this.autoHealingPolicy = autoHealingPolicy;
    }

    /**
     * Description of this resource.
     */
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Fingerprint of this resource.
     * This field may be used in optimistic locking. It will be ignored when inserting an InstanceGroupManager. An up-to-date fingerprint must be provided in order to update the InstanceGroupManager, otherwise the request will fail with error 412 ``conditionNotMet``.
     */
    public String getFingerprint() {
        return fingerprint;
    }

    public void setFingerprint(String fingerprint) {
        this.fingerprint = fingerprint;
    }

    /**
     * The instance template that is specified for this managed instance group.
     * The group uses this template to create all new instances in the managed instance group.
     *
     * @resource gyro.google.compute.InstanceTemplateResource
     */
    @ConflictsWith("version")
    @Updatable
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

    /**
     * All TargetPool resources to which instances in the instance group are added.
     * The target pools automatically apply to all of the instances in the managed instance group.
     *
     * @resource gyro.google.compute.TargetPoolResource
     */
    @Updatable
    public List<TargetPoolResource> getTargetPools() {
        if (targetPools == null) {
            targetPools = new ArrayList<>();
        }

        return targetPools;
    }

    public void setTargetPools(List<TargetPoolResource> targetPools) {
        this.targetPools = targetPools;
    }

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
     * Specifies the instance templates used by this managed instance group to create instances.
     * Each version is defined by an ``instance-template`` and a ``name``. Every version can appear at most once per instance group. This field overrides the top-level ``instance-template`` field. Exactly one version must leave the ``target-size`` field unset. That version will be applied to all remaining instances.
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
     * The URL of the Instance Group resource.
     */
    @Output
    public String getInstanceGroupLink() {
        return instanceGroupLink;
    }

    public void setInstanceGroupLink(String instanceGroupLink) {
        this.instanceGroupLink = instanceGroupLink;
    }

    /**
     * The URL for this managed instance group. The server defines this URL.
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
        setName(model.getName());

        if (model.hasInstanceGroup()) {
            setInstanceGroupLink(model.getInstanceGroup());
        }

        if (model.hasSelfLink()) {
            setSelfLink(model.getSelfLink());
        }

        if (model.hasBaseInstanceName()) {
            setBaseInstanceName(model.getBaseInstanceName());
        }

        if (model.hasTargetSize()) {
            setTargetSize(model.getTargetSize());
        }

        if (model.hasDescription()) {
            setDescription(model.getDescription());
        }

        if (model.hasFingerprint()) {
            setFingerprint(model.getFingerprint());
        }

        if (model.hasInstanceTemplate()) {
            setInstanceTemplate(findById(InstanceTemplateResource.class, model.getInstanceTemplate()));
        }

        setStatus(null);
        if (model.hasStatus()) {
            ComputeInstanceGroupManagerStatus ms = newSubresource(ComputeInstanceGroupManagerStatus.class);
            ms.copyFrom(model.getStatus());

            setStatus(ms);
        }

        setCurrentActions(null);
        if (model.hasCurrentActions()) {
            ComputeInstanceGroupManagerActionsSummary ca =
                newSubresource(ComputeInstanceGroupManagerActionsSummary.class);
            ca.copyFrom(model.getCurrentActions());

            setCurrentActions(ca);
        }

        setUpdatePolicy(null);
        if (model.hasUpdatePolicy()) {
            ComputeInstanceGroupManagerUpdatePolicy up = newSubresource(ComputeInstanceGroupManagerUpdatePolicy.class);
            up.copyFrom(model.getUpdatePolicy());

            setUpdatePolicy(up);
        }

        setAutoHealingPolicy(null);
        if (!model.getAutoHealingPoliciesList().isEmpty()) {
            List<ComputeInstanceGroupManagerAutoHealingPolicy> diffableAutoHealingPolicies =
                model.getAutoHealingPoliciesList().stream()
                    .map(e -> {
                        ComputeInstanceGroupManagerAutoHealingPolicy computeInstanceGroupManagerAutoHealingPolicy =
                            newSubresource(ComputeInstanceGroupManagerAutoHealingPolicy.class);
                        computeInstanceGroupManagerAutoHealingPolicy.copyFrom(e);

                        return computeInstanceGroupManagerAutoHealingPolicy;
                    })
                    .collect(Collectors.toList());

            setAutoHealingPolicy(diffableAutoHealingPolicies);
        }

        setNamedPort(null);
        if (!model.getNamedPortsList().isEmpty()) {
            List<ComputeNamedPort> diffableNamedPorts = null;
            diffableNamedPorts = model.getNamedPortsList()
                .stream()
                .map(e -> {
                    ComputeNamedPort computeNamedPort = newSubresource(ComputeNamedPort.class);
                    computeNamedPort.copyFrom(e);

                    return computeNamedPort;
                })
                .collect(Collectors.toList());

            setNamedPort(diffableNamedPorts);
        }

        setTargetPools(null);
        if (!model.getTargetPoolsList().isEmpty()) {
            List<TargetPoolResource> diffableTargetPools = model.getTargetPoolsList()
                .stream()
                .map(e -> findById(TargetPoolResource.class, e))
                .collect(Collectors.toList());

            setTargetPools(diffableTargetPools);
        }

        setVersion(null);
        if (!model.getVersionsList().isEmpty()) {
            List<ComputeInstanceGroupManagerVersion> diffableInstanceGroupManagerVersion =
                model.getVersionsList().stream().map(e -> {
                    ComputeInstanceGroupManagerVersion computeVersion = newSubresource(
                        ComputeInstanceGroupManagerVersion.class);
                    computeVersion.copyFrom(e);
                    return computeVersion;
                }).collect(Collectors.toList());

            setVersion(diffableInstanceGroupManagerVersion);
        }
    }

    @Override
    protected void doCreate(GyroUI ui, State state) throws Exception {
        InstanceGroupManager.Builder instanceGroupManager = InstanceGroupManager.newBuilder();
        instanceGroupManager.setName(getName());

        if (getTargetSize() != null) {
            instanceGroupManager.setTargetSize(getTargetSize());
        }

        if (getBaseInstanceName() != null) {
            instanceGroupManager.setBaseInstanceName(getBaseInstanceName());
        }

        if (getDescription() != null) {
            instanceGroupManager.setDescription(getDescription());
        }

        if (getFingerprint() != null) {
            instanceGroupManager.setFingerprint(getFingerprint());
        }

        if (getInstanceTemplate() != null) {
            instanceGroupManager.setInstanceTemplate(getInstanceTemplate().getSelfLink());
        }

        if (getUpdatePolicy() != null) {
            instanceGroupManager.setUpdatePolicy(getUpdatePolicy().copyTo());
        }

        instanceGroupManager.addAllAutoHealingPolicies(getAutoHealingPolicy()
            .stream()
            .map(ComputeInstanceGroupManagerAutoHealingPolicy::copyTo)
            .collect(Collectors.toList()));

        instanceGroupManager.addAllNamedPorts(getNamedPort()
            .stream()
            .map(ComputeNamedPort::copyTo)
            .collect(Collectors.toList()));

        instanceGroupManager.addAllTargetPools(getTargetPools()
            .stream()
            .map(TargetPoolResource::getSelfLink)
            .collect(Collectors.toList()));

        instanceGroupManager.addAllVersions(getVersion()
            .stream()
            .map(ComputeInstanceGroupManagerVersion::copyTo)
            .collect(Collectors.toList()));

        insert(instanceGroupManager.build());

        refresh();
    }

    @Override
    protected void doUpdate(GyroUI ui, State state, Resource current, Set<String> changedFieldNames)
        throws Exception {
        // TODO: investigate as GCP UI allows updating of named ports.

        boolean shouldPatch = false;
        InstanceGroupManager.Builder instanceGroupManager = InstanceGroupManager.newBuilder();

        for (String changedFieldName : changedFieldNames) {
            // template changes
            if (changedFieldName.equals("instance-template")) {
                setInstanceTemplate();
            } else if (changedFieldName.equals("target-pools")) {
                setTargetPools();
            } else if (changedFieldName.equals("target-size")) {
                instanceGroupManager.setTargetSize(getTargetSize());
                shouldPatch = true;
            } else if (changedFieldName.equals("auto-healing-policy")) {
                List<ComputeInstanceGroupManagerAutoHealingPolicy> diffableAutoHealingPolicy = getAutoHealingPolicy();
                List<InstanceGroupManagerAutoHealingPolicy> autoHealingPolicies = null;

                if (diffableAutoHealingPolicy.isEmpty()) {
                    autoHealingPolicies = Data.nullOf(List.class);
                } else {
                    autoHealingPolicies = diffableAutoHealingPolicy
                        .stream()
                        .map(ComputeInstanceGroupManagerAutoHealingPolicy::copyTo)
                        .collect(Collectors.toList());
                }
                instanceGroupManager.addAllAutoHealingPolicies(autoHealingPolicies);
                shouldPatch = true;
            }
        }

        if (shouldPatch) {
            patch(instanceGroupManager.build());
        }
        refresh();
    }

    Instance getInstance(InstancesClient client, String name, String zone) throws IOException {
        Instance instance = null;

        try {
            instance = client.get(getProjectId(), zone, name);
        } catch (ApiException ex) {
            if (ex.getStatusCode().getCode().equals(StatusCode.Code.NOT_FOUND)) {
                throw ex;
            }
        }

        return instance;
    }
}
