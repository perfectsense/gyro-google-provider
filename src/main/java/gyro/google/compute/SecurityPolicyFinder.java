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

import com.google.api.services.compute.Compute;
import com.google.api.services.compute.model.SecurityPolicy;
import com.google.api.services.compute.model.SecurityPolicyList;
import gyro.core.Type;
import gyro.google.GoogleFinder;

/**
 * Query for security policies..
 *
 * Example
 * --------
 *
 * .. code-block:: gyro
 *
 *      compute-security-policy: $(external-query google::compute-security-policy { name: "security-policy-example" })
 */
@Type("compute-security-policy")
public class SecurityPolicyFinder extends GoogleFinder<Compute, SecurityPolicy, SecurityPolicyResource> {

    private String name;

    /**
     * The name of the security policy.
     */
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    protected List<SecurityPolicy> findAllGoogle(Compute client) throws Exception {
        List<SecurityPolicy> securityPolicies = new ArrayList<>();
        SecurityPolicyList securityPolicyList;
        String nextPageToken = null;
        do {
            securityPolicyList = client.securityPolicies().list(getProjectId()).setPageToken(nextPageToken).execute();
            if (securityPolicyList.getItems() != null) {
                securityPolicies.addAll(securityPolicyList.getItems());
                nextPageToken = securityPolicyList.getNextPageToken();
            }
        } while (nextPageToken != null);

        return securityPolicies;
    }

    @Override
    protected List<SecurityPolicy> findGoogle(Compute client, Map<String, String> filters) throws Exception {
        List<SecurityPolicy> securityPolicies = new ArrayList<>();
        if (filters.containsKey("name")) {
            securityPolicies.add(client.securityPolicies()
                .get(getProjectId(), filters.get("name"))
                .execute());
        }

        return securityPolicies;
    }
}
