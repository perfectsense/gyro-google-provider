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
import com.google.api.services.compute.model.Autoscaler;
import com.google.api.services.compute.model.RegionAutoscalerList;
import com.psddev.dari.util.ObjectUtils;
import com.psddev.dari.util.StringUtils;
import gyro.core.GyroException;
import gyro.core.Type;
import gyro.google.GoogleFinder;
import gyro.google.util.Utils;

/**
 * Query a region autoscaler.
 *
 * You can provide an expression that filters resources. The expression must specify the field name, and the value that you want to use for filtering.
 *
 * Please see :doc:`compute-region-autoscaler` resource for available fields.
 *
 * Example
 * -------
 *
 * .. code-block:: gyro
 *
 *    region-autoscaler: $(external-query google::compute-region-autoscaler { name: 'compute-region-autoscaler-example', region: 'us-central1' })
 */
@Type("compute-region-autoscaler")
public class RegionAutoscalerFinder extends GoogleFinder<Compute, Autoscaler, RegionAutoscalerResource> {

    @Override
    protected List<Autoscaler> findAllGoogle(Compute client) throws Exception {
        return AutoscalerFinder.findAllAutoscalers(client, getProjectId(), ResourceScope.REGION, null);
    }

    @Override
    protected List<Autoscaler> findGoogle(Compute client, Map<String, String> filters) throws Exception {
        if (filters.containsKey("zone")) {
            throw new GyroException("For zonal autoscaler, use 'compute-autoscaler' instead.");
        }

        String region = filters.remove("region");

        if (StringUtils.isBlank(region) || ObjectUtils.isBlank(filters)) {
            return AutoscalerFinder.findAllAutoscalers(client, getProjectId(), ResourceScope.REGION, filters);
        }
        Compute.RegionAutoscalers regionAutoscalers = client.regionAutoscalers();
        List<Autoscaler> allAutoscalers = new ArrayList<>();

        Compute.RegionAutoscalers.List request = regionAutoscalers.list(getProjectId(), region);
        request.setFilter(Utils.convertToFilters(filters));
        String nextPageToken = null;

        do {
            RegionAutoscalerList response = request.execute();
            List<Autoscaler> items = response.getItems();

            if (items == null) {
                break;
            }
            allAutoscalers.addAll(items);
            nextPageToken = response.getNextPageToken();
            request.setPageToken(nextPageToken);
        } while (nextPageToken != null);
        return allAutoscalers;
    }
}
