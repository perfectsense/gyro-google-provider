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

import com.google.cloud.compute.v1.ListSslPoliciesRequest;
import com.google.cloud.compute.v1.SslPoliciesClient;
import com.google.cloud.compute.v1.SslPoliciesList;
import com.google.cloud.compute.v1.SslPolicy;
import gyro.core.Type;
import gyro.google.GoogleFinder;
import org.apache.commons.lang3.StringUtils;

/**
 * Query SSL policies.
 *
 * Example
 * -------
 *
 * .. code-block:: gyro
 *
 *    ssl-policy: $(external-query google::compute-ssl-policy { name: 'ssl-policy-example' })
 */
@Type("compute-ssl-policy")
public class SslPolicyFinder extends GoogleFinder<SslPoliciesClient, SslPolicy, SslPolicyResource> {

    private String name;

    /**
     * The name of the SSL policy.
     */
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    protected List<SslPolicy> findAllGoogle(SslPoliciesClient client) throws Exception {
        List<SslPolicy> sslPolicies = new ArrayList<>();
        SslPoliciesList sslPolicyList;
        String nextPageToken = null;

        try {
            do {
                ListSslPoliciesRequest.Builder builder = ListSslPoliciesRequest.newBuilder()
                    .setProject(getProjectId());

                if (nextPageToken != null) {
                    builder.setPageToken(nextPageToken);
                }

                sslPolicyList = client.list(builder.build()).getPage().getResponse();
                nextPageToken = sslPolicyList.getNextPageToken();

                sslPolicies.addAll(sslPolicyList.getItemsList());
            } while (!StringUtils.isEmpty(nextPageToken));

            return sslPolicies;

        } finally {
            client.close();
        }
    }

    @Override
    protected List<SslPolicy> findGoogle(SslPoliciesClient client, Map<String, String> filters) throws Exception {
        return Collections.singletonList(client.get(getProjectId(), filters.get("name")));
    }
}
