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

import com.google.cloud.compute.v1.ListUrlMapsRequest;
import com.google.cloud.compute.v1.UrlMap;
import com.google.cloud.compute.v1.UrlMapList;
import com.google.cloud.compute.v1.UrlMapsClient;
import com.psddev.dari.util.StringUtils;
import gyro.core.Type;
import gyro.google.GoogleFinder;

/**
 * Query URL map.
 *
 * Example
 * -------
 *
 * .. code-block:: gyro
 *
 *    compute-url-map: $(external-query google::compute-url-map { name: 'url-map-example' })
 */
@Type("compute-url-map")
public class UrlMapFinder extends GoogleFinder<UrlMapsClient, UrlMap, UrlMapResource> {

    private String name;

    /**
     * The name of the URL map.
     */
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    protected List<UrlMap> findAllGoogle(UrlMapsClient client) throws Exception {
        List<UrlMap> urlMaps = new ArrayList<>();
        UrlMapList urlMapList;
        String nextPageToken = null;

        try {
            do {
                ListUrlMapsRequest.Builder builder = ListUrlMapsRequest.newBuilder().setProject(getProjectId());

                if (nextPageToken != null) {
                    builder.setPageToken(nextPageToken);
                }

                urlMapList = client.list(builder.build()).getPage().getResponse();
                nextPageToken = urlMapList.getNextPageToken();

                if (urlMapList.getItemsList() != null) {
                    urlMaps.addAll(urlMapList.getItemsList());
                }

            } while (!StringUtils.isEmpty(nextPageToken));

            return urlMaps;

        } finally {
            client.close();
        }
    }

    @Override
    protected List<UrlMap> findGoogle(UrlMapsClient client, Map<String, String> filters) throws Exception {
        return Collections.singletonList(client.get(getProjectId(), filters.get("name")));
    }
}
