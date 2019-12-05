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

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.compute.Compute;
import com.google.api.services.compute.model.Operation;
import com.google.api.services.compute.model.Snapshot;
import com.google.cloud.compute.v1.ProjectZoneDiskName;
import gyro.core.GyroException;
import gyro.core.GyroUI;
import gyro.core.Type;
import gyro.core.scope.State;
import gyro.core.validation.Required;

/**
 * Creates a snapshot of a disk.
 *
 * Example
 * -------
 *
 * .. code-block:: gyro
 *
 *     google::compute-snapshot snapshot-example
 *         name: "snapshot-example"
 *         description: "snapshot-example-desc"
 *         source-disk: $(google::compute-disk disk-example)
 *         labels: {
 *             label-key: 'label-value'
 *         }
 *         source-disk-encryption-key
 *             raw-key: "disk-256-bit-raw-key"
 *         end
 *         storage-locations: [
 *             "us-west1"
 *         ]
 *     end
 */

@Type("compute-snapshot")
public class SnapshotResource extends AbstractSnapshotResource {

    private DiskResource sourceDisk;

    /**
     * The source disk used to create this snapshot. (Required)
     */
    @Required
    public DiskResource getSourceDisk() {
        return sourceDisk;
    }

    public void setSourceDisk(DiskResource sourceDisk) {
        this.sourceDisk = sourceDisk;
    }

    @Override
    public void copyFrom(Snapshot snapshot) {
        super.copyFrom(snapshot);

        setSourceDisk(findById(DiskResource.class, snapshot.getSourceDisk()));
    }

    @Override
    public void create(GyroUI ui, State state) {
        Compute client = createComputeClient();

        String selfLink = getSourceDisk().getSelfLink();
        Snapshot snapshot = toSnapshot();
        snapshot.setSourceDisk(selfLink);

        try {
            ProjectZoneDiskName zoneDisk = parseZoneDisk(getProjectId(), selfLink);

            if (zoneDisk != null) {
                Compute.Disks.CreateSnapshot insert =
                    client.disks().createSnapshot(getProjectId(), zoneDisk.getZone(), zoneDisk.getDisk(), snapshot);
                Operation operation = insert.execute();
                Operation.Error error = waitForCompletion(client, operation);
                if (error != null) {
                    throw new GyroException(error.toPrettyString());
                }

                refresh();
            } else {
                throw new GyroException(String.format("Unable to parse %s disk", selfLink));
            }
        } catch (GoogleJsonResponseException je) {
            throw new GyroException(je.getDetails().getMessage());
        } catch (Exception ex) {
            throw new GyroException(ex.getMessage(), ex.getCause());
        }
    }

    static ProjectZoneDiskName parseZoneDisk(String projectId, String selfLink) {
        String parseDiskName = ComputeUtils.formatResource(projectId, selfLink);
        if (ProjectZoneDiskName.isParsableFrom(parseDiskName)) {
            return ProjectZoneDiskName.parse(parseDiskName);
        }
        return null;
    }
}
