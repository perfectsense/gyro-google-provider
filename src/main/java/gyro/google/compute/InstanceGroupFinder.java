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

import com.google.api.gax.rpc.NotFoundException;
import com.google.api.gax.rpc.UnaryCallable;
import com.google.cloud.compute.v1.AggregatedListInstanceGroupsRequest;
import com.google.cloud.compute.v1.InstanceGroup;
import com.google.cloud.compute.v1.InstanceGroupAggregatedList;
import com.google.cloud.compute.v1.InstanceGroupList;
import com.google.cloud.compute.v1.InstanceGroupsClient;
import com.google.cloud.compute.v1.InstanceGroupsScopedList;
import com.google.cloud.compute.v1.ListInstanceGroupsRequest;
import com.psddev.dari.util.StringUtils;
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
public class InstanceGroupFinder extends GoogleFinder<InstanceGroupsClient, InstanceGroup, InstanceGroupResource> {

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
    protected List<InstanceGroup> findAllGoogle(InstanceGroupsClient client) throws Exception {
        try {
            return getInstanceGroups(client);
        } finally {
            client.close();
        }
    }

    @Override
    protected List<InstanceGroup> findGoogle(InstanceGroupsClient client, Map<String, String> filters)
        throws Exception {
        List<InstanceGroup> instanceGroups = new ArrayList<>();

        try {
            if (filters.containsKey("name") && filters.containsKey("zone")) {
                instanceGroups = Collections.singletonList(client.get(getProjectId(), filters.get("zone"),
                    filters.get("name")));

            } else if (filters.containsKey("zone")) {
                InstanceGroupList forwardingRuleList;
                String nextPageToken = null;

                do {
                    UnaryCallable<ListInstanceGroupsRequest, InstanceGroupsClient.ListPagedResponse> callable = client
                        .listPagedCallable();

                    ListInstanceGroupsRequest.Builder builder = ListInstanceGroupsRequest.newBuilder()
                        .setZone(filters.get("zone"));

                    if (nextPageToken != null) {
                        builder.setPageToken(nextPageToken);
                    }

                    InstanceGroupsClient.ListPagedResponse pagedResponse = callable.call(builder.setProject(
                        getProjectId())
                        .build());
                    forwardingRuleList = pagedResponse.getPage().getResponse();
                    nextPageToken = pagedResponse.getNextPageToken();

                    instanceGroups.addAll(forwardingRuleList.getItemsList().stream()
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList()));
                } while (!StringUtils.isEmpty(nextPageToken));

            } else {
                instanceGroups.addAll(getInstanceGroups(client));
                instanceGroups.removeIf(d -> !d.getName().equals(filters.get("name")));
            }
        } catch (NotFoundException ex) {
            // ignore
        } finally {
            client.close();
        }

        return instanceGroups;
    }

    private List<InstanceGroup> getInstanceGroups(InstanceGroupsClient client) {
        List<InstanceGroup> instanceGroups = new ArrayList<>();
        String pageToken = null;

        do {
            UnaryCallable<AggregatedListInstanceGroupsRequest, InstanceGroupAggregatedList> callable = client
                .aggregatedListCallable();
            AggregatedListInstanceGroupsRequest.Builder builder = AggregatedListInstanceGroupsRequest.newBuilder();

            if (pageToken != null) {
                builder.setPageToken(pageToken);
            }

            InstanceGroupAggregatedList aggregatedList = callable.call(builder.setProject(getProjectId()).build());
            pageToken = aggregatedList.getNextPageToken();

            if (aggregatedList.getItemsMap() != null) {
                instanceGroups.addAll(aggregatedList.getItemsMap().values().stream()
                    .map(InstanceGroupsScopedList::getInstanceGroupsList)
                    .filter(Objects::nonNull)
                    .flatMap(Collection::stream)
                    .collect(Collectors.toList()));
            }

        } while (!StringUtils.isEmpty(pageToken));

        return instanceGroups;
    }
}
