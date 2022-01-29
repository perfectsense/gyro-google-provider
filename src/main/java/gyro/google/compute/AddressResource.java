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

import java.util.concurrent.TimeUnit;

import com.google.api.gax.rpc.InvalidArgumentException;
import com.google.api.gax.rpc.NotFoundException;
import com.google.cloud.compute.v1.Address;
import com.google.cloud.compute.v1.AddressesClient;
import com.google.cloud.compute.v1.DeleteAddressRequest;
import com.google.cloud.compute.v1.GetAddressRequest;
import com.google.cloud.compute.v1.InsertAddressRequest;
import gyro.core.GyroException;
import gyro.core.GyroUI;
import gyro.core.Type;
import gyro.core.Wait;
import gyro.core.scope.State;
import gyro.core.validation.Required;
import gyro.core.validation.ValidStrings;

/**
 * Adds a regional internal IP address that comes from either a primary or secondary IP range of a subnet in a VPC network. Regional external IP addresses can be assigned to GCP VM instances, Cloud VPN gateways, regional external forwarding rules for network load balancers (in either Standard or Premium Tier), and regional external forwarding rules for HTTP(S), SSL Proxy, and TCP Proxy load balancers in Standard Tier.
 *
 * Example
 * -------
 *
 * .. code-block:: gyro
 *
 *     google::address address_1
 *         name: 'test-one'
 *         region: 'us-west2'
 *         description: 'test static IP address'
 *         network-tier: 'STANDARD'
 *     end
 */
@Type("address")
public class AddressResource extends AbstractAddressResource {

    private String networkTier;
    private String region;

    /**
     * Networking tier used for configuring this address. Defaults to ``PREMIUM``.
     */
    @ValidStrings({ "PREMIUM", "STANDARD" })
    public String getNetworkTier() {
        return networkTier;
    }

    public void setNetworkTier(String networkTier) {
        this.networkTier = networkTier;
    }

    /**
     * The region where this address resides.
     */
    @Required
    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    @Override
    public boolean doRefresh() throws Exception {
        try (AddressesClient client = createClient(AddressesClient.class)) {
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
        try (AddressesClient client = createClient(AddressesClient.class)) {
            Address.Builder builder = copyTo().toBuilder().setRegion(getRegion());

            if (getNetworkTier() != null) {
                builder.setNetworkTier(getNetworkTier());
            }

            // When Network/Subnetwork have just been created they may still be unavailable, so wait until ready.
            boolean success = Wait.atMost(30, TimeUnit.SECONDS)
                .prompt(false)
                .checkEvery(10, TimeUnit.SECONDS)
                .until(() -> createAddress(client, builder.build()));

            if (!success) {
                throw new GyroException(String.format("The resource '%s' is not ready", getSubnetwork().getSelfLink()));
            }
        }
        refresh();
    }

    @Override
    public void doDelete(GyroUI ui, State state) throws Exception {
        try (AddressesClient client = createClient(AddressesClient.class)) {
            waitForCompletion(client.deleteCallable().call(DeleteAddressRequest.newBuilder()
                .setProject(getProjectId())
                .setRegion(getRegion())
                .setAddress(getName())
                .build()));
        }
    }

    @Override
    public void copyFrom(Address model) {
        super.copyFrom(model);

        if (model.hasNetworkTier()) {
            setNetworkTier(model.getNetworkTier());
        }

        // API only accepts region name, but model returns the region selfLink so strip name from the end of URL.
        if (model.getRegion().startsWith("http")) {
            setRegion(model.getRegion().substring(model.getRegion().lastIndexOf("/") + 1));
        }
    }

    private boolean createAddress(AddressesClient client, Address address) throws Exception {
        try {
            waitForCompletion(client.insertCallable().call(InsertAddressRequest.newBuilder()
                .setProject(getProjectId())
                .setRegion(getRegion())
                .setAddressResource(address)
                .build()));
        } catch (InvalidArgumentException ex) {
            if (ex.getCause().getMessage().contains("resourceNotReady")) {
                return false;
            }

            throw ex;
        }

        return true;
    }

    private Address getAddress(AddressesClient client) {
        Address address = null;

        try {
            address = client.get(GetAddressRequest.newBuilder().setProject(getProjectId()).setAddress(getName())
                .setRegion(getRegion()).build());

        } catch (NotFoundException | InvalidArgumentException ex) {
            // ignore
        }

        return address;
    }
}
