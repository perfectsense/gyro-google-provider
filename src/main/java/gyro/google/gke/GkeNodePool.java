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

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.google.cloud.container.v1.ClusterManagerClient;
import com.google.container.v1.CreateNodePoolRequest;
import com.google.container.v1.NodePool;
import com.google.container.v1.SetNodePoolAutoscalingRequest;
import com.google.container.v1.UpdateNodePoolRequest;
import gyro.core.GyroUI;
import gyro.core.Wait;
import gyro.core.resource.Output;
import gyro.core.resource.Resource;
import gyro.core.resource.Updatable;
import gyro.core.scope.State;
import gyro.core.validation.Required;
import gyro.google.Copyable;
import gyro.google.GoogleResource;

/**
 * .. code-block:: gyro
 *
 *     node-pool
 *         initial-node-count: 3
 *         name: "example-gke"
 *     end
 */
public class GkeNodePool extends GoogleResource implements Copyable<NodePool> {

    private GkeNodePoolAutoscaling autoscaling;
    private List<GkeStatusCondition> condition;
    private GkeNodeConfig config;
    private Integer initialNodeCount;
    private List<String> locations;
    private GkeNodeManagement management;
    private GkeMaxPodsConstraint maxPodsConstraint;
    private String name;
    private GkeUpgradeSettings upgradeSettings;
    private String version;

    // Read-only
    private Integer podIpv4CidrSize;
    private String selfLink;
    private NodePool.Status status;
    private List<String> instanceGroupUrls;

    /**
     * The autoscaler configuration for this NodePool.
     */
    @Updatable
    public GkeNodePoolAutoscaling getAutoscaling() {
        return autoscaling;
    }

    public void setAutoscaling(GkeNodePoolAutoscaling autoscaling) {
        this.autoscaling = autoscaling;
    }

    /**
     * The conditions which caused the current node pool state.
     */
    public List<GkeStatusCondition> getCondition() {
        return condition;
    }

    public void setCondition(List<GkeStatusCondition> condition) {
        this.condition = condition;
    }

    /**
     * The node configuration of the pool.
     */
    @Updatable
    public GkeNodeConfig getConfig() {
        return config;
    }

    public void setConfig(GkeNodeConfig config) {
        this.config = config;
    }

    /**
     * The initial node count for the pool.
     */
    @Required
    public Integer getInitialNodeCount() {
        return initialNodeCount;
    }

    public void setInitialNodeCount(Integer initialNodeCount) {
        this.initialNodeCount = initialNodeCount;
    }

    /**
     * The list of Google Compute Engine [zones](https://cloud.google.com/compute/docs/zones#available) in which the NodePool's nodes should be located.
     */
    @Updatable
    public List<String> getLocations() {
        return locations;
    }

    public void setLocations(List<String> locations) {
        this.locations = locations;
    }

    /**
     * The node management configuration for this NodePool.
     */
    public GkeNodeManagement getManagement() {
        return management;
    }

    public void setManagement(GkeNodeManagement management) {
        this.management = management;
    }

    /**
     * The constraint on the maximum number of pods that can be run simultaneously on a node in the node pool.
     */
    public GkeMaxPodsConstraint getMaxPodsConstraint() {
        return maxPodsConstraint;
    }

    public void setMaxPodsConstraint(GkeMaxPodsConstraint maxPodsConstraint) {
        this.maxPodsConstraint = maxPodsConstraint;
    }

    /**
     * The name of the node pool.
     */
    @Required
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * The upgrade settings which control disruption and speed of the upgrade.
     */
    @Updatable
    public GkeUpgradeSettings getUpgradeSettings() {
        return upgradeSettings;
    }

    public void setUpgradeSettings(GkeUpgradeSettings upgradeSettings) {
        this.upgradeSettings = upgradeSettings;
    }

    /**
     * The version of the Kubernetes of this node.
     */
    @Updatable
    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    /**
     * The pod CIDR block size per node in this node pool.
     */
    @Output
    public Integer getPodIpv4CidrSize() {
        return podIpv4CidrSize;
    }

    public void setPodIpv4CidrSize(Integer podIpv4CidrSize) {
        this.podIpv4CidrSize = podIpv4CidrSize;
    }

    /**
     * The Server-defined URL for the resource.
     */
    @Output
    public String getSelfLink() {
        return selfLink;
    }

    public void setSelfLink(String selfLink) {
        this.selfLink = selfLink;
    }

    /**
     * The status of the nodes in this pool instance.
     */
    @Output
    public NodePool.Status getStatus() {
        return status;
    }

    public void setStatus(NodePool.Status status) {
        this.status = status;
    }

    /**
     * The resource URLs of the `managed instance groups <https://cloud.google.com/compute/docs/instance-groups/creating-groups-of-managed-instances>`_ associated with this node pool.
     */
    @Output
    public List<String> getInstanceGroupUrls() {
        return instanceGroupUrls;
    }

    public void setInstanceGroupUrls(List<String> instanceGroupUrls) {
        this.instanceGroupUrls = instanceGroupUrls;
    }

    @Override
    public String primaryKey() {
        return getName();
    }

    @Override
    public void copyFrom(NodePool model) {
        setInitialNodeCount(model.getInitialNodeCount());
        setLocations(model.getLocationsList());
        setName(model.getName());
        setVersion(model.getVersion());
        setPodIpv4CidrSize(model.getPodIpv4CidrSize());
        setSelfLink(model.getSelfLink());
        setStatus(model.getStatus());
        setInstanceGroupUrls(model.getInstanceGroupUrlsList());

        setUpgradeSettings(null);
        if (model.hasUpgradeSettings()) {
            GkeUpgradeSettings gkeUpgradeSettings = newSubresource(GkeUpgradeSettings.class);
            gkeUpgradeSettings.copyFrom(model.getUpgradeSettings());
            setUpgradeSettings(gkeUpgradeSettings);
        }

        setManagement(null);
        if (model.hasManagement()) {
            GkeNodeManagement gkeNodeManagement = newSubresource(GkeNodeManagement.class);
            gkeNodeManagement.copyFrom(model.getManagement());
            setManagement(gkeNodeManagement);
        }

        setMaxPodsConstraint(null);
        if (model.hasMaxPodsConstraint()) {
            GkeMaxPodsConstraint gkeMaxPodsConstraint = newSubresource(GkeMaxPodsConstraint.class);
            gkeMaxPodsConstraint.copyFrom(model.getMaxPodsConstraint());
            setMaxPodsConstraint(gkeMaxPodsConstraint);
        }

        setAutoscaling(null);
        if (model.hasAutoscaling()) {
            GkeNodePoolAutoscaling gkeNodePoolAutoscaling = newSubresource(GkeNodePoolAutoscaling.class);
            gkeNodePoolAutoscaling.copyFrom(model.getAutoscaling());
            setAutoscaling(gkeNodePoolAutoscaling);
        }

        setConfig(null);
        if (model.hasConfig()) {
            GkeNodeConfig gkeNodeConfig = newSubresource(GkeNodeConfig.class);
            gkeNodeConfig.copyFrom(model.getConfig());
            setConfig(gkeNodeConfig);
        }

        setCondition(null);
        if (model.getConditionsCount() > 0) {
            setCondition(model.getConditionsList().stream().map(m -> {
                GkeStatusCondition config = newSubresource(GkeStatusCondition.class);
                config.copyFrom(m);
                return config;
            }).collect(Collectors.toList()));
        }
    }

    @Override
    protected boolean doRefresh() throws Exception {
        return true;
    }

    @Override
    protected void doCreate(GyroUI ui, State state) throws Exception {
        ClusterManagerClient client = createClient(ClusterManagerClient.class);

        if (getNodePool(client) == null) {
            client.createNodePool(CreateNodePoolRequest.newBuilder()
                .setParent(((ClusterResource) parentResource()).getClusterId())
                .setNodePool(buildNodePool())
                .build());
            waitForActiveStatus(client);
        }

        client.close();
    }

    @Override
    protected void doUpdate(GyroUI ui, State state, Resource current, Set<String> changedFieldNames) throws Exception {
        ClusterManagerClient client = createClient(ClusterManagerClient.class);

        UpdateNodePoolRequest.Builder builder = UpdateNodePoolRequest.newBuilder();

        if (changedFieldNames.contains("config")) {
            if (getConfig().getWorkloadMetadataConfig() != null) {
                builder.setWorkloadMetadataConfig(getConfig().getWorkloadMetadataConfig().toWorkloadMetadataConfig());
                updateCluster(client, builder);
                builder.clear();
            }

            builder.setImageType(getConfig().getImageType());
            updateCluster(client, builder);
            builder.clear();
        }

        if (changedFieldNames.contains("locations")) {
            builder.addAllLocations(getLocations());
            updateCluster(client, builder);
            builder.clear();
        }

        if (changedFieldNames.contains("upgradeSettings")) {
            builder.setUpgradeSettings(getUpgradeSettings().toUpgradeSettings());
            updateCluster(client, builder);
            builder.clear();
        }

        if (changedFieldNames.contains("version")) {
            builder.setNodeVersion(getVersion());
            updateCluster(client, builder);
            builder.clear();
        }

        if (changedFieldNames.contains("autoscaling")) {
            if (getAutoscaling() != null) {
                client.setNodePoolAutoscaling(SetNodePoolAutoscalingRequest.newBuilder().setName(getNodePoolId())
                    .setAutoscaling(getAutoscaling().toNodePoolAutoscaling()).build());
            } else {
                client.setNodePoolAutoscaling(SetNodePoolAutoscalingRequest.newBuilder()
                    .setName(getNodePoolId()).clearAutoscaling().build());
            }
            waitForActiveStatus(client);
        }

        client.close();
    }

    @Override
    protected void doDelete(GyroUI ui, State state) throws Exception {
        ClusterManagerClient client = createClient(ClusterManagerClient.class);

        client.deleteNodePool(getNodePoolId());

        Wait.atMost(15, TimeUnit.MINUTES).checkEvery(1, TimeUnit.MINUTES).until(() -> getNodePool(client) == null);

        client.close();
    }

    private void updateCluster(ClusterManagerClient client, UpdateNodePoolRequest.Builder builder) {
        client.updateNodePool(builder.setName(getNodePoolId()).build());
        waitForActiveStatus(client);
    }

    private void waitForActiveStatus(ClusterManagerClient client) {
        Wait.atMost(30, TimeUnit.MINUTES)
            .checkEvery(1, TimeUnit.MINUTES)
            .until(() -> getNodePool(client).getStatus().equals(NodePool.Status.RUNNING));
    }

    protected NodePool buildNodePool() {
        NodePool.Builder builder = NodePool.newBuilder()
            .setName(getName())
            .setInitialNodeCount(getInitialNodeCount());

        if (getAutoscaling() != null) {
            builder.setAutoscaling(getAutoscaling().toNodePoolAutoscaling());
        }

        if (getCondition() != null) {
            builder.addAllConditions(getCondition().stream().map(GkeStatusCondition::toStatusCondition)
                .collect(Collectors.toList()));
        }

        if (getConfig() != null) {
            builder.setConfig(getConfig().toNodeConfig());
        }

        if (getLocations() != null) {
            builder.addAllLocations(getLocations());
        }

        if (getManagement() != null) {
            builder.setManagement(getManagement().toNodeManagement());
        }

        if (getMaxPodsConstraint() != null) {
            builder.setMaxPodsConstraint(getMaxPodsConstraint().toMaxPodsConstraint());
        }

        if (getUpgradeSettings() != null) {
            builder.setUpgradeSettings(getUpgradeSettings().toUpgradeSettings());
        }

        if (getVersion() != null) {
            builder.setVersion(getVersion());
        }

        return builder.build();
    }

    protected NodePool getNodePool(ClusterManagerClient client) {
        NodePool nodePool = null;

        try {
            nodePool = client.getNodePool(getNodePoolId());
        } catch (Exception ex) {
            // ignore
        }

        return nodePool;
    }

    protected String getNodePoolId() {
        return String.format("%s/nodePools/%s", ((ClusterResource) parentResource()).getClusterId(), getName());
    }
}
