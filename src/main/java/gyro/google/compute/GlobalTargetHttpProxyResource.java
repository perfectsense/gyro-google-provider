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

import java.util.Optional;
import java.util.Set;

import com.google.api.services.compute.Compute;
import com.google.api.services.compute.model.Operation;
import com.google.api.services.compute.model.TargetHttpProxy;
import gyro.core.GyroException;
import gyro.core.GyroUI;
import gyro.core.Type;
import gyro.core.resource.Resource;
import gyro.core.scope.State;

@Type("compute-target-http-proxy")
public class GlobalTargetHttpProxyResource extends TargetHttpProxyResource {

    @Override
    protected boolean doRefresh() throws Exception {
        Compute client = createComputeClient();
        copyFrom(client.targetHttpProxies().get(getProjectId(), getName()).execute());
        return true;
    }

    @Override
    protected void doCreate(GyroUI ui, State state) throws Exception {
        TargetHttpProxy targetHttpProxy = new TargetHttpProxy();
        targetHttpProxy.setDescription(getDescription());
        targetHttpProxy.setName(getName());
        Optional.ofNullable(getUrlMap()).ifPresent(urlMap -> targetHttpProxy.setUrlMap(urlMap.getSelfLink()));

        Compute client = createComputeClient();
        Operation response = client.targetHttpProxies().insert(getProjectId(), targetHttpProxy).execute();
        Operation.Error error = waitForCompletion(client, response);

        if (error != null) {
            throw new GyroException(error.toPrettyString());
        }
        refresh();
    }

    @Override
    public void doUpdate(
        GyroUI ui, State state, Resource current, Set<String> changedFieldNames) throws Exception {
        // TODO:
    }

    @Override
    public void doDelete(GyroUI ui, State state) throws Exception {
        Compute client = createComputeClient();
        Operation response = client.targetHttpProxies().delete(getProjectId(), getName()).execute();
        Operation.Error error = waitForCompletion(client, response);

        if (error != null) {
            throw new GyroException(error.toPrettyString());
        }
    }
}
