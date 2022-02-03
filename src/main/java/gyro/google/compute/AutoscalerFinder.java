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

import com.google.api.gax.rpc.UnaryCallable;
import com.google.cloud.compute.v1.AggregatedListAutoscalersRequest;
import com.google.cloud.compute.v1.Autoscaler;
import com.google.cloud.compute.v1.AutoscalerAggregatedList;
import com.google.cloud.compute.v1.AutoscalerList;
import com.google.cloud.compute.v1.AutoscalersClient;
import com.google.cloud.compute.v1.AutoscalersScopedList;
import com.google.cloud.compute.v1.GetAutoscalerRequest;
import com.google.cloud.compute.v1.ListAutoscalersRequest;
import gyro.core.Type;
import gyro.google.GoogleFinder;
import gyro.google.util.Utils;
import org.apache.commons.lang3.StringUtils;

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
public class AutoscalerFinder extends GoogleFinder<AutoscalersClient, Autoscaler, AutoscalerResource> {

    @Override
    protected List<Autoscaler> findAllGoogle(AutoscalersClient client) throws Exception {
        try {
            return getAutoscalers(client, ResourceScope.ZONE, null);
        } finally {
            client.close();
        }
    }

    @Override
    protected List<Autoscaler> findGoogle(
        AutoscalersClient client, Map<String, String> filters) throws Exception {

        List<Autoscaler> autoscalers = new ArrayList<>();
        String pageToken = null;

        try {
            if (filters.containsKey("zone")) {
                if (filters.containsKey("name")) {
                    autoscalers.add(client.get(GetAutoscalerRequest.newBuilder().setZone(filters.get("zone"))
                        .setAutoscaler(filters.get("name")).setProject(getProjectId()).build()));
                } else {
                    do {
                        ListAutoscalersRequest.Builder builder = ListAutoscalersRequest.newBuilder()
                            .setProject(getProjectId()).setZone(filters.get("zone"))
                            .setFilter(filters.getOrDefault("filter", ""));

                        if (pageToken != null) {
                            builder.setPageToken(pageToken);
                        }

                        AutoscalerList addressList = client.list(builder.build()).getPage().getResponse();
                        pageToken = addressList.getNextPageToken();

                        autoscalers.addAll(addressList.getItemsList());
                    } while (!StringUtils.isEmpty(pageToken));
                }
            } else {
                autoscalers.addAll(getAutoscalers(client, ResourceScope.ZONE, filters));
            }

        } finally {
            client.close();
        }

        return autoscalers;
    }

    private List<Autoscaler> getAutoscalers(
        AutoscalersClient client, ResourceScope scope, Map<String, String> filterMap) {
        String filter = Utils.convertToFilters(filterMap);

        if (scope != null) {
            filter = StringUtils.join(Arrays.asList(scope.toFilterString(), filter), " ");
        }

        List<Autoscaler> autoscalers = new ArrayList<>();
        String pageToken = null;

        do {
            UnaryCallable<AggregatedListAutoscalersRequest, AutoscalerAggregatedList> callable = client
                .aggregatedListCallable();

            AggregatedListAutoscalersRequest.Builder builder = AggregatedListAutoscalersRequest.newBuilder();

            if (pageToken != null) {
                builder.setPageToken(pageToken);
            }

            if (filter != null) {
                builder.setFilter(filter);
            }

            AutoscalerAggregatedList aggregatedList = callable.call(builder.setProject(getProjectId())
                .build());
            pageToken = aggregatedList.getNextPageToken();

            if (aggregatedList.getItemsMap() != null) {
                autoscalers.addAll(aggregatedList.getItemsMap().values().stream()
                    .map(AutoscalersScopedList::getAutoscalersList)
                    .filter(Objects::nonNull)
                    .flatMap(Collection::stream)
                    .collect(Collectors.toList()));
            }

        } while (!StringUtils.isEmpty(pageToken));

        return autoscalers;
    }
}
