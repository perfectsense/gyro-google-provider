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
import java.util.Set;

import com.google.api.services.compute.Compute;
import com.google.api.services.compute.model.InstanceGroupManager;
import com.google.api.services.compute.model.Operation;
import gyro.core.GyroUI;
import gyro.core.Type;
import gyro.core.resource.Immutable;
import gyro.core.resource.Resource;
import gyro.core.scope.State;
import gyro.core.validation.Required;

/**
 * Creates a region instance group manager.
 *
 * Example
 * -------
 *
 * .. code-block:: gyro
 *
 *     google::compute-region-instance-group-manager region-instance-group-manager-example
 *         name: "region-instance-group-manager-example"
 *         base-instance-name: "region-instance-group-manager-example"
 *         description: "Region instance group manager example"
 *         instance-template: $(google::compute-instance-template instance-group-template-example)
 *         target-size: 1
 *         region: "us-central"
 *     end
 */
@Type("compute-region-instance-group-manager")
public class RegionInstanceGroupManagerResource extends AbstractInstanceGroupManagerResource {

    private ComputeDistributionPolicy distributionPolicy;

    private String region;

    /**
     * Policy specifying intended distribution of instances in regional managed instance group.
     *
     * @subresource gyro.google.compute.ComputeDistributionPolicy
     */
    public ComputeDistributionPolicy getDistributionPolicy() {
        return distributionPolicy;
    }

    public void setDistributionPolicy(ComputeDistributionPolicy distributionPolicy) {
        this.distributionPolicy = distributionPolicy;
    }

    /**
     * The region where the managed instance group resides.
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
    public boolean doRefresh() throws Exception {
        Compute client = createClient(Compute.class);
        copyFrom(client.regionInstanceGroupManagers().get(getProjectId(), getRegion(), getName()).execute());
        return true;
    }

    @Override
    public void doCreate(GyroUI ui, State state) throws Exception {
        InstanceGroupManager instanceGroupManager = createInstanceGroupManager();
        Optional.ofNullable(getDistributionPolicy())
            .map(ComputeDistributionPolicy::copyTo)
            .ifPresent(instanceGroupManager::setDistributionPolicy);
        Compute client = createClient(Compute.class);
        Operation operation = client.regionInstanceGroupManagers()
            .insert(getProjectId(), getRegion(), instanceGroupManager)
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
        Operation operation = client.regionInstanceGroupManagers()
            .delete(getProjectId(), getRegion(), getName())
            .execute();
        waitForCompletion(client, operation);
    }

    @Override
    public void copyFrom(InstanceGroupManager model) {
        // DO NOT update the value from the provider as it's a full url.
        //        setRegion(model.getRegion());
        super.copyFrom(model);

        setDistributionPolicy(Optional.ofNullable(model.getDistributionPolicy())
            .map(e -> {
                ComputeDistributionPolicy computeDistributionPolicy = newSubresource(
                    ComputeDistributionPolicy.class);
                computeDistributionPolicy.copyFrom(e);
                return computeDistributionPolicy;
            })
            .orElse(null));
    }
}
