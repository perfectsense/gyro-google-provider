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
import com.google.api.services.compute.model.Autoscaler;
import com.google.api.services.compute.model.AutoscalerList;
import gyro.core.Type;
import gyro.core.validation.Required;
import gyro.google.GoogleFinder;

/**
 * Query an autoscaler.
 *
 * Example
 * -------
 *
 * .. code-block:: gyro
 *
 *    autoscaler: $(external-query google::compute-autoscaler { name: 'compute-autoscaler-example', zone: 'us-central1-a' })
 */
@Type("compute-autoscaler")
public class AutoscalerFinder
    extends GoogleFinder<Compute, Autoscaler, AutoscalerResource> {

    private String name;

    private String zone;

    /**
     * User assigned name for the autoscaler.
     */
    @Required
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * The zone where the autoscaler is located.
     */
    @Required
    public String getZone() {
        return zone;
    }

    public void setZone(String zone) {
        this.zone = zone;
    }

    @Override
    protected List<Autoscaler> findAllGoogle(Compute client) throws Exception {
        List<Autoscaler> allAutoscalers = new ArrayList<>();
        Compute.Autoscalers.List request = client.autoscalers().list(getProjectId(), getZone());
        String nextPageToken = null;

        do {
            AutoscalerList response = request.execute();
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

    @Override
    protected List<Autoscaler> findGoogle(Compute client, Map<String, String> filters) throws Exception {
        return Collections.singletonList(client.autoscalers()
            .get(getProjectId(), filters.get("zone"), filters.get("name"))
            .execute());
    }
}
