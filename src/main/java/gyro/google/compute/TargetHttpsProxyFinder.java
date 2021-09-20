/*
 * Copyright 2020, Perfect Sense, Inc.
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
import com.google.cloud.compute.v1.TargetHttpsProxy;
import com.google.cloud.compute.v1.TargetHttpsProxyList;
import gyro.core.Type;
import gyro.google.GoogleFinder;

/**
 * Query for a target https proxy.
 *
 * Example
 * -------
 *
 * .. code-block:: gyro
 *
 *    compute-target-https-proxy: $(external-query google::compute-target-https-proxy { name: 'target-https-proxy-example' })
 */
@Type("compute-target-https-proxy")
public class TargetHttpsProxyFinder extends GoogleFinder<Compute, TargetHttpsProxy, TargetHttpsProxyResource> {

    private String name;

    /**
     * Name of the target https proxy.
     */
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    protected List<TargetHttpsProxy> findAllGoogle(Compute client) throws Exception {
        List<TargetHttpsProxy> targetHttpsProxies = new ArrayList<>();
        String nextPageToken = null;
        TargetHttpsProxyList targetHttpsProxyList;

        do {
            targetHttpsProxyList =
                client.targetHttpsProxies().list(getProjectId()).setPageToken(nextPageToken).execute();
            if (targetHttpsProxyList.getItems() != null) {
                targetHttpsProxies.addAll(targetHttpsProxyList.getItems());
            }
            nextPageToken = targetHttpsProxyList.getNextPageToken();
        } while (nextPageToken != null);

        return targetHttpsProxies;
    }

    @Override
    protected List<TargetHttpsProxy> findGoogle(Compute client, Map<String, String> filters) throws Exception {
        return Collections.singletonList(client.targetHttpsProxies()
            .get(getProjectId(), filters.get("name"))
            .execute());
    }
}
