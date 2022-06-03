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
import java.util.stream.Collectors;

import com.google.cloud.compute.v1.AggregatedListInstanceGroupManagersRequest;
import com.google.cloud.compute.v1.InstanceGroupManager;
import com.google.cloud.compute.v1.InstanceGroupManagerAggregatedList;
import com.google.cloud.compute.v1.InstanceGroupManagerList;
import com.google.cloud.compute.v1.InstanceGroupManagersClient;
import com.google.cloud.compute.v1.InstanceGroupManagersScopedList;
import com.google.cloud.compute.v1.ListInstanceGroupManagersRequest;
import gyro.core.Type;
import gyro.google.GoogleFinder;
import gyro.google.util.Utils;
import org.apache.commons.lang3.StringUtils;

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
    extends GoogleFinder<InstanceGroupManagersClient, InstanceGroupManager, InstanceGroupManagerResource> {

    @Override
    protected List<InstanceGroupManager> findAllGoogle(InstanceGroupManagersClient client) throws Exception {
        try {
            return getInstanceGroupManagers(client, ResourceScope.ZONE, null);
        } finally {
            client.close();
        }
    }

    @Override
    protected List<InstanceGroupManager> findGoogle(
        InstanceGroupManagersClient client, Map<String, String> filters) throws Exception {

        List<InstanceGroupManager> instanceGroupManagers = new ArrayList<>();
        String pageToken = null;

        try {
            if (filters.containsKey("zone")) {
                do {
                    ListInstanceGroupManagersRequest.Builder builder = ListInstanceGroupManagersRequest.newBuilder()
                        .setProject(getProjectId()).setZone(filters.get("zone"))
                        .setFilter(filters.getOrDefault("filter", ""));

                    if (pageToken != null) {
                        builder.setPageToken(pageToken);
                    }

                    InstanceGroupManagerList addressList = client.list(builder.build()).getPage().getResponse();
                    pageToken = addressList.getNextPageToken();

                    if (addressList.getItemsList() != null) {
                        instanceGroupManagers.addAll(addressList.getItemsList());
                    }

                } while (!StringUtils.isEmpty(pageToken));
            } else {
                instanceGroupManagers.addAll(getInstanceGroupManagers(client, ResourceScope.ZONE, filters));
            }

        } finally {
            client.close();
        }

        return instanceGroupManagers;
    }

    private List<InstanceGroupManager> getInstanceGroupManagers(
        InstanceGroupManagersClient client, ResourceScope scope, Map<String, String> filterMap) {
        String filter = Utils.convertToFilters(filterMap);

        if (scope != null) {
            filter = StringUtils.join(Arrays.asList(scope.toFilterString(), filter), " ");
        }

        List<InstanceGroupManager> instanceGroupManagers = new ArrayList<>();
        String pageToken = null;

        do {
            AggregatedListInstanceGroupManagersRequest.Builder builder = AggregatedListInstanceGroupManagersRequest.newBuilder();

            if (pageToken != null) {
                builder.setPageToken(pageToken);
            }

            if (filter != null) {
                builder.setFilter(filter);
            }

            InstanceGroupManagerAggregatedList aggregatedList = client.aggregatedList(builder.setProject(getProjectId())
                .build()).getPage().getResponse();
            pageToken = aggregatedList.getNextPageToken();

            if (!aggregatedList.getItemsMap().isEmpty()) {
                instanceGroupManagers.addAll(aggregatedList.getItemsMap().values().stream()
                    .map(InstanceGroupManagersScopedList::getInstanceGroupManagersList)
                    .flatMap(Collection::stream)
                    .collect(Collectors.toList()));
            }
        } while (!StringUtils.isEmpty(pageToken));

        return instanceGroupManagers;
    }
}
