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
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import com.google.api.gax.rpc.InvalidArgumentException;
import com.google.api.gax.rpc.NotFoundException;
import com.google.api.gax.rpc.UnaryCallable;
import com.google.cloud.compute.v1.ForwardingRule;
import com.google.cloud.compute.v1.ForwardingRuleList;
import com.google.cloud.compute.v1.GlobalForwardingRulesClient;
import com.google.cloud.compute.v1.ListGlobalForwardingRulesRequest;
import com.psddev.dari.util.StringUtils;
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
public class GlobalForwardingRuleFinder
    extends GoogleFinder<GlobalForwardingRulesClient, ForwardingRule, GlobalForwardingRuleResource> {

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
    protected List<ForwardingRule> findAllGoogle(GlobalForwardingRulesClient client) throws Exception {
        try {
            return getGlobalForwardingRules(client);
        } finally {
            client.close();
        }
    }

    @Override
    protected List<ForwardingRule> findGoogle(GlobalForwardingRulesClient client, Map<String, String> filters)
        throws Exception {
        List<ForwardingRule> forwardingRules = new ArrayList<>();

        try {
            forwardingRules.add(client.get(getProjectId(), filters.get("name")));
        } catch (NotFoundException | InvalidArgumentException ex) {
            // ignore
        } finally {
            client.close();
        }

        return forwardingRules;
    }

    private List<ForwardingRule> getGlobalForwardingRules(GlobalForwardingRulesClient client) {
        List<ForwardingRule> forwardingRules = new ArrayList<>();

        ForwardingRuleList forwardingRuleList;
        String nextPageToken = null;

        do {
            UnaryCallable<ListGlobalForwardingRulesRequest, GlobalForwardingRulesClient.ListPagedResponse> callable =
                client.listPagedCallable();

            ListGlobalForwardingRulesRequest.Builder builder = ListGlobalForwardingRulesRequest.newBuilder();

            if (nextPageToken != null) {
                builder.setPageToken(nextPageToken);
            }

            GlobalForwardingRulesClient.ListPagedResponse pagedResponse = callable.call(builder.setProject(
                getProjectId()).build());
            forwardingRuleList = pagedResponse.getPage().getResponse();
            nextPageToken = pagedResponse.getNextPageToken();

            if (forwardingRuleList.getItemsList() != null) {
                forwardingRules.addAll(forwardingRuleList.getItemsList().stream().filter(Objects::nonNull)
                    .filter(forwardingRule -> forwardingRule.getRegion() != null).collect(Collectors.toList()));
            }

        } while (!StringUtils.isEmpty(nextPageToken));

        return forwardingRules;

    }
}
