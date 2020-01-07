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

import java.util.Set;

import com.google.api.services.compute.Compute;
import com.google.api.services.compute.model.Operation;
import com.google.api.services.compute.model.UrlMap;
import gyro.core.GyroUI;
import gyro.core.Type;
import gyro.core.resource.Resource;
import gyro.core.scope.State;

/**
 * Creates a global URL map.
 *
 * Example
 * -------
 *
 * .. code-block:: gyro
 *
 *     google::compute-url-map url-map-example
 *         name: "url-map-example"
 *         description: "URL map description."
 *         default-backend-service: $(google::compute-backend-service backend-service-example)
 *
 *         host-rule
 *             hosts: [ "example.com" ]
 *             path-matcher: "abc-path"
 *         end
 *
 *         path-matcher
 *             name: "abc-path"
 *             default-backend-service: $(google::compute-backend-service backend-service-example)
 *             path-rule
 *                 backend-bucket: $(google::compute-backend-bucket backend-bucket-example)
 *                 paths: [ "/abc" ]
 *             end
 *         end
 *     end
 */
@Type("compute-url-map")
public class UrlMapResource extends AbstractUrlMap {

    @Override
    public boolean doRefresh() throws Exception {
        Compute client = createComputeClient();
        copyFrom(client.urlMaps().get(getProjectId(), getName()).execute());
        return true;
    }

    @Override
    public void doCreate(GyroUI ui, State state) throws Exception {
        Compute client = createComputeClient();

        Operation response = client.urlMaps().insert(getProjectId(), toUrlMap(null)).execute();
        waitForCompletion(client, response);

        refresh();
    }

    @Override
    public void doUpdate(
        GyroUI ui, State state, Resource current, Set<String> changedFieldNames) throws Exception {
        Compute client = createComputeClient();

        UrlMap urlMap = toUrlMap(changedFieldNames);
        Operation operation = client.urlMaps().patch(getProjectId(), getName(), urlMap).execute();
        waitForCompletion(client, operation);

        refresh();
    }

    @Override
    public void doDelete(GyroUI ui, State state) throws Exception {
        Compute client = createComputeClient();
        Operation response = client.urlMaps().delete(getProjectId(), getName()).execute();
        waitForCompletion(client, response);
    }
}
