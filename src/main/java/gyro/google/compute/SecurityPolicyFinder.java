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
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.google.cloud.compute.v1.ListSecurityPoliciesRequest;
import com.google.cloud.compute.v1.SecurityPoliciesClient;
import com.google.cloud.compute.v1.SecurityPolicy;
import com.google.cloud.compute.v1.SecurityPolicyList;
import com.psddev.dari.util.StringUtils;
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
public class SecurityPolicyFinder extends GoogleFinder<SecurityPoliciesClient, SecurityPolicy, SecurityPolicyResource> {

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
    protected List<SecurityPolicy> findAllGoogle(SecurityPoliciesClient client) throws Exception {
        List<SecurityPolicy> securityPolicies = new ArrayList<>();
        SecurityPolicyList securityPolicyList;
        String nextPageToken = null;

        try {
            do {
                ListSecurityPoliciesRequest.Builder builder = ListSecurityPoliciesRequest.newBuilder()
                    .setProject(getProjectId());

                if (nextPageToken != null) {
                    builder.setPageToken(nextPageToken);
                }

                securityPolicyList = client.list(builder.build()).getPage().getResponse();
                nextPageToken = securityPolicyList.getNextPageToken();

                securityPolicies.addAll(securityPolicyList.getItemsList());
            } while (!StringUtils.isEmpty(nextPageToken));

            return securityPolicies;

        } finally {
            client.close();
        }
    }

    @Override
    protected List<SecurityPolicy> findGoogle(SecurityPoliciesClient client, Map<String, String> filters)
        throws Exception {
        return Collections.singletonList(client.get(getProjectId(), filters.get("name")));
    }
}
