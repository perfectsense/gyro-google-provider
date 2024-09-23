/*
 * Copyright 2024, Brightspot.
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

package gyro.google.cloudsql;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.google.api.services.sqladmin.model.IpConfiguration;
import gyro.core.resource.Diffable;
import gyro.core.resource.Updatable;
import gyro.core.validation.ValidStrings;
import gyro.google.Copyable;
import gyro.google.compute.NetworkResource;

public class DbIpConfiguration extends Diffable implements Copyable<IpConfiguration> {

    private String allocatedIpRange;
    private List<DbAclEntry> authorizedNetworks;
    private Boolean enablePrivatePathForGoogleCloudServices;
    private Boolean ipv4Enabled;
    private NetworkResource privateNetwork;
    private DbPscConfig pscConfig;
    private String serverCaMode;
    private String sslMode;

    /**
     * The name of the allocated ip range for the private ip Cloud SQL instance.
     */
    @Updatable
    public String getAllocatedIpRange() {
        return allocatedIpRange;
    }

    public void setAllocatedIpRange(String allocatedIpRange) {
        this.allocatedIpRange = allocatedIpRange;
    }

    /**
     * The list of external networks that are allowed to connect to the instance using the IP.
     */
    @Updatable
    public List<DbAclEntry> getAuthorizedNetworks() {
        if (authorizedNetworks == null) {
            authorizedNetworks = new ArrayList<>();
        }

        return authorizedNetworks;
    }

    public void setAuthorizedNetworks(List<DbAclEntry> authorizedNetworks) {
        this.authorizedNetworks = authorizedNetworks;
    }

    /**
     * When set to ``true``, allows connectivity to private IP instances from Google services, such as BigQuery.
     */
    @Updatable
    public Boolean getEnablePrivatePathForGoogleCloudServices() {
        return enablePrivatePathForGoogleCloudServices;
    }

    public void setEnablePrivatePathForGoogleCloudServices(Boolean enablePrivatePathForGoogleCloudServices) {
        this.enablePrivatePathForGoogleCloudServices = enablePrivatePathForGoogleCloudServices;
    }

    /**
     * When set to ``true``, the instance is assigned a public IP address.
     */
    @Updatable
    public Boolean getIpv4Enabled() {
        return ipv4Enabled;
    }

    public void setIpv4Enabled(Boolean ipv4Enabled) {
        this.ipv4Enabled = ipv4Enabled;
    }

    /**
     * The resource link for the VPC network from which the Cloud SQL instance is accessible for private IP.
     */
    @Updatable
    public NetworkResource getPrivateNetwork() {
        return privateNetwork;
    }

    public void setPrivateNetwork(NetworkResource privateNetwork) {
        this.privateNetwork = privateNetwork;
    }

    /**
     * The PSC settings for this instance.
     */
    @Updatable
    public DbPscConfig getPscConfig() {
        return pscConfig;
    }

    public void setPscConfig(DbPscConfig pscConfig) {
        this.pscConfig = pscConfig;
    }

    /**
     * The type of CA used for the server certificate.
     */
    @Updatable
    public String getServerCaMode() {
        return serverCaMode;
    }

    public void setServerCaMode(String serverCaMode) {
        this.serverCaMode = serverCaMode;
    }

    /**
     * The SSL/TLS is enforced in database connections
     */
    @Updatable
    @ValidStrings({ "ALLOW_UNENCRYPTED_AND_ENCRYPTED", "ENCRYPTED_ONLY", "TRUSTED_CLIENT_CERTIFICATE_REQUIRED" })
    public String getSslMode() {
        return sslMode;
    }

    public void setSslMode(String sslMode) {
        this.sslMode = sslMode;
    }

    @Override
    public void copyFrom(IpConfiguration model) throws Exception {
        setAllocatedIpRange(model.getAllocatedIpRange());
        setEnablePrivatePathForGoogleCloudServices(model.getEnablePrivatePathForGoogleCloudServices());
        setIpv4Enabled(model.getIpv4Enabled());
        setServerCaMode(model.getServerCaMode());
        setSslMode(model.getSslMode());
        setPrivateNetwork(findById(NetworkResource.class, model.getPrivateNetwork()));

        getAuthorizedNetworks().clear();
        if (model.getAuthorizedNetworks() != null) {
            getAuthorizedNetworks().addAll(model.getAuthorizedNetworks().stream().map(n -> {
                DbAclEntry entry = new DbAclEntry();
                entry.copyFrom(n);
                return entry;
            }).collect(Collectors.toList()));
        }

        setPscConfig(null);
        if (model.getPscConfig() != null) {
            DbPscConfig config = new DbPscConfig();
            config.copyFrom(model.getPscConfig());
            setPscConfig(config);
        }
    }

    @Override
    public String primaryKey() {
        return "";
    }

    public IpConfiguration toIpConfiguration() {
        IpConfiguration config = new IpConfiguration();

        if (getAllocatedIpRange() != null) {
            config.setAllocatedIpRange(getAllocatedIpRange());
        }

        if (getAuthorizedNetworks() != null) {
            config.setAuthorizedNetworks(getAuthorizedNetworks().stream()
                .map(DbAclEntry::toAclEntry)
                .collect(Collectors.toList()));
        }

        if (getEnablePrivatePathForGoogleCloudServices() != null) {
            config.setEnablePrivatePathForGoogleCloudServices(getEnablePrivatePathForGoogleCloudServices());
        }

        if (getIpv4Enabled() != null) {
            config.setIpv4Enabled(getIpv4Enabled());
        }

        if (getPrivateNetwork() != null) {
            config.setPrivateNetwork(getPrivateNetwork().getSelfLink());
        }

        if (getPscConfig() != null) {
            config.setPscConfig(getPscConfig().toPscConfig());
        }

        if (getServerCaMode() != null) {
            config.setServerCaMode(getServerCaMode());
        }

        if (getSslMode() != null) {
            config.setSslMode(getSslMode());
        }

        return config;
    }
}
