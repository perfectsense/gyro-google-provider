package gyro.google.compute;

import com.google.api.services.compute.model.Firewall.Allowed;
import com.google.api.services.compute.model.Firewall.Denied;
import gyro.core.resource.Diffable;
import gyro.core.resource.Updatable;
import gyro.core.validation.Required;
import gyro.core.validation.ValidationError;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class FirewallAllowDenyRule extends Diffable {
    private static final Pattern PORT_PATTERN = Pattern.compile("((?!(0))\\d+(-[1-9]\\d+)?)");

    private String protocol;
    private Set<String> ports;

    /**
     * The protocol that is going to be matched for the incoming/outgoing requests. Valid values are ``tcp``, ``udp``, ``icmp``, ``esp``, ``ah``, ``ipip``, ``sctp`` or a IP protocol number. (Required)
     */
    @Required
    public String getProtocol() {
        return protocol != null ? protocol.toLowerCase() : null;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    /**
     * A list of ports associated with the protocol. Can only be set when 'protocol' set to  ``tcp`` or ``udp``. Valid values are valid port number or port number range. Ex. ``22`` or ``22-443``.
     */
    @Updatable
    public Set<String> getPorts() {
        if (ports == null) {
            ports = new HashSet<>();
        }

        return ports;
    }

    public void setPorts(Set<String> ports) {
        this.ports = ports;
    }

    @Override
    public String primaryKey() {
        return getProtocol();
    }

    void copyFrom(Allowed allowed) {
        setProtocol(allowed.getIPProtocol());
        setPorts(allowed.getPorts() != null ? new HashSet<>(allowed.getPorts()) : null);
    }

    void copyFrom(Denied denied) {
        setProtocol(denied.getIPProtocol());
        setPorts(denied.getPorts() != null ? new HashSet<>(denied.getPorts()) : null);
    }

    Allowed toAllowed() {
        Allowed allowed = new Allowed();
        allowed.setIPProtocol(getProtocol());
        if (!getPorts().isEmpty()) {
            allowed.setPorts(new ArrayList<>(getPorts()));
        }
        return allowed;
    }

    Denied toDenied() {
        Denied denied = new Denied();
        denied.setIPProtocol(getProtocol());
        if (!getPorts().isEmpty()) {
            denied.setPorts(new ArrayList<>(getPorts()));
        }
        return denied;
    }

    @Override
    public List<ValidationError> validate() {
        List<ValidationError> errors = new ArrayList<>();

        if (!getProtocol().equals("tcp") && !getProtocol().equals("udp") && !getPorts().isEmpty()) {
            errors.add(new ValidationError(this, "ports", "'ports' can only be set when 'protocol' is set to either 'tcp' or 'udp'"));
        }

        List<String> invalidPorts = getPorts().stream().filter(port -> !validatePort(port)).collect(Collectors.toList());
        if (!invalidPorts.isEmpty()) {
            for (String port : invalidPorts) {
                errors.add(new ValidationError(this, "ports", String.format("invalid entry %s. Must be an integer or a valid range", port)));
            }
        }

        return errors;
    }

    private boolean validatePort(String port) {
        if (PORT_PATTERN.matcher(port).matches()) {
            String[] split = port.split("-");
            return split.length == 1 || (Integer.parseInt(split[0]) < Integer.parseInt(split[1]));
        } else {
            return false;
        }
    }
}
