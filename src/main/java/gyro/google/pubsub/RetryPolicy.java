/*
 * Copyright 2021, Brightspot.
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

package gyro.google.pubsub;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import gyro.core.resource.Diffable;
import gyro.core.validation.ValidationError;
import gyro.google.Copyable;

public class RetryPolicy extends Diffable implements Copyable<com.google.pubsub.v1.RetryPolicy> {

    private Duration maximumBackoff;
    private Duration minimumBackoff;

    /**
     * The maximum delay between consecutive deliveries of a given message.
     *
     * @subresource gyro.google.pubsub.Duration
     */
    public Duration getMaximumBackoff() {
        return maximumBackoff;
    }

    public void setMaximumBackoff(Duration maximumBackoff) {
        this.maximumBackoff = maximumBackoff;
    }

    /**
     * The minimum delay between consecutive deliveries of a given message.
     *
     * @subresource gyro.google.pubsub.Duration
     */
    public Duration getMinimumBackoff() {
        return minimumBackoff;
    }

    public void setMinimumBackoff(Duration minimumBackoff) {
        this.minimumBackoff = minimumBackoff;
    }

    @Override
    public String primaryKey() {
        return "";
    }

    @Override
    public void copyFrom(com.google.pubsub.v1.RetryPolicy model) throws Exception {
        if (model.hasMaximumBackoff()) {
            Duration maxBackOff = newSubresource(Duration.class);
            maxBackOff.copyFrom(model.getMaximumBackoff());
            setMaximumBackoff(maxBackOff);
        }

        if (model.hasMinimumBackoff()) {
            Duration minBackOff = newSubresource(Duration.class);
            minBackOff.copyFrom(model.getMaximumBackoff());
            setMinimumBackoff(minBackOff);
        }
    }

    com.google.pubsub.v1.RetryPolicy toRetryPolicy() {
        com.google.pubsub.v1.RetryPolicy.Builder builder = com.google.pubsub.v1.RetryPolicy.newBuilder();

        if (getMaximumBackoff() != null) {
            builder.setMaximumBackoff(getMaximumBackoff().toDuration());
        }

        if (getMinimumBackoff() != null) {
            builder.setMinimumBackoff(getMinimumBackoff().toDuration());
        }

        return builder.build();
    }

    @Override
    public List<ValidationError> validate(Set<String> configuredFields) {
        List<ValidationError> errors = new ArrayList<>();

        if (getMinimumBackoff() == null && getMaximumBackoff() == null) {
            errors.add(new ValidationError(this, null, "At least one of 'minimum-back-off' or 'maximum-back-off' is required."));
        }

        return errors;
    }
}
