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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.api.gax.rpc.NotFoundException;
import com.google.cloud.pubsub.v1.TopicAdminClient;
import com.google.pubsub.v1.ProjectName;
import com.google.pubsub.v1.Topic;
import com.google.pubsub.v1.TopicName;
import gyro.core.Type;
import gyro.google.GoogleFinder;

/**
 * Query for topics.
 *
 * Example
 * -------
 *
 * .. code-block:: gyro
 *
 *    topic: $(external-query google::topic {name: "topic-example"})
 */
@Type("topic")
public class TopicFinder extends GoogleFinder<TopicAdminClient, Topic, TopicResource> {

    private String name;

    /**
     * The name of the topic.
     */
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    protected List<Topic> findAllGoogle(TopicAdminClient client) throws Exception {
        List<Topic> topics = new ArrayList<>();
        try {
            client.listTopics(ProjectName.format(getProjectId())).iterateAll().forEach(topics::add);
        } finally {
            client.shutdownNow();
        }

        return topics;
    }

    @Override
    protected List<Topic> findGoogle(TopicAdminClient client, Map<String, String> filters) throws Exception {
        List<Topic> topics = new ArrayList<>();

        try {
            Topic topic = client.getTopic(TopicName.format(getProjectId(), filters.get("name")));
            topics.add(topic);
        } catch (NotFoundException ignore) {
            // topic not found
        } finally {
            client.shutdownNow();
        }

        return topics;
    }
}
