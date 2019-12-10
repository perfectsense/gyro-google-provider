/*
 * Copyright 2019, Perfect Sense, Inc.
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

import java.util.List;

import com.google.api.services.compute.model.NetworkInterface;
import gyro.core.resource.Updatable;
import gyro.google.Copyable;

public class InstanceNetworkInterface implements Copyable<NetworkInterface> {

    private NetworkResource network;
    private SubnetworkResource subnetwork;
    private String networkIp;
    private List<InstanceAccessConfig> accessConfigs;
    private List<InstanceAliasIpRange> aliasIpRanges;
    private String fingerprint;

    /**
     * Network resource for this instance. If neither the network or subnetwork is specified, the default network ``global/networks/default`` is used and if the network is not specified but the subnetwork is specified, the network is inferred.
     */
    @Updatable
    public NetworkResource getNetwork() {
        return network;
    }

    public void setNetwork(NetworkResource network) {
        this.network = network;
    }

    /**
     * Subnetwork resource for this instance. If the network resource is in legacy mode, do not specify this field. If the network is in auto subnet mode, specifying the subnetwork is optional. If the network is in custom subnet mode, specifying the subnetwork is required.
     */
    @Updatable
    public SubnetworkResource getSubnetwork() {
        return subnetwork;
    }

    public void setSubnetwork(SubnetworkResource subnetwork) {
        this.subnetwork = subnetwork;
    }

    /**
     * An IPv4 internal IP address to assign for this network interface. If unspecified an unused internal IP is assigned.
     */
    @Updatable
    public String getNetworkIp() {
        return networkIp;
    }

    public void setNetworkIp(String networkIp) {
        this.networkIp = networkIp;
    }

    /**
     * Array of configurations for this interface. Currently, only ``NE_TO_ONE_NAT`` is supported. If unspecified this instance will have no external internet access.
     */
    @Updatable
    public List<InstanceAccessConfig> getAccessConfigs() {
        return accessConfigs;
    }

    public void setAccessConfigs(List<InstanceAccessConfig> accessConfigs) {
        this.accessConfigs = accessConfigs;
    }

    /**
     * Array of alias IP ranges for this network interface. Can only specify this for network interfaces in VPC networks.
     */
    @Updatable
    public List<InstanceAliasIpRange> getAliasIpRanges() {
        return aliasIpRanges;
    }

    public void setAliasIpRanges(List<InstanceAliasIpRange> aliasIpRanges) {
        this.aliasIpRanges = aliasIpRanges;
    }

    /**
     * Fingerprint hash of contents stored in this network interface. Will be ignored when inserting an Instance or adding a NetworkInterface. An up-to-date fingerprint must be provided in order to update the NetworkInterface, otherwise the request will fail with HTTP error 412.
     */
    @Updatable
    public String getFingerprint() {
        return fingerprint;
    }

    public void setFingerprint(String fingerprint) {
        this.fingerprint = fingerprint;
    }

    @Override
    public void copyFrom(NetworkInterface model) {
    }
}
