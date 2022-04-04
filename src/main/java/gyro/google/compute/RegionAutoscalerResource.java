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

import com.google.api.gax.rpc.NotFoundException;
import com.google.cloud.compute.v1.Autoscaler;
import com.google.cloud.compute.v1.DeleteRegionAutoscalerRequest;
import com.google.cloud.compute.v1.GetRegionAutoscalerRequest;
import com.google.cloud.compute.v1.InsertRegionAutoscalerRequest;
import com.google.cloud.compute.v1.Operation;
import com.google.cloud.compute.v1.PatchRegionAutoscalerRequest;
import com.google.cloud.compute.v1.RegionAutoscalersClient;
import gyro.core.GyroUI;
import gyro.core.Type;
import gyro.core.scope.State;
import gyro.core.validation.Required;
import gyro.google.util.Utils;

/**
 * Creates an region autoscaler.
 *
 * Example
 * -------
 *
 * .. code-block:: gyro
 *
 *     google::compute-region-autoscaler region-autoscaler-example
 *         name: "region-autoscaler-example"
 *         description: "Region autoscaler example"
 *
 *         autoscaling-policy
 *             cool-down-period-sec: 70
 *             max-num-replicas: 5
 *         end
 *
 *         instance-group-manager: $(google::compute-region-instance-group-manager region-instance-group-manager-example)
 *         region: "us-central1"
 *     end
 */
@Type("compute-region-autoscaler")
public class RegionAutoscalerResource extends AbstractAutoscalerResource {

    private RegionInstanceGroupManagerResource instanceGroupManager;

    private String region;

    /**
     * The managed instance group that this autoscaler will scale.
     *
     * @resource gyro.google.compute.RegionInstanceGroupManagerResource
     */
    @Required
    public RegionInstanceGroupManagerResource getInstanceGroupManager() {
        return instanceGroupManager;
    }

    public void setInstanceGroupManager(RegionInstanceGroupManagerResource instanceGroupManager) {
        this.instanceGroupManager = instanceGroupManager;
    }

    /**
     * The region where the autoscaler resides.
     */
    @Required
    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    @Override
    public void copyFrom(Autoscaler model) {
        super.copyFrom(model);

        if (model.hasTarget()) {
            setInstanceGroupManager(findById(RegionInstanceGroupManagerResource.class, model.getTarget()));
        }

        if (model.hasRegion()) {
            setRegion(Utils.extractName(model.getRegion()));
        }
    }

    @Override
    protected boolean doRefresh() {
        try (RegionAutoscalersClient client = createClient(RegionAutoscalersClient.class)) {
            Autoscaler autoscaler = getRegionAutoscaler(client);

            if (autoscaler == null) {
                return false;
            }

            copyFrom(autoscaler);

            return true;
        }
    }

    @Override
    protected void doDelete(GyroUI ui, State state) {
        try (RegionAutoscalersClient client = createClient(RegionAutoscalersClient.class)) {
            Operation operation = client.deleteCallable().call(DeleteRegionAutoscalerRequest.newBuilder()
                .setProject(getProjectId())
                .setRegion(getRegion())
                .setAutoscaler(getName())
                .build());

            waitForCompletion(operation);
        }
    }

    @Override
    void insert(Autoscaler autoscaler) {
        Autoscaler.Builder builder = autoscaler.toBuilder();
        Optional.ofNullable(getInstanceGroupManager())
            .map(RegionInstanceGroupManagerResource::getSelfLink)
            .ifPresent(builder::setTarget);

        try (RegionAutoscalersClient client = createClient(RegionAutoscalersClient.class)) {
            Operation operation = client.insertCallable().call(InsertRegionAutoscalerRequest.newBuilder()
                .setProject(getProjectId())
                .setRegion(getRegion())
                .setAutoscalerResource(builder)
                .build());

            waitForCompletion(operation);
        }
    }

    @Override
    void patch(Autoscaler autoscaler) {
        try (RegionAutoscalersClient client = createClient(RegionAutoscalersClient.class)) {
            Operation operation = client.patchCallable()
                .call(PatchRegionAutoscalerRequest.newBuilder()
                    .setProject(getProjectId())
                    .setRegion(getRegion())
                    .setAutoscaler(getName())
                    .setAutoscalerResource(autoscaler)
                    .build());

            waitForCompletion(operation);
        }
    }

    private Autoscaler getRegionAutoscaler(RegionAutoscalersClient client) {
        Autoscaler autoscaler = null;

        try {
            autoscaler = client.get(GetRegionAutoscalerRequest.newBuilder()
                .setProject(getProjectId())
                .setRegion(getRegion())
                .setAutoscaler(getName())
                .build());

        } catch (NotFoundException ex) {
            // ignore
        }

        return autoscaler;
    }
}
