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

package gyro.google.iam;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.api.services.iam.v1.Iam;
import com.google.api.services.iam.v1.model.ServiceAccountKey;
import gyro.core.Type;
import gyro.google.GoogleFinder;

/**
 * Query service account keys.
 *
 * Example
 * -------
 *
 * .. code-block:: gyro
 *
 *    service-account-key: $(external-query google::service-account-key {service-account-id: "projects/[PROJECT_NAME]/serviceAccounts/[SERVICE_ACCOUNT_EMAIL]"})
 */
@Type("service-account-key")
public class ServiceAccountKeyFinder extends GoogleFinder<Iam, ServiceAccountKey, ServiceAccountKeyResource> {
    private String serviceAccountId;

    /**
     * The ID of the service account.
     */
    public String getServiceAccountId() {
        return serviceAccountId;
    }

    public void setServiceAccountId(String serviceAccountId) {
        this.serviceAccountId = serviceAccountId;
    }

    @Override
    protected List<ServiceAccountKey> findAllGoogle(Iam client) throws Exception {
        throw new UnsupportedOperationException("Finding `service-account-keys` without filters is not supported!!");
    }

    @Override
    protected List<ServiceAccountKey> findGoogle(Iam client, Map<String, String> filters) throws Exception {
        List<ServiceAccountKey> keys = new ArrayList<>();

        if (filters.containsKey("service-account-id")) {
            keys = client.projects().serviceAccounts().keys().list(filters.get("service-account-id")).execute().getKeys();
        }

        return keys;
    }
}
