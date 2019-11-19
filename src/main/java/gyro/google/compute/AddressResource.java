package gyro.google.compute;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.compute.Compute;
import com.google.api.services.compute.model.Address;
import gyro.core.GyroException;
import gyro.core.GyroUI;
import gyro.core.Type;
import gyro.core.scope.State;
import gyro.core.validation.Required;
import gyro.core.validation.ValidStrings;
import gyro.core.validation.ValidationError;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
    @ValidStrings({"PREMIUM", "STANDARD"})
    public String getNetworkTier() {
        return networkTier;
    }

    public void setNetworkTier(String networkTier) {
        this.networkTier = networkTier;
    }

    @Required
    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    @Override
    public boolean refresh() {
        Compute compute = createClient(Compute.class);
        try {
            Address address = compute.addresses().get(getProjectId(), getRegion(), getName()).execute();
            return (address != null);
        } catch (IOException e) {
            return false;
        }
    }

    @Override
    public void create(GyroUI ui, State state) throws Exception {
        Compute compute = createClient(Compute.class);
        Address address = new Address();
        address.setRegion(getRegion());
        address.setName(getName());
        address.setAddress(getAddress());
        address.setPrefixLength(getPrefixLength());
        address.setNetworkTier(getNetworkTier());
        address.setAddressType(getAddressType());
        address.setPurpose(getPurpose());
        address.setSubnetwork(getSubnetwork());
        address.setNetwork(getNetwork());

        try {
            waitForCompletion(compute, compute.addresses().insert(getProjectId(), getRegion(), address).execute());

            Address savedAddress = compute.addresses().get(getProjectId(), getRegion(), getName()).execute();
            setAddress(savedAddress.getAddress());
        } catch (GoogleJsonResponseException e) {
            throw new GyroException(e.getDetails().getMessage());
        }
    }

    @Override
    public void delete(GyroUI ui, State state) throws Exception {
        Compute compute = createClient(Compute.class);
        try {
            compute.addresses().delete(getProjectId(), getRegion(), getName()).execute();
        } catch (IOException e) {
            throw new GyroException(String.format("Unable to delete Address: %s, Google error: %s", getName(), e.getMessage()));
        }
    }

    @Override
    public List<ValidationError> validate() {
        List<ValidationError> errors = new ArrayList<>();

        if ((getName() != null) && NAME_PATTERN.matcher(getName()).matches() && (getName().length() > 63)) {
            errors.add(new ValidationError(this, "name", "Does not adhere to naming standards."));
        }

        return errors;
    }

    @Override
    public void copyFrom(Address model) {
        super.copyFrom(model);
        setNetworkTier(model.getNetworkTier());
        setRegion(model.getRegion());
    }
}
