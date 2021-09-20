/*
 * Copyright 2020, Perfect Sense, Inc.
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
import com.google.cloud.compute.v1.TargetHttpsProxiesScopedList;
import com.google.cloud.compute.v1.TargetHttpsProxy;
import com.google.cloud.compute.v1.TargetHttpsProxyAggregatedList;
import gyro.core.Type;
import gyro.google.GoogleFinder;

/**
 * Query for a region target https proxy.
 *
 * Example
 * -------
 *
 * .. code-block:: gyro
 *
 *    compute-region-target-https-proxy: $(external-query google::compute-region-target-https-proxy { name: 'region-target-https-proxy-example', region: 'us-east1' })
 */
@Type("compute-region-target-https-proxy")
public class RegionTargetHttpsProxyFinder
    extends GoogleFinder<Compute, TargetHttpsProxy, RegionTargetHttpsProxyResource> {

    private String name;
    private String region;

    /**
     * Name of the region target https proxy.
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
    protected List<TargetHttpsProxy> findAllGoogle(Compute client) throws Exception {
        List<TargetHttpsProxy> targetHttpsProxies = new ArrayList<>();
        TargetHttpsProxyAggregatedList targetHttpsProxyList;
        String nextPageToken = null;

        do {
            targetHttpsProxyList = client.targetHttpsProxies()
                .aggregatedList(getProjectId())
                .setPageToken(nextPageToken)
                .execute();
            targetHttpsProxies.addAll(targetHttpsProxyList
                .getItems().values().stream()
                .map(TargetHttpsProxiesScopedList::getTargetHttpsProxies)
                .filter(Objects::nonNull)
                .flatMap(Collection::stream)
                .filter(targetHttpsProxy -> targetHttpsProxy.getRegion() != null)
                .collect(Collectors.toList()));
            nextPageToken = targetHttpsProxyList.getNextPageToken();
        } while (nextPageToken != null);

        return targetHttpsProxies;
    }

    @Override
    protected List<TargetHttpsProxy> findGoogle(Compute client, Map<String, String> filters) throws Exception {
        List<TargetHttpsProxy> targetHttpsProxies = new ArrayList<>();

        if (filters.containsKey("name")) {
            targetHttpsProxies = Collections.singletonList(client.regionTargetHttpsProxies()
                .get(getProjectId(), filters.get("region"), filters.get("name"))
                .execute());
        } else {
            targetHttpsProxies = Optional.ofNullable(client.regionTargetHttpsProxies()
                .list(getProjectId(), filters.get("region"))
                .execute()
                .getItems())
                .orElse(new ArrayList<>());
        }

        return targetHttpsProxies;
    }
}
