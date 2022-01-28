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
import java.util.stream.Collectors;

import com.google.api.gax.rpc.InvalidArgumentException;
import com.google.api.gax.rpc.NotFoundException;
import com.google.api.gax.rpc.UnaryCallable;
import com.google.cloud.compute.v1.AggregatedListDisksRequest;
import com.google.cloud.compute.v1.Disk;
import com.google.cloud.compute.v1.DiskAggregatedList;
import com.google.cloud.compute.v1.DiskList;
import com.google.cloud.compute.v1.DisksClient;
import com.google.cloud.compute.v1.DisksScopedList;
import com.google.cloud.compute.v1.ListDisksRequest;
import com.psddev.dari.util.StringUtils;
import gyro.core.Type;
import gyro.google.GoogleFinder;

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
public class DiskFinder extends GoogleFinder<DisksClient, Disk, DiskResource> {

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
    protected List<Disk> findAllGoogle(DisksClient client) throws Exception {
        try {
            return getDisks(client);
        } finally {
            client.close();
        }
    }

    @Override
    protected List<Disk> findGoogle(DisksClient client, Map<String, String> filters) throws Exception {
        List<Disk> disks = new ArrayList<>();

        try {
            if (filters.containsKey("name") && filters.containsKey("zone")) {
                disks = Collections.singletonList(client.get(getProjectId(), filters.get("zone"), filters.get("name")));
            } else if (filters.containsKey("zone")) {
                DiskList diskList;
                String nextPageToken = null;

                do {
                    UnaryCallable<ListDisksRequest, DisksClient.ListPagedResponse> callable = client.listPagedCallable();

                    ListDisksRequest.Builder builder = ListDisksRequest.newBuilder().setZone(filters.get("zone"));

                    if (nextPageToken != null) {
                        builder.setPageToken(nextPageToken);
                    }

                    DisksClient.ListPagedResponse pagedResponse = callable.call(builder.setProject(getProjectId())
                        .build());
                    diskList = pagedResponse.getPage().getResponse();
                    nextPageToken = pagedResponse.getNextPageToken();

                    if (diskList.getItemsList() != null) {
                        disks.addAll(diskList.getItemsList().stream().filter(Objects::nonNull)
                            .filter(disk -> disk.getZone() != null).collect(Collectors.toList()));
                    }

                } while (!StringUtils.isEmpty(nextPageToken));

            } else {
                disks.addAll(getDisks(client));
                disks.removeIf(d -> !d.getName().equals(filters.get("name")));
            }
        } catch (NotFoundException | InvalidArgumentException ex) {
            // ignore
        } finally {
            client.close();
        }

        return disks;
    }

    private List<Disk> getDisks(DisksClient client) {
        List<Disk> disks = new ArrayList<>();
        DiskAggregatedList diskAggregatedList;
        String nextPageToken = null;

        do {
            AggregatedListDisksRequest.Builder builder = AggregatedListDisksRequest.newBuilder()
                .setProject(getProjectId());

            if (nextPageToken != null) {
                builder.setPageToken(nextPageToken);
            }

            DisksClient.AggregatedListPagedResponse listPagedResponse = client.aggregatedList(builder.build());

            diskAggregatedList = listPagedResponse.getPage().getResponse();

            nextPageToken = listPagedResponse.getNextPageToken();

            if (diskAggregatedList.getItemsMap() != null) {
                disks.addAll(diskAggregatedList.getItemsMap().values().stream().map(DisksScopedList::getDisksList)
                    .flatMap(Collection::stream).collect(Collectors.toList()));
            }

        } while (!StringUtils.isEmpty(nextPageToken));

        return disks;
    }
}
