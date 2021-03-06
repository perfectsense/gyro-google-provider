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
import com.google.api.services.compute.model.TargetHttpProxy;
import com.google.api.services.compute.model.TargetHttpProxyList;
import gyro.core.Type;
import gyro.google.GoogleFinder;

/**
 * Query for a target http proxy.
 *
 * Example
 * -------
 *
 * .. code-block:: gyro
 *
 *    compute-target-http-proxy: $(external-query google::compute-target-http-proxy { name: 'target-http-proxy-example' })
 */
@Type("compute-target-http-proxy")
public class TargetHttpProxyFinder extends GoogleFinder<Compute, TargetHttpProxy, TargetHttpProxyResource> {

    private String name;

    /**
     * Name of the target http proxy.
     */
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    protected List<TargetHttpProxy> findAllGoogle(Compute client) throws Exception {
        List<TargetHttpProxy> targetHttpProxies = new ArrayList<>();
        String nextPageToken = null;
        TargetHttpProxyList targetHttpProxyList;

        do {
            targetHttpProxyList = client.targetHttpProxies().list(getProjectId()).setPageToken(nextPageToken).execute();
            if (targetHttpProxyList.getItems() != null) {
                targetHttpProxies.addAll(targetHttpProxyList.getItems());
            }
            nextPageToken = targetHttpProxyList.getNextPageToken();
        } while (nextPageToken != null);

        return targetHttpProxies;
    }

    @Override
    protected List<TargetHttpProxy> findGoogle(Compute client, Map<String, String> filters) throws Exception {
        return Collections.singletonList(client.targetHttpProxies().get(getProjectId(), filters.get("name")).execute());
    }
}
