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
import java.util.Optional;
import java.util.stream.Collectors;

import com.google.api.services.compute.Compute;
import com.google.api.services.compute.model.ForwardingRule;
import com.google.api.services.compute.model.ForwardingRuleAggregatedList;
import com.google.api.services.compute.model.ForwardingRulesScopedList;
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
public class ForwardingRuleFinder extends GoogleFinder<Compute, ForwardingRule, ForwardingRuleResource> {

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
    protected List<ForwardingRule> findAllGoogle(Compute client) throws Exception {
        List<ForwardingRule> forwardingRules = new ArrayList<>();
        ForwardingRuleAggregatedList forwardingRuleList;
        String nextPageToken = null;

        do {
            forwardingRuleList = client.forwardingRules()
                .aggregatedList(getProjectId())
                .setPageToken(nextPageToken)
                .execute();
            forwardingRules.addAll(forwardingRuleList
                .getItems().values().stream()
                .map(ForwardingRulesScopedList::getForwardingRules)
                .filter(Objects::nonNull)
                .flatMap(Collection::stream)
                .filter(forwardingRule -> forwardingRule.getRegion() != null)
                .collect(Collectors.toList()));
            nextPageToken = forwardingRuleList.getNextPageToken();
        } while (nextPageToken != null);

        return forwardingRules;
    }

    @Override
    protected List<ForwardingRule> findGoogle(Compute client, Map<String, String> filters) throws Exception {
        List<ForwardingRule> forwardingRules;
        if (filters.containsKey("name")) {
            forwardingRules = Collections.singletonList(client.forwardingRules()
                .get(getProjectId(), filters.get("region"), filters.get("name"))
                .execute());
        } else {
            forwardingRules = Optional.ofNullable(
                client.forwardingRules().list(getProjectId(), filters.get("region")).execute().getItems())
                .orElse(new ArrayList<>());
        }

        return forwardingRules;
    }
}
