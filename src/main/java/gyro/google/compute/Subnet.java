package gyro.google.compute;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.compute.Compute;
import com.google.cloud.compute.v1.NetworkClient;
import com.google.cloud.compute.v1.Operation;
import com.google.cloud.compute.v1.ProjectGlobalNetworkName;
import com.google.cloud.compute.v1.ProjectRegionName;
import com.google.cloud.compute.v1.ProjectRegionSubnetworkName;
import com.google.cloud.compute.v1.Subnetwork;
import com.google.cloud.compute.v1.SubnetworkClient;
import com.google.cloud.compute.v1.SubnetworkSettings;
import com.psddev.dari.util.ObjectUtils;
import gyro.core.resource.Resource;
import gyro.core.resource.ResourceDiffProperty;
import gyro.core.resource.ResourceName;
import gyro.google.GoogleResource;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Set;

@ResourceName("subnet")
public class Subnet extends GoogleResource {
    private String subnetName;
    private String description;
    private String ipCidrRange;
    private String networkId;
    private String region;

    // Read-only
    private String subnetworkId;

    public String getSubnetName() {
        return subnetName;
    }

    public void setSubnetName(String subnetName) {
        this.subnetName = subnetName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @ResourceDiffProperty(updatable = true)
    public String getIpCidrRange() {
        return ipCidrRange;
    }

    public void setIpCidrRange(String ipCidrRange) {
        this.ipCidrRange = ipCidrRange;
    }

    public String getNetworkId() {
        return networkId;
    }

    public void setNetworkId(String networkId) {
        this.networkId = networkId;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getSubnetworkId() {
        return subnetworkId;
    }

    public void setSubnetworkId(String subnetworkId) {
        this.subnetworkId = subnetworkId;
    }

    @Override
    public boolean refresh() {
        SubnetworkClient client = creatClient(SubnetworkClient.class);

        Subnetwork subnetwork = client.getSubnetwork(ProjectRegionSubnetworkName.of(getProjectId(), getRegion(), getSubnetName()));

        setSubnetworkId(subnetwork.getId());
        setDescription(subnetwork.getDescription());
        setIpCidrRange(subnetwork.getIpCidrRange());

        return true;
    }

    @Override
    public void create() {
        try {
            Compute computeService = createComputeService();
            com.google.api.services.compute.model.Subnetwork subnetwork = new com.google.api.services.compute.model.Subnetwork();
            subnetwork.setName(getSubnetName());
            subnetwork.setNetwork(ProjectGlobalNetworkName.format(getNetworkId(), getProjectId()));
            subnetwork.setDescription(getDescription());
            subnetwork.setIpCidrRange(getIpCidrRange());
            com.google.api.services.compute.model.Operation execute = computeService.subnetworks().insert(getProjectId(), getRegion(), subnetwork).execute();
            refresh();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }

        /*SubnetworkClient client = creatClient(SubnetworkClient.class);

        Subnetwork subnetwork = Subnetwork.newBuilder()
            .setName(getSubnetName())
            .setNetwork(ProjectGlobalNetworkName.format(getNetworkId(), getProjectId()))
            .setDescription(getDescription())
            .setIpCidrRange(getIpCidrRange())
            .build();

        Operation operation = client.insertSubnetwork(ProjectRegionName.format(getProjectId(), getRegion()), subnetwork);

        setSubnetworkId(operation.getId());*/
    }

    @Override
    public void update(Resource current, Set<String> changedProperties) {
        SubnetworkClient client = creatClient(SubnetworkClient.class);

        Subnetwork subnetwork = client.getSubnetwork(ProjectRegionSubnetworkName.of(getProjectId(), getRegion(), getSubnetName()));

        Subnetwork subnetwork1 = subnetwork.toBuilder().setPrivateIpGoogleAccess(true).build();

        /*client.patchSubnetwork(ProjectRegionSubnetworkName.of(getProjectId(), getRegion(), getSubnetName()),
            subnetwork1, Collections.emptyList());*/



        try {
            Compute computeService = createComputeService();
            com.google.api.services.compute.model.Subnetwork subnetwork2 = computeService.subnetworks().get(getProjectId(), getRegion(), getSubnetName()).execute();
            subnetwork2.setPrivateIpGoogleAccess(true);
            computeService.subnetworks().patch(getProjectId(), getRegion(), getSubnetName(), subnetwork2).execute();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void delete() {
        SubnetworkClient client = creatClient(SubnetworkClient.class);

        client.deleteSubnetwork(ProjectRegionSubnetworkName.of(getProjectId(), getRegion(), getSubnetName()));
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

    private Compute createComputeService() throws IOException, GeneralSecurityException {
        HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();

        GoogleCredential credential = GoogleCredential.fromStream(
            new FileInputStream("/Users/dbhattacharyya/Downloads/gyro-sandbox-google-cred.json")
        ).createScoped(Collections.singleton("https://www.googleapis.com/auth/cloud-platform"));

        return new Compute.Builder(httpTransport, jsonFactory, credential)
            .setApplicationName("Google-ComputeSample/0.1")
            .build();
    }
}
