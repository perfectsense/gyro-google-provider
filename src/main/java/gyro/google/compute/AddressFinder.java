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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import com.google.api.services.compute.Compute;
import com.google.cloud.compute.v1.Address;
import com.google.cloud.compute.v1.AddressAggregatedList;
import com.google.cloud.compute.v1.AddressList;
import com.google.cloud.compute.v1.AddressesScopedList;
import gyro.core.Type;
import gyro.google.GoogleFinder;

/**
 * Query for regional addresses.
 *
 * Example find the address filtering on name equal to 'us-east1-test-two' in the region 'us-east1'.
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
    protected List<Address> findAllGoogle(Compute client) throws Exception {
        List<Address> addresses = new ArrayList<>();
        String pageToken = null;

        do {
            AddressAggregatedList aggregatedList = client.addresses().aggregatedList(getProjectId())
                .setPageToken(pageToken)
                .execute();
            pageToken = aggregatedList.getNextPageToken();

            aggregatedList.getItems().remove("global");

            addresses.addAll(aggregatedList.getItems().values().stream()
                .map(AddressesScopedList::getAddresses)
                .filter(Objects::nonNull)
                .flatMap(List::stream)
                .collect(Collectors.toList()));

        } while (pageToken != null);

        return addresses;
    }

    @Override
    protected List<Address> findGoogle(Compute client, Map<String, String> filters) throws Exception {
        List<Address> addresses = new ArrayList<>();

        if (filters.containsKey("region")) {
            String pageToken = null;

            do {
                AddressList addressList = client.addresses().list(getProjectId(), filters.get("region"))
                    .setFilter(filters.get("filter"))
                    .setPageToken(pageToken)
                    .execute();
                pageToken = addressList.getNextPageToken();

                if (addressList.getItems() != null) {
                    addresses.addAll(addressList.getItems());
                }

            } while (pageToken != null);
        }

        return addresses;
    }
}
