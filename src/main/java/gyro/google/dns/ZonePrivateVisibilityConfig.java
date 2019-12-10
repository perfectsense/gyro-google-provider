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

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.google.api.services.dns.model.ManagedZonePrivateVisibilityConfig;
import com.google.api.services.dns.model.ManagedZonePrivateVisibilityConfigNetwork;
import gyro.core.resource.Diffable;
import gyro.core.validation.Required;
import gyro.google.Copyable;

public class ZonePrivateVisibilityConfig extends Diffable implements Copyable<ManagedZonePrivateVisibilityConfig> {

    private List<ZonePrivateVisibilityConfigNetwork> network;

    /**
     * The list of VPC networks that can see this zone.
     *
     * @subresource gyro.google.dns.ZonePrivateVisibilityConfigNetwork
     */
    @Required
    public List<ZonePrivateVisibilityConfigNetwork> getNetwork() {
        return network;
    }

    public void setNetwork(List<ZonePrivateVisibilityConfigNetwork> network) {
        this.network = network;
    }

    @Override
    public void copyFrom(ManagedZonePrivateVisibilityConfig model) {
        List<ManagedZonePrivateVisibilityConfigNetwork> networks = model.getNetworks();

        if (networks != null && !networks.isEmpty()) {
            setNetwork(networks
                .stream()
                .map(network -> {
                    ZonePrivateVisibilityConfigNetwork diffableConfigNetwork = Optional.ofNullable(
                        getNetwork())
                        .orElse(Collections.emptyList())
                        .stream()
                        .filter(e -> e.isEqualTo(network))
                        .findFirst()
                        .orElse(newSubresource(ZonePrivateVisibilityConfigNetwork.class));
                    diffableConfigNetwork.copyFrom(network);
                    return diffableConfigNetwork;
                })
                .collect(Collectors.toList()));
        }
    }

    public ManagedZonePrivateVisibilityConfig copyTo() {
        ManagedZonePrivateVisibilityConfig managedZonePrivateVisibilityConfig = new ManagedZonePrivateVisibilityConfig();
        List<ZonePrivateVisibilityConfigNetwork> networks = getNetwork();

        if (networks != null && !networks.isEmpty()) {
            managedZonePrivateVisibilityConfig.setNetworks(networks
                .stream()
                .map(ZonePrivateVisibilityConfigNetwork::copyTo)
                .collect(Collectors.toList()));
        }
        return managedZonePrivateVisibilityConfig;
    }
}
