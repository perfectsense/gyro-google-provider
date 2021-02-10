package gyro.google.pubsub;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.google.api.gax.rpc.NotFoundException;
import com.google.cloud.pubsub.v1.SubscriptionAdminClient;
import com.google.protobuf.FieldMask;
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
    private String referenceName;

    /**
     * Required. The name of the subscription. It must have the format `"projects/{project}/subscriptions/{subscription}"`. `{subscription}` must start with a letter, and contain only letters (`[A-Za-z]`), numbers (`[0-9]`), dashes (`-`), underscores (`_`), periods (`.`), tildes (`~`), plus (`+`) or percent signs (`%`). It must be between 3 and 255 characters in length, and it must not start with `"goog"`.
     */
    @Required
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * Required. The name of the topic from which this subscription is receiving messages. Format is `projects/{project}/topics/{topic}`. The value of this field will be `_deleted-topic_` if the topic has been deleted.
     */
    @Required
    public TopicResource getTopic() {
        return topic;
    }

    public void setTopic(TopicResource topic) {
        this.topic = topic;
    }

    /**
     * The approximate amount of time (on a best-effort basis) Pub/Sub waits for the subscriber to acknowledge receipt before resending the message. In the interval after the message is delivered and before it is acknowledged, it is considered to be *outstanding*. During that time period, the message will not be redelivered (on a best-effort basis). For pull subscriptions, this value is used as the initial value for the ack deadline. To override this value for a given message, call `ModifyAckDeadline` with the corresponding `ack_id` if using non-streaming pull or send the `ack_id` in a `StreamingModifyAckDeadlineRequest` if using streaming pull. The minimum custom deadline you can specify is 10 seconds. The maximum custom deadline you can specify is 600 seconds (10 minutes). If this parameter is 0, a default value of 10 seconds is used. For push delivery, this value is also used to set the request timeout for the call to the push endpoint. If the subscriber never acknowledges the message, the Pub/Sub system will eventually redeliver the message.
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
     * A policy that specifies the conditions for dead lettering messages in this subscription. If dead_letter_policy is not set, dead lettering is disabled. The Cloud Pub/Sub service account associated with this subscriptions's parent project (i.e., service-{project_number}@gcp-sa-pubsub.iam.gserviceaccount.com) must have permission to Acknowledge() messages on this subscription.
     */
    @Updatable
    public DeadLetterPolicy getDeadLetterPolicy() {
        return deadLetterPolicy;
    }

    public void setDeadLetterPolicy(DeadLetterPolicy deadLetterPolicy) {
        this.deadLetterPolicy = deadLetterPolicy;
    }

    /**
     * Indicates whether the subscription is detached from its topic. Detached subscriptions don't receive messages from their topic and don't retain any backlog. `Pull` and `StreamingPull` requests will return FAILED_PRECONDITION. If the subscription is a push subscription, pushes to the endpoint will not be made.
     */
    @Updatable
    public Boolean getDetached() {
        return detached;
    }

    public void setDetached(Boolean detached) {
        this.detached = detached;
    }

    /**
     * If true, messages published with the same `ordering_key` in `PubsubMessage` will be delivered to the subscribers in the order in which they are received by the Pub/Sub system. Otherwise, they may be delivered in any order.
     */
    @Updatable
    public Boolean getEnableMessageOrdering() {
        return enableMessageOrdering;
    }

    public void setEnableMessageOrdering(Boolean enableMessageOrdering) {
        this.enableMessageOrdering = enableMessageOrdering;
    }

    /**
     * A policy that specifies the conditions for this subscription's expiration. A subscription is considered active as long as any connected subscriber is successfully consuming messages from the subscription or is issuing operations on the subscription. If `expiration_policy` is not set, a *default policy* with `ttl` of 31 days will be used. The minimum allowed value for `expiration_policy.ttl` is 1 day.
     */
    @Updatable
    public ExpirationPolicy getExpirationPolicy() {
        return expirationPolicy;
    }

    public void setExpirationPolicy(ExpirationPolicy expirationPolicy) {
        this.expirationPolicy = expirationPolicy;
    }

    /**
     * An expression written in the Pub/Sub [filter language](https://cloud.google.com/pubsub/docs/filtering). If non-empty, then only `PubsubMessage`s whose `attributes` field matches the filter are delivered on this subscription. If empty, then no messages are filtered out.
     */
    @Updatable
    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    /**
     * See Creating and managing labels.
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
     * Configuration for how long to retain unacknowledged messages in the subscription's backlog, from the moment a message is published. If `retain_acked_messages` is true, then this also configures the retention of acknowledged messages, and thus configures how far back in time a `Seek` can be done. Defaults to 7 days. Cannot be more than 7 days or less than 10 minutes.
     */
    @Updatable
    public Duration getMessageRetention() {
        return this.messageRetention;
    }

    public void setMessageRetention(Duration messageRetention) {
        this.messageRetention = messageRetention;
    }

    /**
     * If push delivery is used with this subscription, this field is used to configure it. An empty `pushConfig` signifies that the subscriber will pull and ack messages using API methods.
     */
    @Updatable
    public PushConfig getPushConfig() {
        return pushConfig;
    }

    public void setPushConfig(PushConfig pushConfig) {
        this.pushConfig = pushConfig;
    }

    /**
     * Indicates whether to retain acknowledged messages. If true, then messages are not expunged from the subscription's backlog, even if they are acknowledged, until they fall out of the `message_retention_duration` window. This must be true if you would like to [Seek to a timestamp] (https://cloud.google.com/pubsub/docs/replay-overview#seek_to_a_time).
     */
    @Updatable
    public Boolean getRetainAckedMessages() {
        return retainAckedMessages;
    }

    public void setRetainAckedMessages(Boolean retainAckedMessages) {
        this.retainAckedMessages = retainAckedMessages;
    }

    /**
     * A policy that specifies how Pub/Sub retries message delivery for this subscription. If not set, the default retry policy is applied. This generally implies that messages will be retried as soon as possible for healthy subscribers. RetryPolicy will be triggered on NACKs or acknowledgement deadline exceeded events for a given message.
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
            .setName(ProjectSubscriptionName.format(getProjectId(), getName()))
            .setTopic(getTopic().getReferenceName());

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
            client.createSubscription(builder.build());
        } finally {
            client.shutdownNow();
        }
    }

    @Override
    protected void doUpdate(
        GyroUI ui, State state, Resource current, Set<String> changedFieldNames) throws Exception {
        SubscriptionAdminClient client = createClient(SubscriptionAdminClient.class);

        try {

            // handled via topic client
            if (changedFieldNames.contains("detached")) {
                // todo
            }

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

            if (changedFieldNames.contains("enable-message-ordering")) {
                subscriptionBuilder.clearEnableMessageOrdering();
                subscriptionBuilder.setEnableMessageOrdering(getEnableMessageOrdering());
                fieldMaskBuilder.addPaths("enable_message_ordering");
            }

            if (changedFieldNames.contains("expiration-policy")) {
                subscriptionBuilder.clearExpirationPolicy();
                subscriptionBuilder.setExpirationPolicy(getExpirationPolicy().toExpirationPolicy());
                fieldMaskBuilder.addPaths("expiration_policy");
            }

            if (changedFieldNames.contains("message-retention")) {
                subscriptionBuilder.clearMessageRetentionDuration();
                subscriptionBuilder.setMessageRetentionDuration(getMessageRetention().toDuration());
                fieldMaskBuilder.addPaths("_message_retention_duration");
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
