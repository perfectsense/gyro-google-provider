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

import com.google.api.services.compute.model.ResourcePolicySnapshotSchedulePolicyRetentionPolicy;
import gyro.core.resource.Diffable;
import gyro.core.validation.ValidStrings;
import gyro.google.Copyable;

public class SnapshotSchedulePolicyRetentionPolicy extends Diffable implements Copyable<ResourcePolicySnapshotSchedulePolicyRetentionPolicy> {

    private Integer maxRetentionDays;
    private String onSourceDiskDelete;

    /**
     * Maximum age of the snapshot that is allowed to be kept.
     */
    public Integer getMaxRetentionDays() {
        return maxRetentionDays;
    }

    public void setMaxRetentionDays(Integer maxRetentionDays) {
        this.maxRetentionDays = maxRetentionDays;
    }

    /**
     * The behavior to apply to scheduled snapshots when the source disk is deleted.
     */
    @ValidStrings({"APPLY_RETENTION_POLICY", "KEEP_AUTO_SNAPSHOTS", "UNSPECIFIED_ON_SOURCE_DISK_DELETE"})
    public String getOnSourceDiskDelete() {
        return onSourceDiskDelete;
    }

    public void setOnSourceDiskDelete(String onSourceDiskDelete) {
        this.onSourceDiskDelete = onSourceDiskDelete;
    }

    @Override
    public String primaryKey() {
        StringBuilder key = new StringBuilder();

        if (getMaxRetentionDays() != null) {
            key.append(String.format("keeping snapshot %s days", getMaxRetentionDays()));
        }

        if (getOnSourceDiskDelete() != null) {
            if (key.length() != 0) {
                key.append(" and ");
            }
            key.append(String.format("setting %s policy when disk is deleted", getOnSourceDiskDelete()));
        }

        return key.toString();
    }

    @Override
    public void copyFrom(ResourcePolicySnapshotSchedulePolicyRetentionPolicy model) {
        setMaxRetentionDays(model.getMaxRetentionDays());
        setOnSourceDiskDelete(model.getOnSourceDiskDelete());
    }

    public ResourcePolicySnapshotSchedulePolicyRetentionPolicy copyTo() {
       return new ResourcePolicySnapshotSchedulePolicyRetentionPolicy()
           .setMaxRetentionDays(getMaxRetentionDays())
           .setOnSourceDiskDelete(getOnSourceDiskDelete());
    }
}
