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

import com.google.api.services.compute.Compute;
import com.google.cloud.compute.v1.SslPoliciesList;
import com.google.cloud.compute.v1.SslPolicy;
import gyro.core.Type;
import gyro.google.GoogleFinder;

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
public class SslPolicyFinder extends GoogleFinder<Compute, SslPolicy, SslPolicyResource> {

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
    protected List<SslPolicy> findAllGoogle(Compute client) throws Exception {
        List<SslPolicy> sslPolicies = new ArrayList<>();
        SslPoliciesList sslPolicyList;
        String nextPageToken = null;

        do {
            sslPolicyList = client.sslPolicies().list(getProjectId()).setPageToken(nextPageToken).execute();
            if (sslPolicyList.getItems() != null) {
                sslPolicies.addAll(sslPolicyList.getItems());
            }
            nextPageToken = sslPolicyList.getNextPageToken();
        } while (nextPageToken != null);

        return sslPolicies;
    }

    @Override
    protected List<SslPolicy> findGoogle(Compute client, Map<String, String> filters) throws Exception {
        return Collections.singletonList(client.sslPolicies().get(getProjectId(), filters.get("name")).execute());
    }
}
