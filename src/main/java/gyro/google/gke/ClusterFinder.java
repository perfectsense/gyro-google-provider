/*
 * Copyright 2021, Brightspot.
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

package gyro.google.gke;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.cloud.container.v1beta1.ClusterManagerClient;
import com.google.container.v1beta1.Cluster;
import com.google.container.v1beta1.ListClustersRequest;
import gyro.core.Type;
import gyro.google.GoogleFinder;

/**
 * Query for a cluster.
 *
 * Examples
 * --------
 *
 * .. code-block:: gyro
 *
 *    cluster: $(external-query google::gke-cluster { location: 'us-east1', name: 'example-one' })
 */
@Type("gke-cluster")
public class ClusterFinder extends GoogleFinder<ClusterManagerClient, Cluster, ClusterResource> {

    private String location;
    private String name;

    /**
     * The region or zone where the cluster is located.
     */
    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    /**
     * The name of the cluster.
     */
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    protected List<Cluster> findAllGoogle(ClusterManagerClient client) throws Exception {
        client.close();
        throw new UnsupportedOperationException("Finding all `gke-clusters` without any filter is not supported!!");
    }

    @Override
    protected List<Cluster> findGoogle(ClusterManagerClient client, Map<String, String> filters) throws Exception {
        ArrayList<Cluster> clusters = new ArrayList<>();

        if (filters.containsKey("location")) {
            try {
                if (filters.containsKey("name")) {
                    clusters.add(client.getCluster(getProjectId(), filters.get("location"), filters.get("name")));

                } else {
                    String resourceName = String.format("projects/%s/locations/%s",
                        getProjectId(), filters.get("location"));

                    clusters.addAll(client.listClusters(ListClustersRequest.newBuilder()
                            .setParent(resourceName).build()).getClustersList());
                }
            } finally {
                client.close();
            }
        }

        return clusters;
    }
}
