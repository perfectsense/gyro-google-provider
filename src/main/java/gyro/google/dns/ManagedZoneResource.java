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

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
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
import gyro.google.Requestable;

/**
 * Creates a Managed Zone.
 *
 * Example
 * -------
 *
 * .. code-block:: gyro
 *
 *     google::dns-managed-zone managed-zone-example-private
 *         name: "managed-zone-example-private"
 *         description: "Private Managed Zone Example"
 *         dns-name: "private.example.com."
 *         visibility: "private"
 *
 *         private-visibility-config
 *             network
 *                 network: $(google::compute-network network-example)
 *             end
 *         end
 *     end
 *
 *     google::dns-managed-zone managed-zone-example-public
 *         name: "managed-zone-example-public"
 *         description: "Public Managed Zone Example"
 *         dns-name: "p.example.com."
 *         dnssec-config
 *             state: "on"
 *         end
 *     end
 */
@Type("dns-managed-zone")
public class ManagedZoneResource extends GoogleResource implements Copyable<ManagedZone>, Requestable<ManagedZone> {

    private String creationTime;

    private String description;

    private String dnsName;

    private ZoneDnsSecConfig dnssecConfig;

    private ZoneForwardingConfig forwardingConfig;

    private String id;

    private Map<String, String> labels;

    private String name;

    private String nameServerSet;

    private List<String> nameServers;

    private ZonePrivateVisibilityConfig privateVisibilityConfig;

    private String visibility;

    /**
     * The time that this resource was created on the server. This is in RFC3339 text format. Output only. The value may be ``null``.
     */
    @Output
    public String getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(String creationTime) {
        this.creationTime = creationTime;
    }

    /**
     * A mutable string of at most 1024 characters associated with this resource for the user's convenience. Has no effect on the managed zone's function. The value may be ``null``.
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
     * The DNS name of this managed zone, for instance ``example.com.``. The value may be ``null``.
     */
    public String getDnsName() {
        return dnsName;
    }

    public void setDnsName(String dnsName) {
        this.dnsName = dnsName;
    }

    /**
     * DNSSEC configuration. The value may be ``null``.
     *
     * @subresource gyro.google.dns.ZoneDnsSecConfig
     */
    @ConflictsWith({ "forwarding-config", "private-visibility-config" })
    // TODO: xxx
    @Updatable
    public ZoneDnsSecConfig getDnssecConfig() {
        return dnssecConfig;
    }

    public void setDnssecConfig(ZoneDnsSecConfig dnssecConfig) {
        this.dnssecConfig = dnssecConfig;
    }

    /**
     * The presence for this field indicates that outbound forwarding is enabled for this zone. The value of this field contains the set of destinations to forward to. The value may be ``null``.
     *
     * @subresource gyro.google.dns.ZoneForwardingConfig
     */
    @ConflictsWith("dnssec-config")
    // TODO: xxx
    @Updatable
    public ZoneForwardingConfig getForwardingConfig() {
        return forwardingConfig;
    }

    public void setForwardingConfig(ZoneForwardingConfig forwardingConfig) {
        this.forwardingConfig = forwardingConfig;
    }

    /**
     * Unique identifier for the resource; defined by the server (output only) The value may be ``null``.
     */
    @Output
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    /**
     * User labels. The value may be ``null``.
     */
    @Updatable
    public Map<String, String> getLabels() {
        return labels;
    }

    public void setLabels(Map<String, String> labels) {
        this.labels = labels;
    }

    /**
     * User assigned name for this resource. Must be unique within the project. The name must be 1-63 characters long, must begin with a letter, end with a letter or digit, and only contain lowercase letters, digits or dashes. The value may be ``null``.
     */
    @Required
    @Updatable
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * Optionally specifies the NameServerSet for this ManagedZone. A NameServerSet is a set of DNS name servers that all host the same ManagedZones. Most users will leave this field unset. The value may be ``null``.
     */
    public String getNameServerSet() {
        return nameServerSet;
    }

    public void setNameServerSet(String nameServerSet) {
        this.nameServerSet = nameServerSet;
    }

    /**
     * Delegate your managed_zone to these virtual name servers; defined by the server (output only) The value may be ``null``.
     */
    @Output
    public List<String> getNameServers() {
        return nameServers;
    }

    public void setNameServers(List<String> nameServers) {
        this.nameServers = nameServers;
    }

    /**
     * For privately visible zones, the set of Virtual Private Cloud resources that the zone is visible from. The value may be ``null``.
     *
     * @subresource gyro.google.dns.ZonePrivateVisibilityConfig
     */
    @ConflictsWith("dnssec-config")
    // TODO: xxx
    @Updatable
    public ZonePrivateVisibilityConfig getPrivateVisibilityConfig() {
        return privateVisibilityConfig;
    }

    public void setPrivateVisibilityConfig(ZonePrivateVisibilityConfig privateVisibilityConfig) {
        this.privateVisibilityConfig = privateVisibilityConfig;
    }

    /**
     * The zone's visibility: public zones are exposed to the Internet, while private zones are visible only to Virtual Private Cloud resources. The value may be ``null``.
     */
    @ValidStrings({ "private", "public" })
    public String getVisibility() {
        return visibility;
    }

    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }

    @Override
    public boolean refresh() {
        Dns client = createClient(Dns.class);

        try {
            ManagedZone managedZone = client.managedZones().get(getProjectId(), getName()).execute();
            return refreshFrom(managedZone);
        } catch (GoogleJsonResponseException je) {
            if (je.getDetails().getMessage().matches("The resource (.*) was not found")) {
                return false;
            } else {
                throw new GyroException(je.getDetails().getMessage());
            }
        } catch (IOException ex) {
            throw new GyroException(ex.getMessage(), ex.getCause());
        }
    }

    @Override
    public void create(GyroUI ui, State state) throws Exception {
        Dns client = createClient(Dns.class);
        ManagedZone managedZone = createManagedZone();

        try {
            ManagedZone response = client.managedZones().create(getProjectId(), managedZone).execute();
            refreshFrom(response);
        } catch (Exception ex) {
            throw new GyroException(ex.getMessage(), ex.getCause());
        }
    }

    @Override
    public void update(GyroUI ui, State state, Resource current, Set<String> changedFieldNames) throws Exception {
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
            } else if (changedFieldName.equals("name")) {
                managedZone.setName(getName());
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
    public void delete(GyroUI ui, State state) throws Exception {
        Dns client = createClient(Dns.class);
        client.managedZones().delete(getProjectId(), getName()).execute();
    }

    @Override
    public void copyFrom(ManagedZone model) {
        setCreationTime(model.getCreationTime());
        setDescription(model.getDescription());
        setDnsName(model.getDnsName());
        ManagedZoneDnsSecConfig dnssecConfig = model.getDnssecConfig();

        if (dnssecConfig != null) {
            ZoneDnsSecConfig zoneDnsSecConfig = getDnssecConfig();
            zoneDnsSecConfig.copyFrom(dnssecConfig);
        }
        ManagedZoneForwardingConfig forwardingConfig = model.getForwardingConfig();

        if (forwardingConfig != null) {
            ZoneForwardingConfig zoneForwardingConfig = getForwardingConfig();
            zoneForwardingConfig.copyFrom(forwardingConfig);
        }
        setId(model.getId().toString());
        setLabels(model.getLabels());
        setName(model.getName());
        setNameServerSet(model.getNameServerSet());
        setNameServers(model.getNameServers());
        ManagedZonePrivateVisibilityConfig privateVisibilityConfig = model.getPrivateVisibilityConfig();

        if (privateVisibilityConfig != null) {
            ZonePrivateVisibilityConfig zonePrivateVisibilityConfig = getPrivateVisibilityConfig();
            zonePrivateVisibilityConfig.copyFrom(privateVisibilityConfig);
        }
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

    @Override
    public ManagedZone copyTo() {
        ManagedZone managedZone = createManagedZone();
        managedZone.setCreationTime(getCreationTime());
        managedZone.setId(new BigInteger(getId()));
        managedZone.setLabels(getLabels());
        managedZone.setNameServerSet(getNameServerSet());
        managedZone.setNameServers(getNameServers());
        return managedZone;
    }

    private boolean refreshFrom(ManagedZone managedZone) {
        copyFrom(managedZone);
        return true;
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

    private void patch(GyroUI ui, ManagedZone managedZone, boolean shouldRefresh) {
        Dns client = createClient(Dns.class);

        try {
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
                refreshFrom(response.getZoneContext().getNewValue());
            }
        } catch (Exception ex) {
            throw new GyroException(ex.getMessage(), ex.getCause());
        }
    }
}