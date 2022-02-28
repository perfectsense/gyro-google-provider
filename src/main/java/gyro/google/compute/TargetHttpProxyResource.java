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

import com.google.api.gax.rpc.NotFoundException;
import com.google.cloud.compute.v1.DeleteTargetHttpProxyRequest;
import com.google.cloud.compute.v1.GetTargetHttpProxyRequest;
import com.google.cloud.compute.v1.InsertTargetHttpProxyRequest;
import com.google.cloud.compute.v1.Operation;
import com.google.cloud.compute.v1.SetUrlMapTargetHttpProxyRequest;
import com.google.cloud.compute.v1.TargetHttpProxiesClient;
import com.google.cloud.compute.v1.TargetHttpProxy;
import com.google.cloud.compute.v1.UrlMapReference;
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
        try (TargetHttpProxiesClient client = createClient(TargetHttpProxiesClient.class)) {
            TargetHttpProxy targetHttpProxy = getTargetHttpProxy(client);

            if (targetHttpProxy == null) {
                return false;
            }

            copyFrom(targetHttpProxy);

            return true;
        }
    }

    @Override
    protected void doCreate(GyroUI ui, State state) throws Exception {
        try (TargetHttpProxiesClient client = createClient(TargetHttpProxiesClient.class)) {
            Operation operation = client.insertCallable().call(InsertTargetHttpProxyRequest.newBuilder()
                .setProject(getProjectId())
                .setTargetHttpProxyResource(toTargetHttpProxy())
                .build());

            waitForCompletion(operation);
        }

        refresh();
    }

    @Override
    public void doUpdate(GyroUI ui, State state, Resource current, Set<String> changedFieldNames) throws Exception {
        try (TargetHttpProxiesClient client = createClient(TargetHttpProxiesClient.class)) {
            UrlMapReference.Builder builder = UrlMapReference.newBuilder();
            builder.setUrlMap(getUrlMapSelfLink());

            Operation response = client.setUrlMapCallable().call(SetUrlMapTargetHttpProxyRequest.newBuilder()
                .setProject(getProjectId())
                .setTargetHttpProxy(getName())
                .setUrlMapReferenceResource(builder)
                .build());

            waitForCompletion(response);
        }
        refresh();
    }

    @Override
    public void doDelete(GyroUI ui, State state) throws Exception {
        try (TargetHttpProxiesClient client = createClient(TargetHttpProxiesClient.class)) {
            Operation response = client.deleteCallable().call(DeleteTargetHttpProxyRequest.newBuilder()
                .setProject(getProjectId())
                .setTargetHttpProxy(getName())
                .build());

            waitForCompletion(response);
        }
    }

    static boolean isTargetHttpProxy(String selfLink) {
        return selfLink != null && selfLink.contains("targetHttpProxies");
    }

    private TargetHttpProxy getTargetHttpProxy(TargetHttpProxiesClient client) {
        TargetHttpProxy targetHttpProxy = null;

        try {
            targetHttpProxy = client.get(GetTargetHttpProxyRequest.newBuilder()
                .setProject(getProjectId())
                .setTargetHttpProxy(getName())
                .build());

        } catch (NotFoundException ex) {
            // ignore
        }

        return targetHttpProxy;
    }
}
