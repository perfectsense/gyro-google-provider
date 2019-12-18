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
import java.util.stream.Collectors;

import com.google.api.services.compute.Compute;
import com.google.api.services.compute.model.Instance;
import com.google.api.services.compute.model.InstanceList;
import com.google.api.services.compute.model.Zone;
import gyro.core.Type;
import gyro.google.GoogleFinder;

/**
 * Query instance.
 *
 * Example
 * -------
 *
 * .. code-block:: gyro
 *
 *    instance: $(external-query google::instance {zone: 'us-west1-a', filter: 'name = gyro-development'})
 */
@Type("instance")
public class InstanceFinder extends GoogleFinder<Compute, Instance, InstanceResource> {

    @Override
    protected List<Instance> findAllGoogle(Compute client) throws Exception {
        List<Instance> instances = new ArrayList<>();
        String pageToken;
        List<String> zones = client.zones().list(getProjectId()).execute().getItems()
            .stream()
            .map(Zone::getName)
            .collect(Collectors.toList());

        for (String zone : zones) {
            do {
                InstanceList results = client.instances().list(getProjectId(), zone).execute();
                pageToken = results.getNextPageToken();

                if (results.getItems() != null) {
                    instances.addAll(results.getItems());
                }
            } while (pageToken != null);
        }

        return instances;
    }

    @Override
    protected List<Instance> findGoogle(Compute client, Map<String, String> filters) throws Exception {
        List<Instance> instances = new ArrayList<>();

        if (filters.containsKey("zone")) {
            String pageToken;

            do {
                InstanceList results = client.instances().list(getProjectId(), filters.get("zone"))
                    .setFilter(filters.get("filter"))
                    .execute();
                pageToken = results.getNextPageToken();

                if (results.getItems() != null) {
                    instances.addAll(results.getItems());
                }

            } while (pageToken != null);
        }

        return instances;
    }
}
