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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import gyro.core.resource.Diffable;
import gyro.core.resource.Updatable;
import gyro.core.validation.Required;
import gyro.core.validation.ValidationError;

public class FirewallRule extends Diffable {

    private static final Pattern PORT_PATTERN = Pattern.compile("(?<start>[1-9]\\d*)-?(?<end>[1-9]\\d*)?");

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

    @Override
    public List<ValidationError> validate() {
        List<ValidationError> errors = new ArrayList<>();

        if (!getProtocol().equals("tcp") && !getProtocol().equals("udp") && !getPorts().isEmpty()) {
            errors.add(new ValidationError(
                this,
                "ports",
                "'ports' can only be set when 'protocol' is set to either 'tcp' or 'udp'"));
        }

        List<String> invalidPorts = getPorts().stream()
            .filter(port -> !validatePort(port))
            .collect(Collectors.toList());
        if (!invalidPorts.isEmpty()) {
            for (String port : invalidPorts) {
                errors.add(new ValidationError(
                    this,
                    "ports",
                    String.format("invalid entry %s. Must be an integer or a valid range", port)));
            }
        }

        return errors;
    }

    /**
     * Checks if the given port string is valid or not.
     * Valid ports are any positive numbers without leading zeroes, or a range specified with '-' with numbers on either side. The start number less that the end number and both numbers being positive without leading zeroes
     * @param port The port string to check for validity
     * @return true if valid false if invalid
     */
    private boolean validatePort(String port) {
        Matcher matcher = PORT_PATTERN.matcher(port);
        if (matcher.matches()) {
            Integer start = Integer.valueOf(matcher.group("start"));
            Integer end = matcher.group("end") != null
                ? Integer.parseInt(matcher.group("end")) : port.contains("-") ? -1 : 0;

            return end == 0 || (start >= 0 && end >= 0 && start < end);
        }

        return false;
    }
}
