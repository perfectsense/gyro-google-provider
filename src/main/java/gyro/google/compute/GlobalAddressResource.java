package gyro.google.compute;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.compute.Compute;
import com.google.api.services.compute.model.Address;
import gyro.core.GyroException;
import gyro.core.GyroUI;
import gyro.core.Type;
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
            return (address != null);
        } catch (IOException e) {
            return false;
        }
    }

    @Override
    public void create(GyroUI ui, State state) throws Exception {
        Compute compute = createClient(Compute.class);
        Address address = new Address();
        address.setName(getName());
        address.setAddress(getAddress());
        address.setPrefixLength(getPrefixLength());
        address.setIpVersion(getIpVersion());
        address.setAddressType(getAddressType());
        address.setPurpose(getPurpose());
        address.setSubnetwork(getSubnetwork());
        address.setNetwork(getNetwork());

        try {
            waitForCompletion(compute, compute.globalAddresses().insert(getProjectId(), address).execute());

            Address savedAddress = compute.globalAddresses().get(getProjectId(), getName()).execute();
            setAddress(savedAddress.getAddress());
        } catch (GoogleJsonResponseException e) {
            throw new GyroException(e.getDetails().getMessage());
        }
    }

    @Override
    public void delete(GyroUI ui, State state) throws Exception {
        Compute compute = createClient(Compute.class);
        try {
            compute.globalAddresses().delete(getProjectId(), getName()).execute();
        } catch (IOException e) {
            throw new GyroException(String.format("Unable to delete Address: %s, Google error: %s", getName(), e.getMessage()));
        }
    }

    @Override
    public void copyFrom(Address model) {
        super.copyFrom(model);
        setIpVersion(model.getIpVersion());
    }
}
