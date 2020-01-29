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

import java.util.Optional;

import com.google.api.services.compute.Compute;
import com.google.api.services.compute.model.Autoscaler;
import com.google.api.services.compute.model.Operation;
import gyro.core.GyroUI;
import gyro.core.Type;
import gyro.core.resource.Immutable;
import gyro.core.scope.State;
import gyro.core.validation.Required;

/**
 * Creates an autoscaler.
 *
 * Example
 * -------
 *
 * .. code-block:: gyro
 *
 *     google::compute-autoscaler autoscaler-example
 *         name: "autoscaler-example"
 *         description: "Autoscaler example"
 *
 *         autoscaling-policy
 *             cool-down-period-sec: 70
 *             max-num-replicas: 5
 *         end
 *
 *         instance-group-manager: $(google::compute-instance-group-manager instance-group-manager-example)
 *         zone: "us-central1-a"
 *     end
 */
@Type("compute-autoscaler")
public class AutoscalerResource extends AbstractAutoscalerResource {

    private InstanceGroupManagerResource instanceGroupManager;

    private String zone;

    /**
     * The managed instance group that this autoscaler will scale.
     *
     * @resource gyro.google.compute.InstanceGroupManagerResource
     */
    @Required
    public InstanceGroupManagerResource getInstanceGroupManager() {
        return instanceGroupManager;
    }

    public void setInstanceGroupManager(InstanceGroupManagerResource instanceGroupManager) {
        this.instanceGroupManager = instanceGroupManager;
    }

    /**
     * The zone where the autoscaler resides.
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
    public void copyFrom(Autoscaler model) {
        super.copyFrom(model);

        setInstanceGroupManager(Optional.ofNullable(model.getTarget())
            .map(e -> findById(InstanceGroupManagerResource.class, e))
            .orElse(null));
        // Do NOT update zone with a full url as this should be a name.
        //        setZone(model.getZone());
    }

    @Override
    protected boolean doRefresh() throws Exception {
        Compute client = createComputeClient();
        copyFrom(client.autoscalers().get(getProjectId(), getZone(), getName()).execute());
        return true;
    }

    @Override
    protected void doDelete(GyroUI ui, State state) throws Exception {
        Compute client = createComputeClient();
        Operation operation = client.autoscalers()
            .delete(getProjectId(), getZone(), getName())
            .execute();
        waitForCompletion(client, operation);
    }

    @Override
    void insert(Autoscaler autoscaler) throws Exception {
        Optional.ofNullable(getInstanceGroupManager())
            .map(InstanceGroupManagerResource::getSelfLink)
            .ifPresent(autoscaler::setTarget);

        Compute client = createComputeClient();
        Operation operation = client.autoscalers()
            .insert(getProjectId(), getZone(), autoscaler)
            .execute();
        waitForCompletion(client, operation);
    }

    @Override
    void patch(Autoscaler autoscaler) throws Exception {
        Compute client = createComputeClient();
        Operation operation = client.autoscalers()
            .patch(getProjectId(), getZone(), autoscaler)
            .setAutoscaler(getName())
            .execute();
        waitForCompletion(client, operation);
    }
}
