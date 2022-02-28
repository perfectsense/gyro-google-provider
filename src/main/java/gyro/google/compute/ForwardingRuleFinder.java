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
import com.google.cloud.compute.v1.AggregatedListForwardingRulesRequest;
import com.google.cloud.compute.v1.ForwardingRule;
import com.google.cloud.compute.v1.ForwardingRuleAggregatedList;
import com.google.cloud.compute.v1.ForwardingRuleList;
import com.google.cloud.compute.v1.ForwardingRulesClient;
import com.google.cloud.compute.v1.ForwardingRulesScopedList;
import com.google.cloud.compute.v1.ListForwardingRulesRequest;
import com.psddev.dari.util.StringUtils;
import gyro.core.Type;
import gyro.google.GoogleFinder;

/**
 * Query for a forwarding rule.
 *
 * Example
 * -------
 *
 * .. code-block:: gyro
 *
 *    compute-forwarding-rule: $(external-query google::compute-forwarding-rule { name: 'forwarding-rule-example', region: 'us-central1' })
 */
@Type("compute-forwarding-rule")
public class ForwardingRuleFinder extends GoogleFinder<ForwardingRulesClient, ForwardingRule, ForwardingRuleResource> {

    private String name;
    private String region;

    /**
     * The name of the forwarding rule.
     */
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * The region of the forwarding rule.
     */
    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    @Override
    protected List<ForwardingRule> findAllGoogle(ForwardingRulesClient client) throws Exception {
        try {
            return getForwardingRules(client);
        } finally {
            client.close();
        }
    }

    @Override
    protected List<ForwardingRule> findGoogle(ForwardingRulesClient client, Map<String, String> filters)
        throws Exception {
        List<ForwardingRule> forwardingRules = new ArrayList<>();

        try {
            if (filters.containsKey("name") && filters.containsKey("region")) {
                forwardingRules = Collections.singletonList(client.get(getProjectId(), filters.get("region"),
                    filters.get("name")));

            } else if (filters.containsKey("region")) {
                ForwardingRuleList forwardingRuleList;
                String nextPageToken = null;

                do {
                    UnaryCallable<ListForwardingRulesRequest, ForwardingRulesClient.ListPagedResponse> callable = client
                        .listPagedCallable();

                    ListForwardingRulesRequest.Builder builder = ListForwardingRulesRequest.newBuilder()
                        .setRegion(filters.get("region"));

                    if (nextPageToken != null) {
                        builder.setPageToken(nextPageToken);
                    }

                    ForwardingRulesClient.ListPagedResponse pagedResponse = callable.call(builder.setProject(
                        getProjectId())
                        .build());
                    forwardingRuleList = pagedResponse.getPage().getResponse();
                    nextPageToken = pagedResponse.getNextPageToken();

                    if (forwardingRuleList.getItemsList() != null) {
                        forwardingRules.addAll(forwardingRuleList.getItemsList().stream().filter(Objects::nonNull)
                            .filter(forwardingRule -> forwardingRule.getRegion() != null).collect(Collectors.toList()));
                    }

                } while (!StringUtils.isEmpty(nextPageToken));

                return forwardingRules;

            } else {
                forwardingRules.addAll(getForwardingRules(client));
                forwardingRules.removeIf(d -> !d.getName().equals(filters.get("name")));
            }
        } catch (NotFoundException ex) {
            // ignore
        } finally {
            client.close();
        }

        return forwardingRules;
    }

    private List<ForwardingRule> getForwardingRules(ForwardingRulesClient client) {
        List<ForwardingRule> forwardingRules = new ArrayList<>();
        ForwardingRuleAggregatedList forwardingRuleList;
        String nextPageToken = null;

        do {
            AggregatedListForwardingRulesRequest.Builder builder = AggregatedListForwardingRulesRequest.newBuilder()
                .setProject(getProjectId());

            if (nextPageToken != null) {
                builder.setPageToken(nextPageToken);
            }

            ForwardingRulesClient.AggregatedListPagedResponse aggregatedListPagedResponse = client.aggregatedList(
                builder.build());
            forwardingRuleList = aggregatedListPagedResponse.getPage().getResponse();
            nextPageToken = aggregatedListPagedResponse.getNextPageToken();

            forwardingRules.addAll(forwardingRuleList.getItemsMap().values().stream()
                .map(ForwardingRulesScopedList::getForwardingRulesList)
                .filter(Objects::nonNull)
                .flatMap(Collection::stream)
                .filter(forwardingRule -> forwardingRule.getRegion() != null)
                .collect(Collectors.toList()));

        } while (nextPageToken != null);

        return forwardingRules;
    }
}
