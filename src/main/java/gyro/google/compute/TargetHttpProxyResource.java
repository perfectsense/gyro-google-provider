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
import com.google.api.services.compute.model.UrlMapReference;
import gyro.core.GyroUI;
import gyro.core.Type;
import gyro.core.resource.Resource;
import gyro.core.scope.State;

/**
 * Creates a target http proxy.
 *
 * Example
 * -------
 *
 * .. code-block:: gyro
 *
 *     google::compute-target-http-proxy target-http-proxy-example
 *         name: "target-http-proxy-example"
 *         description: "Target http proxy description."
 *         url-map: $(google::compute-url-map url-map-example-target-http-proxy)
 *     end
 */
@Type("compute-target-http-proxy")
public class TargetHttpProxyResource extends AbstractTargetHttpProxyResource {

    @Override
    protected boolean doRefresh() throws Exception {
        Compute client = createComputeClient();
        copyFrom(client.targetHttpProxies().get(getProjectId(), getName()).execute());

        return true;
    }

    @Override
    protected void doCreate(GyroUI ui, State state) throws Exception {
        Compute client = createComputeClient();
        Operation response = client.targetHttpProxies().insert(getProjectId(), toTargetHttpProxy()).execute();
        waitForCompletion(client, response);
        refresh();
    }

    @Override
    public void doUpdate(GyroUI ui, State state, Resource current, Set<String> changedFieldNames) throws Exception {
        Compute client = createComputeClient();

        UrlMapReference urlMapReference = new UrlMapReference();
        urlMapReference.setUrlMap(getUrlMapSelfLink());
        client.targetHttpProxies().setUrlMap(getProjectId(), getName(), urlMapReference).execute();

        refresh();
    }

    @Override
    public void doDelete(GyroUI ui, State state) throws Exception {
        Compute client = createComputeClient();
        Operation response = client.targetHttpProxies().delete(getProjectId(), getName()).execute();
        waitForCompletion(client, response);
    }
}
