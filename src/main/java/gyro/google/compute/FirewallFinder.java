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

import com.google.api.gax.rpc.NotFoundException;
import com.google.cloud.compute.v1.Firewall;
import com.google.cloud.compute.v1.FirewallList;
import com.google.cloud.compute.v1.FirewallsClient;
import com.google.cloud.compute.v1.ListFirewallsRequest;
import gyro.core.Type;
import gyro.google.GoogleFinder;
import org.apache.commons.lang3.StringUtils;

/**
 * Query firewall rue.
 *
 * Example
 * -------
 *
 * .. code-block:: gyro
 *
 *    firewall-rule: $(external-query google::compute-firewall-rule { name: 'firewall-rule-example'})
 */
@Type("compute-firewall-rule")
public class FirewallFinder extends GoogleFinder<FirewallsClient, Firewall, FirewallResource> {

    private String name;

    /**
     * The name of the firewall rule.
     */
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    protected List<Firewall> findAllGoogle(FirewallsClient client) throws Exception {
        List<Firewall> firewalls = new ArrayList<>();
        FirewallList firewallList;
        String nextPageToken = null;

        try {
            do {
                ListFirewallsRequest.Builder builder = ListFirewallsRequest.newBuilder().setProject(getProjectId());

                if (nextPageToken != null) {
                    builder.setPageToken(nextPageToken);
                }

                FirewallsClient.ListPagedResponse listPagedResponse = client.list(builder.build());
                firewallList = listPagedResponse.getPage().getResponse();
                nextPageToken = listPagedResponse.getNextPageToken();

                firewalls.addAll(firewallList.getItemsList());
            } while (!StringUtils.isEmpty(nextPageToken));

            return firewalls;
        } finally {
            client.close();
        }
    }

    @Override
    protected List<Firewall> findGoogle(FirewallsClient client, Map<String, String> filters) throws Exception {
        ArrayList<Firewall> firewalls = new ArrayList<>();

        try {
            firewalls.add(client.get(getProjectId(), filters.get("name")));
        } catch (NotFoundException ex) {
            // ignore
        } finally {
            client.close();
        }

        return firewalls;
    }
}
