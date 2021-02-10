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
        setMaxDeliveryAttempts(getMaxDeliveryAttempts());
    }

    com.google.pubsub.v1.DeadLetterPolicy toDeadLetterPolicy() {
        return com.google.pubsub.v1.DeadLetterPolicy.newBuilder()
            .setDeadLetterTopic(getDeadLetterTopic().getReferenceName())
            .setMaxDeliveryAttempts(getMaxDeliveryAttempts())
            .build();
    }
}
