package gyro.google.compute;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.compute.Compute;
import com.google.api.services.compute.model.Firewall;
import gyro.core.GyroException;
import gyro.core.Type;
import gyro.google.GoogleFinder;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Query firewall rue.
 *
 * Example
 * -------
 *
 * .. code-block:: gyro
 *
 *    firewall-rule: $(external-query google::firewall-rule { name: 'firewall-rule-example'})
 */
@Type("firewall-rule")
public class FirewallRuleFinder extends GoogleFinder<Compute, Firewall, FirewallRuleResource> {
    private String name;

    /**
     * The name of the firewall rule.
     */
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    protected List<Firewall> findAllGoogle(Compute client) {
        try {
            return client.firewalls().list(getProjectId()).execute().getItems();
        } catch (GoogleJsonResponseException je) {
            throw new GyroException(je.getDetails().getMessage());
        } catch (IOException ex) {
            throw new GyroException(ex);
        }
    }

    @Override
    protected List<Firewall> findGoogle(Compute client, Map<String, String> filters) {
        Firewall firewall = null;

        try {
            firewall = client.firewalls().get(getProjectId(), filters.get("name")).execute();
        } catch (GoogleJsonResponseException je) {
            if (!je.getDetails().getMessage().matches("The resource (.*) was not found")) {
                throw new GyroException(je.getDetails().getMessage());
            }
        } catch (IOException ex) {
            throw new GyroException(ex);
        }

        if (firewall != null) {
            return Collections.singletonList(firewall);
        } else {
            return Collections.emptyList();
        }
    }
}
