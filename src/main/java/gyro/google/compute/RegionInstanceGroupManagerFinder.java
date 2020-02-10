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
import com.google.api.services.compute.model.InstanceGroupManager;
import com.google.api.services.compute.model.RegionInstanceGroupManagerList;
import com.psddev.dari.util.ObjectUtils;
import gyro.core.GyroException;
import gyro.core.Type;
import gyro.google.GoogleFinder;
import gyro.google.util.Utils;

/**
 * Query a region instance group manager.
 *
 * You can provide an expression that filters resources. The expression must specify the field name, and the value that you want to use for filtering.
 *
 * Please see :doc:`compute-region-instance-group-manager` resource for available fields.
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

    @Override
    protected List<InstanceGroupManager> findAllGoogle(Compute client) throws Exception {
        return InstanceGroupManagerFinder.findAllInstanceGroupManagers(
            client,
            getProjectId(),
            ResourceScope.REGION,
            null);
    }

    @Override
    protected List<InstanceGroupManager> findGoogle(Compute client, Map<String, String> filters) throws Exception {
        if (filters.containsKey("zone")) {
            throw new GyroException("For zonal instance group manager, use 'compute-instance-group-manager' instead.");
        }

        String region = filters.remove("region");

        if (region == null && !ObjectUtils.isBlank(filters)) {
            return InstanceGroupManagerFinder.findAllInstanceGroupManagers(
                client,
                getProjectId(),
                ResourceScope.REGION,
                filters);
        }
        Compute.RegionInstanceGroupManagers regionInstanceGroupManagers = client.regionInstanceGroupManagers();
        List<InstanceGroupManager> allInstanceGroupManagers = new ArrayList<>();

        Compute.RegionInstanceGroupManagers.List request = regionInstanceGroupManagers.list(getProjectId(), region);
        request.setFilter(Utils.convertToFilters(filters));
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
}
