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

import com.google.api.services.storage.model.Bucket.Logging;
import gyro.core.resource.Diffable;
import gyro.core.resource.Updatable;
import gyro.google.Copyable;

/**
 * The bucket's logging configuration setting the destination bucket and optional name prefix for the current bucket's logs.
 */
public class BucketLogging extends Diffable implements Copyable<Logging> {

    private String logBucket;
    private String logObjectPrefix;

    /**
     * A prefix for log object names.
     */
    @Updatable
    public String getLogBucket() {
        return logBucket;
    }

    public void setLogBucket(String logBucket) {
        this.logBucket = logBucket;
    }

    /**
     * The destination bucket where the current bucket's logs should be placed.
     */
    @Updatable
    public String getLogObjectPrefix() {
        return logObjectPrefix;
    }

    public void setLogObjectPrefix(String logObjectPrefix) {
        this.logObjectPrefix = logObjectPrefix;
    }

    @Override
    public void copyFrom(Logging model) {
        setLogBucket(model.getLogBucket());
        setLogObjectPrefix(model.getLogObjectPrefix());
    }

    public Logging toBucketLogging() {
        return new Logging()
                .setLogBucket(getLogBucket())
                .setLogObjectPrefix(getLogObjectPrefix());
    }
}
