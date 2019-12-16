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
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import com.google.api.services.compute.Compute;
import com.google.api.services.compute.model.Disk;
import com.google.api.services.compute.model.DiskAggregatedList;
import com.google.api.services.compute.model.DisksScopedList;
import gyro.core.Type;
import gyro.google.GoogleFinder;

/**
 * Query for a regional disk.
 *
 * Example
 * -------
 *
 * .. code-block:: gyro
 *
 *    compute-region-disk: $(external-query google::compute-region-disk { name: 'disk-example', region: 'us-east1' })
 */
@Type("compute-region-disk")
public class RegionDiskFinder extends GoogleFinder<Compute, Disk, RegionDiskResource> {

    private String region;
    private String name;

    /**
     * The name of the disk.
     */
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * The region of the disk.
     */
    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    @Override
    protected List<Disk> findAllGoogle(Compute client) throws Exception {
        List<Disk> disks = new ArrayList<>();
        DiskAggregatedList diskAggregatedList;
        String nextPageToken = null;

        do {
            diskAggregatedList = client.disks().aggregatedList(getProjectId()).setPageToken(nextPageToken).execute();
            disks.addAll(diskAggregatedList
                .getItems().values().stream()
                .map(DisksScopedList::getDisks)
                .filter(Objects::nonNull)
                .flatMap(Collection::stream)
                .filter(disk -> disk.getRegion() != null)
                .collect(Collectors.toList()));
            nextPageToken = diskAggregatedList.getNextPageToken();
        } while (nextPageToken != null);

        return disks;
    }

    @Override
    protected List<Disk> findGoogle(Compute client, Map<String, String> filters) throws Exception {
        if (filters.containsKey("name")) {
            return Collections.singletonList(client.regionDisks()
                .get(getProjectId(), filters.get("region"), filters.get("name"))
                .execute());
        } else {
            return Optional.ofNullable(client.regionDisks()
                .list(getProjectId(), filters.get("region"))
                .execute()
                .getItems())
                .orElse(new ArrayList<>());
        }
    }
}
