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

import com.google.api.services.storage.model.Bucket.IamConfiguration;
import gyro.core.resource.Diffable;
import gyro.core.resource.Updatable;
import gyro.google.Copyable;

/**
 * Subresource for setting the Bucket IamConfiguration configuration to a Bucket.
 */
public class BucketIamConfiguration extends Diffable implements Copyable<IamConfiguration> {

    private BucketUniformBucketLevelAccess uniformBucketLevelAccess;

    /**
     * The bucket's uniform bucket-level access configuration.
     *
     * @subresource gyro.google.storage.BucketUniformBucketLevelAccess
     */
    @Updatable
    public BucketUniformBucketLevelAccess getUniformBucketLevelAccess() {
        return uniformBucketLevelAccess;
    }

    public void setUniformBucketLevelAccess(BucketUniformBucketLevelAccess uniformBucketLevelAccess) {
        this.uniformBucketLevelAccess = uniformBucketLevelAccess;
    }

    @Override
    public String primaryKey() {
        return "";
    }

    @Override
    public void copyFrom(IamConfiguration model) {
        BucketUniformBucketLevelAccess bucketUniformBucketLevelAccess = newSubresource(BucketUniformBucketLevelAccess.class);
        bucketUniformBucketLevelAccess.copyFrom(model.getUniformBucketLevelAccess());
        setUniformBucketLevelAccess(bucketUniformBucketLevelAccess);
    }

    public IamConfiguration toBucketIamConfiguration() {
        return new IamConfiguration()
            .setUniformBucketLevelAccess(getUniformBucketLevelAccess() == null
                ? null
                : getUniformBucketLevelAccess().toIamConfigurationUniformBucketLevelAccess());
    }
}
