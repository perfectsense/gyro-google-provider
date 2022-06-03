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

import com.google.api.gax.rpc.NotFoundException;
import com.google.cloud.compute.v1.AddResourcePoliciesRegionDiskRequest;
import com.google.cloud.compute.v1.DeleteRegionDiskRequest;
import com.google.cloud.compute.v1.Disk;
import com.google.cloud.compute.v1.GetRegionDiskRequest;
import com.google.cloud.compute.v1.InsertRegionDiskRequest;
import com.google.cloud.compute.v1.Operation;
import com.google.cloud.compute.v1.RegionDisksAddResourcePoliciesRequest;
import com.google.cloud.compute.v1.RegionDisksClient;
import com.google.cloud.compute.v1.RegionDisksRemoveResourcePoliciesRequest;
import com.google.cloud.compute.v1.RegionDisksResizeRequest;
import com.google.cloud.compute.v1.RegionSetLabelsRequest;
import com.google.cloud.compute.v1.RemoveResourcePoliciesRegionDiskRequest;
import com.google.cloud.compute.v1.ResizeRegionDiskRequest;
import com.google.cloud.compute.v1.SetLabelsRegionDiskRequest;
import gyro.core.GyroException;
import gyro.core.GyroUI;
import gyro.core.Type;
import gyro.core.resource.Resource;
import gyro.core.scope.State;
import gyro.core.validation.Required;
import gyro.core.validation.ValidationError;
import gyro.google.util.Utils;

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
 *         resource-policy: $(google::compute-resource-policy example-policy-disk-gamma)
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
 *             raw-key: "AGVsbG8gZnJvbSBHb29nbGUgQ2xvdWQgUGxhdGZvcm0="
 *         end
 *     end
 */
@Type("compute-region-disk")
public class RegionDiskResource extends AbstractDiskResource {

    private String region;
    private List<String> replicaZones;
    private String type;

    /**
     * The region where the disk resides.
     */
    @Required
    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region != null ? region.substring(region.lastIndexOf("/") + 1) : null;
    }

    /**
     * The zones where the disk should be replicated to.
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
        this.replicaZones = replicaZones != null ? replicaZones : new ArrayList<>();
    }

    /**
     * The disk type used to create the disk.
     */
    public String getType() {
        if (type == null) {
            type = "pd-standard";
        }

        return type;
    }

    public void setType(String type) {
        // Full URLs are required for type, so format the type to a full URL so it is accepted
        requires("region");
        this.type = type;
    }

    @Override
    public void copyFrom(Disk disk) {
        super.copyFrom(disk);

        setRegion(disk.getRegion());

        if (disk.hasType()) {
            setType(Utils.extractName(disk.getType()));
        }

        List<String> zones = disk.getReplicaZonesList().stream()
            .map(Utils::extractName)
            .collect(Collectors.toList());
        setReplicaZones(zones);
    }

    @Override
    public boolean doRefresh() throws Exception {
        try (RegionDisksClient client = createClient(RegionDisksClient.class)) {
            Disk disk = getRegionDisk(client);

            if (disk == null) {
                return false;
            }

            copyFrom(disk);

            return true;
        }
    }

    @Override
    public void doCreate(GyroUI ui, State state) throws Exception {
        try (RegionDisksClient client = createClient(RegionDisksClient.class)) {
            Disk.Builder builder = toDisk().toBuilder();
            builder.setRegion(getRegion());

            builder.setType(Utils.computeRegionDiskTypeUrl(getProjectId(), getRegion(), getType()));

            builder.addAllResourcePolicies(getResourcePolicy().stream()
                .map(ResourcePolicyResource::getSelfLink)
                .collect(Collectors.toList()));

            builder.addAllReplicaZones(getReplicaZones().stream()
                .map(z -> Utils.computeZoneUrl(getProjectId(), z))
                .collect(Collectors.toList()));

            Operation operation = client.insertCallable().call(InsertRegionDiskRequest.newBuilder()
                .setProject(getProjectId())
                .setRegion(getRegion())
                .setDiskResource(builder)
                .build());

            waitForCompletion(operation);
        }

        refresh();
    }

    @Override
    public void doUpdate(GyroUI ui, State state, Resource current, Set<String> changedFieldNames) throws Exception {
        try (RegionDisksClient client = createClient(RegionDisksClient.class)) {

            if (changedFieldNames.contains("size-gb")) {
                saveSizeGb(client, (RegionDiskResource) current);
            }

            if (changedFieldNames.contains("labels")) {
                saveLabels(client);
            }

            if (changedFieldNames.contains("resource-policy")) {
                saveResourcePolicies(client, (RegionDiskResource) current);
            }
        }
    }

    @Override
    public void doDelete(GyroUI ui, State state) throws Exception {
        try (RegionDisksClient client = createClient(RegionDisksClient.class)) {
            Operation operation = client.deleteCallable().call(DeleteRegionDiskRequest.newBuilder()
                .setProject(getProjectId())
                .setRegion(getRegion())
                .setDisk(getName())
                .build());

            waitForCompletion(operation);
        }
    }

    @Override
    public List<ValidationError> validate(Set<String> configuredFields) {
        List<ValidationError> errors = new ArrayList<>();

        if (getReplicaZones().size() != 2) {
            errors.add(new ValidationError(this, "replica-zones", "Disk requires exactly two replica zones."));
        }

        return errors;
    }

    private void saveSizeGb(RegionDisksClient client, RegionDiskResource oldRegionDiskResource) throws Exception {
        if (getSizeGb() < oldRegionDiskResource.getSizeGb()) {
            throw new GyroException(String.format(
                "Size of the disk cannot be decreased once set. Current size %s.",
                oldRegionDiskResource.getSizeGb()));
        }

        RegionDisksResizeRequest.Builder builder = RegionDisksResizeRequest.newBuilder();
        builder.setSizeGb(getSizeGb());

        Operation operation = client.resizeCallable().call(ResizeRegionDiskRequest.newBuilder()
            .setProject(getProjectId())
            .setRegion(getRegion())
            .setDisk(getName())
            .setRegionDisksResizeRequestResource(builder)
            .build());

        waitForCompletion(operation);
    }

    private void saveLabels(RegionDisksClient client) throws Exception {
        RegionSetLabelsRequest.Builder builder = RegionSetLabelsRequest.newBuilder();
        builder.putAllLabels(getLabels());
        builder.setLabelFingerprint(getLabelFingerprint());

        Operation operation = client.setLabelsCallable().call(SetLabelsRegionDiskRequest.newBuilder()
            .setProject(getProjectId())
            .setRegion(getRegion())
            .setResource(getName())
            .setRegionSetLabelsRequestResource(builder)
            .build());

        waitForCompletion(operation);
    }

    private void saveResourcePolicies(RegionDisksClient client, RegionDiskResource current) throws Exception {
        List<String> removed = current.getResourcePolicy().stream()
            .filter(policy -> !getResourcePolicy().contains(policy))
            .map(ResourcePolicyResource::getSelfLink)
            .collect(Collectors.toList());
        List<String> added = getResourcePolicy().stream()
            .filter(policy -> !current.getResourcePolicy().contains(policy))
            .map(ResourcePolicyResource::getSelfLink)
            .collect(Collectors.toList());

        if (!removed.isEmpty()) {
            RegionDisksRemoveResourcePoliciesRequest.Builder builder = RegionDisksRemoveResourcePoliciesRequest
                .newBuilder()
                .addAllResourcePolicies(removed);

            Operation operation = client.removeResourcePoliciesCallable().call(RemoveResourcePoliciesRegionDiskRequest.newBuilder()
                .setProject(getProjectId())
                .setRegion(getRegion())
                .setDisk(getName())
                .setRegionDisksRemoveResourcePoliciesRequestResource(builder)
                .build());

            waitForCompletion(operation);
        }

        if (!added.isEmpty()) {
            RegionDisksAddResourcePoliciesRequest.Builder builder = RegionDisksAddResourcePoliciesRequest.newBuilder()
                .addAllResourcePolicies(added);

            Operation operation = client.addResourcePoliciesCallable().call(
                AddResourcePoliciesRegionDiskRequest.newBuilder()
                    .setProject(getProjectId())
                    .setRegion(getRegion())
                    .setDisk(getName())
                    .setRegionDisksAddResourcePoliciesRequestResource(builder)
                    .build());

            waitForCompletion(operation);
        }
    }

    private Disk getRegionDisk(RegionDisksClient client) {
        Disk autoscaler = null;

        try {
            autoscaler = client.get(GetRegionDiskRequest.newBuilder()
                .setProject(getProjectId())
                .setRegion(getRegion())
                .setDisk(getName())
                .build());

        } catch (NotFoundException ex) {
            // ignore
        }

        return autoscaler;
    }
}
