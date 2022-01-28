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
import com.google.api.gax.rpc.InvalidArgumentException;
import com.google.api.gax.rpc.NotFoundException;
import com.google.cloud.compute.v1.DeleteRegionInstanceGroupManagerRequest;
import com.google.cloud.compute.v1.GetRegionInstanceGroupManagerRequest;
import com.google.cloud.compute.v1.InsertRegionInstanceGroupManagerRequest;
import com.google.cloud.compute.v1.Instance;
import com.google.cloud.compute.v1.InstanceGroupManager;
import com.google.cloud.compute.v1.InstancesClient;
import com.google.cloud.compute.v1.ManagedInstance;
import com.google.cloud.compute.v1.Operation;
import com.google.cloud.compute.v1.PatchRegionInstanceGroupManagerRequest;
import com.google.cloud.compute.v1.RegionInstanceGroupManagersClient;
import com.google.cloud.compute.v1.RegionInstanceGroupManagersSetTargetPoolsRequest;
import com.google.cloud.compute.v1.RegionInstanceGroupManagersSetTemplateRequest;
import com.google.cloud.compute.v1.SetInstanceTemplateRegionInstanceGroupManagerRequest;
import com.google.cloud.compute.v1.SetTargetPoolsRegionInstanceGroupManagerRequest;
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
        try (RegionInstanceGroupManagersClient client = createClient(RegionInstanceGroupManagersClient.class)) {
            InstanceGroupManager instanceGroupManager = getRegionInstanceGroupManager(client);

            if (instanceGroupManager == null) {
                return false;
            }

            copyFrom(instanceGroupManager);

            return true;
        }
    }

    @Override
    protected void doDelete(GyroUI ui, State state) throws Exception {
        try (RegionInstanceGroupManagersClient client = createClient(RegionInstanceGroupManagersClient.class)) {
            Operation operation = client.deleteOperationCallable().call(
                DeleteRegionInstanceGroupManagerRequest.newBuilder()
                .setProject(getProjectId())
                .setRegion(getRegion())
                .setInstanceGroupManager(getName())
                .build());

            waitForCompletion(operation);
        }
    }

    @Override
    void insert(InstanceGroupManager instanceGroupManager) {
        try (RegionInstanceGroupManagersClient client = createClient(RegionInstanceGroupManagersClient.class)) {
            InstanceGroupManager.Builder builder = instanceGroupManager.toBuilder();

            if (getDistributionPolicy() != null) {
                builder.setDistributionPolicy(getDistributionPolicy().copyTo());
            }

            Operation operation = client.insertCallable().call(InsertRegionInstanceGroupManagerRequest.newBuilder()
                .setProject(getProjectId())
                .setRegion(getRegion())
                .setInstanceGroupManagerResource(builder)
                .build());

            waitForCompletion(operation);
        }
    }

    @Override
    void patch(InstanceGroupManager instanceGroupManager) throws Exception {
        try (RegionInstanceGroupManagersClient client = createClient(RegionInstanceGroupManagersClient.class)) {
            Operation operation = client.patchCallable().call(PatchRegionInstanceGroupManagerRequest.newBuilder()
                .setProject(getProjectId())
                .setRegion(getRegion())
                .setInstanceGroupManagerResource(instanceGroupManager)
                .build());

            waitForCompletion(operation);
        }
    }

    @Override
    void setInstanceTemplate() {
        try (RegionInstanceGroupManagersClient client = createClient(RegionInstanceGroupManagersClient.class)) {
            RegionInstanceGroupManagersSetTemplateRequest.Builder builder = RegionInstanceGroupManagersSetTemplateRequest
                .newBuilder();
            InstanceTemplateResource instanceTemplate = getInstanceTemplate();
            builder.setInstanceTemplate(instanceTemplate == null
                ? Data.nullOf(String.class)
                : instanceTemplate.getSelfLink());

            Operation operation = client.setInstanceTemplateOperationCallable().call(
                SetInstanceTemplateRegionInstanceGroupManagerRequest.newBuilder()
                    .setProject(getProjectId())
                    .setRegion(getRegion())
                    .setInstanceGroupManager(getName())
                    .setRegionInstanceGroupManagersSetTemplateRequestResource(builder)
                    .build());

            waitForCompletion(operation);
        }
    }

    @Override
    void setTargetPools() {
        try (RegionInstanceGroupManagersClient client = createClient(RegionInstanceGroupManagersClient.class)) {
            RegionInstanceGroupManagersSetTargetPoolsRequest.Builder builder = RegionInstanceGroupManagersSetTargetPoolsRequest
                .newBuilder();
            List<TargetPoolResource> targetPoolResources = getTargetPools();
            builder.addAllTargetPools(targetPoolResources.stream().map(TargetPoolResource::getSelfLink)
                .collect(Collectors.toList()));

            Operation operation = client.setTargetPoolsOperationCallable().call(
                SetTargetPoolsRegionInstanceGroupManagerRequest.newBuilder()
                    .setProject(getProjectId())
                    .setRegion(getRegion())
                    .setInstanceGroupManager(getName())
                    .setRegionInstanceGroupManagersSetTargetPoolsRequestResource(builder)
                    .build());

            waitForCompletion(operation);
        }
    }

    @Override
    public List<GyroInstance> getInstances() {
        List<GyroInstance> instances = new ArrayList<>();

        try (RegionInstanceGroupManagersClient client = createClient(RegionInstanceGroupManagersClient.class);
            InstancesClient instancesClient = createClient(InstancesClient.class)
        ) {
            RegionInstanceGroupManagersClient.ListManagedInstancesPagedResponse response = client
                .listManagedInstances(getProjectId(), getRegion(), getName());

            List<String> instanceNameList = response.getPage().getResponse().getManagedInstancesList().stream()
                .filter(o -> o.getCurrentAction().equals(ManagedInstance.CurrentAction.NONE))
                .map(ManagedInstance::getInstance)
                .collect(Collectors.toList());

            for (String instanceName : instanceNameList) {
                String zone = StringUtils.substringBetween(instanceName, "/zones/", "/instances/");
                Instance instance = getInstance(instancesClient,
                    instanceName.substring(instanceName.lastIndexOf("/") + 1), zone);

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

    private InstanceGroupManager getRegionInstanceGroupManager(RegionInstanceGroupManagersClient client) {
        InstanceGroupManager instanceGroupManager = null;

        try {
            instanceGroupManager = client.get(GetRegionInstanceGroupManagerRequest.newBuilder()
                .setProject(getProjectId())
                .setRegion(getRegion())
                .setInstanceGroupManager(getName())
                .build());

        } catch (NotFoundException | InvalidArgumentException ex) {
            // ignore
        }

        return instanceGroupManager;
    }
}
