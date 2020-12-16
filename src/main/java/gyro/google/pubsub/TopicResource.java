/*
 * Copyright 2020, Brightspot.
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
import com.google.cloud.pubsub.v1.TopicAdminClient;
import com.google.protobuf.FieldMask;
import com.google.pubsub.v1.Topic;
import com.google.pubsub.v1.TopicName;
import com.google.pubsub.v1.UpdateTopicRequest;
import com.psddev.dari.util.ObjectUtils;
import gyro.core.GyroUI;
import gyro.core.Type;
import gyro.core.resource.Id;
import gyro.core.resource.Output;
import gyro.core.resource.Resource;
import gyro.core.resource.Updatable;
import gyro.core.scope.State;
import gyro.core.validation.Required;
import gyro.google.Copyable;
import gyro.google.GoogleResource;
import gyro.google.kms.CryptoKeyResource;
import gyro.google.util.Utils;

/**
 * Add a topic.
 *
 * Example
 * -------
 *
 * .. code-block:: gyro
 *
 *    google::topic topic-example
 *        name: "topic-example"
 *
 *        labels: {
 *            name: "topic-example"
 *        }
 *    end
 */
@Type("topic")
public class TopicResource extends GoogleResource implements Copyable<Topic> {

    private CryptoKeyResource kmsKey;
    private Map<String, String> labels;
    private MessageStoragePolicy messageStoragePolicy;
    private String name;
    private String referenceName;

    /**
     * The Cloud KMS CryptoKey to be used to protect access to messages published on this topic.
     */
    @Updatable
    public CryptoKeyResource getKmsKey() {
        return kmsKey;
    }

    public void setKmsKey(CryptoKeyResource kmsKey) {
        this.kmsKey = kmsKey;
    }

    /**
     * A set of labels for the topic.
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
     * The message storage policy configuration.
     */
    @Updatable
    public MessageStoragePolicy getMessageStoragePolicy() {
        return messageStoragePolicy;
    }

    public void setMessageStoragePolicy(MessageStoragePolicy messageStoragePolicy) {
        this.messageStoragePolicy = messageStoragePolicy;
    }

    /**
     * The name of the topic.
     */
    @Required
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * The full name of the topic along with project path.
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
    public void copyFrom(Topic model) throws Exception {
        if (!ObjectUtils.isBlank(model.getKmsKeyName())) {
            setKmsKey(findById(CryptoKeyResource.class, model.getKmsKeyName()));
        }

        setLabels(model.getLabelsMap());
        setName(Utils.getTopicNameFromId(model.getName()));
        setReferenceName(model.getName());

        setMessageStoragePolicy(null);
        if (model.hasMessageStoragePolicy()) {
            MessageStoragePolicy storagePolicy = newSubresource(MessageStoragePolicy.class);
            storagePolicy.copyFrom(model.getMessageStoragePolicy());
            setMessageStoragePolicy(storagePolicy);
        }
    }

    @Override
    protected boolean doRefresh() throws Exception {
        TopicAdminClient client = createClient(TopicAdminClient.class);

        Topic topic = null;

        try {
            topic = client.getTopic(TopicName.format(getProjectId(), getName()));
        } catch (NotFoundException ignore) {
            // topic not found
        } finally {
            client.shutdownNow();
        }

        if (topic == null) {
            return false;
        }

        copyFrom(topic);

        return true;
    }

    @Override
    protected void doCreate(GyroUI ui, State state) throws Exception {
        TopicAdminClient client = createClient(TopicAdminClient.class);

        Topic.Builder builder = Topic.newBuilder().setName(TopicName.format(getProjectId(), getName()));

        if (!getLabels().isEmpty()) {
            builder.putAllLabels(getLabels());
        }

        if (getKmsKey() != null) {
            builder.setKmsKeyName(getKmsKey().getId());
        }

        if (getMessageStoragePolicy() != null) {
            builder.setMessageStoragePolicy(getMessageStoragePolicy().toMessageStoragePolicy());
        }

        try {
            client.createTopic(builder.build());
        } finally {
            client.shutdownNow();
        }

        refresh();
    }

    @Override
    protected void doUpdate(
        GyroUI ui, State state, Resource current, Set<String> changedFieldNames) throws Exception {
        TopicAdminClient client = createClient(TopicAdminClient.class);

        try {
            Topic topic = client.getTopic(getReferenceName());

            Topic.Builder topicBuilder = topic.toBuilder();

            FieldMask.Builder fieldMaskBuilder = FieldMask.newBuilder();

            if (changedFieldNames.contains("labels")) {
                topicBuilder.clearLabels();
                topicBuilder.putAllLabels(getLabels());
                fieldMaskBuilder.addPaths("labels");
            }

            if (changedFieldNames.contains("kms-key")) {
                topicBuilder.clearKmsKeyName();
                topicBuilder.setKmsKeyName(getKmsKey().getName());
                fieldMaskBuilder.addPaths("kms_key_name");
            }

            if (changedFieldNames.contains("message-storage-policy")) {
                topicBuilder.clearMessageStoragePolicy();
                topicBuilder.setMessageStoragePolicy(getMessageStoragePolicy().toMessageStoragePolicy());
                fieldMaskBuilder.addPaths("message_storage_policy");
            }

            client.updateTopic(UpdateTopicRequest.newBuilder()
                .setUpdateMask(fieldMaskBuilder.build())
                .setTopic(topicBuilder.build())
                .build());
        } finally {
            client.shutdownNow();
        }
    }

    @Override
    protected void doDelete(GyroUI ui, State state) throws Exception {
        TopicAdminClient client = createClient(TopicAdminClient.class);

        try {
            client.deleteTopic(getReferenceName());
        } finally {
            client.shutdownNow();
        }
    }
}
