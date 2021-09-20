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
import java.util.stream.Collectors;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.util.Data;
import com.google.api.services.compute.Compute;
import com.google.cloud.compute.v1.Instance;
import com.google.cloud.compute.v1.InstanceGroupManager;
import com.google.cloud.compute.v1.ManagedInstance;
import com.google.cloud.compute.v1.Operation;
import com.google.cloud.compute.v1.RegionInstanceGroupManagersListInstancesResponse;
import com.google.cloud.compute.v1.RegionInstanceGroupManagersSetTargetPoolsRequest;
import com.google.cloud.compute.v1.RegionInstanceGroupManagersSetTemplateRequest;
import gyro.core.GyroException;
import gyro.core.GyroInstance;
import gyro.core.GyroInstances;
import gyro.core.GyroUI;
import gyro.core.Type;
import gyro.core.scope.State;
import gyro.core.validation.Required;
import gyro.google.util.Utils;
import org.apache.commons.lang3.StringUtils;

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
 *         region: "us-central1"
 *     end
 */
@Type("compute-region-instance-group-manager")
public class RegionInstanceGroupManagerResource extends AbstractInstanceGroupManagerResource implements GyroInstances {

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
    @Required
    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    @Override
    public void copyFrom(InstanceGroupManager model) {
        super.copyFrom(model);

        setDistributionPolicy(Optional.ofNullable(model.getDistributionPolicy())
            .map(e -> {
                ComputeDistributionPolicy computeDistributionPolicy = newSubresource(
                    ComputeDistributionPolicy.class);
                computeDistributionPolicy.copyFrom(e);
                return computeDistributionPolicy;
            })
            .orElse(null));
        setRegion(Utils.extractName(model.getRegion()));
    }

    @Override
    protected boolean doRefresh() throws Exception {
        Compute client = createComputeClient();
        copyFrom(client.regionInstanceGroupManagers().get(getProjectId(), getRegion(), getName()).execute());
        return true;
    }

    @Override
    protected void doDelete(GyroUI ui, State state) throws Exception {
        Compute client = createComputeClient();
        Operation operation = client.regionInstanceGroupManagers()
            .delete(getProjectId(), getRegion(), getName())
            .execute();
        waitForCompletion(client, operation);
    }

    @Override
    void insert(InstanceGroupManager instanceGroupManager) throws Exception {
        Optional.ofNullable(getDistributionPolicy())
            .map(ComputeDistributionPolicy::copyTo)
            .ifPresent(instanceGroupManager::setDistributionPolicy);

        Compute client = createComputeClient();
        Operation operation = client.regionInstanceGroupManagers()
            .insert(getProjectId(), getRegion(), instanceGroupManager)
            .execute();
        waitForCompletion(client, operation);
    }

    @Override
    void patch(InstanceGroupManager instanceGroupManager) throws Exception {
        Compute client = createComputeClient();
        Operation operation = client.regionInstanceGroupManagers()
            .patch(getProjectId(), getRegion(), getName(), instanceGroupManager)
            .execute();
        waitForCompletion(client, operation);
    }

    @Override
    void setInstanceTemplate() throws Exception {
        RegionInstanceGroupManagersSetTemplateRequest request = new RegionInstanceGroupManagersSetTemplateRequest();
        InstanceTemplateResource instanceTemplate = getInstanceTemplate();
        request.setInstanceTemplate(instanceTemplate == null
            ? Data.nullOf(String.class)
            : instanceTemplate.getSelfLink());

        Compute client = createComputeClient();
        Operation operation = client.regionInstanceGroupManagers()
            .setInstanceTemplate(getProjectId(), getRegion(), getName(), request)
            .execute();
        waitForCompletion(client, operation);
    }

    @Override
    void setTargetPools() throws Exception {
        RegionInstanceGroupManagersSetTargetPoolsRequest request = new RegionInstanceGroupManagersSetTargetPoolsRequest();
        List<TargetPoolResource> targetPoolResources = getTargetPools();
        request.setTargetPools(targetPoolResources.isEmpty()
            ? Data.nullOf(ArrayList.class)
            : targetPoolResources.stream()
                .map(TargetPoolResource::getSelfLink)
                .collect(Collectors.toList()));

        Compute client = createComputeClient();
        Operation operation = client.regionInstanceGroupManagers()
            .setTargetPools(getProjectId(), getRegion(), getName(), request)
            .execute();
        waitForCompletion(client, operation);
    }

    @Override
    public List<GyroInstance> getInstances() {
        List<GyroInstance> instances = new ArrayList<>();

        Compute client = createComputeClient();

        try {
            RegionInstanceGroupManagersListInstancesResponse response = client.regionInstanceGroupManagers()
                .listManagedInstances(getProjectId(), getRegion(), getName())
                .execute();

            List<String> instanceNameList = response.getManagedInstances().stream()
                .filter(o -> o.getCurrentAction().equals("NONE"))
                .map(ManagedInstance::getInstance)
                .collect(Collectors.toList());

            for (String instanceName : instanceNameList) {
                String zone = StringUtils.substringBetween(instanceName, "/zones/", "/instances/");
                Instance instance = getInstance(client, instanceName.substring(instanceName.lastIndexOf("/") + 1), zone);

                if (instance != null) {
                    InstanceResource resource = newSubresource(InstanceResource.class);
                    resource.copyFrom(instance);
                    instances.add(resource);
                }
            }
        } catch (GoogleJsonResponseException je) {
            throw new GyroException(formatGoogleExceptionMessage(je));
        } catch (Exception ex) {
            throw new GyroException(ex);
        }

        return instances;
    }
}
