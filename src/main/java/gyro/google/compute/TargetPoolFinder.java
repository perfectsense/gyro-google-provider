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

import com.google.api.services.compute.Compute;
import com.google.api.services.compute.model.TargetPool;
import com.google.api.services.compute.model.TargetPoolAggregatedList;
import com.google.api.services.compute.model.TargetPoolsScopedList;
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
public class TargetPoolFinder extends GoogleFinder<Compute, TargetPool, TargetPoolResource> {

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
    protected List<TargetPool> findAllGoogle(Compute client) throws Exception {
        List<TargetPool> targetPools = new ArrayList<>();
        TargetPoolAggregatedList targetPoolList;
        String nextPageToken = null;

        do {
            targetPoolList = client.targetPools().aggregatedList(getProjectId()).setPageToken(nextPageToken).execute();
            targetPools.addAll(targetPoolList.getItems().values().stream()
                .map(TargetPoolsScopedList::getTargetPools)
                .filter(Objects::nonNull)
                .flatMap(Collection::stream)
                .collect(Collectors.toList()));
            nextPageToken = targetPoolList.getNextPageToken();
        } while (nextPageToken != null);

        return targetPools;
    }

    @Override
    protected List<TargetPool> findGoogle(Compute client, Map<String, String> filters) throws Exception {
        return Collections.singletonList(
            client.targetPools().get(getProjectId(), filters.get("region"), filters.get("name")).execute());
    }
}
