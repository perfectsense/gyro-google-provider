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

import com.google.api.services.compute.Compute;
import com.google.api.services.compute.model.InstanceGroupManager;
import com.google.api.services.compute.model.InstanceGroupManagerAutoHealingPolicy;
import com.google.api.services.compute.model.InstanceGroupManagerVersion;
import com.google.api.services.compute.model.NamedPort;
import com.google.api.services.compute.model.Operation;
import gyro.core.GyroUI;
import gyro.core.Type;
import gyro.core.resource.Resource;
import gyro.core.scope.State;
import gyro.core.validation.ValidationError;

/**
 * Creates an Instance Group Manager.
 *
 * Example
 * -------
 *
 * .. code-block:: gyro
 *
 *     google::compute-instance-group-manager instance-group-manager-example
 *         name: "instance-group-manager-example"
 *         base-instance-name: "instance-group-manager-example"
 *         description: "Instance group manager example"
 *         instance-template: $(google::compute-instance-template instance-group-template-example)
 *         target-size: 1
 *         zone: "us-central-1"
 *     end
 */
@Type("compute-instance-group-manager")
public class InstanceGroupManagerResource extends AbstractInstanceGroupManagerResource {

    @Override
    public boolean doRefresh() throws Exception {
        Compute client = createClient(Compute.class);
        copyFrom(client.instanceGroupManagers().get(getProjectId(), getZone(), getName()).execute());
        return true;
    }

    @Override
    public void doCreate(GyroUI ui, State state) throws Exception {
        InstanceGroupManager instanceGroupManager = new InstanceGroupManager();
        instanceGroupManager.setBaseInstanceName(getBaseInstanceName());
        instanceGroupManager.setName(getName());
        instanceGroupManager.setTargetSize(getTargetSize());
        instanceGroupManager.setAutoHealingPolicies(getAutoHealingPolicy()
            .stream()
            .map(ComputeInstanceGroupManagerAutoHealingPolicy::copyTo)
            .collect(Collectors.toList()));
        instanceGroupManager.setDescription(getDescription());
        Optional.ofNullable(getDistributionPolicy())
            .map(ComputeDistributionPolicy::copyTo)
            .ifPresent(instanceGroupManager::setDistributionPolicy);
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

        Compute client = createClient(Compute.class);
        Operation operation = client.instanceGroupManagers()
            .insert(getProjectId(), getZone(), instanceGroupManager)
            .execute();
        waitForCompletion(client, operation);
        refresh();
    }

    @Override
    public void doUpdate(GyroUI ui, State state, Resource current, Set<String> changedFieldNames)
        throws Exception {
    }

    @Override
    public void doDelete(GyroUI ui, State state) throws Exception {
        Compute client = createClient(Compute.class);
        Operation operation = client.instanceGroupManagers()
            .delete(getProjectId(), getZone(), getName())
            .execute();
        waitForCompletion(client, operation);
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
        setDistributionPolicy(Optional.ofNullable(model.getDistributionPolicy())
            .map(e -> {
                ComputeDistributionPolicy computeDistributionPolicy = newSubresource(ComputeDistributionPolicy.class);
                computeDistributionPolicy.copyFrom(e);
                return computeDistributionPolicy;
            })
            .orElse(null));
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
        setRegion(model.getRegion());
        setSelfLink(model.getSelfLink());
        setStatus(Optional.ofNullable(model.getStatus())
            .map(e -> {
                ComputeInstanceGroupManagerStatus computeInstanceGroupManagerStatus = newSubresource(
                    ComputeInstanceGroupManagerStatus.class);
                computeInstanceGroupManagerStatus.copyFrom(e);
                return computeInstanceGroupManagerStatus;
            })
            .orElse(null));

        // DO NOT update the value from the provider as it's a full url.
        //        setZone(model.getZone());
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
}
