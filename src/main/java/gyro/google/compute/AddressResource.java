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

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.compute.Compute;
import com.google.api.services.compute.model.Address;
import gyro.core.GyroException;
import gyro.core.GyroUI;
import gyro.core.Type;
import gyro.core.resource.Updatable;
import gyro.core.scope.State;
import gyro.core.validation.Required;
import gyro.core.validation.ValidStrings;

import java.io.IOException;

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
     * Networking tier used for configuring this address. Valid values are ``PREMIUM`` or ``STANDARD``. Defaults to ``PREMIUM``.
     */
    @Updatable
    @ValidStrings({"PREMIUM", "STANDARD"})
    public String getNetworkTier() {
        return networkTier;
    }

    public void setNetworkTier(String networkTier) {
        this.networkTier = networkTier;
    }

    @Required
    @Updatable
    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        if ((region != null) && region.startsWith("http")) {
            String[] paths = region.split("/");
            region = paths[paths.length - 1];
        }
        this.region = region;
    }

    @Override
    public boolean refresh() {
        Compute compute = createClient(Compute.class);
        try {
            Address address = compute.addresses().get(getProjectId(), getRegion(), getName()).execute();

            if (address == null) {
                return false;
            }

            copyFrom(address);

            return true;

        } catch (GoogleJsonResponseException e) {
            throw new GyroException(e.getDetails().getMessage());
        } catch (IOException e) {
            throw new GyroException(e.getMessage(), e.getCause());
        }
    }

    @Override
    public void create(GyroUI ui, State state) throws Exception {
        Compute compute = createClient(Compute.class);
        Address address = copyTo()
                .setRegion(getRegion())
                .setNetworkTier(getNetwork() != null ? getNetwork().getSelfLink() : null);

        try {
            waitForCompletion(compute, compute.addresses().insert(getProjectId(), getRegion(), address).execute());
            refresh();

        } catch (GoogleJsonResponseException e) {
            throw new GyroException(e.getDetails().getMessage());
        }
    }

    @Override
    public void delete(GyroUI ui, State state) throws Exception {
        Compute compute = createClient(Compute.class);
        try {
            compute.addresses().delete(getProjectId(), getRegion(), getName()).execute();

        } catch (GoogleJsonResponseException e) {
            throw new GyroException(e.getDetails().getMessage());
        } catch (IOException e) {
            throw new GyroException(e.getMessage(), e.getCause());
        }
    }

    @Override
    public void copyFrom(Address model) {
        super.copyFrom(model);
        setNetworkTier(model.getNetworkTier());
        setRegion(model.getRegion());
    }
}
