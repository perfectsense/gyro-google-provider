package gyro.google.compute;

import com.google.api.services.compute.model.Address;
import gyro.core.GyroUI;
import gyro.core.resource.Id;
import gyro.core.resource.Output;
import gyro.core.resource.Resource;
import gyro.core.scope.State;
import gyro.core.validation.Required;
import gyro.core.validation.ValidStrings;
import gyro.core.validation.ValidationError;
import gyro.google.Copyable;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

public abstract class AbstractAddressResource extends ComputeResource implements Copyable<Address> {

    protected static final String NAME_REGEX = "[a-z]([-a-z0-9]*[a-z0-9])?.";
    protected static final Pattern NAME_PATTERN = Pattern.compile(NAME_REGEX);
    private static final int SLEEP_TIMEOUT = 900;
    private static final int WAIT_TIMEOUT = 5000;

    private String name;
    private String description;
    private String address;
    private Integer prefixLength;
    private String addressType;
    private String purpose;
    private String subnetwork;
    private String network;

    /**
     * Name of the resource. [Required] See `Fields <https://cloud.google.com/compute/docs/reference/rest/v1/addresses#Address.FIELDS-table/>`_ for formatting requirements.
     */
    @Id
    @Required
    @Output
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * An optional description of the address.
     */
    @Output
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * An optional static IP address to set.
     */
    @Output
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * The prefix length if the resource reprensents an IP range.
     */
    @Output
    public Integer getPrefixLength() {
        return prefixLength;
    }

    public void setPrefixLength(Integer prefixLength) {
        this.prefixLength = prefixLength;
    }
    
    /**
     * Type of address to reserve. Valid values are "INTERNAL" or "EXTERNAL". Defaults to "EXTERNAL".
     */
    @Output
    @ValidStrings({"EXTERNAL", "INTERNAL"})
    public String getAddressType() {
        return addressType;
    }

    public void setAddressType(String addressType) {
        this.addressType = addressType;
    }

    /**
     * Purpose for this resource. Valid values are ``GCE_ENDPOINT``, ``DNS_RESOLVER``, ``VPC_PEERING`` or ``NAT_AUTO``.
     */
    @Output
    @ValidStrings({"GCE_ENDPOINT", "DNS_RESOLVER", "VPC_PEERING", "NAT_AUTO"})
    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    /**
     * URL of the subnetwork in which to reserve the address. If an IP address is specified, it must be within the subnetwork's IP range. This field can only be used with ``INTERNAL`` type with a ``GCE_ENDPOINT`` or ``DNS_RESOLVER`` purpose.
     */
    @Output
    public String getSubnetwork() {
        return subnetwork;
    }

    public void setSubnetwork(String subnetwork) {
        this.subnetwork = subnetwork;
    }

    /**
     * The URL of the network in which to reserve the address. This field can only be used with ``INTERNAL`` type with the ``VPC_PEERING`` purpose.
     */
    @Output
    public String getNetwork() {
        return network;
    }

    public void setNetwork(String network) {
        this.network = network;
    }


    @Override
    public void update(GyroUI ui, State state, Resource current, Set<String> changedFieldNames) throws Exception {
        // Do nothing, API doesn't support with an update and/or patch method.
    }

    @Override
    public void copyFrom(Address model) {
        setName(model.getName());
        setAddress(model.getAddress());
        setPrefixLength(model.getPrefixLength());
        setAddressType(model.getAddressType());
        setPurpose(model.getPurpose());
        setSubnetwork(model.getSubnetwork());
        setNetwork(model.getNetwork());
    }

    @Override
    public List<ValidationError> validate() {
        List<ValidationError> errors = new ArrayList<>();

        if ((getName() != null) && NAME_PATTERN.matcher(getName()).matches() && (getName().length() > 63)) {
            errors.add(new ValidationError(this, "name", "Does not adhere to naming standards."));
        }

        return errors;
    }
}

