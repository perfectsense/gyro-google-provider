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

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.api.client.util.Data;
import com.google.api.services.compute.Compute;
import com.google.api.services.compute.model.InstanceGroupManager;
import com.google.api.services.compute.model.InstanceGroupManagerAutoHealingPolicy;
import com.google.api.services.compute.model.InstanceGroupManagersSetInstanceTemplateRequest;
import com.google.api.services.compute.model.Operation;
import gyro.core.GyroUI;
import gyro.core.Type;
import gyro.core.resource.Immutable;
import gyro.core.resource.Resource;
import gyro.core.scope.State;
import gyro.core.validation.Required;

/**
 * Creates an instance group manager.
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

    private String zone;

    /**
     * The zone where the managed instance group is located.
     */
    @Immutable
    @Required
    public String getZone() {
        return zone;
    }

    public void setZone(String zone) {
        this.zone = zone;
    }

    @Override
    public boolean doRefresh() throws Exception {
        Compute client = createClient(Compute.class);
        copyFrom(client.instanceGroupManagers().get(getProjectId(), getZone(), getName()).execute());
        return true;
    }

    @Override
    public void doCreate(GyroUI ui, State state) throws Exception {
        InstanceGroupManager instanceGroupManager = createInstanceGroupManager();
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
        // TODO: target pool resource
        // https://github.com/perfectsense/gyro-google-provider/issues/79
        //    private List<AbstractTargetPoolResource> targetPools;
        boolean shouldPatch = false;

        InstanceGroupManager instanceGroupManager = new InstanceGroupManager();

        for (String changedFieldName : changedFieldNames) {
            // template changes
            if (changedFieldName.equals("instance-template")) {
                setInstanceTemplate();
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
                instanceGroupManager.setAutoHealingPolicies(autoHealingPolicies);
                shouldPatch = true;
            } else if (changedFieldName.equals("named-port")) {
                // TODO: investigate as GCP UI allows updating of named ports.
            }
        }

        if (shouldPatch) {
            patch(instanceGroupManager);
        }
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
        super.copyFrom(model);

        // Do NOT update zone with a full url as this should be a name.
        //        setZone(model.getZone());
    }

    private void setInstanceTemplate() throws Exception {
        Compute client = createClient(Compute.class);
        InstanceGroupManagersSetInstanceTemplateRequest request = new InstanceGroupManagersSetInstanceTemplateRequest();
        request.setInstanceTemplate(getInstanceTemplate().getSelfLink());
        Operation operation = client.instanceGroupManagers()
            .setInstanceTemplate(getProjectId(), getZone(), getName(), request)
            .execute();
        waitForCompletion(client, operation);
        refresh();
    }

    private void patch(InstanceGroupManager instanceGroupManager) throws Exception {
        Compute client = createClient(Compute.class);
        Operation operation = client.instanceGroupManagers()
            .patch(getProjectId(), getZone(), getName(), instanceGroupManager)
            .execute();
        waitForCompletion(client, operation);
        refresh();
    }
}
