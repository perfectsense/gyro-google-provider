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
import com.google.api.services.compute.model.InstanceGroupManagerList;
import gyro.core.Type;
import gyro.core.validation.Required;
import gyro.google.GoogleFinder;

/**
 * Query an instance group manager.
 *
 * Example
 * -------
 *
 * .. code-block:: gyro
 *
 *    instance-group-manager: $(external-query google::compute-instance-group-manager { name: 'instance-group-manager-example', zone: 'us-central1-a' })
 */
@Type("compute-instance-group-manager")
public class InstanceGroupManagerFinder
    extends GoogleFinder<Compute, InstanceGroupManager, InstanceGroupManagerResource> {

    private String name;

    private String zone;

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
    public String getZone() {
        return zone;
    }

    public void setZone(String zone) {
        this.zone = zone;
    }

    @Override
    protected List<InstanceGroupManager> findAllGoogle(Compute client) throws Exception {
        List<InstanceGroupManager> allInstanceGroupManagers = new ArrayList<>();
        Compute.InstanceGroupManagers.List request = client.instanceGroupManagers().list(getProjectId(), getZone());
        String nextPageToken = null;

        do {
            InstanceGroupManagerList response = request.execute();
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
        return Collections.singletonList(client.instanceGroupManagers()
            .get(getProjectId(), filters.get("zone"), filters.get("name"))
            .execute());
    }
}
