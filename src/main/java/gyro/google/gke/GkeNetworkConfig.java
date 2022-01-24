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

import java.util.Optional;

import com.google.container.v1.NetworkConfig;
import gyro.core.resource.Diffable;
import gyro.core.resource.Output;
import gyro.core.resource.Updatable;
import gyro.google.Copyable;
import gyro.google.compute.NetworkResource;
import gyro.google.compute.SubnetworkResource;
import gyro.google.util.Utils;

public class GkeNetworkConfig extends Diffable implements Copyable<NetworkConfig> {

    private Boolean enableIntraNodeVisibility;
    private GkeDefaultSnatStatus defaultSnatStatus;

    // Read-only
    private NetworkResource network;
    private SubnetworkResource subnetwork;

    /**
     * When set to ``true``, the Intra-node visibility is enabled for this cluster.
     */
    @Updatable
    public Boolean getEnableIntraNodeVisibility() {
        return enableIntraNodeVisibility;
    }

    public void setEnableIntraNodeVisibility(Boolean enableIntraNodeVisibility) {
        this.enableIntraNodeVisibility = enableIntraNodeVisibility;
    }

    /**
     * The configuration for the default in-node SNAT rules.
     *
     * @subresource gyro.google.gke.GkeDefaultSnatStatus
     */
    @Updatable
    public GkeDefaultSnatStatus getDefaultSnatStatus() {
        return defaultSnatStatus;
    }

    public void setDefaultSnatStatus(GkeDefaultSnatStatus defaultSnatStatus) {
        this.defaultSnatStatus = defaultSnatStatus;
    }

    /**
     * The Google Compute Engine network to which the cluster is connected.
     */
    @Output
    public NetworkResource getNetwork() {
        return network;
    }

    public void setNetwork(NetworkResource network) {
        this.network = network;
    }

    /**
     * The Google Compute Engine subnetwork to which the cluster is connected.
     */
    @Output
    public SubnetworkResource getSubnetwork() {
        return subnetwork;
    }

    public void setSubnetwork(SubnetworkResource subnetwork) {
        this.subnetwork = subnetwork;
    }

    @Override
    public String primaryKey() {
        return "";
    }

    @Override
    public void copyFrom(NetworkConfig model) throws Exception {
        setEnableIntraNodeVisibility(model.getEnableIntraNodeVisibility());
        setSubnetwork(Optional.ofNullable(Utils.findResourceByField(SubnetworkResource.class,
            findByClass(SubnetworkResource.class), model.getSubnetwork()))
            .orElse(findById(SubnetworkResource.class, model.getSubnetwork())));
        setNetwork(Optional.ofNullable(Utils.findResourceByField(NetworkResource.class,
            findByClass(NetworkResource.class), model.getNetwork()))
            .orElse(findById(NetworkResource.class, model.getNetwork())));

        setDefaultSnatStatus(null);
        if (model.hasDefaultSnatStatus()) {
            GkeDefaultSnatStatus status = newSubresource(GkeDefaultSnatStatus.class);
            status.copyFrom(model.getDefaultSnatStatus());
            setDefaultSnatStatus(status);
        }
    }

    NetworkConfig toNetworkConfig() {
        NetworkConfig.Builder builder = NetworkConfig.newBuilder();

        if (getEnableIntraNodeVisibility() != null) {
            builder.setEnableIntraNodeVisibility(getEnableIntraNodeVisibility());
        }

        if (getDefaultSnatStatus() != null) {
            builder.setDefaultSnatStatus(getDefaultSnatStatus().toDefaultSnatStatus());
        }

        return builder.build();
    }
}
