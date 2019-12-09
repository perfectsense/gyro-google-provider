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

import com.google.api.services.compute.Compute;
import com.google.api.services.compute.model.Operation;
import com.google.api.services.compute.model.Snapshot;
import com.google.cloud.compute.v1.ProjectRegionDiskName;
import gyro.core.GyroException;
import gyro.core.GyroUI;
import gyro.core.Type;
import gyro.core.scope.State;
import gyro.core.validation.Required;

/**
 * Creates a snapshot of a regional disk.
 *
 * Example
 * -------
 *
 * .. code-block:: gyro
 *
 *     google::compute-region-snapshot region-snapshot-example
 *         name: "region-snapshot-example"
 *         description: "region-snapshot-example-desc"
 *         region-source-disk: $(google::compute-region-disk region-disk-example)
 *         labels: {
 *             label-key: 'label-value'
 *         }
 *         source-disk-encryption-key
 *             raw-key: "disk-256-bit-raw-key"
 *         end
 *         storage-locations: [
 *             "us"
 *         ]
 *     end
 */
@Type("compute-region-snapshot")
public class RegionSnapshotResource extends AbstractSnapshotResource {

    private RegionDiskResource regionSourceDisk;

    /**
     * The regional source disk used to create this snapshot. (Required)
     */
    @Required
    public RegionDiskResource getRegionSourceDisk() {
        return regionSourceDisk;
    }

    public void setRegionSourceDisk(RegionDiskResource regionSourceDisk) {
        this.regionSourceDisk = regionSourceDisk;
    }

    @Override
    public void copyFrom(Snapshot snapshot) {
        super.copyFrom(snapshot);

        setRegionSourceDisk(findById(RegionDiskResource.class, snapshot.getSourceDisk()));
    }

    @Override
    public void doCreate(GyroUI ui, State state) throws Exception {
        Compute client = createComputeClient();

        String selfLink = getRegionSourceDisk().getSelfLink();
        Snapshot snapshot = toSnapshot();
        snapshot.setSourceDisk(selfLink);

        ProjectRegionDiskName regionDisk = parseRegionDisk(getProjectId(), selfLink);

        if (regionDisk != null) {
            Compute.RegionDisks.CreateSnapshot insert = client.regionDisks()
                .createSnapshot(getProjectId(), regionDisk.getRegion(), regionDisk.getDisk(), snapshot);
            Operation operation = insert.execute();
            Operation.Error error = waitForCompletion(client, operation);
            if (error != null) {
                throw new GyroException(error.toPrettyString());
            }

            refresh();
        } else {
            throw new GyroException(String.format("Unable to parse %s disk", selfLink));
        }
    }

    static ProjectRegionDiskName parseRegionDisk(String projectId, String selfLink) {
        String parseDiskName = ComputeUtils.formatResource(projectId, selfLink);
        if (ProjectRegionDiskName.isParsableFrom(parseDiskName)) {
            return ProjectRegionDiskName.parse(parseDiskName);
        }
        return null;
    }
}
