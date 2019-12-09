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
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import com.google.api.services.compute.Compute;
import com.google.api.services.compute.model.Snapshot;
import com.google.api.services.compute.model.SnapshotList;
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
public class SnapshotFinder extends GoogleFinder<Compute, Snapshot, SnapshotResource> {

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
    protected List<Snapshot> findAllGoogle(Compute client) throws Exception {
        String projectId = getProjectId();
        List<Snapshot> snapshots = new ArrayList<>();
        SnapshotList snapshotAggregatedList;
        String nextPageToken = null;

        do {
            snapshotAggregatedList = client.snapshots().list(projectId).setPageToken(nextPageToken).execute();
            snapshots.addAll(snapshotAggregatedList.getItems()
                .stream()
                .filter(Objects::nonNull)
                .filter(s -> SnapshotResource.parseZoneDisk(projectId, s.getSourceDisk()) != null)
                .collect(Collectors.toList()));
            nextPageToken = snapshotAggregatedList.getNextPageToken();
        } while (nextPageToken != null);

        return snapshots;
    }

    @Override
    protected List<Snapshot> findGoogle(Compute client, Map<String, String> filters) throws Exception {
        return Optional.ofNullable(client.snapshots().get(getProjectId(), filters.get("name")).execute())
            .filter(s -> SnapshotResource.parseZoneDisk(getProjectId(), s.getSourceDisk()) != null)
            .map(Collections::singletonList)
            .orElse(Collections.emptyList());
    }
}
