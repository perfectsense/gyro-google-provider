package gyro.google.kms;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.cloud.kms.v1.CryptoKey;
import com.google.cloud.kms.v1.KeyManagementServiceClient;
import com.google.cloud.kms.v1.KeyRingName;
import gyro.core.Type;
import gyro.google.GoogleFinder;

/**
 * Query for crypto keys.
 *
 * Example
 * -------
 *
 * .. code-block:: gyro
 *
 *    crypto-key: $(external-query google::crypto-key {location: "us-east4", key-ring-name: "key-ring-example"})
 */
@Type("crypto-key")
public class CryptoKeyFinder extends GoogleFinder<KeyManagementServiceClient, CryptoKey, CryptoKeyResource> {
    private String location;
    private String name;

    /**
     * The location of the key ring.
     */
    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    /**
     * The name of the key ring.
     */
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    protected List<CryptoKey> findAllGoogle(KeyManagementServiceClient client) throws Exception {
        throw new GyroException("'location' and 'key-ring-name' are required filters");
    }

    @Override
    protected List<CryptoKey> findGoogle(
        KeyManagementServiceClient client, Map<String, String> filters) throws Exception {
        if (filters.containsKey("location") && filters.containsKey("key-ring-name")) {
            List<CryptoKey> keys = new ArrayList<>();

            KeyManagementServiceClient.ListCryptoKeysPagedResponse response = client.listCryptoKeys(KeyRingName.format(
                getProjectId(),
                filters.get("location"),
                filters.get("key-ring-name")));
            response.iterateAll().forEach(keys::add);

            return keys;
        } else {
            throw new GyroException("'location' and 'key-ring-name' are required filters");
        }
    }
}
