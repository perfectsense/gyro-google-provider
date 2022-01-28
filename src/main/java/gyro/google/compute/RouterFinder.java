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
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.cloud.compute.v1.ListRoutersRequest;
import com.google.cloud.compute.v1.Router;
import com.google.cloud.compute.v1.RoutersClient;
import com.google.common.base.CaseFormat;
import gyro.core.Type;
import gyro.core.finder.Filter;
import gyro.google.GoogleFinder;

/**
 * Query router.
 *
 * Example
 * -------
 *
 * .. code-block:: gyro
 *
 *    router: $(external-query google::router { region: "us-east1" })
 */
@Type("router")
public class RouterFinder extends GoogleFinder<RoutersClient, Router, RouterResource> {

    private String name;
    private String description;
    private String region;
    private String network;
    private String advertiseMode;

    /**
     * The name of the router.
     */
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * The description of the router.
     */
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * The region where the router resides.
     */
    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    /**
     * The network to which the router belongs.
     */
    public String getNetwork() {
        return network;
    }

    public void setNetwork(String network) {
        this.network = network;
    }

    /**
     * The advertise mode of the router.
     */
    @Filter("bgp.advertise-mode")
    public String getAdvertiseMode() {
        return advertiseMode;
    }

    public void setAdvertiseMode(String advertiseMode) {
        this.advertiseMode = advertiseMode;
    }

    @Override
    protected List<Router> findAllGoogle(RoutersClient client) throws Exception {
        throw new UnsupportedOperationException("Finding `router` without filters is not supported!!");
    }

    @Override
    protected List<Router> findGoogle(RoutersClient client, Map<String, String> filters) throws Exception {
        List<Router> routers = new ArrayList<>();

        if (filters.containsKey("region")) {
            String filterString = filters.entrySet()
                .stream()
                .filter(e -> !e.getKey().equals("region"))
                .map(e -> String.format(
                    "(%s = \"%s\")",
                    CaseFormat.LOWER_HYPHEN.to(CaseFormat.LOWER_CAMEL, e.getKey()),
                    e.getValue()))
                .collect(Collectors.joining(" AND "));

            routers = client.list(ListRoutersRequest.newBuilder().setProject(getProjectId()).setFilter(filterString)
                .setRegion(filters.get("region")).build()).getPage().getResponse().getItemsList();
        }

        return routers;
    }
}
