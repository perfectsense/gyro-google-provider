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

import com.google.cloud.compute.v1.BackendService;
import com.google.cloud.compute.v1.BackendServiceList;
import com.google.cloud.compute.v1.BackendServicesClient;
import com.google.cloud.compute.v1.ListBackendServicesRequest;
import com.psddev.dari.util.StringUtils;
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
public class BackendServiceFinder extends GoogleFinder<BackendServicesClient, BackendService, BackendServiceResource> {

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
    protected List<BackendService> findAllGoogle(BackendServicesClient client) throws Exception {
        List<BackendService> backendServices = new ArrayList<>();
        BackendServiceList backendServiceList;
        String nextPageToken = null;

        try {
            do {
                ListBackendServicesRequest.Builder builder = ListBackendServicesRequest.newBuilder();

                if (nextPageToken != null) {
                    builder.setPageToken(nextPageToken);
                }

                backendServiceList = client.list(builder.build()).getPage().getResponse();
                if (backendServiceList.getItemsList() != null) {
                    backendServices.addAll(backendServiceList.getItemsList());
                }
                nextPageToken = backendServiceList.getNextPageToken();

            } while (!StringUtils.isEmpty(nextPageToken));

            return backendServices;

        } finally {
            client.close();
        }
    }

    @Override
    protected List<BackendService> findGoogle(BackendServicesClient client, Map<String, String> filters)
        throws Exception {
        return Collections.singletonList(client.get(getProjectId(), filters.get("name")));
    }
}
