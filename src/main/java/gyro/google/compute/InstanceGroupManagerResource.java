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

import com.google.api.services.compute.Compute;
import com.google.api.services.compute.model.InstanceGroupManager;
import com.google.api.services.compute.model.InstanceGroupManagersSetInstanceTemplateRequest;
import com.google.api.services.compute.model.Operation;
import gyro.core.GyroUI;
import gyro.core.Type;
import gyro.core.scope.State;
import gyro.core.validation.Required;
import gyro.google.util.Utils;

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
 *         zone: "us-central1-a"
 *     end
 */
@Type("compute-instance-group-manager")
public class InstanceGroupManagerResource extends AbstractInstanceGroupManagerResource {

    private String zone;

    /**
     * The zone where the managed instance group is located.
     */
    @Required
    public String getZone() {
        return zone;
    }

    public void setZone(String zone) {
        this.zone = zone;
    }

    @Override
    public void copyFrom(InstanceGroupManager model) {
        super.copyFrom(model);

        setZone(Utils.extractName(model.getZone()));
    }

    @Override
    protected boolean doRefresh() throws Exception {
        Compute client = createComputeClient();
        copyFrom(client.instanceGroupManagers().get(getProjectId(), getZone(), getName()).execute());
        return true;
    }

    @Override
    protected void doDelete(GyroUI ui, State state) throws Exception {
        Compute client = createComputeClient();
        Operation operation = client.instanceGroupManagers()
            .delete(getProjectId(), getZone(), getName())
            .execute();
        waitForCompletion(client, operation);
    }

    @Override
    void insert(InstanceGroupManager instanceGroupManager) throws Exception {
        Compute client = createComputeClient();
        Operation operation = client.instanceGroupManagers()
            .insert(getProjectId(), getZone(), instanceGroupManager)
            .execute();
        waitForCompletion(client, operation);
    }

    @Override
    void patch(InstanceGroupManager instanceGroupManager) throws Exception {
        Compute client = createComputeClient();
        Operation operation = client.instanceGroupManagers()
            .patch(getProjectId(), getZone(), getName(), instanceGroupManager)
            .execute();
        waitForCompletion(client, operation);
    }

    @Override
    void setInstanceTemplate() throws Exception {
        Compute client = createComputeClient();
        InstanceGroupManagersSetInstanceTemplateRequest request = new InstanceGroupManagersSetInstanceTemplateRequest();
        request.setInstanceTemplate(getInstanceTemplate().getSelfLink());
        Operation operation = client.instanceGroupManagers()
            .setInstanceTemplate(getProjectId(), getZone(), getName(), request)
            .execute();
        waitForCompletion(client, operation);
    }
}
