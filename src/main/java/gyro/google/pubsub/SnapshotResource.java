package gyro.google.pubsub;

import java.util.Map;
import java.util.Set;

import com.google.pubsub.v1.Snapshot;
import gyro.core.GyroUI;
import gyro.core.resource.Resource;
import gyro.core.scope.State;
import gyro.google.Copyable;
import gyro.google.GoogleResource;

public class SnapshotResource extends GoogleResource implements Copyable<Snapshot> {

    private String expireTime;
    private Map<String, String> labels;
    private String name;
    private TopicResource topic;

    /**
     * The snapshot is guaranteed to exist up until this time. A newly-created snapshot expires no later than 7 days from the time of its creation. Its exact lifetime is determined at creation by the existing backlog in the source subscription. Specifically, the lifetime of the snapshot is `7 days - (age of oldest unacked message in the subscription)`. For example, consider a subscription whose oldest unacked message is 3 days old. If a snapshot is created from this subscription, the snapshot -- which will always capture this 3-day-old backlog as long as the snapshot exists -- will expire in 4 days. The service will refuse to create a snapshot that would expire in less than 1 hour after creation.
     */
    public String getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(String expireTime) {
        this.expireTime = expireTime;
    }

    /**
     * See [Creating and managing labels] (https://cloud.google.com/pubsub/docs/labels).
     */
    public Map<String, String> getLabels() {
        return labels;
    }

    public void setLabels(Map<String, String> labels) {
        this.labels = labels;
    }

    /**
     * The name of the snapshot.
     */
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * The name of the topic from which this snapshot is retaining messages.
     */
    public TopicResource getTopic() {
        return topic;
    }

    public void setTopic(TopicResource topic) {
        this.topic = topic;
    }

    @Override
    public void copyFrom(Snapshot model) throws Exception {

    }

    @Override
    protected boolean doRefresh() throws Exception {
        return false;
    }

    @Override
    protected void doCreate(GyroUI ui, State state) throws Exception {

    }

    @Override
    protected void doUpdate(
        GyroUI ui, State state, Resource current, Set<String> changedFieldNames) throws Exception {

    }

    @Override
    protected void doDelete(GyroUI ui, State state) throws Exception {

    }
}
