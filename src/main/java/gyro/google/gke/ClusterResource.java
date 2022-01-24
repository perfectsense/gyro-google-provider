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
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.google.api.gax.rpc.InvalidArgumentException;
import com.google.api.gax.rpc.NotFoundException;
import com.google.cloud.container.v1.ClusterManagerClient;
import com.google.container.v1.Cluster;
import com.google.container.v1.ClusterUpdate;
import com.google.container.v1.CreateClusterRequest;
import com.google.container.v1.IntraNodeVisibilityConfig;
import com.google.container.v1.SetLabelsRequest;
import gyro.core.GyroUI;
import gyro.core.Type;
import gyro.core.Wait;
import gyro.core.resource.Id;
import gyro.core.resource.Output;
import gyro.core.resource.Resource;
import gyro.core.resource.Updatable;
import gyro.core.scope.State;
import gyro.core.validation.Required;
import gyro.core.validation.ValidStrings;
import gyro.google.Copyable;
import gyro.google.GoogleResource;
import gyro.google.compute.NetworkResource;
import gyro.google.compute.SubnetworkResource;
import gyro.google.util.Utils;

/**
 * .. code-block:: gyro
 *
 *     google::gke-cluster cluster-example-gke
 *         name: "cluster-example-gke"
 *         description: "The example cluster"
 *         location: "us-central1-c"
 *         master-version: "1.20.9-gke.1001"
 *         logging-service: "logging.googleapis.com/kubernetes"
 *         monitoring-service: "monitoring.googleapis.com/kubernetes"
 *         enable-kubernetes-alpha: false
 *         enable-tpu: false
 *         node-locations: ["us-central1-c"]
 *
 *         addons-config
 *             http-load-balancing
 *                 disabled: true
 *             end
 *
 *             horizontal-pod-autoscaling
 *                 disabled: true
 *             end
 *
 *             network-policy-config
 *                 disabled: false
 *             end
 *
 *             cloud-run-config
 *                 disabled: true
 *             end
 *
 *             dns-cache-config
 *                 enabled: false
 *             end
 *
 *             config-connector-config
 *                 enabled: false
 *             end
 *         end
 *
 *         master-auth-config
 *             client-certificate-config
 *                 issue-client-certificate: false
 *             end
 *         end
 *
 *         legacy-abac-config
 *             enabled: true
 *         end
 *
 *         network-policy-config
 *             enabled: true
 *             provider: "CALICO"
 *         end
 *
 *         ip-allocation-policy
 *             use-ip-aliases: false
 *         end
 *
 *         binary-authorization-config
 *             enabled: true
 *         end
 *
 *         cluster-autoscaling-config
 *             enable-node-autoprovisioning: true
 *             autoprovisioning-locations: ["us-central1-c"]
 *
 *             resource-limit
 *                 resource-type: cpu
 *                 minimum: 1
 *                 maximum: 1
 *             end
 *
 *             resource-limit
 *                 resource-type: memory
 *                 minimum: 1
 *                 maximum: 1
 *             end
 *
 *             autoprovisioning-node-pool-defaults
 *                 disk-type: "pd-standard"
 *                 disk-size-gb: 20
 *
 *                 oauth-scopes: [
 *                     "https://www.googleapis.com/auth/compute",
 *                     "https://www.googleapis.com/auth/devstorage.read_only",
 *                     "https://www.googleapis.com/auth/monitoring",
 *                     "https://www.googleapis.com/auth/logging.write"
 *                 ]
 *
 *                 upgrade-settings
 *                     max-surge: 1
 *                     max-unavailable: 1
 *                 end
 *
 *                 management
 *                     auto-upgrade: true
 *                     auto-repair: true
 *                 end
 *             end
 *         end
 *
 *         network-config
 *             enable-intra-node-visibility: true
 *
 *             default-snat-status
 *                 disabled: false
 *             end
 *         end
 *
 *         ip-allocation-policy
 *             use-ip-aliases: true
 *             create-subnetwork: true
 *             subnetwork-name: "example-ip-aliasing-subnet"
 *             cluster-ipv4-cidr-block: "192.168.0.0/16"
 *         end
 *
 *         vertical-pod-autoscaling
 *             enabled: true
 *         end
 *
 *         shielded-nodes
 *             enabled: false
 *         end
 *
 *         binary-authorization-config
 *             enabled: true
 *         end
 *
 *         labels: {
 *             "example-label": "example-value"
 *         }
 *
 *         node-pool
 *             initial-node-count: 3
 *             name: "example-gke"
 *         end
 *
 *         node-pool
 *             initial-node-count: 3
 *             name: "example-gke-2"
 *
 *             config
 *                 machine-type: "e2-standard-2"
 *                 disk-size-gb: 20
 *                 image-type: "COS_CONTAINERD"
 *                 preemptible: false
 *                 disk-type: "pd-standard"
 *
 *                 oauth-scopes: [
 *                     "https://www.googleapis.com/auth/compute",
 *                     "https://www.googleapis.com/auth/devstorage.read_only",
 *                     "https://www.googleapis.com/auth/monitoring",
 *                     "https://www.googleapis.com/auth/logging.write"
 *                 ]
 *
 *                 metadata: {
 *                     "disable-legacy-endpoints": "true"
 *                 }
 *
 *                 labels: {
 *                     "example-label": "example-value"
 *                 }
 *
 *                 tags: [
 *                      "example-tag"
 *                 ]
 *
 *                 taint
 *                     key: "example-key"
 *                     value: "example-value"
 *                     effect: NO_EXECUTE
 *                 end
 *
 *                 sandbox-config
 *                     type: GVISOR
 *                 end
 *             end
 *
 *             management
 *                 auto-upgrade: true
 *                 auto-repair: true
 *             end
 *
 *             upgrade-settings
 *                 max-surge: 1
 *                 max-unavailable: 1
 *             end
 *         end
 *     end
 */
@Type("gke-cluster")
public class ClusterResource extends GoogleResource implements Copyable<Cluster> {

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
    private List<GkeNodePool> nodePool;
    private String masterVersion;

    // Read-only
    private String tpuIpv4CidrBlock;
    private String servicesIpv4Cidr;
    private Integer nodeIpv4CidrSize;
    private Cluster.Status status;
    private String endpoint;
    private String selfLink;
    private String labelFingerPrint;

    /**
     * The location where this cluster should live.
     */
    @Required
    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    /**
     * The name of this cluster.
     */
    @Id
    @Required
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * The optional description of this cluster.
     */
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * The authentication information for accessing the master endpoint.
     *
     * @subresource gyro.google.gke.GkeMasterAuth
     */
    public GkeMasterAuth getMasterAuthConfig() {
        return masterAuthConfig;
    }

    public void setMasterAuthConfig(GkeMasterAuth masterAuthConfig) {
        this.masterAuthConfig = masterAuthConfig;
    }

    /**
     * The logging service the cluster should use to write logs. Defaults to ``logging.googleapis.com/kubernetes`` for GKE 1.14+ or ``logging.googleapis.com`` for earlier versions.
     */
    @Updatable
    @ValidStrings({ "logging.googleapis.com/kubernetes", "logging.googleapis.com" })
    public String getLoggingService() {
        return loggingService;
    }

    public void setLoggingService(String loggingService) {
        this.loggingService = loggingService;
    }

    /**
     * The monitoring service the cluster should use to write metrics. Defaults to ``monitoring.googleapis.com/kubernetes`` for GKE 1.14+ or ``monitoring.googleapis.com`` for earlier versions.
     */
    @Updatable
    @ValidStrings({ "monitoring.googleapis.com/kubernetes", "monitoring.googleapis.com" })
    public String getMonitoringService() {
        return monitoringService;
    }

    public void setMonitoringService(String monitoringService) {
        this.monitoringService = monitoringService;
    }

    /**
     * The Google Compute Engine network to which the cluster is connected. If left unspecified, the default network will be used.
     */
    public NetworkResource getNetwork() {
        return network;
    }

    public void setNetwork(NetworkResource network) {
        this.network = network;
    }

    /**
     * The IP address range of the container pods in this cluster. Leave blank to have one automatically chosen or specify a ``/14`` block in ``10.0.0.0/8``.
     */
    public String getClusterIpv4Cidr() {
        return clusterIpv4Cidr;
    }

    public void setClusterIpv4Cidr(String clusterIpv4Cidr) {
        this.clusterIpv4Cidr = clusterIpv4Cidr;
    }

    /**
     * The configurations for the various addons available to run in the cluster.
     *
     * @subresource gyro.google.gke.GkeAddonsConfig
     */
    @Updatable
    public GkeAddonsConfig getAddonsConfig() {
        return addonsConfig;
    }

    public void setAddonsConfig(GkeAddonsConfig addonsConfig) {
        this.addonsConfig = addonsConfig;
    }

    /**
     * The Google Compute Engine subnetwork to which the cluster is connected.
     */
    public SubnetworkResource getSubnetwork() {
        return subnetwork;
    }

    public void setSubnetwork(SubnetworkResource subnetwork) {
        this.subnetwork = subnetwork;
    }

    /**
     * The list of Google Compute Engine zones in which the cluster's nodes should be located.
     */
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

    /**
     * When set to ``true``, kubernetes alpha features are enabled on this cluster. This includes alpha API groups (e.g. ``v1alpha1``) and features that may not be production ready in the kubernetes version of the master and nodes. The cluster has no SLA for uptime and master/node upgrades are disabled. Alpha enabled clusters are automatically deleted thirty days after creation.
     */
    public Boolean getEnableKubernetesAlpha() {
        return enableKubernetesAlpha;
    }

    public void setEnableKubernetesAlpha(Boolean enableKubernetesAlpha) {
        this.enableKubernetesAlpha = enableKubernetesAlpha;
    }

    /**
     * The configuration for the legacy Attribute Based Access Control authorization mode.
     *
     * @subresource gyro.google.gke.GkeLegacyAbac
     */
    public GkeLegacyAbac getLegacyAbacConfig() {
        return legacyAbacConfig;
    }

    public void setLegacyAbacConfig(GkeLegacyAbac legacyAbacConfig) {
        this.legacyAbacConfig = legacyAbacConfig;
    }

    /**
     * The configuration for the cluster networking.
     *
     * @subresource gyro.google.gke.GkeNetworkPolicy
     */
    public GkeNetworkPolicy getNetworkPolicyConfig() {
        return networkPolicyConfig;
    }

    public void setNetworkPolicyConfig(GkeNetworkPolicy networkPolicyConfig) {
        this.networkPolicyConfig = networkPolicyConfig;
    }

    /**
     * The configuration for controlling how IPs are allocated in the cluster.
     *
     * @subresource gyro.google.gke.GkeIpAllocationPolicy
     */
    public GkeIpAllocationPolicy getIpAllocationPolicy() {
        return ipAllocationPolicy;
    }

    public void setIpAllocationPolicy(GkeIpAllocationPolicy ipAllocationPolicy) {
        this.ipAllocationPolicy = ipAllocationPolicy;
    }

    /**
     * The configuration options for master authorized networks feature.
     *
     * @subresource gyro.google.gke.GkeMasterAuthorizedNetworksConfig
     */
    @Updatable
    public GkeMasterAuthorizedNetworksConfig getMasterAuthorizedNetworksConfig() {
        return masterAuthorizedNetworksConfig;
    }

    public void setMasterAuthorizedNetworksConfig(GkeMasterAuthorizedNetworksConfig masterAuthorizedNetworksConfig) {
        this.masterAuthorizedNetworksConfig = masterAuthorizedNetworksConfig;
    }

    /**
     * The configuration for the maintenance policy for this cluster.
     *
     * @subresource gyro.google.gke.GkeMaintenancePolicy
     */
    public GkeMaintenancePolicy getMaintenancePolicy() {
        return maintenancePolicy;
    }

    public void setMaintenancePolicy(GkeMaintenancePolicy maintenancePolicy) {
        this.maintenancePolicy = maintenancePolicy;
    }

    /**
     * The configuration for Binary Authorization.
     *
     * @subresource gyro.google.gke.GkeBinaryAuthorization
     */
    @Updatable
    public GkeBinaryAuthorization getBinaryAuthorizationConfig() {
        return binaryAuthorizationConfig;
    }

    public void setBinaryAuthorizationConfig(GkeBinaryAuthorization binaryAuthorizationConfig) {
        this.binaryAuthorizationConfig = binaryAuthorizationConfig;
    }

    /**
     * The cluster-level autoscaling configuration.
     *
     * @subresource gyro.google.gke.GkeClusterAutoscaling
     */
    @Updatable
    public GkeClusterAutoscaling getClusterAutoscalingConfig() {
        return clusterAutoscalingConfig;
    }

    public void setClusterAutoscalingConfig(GkeClusterAutoscaling clusterAutoscalingConfig) {
        this.clusterAutoscalingConfig = clusterAutoscalingConfig;
    }

    /**
     * The configuration for cluster networking.
     *
     * @subresource gyro.google.gke.GkeNetworkConfig
     */
    @Updatable
    public GkeNetworkConfig getNetworkConfig() {
        return networkConfig;
    }

    public void setNetworkConfig(GkeNetworkConfig networkConfig) {
        this.networkConfig = networkConfig;
    }

    /**
     * The default constraint on the maximum number of pods that can be run simultaneously on a node in the node pool of this cluster.
     *
     * @subresource gyro.google.gke.GkeMaxPodsConstraint
     */
    public GkeMaxPodsConstraint getDefaultMaxPodsConstraint() {
        return defaultMaxPodsConstraint;
    }

    public void setDefaultMaxPodsConstraint(GkeMaxPodsConstraint defaultMaxPodsConstraint) {
        this.defaultMaxPodsConstraint = defaultMaxPodsConstraint;
    }

    /**
     * The configuration for exporting resource usages.
     *
     * @subresource gyro.google.gke.GkeResourceUsageExportConfig
     */
    @Updatable
    public GkeResourceUsageExportConfig getResourceUsageExportConfig() {
        return resourceUsageExportConfig;
    }

    public void setResourceUsageExportConfig(GkeResourceUsageExportConfig resourceUsageExportConfig) {
        this.resourceUsageExportConfig = resourceUsageExportConfig;
    }

    /**
     * The configuration for the authenticator groups.
     *
     * @subresource gyro.google.gke.GkeAuthenticatorGroupsConfig
     */
    public GkeAuthenticatorGroupsConfig getAuthenticatorGroupsConfig() {
        return authenticatorGroupsConfig;
    }

    public void setAuthenticatorGroupsConfig(GkeAuthenticatorGroupsConfig authenticatorGroupsConfig) {
        this.authenticatorGroupsConfig = authenticatorGroupsConfig;
    }

    /**
     * The configuration for private cluster.
     *
     * @subresource gyro.google.gke.GkePrivateClusterConfig
     */
    @Updatable
    public GkePrivateClusterConfig getPrivateClusterConfig() {
        return privateClusterConfig;
    }

    public void setPrivateClusterConfig(GkePrivateClusterConfig privateClusterConfig) {
        this.privateClusterConfig = privateClusterConfig;
    }

    /**
     * The configuration of etcd encryption.
     *
     * @subresource gyro.google.gke.GkeDatabaseEncryption
     */
    @Updatable
    public GkeDatabaseEncryption getDatabaseEncryption() {
        return databaseEncryption;
    }

    public void setDatabaseEncryption(GkeDatabaseEncryption databaseEncryption) {
        this.databaseEncryption = databaseEncryption;
    }

    /**
     * The cluster-level Vertical Pod Autoscaling configuration.
     *
     * @subresource gyro.google.gke.GkeVerticalPodAutoscaling
     */
    @Updatable
    public GkeVerticalPodAutoscaling getVerticalPodAutoscaling() {
        return verticalPodAutoscaling;
    }

    public void setVerticalPodAutoscaling(GkeVerticalPodAutoscaling verticalPodAutoscaling) {
        this.verticalPodAutoscaling = verticalPodAutoscaling;
    }

    /**
     * The configuration to enable shielded nodes in the cluster.
     *
     * @subresource gyro.google.gke.GkeShieldedNodes
     */
    @Updatable
    public GkeShieldedNodes getShieldedNodes() {
        return shieldedNodes;
    }

    public void setShieldedNodes(GkeShieldedNodes shieldedNodes) {
        this.shieldedNodes = shieldedNodes;
    }

    /**
     * The configuration for the release channel that the cluster is subscribed to.
     *
     * @subresource gyro.google.gke.GkeReleaseChannel
     */
    @Updatable
    public GkeReleaseChannel getReleaseChannel() {
        return releaseChannel;
    }

    public void setReleaseChannel(GkeReleaseChannel releaseChannel) {
        this.releaseChannel = releaseChannel;
    }

    /**
     * The configuration for the use of Kubernetes Service Accounts in GCP IAM policies.
     *
     * @subresource gyro.google.gke.GkeWorkloadIdentityConfig
     */
    @Updatable
    public GkeWorkloadIdentityConfig getWorkloadIdentityConfig() {
        return workloadIdentityConfig;
    }

    public void setWorkloadIdentityConfig(GkeWorkloadIdentityConfig workloadIdentityConfig) {
        this.workloadIdentityConfig = workloadIdentityConfig;
    }

    /**
     * The initial Kubernetes version for this cluster.
     */
    public String getInitialClusterVersion() {
        return initialClusterVersion;
    }

    public void setInitialClusterVersion(String initialClusterVersion) {
        this.initialClusterVersion = initialClusterVersion;
    }

    /**
     * The conditions which caused the current cluster state.
     *
     * @subresource gyro.google.gke.GkeStatusCondition
     */
    public List<GkeStatusCondition> getCondition() {
        if (condition == null) {
            condition = new ArrayList<>();
        }

        return condition;
    }

    public void setCondition(List<GkeStatusCondition> condition) {
        this.condition = condition;
    }

    /**
     * When set to ``true`` Cloud TPUs can be used in this cluster.
     */
    public Boolean getEnableTpu() {
        return enableTpu;
    }

    public void setEnableTpu(Boolean enableTpu) {
        this.enableTpu = enableTpu;
    }

    /**
     * The resource labels for the cluster to use to annotate any related Google Compute Engine resources.
     */
    public Map<String, String> getLabels() {
        if (labels == null) {
            labels = new HashMap<>();
        }

        return labels;
    }

    public void setLabels(Map<String, String> labels) {
        this.labels = labels;
    }

    /**
     * The node pools associated with this cluster.
     *
     * @subresource gyro.google.gke.GkeNodePool
     */
    @Required
    public List<GkeNodePool> getNodePool() {
        if (nodePool == null) {
            nodePool = new ArrayList<>();
        }

        return nodePool;
    }

    public void setNodePool(List<GkeNodePool> nodePool) {
        this.nodePool = nodePool;
    }

    /**
     * The current software version of the master endpoint.
     */
    @Updatable
    public String getMasterVersion() {
        return masterVersion;
    }

    public void setMasterVersion(String masterVersion) {
        this.masterVersion = masterVersion;
    }

    /**
     * The IP address range of the Cloud TPUs in this cluster.
     */
    @Output
    public String getTpuIpv4CidrBlock() {
        return tpuIpv4CidrBlock;
    }

    public void setTpuIpv4CidrBlock(String tpuIpv4CidrBlock) {
        this.tpuIpv4CidrBlock = tpuIpv4CidrBlock;
    }

    /**
     * The IP address range of the Kubernetes services in this cluster.
     */
    @Output
    public String getServicesIpv4Cidr() {
        return servicesIpv4Cidr;
    }

    public void setServicesIpv4Cidr(String servicesIpv4Cidr) {
        this.servicesIpv4Cidr = servicesIpv4Cidr;
    }

    /**
     * The size of the address space on each node for hosting containers.
     */
    @Output
    public Integer getNodeIpv4CidrSize() {
        return nodeIpv4CidrSize;
    }

    public void setNodeIpv4CidrSize(Integer nodeIpv4CidrSize) {
        this.nodeIpv4CidrSize = nodeIpv4CidrSize;
    }

    /**
     * The current status of this cluster.
     */
    @Output
    public Cluster.Status getStatus() {
        return status;
    }

    public void setStatus(Cluster.Status status) {
        this.status = status;
    }

    /**
     * The IP address of this cluster's master endpoint.
     */
    @Output
    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    /**
     * Server-defined URL for the resource.
     */
    @Output
    public String getSelfLink() {
        return selfLink;
    }

    public void setSelfLink(String selfLink) {
        this.selfLink = selfLink;
    }

    /**
     * The fingerprint of the set of labels for this cluster.
     */
    @Output
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

        setNodePool(model.getNodePoolsList().stream().map(n -> {
            GkeNodePool gkeNodePool = newSubresource(GkeNodePool.class);
            gkeNodePool.copyFrom(n);
            return gkeNodePool;
        }).collect(Collectors.toList()));

        setLocation(model.getLocation());
        setName(model.getName());
        setDescription(model.getDescription());
        setEnableTpu(model.getEnableTpu());
        setLabels(model.getResourceLabelsMap());
        setTpuIpv4CidrBlock(model.getTpuIpv4CidrBlock());
        setServicesIpv4Cidr(model.getServicesIpv4Cidr());
        setNodeIpv4CidrSize(model.getNodeIpv4CidrSize());
        setStatus(model.getStatus());
        setMasterVersion(model.getCurrentMasterVersion());
        setEndpoint(model.getEndpoint());
        setSelfLink(model.getSelfLink());
        setInitialClusterVersion(model.getInitialClusterVersion());
        setSubnetwork(Optional.ofNullable(Utils.findResourceByField(SubnetworkResource.class,
            findByClass(SubnetworkResource.class), model.getSubnetwork()))
            .orElse(findById(SubnetworkResource.class, model.getSubnetwork())));
        setNodeLocations(model.getLocationsList());
        setEnableKubernetesAlpha(model.getEnableKubernetesAlpha());
        setLoggingService(model.getLoggingService());
        setMonitoringService(model.getMonitoringService());
        setNetwork(Optional.ofNullable(Utils.findResourceByField(NetworkResource.class,
            findByClass(NetworkResource.class), model.getNetwork()))
            .orElse(findById(NetworkResource.class, model.getNetwork())));
        setClusterIpv4Cidr(model.getClusterIpv4Cidr());
        setLabelFingerPrint(model.getLabelFingerprint());
    }

    @Override
    protected boolean doRefresh() throws Exception {
        try (ClusterManagerClient client = createClient(ClusterManagerClient.class)) {
            Cluster cluster = getCluster(client);

            if (cluster == null) {
                return false;
            }

            copyFrom(cluster);

            return true;
        }
    }

    @Override
    protected void doCreate(GyroUI ui, State state) throws Exception {
        Cluster.Builder builder = Cluster.newBuilder().setName(getName()).addAllNodePools(getNodePool().stream()
            .map(GkeNodePool::buildNodePool).collect(Collectors.toList()));

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

        if (getDatabaseEncryption() != null) {
            builder.setDatabaseEncryption(getDatabaseEncryption().toDatabaseEncryption());
        }

        try (ClusterManagerClient client = createClient(ClusterManagerClient.class)) {
            client.createCluster(CreateClusterRequest.newBuilder()
                .setParent(getParent())
                .setCluster(builder.build())
                .build());

            state.save();

            waitForActiveStatus(client);
        }

        state.save();

        doRefresh();
    }

    @Override
    protected void doUpdate(GyroUI ui, State state, Resource current, Set<String> changedFieldNames) throws Exception {
        try (ClusterManagerClient client = createClient(ClusterManagerClient.class)) {

            ClusterUpdate.Builder builder = ClusterUpdate.newBuilder();

            if (changedFieldNames.contains("addons-config")) {
                builder.setDesiredAddonsConfig(getAddonsConfig().toAddonsConfig());
                updateCluster(client, builder);
                builder.clear();
            }

            if (changedFieldNames.contains("master-authorized-networks-config")) {
                builder.setDesiredMasterAuthorizedNetworksConfig(getMasterAuthorizedNetworksConfig().toMasterAuthorizedNetworksConfig());
                updateCluster(client, builder);
                builder.clear();
            }

            if (changedFieldNames.contains("binary-authorization-config")) {
                builder.setDesiredBinaryAuthorization(getBinaryAuthorizationConfig().toBinaryAuthorization());
                updateCluster(client, builder);
                builder.clear();
            }

            if (changedFieldNames.contains("cluster-autoscaling-config")) {
                builder.setDesiredClusterAutoscaling(getClusterAutoscalingConfig().toClusterAutoscaling());
                updateCluster(client, builder);
                builder.clear();
            }

            if (changedFieldNames.contains("database-encryption")) {
                builder.setDesiredDatabaseEncryption(getDatabaseEncryption().toDatabaseEncryption());
                updateCluster(client, builder);
                builder.clear();
            }

            if (getNetworkConfig() != null) {
                if (getNetworkConfig().getDefaultSnatStatus() != null) {
                    builder.setDesiredDefaultSnatStatus(getNetworkConfig().getDefaultSnatStatus()
                        .toDefaultSnatStatus());
                    updateCluster(client, builder);
                    builder.clear();
                }

                if (getNetworkConfig().getEnableIntraNodeVisibility() != null) {
                    builder.setDesiredIntraNodeVisibilityConfig(IntraNodeVisibilityConfig.newBuilder()
                        .setEnabled(getNetworkConfig().getEnableIntraNodeVisibility())
                        .build());
                    updateCluster(client, builder);
                    builder.clear();
                }
            }

            if (changedFieldNames.contains("master-version")) {
                builder.setDesiredMasterVersion(getMasterVersion());
                updateCluster(client, builder);
                builder.clear();
            }

            if (changedFieldNames.contains("resource-usage-export-config")) {
                builder.setDesiredResourceUsageExportConfig(getResourceUsageExportConfig().toResourceUsageExportConfig());
                updateCluster(client, builder);
                builder.clear();
            }

            if (changedFieldNames.contains("private-cluster-config")) {
                builder.setDesiredPrivateClusterConfig(getPrivateClusterConfig().toPrivateClusterConfig());
                updateCluster(client, builder);
                builder.clear();
            }

            if (changedFieldNames.contains("vertical-pod-autoscaling")) {
                builder.setDesiredVerticalPodAutoscaling(getVerticalPodAutoscaling().toVerticalPodAutoscaling());
                updateCluster(client, builder);
                builder.clear();
            }

            if (changedFieldNames.contains("shielded-nodes")) {
                builder.setDesiredShieldedNodes(getShieldedNodes().toShieldedNodes());
                updateCluster(client, builder);
                builder.clear();
            }

            if (changedFieldNames.contains("release-channel")) {
                builder.setDesiredReleaseChannel(getReleaseChannel().toReleaseChannel());
                updateCluster(client, builder);
                builder.clear();
            }

            if (changedFieldNames.contains("workload-identity-config")) {
                builder.setDesiredWorkloadIdentityConfig(getWorkloadIdentityConfig().toWorkloadIdentityConfig());
                updateCluster(client, builder);
                builder.clear();
            }

            if (changedFieldNames.contains("node-locations")) {
                builder.addAllDesiredLocations(getNodeLocations());
                updateCluster(client, builder);
                builder.clear();
            }

            if (changedFieldNames.contains("logging-service")) {
                builder.setDesiredLoggingService(getLoggingService());
                updateCluster(client, builder);
                builder.clear();
            }

            if (changedFieldNames.contains("monitoring-service")) {
                builder.setDesiredMonitoringService(getMonitoringService());
                updateCluster(client, builder);
                builder.clear();
            }

            if (changedFieldNames.contains("labels")) {
                client.setLabels(SetLabelsRequest.newBuilder().setLabelFingerprint(getLabelFingerPrint())
                    .putAllResourceLabels(getLabels()).setName(getClusterId()).build());
            }
        }

        doRefresh();
    }

    @Override
    protected void doDelete(GyroUI ui, State state) throws Exception {
        try (ClusterManagerClient client = createClient(ClusterManagerClient.class)) {
            client.deleteCluster(getClusterId());

            Wait.atMost(5, TimeUnit.MINUTES)
                .checkEvery(1, TimeUnit.MINUTES)
                .until(() -> getCluster(client) == null);
        }
    }

    private void updateCluster(ClusterManagerClient client, ClusterUpdate.Builder builder) {
        client.updateCluster(getClusterId(), builder.build());

        waitForActiveStatus(client);
    }

    private void waitForActiveStatus(ClusterManagerClient client) {
        Wait.atMost(20, TimeUnit.MINUTES)
            .checkEvery(1, TimeUnit.MINUTES)
            .until(() -> getCluster(client).getStatus().equals(Cluster.Status.RUNNING));
    }

    private Cluster getCluster(ClusterManagerClient client) {
        Cluster cluster = null;

        try {
            cluster = client.getCluster(getClusterId());
        } catch (NotFoundException | InvalidArgumentException ex) {
            // ignore
        }

        return cluster;
    }

    protected String getClusterId() {
        return String.format("projects/%s/locations/%s/clusters/%s", getProjectId(), getLocation(), getName());
    }

    protected String getParent() {
        return String.format("projects/%s/locations/%s", getProjectId(), getLocation());
    }
}
