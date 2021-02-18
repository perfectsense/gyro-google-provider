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

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.google.api.gax.rpc.NotFoundException;
import com.google.cloud.pubsub.v1.SubscriptionAdminClient;
import com.google.cloud.pubsub.v1.TopicAdminClient;
import com.google.protobuf.FieldMask;
import com.google.pubsub.v1.DetachSubscriptionRequest;
import com.google.pubsub.v1.ProjectSubscriptionName;
import com.google.pubsub.v1.Subscription;
import com.google.pubsub.v1.UpdateSubscriptionRequest;
import gyro.core.GyroUI;
import gyro.core.Type;
import gyro.core.resource.Id;
import gyro.core.resource.Output;
import gyro.core.resource.Resource;
import gyro.core.resource.Updatable;
import gyro.core.scope.State;
import gyro.core.validation.Range;
import gyro.core.validation.Required;
import gyro.google.Copyable;
import gyro.google.GoogleResource;
import gyro.google.util.Utils;

/**
 * Add a subscription.
 *
 * Example
 * -------
 *
 * .. code-block:: gyro
 *
 *    google::subscription subscription-pull-example
 *        name: "subscription-pull-example"
 *        topic: $(google::topic topic-example-for-subscription)
 *
 *        ack-deadline-seconds: 15
 *        enable-message-ordering: false
 *        filter: ""
 *        retain-acked-messages: false
 *        detached: true
 *
 *        expiration-policy
 *            ttl
 *                seconds: 2678400
 *                nanos: 0
 *            end
 *        end
 *
 *        message-retention
 *            seconds: 525780
 *            nanos: 0
 *        end
 *
 *        retry-policy
 *            maximum-backoff
 *                seconds: 600
 *                nanos: 0
 *            end
 *
 *            minimum-backoff
 *                seconds: 600
 *                nanos: 0
 *            end
 *        end
 *
 *        labels: {
 *            name: "subscription-pull-example"
 *        }
 *    end
 */
@Type("subscription")
public class SubscriptionResource extends GoogleResource implements Copyable<Subscription> {

    private String name;
    private TopicResource topic;
    private Integer ackDeadlineSeconds;
    private DeadLetterPolicy deadLetterPolicy;
    private Boolean detached;
    private Boolean enableMessageOrdering;
    private ExpirationPolicy expirationPolicy;
    private String filter;
    private Map<String, String> labels;
    private Duration messageRetention;
    private PushConfig pushConfig;
    private Boolean retainAckedMessages;
    private RetryPolicy retryPolicy;

    // Read-only
    private String referenceName;

    /**
     * The name of the subscription.
     */
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * The topic from which this subscription is receiving messages.
     */
    @Required
    public TopicResource getTopic() {
        return topic;
    }

    public void setTopic(TopicResource topic) {
        this.topic = topic;
    }

    /**
     * The approximate amount of time (on a best-effort basis) Pub/Sub waits for the subscriber to acknowledge receipt before resending the message.
     */
    @Range(min = 10, max = 600)
    @Updatable
    public Integer getAckDeadlineSeconds() {
        return ackDeadlineSeconds;
    }

    public void setAckDeadlineSeconds(Integer ackDeadlineSeconds) {
        this.ackDeadlineSeconds = ackDeadlineSeconds;
    }

    /**
     * The policy that specifies the conditions for dead lettering messages in this subscription.
     *
     * @subresource gyro.google.pubsub.DeadLetterPolicy
     */
    @Updatable
    public DeadLetterPolicy getDeadLetterPolicy() {
        return deadLetterPolicy;
    }

    public void setDeadLetterPolicy(DeadLetterPolicy deadLetterPolicy) {
        this.deadLetterPolicy = deadLetterPolicy;
    }

    /**
     * When set to ``true``, the subscription is detached from its topic.
     */
    @Updatable
    public Boolean getDetached() {
        return detached;
    }

    public void setDetached(Boolean detached) {
        this.detached = detached;
    }

    /**
     * When set to ``true``, messages published with the same ordering key in a message will be delivered to the subscribers in the order in which they are received by the Pub/Sub system.
     */
    public Boolean getEnableMessageOrdering() {
        return enableMessageOrdering;
    }

    public void setEnableMessageOrdering(Boolean enableMessageOrdering) {
        this.enableMessageOrdering = enableMessageOrdering;
    }

    /**
     * The policy that specifies the conditions for this subscription's expiration.
     *
     * @subresource gyro.google.pubsub.ExpirationPolicy
     */
    @Updatable
    public ExpirationPolicy getExpirationPolicy() {
        return expirationPolicy;
    }

    public void setExpirationPolicy(ExpirationPolicy expirationPolicy) {
        this.expirationPolicy = expirationPolicy;
    }

    /**
     * The expression written in the Pub/Sub `filter language <https://cloud.google.com/pubsub/docs/filtering>`_.
     */
    @Updatable
    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    /**
     * The set of labels for the subscription.
     */
    @Updatable
    public Map<String, String> getLabels() {
        if (labels == null) {
            labels = new HashMap<>();
        }

        return labels;
    }

    public void setLabels(Map<String, String> labels) {
        this.labels = labels;
    }

    /**
     * The configuration for how long to retain unacknowledged messages in the subscription's backlog, from the moment a message is published.
     *
     * @subresource gyro.google.pubsub.Duration
     */
    @Updatable
    public Duration getMessageRetention() {
        return this.messageRetention;
    }

    public void setMessageRetention(Duration messageRetention) {
        this.messageRetention = messageRetention;
    }

    /**
     * The push delivery configuration for the subscription.
     *
     * @subresource gyro.google.pubsub.PushConfig
     */
    @Updatable
    public PushConfig getPushConfig() {
        return pushConfig;
    }

    public void setPushConfig(PushConfig pushConfig) {
        this.pushConfig = pushConfig;
    }

    /**
     * When set to ``true``, the acknowledged messages are retained.
     */
    @Updatable
    public Boolean getRetainAckedMessages() {
        return retainAckedMessages;
    }

    public void setRetainAckedMessages(Boolean retainAckedMessages) {
        this.retainAckedMessages = retainAckedMessages;
    }

    /**
     * The policy that specifies how Pub/Sub retries message delivery for this subscription.
     *
     * @subresource gyro.google.pubsub.RetryPolicy
     */
    @Updatable
    public RetryPolicy getRetryPolicy() {
        return retryPolicy;
    }

    public void setRetryPolicy(RetryPolicy retryPolicy) {
        this.retryPolicy = retryPolicy;
    }

    /**
     * The full name of the subscription path including the project path.
     */
    @Id
    @Output
    public String getReferenceName() {
        return referenceName;
    }

    public void setReferenceName(String referenceName) {
        this.referenceName = referenceName;
    }

    @Override
    public void copyFrom(Subscription model) throws Exception {
        setAckDeadlineSeconds(model.getAckDeadlineSeconds());
        setFilter(model.getFilter());
        setEnableMessageOrdering(model.getEnableMessageOrdering());
        setLabels(model.getLabelsMap());
        setName(Utils.getSubscriptionNameFromId(model.getName()));
        setRetainAckedMessages(model.getRetainAckedMessages());
        setTopic(findById(TopicResource.class, model.getTopic()));
        setDetached(model.getDetached());

        setMessageRetention(null);
        if (model.hasMessageRetentionDuration()) {
            Duration duration = newSubresource(Duration.class);
            duration.copyFrom(model.getMessageRetentionDuration());
            setMessageRetention(duration);
        }

        setDeadLetterPolicy(null);
        if (model.hasDeadLetterPolicy()) {
            DeadLetterPolicy deadLetterPolicy = newSubresource(DeadLetterPolicy.class);
            deadLetterPolicy.copyFrom(model.getDeadLetterPolicy());
            setDeadLetterPolicy(deadLetterPolicy);
        }

        setRetryPolicy(null);
        if (model.hasRetryPolicy()) {
            RetryPolicy retryPolicy = newSubresource(RetryPolicy.class);
            retryPolicy.copyFrom(model.getRetryPolicy());
            setRetryPolicy(retryPolicy);
        }

        setPushConfig(null);
        if (model.hasPushConfig()) {
            PushConfig pushConfig = newSubresource(PushConfig.class);
            pushConfig.copyFrom(model.getPushConfig());
            setPushConfig(pushConfig);
        }

        setExpirationPolicy(null);
        if (model.hasExpirationPolicy()) {
            ExpirationPolicy expirationPolicy = newSubresource(ExpirationPolicy.class);
            expirationPolicy.copyFrom(model.getExpirationPolicy());
            setExpirationPolicy(expirationPolicy);
        }

        setReferenceName(model.getName());
    }

    @Override
    protected boolean doRefresh() throws Exception {
        SubscriptionAdminClient client = createClient(SubscriptionAdminClient.class);
        Subscription subscription = null;

        try {
            subscription = client.getSubscription(ProjectSubscriptionName.format(
                getProjectId(),
                getName()));
        } catch (NotFoundException ignore) {
            // Subscription not found
        } finally {
            client.shutdownNow();
        }

        if (subscription == null) {
            return false;
        }

        copyFrom(subscription);
        return true;
    }

    @Override
    protected void doCreate(GyroUI ui, State state) throws Exception {
        SubscriptionAdminClient client = createClient(SubscriptionAdminClient.class);

        Subscription.Builder builder = Subscription.newBuilder()
            .setTopic(getTopic().getReferenceName());

        if (getName() != null) {
            builder.setName(ProjectSubscriptionName.format(getProjectId(), getName()));
        }

        if (getAckDeadlineSeconds() != null) {
            builder.setAckDeadlineSeconds(getAckDeadlineSeconds());
        }

        if (getDeadLetterPolicy() != null) {
            builder.setDeadLetterPolicy(getDeadLetterPolicy().toDeadLetterPolicy());
        }

        if (getFilter() != null) {
            builder.setFilter(getFilter());
        }

        if (getEnableMessageOrdering() != null) {
            builder.setEnableMessageOrdering(getEnableMessageOrdering());
        }

        if (getExpirationPolicy() != null) {
            builder.setExpirationPolicy(getExpirationPolicy().toExpirationPolicy());
        }

        if (getMessageRetention() != null) {
            builder.setMessageRetentionDuration(getMessageRetention().toDuration());
        }

        if (getPushConfig() != null) {
            builder.setPushConfig(getPushConfig().toPushConfig());
        }

        if (getRetainAckedMessages() != null) {
            builder.setRetainAckedMessages(getRetainAckedMessages());
        }

        if (getRetryPolicy() != null) {
            builder.setRetryPolicy(getRetryPolicy().toRetryPolicy());
        }

        if (!getLabels().isEmpty()) {
            builder.putAllLabels(getLabels());
        }

        try {
            Subscription subscription = client.createSubscription(builder.build());

            copyFrom(subscription);

            if (getDetached().equals(Boolean.TRUE)) {
                TopicAdminClient topicClient = createClient(TopicAdminClient.class);

                try {
                    topicClient.detachSubscription(DetachSubscriptionRequest.newBuilder()
                        .setSubscription(getReferenceName()).build());
                } finally {
                    topicClient.shutdown();
                }
            }
        } finally {
            client.shutdownNow();
        }
    }

    @Override
    protected void doUpdate(
        GyroUI ui, State state, Resource current, Set<String> changedFieldNames) throws Exception {
        SubscriptionAdminClient client = createClient(SubscriptionAdminClient.class);

        try {
            if (changedFieldNames.contains("detached") && getDetached().equals(Boolean.TRUE)) {
                TopicAdminClient topicClient = createClient(TopicAdminClient.class);

                try {
                    topicClient.detachSubscription(DetachSubscriptionRequest.newBuilder()
                        .setSubscription(getReferenceName()).build());
                } finally {
                    topicClient.shutdown();
                }

                changedFieldNames.remove("detached");
            }

            if (!changedFieldNames.isEmpty()) {
                Subscription subscription = client.getSubscription(getReferenceName());

                Subscription.Builder subscriptionBuilder = subscription.toBuilder();

                FieldMask.Builder fieldMaskBuilder = FieldMask.newBuilder();

                if (changedFieldNames.contains("ack-deadline-seconds")) {
                    subscriptionBuilder.clearAckDeadlineSeconds();
                    subscriptionBuilder.setAckDeadlineSeconds(getAckDeadlineSeconds());
                    fieldMaskBuilder.addPaths("ack_deadline_seconds");
                }

                if (changedFieldNames.contains("dead-letter-policy")) {
                    subscriptionBuilder.clearDeadLetterPolicy();
                    subscriptionBuilder.setDeadLetterPolicy(getDeadLetterPolicy().toDeadLetterPolicy());
                    fieldMaskBuilder.addPaths("dead_letter_policy");
                }

                if (changedFieldNames.contains("filter")) {
                    subscriptionBuilder.clearFilter();
                    subscriptionBuilder.setFilter(getFilter());
                    fieldMaskBuilder.addPaths("filter");
                }

                if (changedFieldNames.contains("expiration-policy")) {
                    subscriptionBuilder.clearExpirationPolicy();
                    subscriptionBuilder.setExpirationPolicy(getExpirationPolicy().toExpirationPolicy());
                    fieldMaskBuilder.addPaths("expiration_policy");
                }

                if (changedFieldNames.contains("message-retention")) {
                    subscriptionBuilder.clearMessageRetentionDuration();
                    subscriptionBuilder.setMessageRetentionDuration(getMessageRetention().toDuration());
                    fieldMaskBuilder.addPaths("message_retention_duration");
                }

                if (changedFieldNames.contains("push-config")) {
                    subscriptionBuilder.clearPushConfig();
                    subscriptionBuilder.setPushConfig(getPushConfig().toPushConfig());
                    fieldMaskBuilder.addPaths("push_config");
                }

                if (changedFieldNames.contains("retain-acked-messages")) {
                    subscriptionBuilder.clearRetainAckedMessages();
                    subscriptionBuilder.setRetainAckedMessages(getRetainAckedMessages());
                    fieldMaskBuilder.addPaths("retain_acked_messages");
                }

                if (changedFieldNames.contains("labels")) {
                    subscriptionBuilder.clearLabels();
                    subscriptionBuilder.putAllLabels(getLabels());
                    fieldMaskBuilder.addPaths("labels");
                }

                if (changedFieldNames.contains("retry-policy")) {
                    subscriptionBuilder.clearRetryPolicy();
                    subscriptionBuilder.setRetryPolicy(getRetryPolicy().toRetryPolicy());
                    fieldMaskBuilder.addPaths("retry_policy");
                }

                client.updateSubscription(UpdateSubscriptionRequest.newBuilder()
                    .setSubscription(subscriptionBuilder.build())
                    .setUpdateMask(fieldMaskBuilder.build())
                    .build());
            }
        } finally {
            client.shutdownNow();
        }
    }

    @Override
    protected void doDelete(GyroUI ui, State state) throws Exception {
        SubscriptionAdminClient client = createClient(SubscriptionAdminClient.class);

        try {
            client.deleteSubscription(getReferenceName());
        } finally {
            client.shutdownNow();
        }
    }
}
