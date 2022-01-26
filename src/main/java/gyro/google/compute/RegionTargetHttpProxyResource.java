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

import com.google.api.gax.rpc.InvalidArgumentException;
import com.google.api.gax.rpc.NotFoundException;
import com.google.cloud.compute.v1.GetRegionTargetHttpProxyRequest;
import com.google.cloud.compute.v1.Operation;
import com.google.cloud.compute.v1.RegionTargetHttpProxiesClient;
import com.google.cloud.compute.v1.TargetHttpProxy;
import com.google.cloud.compute.v1.UrlMapReference;
import gyro.core.GyroUI;
import gyro.core.Type;
import gyro.core.resource.Resource;
import gyro.core.scope.State;
import gyro.core.validation.Required;

/**
 * Creates a regional target http proxy.
 *
 * Example
 * -------
 *
 * .. code-block:: gyro
 *
 *     google::compute-region-target-http-proxy region-target-http-proxy-example
 *         name: "region-target-http-proxy-example"
 *         description: "Region target http proxy description."
 *         region: "us-east1"
 *         region-url-map: $(google::compute-region-url-map region-url-map-example-region-target-http-proxy)
 *     end
 */
@Type("compute-region-target-http-proxy")
public class RegionTargetHttpProxyResource extends AbstractTargetHttpProxyResource {

    private String region;

    /**
     * The region of the target http proxy.
     */
    @Required
    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region != null ? region.substring(region.lastIndexOf("/") + 1) : null;
    }

    @Override
    public void copyFrom(TargetHttpProxy targetHttpProxy) {
        super.copyFrom(targetHttpProxy);

        setRegion(targetHttpProxy.getRegion());
    }

    @Override
    protected boolean doRefresh() throws Exception {
        try (RegionTargetHttpProxiesClient client = createClient(RegionTargetHttpProxiesClient.class)) {
            TargetHttpProxy proxies = getRegionTargetHttpProxy(client);

            if (proxies == null) {
                return false;
            }

            copyFrom(proxies);

            return true;
        }
    }

    @Override
    protected void doCreate(GyroUI ui, State state) throws Exception {
        try (RegionTargetHttpProxiesClient client = createClient(RegionTargetHttpProxiesClient.class)) {
            TargetHttpProxy.Builder builder = toTargetHttpProxy().toBuilder();
            builder.setRegion(getRegion());
            Operation operation = client.insert(getProjectId(), getRegion(), builder.build());
            waitForCompletion(operation);
        }

        refresh();
    }

    @Override
    public void doUpdate(GyroUI ui, State state, Resource current, Set<String> changedFieldNames) throws Exception {
        try (RegionTargetHttpProxiesClient client = createClient(RegionTargetHttpProxiesClient.class)) {
            UrlMapReference.Builder builder = UrlMapReference.newBuilder();

            if (getUrlMap() != null) {
                builder.setUrlMap(getUrlMapSelfLink());
            } else {
                builder.clearUrlMap();
            }

            Operation operation = client.setUrlMap(getProjectId(), getRegion(), getName(), builder.build());
            waitForCompletion(operation);
        }

        refresh();
    }

    @Override
    public void doDelete(GyroUI ui, State state) throws Exception {
        try (RegionTargetHttpProxiesClient client = createClient(RegionTargetHttpProxiesClient.class)) {
            Operation response = client.delete(getProjectId(), getRegion(), getName());
            waitForCompletion(response);
        }
    }

    private TargetHttpProxy getRegionTargetHttpProxy(RegionTargetHttpProxiesClient client) {
        TargetHttpProxy proxies = null;

        try {
            proxies = client.get(GetRegionTargetHttpProxyRequest.newBuilder()
                .setProject(getProjectId())
                .setRegion(getRegion())
                .setTargetHttpProxy(getName())
                .build());

        } catch (NotFoundException | InvalidArgumentException ex) {
            // ignore
        }

        return proxies;
    }
}
