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

import com.google.api.services.compute.Compute;
import com.google.api.services.compute.model.UrlMap;
import com.google.api.services.compute.model.UrlMapList;
import com.google.api.services.compute.model.UrlMapsAggregatedList;
import com.google.api.services.compute.model.UrlMapsScopedList;
import gyro.core.Type;
import gyro.google.GoogleFinder;

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
public class RegionUrlMapFinder extends GoogleFinder<Compute, UrlMap, UrlMapResource> {

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
    protected List<UrlMap> findAllGoogle(Compute client) throws Exception {
        List<UrlMap> urlMaps = new ArrayList<>();
        UrlMapsAggregatedList urlMapList;
        String nextPageToken = null;

        do {
            urlMapList = client.urlMaps().aggregatedList(getProjectId()).setPageToken(nextPageToken).execute();
            urlMaps.addAll(urlMapList.getItems().values().stream()
                .map(UrlMapsScopedList::getUrlMaps)
                .filter(Objects::nonNull)
                .flatMap(Collection::stream)
                .filter(urlMap -> urlMap.getRegion() != null)
                .collect(Collectors.toList()));
            nextPageToken = urlMapList.getNextPageToken();
        } while (nextPageToken != null);

        return urlMaps;
    }

    @Override
    protected List<UrlMap> findGoogle(Compute client, Map<String, String> filters) throws Exception {
        List<UrlMap> urlMaps;

        if (filters.containsKey("name")) {
            urlMaps = Collections.singletonList(client.regionUrlMaps()
                .get(getProjectId(), filters.get("region"), filters.get("name"))
                .execute());
        } else {
            urlMaps = new ArrayList<>();
            UrlMapList urlMapList;
            String nextPageToken = null;

            do {
                urlMapList =
                    client.regionUrlMaps().list(getProjectId(), getRegion()).setPageToken(nextPageToken).execute();
                urlMaps.addAll(urlMapList.getItems().stream()
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList()));
                nextPageToken = urlMapList.getNextPageToken();
            } while (nextPageToken != null);
        }
        return urlMaps;
    }
}
