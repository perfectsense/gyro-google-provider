/*
 * Copyright 2019, Perfect Sense, Inc.
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

package gyro.google.compute;

import com.google.api.services.compute.Compute;
import com.google.api.services.compute.model.Address;
import com.google.api.services.compute.model.AddressAggregatedList;
import com.google.api.services.compute.model.AddressList;
import com.google.api.services.compute.model.AddressesScopedList;
import gyro.core.GyroException;
import gyro.core.Type;
import gyro.google.GoogleFinder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Query for addresses.
 *
 * ========
 * Examples
 * ========
 *
 * Example find all static IPs for project.
 * -------
 *
 * .. code-block:: gyro
 *
 *    addresses: $(external-query google::address)
 *
 *
 * Example find all static IPs for the region 'us-east1'.
 * -------
 *
 * .. code-block:: gyro
 *
 *    addresses: $(external-query google::address { region: 'us-east1' })
 *
 *
 * Example find the static IP filtering on name equal to 'us-east1-test-two' in the region 'us-east1'.
 * -------
 *
 * .. code-block:: gyro
 *
 *    addresses: $(external-query google::address { filter: 'name = "us-east1-test-two"', region: 'us-east1' })
 *
 */
@Type("address")
public class AddressFinder extends GoogleFinder<Compute, Address, AddressResource> {

    private String filter;
    private String region;

    /**
     * A filter expression that filters resources listed in the response. The expression must specify the field name, a comparison operator, and the value that you want to use for filtering.
     */
    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    /**
     * Name of the region for this request. Not applicable to global addresses.
     */
    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    @Override
    protected List<Address> findAllGoogle(Compute client) {
        try {
            List<Address> addresses = new ArrayList<>();
            String pageToken = null;

            do {
                AddressAggregatedList aggregatedList = client.addresses().aggregatedList(getProjectId())
                        .setPageToken(pageToken)
                        .execute();

                if (aggregatedList != null && aggregatedList.getItems() != null) {
                    pageToken = aggregatedList.getNextPageToken();

                    List<Address> aggregated = aggregatedList.getItems().values().stream()
                            .map(AddressesScopedList::getAddresses)
                            .filter(Objects::nonNull)
                            .flatMap(List::stream)
                            .collect(Collectors.toList());

                    if (!aggregated.isEmpty()) {
                        addresses.addAll(aggregated);
                    }
                } else {
                    break;
                }
            } while (pageToken != null);

            return addresses;

        } catch (IOException e) {
            throw new GyroException(e.getMessage());
        }
    }

    @Override
    protected List<Address> findGoogle(Compute client, Map<String, String> filters) {
        try {
            List<Address> addresses = new ArrayList<>();
            String pageToken = null;

            if (filters.containsKey("region")) {
                do {
                    AddressList addressList = client.addresses().list(getProjectId(), filters.get("region"))
                            .setFilter(filters.containsKey("filter") ? filters.get("filter") : null)
                            .setPageToken(pageToken)
                            .execute();

                    if (addressList != null && addressList.getItems() != null) {
                        pageToken = addressList.getNextPageToken();

                        addresses.addAll(addressList.getItems());
                    } else {
                        break;
                    }
                } while (pageToken != null);
            } else {
                return findAllGoogle(client);
            }

            return addresses;

        } catch (IOException e) {
            throw new GyroException(e.getMessage());
        }
    }
}
