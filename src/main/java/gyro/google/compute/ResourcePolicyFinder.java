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
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import com.google.api.gax.rpc.UnaryCallable;
import com.google.cloud.compute.v1.AggregatedListResourcePoliciesRequest;
import com.google.cloud.compute.v1.ListResourcePoliciesRequest;
import com.google.cloud.compute.v1.ResourcePoliciesClient;
import com.google.cloud.compute.v1.ResourcePoliciesScopedList;
import com.google.cloud.compute.v1.ResourcePolicy;
import com.google.cloud.compute.v1.ResourcePolicyAggregatedList;
import com.google.cloud.compute.v1.ResourcePolicyList;
import com.psddev.dari.util.StringUtils;
import gyro.core.Type;
import gyro.google.GoogleFinder;

/**
 * Query for resource policies.
 *
 * Example
 * -------
 *
 * .. code-block:: gyro
 *
 *    policies: $(external-query google::compute-resource-policy { filter: 'name = "example-policy-alpha"', region: 'us-central1' })
 *
 */
@Type("compute-resource-policy")
public class ResourcePolicyFinder extends GoogleFinder<ResourcePoliciesClient, ResourcePolicy, ResourcePolicyResource> {

    private String region;
    private String filter;

    /**
     * Name of the region for this request.
     */
    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    /**
     * A filter expression that filters results returned. See `example filter rules <https://cloud.google.com/compute/docs/reference/rest/v1/resourcePolicies/list#body.QUERY_PARAMETERS.filter/>`_.
     */
    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    @Override
    protected List<ResourcePolicy> findAllGoogle(ResourcePoliciesClient client) throws Exception {
        try {
            return getResourcePolicies(client, null);
        } finally {
            client.close();
        }
    }

    @Override
    protected List<ResourcePolicy> findGoogle(ResourcePoliciesClient client, Map<String, String> filters)
        throws Exception {
        List<ResourcePolicy> addresses = new ArrayList<>();
        String pageToken = null;
        try {
            if (filters.containsKey("region")) {

                do {
                    ListResourcePoliciesRequest.Builder builder = ListResourcePoliciesRequest.newBuilder()
                        .setProject(getProjectId())
                        .setRegion(filters.get("region"))
                        .setFilter(filters.getOrDefault("filter", ""));

                    if (pageToken != null) {
                        builder.setPageToken(pageToken);
                    }

                    ResourcePolicyList addressList = client.list(builder.build()).getPage().getResponse();
                    pageToken = addressList.getNextPageToken();

                    if (addressList.getItemsList() != null) {
                        addresses.addAll(addressList.getItemsList());
                    }

                } while (!StringUtils.isEmpty(pageToken));
            } else {
                return getResourcePolicies(client, filters.get("filter"));
            }

        } finally {
            client.close();
        }
        return addresses;
    }

    private List<ResourcePolicy> getResourcePolicies(ResourcePoliciesClient client, String filter) {
        List<ResourcePolicy> addresses = new ArrayList<>();
        String pageToken = null;

        do {
            UnaryCallable<AggregatedListResourcePoliciesRequest, ResourcePolicyAggregatedList> callable = client
                .aggregatedListCallable();
            AggregatedListResourcePoliciesRequest.Builder builder = AggregatedListResourcePoliciesRequest.newBuilder();

            if (pageToken != null) {
                builder.setPageToken(pageToken);
            }

            if (filter != null) {
                builder.setFilter(filter);
            }

            ResourcePolicyAggregatedList aggregatedList = callable.call(builder.setProject(getProjectId()).build());
            pageToken = aggregatedList.getNextPageToken();

            if (aggregatedList.getItemsMap() != null) {
                addresses.addAll(aggregatedList.getItemsMap().values().stream()
                    .map(ResourcePoliciesScopedList::getResourcePoliciesList)
                    .filter(Objects::nonNull)
                    .flatMap(Collection::stream)
                    .collect(Collectors.toList()));
            }

        } while (!StringUtils.isEmpty(pageToken));

        return addresses;
    }
}
