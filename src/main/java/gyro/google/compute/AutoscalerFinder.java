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
import com.google.cloud.compute.v1.Autoscaler;
import com.google.cloud.compute.v1.AutoscalerAggregatedList;
import com.google.cloud.compute.v1.AutoscalerList;
import com.google.cloud.compute.v1.AutoscalersScopedList;
import com.psddev.dari.util.ObjectUtils;
import com.psddev.dari.util.StringUtils;
import gyro.core.GyroException;
import gyro.core.Type;
import gyro.google.GoogleFinder;
import gyro.google.util.Utils;

/**
 * Query an autoscaler.
 *
 * You can provide an expression that filters resources. The expression must specify the field name, and the value that you want to use for filtering.
 *
 * Please see :doc:`compute-autoscaler` resource for available fields.
 *
 * Example
 * -------
 *
 * .. code-block:: gyro
 *
 *    autoscaler: $(external-query google::compute-autoscaler { name: 'compute-autoscaler-example', zone: 'us-central1-a' })
 */
@Type("compute-autoscaler")
public class AutoscalerFinder extends GoogleFinder<Compute, Autoscaler, AutoscalerResource> {

    @Override
    protected List<Autoscaler> findAllGoogle(Compute client) throws Exception {
        return findAllAutoscalers(client, getProjectId(), ResourceScope.ZONE, null);
    }

    @Override
    protected List<Autoscaler> findGoogle(Compute client, Map<String, String> filters) throws Exception {
        if (filters.containsKey("region")) {
            throw new GyroException("For regional autoscaler, use 'compute-region-autoscaler' instead.");
        }

        String zone = filters.remove("zone");

        if (zone == null && !ObjectUtils.isBlank(filters)) {
            return findAllAutoscalers(client, getProjectId(), ResourceScope.ZONE, filters);
        }
        Compute.Autoscalers instanceGroupManagers = client.autoscalers();
        List<Autoscaler> allAutoscalers = new ArrayList<>();

        Compute.Autoscalers.List request = instanceGroupManagers.list(getProjectId(), zone);
        request.setFilter(Utils.convertToFilters(filters));
        String nextPageToken = null;

        do {
            AutoscalerList response = request.execute();
            List<Autoscaler> items = response.getItems();

            if (items == null) {
                break;
            }
            allAutoscalers.addAll(items);
            nextPageToken = response.getNextPageToken();
            request.setPageToken(nextPageToken);
        } while (nextPageToken != null);
        return allAutoscalers;
    }

    protected static List<Autoscaler> findAllAutoscalers(
        Compute client,
        String projectId,
        ResourceScope scope,
        Map<String, String> filterMap)
        throws Exception {
        String filters = Utils.convertToFilters(filterMap);

        if (scope != null) {
            filters = StringUtils.join(Arrays.asList(scope.toFilterString(), filters), " ");
        }
        Compute.Autoscalers.AggregatedList request = client.autoscalers()
            .aggregatedList(projectId);
        request.setFilter(filters);
        String nextPageToken = null;
        List<Autoscaler> allAutoscalers = new ArrayList<>();

        do {
            AutoscalerAggregatedList response = request.execute();
            Map<String, AutoscalersScopedList> items = response.getItems();

            if (items == null) {
                break;
            }
            items.values().stream()
                .map(AutoscalersScopedList::getAutoscalers)
                .filter(Objects::nonNull)
                .flatMap(Collection::stream)
                .collect(Collectors.toCollection(() -> allAutoscalers));
            nextPageToken = response.getNextPageToken();
            request.setPageToken(nextPageToken);
        } while (nextPageToken != null);
        return allAutoscalers;
    }
}
