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
import com.google.api.services.compute.model.Operation;
import gyro.core.GyroException;
import gyro.core.GyroUI;
import gyro.core.Type;
import gyro.core.Wait;
import gyro.core.resource.Resource;
import gyro.core.scope.State;
import gyro.core.validation.Required;
import gyro.google.Copyable;

@Type("instance-attached-disk")
public class InstanceAttachedDiskResource extends ComputeResource implements Copyable<AttachedDisk> {

    private InstanceResource instance;
    private InstanceAttachedDisk attachedDisk;

    /**
     * TODO
     */
    @Required
    public InstanceResource getInstance() {
        return instance;
    }

    public void setInstance(InstanceResource instance) {
        this.instance = instance;
    }

    /**
     * TODO
     */
    @Required
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

        if (changedFieldNames.contains("instance")) {
            // TODO
        }

        if (changedFieldNames.contains("attached-disk")) {
            attachDisk();
        }

        refresh();
    }

    @Override
    public void doDelete(GyroUI ui, State state) throws Exception {
        Compute compute = createClient(Compute.class);
        Operation.Error error = waitForCompletion(
            compute,
            compute.instances()
                .detachDisk(getProjectId(),
                    getInstance().getZone(),
                    getInstance().getName(),
                    getAttachedDisk().getDeviceName())
                .execute());

        if (error != null) {
            throw new GyroException(error.toPrettyString());
        }
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
                .filter(disk -> disk.getSource().equals(attachedDisk.getSource()))
                .findFirst()
                .orElse(null);
        }

        return null;
    }

    private void attachDisk() throws Exception {
        Compute compute = createClient(Compute.class);

        Operation.Error error = waitForCompletion(
            compute,
            compute.instances()
                .attachDisk(getProjectId(),
                    getInstance().getZone(),
                    getInstance().getName(),
                    getAttachedDisk().copyTo())
                .execute());

        if (error != null) {
            throw new GyroException(error.toPrettyString());
        }
    }
}
