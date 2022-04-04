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

import com.google.api.gax.rpc.NotFoundException;
import com.google.cloud.compute.v1.Address;
import com.google.cloud.compute.v1.DeleteGlobalAddressRequest;
import com.google.cloud.compute.v1.GetGlobalAddressRequest;
import com.google.cloud.compute.v1.GlobalAddressesClient;
import com.google.cloud.compute.v1.InsertGlobalAddressRequest;
import gyro.core.GyroUI;
import gyro.core.Type;
import gyro.core.scope.State;
import gyro.core.validation.ValidStrings;

/**
 * Global external IP addresses are IPv4 or IPv6 addresses. They can only be assigned to global forwarding rules for HTTP(S), SSL Proxy, or TCP Proxy load balancers in Premium Tier.
 *
 * Example
 * -------
 *
 * .. code-block:: gyro
 *
 *     google::global-address global_address_1
 *         name: 'test-global-one'
 *         description: 'test global static IP address'
 *         ip-version: 'IPV6'
 *     end
 */
@Type("global-address")
public class GlobalAddressResource extends AbstractAddressResource {

    private String ipVersion;

    /**
     * IP version that will be used by this address. Defaults to ``IPV4``.
     */
    @ValidStrings({ "IPV4", "IPV6" })
    public String getIpVersion() {
        return ipVersion;
    }

    public void setIpVersion(String ipVersion) {
        this.ipVersion = ipVersion;
    }

    @Override
    public boolean doRefresh() throws Exception {
        try (GlobalAddressesClient client = createClient(GlobalAddressesClient.class)) {
            Address address = getAddress(client);

            if (address == null) {
                return false;
            }

            copyFrom(address);

            return true;
        }
    }

    @Override
    public void doCreate(GyroUI ui, State state) throws Exception {
        try (GlobalAddressesClient client = createClient(GlobalAddressesClient.class)) {
            Address.Builder builder = copyTo().toBuilder();

            if (getIpVersion() != null) {
                builder.setIpVersion(getIpVersion());
            }

            waitForCompletion(client.insertCallable().call(InsertGlobalAddressRequest.newBuilder()
                .setProject(getProjectId())
                .setAddressResource(builder)
                .build()));
        }

        refresh();
    }

    @Override
    public void doDelete(GyroUI ui, State state) throws Exception {
        try (GlobalAddressesClient client = createClient(GlobalAddressesClient.class)) {
            waitForCompletion(client.deleteCallable().call(DeleteGlobalAddressRequest.newBuilder()
                .setProject(getProjectId())
                .setAddress(getName())
                .build()));
        }
    }

    @Override
    public void copyFrom(Address model) {
        super.copyFrom(model);

        setIpVersion(model.getIpVersion());
    }

    private Address getAddress(GlobalAddressesClient client) {
        Address address = null;

        try {
            address = client.get(GetGlobalAddressRequest.newBuilder()
                .setProject(getProjectId())
                .setAddress(getName())
                .build());

        } catch (NotFoundException ex) {
            // ignore
        }

        return address;
    }
}
