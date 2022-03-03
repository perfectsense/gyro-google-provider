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
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import com.google.cloud.compute.v1.Address;
import com.google.cloud.compute.v1.AddressAggregatedList;
import com.google.cloud.compute.v1.AddressList;
import com.google.cloud.compute.v1.AddressesClient;
import com.google.cloud.compute.v1.AddressesScopedList;
import com.google.cloud.compute.v1.AggregatedListAddressesRequest;
import com.google.cloud.compute.v1.ListAddressesRequest;
import gyro.core.Type;
import gyro.google.GoogleFinder;
import org.apache.commons.lang.StringUtils;

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
public class AddressFinder extends GoogleFinder<AddressesClient, Address, AddressResource> {

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
    protected List<Address> findAllGoogle(AddressesClient client) throws Exception {
        try {
            return getAddresses(client, null);
        } finally {
            client.close();
        }
    }

    @Override
    protected List<Address> findGoogle(AddressesClient client, Map<String, String> filters) throws Exception {
        List<Address> addresses = new ArrayList<>();
        String pageToken = null;
        try {
            if (filters.containsKey("region")) {

                do {
                    ListAddressesRequest.Builder builder = ListAddressesRequest.newBuilder().setProject(getProjectId())
                        .setRegion(filters.get("region")).setFilter(filters.getOrDefault("filter", ""));

                    if (pageToken != null) {
                        builder.setPageToken(pageToken);
                    }

                    AddressList addressList = client.list(builder.build()).getPage().getResponse();
                    pageToken = addressList.getNextPageToken();

                    addresses.addAll(addressList.getItemsList());
                } while (!StringUtils.isEmpty(pageToken));
            } else {
                addresses.addAll(getAddresses(client, filters.get("filter")));
            }

        } finally {
            client.close();
        }
        return addresses;
    }

    private List<Address> getAddresses(AddressesClient client, String filter) {
        List<Address> addresses = new ArrayList<>();
        String pageToken = null;

        do {
            AggregatedListAddressesRequest.Builder builder = AggregatedListAddressesRequest.newBuilder();

            if (pageToken != null) {
                builder.setPageToken(pageToken);
            }

            if (filter != null) {
                builder.setFilter(filter);
            }

            AddressAggregatedList aggregatedList = client.aggregatedList(builder.setProject(getProjectId()).build())
                .getPage().getResponse();
            pageToken = aggregatedList.getNextPageToken();

            if (aggregatedList.getItemsMap() != null) {
                addresses.addAll(aggregatedList.getItemsMap().values().stream()
                    .map(AddressesScopedList::getAddressesList)
                    .filter(Objects::nonNull)
                    .flatMap(Collection::stream)
                    .filter(a -> StringUtils.isNotBlank(a.getRegion()))
                    .collect(Collectors.toList()));
            }

        } while (!StringUtils.isEmpty(pageToken));

        return addresses;
    }
}
