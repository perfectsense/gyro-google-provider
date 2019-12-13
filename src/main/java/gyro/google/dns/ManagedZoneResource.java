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

package gyro.google.dns;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.google.api.client.util.Data;
import com.google.api.services.dns.Dns;
import com.google.api.services.dns.model.ManagedZone;
import com.google.api.services.dns.model.ManagedZoneDnsSecConfig;
import com.google.api.services.dns.model.ManagedZoneForwardingConfig;
import com.google.api.services.dns.model.ManagedZonePrivateVisibilityConfig;
import com.google.api.services.dns.model.Operation;
import gyro.core.GyroException;
import gyro.core.GyroUI;
import gyro.core.Type;
import gyro.core.resource.Output;
import gyro.core.resource.Resource;
import gyro.core.resource.Updatable;
import gyro.core.scope.State;
import gyro.core.validation.ConflictsWith;
import gyro.core.validation.Required;
import gyro.core.validation.ValidStrings;
import gyro.core.validation.ValidationError;
import gyro.google.Copyable;
import gyro.google.GoogleResource;

/**
 * Creates a Managed Zone.
 *
 * Example
 * -------
 *
 * .. code-block:: gyro
 *
 *     google::dns-managed-zone private-managed-zone-example
 *         name: "private-managed-zone-example"
 *         description: "Private Managed Zone Example"
 *         dns-name: "private.example.com."
 *         visibility: "private"
 *
 *         private-visibility-config
 *             network
 *                 network: $(google::compute-network managed-zone-network-example)
 *             end
 *         end
 *     end
 *
 *     google::dns-managed-zone public-managed-zone-example
 *         name: "public-managed-zone-example"
 *         description: "Public Managed Zone Example"
 *         dns-name: "p.example.com."
 *
 *         dnssec-config
 *             state: "on"
 *         end
 *     end
 */
@Type("dns-managed-zone")
public class ManagedZoneResource extends GoogleResource implements Copyable<ManagedZone> {

    private String description;

    private String dnsName;

    private ZoneDnsSecConfig dnssecConfig;

    private ZoneForwardingConfig forwardingConfig;

    private Map<String, String> labels;

    private String name;

    private String nameServerSet;

    private List<String> nameServers;

    private ZonePrivateVisibilityConfig privateVisibilityConfig;

    private String visibility;

    /**
     * A mutable string of at most 1024 characters associated with this resource for the user's convenience. Has no effect on the managed zone's function.
     */
    @Required
    @Updatable
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * The DNS name of this managed zone, for instance ``example.com.``.
     */
    public String getDnsName() {
        return dnsName;
    }

    public void setDnsName(String dnsName) {
        this.dnsName = dnsName;
    }

    /**
     * DNSSEC configuration.
     *
     * @subresource gyro.google.dns.ZoneDnsSecConfig
     */
    @ConflictsWith({ "forwarding-config", "private-visibility-config" })
    // XXX: https://github.com/perfectsense/gyro/issues/190
    @Updatable
    public ZoneDnsSecConfig getDnssecConfig() {
        return dnssecConfig;
    }

    public void setDnssecConfig(ZoneDnsSecConfig dnssecConfig) {
        this.dnssecConfig = dnssecConfig;
    }

    /**
     * The presence for this field indicates that outbound forwarding is enabled for this zone. The value of this field contains the set of destinations to forward to.
     *
     * @subresource gyro.google.dns.ZoneForwardingConfig
     */
    @ConflictsWith("dnssec-config")
    // XXX: https://github.com/perfectsense/gyro/issues/190
    @Updatable
    public ZoneForwardingConfig getForwardingConfig() {
        return forwardingConfig;
    }

    public void setForwardingConfig(ZoneForwardingConfig forwardingConfig) {
        this.forwardingConfig = forwardingConfig;
    }

    /**
     * User labels.
     */
    @Updatable
    public Map<String, String> getLabels() {
        if (labels == null) {
            labels = new HashMap<>();
        }
        return labels;
    }

    public void setLabels(Map<String, String> labels) {
        this.labels = labels;
    }

    /**
     * User assigned name for this resource. Must be unique within the project. The name must be 1-63 characters long, must begin with a letter, end with a letter or digit, and only contain lowercase letters, digits or dashes.
     */
    @Required
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * Optionally specifies the NameServerSet for this ManagedZone. A NameServerSet is a set of DNS name servers that all host the same ManagedZones. Most users will leave this field unset.
     */
    public String getNameServerSet() {
        return nameServerSet;
    }

    public void setNameServerSet(String nameServerSet) {
        this.nameServerSet = nameServerSet;
    }

    /**
     * Delegate your managed_zone to these virtual name servers.
     */
    @Output
    public List<String> getNameServers() {
        if (nameServers == null) {
            nameServers = new ArrayList<>();
        }
        return nameServers;
    }

    public void setNameServers(List<String> nameServers) {
        this.nameServers = nameServers;
    }

    /**
     * For privately visible zones, the set of Virtual Private Cloud resources that the zone is visible from.
     *
     * @subresource gyro.google.dns.ZonePrivateVisibilityConfig
     */
    @ConflictsWith("dnssec-config")
    // XXX: https://github.com/perfectsense/gyro/issues/190
    @Updatable
    public ZonePrivateVisibilityConfig getPrivateVisibilityConfig() {
        return privateVisibilityConfig;
    }

    public void setPrivateVisibilityConfig(ZonePrivateVisibilityConfig privateVisibilityConfig) {
        this.privateVisibilityConfig = privateVisibilityConfig;
    }

    /**
     * The zone's visibility: public zones are exposed to the Internet, while private zones are visible only to Virtual Private Cloud resources.
     */
    @ValidStrings({ "private", "public" })
    public String getVisibility() {
        return visibility;
    }

    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }

    @Override
    public boolean doRefresh() throws Exception {
        Dns client = createClient(Dns.class);
        ManagedZone managedZone = client.managedZones().get(getProjectId(), getName()).execute();
        copyFrom(managedZone);
        return true;
    }

    @Override
    public void doCreate(GyroUI ui, State state) throws Exception {
        ManagedZone managedZone = createManagedZone();
        Dns client = createClient(Dns.class);
        ManagedZone response = client.managedZones().create(getProjectId(), managedZone).execute();
        copyFrom(response);
    }

    @Override
    public void doUpdate(GyroUI ui, State state, Resource current, Set<String> changedFieldNames) throws Exception {
        if (!(current instanceof ManagedZoneResource)) {
            throw new GyroException("Incompatible resource type! " + current.getClass().getName());
        }
        ManagedZone managedZone = new ManagedZone();

        for (String changedFieldName : changedFieldNames) {
            if (changedFieldName.equals("description")) {
                managedZone.setDescription(getDescription());
            } else if (changedFieldName.equals("dnssec-config")) {
                ZoneDnsSecConfig dnssecConfig = getDnssecConfig();

                if (dnssecConfig == null) {
                    throw new GyroException("'dnssec-config' can't be removed once set.");
                }
                managedZone.setDnssecConfig(dnssecConfig.copyTo());
            } else if (changedFieldName.equals("forwarding-config")) {
                ZoneForwardingConfig forwardingConfig = getForwardingConfig();

                if (forwardingConfig == null) {
                    throw new GyroException("'forwarding-config' can't be removed once set.");
                }
                managedZone.setForwardingConfig(forwardingConfig.copyTo());
            } else if (changedFieldName.equals("labels")) {
                managedZone.setLabels(Data.nullOf(HashMap.class));
                patch(ui, managedZone, false);
                managedZone.setLabels(getLabels());
            } else if (changedFieldName.equals("private-visibility-config")) {
                ZonePrivateVisibilityConfig privateVisibilityConfig = getPrivateVisibilityConfig();

                if (privateVisibilityConfig == null) {
                    throw new GyroException("'private-visibility-config' can't be removed once set.");
                }
                managedZone.setPrivateVisibilityConfig(privateVisibilityConfig.copyTo());
            }
        }
        patch(ui, managedZone, true);
    }

    @Override
    public void doDelete(GyroUI ui, State state) throws Exception {
        Dns client = createClient(Dns.class);
        client.managedZones().delete(getProjectId(), getName()).execute();
    }

    @Override
    public void copyFrom(ManagedZone model) {
        setDescription(model.getDescription());
        setDnsName(model.getDnsName());
        ZoneDnsSecConfig diffableDnsSecConfig = null;
        ManagedZoneDnsSecConfig dnssecConfig = model.getDnssecConfig();

        if (dnssecConfig != null) {
            diffableDnsSecConfig = Optional.ofNullable(getDnssecConfig())
                .orElse(newSubresource(ZoneDnsSecConfig.class));
            diffableDnsSecConfig.copyFrom(dnssecConfig);
        }
        setDnssecConfig(diffableDnsSecConfig);
        ZoneForwardingConfig diffableForwardingConfig = null;
        ManagedZoneForwardingConfig forwardingConfig = model.getForwardingConfig();

        if (forwardingConfig != null) {
            diffableForwardingConfig = Optional.ofNullable(getForwardingConfig())
                .orElse(newSubresource(ZoneForwardingConfig.class));
            diffableForwardingConfig.copyFrom(forwardingConfig);
        }
        setForwardingConfig(diffableForwardingConfig);
        setLabels(model.getLabels());
        setName(model.getName());
        setNameServerSet(model.getNameServerSet());
        setNameServers(model.getNameServers());
        ZonePrivateVisibilityConfig diffablePrivateVisibilityConfig = null;
        ManagedZonePrivateVisibilityConfig privateVisibilityConfig = model.getPrivateVisibilityConfig();

        if (privateVisibilityConfig != null) {
            diffablePrivateVisibilityConfig = Optional.ofNullable(getPrivateVisibilityConfig())
                .orElse(newSubresource(ZonePrivateVisibilityConfig.class));
            diffablePrivateVisibilityConfig.copyFrom(privateVisibilityConfig);
        }
        setPrivateVisibilityConfig(diffablePrivateVisibilityConfig);
        setVisibility(model.getVisibility());
    }

    @Override
    public List<ValidationError> validate() {
        List<ValidationError> errors = new ArrayList<>();
        String visibility = getVisibility();

        if (visibility == null || visibility.equals("public")) {
            if (getForwardingConfig() != null) {
                errors.add(new ValidationError(
                    this,
                    "forwarding-config",
                    "'forwarding-config' can't be provided in public zone."));
            }

            if (getPrivateVisibilityConfig() != null) {
                errors.add(new ValidationError(
                    this,
                    "private-visibility-config",
                    "'private-visibility-config' can't be provided in public zone."));
            }
        } else if (visibility.equals("private")) {
            if (getDnssecConfig() != null) {
                errors.add(new ValidationError(
                    this,
                    "dnssec-config",
                    "'dnssec-config' can't be provided in private zone."));
            }
        }
        return errors;
    }

    private ManagedZone createManagedZone() {
        ManagedZone managedZone = new ManagedZone();
        managedZone.setDescription(getDescription());
        managedZone.setDnsName(getDnsName());
        ZoneDnsSecConfig dnssecConfig = getDnssecConfig();

        if (dnssecConfig != null) {
            managedZone.setDnssecConfig(dnssecConfig.copyTo());
        }
        ZoneForwardingConfig forwardingConfig = getForwardingConfig();

        if (forwardingConfig != null) {
            managedZone.setForwardingConfig(forwardingConfig.copyTo());
        }
        managedZone.setLabels(getLabels());
        managedZone.setName(getName());
        ZonePrivateVisibilityConfig privateVisibilityConfig = getPrivateVisibilityConfig();

        if (privateVisibilityConfig != null) {
            managedZone.setPrivateVisibilityConfig(privateVisibilityConfig.copyTo());
        }
        managedZone.setVisibility(getVisibility());
        return managedZone;
    }

    private void patch(GyroUI ui, ManagedZone managedZone, boolean shouldRefresh) throws Exception {
        Dns client = createClient(Dns.class);
        Operation response = client.managedZones().patch(getProjectId(), getName(), managedZone).execute();
        Dns.ManagedZoneOperations.Get getRequest = client
            .managedZoneOperations()
            .get(getProjectId(), getName(), response.getId());

        // TODO: limit retry?
        long count = 1;

        while (response.getStatus().equals("pending")) {
            ui.write("\nWaiting to be updated.");
            Thread.sleep(1000L * count++);
            response = getRequest.execute();
        }
        if (shouldRefresh) {
            copyFrom(response.getZoneContext().getNewValue());
        }
    }
}
