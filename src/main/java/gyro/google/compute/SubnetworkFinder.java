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
import com.google.api.services.compute.model.Subnetwork;
import com.google.api.services.compute.model.SubnetworkAggregatedList;
import com.google.api.services.compute.model.SubnetworksScopedList;
import gyro.core.Type;
import gyro.google.GoogleFinder;

/**
 * Query subnet.
 *
 * Example
 * -------
 *
 * .. code-block:: gyro
 *
 *    subnet: $(external-query google::subnet { name: 'subnet-example', region: 'us-east1'})
 */
@Type("subnet")
public class SubnetworkFinder extends GoogleFinder<Compute, Subnetwork, SubnetworkResource> {

    private String name;
    private String region;

    /**
     * The name of the subnet.
     */
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * The region of the subnet.
     */
    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    @Override
    protected List<Subnetwork> findAllGoogle(Compute client) throws Exception {
        List<Subnetwork> subnetworks = new ArrayList<>();
        SubnetworkAggregatedList subnetworkList;
        String nextPageToken = null;

        do {
            subnetworkList = client.subnetworks().aggregatedList(getProjectId()).setPageToken(nextPageToken).execute();
            subnetworks.addAll(subnetworkList.getItems().values().stream()
                .map(SubnetworksScopedList::getSubnetworks)
                .filter(Objects::nonNull)
                .flatMap(Collection::stream)
                .collect(Collectors.toList()));
            nextPageToken = subnetworkList.getNextPageToken();
        } while (nextPageToken != null);

        return subnetworks;
    }

    @Override
    protected List<Subnetwork> findGoogle(Compute client, Map<String, String> filters) throws Exception {
        return Collections.singletonList(
            client.subnetworks().get(getProjectId(), filters.get("region"), filters.get("name")).execute());
    }
}
