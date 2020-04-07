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

import com.google.api.services.compute.model.Backend;
import gyro.core.resource.Diffable;
import gyro.core.resource.Updatable;
import gyro.core.validation.Required;
import gyro.core.validation.ValidStrings;
import gyro.google.Copyable;

public class ComputeBackend extends Diffable implements Copyable<Backend> {

    private String balancingMode;
    private Float capacityScaler;
    private String description;
    private String group;
    private Integer maxConnections;
    private Integer maxConnectionsPerEndpoint;
    private Integer maxConnectionsPerInstance;
    private Integer maxRate;
    private Float maxRatePerEndpoint;
    private Float maxRatePerInstance;
    private Float maxUtilization;

    /**
     * The balancing mode for the backend. Valid values are ``RATE``, ``CONNECTION`` or ``UTILIZATION``. (Required)
     */
    @Required
    @Updatable
    @ValidStrings({ "RATE", "CONNECTION", "UTILIZATION" })
    public String getBalancingMode() {
        return balancingMode;
    }

    public void setBalancingMode(String balancingMode) {
        this.balancingMode = balancingMode;
    }

    /**
     * The multiplier applied to the group's maximum servicing capacity. Valid values are from ``0.0`` to ``1.0``.
     */
    @Updatable
    public Float getCapacityScaler() {
        return capacityScaler;
    }

    public void setCapacityScaler(Float capacityScaler) {
        this.capacityScaler = capacityScaler;
    }

    /**
     * The description of the backend.
     */
    @Updatable
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * The fully-qualified URL of an instance group or network endpoint group (NEG) resource.
     * When ``load-balancing-scheme`` is set to either ``EXTERNAL``, ``INTERNAL_SELF_MANAGED``, or
     * ``INTERNAL_MANAGED``, the group can be a instance group or a NEG. If set to ``INTERNAL``
     * the group needs to be an instance group in the same region as the backend service. When referencing
     * instance group manager/ region intance group manager, use the attribute ``instance-group-link`` 
     * instead of ``self-link``.
     */
    @Required
    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    /**
     * Defines a maximum target for simultaneous connections for the entire backend. Can only be set if ``balancing-mode`` is set to ``CONNECTION`` or ``UTILIZATION``. For ``CONNECTION`` mode either ``max-connections``, ``max-connections-per-endpoint`` or ``max-connections-per-instance`` must be set.
     */
    @Updatable
    public Integer getMaxConnections() {
        return maxConnections;
    }

    public void setMaxConnections(Integer maxConnections) {
        this.maxConnections = maxConnections;
    }

    /**
     * Defines a maximum target for simultaneous connections for each instance in the group. Can only be set if ``balancing-mode`` is set to ``CONNECTION`` or ``UTILIZATION``. For ``CONNECTION`` mode either ``max-connections``, ``max-connections-per-endpoint`` or ``max-connections-per-instance`` must be set.
     */
    @Updatable
    public Integer getMaxConnectionsPerEndpoint() {
        return maxConnectionsPerEndpoint;
    }

    public void setMaxConnectionsPerEndpoint(Integer maxConnectionsPerEndpoint) {
        this.maxConnectionsPerEndpoint = maxConnectionsPerEndpoint;
    }

    /**
     * Defines a maximum target for simultaneous connections for each endpoint in the group. Can only be set if ``balancing-mode`` is set to ``CONNECTION`` or ``UTILIZATION``. For ``CONNECTION`` mode either ``max-connections``, ``max-connections-per-endpoint`` or ``max-connections-per-instance`` must be set.
     */
    @Updatable
    public Integer getMaxConnectionsPerInstance() {
        return maxConnectionsPerInstance;
    }

    public void setMaxConnectionsPerInstance(Integer maxConnectionsPerInstance) {
        this.maxConnectionsPerInstance = maxConnectionsPerInstance;
    }

    /**
     * The max requests per second (RPS) of the group. Can only be set if ``balancing-mode`` is set to ``RATE`` or ``UTILIZATION``. For ``RATE`` mode, either maxRate or maxRatePerInstance must be set.
     */
    @Updatable
    public Integer getMaxRate() {
        return maxRate;
    }

    public void setMaxRate(Integer maxRate) {
        this.maxRate = maxRate;
    }

    /**
     * The max requests per second (RPS) for each endpoint in the group. Can only be set if ``balancing-mode`` is set to ``RATE`` or ``UTILIZATION``. For ``RATE`` mode, either `max-rate` or `max-rate-per-instance` must be set.
     */
    @Updatable
    public Float getMaxRatePerEndpoint() {
        return maxRatePerEndpoint;
    }

    public void setMaxRatePerEndpoint(Float maxRatePerEndpoint) {
        this.maxRatePerEndpoint = maxRatePerEndpoint;
    }

    /**
     * The max requests per second (RPS) for each instance in the group. Can only be set if ``balancing-mode`` is set to ``RATE`` or ``UTILIZATION``. For ``RATE`` mode, either `max-rate` or `max-rate-per-endpoint` must be set.
     */
    @Updatable
    public Float getMaxRatePerInstance() {
        return maxRatePerInstance;
    }

    public void setMaxRatePerInstance(Float maxRatePerInstance) {
        this.maxRatePerInstance = maxRatePerInstance;
    }

    /**
     * The maximum average CPU utilization of a backend instance in an instance group. Valid values are from ``0.0`` to ``1.0``.
     */
    @Updatable
    public Float getMaxUtilization() {
        return maxUtilization;
    }

    public void setMaxUtilization(Float maxUtilization) {
        this.maxUtilization = maxUtilization;
    }

    @Override
    public void copyFrom(Backend model) {
        // pending field validation once https://github.com/perfectsense/gyro/issues/201 is fixed

        setBalancingMode(model.getBalancingMode());
        setCapacityScaler(model.getCapacityScaler());
        setDescription(model.getDescription());
        InstanceGroupResource instanceGroup = null;
        setGroup(model.getGroup());
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
        return String.format("linked to %s", getGroup());
    }

    public Backend toBackend() {
        Backend backend = new Backend();
        backend.setBalancingMode(getBalancingMode());
        backend.setCapacityScaler(getCapacityScaler());
        backend.setDescription(getDescription());
        backend.setGroup(getGroup());
        backend.setMaxConnections(getMaxConnections());
        backend.setMaxConnectionsPerEndpoint(getMaxConnectionsPerEndpoint());
        backend.setMaxConnectionsPerInstance(getMaxConnectionsPerInstance());
        backend.setMaxRate(getMaxRate());
        backend.setMaxRatePerEndpoint(getMaxRatePerEndpoint());
        backend.setMaxRatePerInstance(getMaxRatePerInstance());
        backend.setMaxUtilization(getMaxUtilization());
        return backend;
    }
}
