package gyro.google.kms;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.cloud.kms.v1.CryptoKeyName;
import com.google.cloud.kms.v1.CryptoKeyVersion;
import com.google.cloud.kms.v1.KeyManagementServiceClient;
import gyro.core.GyroException;
import gyro.core.Type;
import gyro.google.GoogleFinder;

/**
 * Query for crypto key versions.
 *
 * Example
 * -------
 *
 * .. code-block:: gyro
 *
 *    crypto-key-version: $(external-query google::crypto-key-version {location: "us-east4", key-ring-name: "key-ring-example", key-name: "crypto-key-example"})
 */
@Type("crypto-key-version")
public class CryptoKeyVersionFinder
    extends GoogleFinder<KeyManagementServiceClient, CryptoKeyVersion, CryptoKeyVersionResource> {
    private String location;
    private String keyRingName;
    private String keyName;

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
    public String getKeyRingName() {
        return keyRingName;
    }

    public void setKeyRingName(String keyRingName) {
        this.keyRingName = keyRingName;
    }

    /**
     * The name of the key.
     */
    public String getKeyName() {
        return keyName;
    }

    public void setKeyName(String keyName) {
        this.keyName = keyName;
    }

    @Override
    protected List<CryptoKeyVersion> findAllGoogle(KeyManagementServiceClient client) throws Exception {
        throw new GyroException("'location', 'key-ring-name' and 'key-name' are required filters");
    }

    @Override
    protected List<CryptoKeyVersion> findGoogle(
        KeyManagementServiceClient client, Map<String, String> filters) throws Exception {
        if (filters.containsKey("location") && filters.containsKey("key-ring-name") && filters.containsKey("key-name")) {
            List<CryptoKeyVersion> keys = new ArrayList<>();

            KeyManagementServiceClient.ListCryptoKeyVersionsPagedResponse response = client.listCryptoKeyVersions(
                CryptoKeyName.format(
                    getProjectId(),
                    filters.get("location"),
                    filters.get("key-ring-name"),
                    filters.get("key-name")));
            response.iterateAll().forEach(keys::add);

            return keys;
        } else {
            throw new GyroException("'location', 'key-ring-name' and 'key-name' are required filters");
        }
    }
}
