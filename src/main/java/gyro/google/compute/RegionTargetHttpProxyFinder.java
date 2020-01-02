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
import com.google.api.services.compute.model.TargetHttpProxiesScopedList;
import com.google.api.services.compute.model.TargetHttpProxy;
import com.google.api.services.compute.model.TargetHttpProxyAggregatedList;
import gyro.core.Type;
import gyro.google.GoogleFinder;

/**
 * Query for a region target http proxy.
 *
 * Example
 * -------
 *
 * .. code-block:: gyro
 *
 *    compute-region-target-http-proxy: $(external-query google::compute-region-target-http-proxy { name: 'region-target-http-proxy-example' })
 */
@Type("compute-region-target-http-proxy")
public class RegionTargetHttpProxyFinder extends GoogleFinder<Compute, TargetHttpProxy, RegionTargetHttpProxyResource> {

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
    protected List<TargetHttpProxy> findAllGoogle(Compute client) throws Exception {
        List<TargetHttpProxy> targetHttpProxies = new ArrayList<>();
        TargetHttpProxyAggregatedList targetHttpProxyList;
        String nextPageToken = null;

        do {
            targetHttpProxyList = client.targetHttpProxies()
                .aggregatedList(getProjectId())
                .setPageToken(nextPageToken)
                .execute();
            targetHttpProxies.addAll(targetHttpProxyList
                .getItems().values().stream()
                .map(TargetHttpProxiesScopedList::getTargetHttpProxies)
                .filter(Objects::nonNull)
                .flatMap(Collection::stream)
                .filter(targetHttpProxy -> targetHttpProxy.getRegion() != null)
                .collect(Collectors.toList()));
            nextPageToken = targetHttpProxyList.getNextPageToken();
        } while (nextPageToken != null);

        return targetHttpProxies;
    }

    @Override
    protected List<TargetHttpProxy> findGoogle(Compute client, Map<String, String> filters) throws Exception {
        List<TargetHttpProxy> targetHttpProxies = new ArrayList<>();

        if (filters.containsKey("name")) {
            targetHttpProxies = Collections.singletonList(client.regionTargetHttpProxies()
                .get(getProjectId(), filters.get("region"), filters.get("name"))
                .execute());
        } else {
            targetHttpProxies = Optional.ofNullable(client.regionTargetHttpProxies()
                .list(getProjectId(), filters.get("region"))
                .execute()
                .getItems())
                .orElse(new ArrayList<>());
        }

        return targetHttpProxies;
    }
}
