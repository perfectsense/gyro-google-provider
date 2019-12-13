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
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import com.google.api.services.compute.Compute;
import com.google.api.services.compute.model.InstanceGroup;
import com.google.api.services.compute.model.InstanceGroupAggregatedList;
import com.google.api.services.compute.model.InstanceGroupsScopedList;
import gyro.core.Type;
import gyro.google.GoogleFinder;

/**
 * Query for instance groups.
 *
 * Example
 * --------
 *
 * .. code-block:: gyro
 *
 *      compute-instance-group: $(external-query google::compute-instance-group { name: "instance-group-example", zone: "us-central1-a" })
 */
@Type("compute-instance-group")
public class InstanceGroupFinder extends GoogleFinder<Compute, InstanceGroup, InstanceGroupResource> {

    private String name;
    private String zone;

    /**
     * The name of the instance group.
     */
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * The zone that the instance group is within.
     */
    public String getZone() {
        return zone;
    }

    public void setZone(String zone) {
        this.zone = zone;
    }

    @Override
    protected List<InstanceGroup> findAllGoogle(Compute client) throws Exception {
        List<InstanceGroup> instanceGroups = new ArrayList<>();
        InstanceGroupAggregatedList instanceGroupAggregatedList;
        String nextPageToken = null;
        do {
            instanceGroupAggregatedList = client.instanceGroups()
                .aggregatedList(getProjectId())
                .setPageToken(nextPageToken)
                .execute();
            nextPageToken = instanceGroupAggregatedList.getNextPageToken();
            instanceGroups.addAll(instanceGroupAggregatedList
                .getItems().values().stream()
                .map(InstanceGroupsScopedList::getInstanceGroups)
                .filter(Objects::nonNull)
                .flatMap(Collection::stream)
                .collect(Collectors.toList()));
        } while (nextPageToken != null);

        return instanceGroups;
    }

    @Override
    protected List<InstanceGroup> findGoogle(Compute client, Map<String, String> filters) throws Exception {
        List<InstanceGroup> instanceGroups = new ArrayList<>();
        if (filters.containsKey("name") && filters.containsKey("zone")) {
            instanceGroups.add(client.instanceGroups()
                .get(getProjectId(), filters.get("zone"), filters.get("name"))
                .execute());
        } else if (filters.containsKey("zone")) {
            instanceGroups = Optional.ofNullable(client.instanceGroups()
                .list(getProjectId(), filters.get("zone"))
                .execute()
                .getItems())
                .orElse(new ArrayList<>());
        }

        return instanceGroups;
    }
}
