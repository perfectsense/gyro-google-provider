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

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import com.google.api.client.googleapis.json.GoogleJsonError;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.compute.Compute;
import com.google.api.services.compute.model.AttachedDisk;
import com.google.api.services.compute.model.Instance;
import gyro.core.GyroUI;
import gyro.core.Type;
import gyro.core.Wait;
import gyro.core.resource.Resource;
import gyro.core.resource.Updatable;
import gyro.core.scope.State;
import gyro.core.validation.Required;
import gyro.google.Copyable;

/**
 * Creates a network.
 *
 * Example
 * -------
 *
 * .. code-block:: gyro
 *
 *      google::instance-attached-disk gyro-disk-1
 *          instance: $(google::instance gyro-dev-1)
 *          attached-disk
 *              auto-delete: false
 *              source: $(google::compute-disk instance-disk-example)
 *          end
 *      end
 */
@Type("instance-attached-disk")
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
    public void doUpdate(
        GyroUI ui, State state, Resource current, Set<String> changedFieldNames) throws Exception {
        Compute compute = createClient(Compute.class);
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
                waitForCompletion(
                    compute,
                    compute.instances()
                        .setDiskAutoDelete(
                            getProjectId(),
                            getInstance().getZone(),
                            getInstance().getName(),
                            Boolean.TRUE.equals(getAttachedDisk().getAutoDelete()),
                            currentResource.getAttachedDisk().getDeviceName())
                        .execute());
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
        Compute compute = createClient(Compute.class);
        AtomicReference<Instance> instanceResult = new AtomicReference<>();
        String attachedDiskSourceSelfLink = formatResource(getProjectId(), attachedDisk.getSource().getSelfLink());

        Wait.atMost(30, TimeUnit.SECONDS)
            .prompt(false)
            .checkEvery(10, TimeUnit.SECONDS)
            .until(() -> {
                try {
                    Instance currentInstance = compute.instances()
                        .get(getProjectId(), getInstance().getZone(), getInstance().getName())
                        .execute();
                    instanceResult.set(currentInstance);
                    return true;
                } catch (GoogleJsonResponseException e) {
                    if (e.getDetails().getErrors().stream()
                        .map(GoogleJsonError.ErrorInfo::getReason)
                        .anyMatch("resourceNotReady"::equals)) {
                        return false;
                    }

                    throw e;
                }
            });

        List<AttachedDisk> disks = instanceResult.get().getDisks();

        if (disks != null) {
            return disks.stream()
                .filter(disk -> formatResource(getProjectId(), disk.getSource()).equals(attachedDiskSourceSelfLink))
                .findFirst()
                .orElse(null);
        }

        return null;
    }

    private void detachDisk(InstanceAttachedDiskResource resource) throws Exception {
        Compute compute = createClient(Compute.class);
        waitForCompletion(
            compute,
            compute.instances()
                .detachDisk(
                    resource.getProjectId(),
                    resource.getInstance().getZone(),
                    resource.getInstance().getName(),
                    resource.getAttachedDisk().getDeviceName())
                .execute());
    }

    private void attachDisk() throws Exception {
        Compute compute = createClient(Compute.class);
        waitForCompletion(
            compute,
            compute.instances()
                .attachDisk(
                    getProjectId(),
                    getInstance().getZone(),
                    getInstance().getName(),
                    getAttachedDisk().copyTo())
                .execute());
    }
}
