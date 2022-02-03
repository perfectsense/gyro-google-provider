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

import com.google.api.gax.rpc.InvalidArgumentException;
import com.google.api.gax.rpc.NotFoundException;
import com.google.cloud.compute.v1.Autoscaler;
import com.google.cloud.compute.v1.AutoscalersClient;
import com.google.cloud.compute.v1.DeleteAutoscalerRequest;
import com.google.cloud.compute.v1.GetAutoscalerRequest;
import com.google.cloud.compute.v1.InsertAutoscalerRequest;
import com.google.cloud.compute.v1.Operation;
import com.google.cloud.compute.v1.PatchAutoscalerRequest;
import gyro.core.GyroUI;
import gyro.core.Type;
import gyro.core.scope.State;
import gyro.core.validation.Required;
import gyro.google.util.Utils;

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

        if (model.hasTarget()) {
            setInstanceGroupManager(findById(InstanceGroupManagerResource.class, model.getTarget()));
        }

        if (model.hasZone()) {
            setZone(Utils.extractName(model.getZone()));
        }
    }

    @Override
    protected boolean doRefresh() throws Exception {
        try (AutoscalersClient client = createClient(AutoscalersClient.class)) {
            Autoscaler autoscaler = getAutoscaler(client);

            if (autoscaler == null) {
                return false;
            }

            copyFrom(autoscaler);

            return true;
        }
    }

    @Override
    protected void doDelete(GyroUI ui, State state) throws Exception {
        try (AutoscalersClient client = createClient(AutoscalersClient.class)) {
            Operation operation = client.deleteCallable().call(DeleteAutoscalerRequest.newBuilder()
                .setProject(getProjectId())
                .setZone(getZone())
                .setAutoscaler(getName())
                .build());

            waitForCompletion(operation);
        }
    }

    @Override
    void insert(Autoscaler autoscaler) {
        Autoscaler.Builder builder = autoscaler.toBuilder();
        Optional.ofNullable(getInstanceGroupManager())
            .map(InstanceGroupManagerResource::getSelfLink)
            .ifPresent(builder::setTarget);

        try (AutoscalersClient client = createClient(AutoscalersClient.class)) {
            Operation operation = client.insertCallable().call(InsertAutoscalerRequest.newBuilder()
                .setProject(getProjectId())
                .setZone(getZone())
                .setAutoscalerResource(builder)
                .build());

            waitForCompletion(operation);
        }
    }

    @Override
    void patch(Autoscaler autoscaler) {
        try (AutoscalersClient client = createClient(AutoscalersClient.class)) {
            Operation operation = client.patchCallable().call(PatchAutoscalerRequest.newBuilder()
                .setProject(getProjectId())
                .setZone(getZone())
                .setAutoscaler(getName())
                .setAutoscalerResource(autoscaler)
                .build());

            waitForCompletion(operation);
        }
    }

    private Autoscaler getAutoscaler(AutoscalersClient client) {
        Autoscaler autoscaler = null;

        try {
            autoscaler = client.get(GetAutoscalerRequest.newBuilder().setProject(getProjectId())
                .setAutoscaler(getName()).setZone(getZone()).build());

        } catch (NotFoundException | InvalidArgumentException ex) {
            // ignore
        }

        return autoscaler;
    }
}
