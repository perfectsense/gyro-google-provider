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

import java.io.IOException;

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
     * IP version that will be used by this address. Valid values are ``IPV4`` or ``IPV6``. Defaults to ``IPV4``.
     */
    @Updatable
    public String getIpVersion() {
        return ipVersion;
    }

    public void setIpVersion(String ipVersion) {
        this.ipVersion = ipVersion;
    }


    @Override
    public boolean refresh() {
        Compute compute = createClient(Compute.class);
        try {
            Address address = compute.globalAddresses().get(getProjectId(), getName()).execute();

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
        Address address = copyTo().setIpVersion(getIpVersion());

        try {
            waitForCompletion(compute, compute.globalAddresses().insert(getProjectId(), address).execute());
            refresh();

        } catch (GoogleJsonResponseException e) {
            throw new GyroException(e.getDetails().getMessage());
        }
    }

    @Override
    public void delete(GyroUI ui, State state) throws Exception {
        Compute compute = createClient(Compute.class);
        try {
            compute.globalAddresses().delete(getProjectId(), getName()).execute();

        } catch (GoogleJsonResponseException e) {
            throw new GyroException(e.getDetails().getMessage());
        } catch (IOException e) {
            throw new GyroException(e.getMessage(), e.getCause());
        }
    }

    @Override
    public void copyFrom(Address model) {
        super.copyFrom(model);
        setIpVersion(model.getIpVersion());
    }
}
