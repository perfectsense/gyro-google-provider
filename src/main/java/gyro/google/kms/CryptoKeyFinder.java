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
        throw new UnsupportedOperationException("Finding all `crypto-keys` without any filter is not supported!!");
    }

    @Override
    protected List<CryptoKey> findGoogle(
        KeyManagementServiceClient client, Map<String, String> filters) throws Exception {
        List<CryptoKey> keys = new ArrayList<>();

        if (filters.containsKey("location") && filters.containsKey("key-ring-name")) {
            KeyManagementServiceClient.ListCryptoKeysPagedResponse response = client.listCryptoKeys(KeyRingName.format(
                getProjectId(),
                filters.get("location"),
                filters.get("key-ring-name")));
            response.iterateAll().forEach(keys::add);

        }

        return keys;
    }
}
