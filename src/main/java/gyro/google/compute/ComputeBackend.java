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

import java.util.Optional;

import com.google.api.services.compute.model.Backend;
import gyro.core.resource.Diffable;
import gyro.core.resource.DiffableType;
import gyro.core.validation.Required;
import gyro.google.Copyable;

public class ComputeBackend extends Diffable implements Copyable<Backend> {

    private String balancingMode;

    private Float capacityScaler;

    private String description;

    // TODO: This can be also RegionInstanceGroupResource
    private InstanceGroupResource group;

    private Integer maxConnections;

    private Integer maxConnectionsPerEndpoint;

    private Integer maxConnectionsPerInstance;

    private Integer maxRate;

    private Float maxRatePerEndpoint;

    private Float maxRatePerInstance;

    private Float maxUtilization;

    /**
     * Specifies the balancing mode for the backend.
     *
     * When choosing a balancing mode, you need to consider the loadBalancingScheme, and protocol for
     * the backend service, as well as the type of backend (instance group or NEG).
     *
     *   - If the load balancing mode is CONNECTION, then the load is spread based on how many
     * concurrent connections the backend can handle. You can use the CONNECTION balancing mode if the
     * protocol for the backend service is SSL, TCP, or UDP.
     *
     * If the loadBalancingScheme for the backend service is EXTERNAL (SSL Proxy and TCP Proxy load
     * balancers), you must also specify exactly one of the following parameters: maxConnections,
     * maxConnectionsPerInstance, or maxConnectionsPerEndpoint.
     *
     * If the loadBalancingScheme for the backend service is INTERNAL (internal TCP/UDP load
     * balancers), you cannot specify any additional parameters.   - If the load balancing mode is
     * RATE, the load is spread based on the rate of HTTP requests per second (RPS). You can use the
     * RATE balancing mode if the protocol for the backend service is HTTP or HTTPS. You must specify
     * exactly one of the following parameters: maxRate, maxRatePerInstance, or maxRatePerEndpoint.
     * - If the load balancing mode is UTILIZATION, the load is spread based on the CPU utilization of
     * instances in an instance group. You can use the UTILIZATION balancing mode if the
     * loadBalancingScheme of the backend service is EXTERNAL, INTERNAL_SELF_MANAGED, or
     * INTERNAL_MANAGED and the backends are instance groups. There are no restrictions on the backend
     * service protocol.
     */
    public String getBalancingMode() {
        return balancingMode;
    }

    public void setBalancingMode(String balancingMode) {
        this.balancingMode = balancingMode;
    }

    /**
     * A multiplier applied to the group's maximum servicing capacity (based on UTILIZATION, RATE or
     * CONNECTION). Default value is 1, which means the group will serve up to 100% of its configured
     * capacity (depending on balancingMode). A setting of 0 means the group is completely drained,
     * offering 0% of its available Capacity. Valid range is [0.0,1.0].
     *
     * This cannot be used for internal load balancing.
     */
    public Float getCapacityScaler() {
        return capacityScaler;
    }

    public void setCapacityScaler(Float capacityScaler) {
        this.capacityScaler = capacityScaler;
    }

    /**
     * An optional description of this resource. Provide this property when you create the resource.
     */
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * The fully-qualified URL of an instance group or network endpoint group (NEG) resource. The type
     * of backend that a backend service supports depends on the backend service's
     * loadBalancingScheme.
     *
     *   - When the loadBalancingScheme for the backend service is EXTERNAL, INTERNAL_SELF_MANAGED, or
     * INTERNAL_MANAGED, the backend can be either an instance group or a NEG. The backends on the
     * backend service must be either all instance groups or all NEGs. You cannot mix instance group
     * and NEG backends on the same backend service.
     *
     * - When the loadBalancingScheme for the backend service is INTERNAL, the backend must be an
     * instance group in the same region as the backend service. NEGs are not supported.
     *
     * You must use the fully-qualified URL (starting with https://www.googleapis.com/) to specify the
     * instance group or NEG. Partial URLs are not supported.
     */
    @Required
    public InstanceGroupResource getGroup() {
        return group;
    }

    public void setGroup(InstanceGroupResource group) {
        this.group = group;
    }

    /**
     * Defines a maximum target for simultaneous connections for the entire backend (instance group or
     * NEG). If the backend's balancingMode is UTILIZATION, this is an optional parameter. If the
     * backend's balancingMode is CONNECTION, and backend is attached to a backend service whose
     * loadBalancingScheme is EXTERNAL, you must specify either this parameter,
     * maxConnectionsPerInstance, or maxConnectionsPerEndpoint.
     *
     * Not available if the backend's balancingMode is RATE. If the loadBalancingScheme is INTERNAL,
     * then maxConnections is not supported, even though the backend requires a balancing mode of
     * CONNECTION.
     */
    public Integer getMaxConnections() {
        return maxConnections;
    }

    public void setMaxConnections(Integer maxConnections) {
        this.maxConnections = maxConnections;
    }

    /**
     * Defines a maximum target for simultaneous connections for an endpoint of a NEG. This is
     * multiplied by the number of endpoints in the NEG to implicitly calculate a maximum number of
     * target maximum simultaneous connections for the NEG. If the backend's balancingMode is
     * CONNECTION, and the backend is attached to a backend service whose loadBalancingScheme is
     * EXTERNAL, you must specify either this parameter, maxConnections, or maxConnectionsPerInstance.
     *
     * Not available if the backend's balancingMode is RATE. Internal TCP/UDP load balancing does not
     * support setting maxConnectionsPerEndpoint even though its backends require a balancing mode of
     * CONNECTION.
     */
    public Integer getMaxConnectionsPerEndpoint() {
        return maxConnectionsPerEndpoint;
    }

    public void setMaxConnectionsPerEndpoint(Integer maxConnectionsPerEndpoint) {
        this.maxConnectionsPerEndpoint = maxConnectionsPerEndpoint;
    }

    /**
     * Defines a maximum target for simultaneous connections for a single VM in a backend instance
     * group. This is multiplied by the number of instances in the instance group to implicitly
     * calculate a target maximum number of simultaneous connections for the whole instance group. If
     * the backend's balancingMode is UTILIZATION, this is an optional parameter. If the backend's
     * balancingMode is CONNECTION, and backend is attached to a backend service whose
     * loadBalancingScheme is EXTERNAL, you must specify either this parameter, maxConnections, or
     * maxConnectionsPerEndpoint.
     *
     * Not available if the backend's balancingMode is RATE. Internal TCP/UDP load balancing does not
     * support setting maxConnectionsPerInstance even though its backends require a balancing mode of
     * CONNECTION.
     */
    public Integer getMaxConnectionsPerInstance() {
        return maxConnectionsPerInstance;
    }

    public void setMaxConnectionsPerInstance(Integer maxConnectionsPerInstance) {
        this.maxConnectionsPerInstance = maxConnectionsPerInstance;
    }

    /**
     * The max requests per second (RPS) of the group. Can be used with either RATE or UTILIZATION
     * balancing modes, but required if RATE mode. For RATE mode, either maxRate or maxRatePerInstance
     * must be set.
     *
     * This cannot be used for internal load balancing.
     */
    public Integer getMaxRate() {
        return maxRate;
    }

    public void setMaxRate(Integer maxRate) {
        this.maxRate = maxRate;
    }

    /**
     * Defines a maximum target for requests per second (RPS) for an endpoint of a NEG. This is
     * multiplied by the number of endpoints in the NEG to implicitly calculate a target maximum rate
     * for the NEG.
     *
     * If the backend's balancingMode is RATE, you must specify either this parameter, maxRate, or
     * maxRatePerInstance.
     *
     * Not available if the backend's balancingMode is CONNECTION.
     */
    public Float getMaxRatePerEndpoint() {
        return maxRatePerEndpoint;
    }

    public void setMaxRatePerEndpoint(Float maxRatePerEndpoint) {
        this.maxRatePerEndpoint = maxRatePerEndpoint;
    }

    /**
     * Defines a maximum target for requests per second (RPS) for a single VM in a backend instance
     * group. This is multiplied by the number of instances in the instance group to implicitly
     * calculate a target maximum rate for the whole instance group.
     *
     * If the backend's balancingMode is UTILIZATION, this is an optional parameter. If the backend's
     * balancingMode is RATE, you must specify either this parameter, maxRate, or maxRatePerEndpoint.
     *
     * Not available if the backend's balancingMode is CONNECTION.
     */
    public Float getMaxRatePerInstance() {
        return maxRatePerInstance;
    }

    public void setMaxRatePerInstance(Float maxRatePerInstance) {
        this.maxRatePerInstance = maxRatePerInstance;
    }

    /**
     * Defines the maximum average CPU utilization of a backend VM in an instance group. The valid
     * range is [0.0, 1.0]. This is an optional parameter if the backend's balancingMode is
     * UTILIZATION.
     *
     * This parameter can be used in conjunction with maxRate, maxRatePerInstance, maxConnections, or
     * maxConnectionsPerInstance.
     */
    public Float getMaxUtilization() {
        return maxUtilization;
    }

    public void setMaxUtilization(Float maxUtilization) {
        this.maxUtilization = maxUtilization;
    }

    @Override
    public void copyFrom(Backend model) {
        setBalancingMode(model.getBalancingMode());
        setCapacityScaler(model.getCapacityScaler());
        setDescription(model.getDescription());
        InstanceGroupResource instanceGroup = null;
        String group = model.getGroup();

        if (group != null) {
            instanceGroup = findById(InstanceGroupResource.class, group);
        }
        setGroup(instanceGroup);
        setMaxConnections(model.getMaxConnections());
        setMaxConnectionsPerEndpoint(model.getMaxConnectionsPerEndpoint());
        setMaxConnectionsPerInstance(model.getMaxConnectionsPerInstance());
        setMaxRate(model.getMaxRate());
        setMaxRatePerEndpoint(model.getMaxRatePerEndpoint());
        setMaxRatePerInstance(model.getMaxRatePerInstance());
        setMaxUtilization(model.getMaxUtilization());
    }

    @Override
    public String primaryKey() {
        return String.format(
            "%s::%s",
            DiffableType.getInstance(getClass()).getName(),
            Optional.ofNullable(getGroup()).map(InstanceGroupResource::getSelfLink).orElse(""));
    }

    public Backend copyTo() {
        Backend backend = new Backend();
        backend.setBalancingMode(getBalancingMode());
        backend.setCapacityScaler(getCapacityScaler());
        backend.setDescription(getDescription());
        Optional.ofNullable(getGroup()).ifPresent(instanceGroup -> backend.setGroup(instanceGroup.getSelfLink()));
        backend.setMaxConnections(getMaxConnections());
        backend.setMaxConnectionsPerEndpoint(getMaxConnectionsPerEndpoint());
        backend.setMaxConnectionsPerInstance(getMaxConnectionsPerInstance());
        backend.setMaxRate(getMaxRate());
        backend.setMaxRatePerEndpoint(getMaxRatePerEndpoint());
        backend.setMaxRatePerInstance(getMaxRatePerInstance());
        backend.setMaxUtilization(getMaxUtilization());
        return backend;
    }

    protected boolean isEqualTo(Backend backend) {
        InstanceGroupResource instanceGroup = getGroup();

        if (instanceGroup == null) {
            return false;
        }
        return Optional.ofNullable(backend)
            .map(Backend::getGroup)
            .filter(group -> group.equals(instanceGroup.getSelfLink()))
            .isPresent();
    }
}
