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

import com.google.container.v1.PrivateClusterConfig;
import gyro.core.resource.Diffable;
import gyro.core.resource.Output;
import gyro.core.resource.Updatable;
import gyro.core.validation.Required;
import gyro.google.Copyable;

public class GkePrivateClusterConfig extends Diffable implements Copyable<PrivateClusterConfig> {

    private Boolean enablePrivateNodes;
    private Boolean enablePrivateEndpoint;
    private String masterIpv4CidrBlock;
    private GkePrivateClusterMasterGlobalAccessConfig masterGlobalAccessConfig;

    // Read-only
    private String privateEndpoint;
    private String publicEndpoint;
    private String peeringName;

    /**
     * When set to ``true`` the nodes have internal IP addresses only and all nodes are given only RFC 1918 private addresses and communicate with the master via private networking.
     */
    @Updatable
    public Boolean getEnablePrivateNodes() {
        return enablePrivateNodes;
    }

    public void setEnablePrivateNodes(Boolean enablePrivateNodes) {
        this.enablePrivateNodes = enablePrivateNodes;
    }

    /**
     * When set to ``true`` the master's internal IP address is used as the cluster endpoint.
     */
    @Updatable
    public Boolean getEnablePrivateEndpoint() {
        return enablePrivateEndpoint;
    }

    public void setEnablePrivateEndpoint(Boolean enablePrivateEndpoint) {
        this.enablePrivateEndpoint = enablePrivateEndpoint;
    }

    /**
     * The IP range in CIDR notation to use for the hosted master network. This range will be used for assigning internal IP addresses to the master or set of masters, as well as the ILB VIP. This range must not overlap with any other ranges in use within the cluster's network.
     */
    @Required
    @Updatable
    public String getMasterIpv4CidrBlock() {
        return masterIpv4CidrBlock;
    }

    public void setMasterIpv4CidrBlock(String masterIpv4CidrBlock) {
        this.masterIpv4CidrBlock = masterIpv4CidrBlock;
    }

    /**
     * The master global access settings.
     */
    @Updatable
    public GkePrivateClusterMasterGlobalAccessConfig getMasterGlobalAccessConfig() {
        return masterGlobalAccessConfig;
    }

    public void setMasterGlobalAccessConfig(GkePrivateClusterMasterGlobalAccessConfig masterGlobalAccessConfig) {
        this.masterGlobalAccessConfig = masterGlobalAccessConfig;
    }

    /**
     * The internal IP address of this cluster's master endpoint.
     */
    @Output
    public String getPrivateEndpoint() {
        return privateEndpoint;
    }

    public void setPrivateEndpoint(String privateEndpoint) {
        this.privateEndpoint = privateEndpoint;
    }

    /**
     * The external IP address of this cluster's master endpoint.
     */
    @Output
    public String getPublicEndpoint() {
        return publicEndpoint;
    }

    public void setPublicEndpoint(String publicEndpoint) {
        this.publicEndpoint = publicEndpoint;
    }

    /**
     * The peering name in the customer VPC used by this cluster.
     */
    @Output
    public String getPeeringName() {
        return peeringName;
    }

    public void setPeeringName(String peeringName) {
        this.peeringName = peeringName;
    }

    @Override
    public String primaryKey() {
        return "";
    }

    @Override
    public void copyFrom(PrivateClusterConfig model) throws Exception {
        setEnablePrivateNodes(model.getEnablePrivateNodes());
        setEnablePrivateEndpoint(model.getEnablePrivateEndpoint());
        setMasterIpv4CidrBlock(model.getMasterIpv4CidrBlock());
        setPrivateEndpoint(model.getPrivateEndpoint());
        setPublicEndpoint(model.getPublicEndpoint());
        setPeeringName(model.getPeeringName());

        setMasterGlobalAccessConfig(null);
        if (model.hasMasterGlobalAccessConfig()) {
            GkePrivateClusterMasterGlobalAccessConfig config =
                newSubresource(GkePrivateClusterMasterGlobalAccessConfig.class);
            config.copyFrom(model.getMasterGlobalAccessConfig());
            setMasterGlobalAccessConfig(config);
        }
    }

    PrivateClusterConfig toPrivateClusterConfig() {
        PrivateClusterConfig.Builder builder = PrivateClusterConfig.newBuilder()
            .setMasterIpv4CidrBlock(getMasterIpv4CidrBlock());

        if (getEnablePrivateEndpoint() != null) {
            builder.setEnablePrivateEndpoint(getEnablePrivateEndpoint());
        }

        if (getEnablePrivateNodes() != null) {
            builder.setEnablePrivateNodes(getEnablePrivateNodes());
        }

        if (getMasterGlobalAccessConfig() != null) {
            builder.setMasterGlobalAccessConfig(getMasterGlobalAccessConfig()
                .toPrivateClusterMasterGlobalAccessConfig());
        }

        return builder.build();
    }
}
