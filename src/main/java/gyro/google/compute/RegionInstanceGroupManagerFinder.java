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
import com.google.api.services.compute.model.InstanceGroupManager;
import com.google.api.services.compute.model.RegionInstanceGroupManagerList;
import gyro.core.Type;
import gyro.core.validation.Required;
import gyro.google.GoogleFinder;

/**
 * Query a region instance group manager.
 *
 * Example
 * -------
 *
 * .. code-block:: gyro
 *
 *    region-instance-group-manager: $(external-query google::compute-region-instance-group-manager { name: 'region-instance-group-manager-example', region: 'us-central1' })
 */
@Type("compute-region-instance-group-manager")
public class RegionInstanceGroupManagerFinder
    extends GoogleFinder<Compute, InstanceGroupManager, RegionInstanceGroupManagerResource> {

    private String name;

    private String region;

    /**
     * User assigned name for the instance group manager.
     */
    @Required
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * The zone where the managed instance group is located (for zonal resources).
     */
    @Required
    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    @Override
    protected List<InstanceGroupManager> findAllGoogle(Compute client) throws Exception {
        List<InstanceGroupManager> allInstanceGroupManagers = new ArrayList<>();
        Compute.RegionInstanceGroupManagers.List request = client.regionInstanceGroupManagers()
            .list(getProjectId(), getRegion());
        String nextPageToken = null;

        do {
            RegionInstanceGroupManagerList response = request.execute();
            List<InstanceGroupManager> items = response.getItems();

            if (items == null) {
                break;
            }
            allInstanceGroupManagers.addAll(items);
            nextPageToken = response.getNextPageToken();
            request.setPageToken(nextPageToken);
        } while (nextPageToken != null);
        return allInstanceGroupManagers;
    }

    @Override
    protected List<InstanceGroupManager> findGoogle(Compute client, Map<String, String> filters) throws Exception {
        return Collections.singletonList(client.regionInstanceGroupManagers()
            .get(getProjectId(), filters.get("region"), filters.get("name"))
            .execute());
    }
}
