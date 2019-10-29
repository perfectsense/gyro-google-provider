package gyro.google.compute;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.compute.Compute;
import com.google.api.services.compute.model.Network;
import gyro.core.GyroException;
import gyro.core.Type;
import gyro.google.GoogleFinder;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Query network.
 *
 * Example
 * -------
 *
 * .. code-block:: gyro
 *
 *    network: $(external-query google::network { name: 'network-example'})
 */
@Type("network")
public class NetworkFinder extends GoogleFinder<Compute, Network, NetworkResource> {
    private String name;

    /**
     * The name of the network.
     */
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    protected List<Network> findAllGoogle(Compute client) {
        try {
            return client.networks().list(getProjectId()).execute().getItems();
        } catch (GoogleJsonResponseException je) {
            throw new GyroException(je.getDetails().getMessage());
        } catch (IOException ex) {
            throw new GyroException(ex);
        }
    }

    @Override
    protected List<Network> findGoogle(Compute client, Map<String, String> filters) {
        Network network = null;

        try {
            network = client.networks().get(getProjectId(), filters.get("name")).execute();
        } catch (GoogleJsonResponseException je) {
            if (!je.getDetails().getMessage().matches("The resource (.*) was not found")) {
                throw new GyroException(je.getDetails().getMessage());
            }
        } catch (IOException ex) {
            throw new GyroException(ex);
        }

        if (network != null) {
            return Collections.singletonList(network);
        } else {
            return Collections.emptyList();
        }
    }
}
