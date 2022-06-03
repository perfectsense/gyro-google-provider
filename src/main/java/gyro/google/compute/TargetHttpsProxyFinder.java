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

import com.google.cloud.compute.v1.ListTargetHttpsProxiesRequest;
import com.google.cloud.compute.v1.TargetHttpsProxiesClient;
import com.google.cloud.compute.v1.TargetHttpsProxy;
import com.google.cloud.compute.v1.TargetHttpsProxyList;
import gyro.core.Type;
import gyro.google.GoogleFinder;
import org.apache.commons.lang3.StringUtils;

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
public class TargetHttpsProxyFinder
    extends GoogleFinder<TargetHttpsProxiesClient, TargetHttpsProxy, TargetHttpsProxyResource> {

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
    protected List<TargetHttpsProxy> findAllGoogle(TargetHttpsProxiesClient client) throws Exception {
        List<TargetHttpsProxy> targetHttpsProxies = new ArrayList<>();
        TargetHttpsProxyList targetHttpsProxyList;
        String nextPageToken = null;

        try {
            do {
                ListTargetHttpsProxiesRequest.Builder builder = ListTargetHttpsProxiesRequest.newBuilder()
                    .setProject(getProjectId());

                if (nextPageToken != null) {
                    builder.setPageToken(nextPageToken);
                }

                targetHttpsProxyList = client.list(builder.build()).getPage().getResponse();
                nextPageToken = targetHttpsProxyList.getNextPageToken();

                targetHttpsProxies.addAll(targetHttpsProxyList.getItemsList());
            } while (!StringUtils.isEmpty(nextPageToken));

            return targetHttpsProxies;

        } finally {
            client.close();
        }
    }

    @Override
    protected List<TargetHttpsProxy> findGoogle(TargetHttpsProxiesClient client, Map<String, String> filters)
        throws Exception {
        return Collections.singletonList(client.get(getProjectId(), filters.get("name")));
    }
}
