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

import com.google.api.services.compute.Compute;
import com.google.api.services.compute.model.Disk;
import com.google.api.services.compute.model.DisksResizeRequest;
import com.google.api.services.compute.model.Operation;
import com.google.api.services.compute.model.ZoneSetLabelsRequest;
import com.google.cloud.compute.v1.ProjectZoneDiskName;
import gyro.core.GyroException;
import gyro.core.GyroUI;
import gyro.core.Type;
import gyro.core.resource.Resource;
import gyro.core.scope.State;
import gyro.core.validation.Required;

/**
 * Creates a zonal disk.
 *
 * Example
 * -------
 *
 * .. code-block:: gyro
 *
 *     google::compute-disk disk-example
 *         name: "disk-example"
 *         description: "disk-example-desc"
 *         zone: "us-central1-a"
 *         size-gb: 32
 *         type: "pd-standard"
 *         labels: {
 *             label-key: 'label-value'
 *         }
 *         physical-block-size-bytes: 4096
 *     end
 */
@Type("compute-disk")
public class DiskResource extends AbstractDiskResource {

    private String zone;

    /**
     * The zone where the disk resides. (Required)
     */
    @Required
    public String getZone() {
        return zone;
    }

    public void setZone(String zone) {
        this.zone = zone != null ? zone.substring(zone.lastIndexOf("/") + 1) : null;
    }

    @Override
    public void copyFrom(Disk disk) {
        super.copyFrom(disk);

        setZone(disk.getZone());
    }

    @Override
    public boolean doRefresh() throws Exception {
        Compute client = createComputeClient();

        Disk disk = client.disks().get(getProjectId(), getZone(), getName()).execute();
        copyFrom(disk);

        return true;
    }

    @Override
    public void doCreate(GyroUI ui, State state) throws Exception {
        Compute client = createComputeClient();

        Disk disk = toDisk();
        disk.setZone(getZone());

        Compute.Disks.Insert insert = client.disks().insert(getProjectId(), getZone(), disk);
        Operation operation = insert.execute();
        Operation.Error error = waitForCompletion(client, operation);
        if (error != null) {
            throw new GyroException(error.toPrettyString());
        }

        refresh();
    }

    @Override
    public void doUpdate(GyroUI ui, State state, Resource current, Set<String> changedFieldNames) throws Exception {
        Compute client = createComputeClient();

        if (changedFieldNames.contains("size-gb")) {
            saveSizeGb(client, (DiskResource) current);
        }

        if (changedFieldNames.contains("labels")) {
            saveLabels(client);
        }

        refresh();
    }

    @Override
    public void doDelete(GyroUI ui, State state) throws Exception {
        Compute compute = createComputeClient();

        compute.disks().delete(getProjectId(), getZone(), getName()).execute();
    }

    private void saveSizeGb(Compute client, DiskResource oldDiskResource) throws Exception {
        if (getSizeGb() < oldDiskResource.getSizeGb()) {
            throw new GyroException("Size of the disk cannot be decreased once set.");
        }

        DisksResizeRequest resizeRequest = new DisksResizeRequest();
        resizeRequest.setSizeGb(getSizeGb());
        client.disks().resize(getProjectId(), getZone(), getName(), resizeRequest).execute();
    }

    private void saveLabels(Compute client) throws Exception {
        ZoneSetLabelsRequest labelsRequest = new ZoneSetLabelsRequest();
        labelsRequest.setLabels(getLabels());
        labelsRequest.setLabelFingerprint(getLabelFingerprint());
        client.disks().setLabels(getProjectId(), getZone(), getName(), labelsRequest).execute();
    }

    static ProjectZoneDiskName parseDisk(String projectId, String selfLink) {
        String parseDiskName = formatResource(projectId, selfLink);
        if (ProjectZoneDiskName.isParsableFrom(parseDiskName)) {
            return ProjectZoneDiskName.parse(parseDiskName);
        }
        return null;
    }
}
