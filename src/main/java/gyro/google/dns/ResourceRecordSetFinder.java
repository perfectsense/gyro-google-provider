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
import com.google.api.services.dns.model.ResourceRecordSet;
import com.google.api.services.dns.model.ResourceRecordSetsListResponse;
import gyro.core.Type;
import gyro.google.GoogleFinder;

/**
 * Query resouce record set.
 *
 * Example
 * -------
 *
 * .. code-block:: gyro
 *
 *    resource-record-set: $(external-query google::dns-resource-record-set { name: 'managed-zone-example-private' })
 */
@Type("dns-resource-record-set")
public class ResourceRecordSetFinder extends GoogleFinder<Dns, ResourceRecordSet, ResourceRecordSetResource> {

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
    protected List<ResourceRecordSet> findAllGoogle(Dns client) throws Exception {
        // TODO: return all resource record sets through out all zones?
        return Collections.emptyList();
    }

    @Override
    protected List<ResourceRecordSet> findGoogle(Dns client, Map<String, String> filters) throws Exception {
        List<ResourceRecordSet> allRecordSets = new ArrayList<>();
        Dns.ResourceRecordSets.List request = client.resourceRecordSets().list(getProjectId(), filters.get("name"));
        String nextPageToken = null;

        do {
            ResourceRecordSetsListResponse response = request.execute();
            allRecordSets.addAll(response.getRrsets());
            nextPageToken = response.getNextPageToken();
            request.setPageToken(nextPageToken);
        } while (nextPageToken != null);
        return allRecordSets;
    }
}
