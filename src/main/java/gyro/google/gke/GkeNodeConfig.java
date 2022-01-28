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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.container.v1beta1.NodeConfig;
import gyro.core.resource.Diffable;
import gyro.core.resource.Updatable;
import gyro.core.validation.Min;
import gyro.google.Copyable;
import gyro.google.kms.CryptoKeyResource;

public class GkeNodeConfig extends Diffable implements Copyable<NodeConfig> {

    private String machineType;
    private Integer diskSizeGb;
    private Set<String> oauthScopes;
    private String serviceAccount;
    private Map<String, String> metadata;
    private String imageType;
    private Map<String, String> labels;
    private Integer localSsdCount;
    private List<String> tags;
    private Boolean preemptible;
    private List<GkeAcceleratorConfig> accelerators;
    private String diskType;
    private String minCpuPlatform;
    private GkeWorkloadMetadataConfig workloadMetadataConfig;
    private Set<GkeNodeTaint> taint;
    private GkeSandboxConfig sandboxConfig;
    private String nodeGroup;
    private GkeReservationAffinity reservationAffinity;
    private CryptoKeyResource bootDiskKmsKey;

    /**
     * The name of a Google Compute Engine machine type. Defaults to ``e2-medium``.
     */
    public String getMachineType() {
        return machineType;
    }

    public void setMachineType(String machineType) {
        this.machineType = machineType;
    }

    /**
     * The size of the disk attached to each node, specified in GB. The smallest allowed disk size is ``10``. Defaults to ``100``.
     */
    @Min(10)
    public Integer getDiskSizeGb() {
        return diskSizeGb;
    }

    public void setDiskSizeGb(Integer diskSizeGb) {
        this.diskSizeGb = diskSizeGb;
    }

    /**
     * The set of Google API scopes to be made available on all of the node VMs under the default service account.
     * The following scopes are recommended, but not required, and by default are not included:
     * `https://www.googleapis.com/auth/compute`_ is required for mounting persistent storage on your nodes.
     * `https://www.googleapis.com/auth/devstorage.read_only`_ is required for communicating with gcr.io (the Google Container Registry).
     * If unspecified, no scopes are added, unless Cloud Logging or Cloud Monitoring are enabled, in which case their required scopes will be added.
     */
    public Set<String> getOauthScopes() {
        if (oauthScopes == null) {
            oauthScopes = new HashSet<>();
        }

        return oauthScopes;
    }

    public void setOauthScopes(Set<String> oauthScopes) {
        this.oauthScopes = oauthScopes;
    }

    /**
     * The Google Cloud Platform Service Account to be used by the node VMs. Specify the email address of the Service Account; otherwise, if no Service Account is specified, the "default" service account is used.
     */
    public String getServiceAccount() {
        return serviceAccount;
    }

    public void setServiceAccount(String serviceAccount) {
        this.serviceAccount = serviceAccount;
    }

    /**
     * The metadata key/value pairs assigned to instances in the cluster.
     */
    public Map<String, String> getMetadata() {
        if (metadata == null) {
            metadata = new HashMap<>();
        }

        return metadata;
    }

    public void setMetadata(Map<String, String> metadata) {
        this.metadata = metadata;
    }

    /**
     * The image type to use for this node. Note that for a given image type, the latest version of it will be used.
     */
    @Updatable
    public String getImageType() {
        return imageType;
    }

    public void setImageType(String imageType) {
        this.imageType = imageType;
    }

    /**
     * The map of Kubernetes labels (key/value pairs) to be applied to each node. These will added in addition to any default label(s) that Kubernetes may apply to the node. In case of conflict in label keys, the applied set may differ depending on the Kubernetes version -- it's best to assume the behavior is undefined and conflicts should be avoided. For more information, including usage and the valid values, see `Working with objects <https://kubernetes.io/docs/concepts/overview/working-with-objects/labels/>`_.
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
     * The number of local SSD disks to be attached to the node. The limit for this value is dependent upon the maximum number of disks available on a machine per zone. See also `Local SSD <https://cloud.google.com/compute/docs/disks/local-ssd>`_.
     */
    public Integer getLocalSsdCount() {
        return localSsdCount;
    }

    public void setLocalSsdCount(Integer localSsdCount) {
        this.localSsdCount = localSsdCount;
    }

    /**
     * The list of instance tags applied to all nodes. Tags are used to identify valid sources or targets for network firewalls and are specified by the client during cluster or node pool creation. Each tag within the list must comply with RFC1035.
     */
    public List<String> getTags() {
        if (tags == null) {
            tags = new ArrayList<>();
        }

        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    /**
     * When set to ``true``, the nodes are created as preemptible VM instances. See also `Preemptible <https://cloud.google.com/compute/docs/instances/preemptible>_.
     */
    public Boolean getPreemptible() {
        return preemptible;
    }

    public void setPreemptible(Boolean preemptible) {
        this.preemptible = preemptible;
    }

    /**
     * The list of hardware accelerators to be attached to each node. See also `GPUs <https://cloud.google.com/compute/docs/gpus>`_.
     *
     * @subresource gyro.google.gke.GkeAcceleratorConfig
     */
    public List<GkeAcceleratorConfig> getAccelerators() {
        if (accelerators == null) {
            accelerators = new ArrayList<>();
        }

        return accelerators;
    }

    public void setAccelerators(List<GkeAcceleratorConfig> accelerators) {
        this.accelerators = accelerators;
    }

    /**
     * Type of the disk attached to each node. Defaults to ``pd-standard``.
     */
    public String getDiskType() {
        return diskType;
    }

    public void setDiskType(String diskType) {
        this.diskType = diskType;
    }

    /**
     * Minimum CPU platform to be used by this instance. The instance may be scheduled on the specified or newer CPU platform. Applicable values are the friendly names of CPU platforms, such as minCpuPlatform: "Intel Haswell" or minCpuPlatform: "Intel Sandy Bridge".
     */
    public String getMinCpuPlatform() {
        return minCpuPlatform;
    }

    public void setMinCpuPlatform(String minCpuPlatform) {
        this.minCpuPlatform = minCpuPlatform;
    }

    /**
     * The workload metadata configuration for this node.
     *
     * @subresource gyro.google.gke.GkeWorkloadMetadataConfig
     */
    @Updatable
    public GkeWorkloadMetadataConfig getWorkloadMetadataConfig() {
        return workloadMetadataConfig;
    }

    public void setWorkloadMetadataConfig(GkeWorkloadMetadataConfig workloadMetadataConfig) {
        this.workloadMetadataConfig = workloadMetadataConfig;
    }

    /**
     * The list of kubernetes taints to be applied to each node. See also `Taint and toleration <https://kubernetes.io/docs/concepts/configuration/taint-and-toleration/>`_.
     *
     * @subresource gyro.google.gke.GkeNodeTaint
     */
    public Set<GkeNodeTaint> getTaint() {
        if (taint == null) {
            taint = new HashSet<>();
        }

        return taint;
    }

    public void setTaint(Set<GkeNodeTaint> taint) {
        this.taint = taint;
    }

    /**
     * The sandbox configuration for this node.
     *
     * @subresource gyro.google.gke.GkeSandboxConfig
     */
    public GkeSandboxConfig getSandboxConfig() {
        return sandboxConfig;
    }

    public void setSandboxConfig(GkeSandboxConfig sandboxConfig) {
        this.sandboxConfig = sandboxConfig;
    }

    /**
     * The node group on which to run the instances of this pool. This is useful for running workloads on sole tenant nodes.
     */
    public String getNodeGroup() {
        return nodeGroup;
    }

    public void setNodeGroup(String nodeGroup) {
        this.nodeGroup = nodeGroup;
    }

    /**
     * The optional reservation affinity. Setting this field will apply the specified Zonal Compute Reservation to this node pool.
     *
     * @subresource gyro.google.gke.GkeReservationAffinity
     */
    public GkeReservationAffinity getReservationAffinity() {
        return reservationAffinity;
    }

    public void setReservationAffinity(GkeReservationAffinity reservationAffinity) {
        this.reservationAffinity = reservationAffinity;
    }

    /**
     * The Customer Managed Encryption Key used to encrypt the boot disk attached to each node in the node pool.
     */
    public CryptoKeyResource getBootDiskKmsKey() {
        return bootDiskKmsKey;
    }

    public void setBootDiskKmsKey(CryptoKeyResource bootDiskKmsKey) {
        this.bootDiskKmsKey = bootDiskKmsKey;
    }

    @Override
    public String primaryKey() {
        return "";
    }

    @Override
    public void copyFrom(NodeConfig model) {
        setMachineType(model.getMachineType());
        setDiskSizeGb(model.getDiskSizeGb());
        setOauthScopes(new HashSet<>(model.getOauthScopesList()));
        setServiceAccount(model.getServiceAccount());
        setMetadata(model.getMetadataMap());
        setImageType(model.getImageType());
        setLabels(model.getLabelsMap().entrySet().stream()
            .filter(a -> !a.getKey().equals(GkeSandboxConfig.SANDBOX_KEY))
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));
        setLocalSsdCount(model.getLocalSsdCount());
        setTags(model.getTagsList());
        setPreemptible(model.getPreemptible());
        setDiskType(model.getDiskType());
        setMinCpuPlatform(model.getMinCpuPlatform());
        setNodeGroup(model.getNodeGroup());
        setBootDiskKmsKey(findById(CryptoKeyResource.class, model.getBootDiskKmsKey()));

        setAccelerators(null);
        if (model.getAcceleratorsCount() > 0) {
            setAccelerators(model.getAcceleratorsList().stream().map(a -> {
                GkeAcceleratorConfig config = newSubresource(GkeAcceleratorConfig.class);
                config.copyFrom(a);
                return config;
            }).collect(Collectors.toList()));
        }

        setTaint(null);
        if (model.getTaintsCount() > 0) {
            setTaint(model.getTaintsList()
                .stream()
                .filter(a -> !a.getKey().equals(GkeSandboxConfig.SANDBOX_KEY))
                .map(a -> {
                    GkeNodeTaint config = newSubresource(GkeNodeTaint.class);
                    config.copyFrom(a);
                    return config;
                })
                .collect(Collectors.toSet()));
        }

        setWorkloadMetadataConfig(null);
        if (model.hasWorkloadMetadataConfig()) {
            GkeWorkloadMetadataConfig config = newSubresource(GkeWorkloadMetadataConfig.class);
            config.copyFrom(model.getWorkloadMetadataConfig());
            setWorkloadMetadataConfig(config);
        }

        setSandboxConfig(null);
        if (model.hasSandboxConfig()) {
            GkeSandboxConfig config = newSubresource(GkeSandboxConfig.class);
            config.copyFrom(model.getSandboxConfig());
            setSandboxConfig(config);
        }

        setReservationAffinity(null);
        if (model.hasWorkloadMetadataConfig()) {
            GkeReservationAffinity config = newSubresource(GkeReservationAffinity.class);
            config.copyFrom(model.getReservationAffinity());
            setReservationAffinity(config);
        }
    }

    NodeConfig toNodeConfig() {
        NodeConfig.Builder builder = NodeConfig.newBuilder();

        if (getMachineType() != null) {
            builder.setMachineType(getMachineType());
        }
        if (getDiskSizeGb() != null) {
            builder.setDiskSizeGb(getDiskSizeGb());
        }
        if (!getOauthScopes().isEmpty()) {
            builder.addAllOauthScopes(getOauthScopes());
        }
        if (getServiceAccount() != null) {
            builder.setServiceAccount(getServiceAccount());
        }
        if (!getMetadata().isEmpty()) {
            builder.putAllMetadata(getMetadata());
        }
        if (getImageType() != null) {
            builder.setImageType(getImageType());
        }
        if (!getLabels().isEmpty()) {
            builder.putAllLabels(getLabels());
        }
        if (getLocalSsdCount() != null) {
            builder.setLocalSsdCount(getLocalSsdCount());
        }
        if (!getTags().isEmpty()) {
            builder.addAllTags(getTags());
        }
        if (getPreemptible() != null) {
            builder.setPreemptible(getPreemptible());
        }
        if (!getAccelerators().isEmpty()) {
            builder.addAllAccelerators(getAccelerators().stream().map(GkeAcceleratorConfig::toAcceleratorConfig)
                .collect(Collectors.toList()));
        }
        if (getDiskType() != null) {
            builder.setDiskType(getDiskType());
        }
        if (getMinCpuPlatform() != null) {
            builder.setMinCpuPlatform(getMinCpuPlatform());
        }
        if (getWorkloadMetadataConfig() != null) {
            builder.setWorkloadMetadataConfig(getWorkloadMetadataConfig().toWorkloadMetadataConfig());
        }
        if (!getTaint().isEmpty()) {
            builder.addAllTaints(getTaint().stream().map(GkeNodeTaint::toNodeTaint).collect(Collectors.toList()));
        }
        if (getSandboxConfig() != null) {
            builder.setSandboxConfig(getSandboxConfig().toSandboxConfig());
        }
        if (getNodeGroup() != null) {
            builder.setNodeGroup(getNodeGroup());
        }
        if (getReservationAffinity() != null) {
            builder.setReservationAffinity(getReservationAffinity().toReservationAffinity());
        }
        if (getBootDiskKmsKey() != null) {
            builder.setBootDiskKmsKey(getBootDiskKmsKey().getId());
        }

        return builder.build();
    }
}
