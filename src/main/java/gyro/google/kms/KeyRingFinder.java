package gyro.google.kms;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.cloud.kms.v1.KeyManagementServiceClient;
import com.google.cloud.kms.v1.KeyRing;
import com.google.cloud.kms.v1.LocationName;
import gyro.core.Type;
import gyro.google.GoogleFinder;

/**
 * Query for key rings.
 *
 * Example
 * -------
 *
 * .. code-block:: gyro
 *
 *    key-ring: $(external-query google::key-ring {location: "us-east4"})
 */
@Type("key-ring")
public class KeyRingFinder extends GoogleFinder<KeyManagementServiceClient, KeyRing, KeyRingResource> {
    private String location;

    /**
     * The location of the key ring.
     */
    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    @Override
    protected List<KeyRing> findAllGoogle(KeyManagementServiceClient client) throws Exception {
        throw new UnsupportedOperationException("Finding all `key-rings` without any filter is not supported!!");
    }

    @Override
    protected List<KeyRing> findGoogle(
        KeyManagementServiceClient client, Map<String, String> filters) throws Exception {
        List<KeyRing> keyRings = new ArrayList<>();

        if (filters.containsKey("location")) {
            KeyManagementServiceClient.ListKeyRingsPagedResponse response = client.listKeyRings(
                LocationName.format(getProjectId(), filters.get("location")));
            response.iterateAll().forEach(keyRings::add);
        }

        return keyRings;
    }
}
