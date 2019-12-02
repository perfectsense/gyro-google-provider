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

import com.google.api.client.util.DateTime;
import com.google.api.services.storage.model.Bucket.RetentionPolicy;
import gyro.core.resource.Diffable;
import gyro.core.resource.Output;
import gyro.core.resource.Updatable;
import gyro.core.validation.Range;
import gyro.google.Copyable;

/**
 * Defines the minimum age an object in the bucket must reach before it can be deleted or overwritten.
 */
public class BucketRetentionPolicy extends Diffable implements Copyable<RetentionPolicy> {

    private String effectiveTime;
    private Long retentionPeriod;

    /**
     * GCP-determined value that indicates the time from which policy was enforced and effective.
     */
    @Output
    public String getEffectiveTime() {
        return effectiveTime;
    }

    public void setEffectiveTime(String effectiveTime) {
        this.effectiveTime = effectiveTime;
    }

    /**
     * The duration in seconds that objects need to be retained. Must be greater than ``0`` and less than ``3,155,760,000`` (100 years).
     */
    @Updatable
    @Range(min = 1D, max = 3155759999D)
    public Long getRetentionPeriod() {
        return retentionPeriod;
    }

    public void setRetentionPeriod(Long retentionPeriod) {
        this.retentionPeriod = retentionPeriod;
    }

    @Override
    public void copyFrom(RetentionPolicy model) {
        setRetentionPeriod(model.getRetentionPeriod());
    }

    public RetentionPolicy toBucketRententionPolicy() {
        return new RetentionPolicy()
                .setEffectiveTime(getEffectiveTime() == null ? null : DateTime.parseRfc3339(getEffectiveTime()))
                .setRetentionPeriod(getRetentionPeriod());
    }
}
