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

import com.google.cloud.compute.v1.ResourcePolicySnapshotSchedulePolicy;
import gyro.core.resource.Diffable;
import gyro.google.Copyable;

public class SnapshotSchedulePolicy extends Diffable implements Copyable<ResourcePolicySnapshotSchedulePolicy> {

    private SnapshotSchedulePolicyRetentionPolicy retentionPolicy;
    private SnapshotSchedulePolicySchedule schedule;
    private SnapshotSchedulePolicySnapshotProperties snapshotProperties;

    /**
     * Retention policy applied to snapshots created by this resource policy.
     *
     * @subresource gyro.google.compute.SnapshotSchedulePolicyRetentionPolicy
     */
    public SnapshotSchedulePolicyRetentionPolicy getRetentionPolicy() {
        return retentionPolicy;
    }

    public void setRetentionPolicy(SnapshotSchedulePolicyRetentionPolicy retentionPolicy) {
        this.retentionPolicy = retentionPolicy;
    }

    /**
     * Specifies what kind of infrastructure maintenance Google is allowed to perform on this VM and when. Schedule that is applied to disks covered by this policy.
     *
     * @subresource gyro.google.compute.SnapshotSchedulePolicySchedule
     */
    public SnapshotSchedulePolicySchedule getSchedule() {
        return schedule;
    }

    public void setSchedule(SnapshotSchedulePolicySchedule schedule) {
        this.schedule = schedule;
    }

    /**
     * Properties with which snapshots are created such as labels and storage locations.
     *
     * @subresource gyro.google.compute.SnapshotSchedulePolicySnapshotProperties
     */
    public SnapshotSchedulePolicySnapshotProperties getSnapshotProperties() {
        return snapshotProperties;
    }

    public void setSnapshotProperties(SnapshotSchedulePolicySnapshotProperties snapshotProperties) {
        this.snapshotProperties = snapshotProperties;
    }

    @Override
    public String primaryKey() {
        return "";
    }

    @Override
    public void copyFrom(ResourcePolicySnapshotSchedulePolicy model) {
        setRetentionPolicy(null);
        if (model.hasRetentionPolicy()) {
            SnapshotSchedulePolicyRetentionPolicy currentRetentionPolicy =
                newSubresource(SnapshotSchedulePolicyRetentionPolicy.class);
            currentRetentionPolicy.copyFrom(model.getRetentionPolicy());

            setRetentionPolicy(currentRetentionPolicy);
        }

        setSchedule(null);
        if (model.hasSchedule()) {
            SnapshotSchedulePolicySchedule currentSchedule = newSubresource(SnapshotSchedulePolicySchedule.class);
            currentSchedule.copyFrom(model.getSchedule());

            setSchedule(currentSchedule);
        }

        setSnapshotProperties(null);
        if (model.hasSnapshotProperties()) {
            SnapshotSchedulePolicySnapshotProperties currentSnapshotProperties =
                newSubresource(SnapshotSchedulePolicySnapshotProperties.class);
            currentSnapshotProperties.copyFrom(model.getSnapshotProperties());

            setSnapshotProperties(currentSnapshotProperties);
        }
    }

    public ResourcePolicySnapshotSchedulePolicy copyTo() {
        ResourcePolicySnapshotSchedulePolicy.Builder builder = ResourcePolicySnapshotSchedulePolicy.newBuilder();

        if (getRetentionPolicy() != null) {
            builder.setRetentionPolicy(getRetentionPolicy().copyTo());
        }

        if (getSchedule() != null) {
            builder.setSchedule(getSchedule().copyTo());
        }

        if (getSnapshotProperties() != null) {
            builder.setSnapshotProperties(getSnapshotProperties().copyTo());
        }

        return builder.build();
    }
}
