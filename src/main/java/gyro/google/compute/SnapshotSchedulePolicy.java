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

import com.google.api.services.compute.model.ResourcePolicySnapshotSchedulePolicy;
import gyro.core.resource.Diffable;
import gyro.google.Copyable;

public class SnapshotSchedulePolicy extends Diffable implements Copyable<ResourcePolicySnapshotSchedulePolicy> {

    private SnapshotSchedulePolicyRetentionPolicy retentionPolicy;
    private SnapshotSchedulePolicySchedule schedule;
    private SnapshotSchedulePolicySnapshotProperties snapshotProperties;

    /**
     * Retention policy applied to snapshots created by this resource policy.
     */
    public SnapshotSchedulePolicyRetentionPolicy getRetentionPolicy() {
        return retentionPolicy;
    }

    public void setRetentionPolicy(SnapshotSchedulePolicyRetentionPolicy retentionPolicy) {
        this.retentionPolicy = retentionPolicy;
    }

    /**
     * Specifies what kind of infrastructure maintenance Google is allowed to perform on this VM and when. Schedule that is applied to disks covered by this policy.
     */
    public SnapshotSchedulePolicySchedule getSchedule() {
        return schedule;
    }

    public void setSchedule(SnapshotSchedulePolicySchedule schedule) {
        this.schedule = schedule;
    }

    /**
     * Properties with which snapshots are created such as labels and storage locations.
     */
    public SnapshotSchedulePolicySnapshotProperties getSnapshotProperties() {
        return snapshotProperties;
    }

    public void setSnapshotProperties(SnapshotSchedulePolicySnapshotProperties snapshotProperties) {
        this.snapshotProperties = snapshotProperties;
    }

    public ResourcePolicySnapshotSchedulePolicy copyTo() {
        ResourcePolicySnapshotSchedulePolicy snapshotSchedulePolicy = new ResourcePolicySnapshotSchedulePolicy();
        snapshotSchedulePolicy.setRetentionPolicy(getRetentionPolicy() != null ? getRetentionPolicy().copyTo() : null);
        snapshotSchedulePolicy.setSchedule(getSchedule() != null ? getSchedule().copyTo() : null);
        snapshotSchedulePolicy.setSnapshotProperties(getSnapshotProperties() != null ? getSnapshotProperties().copyTo() : null);

        return snapshotSchedulePolicy;
    }

    @Override
    public void copyFrom(ResourcePolicySnapshotSchedulePolicy model) {
        setRetentionPolicy(null);
        if (model.getRetentionPolicy() != null) {
            SnapshotSchedulePolicyRetentionPolicy currentRetentionPolicy = newSubresource(SnapshotSchedulePolicyRetentionPolicy.class);
            currentRetentionPolicy.copyFrom(model.getRetentionPolicy());
            setRetentionPolicy(currentRetentionPolicy);
        }

        setSchedule(null);
        if (model.getSchedule() != null) {
            SnapshotSchedulePolicySchedule currentSchedule = newSubresource(SnapshotSchedulePolicySchedule.class);
            currentSchedule.copyFrom(model.getSchedule());
            setSchedule(currentSchedule);
        }

        setSnapshotProperties(null);
        if (model.getSnapshotProperties() != null) {
            SnapshotSchedulePolicySnapshotProperties currentSnapshotProperties = newSubresource(SnapshotSchedulePolicySnapshotProperties.class);
            currentSnapshotProperties.copyFrom(model.getSnapshotProperties());
            setSnapshotProperties(currentSnapshotProperties);
        }
    }
}
