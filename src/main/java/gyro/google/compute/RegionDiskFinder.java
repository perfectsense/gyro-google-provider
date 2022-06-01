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
import java.util.stream.Collectors;

import com.google.api.gax.rpc.NotFoundException;
import com.google.cloud.compute.v1.Disk;
import com.google.cloud.compute.v1.DiskList;
import com.google.cloud.compute.v1.ListRegionDisksRequest;
import com.google.cloud.compute.v1.ListRegionsRequest;
import com.google.cloud.compute.v1.Region;
import com.google.cloud.compute.v1.RegionDisksClient;
import com.google.cloud.compute.v1.RegionsClient;
import gyro.core.Type;
import gyro.google.GoogleCredentials;
import gyro.google.GoogleFinder;
import org.apache.commons.lang3.StringUtils;

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
public class RegionDiskFinder extends GoogleFinder<RegionDisksClient, Disk, RegionDiskResource> {

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
    protected List<Disk> findAllGoogle(RegionDisksClient client) throws Exception {
        try {
            return getRegionDisks(client, getRegions());
        } finally {
            client.close();
        }
    }

    @Override
    protected List<Disk> findGoogle(RegionDisksClient client, Map<String, String> filters) throws Exception {
        List<Disk> regionDisks = new ArrayList<>();

        try {
            if (filters.containsKey("name") && filters.containsKey("region")) {
                regionDisks.add(client.get(getProjectId(), filters.get("region"), filters.get("name")));
            } else if (filters.containsKey("region")) {
                regionDisks.addAll(getRegionDisks(client, Collections.singletonList(filters.get("region"))));
            } else {
                regionDisks.addAll(getRegionDisks(client, getRegions()));
                regionDisks.removeIf(d -> !d.getName().equals(filters.get("name")));
            }
        } catch (NotFoundException ex) {
            // ignore
        } finally {
            client.close();
        }

        return regionDisks;
    }

    private List<Disk> getRegionDisks(RegionDisksClient client, List<String> regions) {
        List<Disk> regionDisks = new ArrayList<>();

        DiskList regionDiskList;

        for (String requestRegion : regions) {
            ListRegionDisksRequest.Builder builder = ListRegionDisksRequest.newBuilder()
                .setRegion(requestRegion);

            String nextPageToken = null;
            do {
                if (nextPageToken != null) {
                    builder.setPageToken(nextPageToken);
                }

                RegionDisksClient.ListPagedResponse pagedResponse = client.listPagedCallable()
                    .call(builder.setProject(getProjectId()).build());

                regionDiskList = pagedResponse.getPage().getResponse();
                nextPageToken = pagedResponse.getNextPageToken();

                regionDisks.addAll(regionDiskList.getItemsList().stream()
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList()));
            } while (!StringUtils.isEmpty(nextPageToken));
        }
        return regionDisks;
    }

    private List<String> getRegions() {
        String pageToken = null;
        List<String> regionList = new ArrayList<>();

        try (RegionsClient regionsClient = credentials(GoogleCredentials.class).createClient(RegionsClient.class)) {
            do {
                ListRegionsRequest.Builder builder = ListRegionsRequest.newBuilder()
                    .setProject(getProjectId());

                if (pageToken != null) {
                    builder.setPageToken(pageToken);
                }

                RegionsClient.ListPagedResponse list = regionsClient.list(builder.build());
                pageToken = list.getNextPageToken();
                regionList.addAll(list.getPage().getResponse().getItemsList()
                    .stream().map(Region::getName).collect(Collectors.toList()));

            } while (!StringUtils.isEmpty(pageToken));
        }

        return regionList;
    }
}
