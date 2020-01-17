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
import com.google.api.services.compute.model.TargetHttpProxy;
import com.google.api.services.compute.model.UrlMapReference;
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
     * The region of the target http proxy. (Required)
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
        Compute client = createComputeClient();
        copyFrom(client.regionTargetHttpProxies().get(getProjectId(), getRegion(), getName()).execute());

        return true;
    }

    @Override
    protected void doCreate(GyroUI ui, State state) throws Exception {
        Compute client = createComputeClient();

        TargetHttpProxy targetHttpProxy = toTargetHttpProxy();
        targetHttpProxy.setRegion(getRegion());

        Operation operation = client.regionTargetHttpProxies()
            .insert(getProjectId(), getRegion(), targetHttpProxy)
            .execute();
        waitForCompletion(client, operation);

        refresh();
    }

    @Override
    public void doUpdate(GyroUI ui, State state, Resource current, Set<String> changedFieldNames) throws Exception {
        Compute client = createComputeClient();

        UrlMapReference urlMapReference = new UrlMapReference();
        urlMapReference.setUrlMap(getUrlMapSelfLink());
        client.regionTargetHttpProxies().setUrlMap(getProjectId(), getRegion(), getName(), urlMapReference).execute();

        refresh();
    }

    @Override
    public void doDelete(GyroUI ui, State state) throws Exception {
        Compute client = createComputeClient();
        Operation response = client.regionTargetHttpProxies().delete(getProjectId(), getRegion(), getName()).execute();
        waitForCompletion(client, response);
    }
}
