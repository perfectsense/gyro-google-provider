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
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import com.google.api.services.compute.Compute;
import com.google.api.services.compute.model.InstanceGroupManager;
import com.google.api.services.compute.model.InstanceGroupManagerAggregatedList;
import com.google.api.services.compute.model.InstanceGroupManagerList;
import com.google.api.services.compute.model.InstanceGroupManagersScopedList;
import com.psddev.dari.util.ObjectUtils;
import com.psddev.dari.util.StringUtils;
import gyro.core.GyroException;
import gyro.core.Type;
import gyro.google.GoogleFinder;
import gyro.google.util.Utils;

/**
 * Query an instance group manager.
 *
 * You can provide an expression that filters resources. The expression must specify the field name, and the value that you want to use for filtering.
 *
 * Please see :doc:`compute-instance-group-manager` resource for available fields.
 *
 * Example
 * -------
 *
 * .. code-block:: gyro
 *
 *    instance-group-manager: $(external-query google::compute-instance-group-manager { name: 'instance-group-manager-example', zone: 'us-central1-a' })
 */
@Type("compute-instance-group-manager")
public class InstanceGroupManagerFinder
    extends GoogleFinder<Compute, InstanceGroupManager, InstanceGroupManagerResource> {

    @Override
    protected List<InstanceGroupManager> findAllGoogle(Compute client) throws Exception {
        return findAllInstanceGroupManagers(client, getProjectId(), ResourceScope.ZONE, null);
    }

    @Override
    protected List<InstanceGroupManager> findGoogle(Compute client, Map<String, String> filters) throws Exception {
        if (filters.containsKey("region")) {
            throw new GyroException(
                "For regional instance group manager, use 'compute-region-instance-group-manager' instead.");
        }

        String zone = filters.remove("zone");

        if (zone == null && !ObjectUtils.isBlank(filters)) {
            return findAllInstanceGroupManagers(client, getProjectId(), ResourceScope.ZONE, filters);
        }
        Compute.InstanceGroupManagers instanceGroupManagers = client.instanceGroupManagers();
        List<InstanceGroupManager> allInstanceGroupManagers = new ArrayList<>();

        Compute.InstanceGroupManagers.List request = instanceGroupManagers.list(getProjectId(), zone);
        request.setFilter(Utils.convertToFilters(filters));
        String nextPageToken = null;

        do {
            InstanceGroupManagerList response = request.execute();
            List<InstanceGroupManager> items = response.getItems();

            if (items == null) {
                break;
            }
            allInstanceGroupManagers.addAll(items);
            nextPageToken = response.getNextPageToken();
            request.setPageToken(nextPageToken);
        } while (nextPageToken != null);
        return allInstanceGroupManagers;
    }

    protected static List<InstanceGroupManager> findAllInstanceGroupManagers(
        Compute client,
        String projectId,
        ResourceScope scope,
        Map<String, String> filterMap)
        throws Exception {
        String filters = Utils.convertToFilters(filterMap);

        if (scope != null) {
            filters = StringUtils.join(Arrays.asList(scope.toFilterString(), filters), " ");
        }
        Compute.InstanceGroupManagers.AggregatedList request = client.instanceGroupManagers()
            .aggregatedList(projectId);
        request.setFilter(filters);
        String nextPageToken = null;
        List<InstanceGroupManager> allInstanceGroupManagers = new ArrayList<>();

        do {
            InstanceGroupManagerAggregatedList response = request.execute();
            Map<String, InstanceGroupManagersScopedList> items = response.getItems();

            if (items == null) {
                break;
            }
            items.values().stream()
                .map(InstanceGroupManagersScopedList::getInstanceGroupManagers)
                .filter(Objects::nonNull)
                .flatMap(Collection::stream)
                .collect(Collectors.toCollection(() -> allInstanceGroupManagers));
            nextPageToken = response.getNextPageToken();
            request.setPageToken(nextPageToken);
        } while (nextPageToken != null);
        return allInstanceGroupManagers;
    }
}
