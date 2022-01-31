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

import com.google.api.gax.rpc.InvalidArgumentException;
import com.google.api.gax.rpc.NotFoundException;
import com.google.cloud.compute.v1.GetRegionTargetHttpProxyRequest;
import com.google.cloud.compute.v1.ListRegionTargetHttpProxiesRequest;
import com.google.cloud.compute.v1.ListRegionsRequest;
import com.google.cloud.compute.v1.Region;
import com.google.cloud.compute.v1.RegionTargetHttpProxiesClient;
import com.google.cloud.compute.v1.RegionsClient;
import com.google.cloud.compute.v1.TargetHttpProxy;
import com.psddev.dari.util.StringUtils;
import gyro.core.GyroException;
import gyro.core.Type;
import gyro.google.GoogleCredentials;
import gyro.google.GoogleFinder;
import gyro.google.util.Utils;

/**
 * Query for a region target http proxy.
 *
 * Example
 * -------
 *
 * .. code-block:: gyro
 *
 *    compute-region-target-http-proxy: $(external-query google::compute-region-target-http-proxy { name: 'region-target-http-proxy-example', region: 'us-east1' })
 */
@Type("compute-region-target-http-proxy")
public class RegionTargetHttpProxyFinder
    extends GoogleFinder<RegionTargetHttpProxiesClient, TargetHttpProxy, RegionTargetHttpProxyResource> {

    private String name;
    private String region;

    /**
     * Name of the region target http proxy.
     */
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * The region of the target proxy.
     */
    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    @Override
    protected List<TargetHttpProxy> findAllGoogle(RegionTargetHttpProxiesClient client) throws Exception {
        return getRegionTargetHttpProxies(client, null);
    }

    @Override
    protected List<TargetHttpProxy> findGoogle(RegionTargetHttpProxiesClient client, Map<String, String> filters)
        throws Exception {
        List<TargetHttpProxy> proxies = new ArrayList<>();
        String region = filters.remove("region");
        String name = filters.remove("name");
        String filter = Utils.convertToFilters(filters);

        try {
            if (filters.containsKey("zone")) {
                throw new GyroException("For zonal autoscaler, use 'compute-autoscaler' instead.");
            }

            if (region != null && name != null) {
                proxies.add(client.get(GetRegionTargetHttpProxyRequest.newBuilder().setTargetHttpProxy(name)
                    .setProject(getProjectId()).setRegion(region).build()));

            } else {
                if (region != null) {
                    proxies.addAll(getTargetHttpProxies(client, filter, region));

                } else if (name != null) {
                    List<String> regions = getRegions();

                    for (String r : regions) {
                        try {
                            proxies.add(client.get(GetRegionTargetHttpProxyRequest.newBuilder()
                                .setTargetHttpProxy(name)
                                .setProject(getProjectId())
                                .setRegion(r)
                                .build()));

                        } catch (NotFoundException | InvalidArgumentException ex) {
                            // ignore
                        }
                    }
                } else {
                    proxies.addAll(getRegionTargetHttpProxies(client, filter));
                }
            }
        } finally {
            client.close();
        }

        return proxies;
    }

    private List<TargetHttpProxy> getRegionTargetHttpProxies(RegionTargetHttpProxiesClient client, String filter) {
        List<String> regionList = getRegions();

        List<TargetHttpProxy> proxies = new ArrayList<>();

        try {
            for (String region : regionList) {
                proxies.addAll(getTargetHttpProxies(client, filter, region));
            }

        } catch (NotFoundException | InvalidArgumentException ex) {
            // ignore

        } finally {
            client.close();
        }

        return proxies;
    }

    private List<TargetHttpProxy> getTargetHttpProxies(
        RegionTargetHttpProxiesClient client,
        String filter,
        String region) {
        String pageToken = null;

        List<TargetHttpProxy> proxies = new ArrayList<>();

        do {
            ListRegionTargetHttpProxiesRequest.Builder builder = ListRegionTargetHttpProxiesRequest.newBuilder()
                .setProject(getProjectId()).setRegion(region);

            if (pageToken != null) {
                builder.setPageToken(pageToken);
            }

            if (filter != null) {
                builder.setFilter(filter);
            }

            RegionTargetHttpProxiesClient.ListPagedResponse response = client.list(builder.build());
            pageToken = response.getNextPageToken();

            if (response.getPage() != null && response.getPage().getResponse() != null) {
                proxies.addAll(response.getPage().getResponse().getItemsList());
            }

        } while (!StringUtils.isEmpty(pageToken));

        return proxies;
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
