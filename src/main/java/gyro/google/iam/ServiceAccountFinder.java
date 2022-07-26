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
import java.util.stream.Collectors;

import com.google.api.services.iam.v1.Iam;
import com.google.api.services.iam.v1.model.ListServiceAccountsResponse;
import com.google.api.services.iam.v1.model.ServiceAccount;
import gyro.core.Type;
import gyro.google.GoogleFinder;
import gyro.google.util.Utils;
import org.apache.commons.lang3.StringUtils;

/**
 * Query service account.
 *
 * Example
 * -------
 *
 * .. code-block:: gyro
 *
 *    service-account: $(external-query google::service-account {})
 */
@Type("service-account")
public class ServiceAccountFinder extends GoogleFinder<Iam, ServiceAccount, ServiceAccountResource> {

    private String name;
    private String displayName;

    /**
     * The name of the service account.
     */
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * The display name of the service account.
     */
    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    @Override
    protected List<ServiceAccount> findAllGoogle(Iam client) throws Exception {
        String token = null;
        List<ServiceAccount> serviceAccounts = new ArrayList<>();

        do {
            Iam.Projects.ServiceAccounts.List list = client.projects()
                .serviceAccounts()
                .list(String.format("projects/%s", getProjectId()))
                .setPageSize(20);

            if (!StringUtils.isBlank(token)) {
                list = list.setPageToken(token);
            }

            ListServiceAccountsResponse response = list.execute();
            token = response.getNextPageToken();

            serviceAccounts.addAll(response.getAccounts());
        } while (!StringUtils.isBlank(token));

        return serviceAccounts;
    }

    @Override
    protected List<ServiceAccount> findGoogle(Iam client, Map<String, String> filters) throws Exception {
        String token = null;
        List<ServiceAccount> serviceAccounts = new ArrayList<>();

        do {
            Iam.Projects.ServiceAccounts.List list = client.projects()
                .serviceAccounts()
                .list(String.format("projects/%s", getProjectId()))
                .setPageSize(20);

            if (!StringUtils.isBlank(token)) {
                list = list.setPageToken(token);
            }

            ListServiceAccountsResponse response = list.execute();
            token = response.getNextPageToken();

            if (filters.containsKey("name")) {
                serviceAccounts.addAll(response.getAccounts().stream()
                    .filter(a -> Utils.getServiceAccountNameFromId(a.getName()).equals(filters.get("name")))
                    .collect(
                        Collectors.toList()));
            }

            if (filters.containsKey("display-name")) {
                serviceAccounts.addAll(response.getAccounts().stream()
                    .filter(a -> a.getDisplayName().equals(filters.get("display-name"))).collect(Collectors.toList()));
            }

        } while (!StringUtils.isBlank(token));

        return serviceAccounts;
    }
}
