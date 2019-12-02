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
import com.google.api.services.compute.model.RegionDisksAddResourcePoliciesRequest;
import com.google.api.services.compute.model.RegionDisksRemoveResourcePoliciesRequest;
import com.google.api.services.compute.model.RegionDisksResizeRequest;
import com.google.api.services.compute.model.RegionSetLabelsRequest;
import gyro.core.GyroException;
import gyro.core.GyroUI;
import gyro.core.Type;
import gyro.core.resource.Resource;
import gyro.core.scope.State;
import gyro.core.validation.Required;
import gyro.core.validation.ValidationError;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
 *         replica-zones: [
 *             "us-central1-c",
 *             "us-central1-a"
 *         ]
 *         size-gb: 32
 *         type: "pd-ssd"
 *         labels: {
 *             label-key: 'label-value'
 *         }
 *         physical-block-size-bytes: 16384
 *         resource-policies: [
 *             "projects/project-name/regions/us-central1/resourcePolicies/schedule-name"
 *         ]
 *     end
 *
 * .. code-block:: gyro
 *
 *     google::compute-region-disk region-disk-snapshot-example
 *         name: "region-disk-snapshot-example"
 *         region: "us-central1"
 *         replica-zones: [
 *             "us-central1-c",
 *             "us-central1-a"
 *         ]
 *         snapshot: "global/snapshots/snapshot-name"
 *         source-snapshot-encryption-key
 *         kms-key-name: "my-kms-key-name"
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
        this.replicaZones = replicaZones != null
            ? replicaZones.stream().map(zone -> ComputeUtils.toZoneUrl(getProjectId(), zone)).collect(Collectors.toList())
            : null;
    }

    @Override
    public void setResourcePolicies(List<String> resourcePolicies) {
        super.setResourcePolicies(resourcePolicies != null
            ? resourcePolicies.stream().map(p -> ComputeUtils.toResourcePolicyUrl(getProjectId(), p, getRegion())).collect(Collectors.toList())
            : null);
    }

    @Override
    public void setType(String type) {
        super.setType(type != null ? ComputeUtils.toRegionDiskTypeUrl(getProjectId(), type, getRegion()) : null);
    }

    @Override
    public void copyFrom(Disk disk) {
        super.copyFrom(disk);

        setRegion(disk.getRegion().substring(disk.getRegion().lastIndexOf("/") + 1));
        setReplicaZones(disk.getReplicaZones());
        setType(disk.getType());
        setResourcePolicies(disk.getResourcePolicies());
    }

    @Override
    public boolean refresh() {
        Compute client = createComputeClient();

        try {
            Disk disk = client.regionDisks().get(getProjectId(), getRegion(), getName()).execute();
            copyFrom(disk);

            return true;
        } catch (GoogleJsonResponseException je) {
            if (je.getDetails().getCode() != 404) {
                return false;
            } else {
                throw new GyroException(je.getDetails().getMessage());
            }
        } catch (Exception ex) {
            throw new GyroException(ex.getMessage(), ex.getCause());
        }
    }

    @Override
    public void create(GyroUI ui, State state) {
        Compute client = createComputeClient();

        Disk disk = toDisk();
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
        } catch (GoogleJsonResponseException je) {
            throw new GyroException(je.getDetails().getMessage());
        } catch (Exception ex) {
            throw new GyroException(ex.getMessage(), ex.getCause());
        }
    }

    @Override
    public List<ValidationError> validate() {
        List<ValidationError> errors = super.validate();

        if (getReplicaZones().size() != 2) {
            errors.add(new ValidationError(
                this,
                "replica-zones",
                "Disk requires exactly two replica zones."));
        }

        return errors;
    }

    @Override
    public void delete(GyroUI ui, State state) {
        Compute compute = createComputeClient();

        try {
            compute.regionDisks().delete(getProjectId(), getRegion(), getName()).execute();
        } catch (GoogleJsonResponseException je) {
            throw new GyroException(je.getDetails().getMessage());
        } catch (Exception ex) {
            throw new GyroException(ex.getMessage(), ex.getCause());
        }
    }

    @Override
    public void update(GyroUI ui, State state, Resource current, Set<String> changedFieldNames) {
        Compute client = createComputeClient();

        if (changedFieldNames.contains("size-gb")) {
            saveSizeGb(client, (RegionDiskResource) current);
        }

        if (changedFieldNames.contains("labels")) {
            saveLabels(client);
        }

        if (changedFieldNames.contains("resource-policies")) {
            saveResourcePolicies(client, (RegionDiskResource) current);
        }

        refresh();
    }

    private void saveSizeGb(Compute client, RegionDiskResource oldRegionDiskResource) {
        if (getSizeGb() < oldRegionDiskResource.getSizeGb()) {
            throw new GyroException("Size of the disk cannot be decreased once set.");
        }

        try {
            RegionDisksResizeRequest resizeRequest = new RegionDisksResizeRequest();
            resizeRequest.setSizeGb(getSizeGb());
            client.regionDisks().resize(getProjectId(), getRegion(), getName(), resizeRequest).execute();
        } catch (GoogleJsonResponseException je) {
            throw new GyroException(je.getDetails().getMessage());
        } catch (Exception ex) {
            throw new GyroException(ex.getMessage(), ex.getCause());
        }
    }

    private void saveLabels(Compute client) {
        try {
            RegionSetLabelsRequest labelsRequest = new RegionSetLabelsRequest();
            labelsRequest.setLabels(getLabels());
            labelsRequest.setLabelFingerprint(getLabelFingerprint());
            client.regionDisks().setLabels(getProjectId(), getRegion(), getName(), labelsRequest).execute();
        } catch (GoogleJsonResponseException je) {
            throw new GyroException(je.getDetails().getMessage());
        } catch (Exception ex) {
            throw new GyroException(ex.getMessage(), ex.getCause());
        }
    }

    private void saveResourcePolicies(Compute client, RegionDiskResource oldRegionDiskResource) {
        try {
            if (!oldRegionDiskResource.getResourcePolicies().isEmpty()) {
                RegionDisksRemoveResourcePoliciesRequest removeResourcePoliciesRequest = new RegionDisksRemoveResourcePoliciesRequest();
                removeResourcePoliciesRequest.setResourcePolicies(oldRegionDiskResource.getResourcePolicies());
                Operation operation = client.regionDisks()
                    .removeResourcePolicies(getProjectId(), getRegion(), getName(), removeResourcePoliciesRequest).execute();
                Operation.Error error = waitForCompletion(client, operation);
                if (error != null) {
                    throw new GyroException(error.toPrettyString());
                }
            }

            if (!getResourcePolicies().isEmpty()) {
                RegionDisksAddResourcePoliciesRequest addResourcePoliciesRequest = new RegionDisksAddResourcePoliciesRequest();
                addResourcePoliciesRequest.setResourcePolicies(getResourcePolicies());
                client.regionDisks().addResourcePolicies(getProjectId(), getRegion(), getName(), addResourcePoliciesRequest).execute();
            }
        } catch (GoogleJsonResponseException je) {
            throw new GyroException(je.getDetails().getMessage());
        } catch (Exception ex) {
            throw new GyroException(ex.getMessage(), ex.getCause());
        }
    }
}
