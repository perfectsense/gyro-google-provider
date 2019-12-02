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

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.compute.Compute;
import com.google.api.services.compute.model.Disk;
import com.google.api.services.compute.model.DiskAggregatedList;
import com.google.api.services.compute.model.DisksScopedList;
import gyro.core.GyroException;
import gyro.core.Type;
import gyro.google.GoogleFinder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Query for a zonal disk.
 *
 * Example
 * -------
 *
 * .. code-block:: gyro
 *
 *    compute-disk: $(external-query google::compute-disk { name: 'disk-example', zone: 'us-east1-b' })
 */
@Type("compute-disk")
public class DiskFinder extends GoogleFinder<Compute, Disk, DiskResource> {
    private String name;
    private String zone;

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
     * The zone of the disk.
     */
    public String getZone() {
        return zone;
    }

    public void setZone(String zone) {
        this.zone = zone;
    }

    @Override
    protected List<Disk> findAllGoogle(Compute client) {
        try {
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
                    .filter(disk -> disk.getZone() != null)
                    .collect(Collectors.toList()));
                nextPageToken = diskAggregatedList.getNextPageToken();
            } while (nextPageToken != null);

            return disks;
        } catch (GoogleJsonResponseException je) {
            throw new GyroException(je.getDetails().getMessage());
        } catch (Exception ex) {
            throw new GyroException(ex.getMessage(), ex.getCause());
        }
    }

    @Override
    protected List<Disk> findGoogle(Compute client, Map<String, String> filters) {
        try {
            if (filters.containsKey("name")) {
                return Collections.singletonList(client.disks().get(getProjectId(), filters.get("zone"), filters.get("name")).execute());
            } else {
                return Optional.ofNullable(client.disks().list(getProjectId(), filters.get("zone")).execute().getItems())
                    .orElse(new ArrayList<>());
            }
        } catch (GoogleJsonResponseException je) {
            if (je.getDetails().getCode() != 404) {
                throw new GyroException(je.getDetails().getMessage());
            }

            return Collections.emptyList();
        } catch (Exception ex) {
            throw new GyroException(ex.getMessage(), ex.getCause());
        }
    }
}
