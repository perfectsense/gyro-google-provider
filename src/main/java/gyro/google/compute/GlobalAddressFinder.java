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

import com.google.cloud.compute.v1.Address;
import com.google.cloud.compute.v1.AddressList;
import com.google.cloud.compute.v1.GlobalAddressesClient;
import com.google.cloud.compute.v1.ListGlobalAddressesRequest;
import com.psddev.dari.util.StringUtils;
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
public class GlobalAddressFinder extends GoogleFinder<GlobalAddressesClient, Address, GlobalAddressResource> {

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
    protected List<Address> findAllGoogle(GlobalAddressesClient client) throws Exception {
        return getAddresses(client, null);
    }

    @Override
    protected List<Address> findGoogle(GlobalAddressesClient client, Map<String, String> filters) throws Exception {
        return getAddresses(client, filters.get("filter"));
    }

    private List<Address> getAddresses(GlobalAddressesClient client, String filter) {
        List<Address> addresses = new ArrayList<>();
        String pageToken = null;

        try {
            do {
                ListGlobalAddressesRequest.Builder builder = ListGlobalAddressesRequest.newBuilder()
                    .setProject(getProjectId());

                if (pageToken != null) {
                    builder.setPageToken(pageToken);
                }

                if (filter != null) {
                    builder.setFilter(filter);
                }

                AddressList addressList = client.list(builder.build()).getPage().getResponse();
                pageToken = addressList.getNextPageToken();

                addresses.addAll(addressList.getItemsList());
            } while (!StringUtils.isEmpty(pageToken));

        } finally {
            client.close();
        }

        return addresses;
    }
}
