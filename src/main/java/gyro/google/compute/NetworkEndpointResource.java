package gyro.google.compute;

import com.google.api.services.compute.model.HealthStatusForNetworkEndpoint;
import com.google.api.services.compute.model.NetworkEndpoint;
import com.google.api.services.compute.model.NetworkEndpointWithHealthStatus;
import gyro.core.resource.Diffable;
import gyro.core.resource.Output;
import gyro.core.validation.Required;
import gyro.google.Copyable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class NetworkEndpointResource extends Diffable implements Copyable<NetworkEndpointWithHealthStatus> {
    private InstanceResource instance;
    private Integer port;
    private String ipAddress;

    // Read-only
    private List<String> healthStatus;

    @Required
    public InstanceResource getInstance() {
        return instance;
    }

    public void setInstance(InstanceResource instance) {
        this.instance = instance;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

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
        return getInstance().getName();
    }

    @Override
    public void copyFrom(NetworkEndpointWithHealthStatus endpoint) {
        NetworkEndpoint networkEndpoint = endpoint.getNetworkEndpoint();
        setInstance(findById(InstanceResource.class, networkEndpoint.getInstance()));
        setIpAddress(networkEndpoint.getIpAddress());
        setPort(networkEndpoint.getPort());

        getHealthStatus().clear();
        if (endpoint.getHealths() != null && !endpoint.getHealths().isEmpty()) {
            setHealthStatus(endpoint.getHealths().stream().map(HealthStatusForNetworkEndpoint::getHealthState).collect(Collectors.toList()));
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
