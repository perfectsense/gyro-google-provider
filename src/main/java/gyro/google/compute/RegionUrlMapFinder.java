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
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.api.gax.rpc.NotFoundException;
import com.google.cloud.compute.v1.GetRegionUrlMapRequest;
import com.google.cloud.compute.v1.ListRegionUrlMapsRequest;
import com.google.cloud.compute.v1.ListRegionsRequest;
import com.google.cloud.compute.v1.Region;
import com.google.cloud.compute.v1.RegionUrlMapsClient;
import com.google.cloud.compute.v1.RegionsClient;
import com.google.cloud.compute.v1.UrlMap;
import com.psddev.dari.util.StringUtils;
import gyro.core.GyroException;
import gyro.core.Type;
import gyro.google.GoogleCredentials;
import gyro.google.GoogleFinder;
import gyro.google.util.Utils;

/**
 * Query region URL map.
 *
 * Example
 * -------
 *
 * .. code-block:: gyro
 *
 *    compute-region-url-map: $(external-query google::compute-region-url-map { name: 'region-url-map-example', region: 'us-east1' })
 */
@Type("compute-region-url-map")
public class RegionUrlMapFinder extends GoogleFinder<RegionUrlMapsClient, UrlMap, UrlMapResource> {

    private String name;
    private String region;

    /**
     * The name of the URL map.
     */
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * The region of the URL map.
     */
    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    @Override
    protected List<UrlMap> findAllGoogle(RegionUrlMapsClient client) throws Exception {
        return getRegionUrlMaps(client, null);
    }

    @Override
    protected List<UrlMap> findGoogle(RegionUrlMapsClient client, Map<String, String> filters) {
        List<UrlMap> urlMaps = new ArrayList<>();
        String region = filters.remove("region");
        String name = filters.remove("name");
        String filter = Utils.convertToFilters(filters);

        try {
            if (filters.containsKey("zone")) {
                throw new GyroException("For zonal urlMap, use 'compute-urlMap' instead.");
            }

            if (region != null && name != null) {
                urlMaps.add(client.get(GetRegionUrlMapRequest.newBuilder().setUrlMap(name)
                    .setProject(getProjectId()).setRegion(region).build()));

            } else {
                if (region != null) {
                    urlMaps.addAll(getUrlMaps(client, filter, region));

                } else if (name != null) {
                    List<String> regions = getRegions();

                    for (String r : regions) {
                        try {
                            urlMaps.add(client.get(GetRegionUrlMapRequest.newBuilder().setUrlMap(name)
                                .setProject(getProjectId()).setRegion(r).build()));

                        } catch (NotFoundException ex) {
                            // ignore
                        }
                    }
                } else {
                    urlMaps.addAll(getRegionUrlMaps(client, filter));
                }
            }
        } finally {
            client.close();
        }

        return urlMaps;
    }

    private List<UrlMap> getRegionUrlMaps(RegionUrlMapsClient client, String filter) {
        List<String> regionList = getRegions();

        List<UrlMap> urlMaps = new ArrayList<>();

        try {
            for (String region : regionList) {
                urlMaps.addAll(getUrlMaps(client, filter, region));
            }

        } catch (NotFoundException ex) {
            // ignore

        } finally {
            client.close();
        }

        return urlMaps;
    }

    private List<UrlMap> getUrlMaps(RegionUrlMapsClient client, String filter, String region) {
        String pageToken = null;

        List<UrlMap> urlMaps = new ArrayList<>();

        do {
            ListRegionUrlMapsRequest.Builder builder = ListRegionUrlMapsRequest.newBuilder()
                .setProject(getProjectId()).setRegion(region);

            if (pageToken != null) {
                builder.setPageToken(pageToken);
            }

            if (filter != null) {
                builder.setFilter(filter);
            }

            RegionUrlMapsClient.ListPagedResponse response = client.list(builder.build());
            pageToken = response.getNextPageToken();

            if (response.getPage() != null && response.getPage().getResponse() != null) {
                urlMaps.addAll(response.getPage().getResponse().getItemsList());
            }

        } while (!StringUtils.isEmpty(pageToken));

        return urlMaps;
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
