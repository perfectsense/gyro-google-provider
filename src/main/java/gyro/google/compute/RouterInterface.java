/*
 * Copyright 2020, Perfect Sense, Inc.
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
import java.util.Set;

import gyro.core.resource.Diffable;
import gyro.core.validation.ConflictsWith;
import gyro.core.validation.Regex;
import gyro.core.validation.Required;
import gyro.core.validation.ValidationError;
import gyro.google.Copyable;

public class RouterInterface extends Diffable
    implements Copyable<com.google.api.services.compute.model.RouterInterface> {

    private String name;
    private String linkedVpnTunnel;
    private String linkedInterconnectAttachment;
    private String ipRange;

    /**
     * The name of the router interface. Must be a string starting with a lowercase letter, followed by hyphens, lowercase letters, or digits, except the last character, which cannot be a hyphen.
     */
    @Required
    @Regex(value = "^[a-z]([-a-z0-9]*[a-z0-9])?$", message = "a string starting with a lowercase letter, followed by hyphens, lowercase letters, or digits, except the last character, which cannot be a hyphen.")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * The Url of the linked VPN tunnel.
     */
    @ConflictsWith("linked-interconnect-attachment")
    public String getLinkedVpnTunnel() {
        return linkedVpnTunnel;
    }

    public void setLinkedVpnTunnel(String linkedVpnTunnel) {
        this.linkedVpnTunnel = linkedVpnTunnel;
    }

    /**
     * The Url of the linked Interconnect attachment.
     */
    @ConflictsWith("linked-vpn-tunnel")
    public String getLinkedInterconnectAttachment() {
        return linkedInterconnectAttachment;
    }

    public void setLinkedInterconnectAttachment(String linkedInterconnectAttachment) {
        this.linkedInterconnectAttachment = linkedInterconnectAttachment;
    }

    /**
     * The IP address and range of the interface.
     */
    public String getIpRange() {
        return ipRange;
    }

    public void setIpRange(String ipRange) {
        this.ipRange = ipRange;
    }

    @Override
    public String primaryKey() {
        return getName();
    }

    @Override
    public void copyFrom(com.google.api.services.compute.model.RouterInterface model) throws Exception {
        setName(model.getName());
        setIpRange(model.getIpRange());
        setLinkedInterconnectAttachment(model.getLinkedInterconnectAttachment());
        setLinkedVpnTunnel(model.getLinkedVpnTunnel());
    }

    com.google.api.services.compute.model.RouterInterface toRouterInterface() {
        com.google.api.services.compute.model.RouterInterface routerInterface = new com.google.api.services.compute.model.RouterInterface();
        routerInterface.setName(getName());
        routerInterface.setIpRange(getIpRange());
        routerInterface.setLinkedInterconnectAttachment(getLinkedInterconnectAttachment());
        routerInterface.setLinkedVpnTunnel(getLinkedVpnTunnel());

        return routerInterface;
    }

    @Override
    public List<ValidationError> validate(Set<String> configuredFields) {
        List<ValidationError> errors = new ArrayList<>();

        if (!configuredFields.contains("linked-vpn-tunnel") && !configuredFields.contains(
            "linked-interconnect-attachment")
            && !configuredFields.contains("ip-range")) {
            errors.add(new ValidationError(
                this,
                null,
                "At least one of 'linked-vpn-tunnel' or 'linked-interconnect-attachment' or 'ip-range' is required"));
        }

        return errors;
    }
}
