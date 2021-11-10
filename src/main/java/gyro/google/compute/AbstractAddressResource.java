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

import java.util.Set;

import com.google.cloud.compute.v1.Address;
import gyro.core.GyroUI;
import gyro.core.resource.Id;
import gyro.core.resource.Output;
import gyro.core.resource.Resource;
import gyro.core.scope.State;
import gyro.core.validation.Regex;
import gyro.core.validation.Required;
import gyro.core.validation.ValidStrings;
import gyro.google.Copyable;

public abstract class AbstractAddressResource extends ComputeResource implements Copyable<Address> {

    private String name;
    private String description;
    private String address;
    private Integer prefixLength;
    private Address.AddressType addressType;
    private Address.Purpose purpose;
    private SubnetworkResource subnetwork;
    private NetworkResource network;
    private Address.Status status;
    private String selfLink;

    /**
     * Name for the resource. See `Fields <https://cloud.google.com/compute/docs/reference/rest/v1/addresses#Address.FIELDS-table/>`_ for formatting requirements.
     */
    @Regex("[a-z]([-a-z0-9]*[a-z0-9])?")
    @Required
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * An optional description of the address.
     */
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * An optional static IP address to set.
     */
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * The prefix length if the resource represents an IP range.
     */
    public Integer getPrefixLength() {
        return prefixLength;
    }

    public void setPrefixLength(Integer prefixLength) {
        this.prefixLength = prefixLength;
    }

    /**
     * Type of address to reserve. Defaults to ``EXTERNAL``.
     */
    @ValidStrings({ "EXTERNAL", "INTERNAL" })
    public Address.AddressType getAddressType() {
        return addressType;
    }

    public void setAddressType(Address.AddressType addressType) {
        this.addressType = addressType;
    }

    /**
     * Purpose for this resource. This field can only be used with ``INTERNAL`` type.
     */
    @ValidStrings({ "GCE_ENDPOINT", "DNS_RESOLVER", "VPC_PEERING", "NAT_AUTO" })
    public Address.Purpose getPurpose() {
        return purpose;
    }

    public void setPurpose(Address.Purpose purpose) {
        this.purpose = purpose;
    }

    /**
     * URL of the subnetwork in which to reserve the address. If an IP address is specified, it must be wihin the subnetwork's IP range. This field can only be used with ``INTERNAL`` type with a ``GCE_ENDPOINT`` or ``DNS_RESOLVER`` purpose.
     */
    public SubnetworkResource getSubnetwork() {
        return subnetwork;
    }

    public void setSubnetwork(SubnetworkResource subnetwork) {
        this.subnetwork = subnetwork;
    }

    /**
     * The URL of the network in which to reserve the address. This field can only be used with ``INTERNAL`` type with the ``VPC_PEERING`` purpose.
     */
    public NetworkResource getNetwork() {
        return network;
    }

    public void setNetwork(NetworkResource network) {
        this.network = network;
    }

    /**
     * The status of the address.
     */
    @ValidStrings({ "RESERVING", "RESERVED", "IN_USE" })
    @Output
    public Address.Status getStatus() {
        return status;
    }

    public void setStatus(Address.Status status) {
        this.status = status;
    }

    /**
     * GCP server-defined URL for the address.
     */
    @Id
    @Output
    public String getSelfLink() {
        return selfLink;
    }

    public void setSelfLink(String selfLink) {
        this.selfLink = selfLink;
    }

    @Override
    public void doUpdate(GyroUI ui, State state, Resource current, Set<String> changedFieldNames) throws Exception {
        // Do nothing, API doesn't support with an update and/or patch method.
    }

    @Override
    public void copyFrom(Address model) {
        setName(model.getName());
        setDescription(model.getDescription());
        setAddress(model.getAddress());
        setPrefixLength(model.getPrefixLength());
        setAddressType(model.getAddressType());
        setPurpose(model.getPurpose());

        setSubnetwork(null);
        if ((model.getSubnetwork() != null) && !model.getSubnetwork().endsWith("default")) {
            setSubnetwork(findById(SubnetworkResource.class, model.getSubnetwork()));
        }

        setNetwork(null);
        if ((model.getNetwork() != null) && !model.getNetwork().endsWith("default")) {
            setNetwork(findById(NetworkResource.class, model.getNetwork()));
        }

        setStatus(model.getStatus());
        setSelfLink(model.getSelfLink());
    }

    public Address copyTo() {
        Address.Builder builder = Address.newBuilder().setName(getName());

        if (getDescription() != null) {
            builder.setDescription(getDescription());
        }

        if (getPrefixLength() != null) {
            builder.setPrefixLength(getPrefixLength());
        }

        if (getPurpose() != null) {
            builder.setPurpose(getPurpose());
        }

        if (getSubnetwork() != null) {
            builder.setSubnetwork(getSubnetwork().getSelfLink());
        }

        if (getNetwork() != null) {
            builder.setNetwork(getNetwork().getSelfLink());
        }

        if (getAddress() != null) {
            builder.setAddress(getAddress());
        }

        if (getAddressType() != null) {
            builder.setAddressType(getAddressType());
        }

        return builder.build();
    }
}

