package gyro.google.compute;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.compute.Compute;
import com.google.api.services.compute.model.NetworkEndpointGroup;
import com.google.api.services.compute.model.NetworkEndpointGroupAggregatedList;
import com.google.api.services.compute.model.NetworkEndpointGroupsScopedList;
import gyro.core.GyroException;
import gyro.core.Type;
import gyro.google.GoogleFinder;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Query network-endpoint-group.
 *
 * Example
 * -------
 *
 * .. code-block:: gyro
 *
 *    network-endpoint-group: $(external-query google::network-endpoint-group { name: 'network-endpoint-group-example', region: 'us-east1-b'})
 */
@Type("network-endpoint-group")
public class NetworkEndpointGroupFinder extends GoogleFinder<Compute, NetworkEndpointGroup, NetworkEndpointGroupResource> {
    private String name;
    private String zone;

    /**
     * The name of the network endpoint group.
     */
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * The zone of the network endpoint group.
     */
    public String getZone() {
        return zone;
    }

    public void setZone(String zone) {
        this.zone = zone;
    }

    @Override
    protected List<NetworkEndpointGroup> findAllGoogle(Compute client) {
        try {
            NetworkEndpointGroupAggregatedList list = client.networkEndpointGroups().aggregatedList(getProjectId()).execute();
            if (list.size() > 0 ) {
                return client.networkEndpointGroups()
                    .aggregatedList(getProjectId()).execute()
                    .getItems().values().stream()
                    .map(NetworkEndpointGroupsScopedList::getNetworkEndpointGroups)
                    .filter(Objects::nonNull)
                    .flatMap(Collection::stream)
                    .collect(Collectors.toList());
            } else {
                return Collections.emptyList();
            }
        } catch (GoogleJsonResponseException je) {
            throw new GyroException(je.getDetails().getMessage());
        } catch (IOException ex) {
            throw new GyroException(ex);
        }
    }

    @Override
    protected List<NetworkEndpointGroup> findGoogle(Compute client, Map<String, String> filters) {
        NetworkEndpointGroup networkEndpointGroup = null;

        try {
            networkEndpointGroup = client.networkEndpointGroups().get(getProjectId(), filters.get("zone"), filters.get("name")).execute();
        } catch (GoogleJsonResponseException je) {
            if (!je.getDetails().getMessage().matches("The resource (.*) was not found")) {
                throw new GyroException(je.getDetails().getMessage());
            }
        } catch (IOException ex) {
            throw new GyroException(ex);
        }

        if (networkEndpointGroup != null) {
            return Collections.singletonList(networkEndpointGroup);
        } else {
            return Collections.emptyList();
        }
    }
}
