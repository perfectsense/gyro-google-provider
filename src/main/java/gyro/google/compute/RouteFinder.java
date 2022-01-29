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

import com.google.cloud.compute.v1.ListRoutesRequest;
import com.google.cloud.compute.v1.Route;
import com.google.cloud.compute.v1.RouteList;
import com.google.cloud.compute.v1.RoutesClient;
import com.psddev.dari.util.StringUtils;
import gyro.core.Type;
import gyro.google.GoogleFinder;

/**
 * Query route.
 *
 * Example
 * -------
 *
 * .. code-block:: gyro
 *
 *    route: $(external-query google::compute-route { name: 'route-example'})
 */
@Type("compute-route")
public class RouteFinder extends GoogleFinder<RoutesClient, Route, RouteResource> {

    private String name;

    /**
     * The name of the route.
     */
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    protected List<Route> findAllGoogle(RoutesClient client) throws Exception {
        List<Route> routes = new ArrayList<>();
        RouteList routeList;
        String nextPageToken = null;

        try {
            do {
                ListRoutesRequest.Builder builder = ListRoutesRequest.newBuilder()
                    .setProject(getProjectId());

                if (nextPageToken != null) {
                    builder.setPageToken(nextPageToken);
                }

                routeList = client.list(builder.build()).getPage().getResponse();
                nextPageToken = routeList.getNextPageToken();

                routes.addAll(routeList.getItemsList());
            } while (!StringUtils.isEmpty(nextPageToken));

            return routes;

        } finally {
            client.close();
        }
    }

    @Override
    protected List<Route> findGoogle(RoutesClient client, Map<String, String> filters)
        throws Exception {
        return Collections.singletonList(client.get(getProjectId(), filters.get("name")));
    }
}
