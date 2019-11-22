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
import com.google.api.services.compute.model.Disk;
import com.google.api.services.compute.model.Operation;
import com.google.api.services.compute.model.RegionDisksResizeRequest;
import com.google.api.services.compute.model.RegionSetLabelsRequest;
import gyro.core.GyroException;
import gyro.core.GyroUI;
import gyro.core.Type;
import gyro.core.resource.Resource;
import gyro.core.scope.State;
import gyro.core.validation.Required;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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
        this.region = region;
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
        this.replicaZones = replicaZones;
    }

    @Override
    public void copyMore(Disk disk) {
        setRegion(disk.getRegion().substring(disk.getRegion().lastIndexOf("/") + 1));
        setReplicaZones(disk.getReplicaZones());
    }

    @Override
    public void doCreate(GyroUI ui, State state, Disk disk) {
        Compute client = createComputeClient();

        disk.setRegion(getRegion());
        disk.setReplicaZones(getReplicaZones());

        try {
            Compute.RegionDisks.Insert insert = client.regionDisks().insert(getProjectId(), getRegion(), disk);
            Operation operation = insert.execute();
            Operation.Error error = waitForCompletion(client, operation);
            if (error != null) {
                throw new GyroException(error.toPrettyString());
            }

            refresh();
        } catch (Exception ex) {
            throw new GyroException(ex.getMessage(), ex.getCause());
        }
    }

    @Override
    public boolean refresh() {
        Compute client = createComputeClient();

        try {
            Disk disk = client.regionDisks().get(getProjectId(), getRegion(), getName()).execute();
            copyFrom(disk);

            return true;
        } catch (GoogleJsonResponseException je) {
            if (je.getDetails().getMessage().matches("The resource (.*) was not found")) {
                return false;
            } else {
                throw new GyroException(je.getDetails().getMessage());
            }
        } catch (IOException ex) {
            throw new GyroException(ex.getMessage(), ex.getCause());
        }
    }

    @Override
    public void delete(GyroUI ui, State state) {
        Compute compute = createComputeClient();

        try {
            compute.regionDisks().delete(getProjectId(), getRegion(), getName()).execute();
        } catch (IOException ex) {
            throw new GyroException(ex.getMessage(), ex.getCause());
        }
    }

    @Override
    public void update(GyroUI ui, State state, Resource current, Set<String> changedFieldNames) {
        Compute client = createComputeClient();

        if (changedFieldNames.contains("size-gb")) {
            saveSizeGb(client);
        }

        if (changedFieldNames.contains("labels")) {
            saveLabels(client);
        }

        if (changedFieldNames.contains("resource-policies")) {
            saveResourcePolicies(client, (RegionDiskResource) current);
        }

        refresh();
    }

    private void saveSizeGb(Compute client) {
        try {
            RegionDisksResizeRequest resizeRequest = new RegionDisksResizeRequest();
            resizeRequest.setSizeGb(getSizeGb());
            client.regionDisks().resize(getProjectId(), getRegion(), getName(), resizeRequest).execute();
        } catch (IOException ex) {
            throw new GyroException(ex.getMessage(), ex.getCause());
        }
    }

    private void saveLabels(Compute client) {
        try {
            RegionSetLabelsRequest labelsRequest = new RegionSetLabelsRequest();
            labelsRequest.setLabels(getLabels());
            labelsRequest.setLabelFingerprint(getLabelFingerprint());
            client.regionDisks().setLabels(getProjectId(), getRegion(), getName(), labelsRequest).execute();
        } catch (IOException ex) {
            throw new GyroException(ex.getMessage(), ex.getCause());
        }
    }

    private void saveResourcePolicies(Compute client, RegionDiskResource oldRegionDiskResource) {
    }
}
