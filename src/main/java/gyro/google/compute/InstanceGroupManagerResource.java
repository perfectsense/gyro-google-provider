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

import java.util.Set;

import com.google.api.services.compute.Compute;
import com.google.api.services.compute.model.InstanceGroupManager;
import com.google.api.services.compute.model.Operation;
import gyro.core.GyroUI;
import gyro.core.Type;
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
        // TODO:
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
        // DO NOT update the value from the provider as it's a full url.
        //        setZone(model.getZone());
        super.copyFrom(model);
    }
}
