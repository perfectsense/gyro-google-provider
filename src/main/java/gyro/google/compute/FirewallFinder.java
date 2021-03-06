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
import com.google.api.services.compute.model.Firewall;
import com.google.api.services.compute.model.FirewallList;
import gyro.core.Type;
import gyro.google.GoogleFinder;

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
public class FirewallFinder extends GoogleFinder<Compute, Firewall, FirewallResource> {

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
    protected List<Firewall> findAllGoogle(Compute client) throws Exception {
        List<Firewall> firewalls = new ArrayList<>();
        FirewallList firewallList;
        String nextPageToken = null;

        do {
            firewallList = client.firewalls().list(getProjectId()).setPageToken(nextPageToken).execute();
            nextPageToken = firewallList.getNextPageToken();

            if (firewallList.getItems() != null) {
                firewalls.addAll(firewallList.getItems());
            }
        } while (nextPageToken != null);

        return firewalls;
    }

    @Override
    protected List<Firewall> findGoogle(Compute client, Map<String, String> filters) throws Exception {
        return Collections.singletonList(client.firewalls().get(getProjectId(), filters.get("name")).execute());
    }
}
