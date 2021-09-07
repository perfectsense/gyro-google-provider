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

import com.google.container.v1.IPAllocationPolicy;
import gyro.core.resource.Diffable;
import gyro.google.Copyable;

public class GkeIpAllocationPolicy extends Diffable implements Copyable<IPAllocationPolicy> {

    // TODO: A lot of validations

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

    public Boolean getUseIpAliases() {
        return useIpAliases;
    }

    public void setUseIpAliases(Boolean useIpAliases) {
        this.useIpAliases = useIpAliases;
    }

    public Boolean getCreateSubnetwork() {
        return createSubnetwork;
    }

    public void setCreateSubnetwork(Boolean createSubnetwork) {
        this.createSubnetwork = createSubnetwork;
    }

    public String getSubnetworkName() {
        return subnetworkName;
    }

    public void setSubnetworkName(String subnetworkName) {
        this.subnetworkName = subnetworkName;
    }

    public String getClusterSecondaryRangeName() {
        return clusterSecondaryRangeName;
    }

    public void setClusterSecondaryRangeName(String clusterSecondaryRangeName) {
        this.clusterSecondaryRangeName = clusterSecondaryRangeName;
    }

    public String getServicesSecondaryRangeName() {
        return servicesSecondaryRangeName;
    }

    public void setServicesSecondaryRangeName(String servicesSecondaryRangeName) {
        this.servicesSecondaryRangeName = servicesSecondaryRangeName;
    }

    public String getClusterIpv4CidrBlock() {
        return clusterIpv4CidrBlock;
    }

    public void setClusterIpv4CidrBlock(String clusterIpv4CidrBlock) {
        this.clusterIpv4CidrBlock = clusterIpv4CidrBlock;
    }

    public String getNodeIpv4CidrBlock() {
        return nodeIpv4CidrBlock;
    }

    public void setNodeIpv4CidrBlock(String nodeIpv4CidrBlock) {
        this.nodeIpv4CidrBlock = nodeIpv4CidrBlock;
    }

    public String getServicesIpv4CidrBlock() {
        return servicesIpv4CidrBlock;
    }

    public void setServicesIpv4CidrBlock(String servicesIpv4CidrBlock) {
        this.servicesIpv4CidrBlock = servicesIpv4CidrBlock;
    }

    public String getTpuIpv4CidrBlock() {
        return tpuIpv4CidrBlock;
    }

    public void setTpuIpv4CidrBlock(String tpuIpv4CidrBlock) {
        this.tpuIpv4CidrBlock = tpuIpv4CidrBlock;
    }

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
        return IPAllocationPolicy.newBuilder().setUseIpAliases(getUseIpAliases())
            .setCreateSubnetwork(getCreateSubnetwork()).setSubnetworkName(getSubnetworkName())
            .setClusterSecondaryRangeName(getClusterSecondaryRangeName())
            .setServicesSecondaryRangeName(getServicesSecondaryRangeName())
            .setClusterIpv4CidrBlock(getClusterIpv4CidrBlock()).setNodeIpv4CidrBlock(getNodeIpv4CidrBlock())
            .setServicesIpv4CidrBlock(getServicesIpv4CidrBlock()).setTpuIpv4CidrBlock(getTpuIpv4CidrBlock())
            .setUseRoutes(getUseRoutes()).build();
    }
}
