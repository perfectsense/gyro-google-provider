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
import com.google.api.services.dns.model.ManagedZone;
import com.google.api.services.dns.model.ManagedZonesListResponse;
import gyro.core.Type;
import gyro.google.GoogleFinder;

/**
 * Query managed zone.
 *
 * Example
 * -------
 *
 * .. code-block:: gyro
 *
 *    managed-zone: $(external-query google::dns-managed-zone { name: 'managed-zone-example-private' })
 */
@Type("dns-managed-zone")
public class ManagedZoneFinder extends GoogleFinder<Dns, ManagedZone, ManagedZoneResource> {

    private String name;

    /**
     * User assigned name for the managed zone.
     */
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    protected List<ManagedZone> findAllGoogle(Dns client) throws Exception {
        List<ManagedZone> allZones = new ArrayList<>();
        Dns.ManagedZones.List request = client.managedZones().list(getProjectId());
        String nextPageToken = null;

        do {
            ManagedZonesListResponse response = request.execute();
            allZones.addAll(response.getManagedZones());
            nextPageToken = response.getNextPageToken();
            request.setPageToken(nextPageToken);
        } while (nextPageToken != null);
        return allZones;
    }

    @Override
    protected List<ManagedZone> findGoogle(Dns client, Map<String, String> filters) throws Exception {
        return Collections.singletonList(client.managedZones().get(getProjectId(), filters.get("name")).execute());
    }
}
