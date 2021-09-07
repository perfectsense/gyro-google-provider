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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.cloud.container.v1.ClusterManagerClient;
import com.google.container.v1.Cluster;
import com.google.container.v1.ClusterUpdate;
import com.google.container.v1.CreateClusterRequest;
import com.google.container.v1.SetLabelsRequest;
import gyro.core.GyroUI;
import gyro.core.resource.Id;
import gyro.core.resource.Output;
import gyro.core.resource.Resource;
import gyro.core.resource.Updatable;
import gyro.core.scope.State;
import gyro.google.Copyable;
import gyro.google.GoogleResource;
import gyro.google.compute.NetworkResource;
import gyro.google.compute.SubnetworkResource;

public class ClusterResource extends GoogleResource implements Copyable<Cluster> {

    // TODO: TEST DEFAULTS
    // TODO: TEST VALIDATIONS

    //    private List<GkeNodePool> nodePools; // Updatable with ImageType, IntraNodeVisibility, NodePoolAutoscaling, nodeVersion, NodePoolId in cluster
    //    MasterVersion
    private String location;
    private String name;
    private String description;
    private GkeMasterAuth masterAuthConfig;
    private String loggingService;
    private String monitoringService;
    private NetworkResource network;
    private String clusterIpv4Cidr;
    private GkeAddonsConfig addonsConfig;
    private SubnetworkResource subnetwork;
    private List<String> nodeLocations;
    private Boolean enableKubernetesAlpha;
    private GkeLegacyAbac legacyAbacConfig;
    private GkeNetworkPolicy networkPolicyConfig;
    private GkeIpAllocationPolicy ipAllocationPolicy;
    private GkeMasterAuthorizedNetworksConfig masterAuthorizedNetworksConfig;
    private GkeMaintenancePolicy maintenancePolicy;
    private GkeBinaryAuthorization binaryAuthorizationConfig;
    private GkeClusterAutoscaling clusterAutoscalingConfig;
    private GkeNetworkConfig networkConfig;
    private GkeMaxPodsConstraint defaultMaxPodsConstraint;
    private GkeResourceUsageExportConfig resourceUsageExportConfig;
    private GkeAuthenticatorGroupsConfig authenticatorGroupsConfig;
    private GkePrivateClusterConfig privateClusterConfig;
    private GkeDatabaseEncryption databaseEncryption;
    private GkeVerticalPodAutoscaling verticalPodAutoscaling;
    private GkeShieldedNodes shieldedNodes;
    private GkeReleaseChannel releaseChannel;
    private GkeWorkloadIdentityConfig workloadIdentityConfig;
    private String initialClusterVersion;
    private List<GkeStatusCondition> condition;
    private Boolean enableTpu;
    private Map<String, String> labels;

    // Read-only
    private String tpuIpv4CidrBlock;
    private String servicesIpv4Cidr;
    private Integer nodeIpv4CidrSize;
    private Cluster.Status status;
    private String currentMasterVersion;
    private String endpoint;
    private String selfLink;
    private String labelFingerPrint;

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    @Id
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public GkeMasterAuth getMasterAuthConfig() {
        return masterAuthConfig;
    }

    public void setMasterAuthConfig(GkeMasterAuth masterAuthConfig) {
        this.masterAuthConfig = masterAuthConfig;
    }

    @Updatable
    public String getLoggingService() {
        return loggingService;
    }

    public void setLoggingService(String loggingService) {
        this.loggingService = loggingService;
    }

    @Updatable
    public String getMonitoringService() {
        return monitoringService;
    }

    public void setMonitoringService(String monitoringService) {
        this.monitoringService = monitoringService;
    }

    public NetworkResource getNetwork() {
        return network;
    }

    public void setNetwork(NetworkResource network) {
        this.network = network;
    }

    public String getClusterIpv4Cidr() {
        return clusterIpv4Cidr;
    }

    public void setClusterIpv4Cidr(String clusterIpv4Cidr) {
        this.clusterIpv4Cidr = clusterIpv4Cidr;
    }

    @Updatable
    public GkeAddonsConfig getAddonsConfig() {
        return addonsConfig;
    }

    public void setAddonsConfig(GkeAddonsConfig addonsConfig) {
        this.addonsConfig = addonsConfig;
    }

    public SubnetworkResource getSubnetwork() {
        return subnetwork;
    }

    public void setSubnetwork(SubnetworkResource subnetwork) {
        this.subnetwork = subnetwork;
    }

    @Updatable
    public List<String> getNodeLocations() {
        if (nodeLocations == null) {
            nodeLocations = new ArrayList<>();
        }

        return nodeLocations;
    }

    public void setNodeLocations(List<String> nodeLocations) {
        this.nodeLocations = nodeLocations;
    }

    public Boolean getEnableKubernetesAlpha() {
        return enableKubernetesAlpha;
    }

    public void setEnableKubernetesAlpha(Boolean enableKubernetesAlpha) {
        this.enableKubernetesAlpha = enableKubernetesAlpha;
    }

    public GkeLegacyAbac getLegacyAbacConfig() {
        return legacyAbacConfig;
    }

    public void setLegacyAbacConfig(GkeLegacyAbac legacyAbacConfig) {
        this.legacyAbacConfig = legacyAbacConfig;
    }

    public GkeNetworkPolicy getNetworkPolicyConfig() {
        return networkPolicyConfig;
    }

    public void setNetworkPolicyConfig(GkeNetworkPolicy networkPolicyConfig) {
        this.networkPolicyConfig = networkPolicyConfig;
    }

    public GkeIpAllocationPolicy getIpAllocationPolicy() {
        return ipAllocationPolicy;
    }

    public void setIpAllocationPolicy(GkeIpAllocationPolicy ipAllocationPolicy) {
        this.ipAllocationPolicy = ipAllocationPolicy;
    }

    @Updatable
    public GkeMasterAuthorizedNetworksConfig getMasterAuthorizedNetworksConfig() {
        return masterAuthorizedNetworksConfig;
    }

    public void setMasterAuthorizedNetworksConfig(GkeMasterAuthorizedNetworksConfig masterAuthorizedNetworksConfig) {
        this.masterAuthorizedNetworksConfig = masterAuthorizedNetworksConfig;
    }

    public GkeMaintenancePolicy getMaintenancePolicy() {
        return maintenancePolicy;
    }

    public void setMaintenancePolicy(GkeMaintenancePolicy maintenancePolicy) {
        this.maintenancePolicy = maintenancePolicy;
    }

    @Updatable
    public GkeBinaryAuthorization getBinaryAuthorizationConfig() {
        return binaryAuthorizationConfig;
    }

    public void setBinaryAuthorizationConfig(GkeBinaryAuthorization binaryAuthorizationConfig) {
        this.binaryAuthorizationConfig = binaryAuthorizationConfig;
    }

    @Updatable
    public GkeClusterAutoscaling getClusterAutoscalingConfig() {
        return clusterAutoscalingConfig;
    }

    public void setClusterAutoscalingConfig(GkeClusterAutoscaling clusterAutoscalingConfig) {
        this.clusterAutoscalingConfig = clusterAutoscalingConfig;
    }

    public GkeNetworkConfig getNetworkConfig() {
        return networkConfig;
    }

    public void setNetworkConfig(GkeNetworkConfig networkConfig) {
        this.networkConfig = networkConfig;
    }

    public GkeMaxPodsConstraint getDefaultMaxPodsConstraint() {
        return defaultMaxPodsConstraint;
    }

    public void setDefaultMaxPodsConstraint(GkeMaxPodsConstraint defaultMaxPodsConstraint) {
        this.defaultMaxPodsConstraint = defaultMaxPodsConstraint;
    }

    @Updatable
    public GkeResourceUsageExportConfig getResourceUsageExportConfig() {
        return resourceUsageExportConfig;
    }

    public void setResourceUsageExportConfig(GkeResourceUsageExportConfig resourceUsageExportConfig) {
        this.resourceUsageExportConfig = resourceUsageExportConfig;
    }

    @Updatable
    public GkeAuthenticatorGroupsConfig getAuthenticatorGroupsConfig() {
        return authenticatorGroupsConfig;
    }

    public void setAuthenticatorGroupsConfig(GkeAuthenticatorGroupsConfig authenticatorGroupsConfig) {
        this.authenticatorGroupsConfig = authenticatorGroupsConfig;
    }

    @Updatable
    public GkePrivateClusterConfig getPrivateClusterConfig() {
        return privateClusterConfig;
    }

    public void setPrivateClusterConfig(GkePrivateClusterConfig privateClusterConfig) {
        this.privateClusterConfig = privateClusterConfig;
    }

    @Updatable
    public GkeDatabaseEncryption getDatabaseEncryption() {
        return databaseEncryption;
    }

    public void setDatabaseEncryption(GkeDatabaseEncryption databaseEncryption) {
        this.databaseEncryption = databaseEncryption;
    }

    @Updatable
    public GkeVerticalPodAutoscaling getVerticalPodAutoscaling() {
        return verticalPodAutoscaling;
    }

    public void setVerticalPodAutoscaling(GkeVerticalPodAutoscaling verticalPodAutoscaling) {
        this.verticalPodAutoscaling = verticalPodAutoscaling;
    }

    @Updatable
    public GkeShieldedNodes getShieldedNodes() {
        return shieldedNodes;
    }

    public void setShieldedNodes(GkeShieldedNodes shieldedNodes) {
        this.shieldedNodes = shieldedNodes;
    }

    @Updatable
    public GkeReleaseChannel getReleaseChannel() {
        return releaseChannel;
    }

    public void setReleaseChannel(GkeReleaseChannel releaseChannel) {
        this.releaseChannel = releaseChannel;
    }

    @Updatable
    public GkeWorkloadIdentityConfig getWorkloadIdentityConfig() {
        return workloadIdentityConfig;
    }

    public void setWorkloadIdentityConfig(GkeWorkloadIdentityConfig workloadIdentityConfig) {
        this.workloadIdentityConfig = workloadIdentityConfig;
    }

    public String getInitialClusterVersion() {
        return initialClusterVersion;
    }

    public void setInitialClusterVersion(String initialClusterVersion) {
        this.initialClusterVersion = initialClusterVersion;
    }

    public List<GkeStatusCondition> getCondition() {
        if (condition == null) {
            condition = new ArrayList<>();
        }
        return condition;
    }

    public void setCondition(List<GkeStatusCondition> condition) {
        this.condition = condition;
    }

    public Boolean getEnableTpu() {
        return enableTpu;
    }

    public void setEnableTpu(Boolean enableTpu) {
        this.enableTpu = enableTpu;
    }

    public Map<String, String> getLabels() {
        if (labels == null) {
            labels = new HashMap<>();
        }

        return labels;
    }

    public void setLabels(Map<String, String> labels) {
        this.labels = labels;
    }

    @Output
    public String getTpuIpv4CidrBlock() {
        return tpuIpv4CidrBlock;
    }

    public void setTpuIpv4CidrBlock(String tpuIpv4CidrBlock) {
        this.tpuIpv4CidrBlock = tpuIpv4CidrBlock;
    }

    @Output
    public String getServicesIpv4Cidr() {
        return servicesIpv4Cidr;
    }

    public void setServicesIpv4Cidr(String servicesIpv4Cidr) {
        this.servicesIpv4Cidr = servicesIpv4Cidr;
    }

    @Output
    public Integer getNodeIpv4CidrSize() {
        return nodeIpv4CidrSize;
    }

    public void setNodeIpv4CidrSize(Integer nodeIpv4CidrSize) {
        this.nodeIpv4CidrSize = nodeIpv4CidrSize;
    }

    @Output
    public Cluster.Status getStatus() {
        return status;
    }

    public void setStatus(Cluster.Status status) {
        this.status = status;
    }

    @Output
    public String getCurrentMasterVersion() {
        return currentMasterVersion;
    }

    public void setCurrentMasterVersion(String currentMasterVersion) {
        this.currentMasterVersion = currentMasterVersion;
    }

    @Output
    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    @Output
    public String getSelfLink() {
        return selfLink;
    }

    public void setSelfLink(String selfLink) {
        this.selfLink = selfLink;
    }

    @Updatable
    public String getLabelFingerPrint() {
        return labelFingerPrint;
    }

    public void setLabelFingerPrint(String labelFingerPrint) {
        this.labelFingerPrint = labelFingerPrint;
    }

    @Override
    public void copyFrom(Cluster model) throws Exception {
        setMasterAuthConfig(null);
        if (model.hasMasterAuth()) {
            GkeMasterAuth config = newSubresource(GkeMasterAuth.class);
            config.copyFrom(model.getMasterAuth());
            setMasterAuthConfig(config);
        }

        setAddonsConfig(null);
        if (model.hasAddonsConfig()) {
            GkeAddonsConfig config = newSubresource(GkeAddonsConfig.class);
            config.copyFrom(model.getAddonsConfig());
            setAddonsConfig(config);
        }

        setLegacyAbacConfig(null);
        if (model.hasLegacyAbac()) {
            GkeLegacyAbac config = newSubresource(GkeLegacyAbac.class);
            config.copyFrom(model.getLegacyAbac());
            setLegacyAbacConfig(config);
        }

        setNetworkPolicyConfig(null);
        if (model.hasNetworkPolicy()) {
            GkeNetworkPolicy config = newSubresource(GkeNetworkPolicy.class);
            config.copyFrom(model.getNetworkPolicy());
            setNetworkPolicyConfig(config);
        }

        setIpAllocationPolicy(null);
        if (model.hasIpAllocationPolicy()) {
            GkeIpAllocationPolicy config = newSubresource(GkeIpAllocationPolicy.class);
            config.copyFrom(model.getIpAllocationPolicy());
            setIpAllocationPolicy(config);
        }

        setMasterAuthorizedNetworksConfig(null);
        if (model.hasMasterAuthorizedNetworksConfig()) {
            GkeMasterAuthorizedNetworksConfig config = newSubresource(GkeMasterAuthorizedNetworksConfig.class);
            config.copyFrom(model.getMasterAuthorizedNetworksConfig());
            setMasterAuthorizedNetworksConfig(config);
        }

        setMaintenancePolicy(null);
        if (model.hasMaintenancePolicy()) {
            GkeMaintenancePolicy config = newSubresource(GkeMaintenancePolicy.class);
            config.copyFrom(model.getMaintenancePolicy());
            setMaintenancePolicy(config);
        }

        setBinaryAuthorizationConfig(null);
        if (model.hasBinaryAuthorization()) {
            GkeBinaryAuthorization config = newSubresource(GkeBinaryAuthorization.class);
            config.copyFrom(model.getBinaryAuthorization());
            setBinaryAuthorizationConfig(config);
        }

        setClusterAutoscalingConfig(null);
        if (model.hasAutoscaling()) {
            GkeClusterAutoscaling config = newSubresource(GkeClusterAutoscaling.class);
            config.copyFrom(model.getAutoscaling());
            setClusterAutoscalingConfig(config);
        }

        setNetworkConfig(null);
        if (model.hasNetworkConfig()) {
            GkeNetworkConfig config = newSubresource(GkeNetworkConfig.class);
            config.copyFrom(model.getNetworkConfig());
            setNetworkConfig(config);
        }

        setDefaultMaxPodsConstraint(null);
        if (model.hasDefaultMaxPodsConstraint()) {
            GkeMaxPodsConstraint config = newSubresource(GkeMaxPodsConstraint.class);
            config.copyFrom(model.getDefaultMaxPodsConstraint());
            setDefaultMaxPodsConstraint(config);
        }

        setResourceUsageExportConfig(null);
        if (model.hasResourceUsageExportConfig()) {
            GkeResourceUsageExportConfig config = newSubresource(GkeResourceUsageExportConfig.class);
            config.copyFrom(model.getResourceUsageExportConfig());
            setResourceUsageExportConfig(config);
        }

        setAuthenticatorGroupsConfig(null);
        if (model.hasAuthenticatorGroupsConfig()) {
            GkeAuthenticatorGroupsConfig config = newSubresource(GkeAuthenticatorGroupsConfig.class);
            config.copyFrom(model.getAuthenticatorGroupsConfig());
            setAuthenticatorGroupsConfig(config);
        }

        setPrivateClusterConfig(null);
        if (model.hasPrivateClusterConfig()) {
            GkePrivateClusterConfig config = newSubresource(GkePrivateClusterConfig.class);
            config.copyFrom(model.getPrivateClusterConfig());
            setPrivateClusterConfig(config);
        }

        setDatabaseEncryption(null);
        if (model.hasDatabaseEncryption()) {
            GkeDatabaseEncryption config = newSubresource(GkeDatabaseEncryption.class);
            config.copyFrom(model.getDatabaseEncryption());
            setDatabaseEncryption(config);
        }

        setVerticalPodAutoscaling(null);
        if (model.hasVerticalPodAutoscaling()) {
            GkeVerticalPodAutoscaling config = newSubresource(GkeVerticalPodAutoscaling.class);
            config.copyFrom(model.getVerticalPodAutoscaling());
            setVerticalPodAutoscaling(config);
        }

        setShieldedNodes(null);
        if (model.hasShieldedNodes()) {
            GkeShieldedNodes config = newSubresource(GkeShieldedNodes.class);
            config.copyFrom(model.getShieldedNodes());
            setShieldedNodes(config);
        }

        setReleaseChannel(null);
        if (model.hasReleaseChannel()) {
            GkeReleaseChannel config = newSubresource(GkeReleaseChannel.class);
            config.copyFrom(model.getReleaseChannel());
            setReleaseChannel(config);
        }

        setWorkloadIdentityConfig(null);
        if (model.hasWorkloadIdentityConfig()) {
            GkeWorkloadIdentityConfig config = newSubresource(GkeWorkloadIdentityConfig.class);
            config.copyFrom(model.getWorkloadIdentityConfig());
            setWorkloadIdentityConfig(config);
        }

        setCondition(null);
        if (model.getConditionsCount() > 0) {
            setCondition(model.getConditionsList().stream().map(m -> {
                GkeStatusCondition config = newSubresource(GkeStatusCondition.class);
                config.copyFrom(m);
                return config;
            }).collect(Collectors.toList()));
        }

        setLocation(model.getLocation());
        setName(model.getName());
        setDescription(model.getDescription());
        setEnableTpu(model.getEnableTpu());
        setLabels(model.getResourceLabelsMap());
        setTpuIpv4CidrBlock(model.getTpuIpv4CidrBlock());
        setServicesIpv4Cidr(model.getServicesIpv4Cidr());
        setNodeIpv4CidrSize(model.getNodeIpv4CidrSize());
        setStatus(model.getStatus());
        setCurrentMasterVersion(model.getCurrentMasterVersion());
        setEndpoint(model.getEndpoint());
        setSelfLink(model.getSelfLink());
        setInitialClusterVersion(model.getInitialClusterVersion());
        setSubnetwork(findById(SubnetworkResource.class, model.getSubnetwork()));
        setNodeLocations(model.getLocationsList());
        setEnableKubernetesAlpha(model.getEnableKubernetesAlpha());
        setLoggingService(model.getLoggingService());
        setMonitoringService(model.getMonitoringService());
        setNetwork(findById(NetworkResource.class, model.getNetwork()));
        setClusterIpv4Cidr(model.getClusterIpv4Cidr());
        setLabelFingerPrint(model.getLabelFingerprint());
    }

    @Override
    protected boolean doRefresh() throws Exception {
        ClusterManagerClient client = createClient(ClusterManagerClient.class);

        Cluster cluster = client.getCluster(getClusterId());

        if (cluster == null) {
            return false;
        }

        copyFrom(cluster);

        return true;
    }

    @Override
    protected void doCreate(GyroUI ui, State state) throws Exception {
        ClusterManagerClient client = createClient(ClusterManagerClient.class);

        Cluster.Builder builder = Cluster.newBuilder().setName(getName());

        if (getMasterAuthConfig() != null) {
            builder.setMasterAuth(getMasterAuthConfig().toMasterAuth());
        }

        if (getAddonsConfig() != null) {
            builder.setAddonsConfig(getAddonsConfig().toAddonsConfig());
        }

        if (getLegacyAbacConfig() != null) {
            builder.setLegacyAbac(getLegacyAbacConfig().toLegacyAbac());
        }

        if (getNetworkPolicyConfig() != null) {
            builder.setNetworkPolicy(getNetworkPolicyConfig().toNetworkPolicy());
        }

        if (getIpAllocationPolicy() != null) {
            builder.setIpAllocationPolicy(getIpAllocationPolicy().toIPAllocationPolicy());
        }

        if (getMasterAuthorizedNetworksConfig() != null) {
            builder.setMasterAuthorizedNetworksConfig(getMasterAuthorizedNetworksConfig().toMasterAuthorizedNetworksConfig());
        }

        if (getMaintenancePolicy() != null) {
            builder.setMaintenancePolicy(getMaintenancePolicy().toMaintenancePolicy());
        }

        if (getBinaryAuthorizationConfig() != null) {
            builder.setBinaryAuthorization(getBinaryAuthorizationConfig().toBinaryAuthorization());
        }

        if (getClusterAutoscalingConfig() != null) {
            builder.setAutoscaling(getClusterAutoscalingConfig().toClusterAutoscaling());
        }

        if (getNetworkConfig() != null) {
            builder.setNetworkConfig(getNetworkConfig().toNetworkConfig());
        }

        if (getDefaultMaxPodsConstraint() != null) {
            builder.setDefaultMaxPodsConstraint(getDefaultMaxPodsConstraint().toMaxPodsConstraint());
        }

        if (getResourceUsageExportConfig() != null) {
            builder.setResourceUsageExportConfig(getResourceUsageExportConfig().toResourceUsageExportConfig());
        }

        if (getAuthenticatorGroupsConfig() != null) {
            builder.setAuthenticatorGroupsConfig(getAuthenticatorGroupsConfig().toAuthenticatorGroupsConfig());
        }

        if (getPrivateClusterConfig() != null) {
            builder.setPrivateClusterConfig(getPrivateClusterConfig().toPrivateClusterConfig());
        }

        if (getVerticalPodAutoscaling() != null) {
            builder.setVerticalPodAutoscaling(getVerticalPodAutoscaling().toVerticalPodAutoscaling());
        }

        if (getShieldedNodes() != null) {
            builder.setShieldedNodes(getShieldedNodes().toShieldedNodes());
        }

        if (getReleaseChannel() != null) {
            builder.setReleaseChannel(getReleaseChannel().toReleaseChannel());
        }

        if (getWorkloadIdentityConfig() != null) {
            builder.setWorkloadIdentityConfig(getWorkloadIdentityConfig().toWorkloadIdentityConfig());
        }

        if (getCondition() != null) {
            builder.addAllConditions(getCondition().stream().map(GkeStatusCondition::toStatusCondition).collect(
                Collectors.toList()));
        }

        if (getLocation() != null) {
            builder.setLocation(getLocation());
        }

        if (getName() != null) {
            builder.setName(getName());
        }

        if (getDescription() != null) {
            builder.setDescription(getDescription());
        }

        if (getEnableTpu() != null) {
            builder.setEnableTpu(getEnableTpu());
        }

        if (getLabels() != null) {
            builder.putAllResourceLabels(getLabels());
        }

        if (getInitialClusterVersion() != null) {
            builder.setInitialClusterVersion(getInitialClusterVersion());
        }

        if (getSubnetwork() != null) {
            builder.setSubnetwork(getSubnetwork().getId());
        }

        if (getNodeLocations() != null) {
            builder.addAllLocations(getNodeLocations());
        }

        if (getEnableKubernetesAlpha() != null) {
            builder.setEnableKubernetesAlpha(getEnableKubernetesAlpha());
        }

        if (getLoggingService() != null) {
            builder.setLoggingService(getLoggingService());
        }

        if (getMonitoringService() != null) {
            builder.setMonitoringService(getMonitoringService());
        }

        if (getNetwork() != null) {
            builder.setNetwork(getNetwork().getId());
        }

        if (getClusterIpv4Cidr() != null) {
            builder.setClusterIpv4Cidr(getClusterIpv4Cidr());
        }

        client.createCluster(CreateClusterRequest.newBuilder()
            .setParent(String.format("projects/%s/locations/%s", getProjectId(), getLocation()))
            .setCluster(builder.build())
            .build());

        refresh();
    }

    @Override
    protected void doUpdate(GyroUI ui, State state, Resource current, Set<String> changedFieldNames) throws Exception {
        ClusterManagerClient client = createClient(ClusterManagerClient.class);

        ClusterUpdate.Builder builder = ClusterUpdate.newBuilder();

        if (changedFieldNames.contains("addons_config")) {
            if (getAddonsConfig() != null) {
                builder.setDesiredAddonsConfig(getAddonsConfig().toAddonsConfig());
            } else {
                builder.clearDesiredAddonsConfig();
            }
        }

        if (changedFieldNames.contains("master_authorized_networks_config")) {
            if (getMasterAuthorizedNetworksConfig() != null) {
                builder.setDesiredMasterAuthorizedNetworksConfig(getMasterAuthorizedNetworksConfig().toMasterAuthorizedNetworksConfig());
            } else {
                builder.clearDesiredMasterAuthorizedNetworksConfig();
            }
        }

        if (changedFieldNames.contains("binary_authorization_config")) {
            if (getBinaryAuthorizationConfig() != null) {
                builder.setDesiredBinaryAuthorization(getBinaryAuthorizationConfig().toBinaryAuthorization());
            } else {
                builder.clearDesiredBinaryAuthorization();
            }
        }

        if (changedFieldNames.contains("cluster_autoscaling_config")) {
            if (getClusterAutoscalingConfig() != null) {
                builder.setDesiredClusterAutoscaling(getClusterAutoscalingConfig().toClusterAutoscaling());
            } else {
                builder.clearDesiredClusterAutoscaling();
            }
        }

        if (changedFieldNames.contains("resource_usage_export_config")) {
            if (getResourceUsageExportConfig() != null) {
                builder.setDesiredResourceUsageExportConfig(getResourceUsageExportConfig().toResourceUsageExportConfig());
            } else {
                builder.clearDesiredResourceUsageExportConfig();
            }
        }

        if (changedFieldNames.contains("authenticator_groups_config")) {
            if (getAuthenticatorGroupsConfig() != null) {
                builder.setDesiredAuthenticatorGroupsConfig(getAuthenticatorGroupsConfig().toAuthenticatorGroupsConfig());
            } else {
                builder.clearDesiredAuthenticatorGroupsConfig();
            }
        }

        if (changedFieldNames.contains("private_cluster_config")) {
            if (getPrivateClusterConfig() != null) {
                builder.setDesiredPrivateClusterConfig(getPrivateClusterConfig().toPrivateClusterConfig());
            } else {
                builder.clearDesiredPrivateClusterConfig();
            }
        }

        if (changedFieldNames.contains("vertical_pod_autoscaling")) {
            if (getVerticalPodAutoscaling() != null) {
                builder.setDesiredVerticalPodAutoscaling(getVerticalPodAutoscaling().toVerticalPodAutoscaling());
            } else {
                builder.clearDesiredVerticalPodAutoscaling();
            }
        }

        if (changedFieldNames.contains("shielded_nodes")) {
            if (getShieldedNodes() != null) {
                builder.setDesiredShieldedNodes(getShieldedNodes().toShieldedNodes());
            } else {
                builder.clearDesiredShieldedNodes();
            }
        }

        if (changedFieldNames.contains("release_channel")) {
            if (getReleaseChannel() != null) {
                builder.setDesiredReleaseChannel(getReleaseChannel().toReleaseChannel());
            } else {
                builder.clearDesiredReleaseChannel();
            }
        }

        if (changedFieldNames.contains("workload_identity_config")) {
            if (getWorkloadIdentityConfig() != null) {
                builder.setDesiredWorkloadIdentityConfig(getWorkloadIdentityConfig().toWorkloadIdentityConfig());
            } else {
                builder.clearDesiredWorkloadIdentityConfig();
            }
        }

        if (changedFieldNames.contains("node_locations")) {
            if (getNodeLocations() != null) {
                builder.addAllDesiredLocations(getNodeLocations());
            } else {
                builder.clearDesiredLocations();
            }
        }

        if (changedFieldNames.contains("logging_service")) {
            if (getLoggingService() != null) {
                builder.setDesiredLoggingService(getLoggingService());
            } else {
                builder.clearDesiredLoggingService();
            }
        }

        if (changedFieldNames.contains("monitoring_service")) {
            if (getMonitoringService() != null) {
                builder.setDesiredMonitoringService(getMonitoringService());
            } else {
                builder.clearDesiredMonitoringService();
            }
        }

        client.updateCluster(getClusterId(), builder.build());

        if (changedFieldNames.contains("labels")) {
            client.setLabels(SetLabelsRequest.newBuilder().setLabelFingerprint(getLabelFingerPrint())
                .putAllResourceLabels(getLabels()).setName(getClusterId()).build());
        }

        refresh();
    }

    @Override
    protected void doDelete(GyroUI ui, State state) throws Exception {
        ClusterManagerClient client = createClient(ClusterManagerClient.class);

        client.deleteCluster(getClusterId());
    }

    private String getClusterId() {
        return String.format("projects/%s/locations/%s/clusters/%s",
            getProjectId(), getLocation(), getName());
    }
}
