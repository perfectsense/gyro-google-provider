/*
 * Copyright 2020, Perfect Sense, Inc.
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

package gyro.google.kms;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.cloud.kms.v1.CryptoKeyName;
import com.google.cloud.kms.v1.CryptoKeyVersion;
import com.google.cloud.kms.v1.KeyManagementServiceClient;
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
        throw new UnsupportedOperationException(
            "Finding all `crypto-key-versions` without any filter is not supported!!");
    }

    @Override
    protected List<CryptoKeyVersion> findGoogle(
        KeyManagementServiceClient client, Map<String, String> filters) throws Exception {
        List<CryptoKeyVersion> keys = new ArrayList<>();

        if (filters.containsKey("location") && filters.containsKey("key-ring-name")
            && filters.containsKey("key-name")) {

            KeyManagementServiceClient.ListCryptoKeyVersionsPagedResponse response = client.listCryptoKeyVersions(
                CryptoKeyName.format(
                    getProjectId(),
                    filters.get("location"),
                    filters.get("key-ring-name"),
                    filters.get("key-name")));
            response.iterateAll().forEach(keys::add);

        }
        return keys;
    }
}
