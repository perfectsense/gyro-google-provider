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
import java.util.Map;
import java.util.stream.StreamSupport;

import com.google.api.gax.rpc.NotFoundException;
import com.google.cloud.pubsub.v1.SubscriptionAdminClient;
import com.google.pubsub.v1.ProjectName;
import com.google.pubsub.v1.Snapshot;
import gyro.core.Type;
import gyro.google.GoogleFinder;
import gyro.google.util.Utils;

/**
 * Query for snapshot.
 *
 * Example
 * -------
 *
 * .. code-block:: gyro
 *
 *    snapshot: $(external-query google::snapshot {name: "example-snapshot"})
 */
@Type("snapshot")
public class SnapshotFinder extends GoogleFinder<SubscriptionAdminClient, Snapshot, SnapshotResource> {

    private String name;

    /**
     * The name of the snapshot.
     */
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    protected List<Snapshot> findAllGoogle(SubscriptionAdminClient client) throws Exception {
        List<Snapshot> snapshots = new ArrayList<>();

        try {
            client.listSnapshots(ProjectName.format(getProjectId())).iterateAll().forEach(snapshots::add);
        } catch (NotFoundException ignore) {
            // Project not found
        } finally {
            client.shutdownNow();
        }

        return snapshots;
    }

    @Override
    protected List<Snapshot> findGoogle(SubscriptionAdminClient client, Map<String, String> filters) throws Exception {
        List<Snapshot> snapshots = new ArrayList<>();

        try {
            Snapshot snapshot = StreamSupport.stream(client.listSnapshots(ProjectName.newBuilder()
                .setProject(getProjectId())
                .build()).iterateAll().spliterator(), false)
                .filter(r -> Utils.getSnapshotNameFromId(r.getName()).equals(filters.get("name")))
                .findFirst()
                .orElse(null);

            if (snapshot != null) {
                snapshots.add(snapshot);
            }
        } catch (NotFoundException ignore) {
            // Subscription not found
        } finally {
            client.shutdownNow();
        }

        return snapshots;
    }
}
