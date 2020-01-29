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
    @Immutable
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

        setInstanceGroupManager(Optional.ofNullable(model.getTarget())
            .map(e -> findById(RegionInstanceGroupManagerResource.class, e))
            .orElse(null));
        // Do NOT update region with a full url as this should be a name.
        //                setRegion(model.getRegion());
    }

    @Override
    protected boolean doRefresh() throws Exception {
        Compute client = createComputeClient();
        copyFrom(client.regionAutoscalers().get(getProjectId(), getRegion(), getName()).execute());
        return true;
    }

    @Override
    protected void doDelete(GyroUI ui, State state) throws Exception {
        Compute client = createComputeClient();
        Operation operation = client.regionAutoscalers()
            .delete(getProjectId(), getRegion(), getName())
            .execute();
        waitForCompletion(client, operation);
    }

    @Override
    void insert(Autoscaler autoscaler) throws Exception {
        Optional.ofNullable(getInstanceGroupManager())
            .map(RegionInstanceGroupManagerResource::getSelfLink)
            .ifPresent(autoscaler::setTarget);

        Compute client = createComputeClient();
        Operation operation = client.regionAutoscalers()
            .insert(getProjectId(), getRegion(), autoscaler)
            .execute();
        waitForCompletion(client, operation);
    }

    @Override
    void patch(Autoscaler autoscaler) throws Exception {
        Compute client = createComputeClient();
        Operation operation = client.regionAutoscalers()
            .patch(getProjectId(), getRegion(), autoscaler)
            .setAutoscaler(getName())
            .execute();
        waitForCompletion(client, operation);
    }
}
