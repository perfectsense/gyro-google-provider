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
import java.util.stream.Collectors;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.util.Data;
import com.google.api.gax.rpc.InvalidArgumentException;
import com.google.api.gax.rpc.NotFoundException;
import com.google.cloud.compute.v1.DeleteInstanceGroupManagerRequest;
import com.google.cloud.compute.v1.GetInstanceGroupManagerRequest;
import com.google.cloud.compute.v1.InsertInstanceGroupManagerRequest;
import com.google.cloud.compute.v1.Instance;
import com.google.cloud.compute.v1.InstanceGroupManager;
import com.google.cloud.compute.v1.InstanceGroupManagersClient;
import com.google.cloud.compute.v1.InstanceGroupManagersSetInstanceTemplateRequest;
import com.google.cloud.compute.v1.InstanceGroupManagersSetTargetPoolsRequest;
import com.google.cloud.compute.v1.InstancesClient;
import com.google.cloud.compute.v1.ManagedInstance;
import com.google.cloud.compute.v1.Operation;
import com.google.cloud.compute.v1.PatchInstanceGroupManagerRequest;
import com.google.cloud.compute.v1.SetInstanceTemplateInstanceGroupManagerRequest;
import com.google.cloud.compute.v1.SetTargetPoolsInstanceGroupManagerRequest;
import gyro.core.GyroException;
import gyro.core.GyroInstance;
import gyro.core.GyroInstances;
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
public class InstanceGroupManagerResource extends AbstractInstanceGroupManagerResource implements GyroInstances {

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
        try (InstanceGroupManagersClient client = createClient(InstanceGroupManagersClient.class)) {
            InstanceGroupManager instanceGroupManager = getInstanceGroupManager(client);

            if (instanceGroupManager == null) {
                return false;
            }

            copyFrom(instanceGroupManager);

            return true;
        }
    }

    @Override
    protected void doDelete(GyroUI ui, State state) throws Exception {
        try (InstanceGroupManagersClient client = createClient(InstanceGroupManagersClient.class)) {
            Operation operation = client.deleteCallable().call(DeleteInstanceGroupManagerRequest.newBuilder()
                .setProject(getProjectId())
                .setZone(getZone())
                .setInstanceGroupManager(getName())
                .build());

            waitForCompletion(operation);
        }
    }

    @Override
    void insert(InstanceGroupManager instanceGroupManager) {
        try (InstanceGroupManagersClient client = createClient(InstanceGroupManagersClient.class)) {
            Operation operation = client.insertCallable().call(InsertInstanceGroupManagerRequest.newBuilder()
                .setProject(getProjectId())
                .setZone(getZone())
                .setInstanceGroupManagerResource(instanceGroupManager)
                .build());
            waitForCompletion(operation);
        }
    }

    @Override
    void patch(InstanceGroupManager instanceGroupManager) {
        try (InstanceGroupManagersClient client = createClient(InstanceGroupManagersClient.class)) {
            Operation operation = client.patchCallable().call(PatchInstanceGroupManagerRequest.newBuilder()
                .setProject(getProjectId())
                .setZone(getZone())
                .setInstanceGroupManagerResource(instanceGroupManager)
                .build());
            waitForCompletion(operation);
        }
    }

    @Override
    void setInstanceTemplate() {
        InstanceGroupManagersSetInstanceTemplateRequest.Builder builder = InstanceGroupManagersSetInstanceTemplateRequest
            .newBuilder();
        InstanceTemplateResource instanceTemplate = getInstanceTemplate();
        builder.setInstanceTemplate(instanceTemplate == null ? Data.nullOf(String.class)
            : instanceTemplate.getSelfLink());

        try (InstanceGroupManagersClient client = createClient(InstanceGroupManagersClient.class)) {
            Operation operation = client.setInstanceTemplateCallable().call(
                SetInstanceTemplateInstanceGroupManagerRequest.newBuilder()
                    .setProject(getProjectId())
                    .setZone(getZone())
                    .setInstanceGroupManagersSetInstanceTemplateRequestResource(builder)
                    .build());

            waitForCompletion(operation);
        }
    }

    @Override
    void setTargetPools() {
        InstanceGroupManagersSetTargetPoolsRequest.Builder builder = InstanceGroupManagersSetTargetPoolsRequest.newBuilder();
        builder.addAllTargetPools(getTargetPools().stream()
            .map(TargetPoolResource::getSelfLink).collect(Collectors.toList()));

        try (InstanceGroupManagersClient client = createClient(InstanceGroupManagersClient.class)) {
            Operation operation = client.setTargetPoolsCallable().call(SetTargetPoolsInstanceGroupManagerRequest.newBuilder()
                .setProject(getProjectId())
                .setZone(getZone())
                .setInstanceGroupManager(getName())
                .setInstanceGroupManagersSetTargetPoolsRequestResource(builder)
                .build());

            waitForCompletion(operation);
        }
    }

    @Override
    public List<GyroInstance> getInstances() {
        List<GyroInstance> instances = new ArrayList<>();

        try (InstanceGroupManagersClient client = createClient(InstanceGroupManagersClient.class);
            InstancesClient instancesClient = createClient(InstancesClient.class)) {

            InstanceGroupManagersClient.ListManagedInstancesPagedResponse response = client
                .listManagedInstances(getProjectId(), getZone(), getName());

            List<String> instanceNameList = response.getPage().getResponse().getManagedInstancesList().stream()
                .filter(o -> o.getCurrentAction().equals("NONE"))
                .map(ManagedInstance::getInstance)
                .collect(Collectors.toList());

            for (String instanceName : instanceNameList) {
                Instance instance = getInstance(instancesClient,
                    instanceName.substring(instanceName.lastIndexOf("/") + 1), getZone());

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

    private InstanceGroupManager getInstanceGroupManager(InstanceGroupManagersClient client) {
        InstanceGroupManager instanceGroupManager = null;

        try {
            instanceGroupManager = client.get(GetInstanceGroupManagerRequest.newBuilder()
                .setProject(getProjectId())
                .setZone(getZone())
                .setInstanceGroupManager(getName())
                .build());

        } catch (NotFoundException | InvalidArgumentException ex) {
            // ignore
        }

        return instanceGroupManager;
    }
}
