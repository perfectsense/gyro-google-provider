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

import com.google.api.services.compute.model.Address;
import gyro.core.GyroUI;
import gyro.core.resource.Id;
import gyro.core.resource.Output;
import gyro.core.resource.Resource;
import gyro.core.resource.Updatable;
import gyro.core.scope.State;
import gyro.core.validation.Regex;
import gyro.core.validation.Required;
import gyro.core.validation.ValidStrings;
import gyro.google.Copyable;

import java.util.Set;

public abstract class AbstractAddressResource extends ComputeResource implements Copyable<Address> {

    private String id;
    private String name;
    private String description;
    private String address;
    private Integer prefixLength;
    private String addressType;
    private String purpose;
    private SubnetworkResource subnetwork;
    private NetworkResource network;
    private String status;
    private String selfLink;

    /**
     * Internal Google id.
     */
    @Output
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    /**
     * Name for the resource. See `Fields <https://cloud.google.com/compute/docs/reference/rest/v1/addresses#Address.FIELDS-table/>`_ for formatting requirements.
     */
    @Id
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
    @Updatable
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * An optional static IP address to set.
     */
    @Updatable
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * The prefix length if the resource represents an IP range.
     */
    @Updatable
    public Integer getPrefixLength() {
        return prefixLength;
    }

    public void setPrefixLength(Integer prefixLength) {
        this.prefixLength = prefixLength;
    }
    
    /**
     * Type of address to reserve. Valid values are "INTERNAL" or "EXTERNAL". Defaults to "EXTERNAL".
     */
    @Updatable
    @ValidStrings({"EXTERNAL", "INTERNAL"})
    public String getAddressType() {
        return addressType;
    }

    public void setAddressType(String addressType) {
        this.addressType = addressType;
    }

    /**
     * Purpose for this resource. Valid values are ``GCE_ENDPOINT``, ``DNS_RESOLVER``, ``VPC_PEERING`` or ``NAT_AUTO``.
     */
    @Updatable
    @ValidStrings({"GCE_ENDPOINT", "DNS_RESOLVER", "VPC_PEERING", "NAT_AUTO"})
    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    /**
     * URL of the subnetwork in which to reserve the address. If an IP address is specified, it must be wihin the subnetwork's IP range. This field can only be used with ``INTERNAL`` type with a ``GCE_ENDPOINT`` or ``DNS_RESOLVER`` purpose.
     */
    @Updatable
    public SubnetworkResource getSubnetwork() {
        return subnetwork;
    }

    public void setSubnetwork(SubnetworkResource subnetwork) {
        this.subnetwork = subnetwork;
    }

    /**
     * The URL of the network in which to reserve the address. This field can only be used with ``INTERNAL`` type with the ``VPC_PEERING`` purpose.
     */
    @Updatable
    public NetworkResource getNetwork() {
        return network;
    }

    public void setNetwork(NetworkResource network) {
        this.network = network;
    }

    /**
     * The status of the address. Values are ``RESERVING``, ``RESERVED`` or ``IN_USE``.
     */
    @Output
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * GCP server-defined URL for the address.
     */
    @Output
    public String getSelfLink() {
        return selfLink;
    }

    public void setSelfLink(String selfLink) {
        this.selfLink = selfLink;
    }

    @Override
    public void update(GyroUI ui, State state, Resource current, Set<String> changedFieldNames) throws Exception {
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
        setSubnetwork(model.getSubnetwork() == null ? null : findById(SubnetworkResource.class, model.getSubnetwork().substring(model.getSubnetwork().lastIndexOf('/') + 1)));
        setNetwork(model.getNetwork() == null ? null : findById(NetworkResource.class, model.getNetwork().substring(model.getNetwork().lastIndexOf('/') + 1)));
        setStatus(model.getStatus());
        setSelfLink(model.getSelfLink());
    }

    public Address copyTo() {
        return new Address()
                .setName(getName())
                .setDescription(getDescription())
                .setAddress(getAddress())
                .setPrefixLength(getPrefixLength())
                .setAddressType(getAddressType())
                .setPurpose(getPurpose())
                .setSubnetwork(getSubnetwork() != null ? getSubnetwork().getSelfLink() : null)
                .setNetwork(getNetwork() != null ? getNetwork().getSelfLink() : null);
    }
}

