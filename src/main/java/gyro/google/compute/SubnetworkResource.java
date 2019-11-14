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

import java.io.IOException;
import java.util.Set;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.compute.Compute;
import com.google.api.services.compute.model.Operation;
import com.google.api.services.compute.model.Subnetwork;
import com.google.api.services.compute.model.SubnetworksSetPrivateIpGoogleAccessRequest;
import com.google.cloud.compute.v1.ProjectGlobalNetworkName;
import gyro.core.GyroException;
import gyro.core.GyroUI;
import gyro.core.Type;
import gyro.core.resource.Output;
import gyro.core.resource.Resource;
import gyro.core.resource.Updatable;
import gyro.core.scope.State;
import gyro.core.validation.Required;
import gyro.google.Copyable;

/**
 * Creates a subnet.
 *
 * Example
 * -------
 *
 * .. code-block:: gyro
 *
 *     google::subnet subnet-example
 *         name: "subnet-example"
 *         description: "subnet-example-description"
 *         ip-cidr-range: "10.0.0.0/16"
 *         network: $(google::network network-example-subnet)
 *         region: "us-east1"
 *     end
 */
@Type("subnet")
public class SubnetworkResource extends ComputeResource implements Copyable<Subnetwork> {
    private String name;
    private String description;
    private String ipCidrRange;
    private NetworkResource network;
    private String region;
    private Boolean enableFlowLogs;
    private Boolean privateIpGoogleAccess;

    // Read-only
    private String id;

    /**
     * The name of the subnet. (Required)
     */
    @Required
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * The description of the subnet.
     */
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * The IPv4 network range for the subnet, in CIDR notation. (Required)
     */
    @Required
    public String getIpCidrRange() {
        return ipCidrRange;
    }

    public void setIpCidrRange(String ipCidrRange) {
        this.ipCidrRange = ipCidrRange;
    }

    /**
     * The network to create this subnet in. (Required)
     */
    @Required
    public NetworkResource getNetwork() {
        return network;
    }

    public void setNetwork(NetworkResource network) {
        this.network = network;
    }

    /**
     * The region to create this subnet in. (Required)
     */
    @Required
    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    /**
     * When true, enables flow logs. Defaults to ``false``.
     */
    @Updatable
    public Boolean getEnableFlowLogs() {
        if (enableFlowLogs == null) {
            enableFlowLogs = false;
        }

        return enableFlowLogs;
    }

    public void setEnableFlowLogs(Boolean enableFlowLogs) {
        this.enableFlowLogs = enableFlowLogs;
    }

    /**
     * When true, allows virtual machines in this subnet that only have private IPs to access Google APIs and services. See `Configuring Private Google Access <https://cloud.google.com/vpc/docs/configure-private-google-access>`_. Defaults to ``false``.
     */
    @Updatable
    public Boolean getPrivateIpGoogleAccess() {
        if (privateIpGoogleAccess == null) {
            privateIpGoogleAccess = false;
        }

        return privateIpGoogleAccess;
    }

    public void setPrivateIpGoogleAccess(Boolean privateIpGoogleAccess) {
        this.privateIpGoogleAccess = privateIpGoogleAccess;
    }

    /**
     * The Id of the subnet.
     */
    @Output
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public void copyFrom(Subnetwork subnetwork) {
        setId(subnetwork.getId().toString());
        setDescription(subnetwork.getDescription());
        setIpCidrRange(subnetwork.getIpCidrRange());
        setEnableFlowLogs(subnetwork.getEnableFlowLogs());
        setPrivateIpGoogleAccess(subnetwork.getPrivateIpGoogleAccess());
        setName(subnetwork.getName());
        setNetwork(findById(NetworkResource.class, subnetwork.getNetwork().substring(subnetwork.getNetwork().lastIndexOf("/") + 1)));
        setRegion(subnetwork.getRegion().substring(subnetwork.getRegion().lastIndexOf("/") + 1));
    }

    @Override
    public boolean refresh() {
        Compute client = createComputeClient();

        try {
            Subnetwork subnetwork = client.subnetworks().get(getProjectId(), getRegion(), getName()).execute();
            copyFrom(subnetwork);

            return true;
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
    public void create(GyroUI ui, State state) {
        Compute client = createComputeClient();

        Subnetwork subnetwork = new Subnetwork();
        subnetwork.setName(getName());
        subnetwork.setNetwork(ProjectGlobalNetworkName.format(getNetwork().getName(), getProjectId()));
        subnetwork.setDescription(getDescription());
        subnetwork.setIpCidrRange(getIpCidrRange());
        subnetwork.setEnableFlowLogs(getEnableFlowLogs());
        subnetwork.setPrivateIpGoogleAccess(getPrivateIpGoogleAccess());

        try {
            Compute.Subnetworks.Insert insert = client.subnetworks().insert(getProjectId(), getRegion(), subnetwork);
            Operation operation = insert.execute();
            waitForCompletion(client, operation);

            refresh();
        } catch (GyroException ge) {
            throw ge;
        } catch (Exception ex) {
            throw new GyroException(ex.getMessage(), ex.getCause());
        }
    }

    @Override
    public void update(GyroUI ui, State state, Resource current, Set<String> changedFieldNames) {
        Compute client = createComputeClient();

        try {
            if (changedFieldNames.contains("enable-flow-logs")) {
                Subnetwork subnetwork = client.subnetworks().get(getProjectId(), getRegion(), getName()).execute();
                subnetwork.setEnableFlowLogs(getEnableFlowLogs());
                Operation operation = client.subnetworks().patch(getProjectId(), getRegion(), getName(), subnetwork).execute();
                waitForCompletion(client, operation);
            }

            if (changedFieldNames.contains("private-ip-google-access")) {
                SubnetworksSetPrivateIpGoogleAccessRequest flag = new SubnetworksSetPrivateIpGoogleAccessRequest();
                flag.setPrivateIpGoogleAccess(getPrivateIpGoogleAccess());
                Operation operation = client.subnetworks().setPrivateIpGoogleAccess(getProjectId(), getRegion(), getName(), flag).execute();
                waitForCompletion(client, operation);
            }
        } catch (GyroException ge) {
            throw ge;
        } catch (Exception ex) {
            throw new GyroException(ex.getMessage(), ex.getCause());
        }
    }

    @Override
    public void delete(GyroUI ui, State state) {
        Compute client = createComputeClient();

        try {
            Operation operation = client.subnetworks().delete(getProjectId(), getRegion(), getName()).execute();
            waitForCompletion(client, operation);
        } catch (GyroException ge) {
            throw ge;
        } catch (Exception ex) {
            throw new GyroException(ex.getMessage(), ex.getCause());
        }
    }
}
