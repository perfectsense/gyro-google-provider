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
import gyro.core.resource.Updatable;
import gyro.core.validation.Range;
import gyro.core.validation.Required;
import gyro.google.Copyable;

public class DeadLetterPolicy extends Diffable implements Copyable<com.google.pubsub.v1.DeadLetterPolicy> {

    private TopicResource deadLetterTopic;
    private Integer maxDeliveryAttempts;

    /**
     * The topic to which dead letter messages should be published.
     */
    @Required
    @Updatable
    public TopicResource getDeadLetterTopic() {
        return deadLetterTopic;
    }

    public void setDeadLetterTopic(TopicResource deadLetterTopic) {
        this.deadLetterTopic = deadLetterTopic;
    }

    /**
     * The maximum number of delivery attempts for any message.
     */
    @Required
    @Updatable
    @Range(min = 5, max = 100)
    public Integer getMaxDeliveryAttempts() {
        return maxDeliveryAttempts;
    }

    public void setMaxDeliveryAttempts(Integer maxDeliveryAttempts) {
        this.maxDeliveryAttempts = maxDeliveryAttempts;
    }

    @Override
    public String primaryKey() {
        return "";
    }

    @Override
    public void copyFrom(com.google.pubsub.v1.DeadLetterPolicy model) throws Exception {
        setDeadLetterTopic(findById(TopicResource.class, model.getDeadLetterTopic()));
        setMaxDeliveryAttempts(model.getMaxDeliveryAttempts());
    }

    com.google.pubsub.v1.DeadLetterPolicy toDeadLetterPolicy() {
        return com.google.pubsub.v1.DeadLetterPolicy.newBuilder()
            .setDeadLetterTopic(getDeadLetterTopic().getReferenceName())
            .setMaxDeliveryAttempts(getMaxDeliveryAttempts())
            .build();
    }
}
