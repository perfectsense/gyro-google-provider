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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.api.services.compute.Compute;
import com.google.api.services.compute.model.Disk;
import com.google.api.services.compute.model.Operation;
import com.google.api.services.compute.model.RegionDisksResizeRequest;
import com.google.api.services.compute.model.RegionSetLabelsRequest;
import com.google.cloud.compute.v1.ProjectRegionDiskName;
import com.google.cloud.compute.v1.ProjectZoneName;
import gyro.core.GyroException;
import gyro.core.GyroUI;
import gyro.core.Type;
import gyro.core.resource.Resource;
import gyro.core.scope.State;
import gyro.core.validation.Required;
import gyro.core.validation.ValidationError;

/**
 * Creates a regional disk.
 *
 * Example
 * -------
 *
 * .. code-block:: gyro
 *
 *     google::compute-region-disk region-disk-example
 *         name: "region-disk-example"
 *         description: "region-disk-example-desc"
 *         region: "us-central1"
 *
 *         replica-zones: [
 *             "us-central1-c",
 *             "us-central1-a"
 *         ]
 *
 *         size-gb: 32
 *         type: "pd-ssd"
 *         labels: {
 *             label-key: 'label-value'
 *         }
 *         physical-block-size-bytes: 16384
 *     end
 *
 * .. code-block:: gyro
 *
 *     google::compute-region-disk region-disk-snapshot-example
 *         name: "region-disk-snapshot-example"
 *         region: "us-central1"
 *
 *         replica-zones: [
 *             "us-central1-c",
 *             "us-central1-a"
 *         ]
 *
 *         source-snapshot: $(google::compute-snapshot region-snapshot-example)
 *
 *         source-snapshot-encryption-key
 *             kms-key-name: "my-kms-key-name"
 *         end
 *     end
 */
@Type("compute-region-disk")
public class RegionDiskResource extends AbstractDiskResource {

    private String region;
    private List<String> replicaZones;

    /**
     * The region where the disk resides. (Required)
     */
    @Required
    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region != null ? region.substring(region.lastIndexOf("/") + 1) : null;
    }

    /**
     * The zones where the disk should be replicated to. (Required)
     */
    @Required
    public List<String> getReplicaZones() {
        if (replicaZones == null) {
            replicaZones = new ArrayList<>();
        }
        return replicaZones;
    }

    public void setReplicaZones(List<String> replicaZones) {
        // Full URLs are required for replicaZones, so format the zone to a full URL so it is accepted
        this.replicaZones = replicaZones != null
            ? replicaZones.stream().map(zone -> toZoneUrl(getProjectId(), zone)).collect(Collectors.toList())
            : null;
    }

    @Override
    public void copyFrom(Disk disk) {
        super.copyFrom(disk);

        setRegion(disk.getRegion());
        setReplicaZones(disk.getReplicaZones());
    }

    @Override
    public boolean doRefresh() throws Exception {
        Compute client = createComputeClient();

        Disk disk = client.regionDisks().get(getProjectId(), getRegion(), getName()).execute();
        copyFrom(disk);

        return true;
    }

    @Override
    public void doCreate(GyroUI ui, State state) throws Exception {
        Compute client = createComputeClient();

        Disk disk = toDisk();
        disk.setRegion(getRegion());
        disk.setReplicaZones(getReplicaZones());

        Compute.RegionDisks.Insert insert = client.regionDisks().insert(getProjectId(), getRegion(), disk);
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
            saveSizeGb(client, (RegionDiskResource) current);
        }

        if (changedFieldNames.contains("labels")) {
            saveLabels(client);
        }

        refresh();
    }

    @Override
    public void doDelete(GyroUI ui, State state) throws Exception {
        Compute client = createComputeClient();

        Operation operation = client.regionDisks().delete(getProjectId(), getRegion(), getName()).execute();
        Operation.Error error = waitForCompletion(client, operation);
        if (error != null) {
            throw new GyroException(error.toPrettyString());
        }
    }

    @Override
    public List<ValidationError> validate() {
        List<ValidationError> errors = new ArrayList<>();

        if (getReplicaZones().size() != 2) {
            errors.add(new ValidationError(
                this,
                "replica-zones",
                "Disk requires exactly two replica zones."));
        }

        return errors;
    }

    private void saveSizeGb(Compute client, RegionDiskResource oldRegionDiskResource) throws Exception {
        if (getSizeGb() < oldRegionDiskResource.getSizeGb()) {
            throw new GyroException(String.format(
                "Size of the disk cannot be decreased once set. Current size %s.",
                oldRegionDiskResource.getSizeGb()));
        }

        RegionDisksResizeRequest resizeRequest = new RegionDisksResizeRequest();
        resizeRequest.setSizeGb(getSizeGb());
        Operation operation = client.regionDisks()
            .resize(getProjectId(), getRegion(), getName(), resizeRequest)
            .execute();
        Operation.Error error = waitForCompletion(client, operation);
        if (error != null) {
            throw new GyroException(error.toPrettyString());
        }
    }

    private void saveLabels(Compute client) throws Exception {
        RegionSetLabelsRequest labelsRequest = new RegionSetLabelsRequest();
        labelsRequest.setLabels(getLabels());
        labelsRequest.setLabelFingerprint(getLabelFingerprint());
        Operation operation = client.regionDisks()
            .setLabels(getProjectId(), getRegion(), getName(), labelsRequest)
            .execute();
        Operation.Error error = waitForCompletion(client, operation);
        if (error != null) {
            throw new GyroException(error.toPrettyString());
        }
    }

    static ProjectRegionDiskName parseRegionDisk(String projectId, String selfLink) {
        String parseDiskName = formatResource(projectId, selfLink);
        if (ProjectRegionDiskName.isParsableFrom(parseDiskName)) {
            return ProjectRegionDiskName.parse(parseDiskName);
        }
        return null;
    }

    static String toZoneUrl(String projectId, String zone) {
        String parseZone = formatResource(projectId, zone);
        if (ProjectZoneName.isParsableFrom(parseZone)) {
            return ProjectZoneName.parse(parseZone).toString();
        }
        return ProjectZoneName.format(projectId, zone);
    }
}