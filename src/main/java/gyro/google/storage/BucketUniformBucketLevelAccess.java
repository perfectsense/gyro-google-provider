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

package gyro.google.storage;

import com.google.api.services.storage.model.Bucket;
import com.google.api.services.storage.model.Bucket.IamConfiguration.UniformBucketLevelAccess;
import gyro.core.resource.Diffable;
import gyro.core.resource.Updatable;
import gyro.google.Copyable;

/**
 * UniformBucketLevelAccess configuration for a Bucket.
 */
public class BucketUniformBucketLevelAccess extends Diffable implements Copyable<UniformBucketLevelAccess> {

    private Boolean enabled;
    private String lockedTime;

    /**
     * When ``true`` access is controlled only by bucket-level or above IAM policies.
     */
    @Updatable
    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * The deadline date after which ``enabled`` becomes ``false``.
     */
    public String getLockedTime() {
        return lockedTime;
    }

    public void setLockedTime(String lockedTime) {
        this.lockedTime = lockedTime;
    }

    @Override
    public void copyFrom(UniformBucketLevelAccess model) {
        setEnabled(model.getEnabled());

        if (model.getLockedTime() != null) {
            setLockedTime(model.getLockedTime().toStringRfc3339());
        }
    }

    public UniformBucketLevelAccess toIamConfigurationUniformBucketLevelAccess() {
        return new Bucket.IamConfiguration.UniformBucketLevelAccess().setEnabled(getEnabled());
    }
}
