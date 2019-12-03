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

package gyro.google.dns;

import java.util.Optional;

import com.google.api.services.dns.model.PolicyNetwork;
import gyro.core.resource.Diffable;
import gyro.google.Copyable;
import gyro.google.Requestable;
import gyro.google.compute.NetworkResource;

public class DnsPolicyNetwork extends Diffable implements Copyable<PolicyNetwork>,
    Requestable<PolicyNetwork> {

    private NetworkResource network;

    /**
     * The VPC network to bind to. The value may be ``null``.
     *
     * @resource gyro.google.compute.NetworkResource
     */
    public NetworkResource getNetwork() {
        return network;
    }

    public void setNetwork(NetworkResource network) {
        this.network = network;
    }

    @Override
    public void copyFrom(PolicyNetwork model) {
        String networkUrl = model.getNetworkUrl();

        if (networkUrl != null) {
            setNetwork(findById(NetworkResource.class, networkUrl.substring(networkUrl.lastIndexOf("/") + 1)));
        }
    }

    @Override
    public PolicyNetwork copyTo() {
        PolicyNetwork policyNetwork = new PolicyNetwork();
        NetworkResource network = getNetwork();

        if (network != null) {
            policyNetwork.setNetworkUrl(network.getSelfLink());
        }
        return policyNetwork;
    }

    @Override
    public String primaryKey() {
        return Optional.ofNullable(getNetwork())
            .map(NetworkResource::primaryKey)
            .orElse(super.primaryKey());
    }

    public boolean isEqualTo(PolicyNetwork network) {
        return Optional.ofNullable(network)
            .map(PolicyNetwork::getNetworkUrl)
            .filter(networkUrl -> Optional.ofNullable(getNetwork())
                .filter(e -> networkUrl.equals(e.getSelfLink()))
                .isPresent())
            .isPresent();
    }
}
