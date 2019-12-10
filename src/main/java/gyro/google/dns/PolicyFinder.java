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

package gyro.google.dns;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.google.api.services.dns.Dns;
import com.google.api.services.dns.model.PoliciesListResponse;
import com.google.api.services.dns.model.Policy;
import gyro.core.Type;
import gyro.google.GoogleFinder;

/**
 * Query DNS Policy.
 *
 * Example
 * -------
 *
 * .. code-block:: gyro
 *
 *    policy: $(external-query google::dns-policy { name: 'policy-example' })
 */
@Type("dns-policy")
public class PolicyFinder extends GoogleFinder<Dns, Policy, PolicyResource> {

    private String name;

    /**
     * User given friendly name of the policy.
     */
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    protected List<Policy> findAllGoogle(Dns client) throws Exception {
        List<Policy> allPolicies = new ArrayList<>();
        Dns.Policies.List request = client.policies().list(getProjectId());
        String nextPageToken = null;

        do {
            PoliciesListResponse response = request.execute();
            allPolicies.addAll(response.getPolicies());
            nextPageToken = response.getNextPageToken();
            request.setPageToken(nextPageToken);
        } while (nextPageToken != null);
        return allPolicies;
    }

    @Override
    protected List<Policy> findGoogle(Dns client, Map<String, String> filters) throws Exception {
        return Collections.singletonList(client.policies().get(getProjectId(), filters.get("name")).execute());
    }
}
