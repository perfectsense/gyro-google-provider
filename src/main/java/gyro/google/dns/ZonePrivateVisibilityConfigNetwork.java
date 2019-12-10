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

import com.google.api.services.dns.model.ManagedZonePrivateVisibilityConfigNetwork;
import gyro.core.resource.Diffable;
import gyro.core.validation.Required;
import gyro.google.Copyable;
import gyro.google.compute.NetworkResource;

public class ZonePrivateVisibilityConfigNetwork extends Diffable
    implements Copyable<ManagedZonePrivateVisibilityConfigNetwork> {

    private NetworkResource network;

    /**
     * The VPC network to bind to.
     *
     * @resource gyro.google.compute.NetworkResource
     */
    @Required
    public NetworkResource getNetwork() {
        return network;
    }

    public void setNetwork(NetworkResource network) {
        this.network = network;
    }

    @Override
    public void copyFrom(ManagedZonePrivateVisibilityConfigNetwork model) {
        String networkUrl = model.getNetworkUrl();

        if (networkUrl != null) {
            setNetwork(findById(NetworkResource.class, networkUrl.substring(networkUrl.lastIndexOf("/") + 1)));
        }
    }

    @Override
    public String primaryKey() {
        return Optional.ofNullable(getNetwork())
            .map(NetworkResource::primaryKey)
            .orElse(super.primaryKey());
    }

    public ManagedZonePrivateVisibilityConfigNetwork copyTo() {
        ManagedZonePrivateVisibilityConfigNetwork managedZonePrivateVisibilityConfigNetwork = new ManagedZonePrivateVisibilityConfigNetwork();
        NetworkResource network = getNetwork();

        if (network != null) {
            managedZonePrivateVisibilityConfigNetwork.setNetworkUrl(network.getSelfLink());
        }
        return managedZonePrivateVisibilityConfigNetwork;
    }

    public boolean isEqualTo(ManagedZonePrivateVisibilityConfigNetwork network) {
        return Optional.ofNullable(network)
            .map(ManagedZonePrivateVisibilityConfigNetwork::getNetworkUrl)
            .filter(networkUrl -> Optional.ofNullable(getNetwork())
                .filter(e -> networkUrl.equals(e.getSelfLink()))
                .isPresent())
            .isPresent();
    }
}
