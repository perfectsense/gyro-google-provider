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
import java.util.stream.StreamSupport;

import com.google.api.gax.rpc.NotFoundException;
import com.google.cloud.pubsub.v1.SubscriptionAdminClient;
import com.google.protobuf.FieldMask;
import com.google.pubsub.v1.CreateSnapshotRequest;
import com.google.pubsub.v1.ProjectName;
import com.google.pubsub.v1.ProjectSnapshotName;
import com.google.pubsub.v1.Snapshot;
import com.google.pubsub.v1.UpdateSnapshotRequest;
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
import gyro.google.util.Utils;

/**
 * Add a snapshot.
 *
 * Example
 * -------
 *
 * .. code-block:: gyro
 *
 *    google::snapshot example-snapshot
 *        name: "example-snapshot"
 *        subscription: $(google::subscription subscription-push-example)
 *
 *        labels: {
 *            "example-label": "example-value"
 *        }
 *    end
 */
@Type("snapshot")
public class SnapshotResource extends GoogleResource implements Copyable<Snapshot> {

    private Map<String, String> labels;
    private String name;
    private SubscriptionResource subscription;

    // Read-only
    private String resourceName;
    private String expireTime;
    private TopicResource topic;

    /**
     * The set of labels for the snapshot.
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
     * The name of the snapshot.
     */
    @Required
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * The name of the subscription from which this snapshot is retaining messages.
     */
    @Required
    public SubscriptionResource getSubscription() {
        return subscription;
    }

    public void setSubscription(SubscriptionResource subscription) {
        this.subscription = subscription;
    }

    /**
     * The full name of the snapshot along with the project path.
     */
    @Id
    @Output
    public String getResourceName() {
        return resourceName;
    }

    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }

    /**
     * The time until which the snapshot is guaranteed to exist.
     */
    @Output
    public String getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(String expireTime) {
        this.expireTime = expireTime;
    }

    /**
     * The name of the topic from which this snapshot is retaining messages.
     */
    @Output
    public TopicResource getTopic() {
        return topic;
    }

    public void setTopic(TopicResource topic) {
        this.topic = topic;
    }

    @Override
    public void copyFrom(Snapshot model) throws Exception {
        setName(Utils.getSnapshotNameFromId(model.getName()));
        setLabels(model.getLabelsMap());
        setResourceName(model.getName());
        setTopic(findById(TopicResource.class, model.getTopic()));

        if (model.hasExpireTime()) {
            setExpireTime(model.getExpireTime().toString());
        }
    }

    @Override
    protected boolean doRefresh() throws Exception {
        SubscriptionAdminClient client = createClient(SubscriptionAdminClient.class);
        Snapshot snapshot = null;

        try {
            snapshot = getSnapshot(client);
        } catch (NotFoundException ignore) {
            // project not found
        } finally {
            client.shutdownNow();
        }

        if (snapshot == null) {
            return false;
        }

        copyFrom(snapshot);

        return true;
    }

    @Override
    protected void doCreate(GyroUI ui, State state) throws Exception {
        SubscriptionAdminClient client = createClient(SubscriptionAdminClient.class);

        copyFrom(client.createSnapshot(CreateSnapshotRequest.newBuilder()
            .setName(ProjectSnapshotName.format(getProjectId(), getName()))
            .setSubscription(getSubscription().getReferenceName())
            .putAllLabels(getLabels())
            .build()));
    }

    @Override
    protected void doUpdate(
        GyroUI ui, State state, Resource current, Set<String> changedFieldNames) throws Exception {
        SubscriptionAdminClient client = createClient(SubscriptionAdminClient.class);

        Snapshot.Builder builder = getSnapshot(client).toBuilder();
        builder.clearLabels();
        builder.putAllLabels(getLabels());

        client.updateSnapshot(UpdateSnapshotRequest.newBuilder()
            .setSnapshot(builder.build())
            .setUpdateMask(FieldMask.newBuilder().addPaths("labels").build())
            .build());
    }

    @Override
    protected void doDelete(GyroUI ui, State state) throws Exception {
        SubscriptionAdminClient client = createClient(SubscriptionAdminClient.class);

        client.deleteSnapshot(getResourceName());
    }

    private Snapshot getSnapshot(SubscriptionAdminClient client) {
        return StreamSupport.stream(client.listSnapshots(ProjectName.newBuilder()
            .setProject(getProjectId())
            .build()).iterateAll().spliterator(), false)
            .filter(r -> r.getName().equals(getResourceName()))
            .findFirst()
            .orElse(null);
    }
}
