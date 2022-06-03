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
import java.util.stream.Collectors;

import com.google.cloud.compute.v1.AggregatedListInstancesRequest;
import com.google.cloud.compute.v1.Instance;
import com.google.cloud.compute.v1.InstanceAggregatedList;
import com.google.cloud.compute.v1.InstanceList;
import com.google.cloud.compute.v1.InstancesClient;
import com.google.cloud.compute.v1.InstancesScopedList;
import com.google.cloud.compute.v1.ListInstancesRequest;
import com.psddev.dari.util.StringUtils;
import gyro.core.Type;
import gyro.google.GoogleFinder;

/**
 * Query instance.
 *
 * Example
 * -------
 *
 * .. code-block:: gyro
 *
 *    instance: $(external-query google::compute-instance {zone: 'us-west1-a', filter: 'name = gyro-development'})
 */
@Type("compute-instance")
public class InstanceFinder extends GoogleFinder<InstancesClient, Instance, InstanceResource> {

    private String zone;
    private String filter;

    /**
     * Zone for an instance.
     */
    public String getZone() {
        return zone;
    }

    public void setZone(String zone) {
        this.zone = zone;
    }

    /**
     * A filter expression that filters results returned. See `example filter rules <https://cloud.google.com/compute/docs/reference/rest/v1/instances/list#body.QUERY_PARAMETERS.filter/>`_.
     */
    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    @Override
    protected List<Instance> findAllGoogle(InstancesClient client) throws Exception {
        try {
            return getInstances(client, null);
        } finally {
            client.close();
        }
    }

    @Override
    protected List<Instance> findGoogle(InstancesClient client, Map<String, String> filters) throws Exception {
        List<Instance> instances = new ArrayList<>();
        String pageToken = null;
        try {
            if (filters.containsKey("zone")) {

                do {
                    ListInstancesRequest.Builder builder = ListInstancesRequest.newBuilder().setProject(getProjectId())
                        .setZone(filters.get("zone")).setFilter(filters.getOrDefault("filter", ""));

                    if (pageToken != null) {
                        builder.setPageToken(pageToken);
                    }

                    InstanceList addressList = client.list(builder.build()).getPage().getResponse();
                    pageToken = addressList.getNextPageToken();

                    instances.addAll(addressList.getItemsList());
                } while (!StringUtils.isEmpty(pageToken));
            } else {
                instances.addAll(getInstances(client, filters.get("filter")));
            }

        } finally {
            client.close();
        }

        return instances;
    }

    private List<Instance> getInstances(InstancesClient client, String filter) {
        List<Instance> instances = new ArrayList<>();
        String pageToken = null;

        do {
            AggregatedListInstancesRequest.Builder builder = AggregatedListInstancesRequest.newBuilder();

            if (pageToken != null) {
                builder.setPageToken(pageToken);
            }

            if (filter != null) {
                builder.setFilter(filter);
            }

            InstanceAggregatedList aggregatedList = client.aggregatedList(builder.setProject(getProjectId()).build())
                .getPage().getResponse();
            pageToken = aggregatedList.getNextPageToken();

            if (aggregatedList.getItemsMap() != null) {
                instances.addAll(aggregatedList.getItemsMap().values().stream()
                    .map(InstancesScopedList::getInstancesList)
                    .filter(Objects::nonNull)
                    .flatMap(Collection::stream)
                    .collect(Collectors.toList()));
            }

        } while (!StringUtils.isEmpty(pageToken));

        return instances;
    }
}
