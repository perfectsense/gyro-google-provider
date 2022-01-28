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

import com.google.cloud.compute.v1.AccessConfig;
import gyro.core.resource.Diffable;
import gyro.core.validation.ValidStrings;
import gyro.google.Copyable;

public class InstanceAccessConfig extends Diffable implements Copyable<AccessConfig> {

    private String name;
    private String natIp;
    private String networkTier;
    private String publicPtrDomainName;
    private Boolean setPublicPtr;
    private String type;

    /**
     * Name of this access configuration. Default and recommended name is ``External NAT``, but can be any arbitrary string.
     */
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * An unused static external IP address available to the project. Leave this field undefined to use an IP from a shared ephemeral IP address pool. If you specify a static external IP address, it must live in the same region as the zone of the instance.
     */
    public String getNatIp() {
        return natIp;
    }

    public void setNatIp(String natIp) {
        this.natIp = natIp;
    }

    /**
     * Signifies the networking tier used for configuring this access configuration. If specified without a valid external IP address, an ephemeral IP will be created with this networkTier. If a valid external IP address is specified, it must match that of the networkTier associated with the Address resource owning that IP.
     */
    @ValidStrings({ "PREMIUM", "STANDARD" })
    public String getNetworkTier() {
        return networkTier;
    }

    public void setNetworkTier(String networkTier) {
        this.networkTier = networkTier;
    }

    /**
     * The DNS domain name for the public PTR record. Can only be set if the ``setPublicPtr`` field is set to ``true``.
     */
    public String getPublicPtrDomainName() {
        return publicPtrDomainName;
    }

    public void setPublicPtrDomainName(String publicPtrDomainName) {
        this.publicPtrDomainName = publicPtrDomainName;
    }

    /**
     * if ``true`` a public DNS 'PTR' record should be created to map the external IP address of the instance to a DNS domain name.
     */
    public Boolean getSetPublicPtr() {
        return setPublicPtr;
    }

    public void setSetPublicPtr(Boolean setPublicPtr) {
        this.setPublicPtr = setPublicPtr;
    }

    /**
     * The type of configuration. Defaults to ``ONE_TO_ONE_NAT``.
     */
    @ValidStrings("ONE_TO_ONE_NAT")
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String primaryKey() {
        return "";
    }

    @Override
    public void copyFrom(AccessConfig model) {
        setName(model.getName());
        setNatIp(model.getNatIP());
        setNetworkTier(model.getNetworkTier().toString());
        setPublicPtrDomainName(model.getPublicPtrDomainName());
        setSetPublicPtr(model.getSetPublicPtr());
        setType(model.getType().toString());
    }

    public AccessConfig copyTo() {
        AccessConfig.Builder builder = AccessConfig.newBuilder();

        if (getName() != null) {
            builder.setName(getName());
        }

        if (getNatIp() != null) {
            builder.setNatIP(getNatIp());
        }

        if (getNetworkTier() != null) {
            builder.setNetworkTier(getNetworkTier());
        }

        if (getPublicPtrDomainName() != null) {
            builder.setPublicPtrDomainName(getPublicPtrDomainName());
        }

        if (getSetPublicPtr() != null) {
            builder.setSetPublicPtr(getSetPublicPtr());
        }

        if (getType() != null) {
            builder.setType(getType()).build();
        }

        return builder.build();
    }
}
