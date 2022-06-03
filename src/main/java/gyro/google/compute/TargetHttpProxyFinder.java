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

import com.google.cloud.compute.v1.ListTargetHttpProxiesRequest;
import com.google.cloud.compute.v1.TargetHttpProxiesClient;
import com.google.cloud.compute.v1.TargetHttpProxy;
import com.google.cloud.compute.v1.TargetHttpProxyList;
import gyro.core.Type;
import gyro.google.GoogleFinder;
import org.apache.commons.lang3.StringUtils;

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
public class TargetHttpProxyFinder
    extends GoogleFinder<TargetHttpProxiesClient, TargetHttpProxy, TargetHttpProxyResource> {

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
    protected List<TargetHttpProxy> findAllGoogle(TargetHttpProxiesClient client) throws Exception {
        List<TargetHttpProxy> targetHttpProxies = new ArrayList<>();
        TargetHttpProxyList targetHttpProxyList;
        String nextPageToken = null;

        try {
            do {
                ListTargetHttpProxiesRequest.Builder builder = ListTargetHttpProxiesRequest.newBuilder()
                    .setProject(getProjectId());

                if (nextPageToken != null) {
                    builder.setPageToken(nextPageToken);
                }

                targetHttpProxyList = client.list(builder.build()).getPage().getResponse();
                nextPageToken = targetHttpProxyList.getNextPageToken();

                targetHttpProxies.addAll(targetHttpProxyList.getItemsList());
            } while (!StringUtils.isEmpty(nextPageToken));

            return targetHttpProxies;

        } finally {
            client.close();
        }
    }

    @Override
    protected List<TargetHttpProxy> findGoogle(TargetHttpProxiesClient client, Map<String, String> filters)
        throws Exception {
        return Collections.singletonList(client.get(getProjectId(), filters.get("name")));
    }
}
