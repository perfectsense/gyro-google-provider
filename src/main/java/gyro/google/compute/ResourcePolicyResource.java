/*
 * Copyright 2020, Perfect Sense, Inc.
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

import com.google.api.gax.rpc.NotFoundException;
import com.google.cloud.compute.v1.DeleteResourcePolicyRequest;
import com.google.cloud.compute.v1.GetResourcePolicyRequest;
import com.google.cloud.compute.v1.InsertResourcePolicyRequest;
import com.google.cloud.compute.v1.ResourcePoliciesClient;
import com.google.cloud.compute.v1.ResourcePolicy;
import gyro.core.GyroUI;
import gyro.core.Type;
import gyro.core.resource.Id;
import gyro.core.resource.Output;
import gyro.core.resource.Resource;
import gyro.core.scope.State;
import gyro.core.validation.Regex;
import gyro.core.validation.Required;
import gyro.google.Copyable;
import gyro.google.util.Utils;

/**
 * Creates a resource policy.
 *
 * Example
 * -------
 *
 * .. code-block:: gyro
 *
 *    google::compute-resource-policy example-policy-alpha
 *        name: "example-policy-alpha"
 *        region: "us-central1"
 *        description: "Resource policy example alpha"
 *
 *        snapshot-schedule-policy
 *            schedule
 *                weekly-schedule
 *                    day-of-weeks
 *                        day: "FRIDAY"
 *                        start-time: "00:00"
 *                    end
 *                    day-of-weeks
 *                        day: "MONDAY"
 *                        start-time: "00:00"
 *                    end
 *                end
 *            end
 *
 *            retention-policy
 *                max-retention-days: 7
 *                on-source-disk-delete: "KEEP_AUTO_SNAPSHOTS"
 *            end
 *
 *            snapshot-properties
 *                guest-flush: false
 *                labels: {
 *                    "foo": "bar"
 *                }
 storage-locations: ["us-central1"]
 *            end
 *        end
 *    end
 */
@Type("compute-resource-policy")
public class ResourcePolicyResource extends ComputeResource implements Copyable<ResourcePolicy> {

    private String region;
    private String name;
    private String description;
    private SnapshotSchedulePolicy snapshotSchedulePolicy;

    // Read-only
    private String selfLink;

    /**
     * Name of the region.
     */
    @Required
    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    /**
     * The name of the resource policy.
     */
    @Regex(value = "^[a-z]([-a-z0-9]{1,61}[a-z0-9])?", message = "a string 1-63 characters long and the first character must be a lowercase letter, and all following characters must be a dash, lowercase letter, or digit, except the last character, which cannot be a dash")
    @Required
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * Description for the resource policy.
     */
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Resource policy for persistent disks for creating snapshots.
     *
     * @subresource gyro.google.compute.SnapshotSchedulePolicy
     */
    @Required
    public SnapshotSchedulePolicy getSnapshotSchedulePolicy() {
        return snapshotSchedulePolicy;
    }

    public void setSnapshotSchedulePolicy(SnapshotSchedulePolicy snapshotSchedulePolicy) {
        this.snapshotSchedulePolicy = snapshotSchedulePolicy;
    }

    /**
     * Server-defined fully-qualified URL for this resource.
     */
    @Id
    @Output
    public String getSelfLink() {
        return selfLink;
    }

    public void setSelfLink(String selfLink) {
        this.selfLink = selfLink;
    }

    @Override
    protected boolean doRefresh() throws Exception {
        try (ResourcePoliciesClient client = createClient(ResourcePoliciesClient.class)) {
            ResourcePolicy policy = getResourcePolicy(client);

            if (policy == null) {
                return false;
            }

            copyFrom(policy);

            return true;
        }
    }

    @Override
    protected void doCreate(GyroUI ui, State state) throws Exception {
        try (ResourcePoliciesClient client = createClient(ResourcePoliciesClient.class)) {
            ResourcePolicy.Builder builder = ResourcePolicy.newBuilder();
            builder.setName(getName());

            if (getDescription() != null) {
                builder.setDescription(getDescription());
            }

            if (getSnapshotSchedulePolicy() != null) {
                builder.setSnapshotSchedulePolicy(getSnapshotSchedulePolicy().copyTo());
            }

            waitForCompletion(client.insertCallable().call(InsertResourcePolicyRequest.newBuilder()
                .setProject(getProjectId())
                .setRegion(getRegion())
                .setResourcePolicyResource(builder)
                .build()));
        }

        refresh();
    }

    @Override
    public void doUpdate(GyroUI ui, State state, Resource current, Set<String> changedFieldNames) throws Exception {
        // Do nothing since there is no patch/update API.
    }

    @Override
    public void doDelete(GyroUI ui, State state) throws Exception {
        try (ResourcePoliciesClient client = createClient(ResourcePoliciesClient.class)) {
            waitForCompletion(client.deleteCallable().call(DeleteResourcePolicyRequest.newBuilder()
                .setProject(getProjectId())
                .setRegion(getRegion())
                .setResourcePolicy(getName())
                .build()));
        }
    }

    @Override
    public void copyFrom(ResourcePolicy model) {
        setName(model.getName());

        if (model.hasRegion()) {
            setRegion(Utils.extractName(model.getRegion()));
        }

        if (model.hasSelfLink()) {
            setSelfLink(model.getSelfLink());
        }

        if (model.hasDescription()) {
            setDescription(model.getDescription());
        }

        setSnapshotSchedulePolicy(null);
        if (model.hasSnapshotSchedulePolicy()) {
            SnapshotSchedulePolicy currentSnapshotSchedulePolicy = newSubresource(SnapshotSchedulePolicy.class);
            currentSnapshotSchedulePolicy.copyFrom(model.getSnapshotSchedulePolicy());

            setSnapshotSchedulePolicy(currentSnapshotSchedulePolicy);
        }
    }

    private ResourcePolicy getResourcePolicy(ResourcePoliciesClient client) {
        ResourcePolicy resourcePolicy = null;

        try {
            resourcePolicy = client.get(GetResourcePolicyRequest.newBuilder().setProject(getProjectId())
                .setResourcePolicy(getName()).setRegion(getRegion()).build());

        } catch (NotFoundException ex) {
            // ignore
        }

        return resourcePolicy;
    }
}
