/*
 * Copyright 2019, Perfect Sense, Inc.
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
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import com.google.api.gax.rpc.InvalidArgumentException;
import com.google.api.gax.rpc.NotFoundException;
import com.google.cloud.compute.v1.AttachDiskInstanceRequest;
import com.google.cloud.compute.v1.AttachedDisk;
import com.google.cloud.compute.v1.DetachDiskInstanceRequest;
import com.google.cloud.compute.v1.Instance;
import com.google.cloud.compute.v1.InstancesClient;
import com.google.cloud.compute.v1.Operation;
import com.google.cloud.compute.v1.SetDiskAutoDeleteInstanceRequest;
import gyro.core.GyroUI;
import gyro.core.Type;
import gyro.core.Wait;
import gyro.core.resource.Resource;
import gyro.core.resource.Updatable;
import gyro.core.scope.State;
import gyro.core.validation.Required;
import gyro.google.Copyable;

/**
 * Attaches a disk to an instance.
 *
 * Example
 * -------
 *
 * .. code-block:: gyro
 *
 *      google::compute-instance-attached-disk gyro-disk-1
 *          instance: $(google::compute-instance gyro-dev-1)
 *          attached-disk
 *              auto-delete: false
 *              source: $(google::compute-disk instance-disk-example)
 *          end
 *      end
 */
@Type("compute-instance-attached-disk")
public class InstanceAttachedDiskResource extends ComputeResource implements Copyable<AttachedDisk> {

    private InstanceResource instance;
    private InstanceAttachedDisk attachedDisk;

    /**
     * Gyro instance resource.
     */
    @Required
    @Updatable
    public InstanceResource getInstance() {
        return instance;
    }

    public void setInstance(InstanceResource instance) {
        this.instance = instance;
    }

    /**
     * Configuration for attached disk.
     *
     * @subresource gyro.google.compute.InstanceAttachedDisk
     */
    @Required
    @Updatable
    public InstanceAttachedDisk getAttachedDisk() {
        return attachedDisk;
    }

    public void setAttachedDisk(InstanceAttachedDisk attachedDisk) {
        this.attachedDisk = attachedDisk;
    }

    @Override
    protected boolean doRefresh() throws Exception {
        AttachedDisk currentAttachedDisk = currentAttachedDisk();

        if (currentAttachedDisk == null) {
            return false;
        }

        getAttachedDisk().copyFrom(currentAttachedDisk);

        return true;
    }

    @Override
    protected void doCreate(GyroUI ui, State state) throws Exception {
        attachDisk();

        refresh();
    }

    @Override
    public void doUpdate(GyroUI ui, State state, Resource current, Set<String> changedFieldNames) throws Exception {
        try (InstancesClient client = createClient(InstancesClient.class)) {
            InstanceAttachedDiskResource currentResource = (InstanceAttachedDiskResource) current;

            if (changedFieldNames.contains("instance")) {
                detachDisk(currentResource);
                attachDisk();

            } else if (changedFieldNames.contains("attached-disk")) {
                if (!currentResource.getAttachedDisk().getSource().equals(getAttachedDisk().getSource())) {
                    attachDisk();
                    detachDisk(currentResource);

                    // Since current has now changed reflect in currentResource for any future calls. This is mainly
                    // to get the correct new deviceName.
                    AttachedDisk currentAttachedDisk = currentAttachedDisk();
                    currentResource.copyFrom(currentAttachedDisk);
                }

                if (currentResource.getAttachedDisk().getAutoDelete() != getAttachedDisk().getAutoDelete()) {
                    Operation operation = client.setDiskAutoDeleteOperationCallable()
                        .call(
                            SetDiskAutoDeleteInstanceRequest.newBuilder()
                                .setProject(getProjectId())
                                .setInstance(getInstance().getName())
                                .setDeviceName(currentResource.getAttachedDisk().getDeviceName())
                                .setAutoDelete(Boolean.TRUE.equals(getAttachedDisk().getAutoDelete()))
                                .build());

                    waitForCompletion(operation);
                }
            }
        }

        refresh();
    }

    @Override
    public void doDelete(GyroUI ui, State state) throws Exception {
        detachDisk(this);
    }

    @Override
    public void copyFrom(AttachedDisk model) {
        InstanceAttachedDisk instanceAttachedDisk = newSubresource(InstanceAttachedDisk.class);
        instanceAttachedDisk.copyFrom(model);
        setAttachedDisk(instanceAttachedDisk);
    }

    private AttachedDisk currentAttachedDisk() {
        try (InstancesClient client = createClient(InstancesClient.class)) {
            AtomicReference<Instance> instanceResult = new AtomicReference<>();
            String attachedDiskSourceSelfLink = formatResource(
                getProjectId(), getAttachedDisk().getSource().getSelfLink());

            Wait.atMost(30, TimeUnit.SECONDS)
                .prompt(false)
                .checkEvery(10, TimeUnit.SECONDS)
                .until(() -> {
                    try {
                        Instance currentInstance = client
                            .get(getProjectId(), getInstance().getZone(), getInstance().getName());
                        instanceResult.set(currentInstance);

                        return true;

                    } catch (NotFoundException | InvalidArgumentException e) {
                        return false;
                    }
                });

            return instanceResult.get().getDisksList().stream()
                .filter(disk -> formatResource(getProjectId(), disk.getSource()).equals(attachedDiskSourceSelfLink))
                .findFirst()
                .orElse(null);
        }
    }

    private void detachDisk(InstanceAttachedDiskResource resource) throws Exception {
        try (InstancesClient client = createClient(InstancesClient.class)) {
            Operation operation = client.detachDiskCallable()
                .call(
                    DetachDiskInstanceRequest.newBuilder()
                        .setProject(getProjectId())
                        .setInstance(getInstance().getName())
                        .setZone(getInstance().getZone())
                        .setDeviceName(resource.getAttachedDisk().getDeviceName())
                        .build());

            waitForCompletion(operation);
        }
    }

    private void attachDisk() throws Exception {
        try (InstancesClient client = createClient(InstancesClient.class)) {
            Operation operation = client.attachDiskOperationCallable()
                .call(
                    AttachDiskInstanceRequest.newBuilder()
                        .setProject(getProjectId())
                        .setInstance(getInstance().getName())
                        .setZone(getInstance().getZone())
                        .setAttachedDiskResource(getAttachedDisk().copyTo())
                        .build());

            waitForCompletion(operation);
        }
    }
}
