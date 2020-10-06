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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.google.api.services.compute.model.HealthStatusForNetworkEndpoint;
import com.google.api.services.compute.model.NetworkEndpoint;
import com.google.api.services.compute.model.NetworkEndpointWithHealthStatus;
import gyro.core.resource.Diffable;
import gyro.core.resource.Output;
import gyro.core.validation.Required;
import gyro.google.Copyable;

public class NetworkEndpointResource extends Diffable implements Copyable<NetworkEndpointWithHealthStatus> {

    private InstanceResource instance;
    private Integer port;
    private String ipAddress;

    // Read-only
    private List<String> healthStatus;

    /**
     * The instance resource to add. Required for network endpoints of type ``GCE_VM_IP_PORT``.
     */
    @Required
    public InstanceResource getInstance() {
        return instance;
    }

    public void setInstance(InstanceResource instance) {
        this.instance = instance;
    }

    /**
     * Port number of network endpoint. If unspecified and the ``NetworkEndpointGroup.network_endpoint_type`` is ``GCE_IP_PORT``, then the ``defaultPort`` for the network endpoint group will be used.
     */
    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    /**
     * IPv4 address of network endpoint. The IP address must belong to a VM in Compute Engine as either the primary IP or as part of an aliased IP range. If not specified the primary IP address for the instance resource in the network that the network endpoint group belongs to will be used.
     */
    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    /**
     * The health status of the network endpoint.
     */
    @Output
    public List<String> getHealthStatus() {
        if (healthStatus == null) {
            healthStatus = new ArrayList<>();
        }

        return healthStatus;
    }

    public void setHealthStatus(List<String> healthStatus) {
        this.healthStatus = healthStatus;
    }

    @Override
    public String primaryKey() {
        StringBuilder key = new StringBuilder();

        if (getInstance() != null) {
            key.append(getInstance().getName());
        }

        if (getPort() != null) {
            key.append(String.format(" port %d", getPort()));
        }

        return key.toString();
    }

    @Override
    public void copyFrom(NetworkEndpointWithHealthStatus endpoint) {
        NetworkEndpoint networkEndpoint = endpoint.getNetworkEndpoint();
        setInstance(findById(InstanceResource.class, networkEndpoint.getInstance()));
        setIpAddress(networkEndpoint.getIpAddress());
        setPort(networkEndpoint.getPort());

        getHealthStatus().clear();
        if (endpoint.getHealths() != null && !endpoint.getHealths().isEmpty()) {
            setHealthStatus(endpoint.getHealths()
                .stream()
                .map(HealthStatusForNetworkEndpoint::getHealthState)
                .collect(Collectors.toList()));
        }
    }

    NetworkEndpoint toNetworkEndpoint() {
        NetworkEndpoint networkEndpoint = new NetworkEndpoint();
        networkEndpoint.setInstance(getInstance().getName());
        networkEndpoint.setPort(getPort());
        networkEndpoint.setIpAddress(getIpAddress());

        return networkEndpoint;
    }
}
