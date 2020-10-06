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

import com.google.api.services.compute.Compute;
import com.google.api.services.compute.model.ResourcePolicy;
import gyro.core.GyroUI;
import gyro.core.Type;
import gyro.core.resource.Id;
import gyro.core.resource.Output;
import gyro.core.resource.Resource;
import gyro.core.scope.State;
import gyro.core.validation.Regex;
import gyro.core.validation.Required;
import gyro.google.Copyable;

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
     * The name of the resource policy. Must be 1-63 characters, first character must be a lowercase letter and all following characters must be a dash, lowercase letter, or digit, except the last character, which cannot be a dash.
     */
    @Regex("^[a-z]([-a-z0-9]{1,61}[a-z0-9])?")
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
        Compute client = createComputeClient();
        ResourcePolicy policy = client.resourcePolicies().get(getProjectId(), getRegion(), getName()).execute();

        copyFrom(policy);

        return true;
    }

    @Override
    protected void doCreate(GyroUI ui, State state) throws Exception {
        Compute client = createComputeClient();
        ResourcePolicy policy = new ResourcePolicy();
        policy.setName(getName());
        policy.setDescription(getDescription());
        policy.setSnapshotSchedulePolicy(getSnapshotSchedulePolicy() != null ? getSnapshotSchedulePolicy().copyTo() : null);

        waitForCompletion(client, client.resourcePolicies().insert(getProjectId(), getRegion(), policy).execute());

        refresh();
    }

    @Override
    public void doUpdate(
        GyroUI ui, State state, Resource current, Set<String> changedFieldNames) throws Exception {
        // Do nothing since there is no patch/update API.
    }

    @Override
    public void doDelete(GyroUI ui, State state) throws Exception {
        Compute client = createComputeClient();
        client.resourcePolicies().delete(getProjectId(), getRegion(), getName()).execute();
    }

    @Override
    public void copyFrom(ResourcePolicy model) {
        setRegion(model.getRegion().substring(model.getRegion().lastIndexOf("/") + 1));
        setName(model.getName());
        setDescription(model.getDescription());
        setSelfLink(model.getSelfLink());

        setSnapshotSchedulePolicy(null);
        if (model.getSnapshotSchedulePolicy() != null) {
            SnapshotSchedulePolicy currentSnapshotSchedulePolicy = newSubresource(SnapshotSchedulePolicy.class);
            currentSnapshotSchedulePolicy.copyFrom(model.getSnapshotSchedulePolicy());
            setSnapshotSchedulePolicy(currentSnapshotSchedulePolicy);
        }
    }
}
