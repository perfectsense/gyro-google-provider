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
import com.google.api.services.compute.model.BackendService;
import com.google.api.services.compute.model.BackendServiceList;
import gyro.core.Type;
import gyro.google.GoogleFinder;

/**
 * Query backend service.
 *
 * Example
 * -------
 *
 * .. code-block:: gyro
 *
 *    compute-backend-service: $(external-query google::compute-backend-service { name: 'compute-backend-service-example'})
 */
@Type("compute-backend-service")
public class BackendServiceFinder extends GoogleFinder<Compute, BackendService, BackendServiceResource> {

    private String name;

    /**
     * The name of the backend service.
     */
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    protected List<BackendService> findAllGoogle(Compute client) throws Exception {
        List<BackendService> backendServices = new ArrayList<>();
        BackendServiceList backendServiceList;
        String nextPageToken = null;

        do {
            backendServiceList = client.backendServices().list(getProjectId()).setPageToken(nextPageToken).execute();
            if (backendServiceList.getItems() != null) {
                backendServices.addAll(backendServiceList.getItems());
            }
            nextPageToken = backendServiceList.getNextPageToken();
        } while (nextPageToken != null);

        return backendServices;
    }

    @Override
    protected List<BackendService> findGoogle(Compute client, Map<String, String> filters) throws Exception {
        return Collections.singletonList(client.backendServices().get(getProjectId(), filters.get("name")).execute());
    }
}
