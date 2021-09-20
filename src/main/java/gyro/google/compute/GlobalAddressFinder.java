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

import com.google.api.services.compute.Compute;
import com.google.cloud.compute.v1.Address;
import com.google.cloud.compute.v1.AddressList;
import gyro.core.Type;
import gyro.google.GoogleFinder;

/**
 * Query for global addresses.
 *
 * Example find the global address filtering on name equal to 'global-address-test-1'.
 * -------
 *
 * .. code-block:: gyro
 *
 *    addresses: $(external-query google::global-address { filter: 'name = "global-address-test-1"' })
 *
 */
@Type("global-address")
public class GlobalAddressFinder extends GoogleFinder<Compute, Address, GlobalAddressResource> {

    private String filter;

    /**
     * A filter expression that filters resources listed in the response. The expression must specify the field name, a comparison operator, and the value that you want to use for filtering.
     */
    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    @Override
    protected List<Address> findAllGoogle(Compute client) throws Exception {
        List<Address> addresses = new ArrayList<>();
        String pageToken = null;

        do {
            AddressList addressList = client.globalAddresses().list(getProjectId())
                .setPageToken(pageToken)
                .execute();
            pageToken = addressList.getNextPageToken();

            if (addressList.getItems() != null) {
                addresses.addAll(addressList.getItems());
            }
        } while (pageToken != null);

        return addresses;
    }

    @Override
    protected List<Address> findGoogle(Compute client, Map<String, String> filters) throws Exception {
        List<Address> addresses = new ArrayList<>();

        if (filters.containsKey("filter")) {
            String pageToken = null;

            do {
                AddressList addressList = client.globalAddresses().list(getProjectId())
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
