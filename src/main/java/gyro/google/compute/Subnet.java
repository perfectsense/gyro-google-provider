package gyro.google.compute;

import com.google.api.services.compute.Compute;
import com.google.api.services.compute.model.Operation;
import com.google.api.services.compute.model.Subnetwork;
import com.google.api.services.compute.model.SubnetworksSetPrivateIpGoogleAccessRequest;
import com.google.cloud.compute.v1.ProjectGlobalNetworkName;
import com.psddev.dari.util.ObjectUtils;
import gyro.core.GyroException;
import gyro.core.resource.Resource;
import gyro.core.resource.Updatable;
import gyro.core.Type;
import gyro.core.resource.Output;
import gyro.core.scope.State;

import java.io.IOException;
import java.util.Set;

/**
 * Creates a subnet.
 *
 * Example
 * -------
 *
 * .. code-block:: gyro
 *
 *     google::subnet subnet-example
 *         subnet-name: "subnet-example"
 *         description: "subnet-example-description"
 *         ip-cidr-range: "10.0.0.0/16"
 *         network-name: $(google::network network-example-subnet | network-name)
 *         region: "us-east1"
 *     end
 */
@Type("subnet")
public class Subnet extends ComputeResource {
    private String subnetName;
    private String description;
    private String ipCidrRange;
    private String networkName;
    private String region;
    private Boolean enableFlowLogs;
    private Boolean privateIpGoogleAccess;

    // Read-only
    private String subnetworkId;

    /**
     * The name of the subnet. (Required)
     */
    public String getSubnetName() {
        return subnetName;
    }

    public void setSubnetName(String subnetName) {
        this.subnetName = subnetName;
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
    public String getIpCidrRange() {
        return ipCidrRange;
    }

    public void setIpCidrRange(String ipCidrRange) {
        this.ipCidrRange = ipCidrRange;
    }

    /**
     * The vpc network name under which the subnet will reside. (Required)
     */
    public String getNetworkName() {
        return networkName;
    }

    public void setNetworkName(String networkName) {
        this.networkName = networkName;
    }

    /**
     * The region under which the subnet will reside. (Required)
     */
    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    /**
     * Enable/Disable flow logs. Defaults to disabled.
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
     * Enable/Disable private ip google access. Defaults to disabled.
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
    public String getSubnetworkId() {
        return subnetworkId;
    }

    public void setSubnetworkId(String subnetworkId) {
        this.subnetworkId = subnetworkId;
    }

    @Override
    public boolean refresh() {
        Compute client = creatClient(Compute.class);

        try {
            Subnetwork subnetwork = client.subnetworks().get(getProjectId(), getRegion(), getSubnetName()).execute();
            setSubnetworkId(subnetwork.getId().toString());
            setDescription(subnetwork.getDescription());
            setIpCidrRange(subnetwork.getIpCidrRange());
            setEnableFlowLogs(subnetwork.getEnableFlowLogs());
            setPrivateIpGoogleAccess(subnetwork.getPrivateIpGoogleAccess());
        } catch (IOException ex) {
            return false;
        }

        return true;
    }

    @Override
    public void create(State state) {
        Compute client = creatClient(Compute.class);

        Subnetwork subnetwork = new Subnetwork();
        subnetwork.setName(getSubnetName());
        subnetwork.setNetwork(ProjectGlobalNetworkName.format(getNetworkName(), getProjectId()));
        subnetwork.setDescription(getDescription());
        subnetwork.setIpCidrRange(getIpCidrRange());
        subnetwork.setEnableFlowLogs(getEnableFlowLogs());
        subnetwork.setPrivateIpGoogleAccess(getPrivateIpGoogleAccess());

        try {
            Compute.Subnetworks.Insert insert = client.subnetworks().insert(getProjectId(), getRegion(), subnetwork);
            Operation operation = insert.execute();
            Operation.Error error = waitForCompletion(client, operation);
            if (error != null) {
                throw new GyroException(error.toPrettyString());
            }

            refresh();
        } catch (Exception ex) {
            throw new GyroException(ex.getMessage(), ex.getCause());
        }
    }

    @Override
    public void update(State state, Resource current, Set<String> changedFieldNames) {
        Compute client = creatClient(Compute.class);

        try {

            if (changedFieldNames.contains("enable-flow-logs")) {
                Subnetwork subnetwork = client.subnetworks().get(getProjectId(), getRegion(), getSubnetName()).execute();
                subnetwork.setEnableFlowLogs(getEnableFlowLogs());
                client.subnetworks().patch(getProjectId(), getRegion(), getSubnetName(), subnetwork).execute();
            }

            if (changedFieldNames.contains("private-ip-google-access")) {
                SubnetworksSetPrivateIpGoogleAccessRequest flag = new SubnetworksSetPrivateIpGoogleAccessRequest();
                flag.setPrivateIpGoogleAccess(getPrivateIpGoogleAccess());
                client.subnetworks().setPrivateIpGoogleAccess(getProjectId(), getRegion(), getSubnetName(), flag).execute();
            }
        } catch (IOException ex) {
            throw new GyroException(ex.getMessage(), ex.getCause());
        }
    }

    @Override
    public void delete(State state) {
        Compute client = creatClient(Compute.class);

        try {
            client.subnetworks().delete(getProjectId(), getRegion(), getSubnetName()).execute();

            // Wait till its actually deleted.
            // Network doesn't get deleted just sits.
            try {
                Thread.sleep(20000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } catch (IOException ex) {
            throw new GyroException(ex.getMessage(), ex.getCause());
        }
    }

    @Override
    public String toDisplayString() {
        StringBuilder sb = new StringBuilder();

        sb.append("subnet");

        if (!ObjectUtils.isBlank(getSubnetName())) {
            sb.append(" ( ").append(getSubnetName()).append(" )");
        }

        if (!ObjectUtils.isBlank(getSubnetworkId())) {
            sb.append(" - ").append(getSubnetworkId());
        }

        return sb.toString();
    }
}
