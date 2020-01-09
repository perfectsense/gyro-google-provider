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
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import com.google.api.services.compute.Compute;
import com.google.api.services.compute.model.ResourcePoliciesScopedList;
import com.google.api.services.compute.model.ResourcePolicy;
import com.google.api.services.compute.model.ResourcePolicyAggregatedList;
import com.google.api.services.compute.model.ResourcePolicyList;
import gyro.core.Type;
import gyro.google.GoogleFinder;

/**
 * Query for resource policies.
 *
 * Example find the address filtering on name equal to 'us-east1-test-two' in the region 'us-east1'.
 * -------
 *
 * .. code-block:: gyro
 *
 *    policies: $(external-query google::compute-resource-policy { filter: 'name = "example-policy-alpha"', region: 'us-central1' })
 *
 */
@Type("compute-resource-policy")
public class ResourcePolicyFinder extends GoogleFinder<Compute, ResourcePolicy, ResourcePolicyResource> {

    @Override
    protected List<ResourcePolicy> findAllGoogle(Compute client) throws Exception {
        List<ResourcePolicy> policies = new ArrayList<>();
        String pageToken = null;

        do {
            ResourcePolicyAggregatedList aggregatedList = client.resourcePolicies().aggregatedList(getProjectId())
                .setPageToken(pageToken)
                .execute();
            pageToken = aggregatedList.getNextPageToken();

            policies.addAll(aggregatedList.getItems().values().stream()
                .map(ResourcePoliciesScopedList::getResourcePolicies)
                .filter(Objects::nonNull)
                .flatMap(List::stream)
                .collect(Collectors.toList()));

        } while (pageToken != null);

        return policies;
    }

    @Override
    protected List<ResourcePolicy> findGoogle(Compute client, Map<String, String> filters) throws Exception {
        List<ResourcePolicy> policies = new ArrayList<>();

        if (filters.containsKey("region")) {
            String pageToken = null;

            do {
                ResourcePolicyList policyList = client.resourcePolicies().list(getProjectId(), filters.get("region"))
                    .setFilter(filters.get("filter"))
                    .setPageToken(pageToken)
                    .execute();
                pageToken = policyList.getNextPageToken();

                policies.addAll(policyList.getItems());

            } while (pageToken != null);
        }

        return policies;
    }
}
