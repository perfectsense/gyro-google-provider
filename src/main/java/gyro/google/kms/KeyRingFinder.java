package gyro.google.kms;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.google.cloud.kms.v1.KeyManagementServiceClient;
import com.google.cloud.kms.v1.KeyRing;
import com.google.cloud.kms.v1.LocationName;
import gyro.core.GyroException;
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
        List<KeyRing> keyRings = new ArrayList<>();
        List<String> locations = Arrays.asList(
            "asia-east1",
            "asia-east2",
            "asia-northeast1",
            "asia-northeast2",
            "asia-northeast3",
            "asia-south1",
            "asia-southeast1",
            "australia-southeast1",
            "europe-north1",
            "europe-west1",
            "europe-west2",
            "europe-west3",
            "europe-west4",
            "europe-west6",
            "northamerica-northeast1",
            "us-central1",
            "us-east1",
            "us-east4",
            "us-west1",
            "us-west2",
            "us-west3",
            "southamerica-east1",
            "eur4",
            "nam4",
            "global",
            "asia",
            "europe",
            "us");
        for (String location : locations) {
            KeyManagementServiceClient.ListKeyRingsPagedResponse response = client.listKeyRings(
                LocationName.format(getProjectId(), location));
            response.iterateAll().forEach(k -> keyRings.add(k));
        }
        return keyRings;
    }

    @Override
    protected List<KeyRing> findGoogle(
        KeyManagementServiceClient client, Map<String, String> filters) throws Exception {
        if (filters.containsKey("location")) {
            List<KeyRing> keyRings = new ArrayList<>();

            KeyManagementServiceClient.ListKeyRingsPagedResponse response = client.listKeyRings(
                LocationName.format(getProjectId(), filters.get("location")));
            response.iterateAll().forEach(k -> keyRings.add(k));

            return keyRings;
        } else {
            throw new GyroException("'location' is a required filter");
        }
    }
}
