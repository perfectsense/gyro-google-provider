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

package gyro.google.sqladmin;

import java.util.ArrayList;
import java.util.List;

import gyro.core.resource.Diffable;
import gyro.core.validation.ValidStrings;
import gyro.google.Copyable;

public class IpConfiguration extends Diffable
    implements Copyable<com.google.api.services.sqladmin.model.IpConfiguration> {

    private String allocatedIpRange;

    private List<AclEntry> authorizedNetwork;

    private Boolean enablePrivatePathForGoogleCloudServices;

    private Boolean ipv4Enabled;

    private String privateNetwork;

    private PscConfig pscConfig;

    private Boolean requireSsl;

    private String sslMode;

    /**
     * The name of the allocated ip range for the private ip Cloud SQL instance. For example: "google-managed-services-default". If set, the instance ip will be created in the allocated range. The range name must comply with [RFC 1035](https://tools.ietf.org/html/rfc1035). Specifically, the name must be 1-63 characters long and match the regular expression `[a-z]([-a-z0-9]*[a-z0-9])?.`
     */
    public String getAllocatedIpRange() {
        return allocatedIpRange;
    }

    public void setAllocatedIpRange(String allocatedIpRange) {
        this.allocatedIpRange = allocatedIpRange;
    }

    /**
     * The list of external networks that are allowed to connect to the instance using the IP.
     * In 'CIDR' notation, also known as 'slash' notation (for example: `157.197.200.0/24`).
     *
     * @subresource gyro.google.sqladmin.base.AclEntry
     */
    public List<AclEntry> getAuthorizedNetwork() {
        if (authorizedNetwork == null) {
            authorizedNetwork = new ArrayList<>();
        }

        return authorizedNetwork;
    }

    public void setAuthorizedNetwork(List<AclEntry> authorizedNetwork) {
        this.authorizedNetwork = authorizedNetwork;
    }

    /**
     * Controls connectivity to private IP instances from Google services, such as BigQuery.
     */
    public Boolean getEnablePrivatePathForGoogleCloudServices() {
        return enablePrivatePathForGoogleCloudServices;
    }

    public void setEnablePrivatePathForGoogleCloudServices(
        Boolean enablePrivatePathForGoogleCloudServices) {
        this.enablePrivatePathForGoogleCloudServices = enablePrivatePathForGoogleCloudServices;
    }

    /**
     * Whether the instance is assigned a public IP address or not.
     */
    public Boolean getIpv4Enabled() {
        return ipv4Enabled;
    }

    public void setIpv4Enabled(Boolean ipv4Enabled) {
        this.ipv4Enabled = ipv4Enabled;
    }

    /**
     * The resource link for the VPC network from which the Cloud SQL instance is accessible for private IP. For example, `/projects/myProject/global/networks/default`. This setting can be updated, but it cannot be removed after it is set.
     */
    public String getPrivateNetwork() {
        return privateNetwork;
    }

    public void setPrivateNetwork(String privateNetwork) {
        this.privateNetwork = privateNetwork;
    }

    /**
     * PSC settings for this instance.
     *
     * @subresource gyro.google.sqladmin.base.PscConfig
     */
    public PscConfig getPscConfig() {
        return pscConfig;
    }

    public void setPscConfig(PscConfig pscConfig) {
        this.pscConfig = pscConfig;
    }

    /**
     * Use `ssl_mode` instead. Whether SSL/TLS connections over IP are enforced. If set to false, then allow both non-SSL/non-TLS and SSL/TLS connections. For SSL/TLS connections, the client certificate won't be verified. If set to true, then only allow connections encrypted with SSL/TLS and with valid client certificates. If you want to enforce SSL/TLS without enforcing the requirement for valid client certificates, then use the `ssl_mode` flag instead of the `require_ssl` flag.
     */
    public Boolean getRequireSsl() {
        return requireSsl;
    }

    public void setRequireSsl(Boolean requireSsl) {
        this.requireSsl = requireSsl;
    }

    /**
     * Specify how SSL/TLS is enforced in database connections. If you must use the `require_ssl` flag for backward compatibility, then only the following value pairs are valid: For PostgreSQL and MySQL: * `ssl_mode=ALLOW_UNENCRYPTED_AND_ENCRYPTED` and `require_ssl=false` * `ssl_mode=ENCRYPTED_ONLY` and `require_ssl=false` * `ssl_mode=TRUSTED_CLIENT_CERTIFICATE_REQUIRED` and `require_ssl=true` For SQL Server: * `ssl_mode=ALLOW_UNENCRYPTED_AND_ENCRYPTED` and `require_ssl=false` * `ssl_mode=ENCRYPTED_ONLY` and `require_ssl=true` The value of `ssl_mode` has priority over the value of `require_ssl`. For example, for the pair `ssl_mode=ENCRYPTED_ONLY` and `require_ssl=false`, `ssl_mode=ENCRYPTED_ONLY` means accept only SSL connections, while `require_ssl=false` means accept both non-SSL and SSL connections. In this case, MySQL and PostgreSQL databases respect `ssl_mode` and accepts only SSL connections.
     */
    @ValidStrings({
        "SSL_MODE_UNSPECIFIED",
        "ALLOW_UNENCRYPTED_AND_ENCRYPTED",
        "ENCRYPTED_ONLY",
        "TRUSTED_CLIENT_CERTIFICATE_REQUIRED"
    })
    public String getSslMode() {
        return sslMode;
    }

    public void setSslMode(String sslMode) {
        this.sslMode = sslMode;
    }

    @Override
    public String primaryKey() {
        return "";
    }

    @Override
    public void copyFrom(com.google.api.services.sqladmin.model.IpConfiguration model) {
        setAllocatedIpRange(model.getAllocatedIpRange());
        setEnablePrivatePathForGoogleCloudServices(model.getEnablePrivatePathForGoogleCloudServices());
        setIpv4Enabled(model.getIpv4Enabled());
        setPrivateNetwork(model.getPrivateNetwork());
        setRequireSsl(model.getRequireSsl());
        setSslMode(model.getSslMode());

        getAuthorizedNetwork().clear();
        if (model.getAuthorizedNetworks() != null) {
            for (com.google.api.services.sqladmin.model.AclEntry aclEntry : model.getAuthorizedNetworks()) {
                AclEntry entry = newSubresource(AclEntry.class);
                entry.copyFrom(aclEntry);
                getAuthorizedNetwork().add(entry);
            }
        }

        if (model.getPscConfig() != null) {
            PscConfig config = newSubresource(PscConfig.class);
            config.copyFrom(model.getPscConfig());
            setPscConfig(config);
        }
    }

    com.google.api.services.sqladmin.model.IpConfiguration copyTo() {
        com.google.api.services.sqladmin.model.IpConfiguration ipConfiguration = new com.google.api.services.sqladmin.model.IpConfiguration();
        ipConfiguration.setAllocatedIpRange(getAllocatedIpRange());
        ipConfiguration.setEnablePrivatePathForGoogleCloudServices(getEnablePrivatePathForGoogleCloudServices());
        ipConfiguration.setIpv4Enabled(getIpv4Enabled());
        ipConfiguration.setPrivateNetwork(getPrivateNetwork());
        ipConfiguration.setRequireSsl(getRequireSsl());
        ipConfiguration.setSslMode(getSslMode());

        ipConfiguration.setAuthorizedNetworks(null);
        if (!getAuthorizedNetwork().isEmpty()) {
            List<com.google.api.services.sqladmin.model.AclEntry> aclEntries = new ArrayList<>();
            for (AclEntry entry : getAuthorizedNetwork()) {
                aclEntries.add(entry.copyTo());
            }

            ipConfiguration.setAuthorizedNetworks(aclEntries);
        }

        if (getPscConfig() != null) {
            ipConfiguration.setPscConfig(getPscConfig().copyTo());
        }

        return ipConfiguration;
    }
}
