/*
 * Copyright 2019, Perfect Sense, Inc.
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

package gyro.google.compute;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.google.cloud.compute.v1.ListSnapshotsRequest;
import com.google.cloud.compute.v1.Snapshot;
import com.google.cloud.compute.v1.SnapshotList;
import com.google.cloud.compute.v1.SnapshotsClient;
import com.psddev.dari.util.StringUtils;
import gyro.core.Type;
import gyro.google.GoogleFinder;

/**
 * Query for a snapshot created from a zonal disk.
 *
 * Example
 * -------
 *
 * .. code-block:: gyro
 *
 *    compute-snapshot: $(external-query google::compute-snapshot { name: 'snapshot-example' })
 */
@Type("compute-snapshot")
public class SnapshotFinder extends GoogleFinder<SnapshotsClient, Snapshot, SnapshotResource> {

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
    protected List<Snapshot> findAllGoogle(SnapshotsClient client) throws Exception {
        List<Snapshot> snapshots = new ArrayList<>();
        SnapshotList snapshotList;
        String nextPageToken = null;

        try {
            do {
                ListSnapshotsRequest.Builder builder = ListSnapshotsRequest.newBuilder()
                    .setProject(getProjectId());

                if (nextPageToken != null) {
                    builder.setPageToken(nextPageToken);
                }

                snapshotList = client.list(builder.build()).getPage().getResponse();
                nextPageToken = snapshotList.getNextPageToken();

                snapshots.addAll(snapshotList.getItemsList());
            } while (!StringUtils.isEmpty(nextPageToken));

            return snapshots;

        } finally {
            client.close();
        }
    }

    @Override
    protected List<Snapshot> findGoogle(SnapshotsClient client, Map<String, String> filters) throws Exception {
        return Collections.singletonList(client.get(getProjectId(), filters.get("name")));
    }
}
