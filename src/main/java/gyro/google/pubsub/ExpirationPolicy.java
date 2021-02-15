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

import gyro.core.resource.Diffable;
import gyro.core.validation.Required;
import gyro.google.Copyable;

public class ExpirationPolicy extends Diffable implements Copyable<com.google.pubsub.v1.ExpirationPolicy> {

    private Duration ttl;

    /**
     * The "time-to-live" duration for the subscription.
     *
     * @subresource gyro.google.pubsub.Duration
     */
    @Required
    public Duration getTtl() {
        return ttl;
    }

    public void setTtl(Duration ttl) {
        this.ttl = ttl;
    }

    @Override
    public String primaryKey() {
        return "";
    }

    @Override
    public void copyFrom(com.google.pubsub.v1.ExpirationPolicy model) throws Exception {
        Duration duration = newSubresource(Duration.class);
        duration.copyFrom(model.getTtl());
        setTtl(duration);
    }

    com.google.pubsub.v1.ExpirationPolicy toExpirationPolicy() {
        return com.google.pubsub.v1.ExpirationPolicy.newBuilder().setTtl(getTtl().toDuration()).build();
    }
}
