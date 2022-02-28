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
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import com.google.api.gax.rpc.NotFoundException;
import com.google.api.gax.rpc.UnaryCallable;
import com.google.cloud.compute.v1.AggregatedListTargetPoolsRequest;
import com.google.cloud.compute.v1.ListTargetPoolsRequest;
import com.google.cloud.compute.v1.TargetPool;
import com.google.cloud.compute.v1.TargetPoolAggregatedList;
import com.google.cloud.compute.v1.TargetPoolList;
import com.google.cloud.compute.v1.TargetPoolsClient;
import com.google.cloud.compute.v1.TargetPoolsScopedList;
import com.psddev.dari.util.StringUtils;
import gyro.core.Type;
import gyro.google.GoogleFinder;

/**
 * Query target pool.
 *
 * Example
 * -------
 *
 * .. code-block:: gyro
 *
 *    target-pool: $(external-query google::compute-target-pool { name: 'target-pool-example', region: 'us-central1'})
 */
@Type("compute-target-pool")
public class TargetPoolFinder extends GoogleFinder<TargetPoolsClient, TargetPool, TargetPoolResource> {

    private String name;
    private String region;

    /**
     * The name of the target pool.
     */
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * The region of the target pool.
     */
    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    @Override
    protected List<TargetPool> findAllGoogle(TargetPoolsClient client) throws Exception {
        try {
            return getTargetPools(client);
        } finally {
            client.close();
        }
    }

    @Override
    protected List<TargetPool> findGoogle(TargetPoolsClient client, Map<String, String> filters)
        throws Exception {
        List<TargetPool> targetPools = new ArrayList<>();

        try {
            if (filters.containsKey("name") && filters.containsKey("region")) {
                targetPools = Collections.singletonList(client.get(getProjectId(), filters.get("region"),
                    filters.get("name")));

            } else if (filters.containsKey("region")) {
                TargetPoolList targetPoolList;
                String nextPageToken = null;

                do {
                    UnaryCallable<ListTargetPoolsRequest, TargetPoolsClient.ListPagedResponse> callable = client
                        .listPagedCallable();

                    ListTargetPoolsRequest.Builder builder = ListTargetPoolsRequest.newBuilder()
                        .setRegion(filters.get("region"));

                    if (nextPageToken != null) {
                        builder.setPageToken(nextPageToken);
                    }

                    TargetPoolsClient.ListPagedResponse pagedResponse = callable.call(builder.setProject(
                        getProjectId())
                        .build());
                    targetPoolList = pagedResponse.getPage().getResponse();
                    nextPageToken = pagedResponse.getNextPageToken();

                    targetPools.addAll(targetPoolList.getItemsList().stream()
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList()));
                } while (!StringUtils.isEmpty(nextPageToken));

            } else {
                targetPools.addAll(getTargetPools(client));
                targetPools.removeIf(d -> !d.getName().equals(filters.get("name")));
            }
        } catch (NotFoundException ex) {
            // ignore
        } finally {
            client.close();
        }

        return targetPools;
    }

    private List<TargetPool> getTargetPools(TargetPoolsClient client) {
        List<TargetPool> targetPools = new ArrayList<>();
        TargetPoolAggregatedList targetPoolList;
        String nextPageToken = null;

        do {
            AggregatedListTargetPoolsRequest.Builder builder = AggregatedListTargetPoolsRequest.newBuilder()
                .setProject(getProjectId());

            if (nextPageToken != null) {
                builder.setPageToken(nextPageToken);
            }

            TargetPoolsClient.AggregatedListPagedResponse aggregatedListPagedResponse = client.aggregatedList(
                builder.build());
            targetPoolList = aggregatedListPagedResponse.getPage().getResponse();
            nextPageToken = aggregatedListPagedResponse.getNextPageToken();

            targetPools.addAll(targetPoolList.getItemsMap().values().stream()
                .map(TargetPoolsScopedList::getTargetPoolsList)
                .filter(Objects::nonNull)
                .flatMap(Collection::stream)
                .filter(targetPool -> targetPool.getRegion() != null)
                .collect(Collectors.toList()));

        } while (!StringUtils.isEmpty(nextPageToken));

        return targetPools;
    }
}
