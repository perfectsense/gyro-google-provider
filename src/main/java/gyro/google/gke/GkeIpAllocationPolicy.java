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
import java.util.Set;

import com.google.container.v1.IPAllocationPolicy;
import gyro.core.resource.Diffable;
import gyro.core.validation.Required;
import gyro.core.validation.ValidationError;
import gyro.google.Copyable;

public class GkeIpAllocationPolicy extends Diffable implements Copyable<IPAllocationPolicy> {

    private Boolean useIpAliases;
    private Boolean createSubnetwork;
    private String subnetworkName;
    private String clusterSecondaryRangeName;
    private String servicesSecondaryRangeName;
    private String clusterIpv4CidrBlock;
    private String nodeIpv4CidrBlock;
    private String servicesIpv4CidrBlock;
    private String tpuIpv4CidrBlock;
    private Boolean useRoutes;

    /**
     * When set to ``true``, alias IPs will be used for pod IPs in the cluster..
     */
    @Required
    public Boolean getUseIpAliases() {
        return useIpAliases;
    }

    public void setUseIpAliases(Boolean useIpAliases) {
        this.useIpAliases = useIpAliases;
    }

    /**
     * When set to ``true``, a new subnetwork will be created automatically for the cluster.
     */
    public Boolean getCreateSubnetwork() {
        return createSubnetwork;
    }

    public void setCreateSubnetwork(Boolean createSubnetwork) {
        this.createSubnetwork = createSubnetwork;
    }

    /**
     * The name for the subnetwork that should be created.
     */
    public String getSubnetworkName() {
        return subnetworkName;
    }

    public void setSubnetworkName(String subnetworkName) {
        this.subnetworkName = subnetworkName;
    }

    /**
     * The name of the secondary range to be used for the cluster CIDR block.
     */
    public String getClusterSecondaryRangeName() {
        return clusterSecondaryRangeName;
    }

    public void setClusterSecondaryRangeName(String clusterSecondaryRangeName) {
        this.clusterSecondaryRangeName = clusterSecondaryRangeName;
    }

    /**
     * The name of the secondary range to be used for the services CIDR block.
     */
    public String getServicesSecondaryRangeName() {
        return servicesSecondaryRangeName;
    }

    public void setServicesSecondaryRangeName(String servicesSecondaryRangeName) {
        this.servicesSecondaryRangeName = servicesSecondaryRangeName;
    }

    /**
     * The IP address range for the cluster pod IPs.
     */
    public String getClusterIpv4CidrBlock() {
        return clusterIpv4CidrBlock;
    }

    public void setClusterIpv4CidrBlock(String clusterIpv4CidrBlock) {
        this.clusterIpv4CidrBlock = clusterIpv4CidrBlock;
    }

    /**
     * The IP address range of the instance IPs in this cluster.
     */
    public String getNodeIpv4CidrBlock() {
        return nodeIpv4CidrBlock;
    }

    public void setNodeIpv4CidrBlock(String nodeIpv4CidrBlock) {
        this.nodeIpv4CidrBlock = nodeIpv4CidrBlock;
    }

    /**
     * The IP address range of the services IPs in this cluster.
     */
    public String getServicesIpv4CidrBlock() {
        return servicesIpv4CidrBlock;
    }

    public void setServicesIpv4CidrBlock(String servicesIpv4CidrBlock) {
        this.servicesIpv4CidrBlock = servicesIpv4CidrBlock;
    }

    /**
     * The IP address range of the Cloud TPUs in this cluster.
     */
    public String getTpuIpv4CidrBlock() {
        return tpuIpv4CidrBlock;
    }

    public void setTpuIpv4CidrBlock(String tpuIpv4CidrBlock) {
        this.tpuIpv4CidrBlock = tpuIpv4CidrBlock;
    }

    /**
     * When set to ``true``, routes will be used for pod IPs in the cluster.
     */
    public Boolean getUseRoutes() {
        return useRoutes;
    }

    public void setUseRoutes(Boolean useRoutes) {
        this.useRoutes = useRoutes;
    }

    @Override
    public String primaryKey() {
        return "";
    }

    @Override
    public void copyFrom(IPAllocationPolicy model) throws Exception {
        setUseIpAliases(model.getUseIpAliases());
        setCreateSubnetwork(model.getCreateSubnetwork());
        setSubnetworkName(model.getSubnetworkName());
        setClusterSecondaryRangeName(model.getClusterSecondaryRangeName());
        setServicesSecondaryRangeName(model.getServicesSecondaryRangeName());
        setClusterIpv4CidrBlock(model.getClusterIpv4CidrBlock());
        setNodeIpv4CidrBlock(model.getNodeIpv4CidrBlock());
        setServicesIpv4CidrBlock(model.getServicesIpv4CidrBlock());
        setTpuIpv4CidrBlock(model.getTpuIpv4CidrBlock());
        setUseRoutes(model.getUseRoutes());
    }

    IPAllocationPolicy toIPAllocationPolicy() {
        IPAllocationPolicy.Builder builder = IPAllocationPolicy.newBuilder();
        if (getUseIpAliases() != null) {
            builder.setUseIpAliases(getUseIpAliases());
        }
        if (getCreateSubnetwork() != null) {
            builder.setCreateSubnetwork(getCreateSubnetwork());
        }
        if (getSubnetworkName() != null) {
            builder.setSubnetworkName(getSubnetworkName());
        }
        if (getClusterSecondaryRangeName() != null) {
            builder.setClusterSecondaryRangeName(getClusterSecondaryRangeName());
        }
        if (getServicesSecondaryRangeName() != null) {
            builder.setServicesSecondaryRangeName(getServicesSecondaryRangeName());
        }
        if (getClusterIpv4CidrBlock() != null) {
            builder.setClusterIpv4CidrBlock(getClusterIpv4CidrBlock());
        }
        if (getNodeIpv4CidrBlock() != null) {
            builder.setNodeIpv4CidrBlock(getNodeIpv4CidrBlock());
        }
        if (getServicesIpv4CidrBlock() != null) {
            builder.setServicesIpv4CidrBlock(getServicesIpv4CidrBlock());
        }
        if (getTpuIpv4CidrBlock() != null) {
            builder.setTpuIpv4CidrBlock(getTpuIpv4CidrBlock());
        }
        if (getUseRoutes() != null) {
            builder.setUseRoutes(getUseRoutes());
        }

        return builder.build();
    }

    @Override
    public List<ValidationError> validate(Set<String> configuredFields) {
        ArrayList<ValidationError> errors = new ArrayList<>();

        if (configuredFields.contains("create-subnetwork") && !Boolean.TRUE.equals(getUseIpAliases())) {
            errors.add(new ValidationError(this, "create-subnetwork",
                "'create-subnetwork' can only be set if 'use-ip-aliases' is set to 'true'"));
        }

        if (configuredFields.contains("subnetwork-name") && !Boolean.TRUE.equals(getCreateSubnetwork())) {
            errors.add(new ValidationError(this, "subnetwork-name",
                "'subnetwork-name' can only be set if 'create-subnetwork' is set to 'true'"));
        }

        if (configuredFields.contains("cluster-secondary-range-name") && !Boolean.TRUE.equals(getUseIpAliases())
            && Boolean.TRUE.equals(getCreateSubnetwork())) {
            errors.add(new ValidationError(this, "cluster-secondary-range-name",
                "'cluster-secondary-range-name' can only be set if 'use-ip-aliases' is set to "
                    + "'true' and 'create-subnetwork' is set to 'false'"));
        }

        if (configuredFields.contains("services-secondary-range-name") && !Boolean.TRUE.equals(getUseIpAliases())
            && Boolean.TRUE.equals(getCreateSubnetwork())) {
            errors.add(new ValidationError(this, "services-secondary-range-name",
                "'services-secondary-range-name' can only be set if 'use-ip-aliases' is set to "
                    + "'true' and 'create-subnetwork' is set to 'false'"));
        }

        if (configuredFields.contains("cluster-ipv4-cidr-block") && !Boolean.TRUE.equals(getUseIpAliases())) {
            errors.add(new ValidationError(this, "cluster-ipv4-cidr-block",
                "'cluster-ipv4-cidr-block' can only be set if 'use-ip-aliases' is set to 'true'"));
        }

        if (configuredFields.contains("node-ipv4-cidr-block") && !Boolean.TRUE.equals(getCreateSubnetwork())) {
            errors.add(new ValidationError(this, "node-ipv4-cidr-block",
                "'node-ipv4-cidr-block' can only be set if 'create-subnetwork' is set to 'true'"));
        }

        if (configuredFields.contains("services-ipv4-cidr-block") && !Boolean.TRUE.equals(getUseIpAliases())) {
            errors.add(new ValidationError(this, "services-ipv4-cidr-block",
                "'services-ipv4-cidr-block' can only be set if 'use-ip-aliases' is set to 'true'"));
        }

        if (configuredFields.contains("tpu-ipv4-cidr-block") && !Boolean.TRUE.equals(getUseIpAliases())) {
            errors.add(new ValidationError(this, "tpu-ipv4-cidr-block",
                "'tpu-ipv4-cidr-block' can only be set if 'use-ip-aliases' is set to 'true'"));
        }

        if (configuredFields.contains("use-routes") && Boolean.TRUE.equals(getUseRoutes()) && Boolean.TRUE.equals(
            getUseIpAliases())) {
            errors.add(new ValidationError(this, null,
                "both 'use-routes' and 'use-ip-aliases' cannot be set to 'true'"));
        }

        return errors;
    }
}
