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
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.google.api.services.compute.Compute;
import com.google.api.services.compute.model.ForwardingRule;
import com.google.api.services.compute.model.ForwardingRuleList;
import gyro.core.Type;
import gyro.google.GoogleFinder;

/**
 * Query for a global forwarding rule.
 *
 * Example
 * -------
 *
 * .. code-block:: gyro
 *
 *    compute-global-forwarding-rule: $(external-query google::compute-global-forwarding-rule { name: 'global-forwarding-rule-example' })
 */
@Type("compute-global-forwarding-rule")
public class GlobalForwardingRuleFinder extends GoogleFinder<Compute, ForwardingRule, GlobalForwardingRuleResource> {

    private String name;

    /**
     * Name of the global forwarding rule.
     */
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    protected List<ForwardingRule> findAllGoogle(Compute client) throws Exception {
        List<ForwardingRule> forwardingRules = new ArrayList<>();
        String nextPageToken = null;
        ForwardingRuleList forwardingRuleList;

        do {
            forwardingRuleList = client.globalForwardingRules()
                .list(getProjectId())
                .setPageToken(nextPageToken)
                .execute();

            forwardingRules.addAll(forwardingRuleList.getItems());
            nextPageToken = forwardingRuleList.getNextPageToken();
        } while (nextPageToken != null);

        return forwardingRules;
    }

    @Override
    protected List<ForwardingRule> findGoogle(Compute client, Map<String, String> filters) throws Exception {
        return Collections.singletonList(client.globalForwardingRules()
            .get(getProjectId(), filters.get("name"))
            .execute());
    }
}
