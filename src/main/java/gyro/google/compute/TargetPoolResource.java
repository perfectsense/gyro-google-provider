/*
 * Copyright 2020, Perfect Sense, Inc.
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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.api.services.compute.Compute;
import com.google.api.services.compute.model.HealthCheckReference;
import com.google.api.services.compute.model.InstanceReference;
import com.google.api.services.compute.model.Operation;
import com.google.api.services.compute.model.TargetPool;
import com.google.api.services.compute.model.TargetPoolsAddHealthCheckRequest;
import com.google.api.services.compute.model.TargetPoolsAddInstanceRequest;
import com.google.api.services.compute.model.TargetPoolsRemoveHealthCheckRequest;
import com.google.api.services.compute.model.TargetPoolsRemoveInstanceRequest;
import com.google.api.services.compute.model.TargetReference;
import gyro.core.GyroUI;
import gyro.core.Type;
import gyro.core.resource.Id;
import gyro.core.resource.Output;
import gyro.core.resource.Resource;
import gyro.core.resource.Updatable;
import gyro.core.scope.State;
import gyro.core.validation.CollectionMax;
import gyro.core.validation.DependsOn;
import gyro.core.validation.Range;
import gyro.core.validation.Regex;
import gyro.core.validation.Required;
import gyro.core.validation.ValidStrings;
import gyro.google.Copyable;

/**
 * Creates a target pool.
 *
 * Examples
 * --------
 *
 * .. code-block:: gyro
 *
 *      google::compute-target-pool target-pool-example
 *         name: "target-pool-example"
 *         region: "us-central1"
 *         description: "Target pool description."
 *         session-affinity: "CLIENT_IP"
 *
 *         instances: [ $(google::compute-instance instance-example-target-pool) ]
 *
 *         health-checks: [ $(google::compute-http-health-check http-health-check-example-target-pool) ]
 *     end
 *
 * .. code-block:: gyro
 *
 *     google::compute-target-pool target-pool-backup-example
 *         name: "target-pool-backup-example"
 *         region: "us-central1"
 *         description: "Target pool backup description."
 *         failover-ratio: 0.15
 *         backup-pool: $(google::compute-target-pool target-pool-example)
 *
 *         instances: [ $(google::compute-instance instance-example-target-pool) ]
 *
 *         health-checks: [ $(google::compute-http-health-check http-health-check-example-target-pool) ]
 *     end
 */
@Type("compute-target-pool")
public class TargetPoolResource extends ComputeResource implements Copyable<TargetPool> {

    private TargetPoolResource backupPool;
    private String description;
    private Float failoverRatio;
    private List<InstanceResource> instances;
    private List<HttpHealthCheckResource> healthChecks;
    private String name;
    private String sessionAffinity;
    private String region;

    // Read-only
    private String selfLink;

    /**
     * The backup target pool handles traffic if the health of this target pool falls below the failover ratio. If set, ``failover-ratio`` must also be set.
     */
    @DependsOn("failover-ratio")
    @Updatable
    public TargetPoolResource getBackupPool() {
        return backupPool;
    }

    public void setBackupPool(TargetPoolResource backupPool) {
        this.backupPool = backupPool;
    }

    /**
     * An optional description of this target pool.
     */
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * The percentage of healthy instances below which a failover to the backup target pool is triggered. The value must be between 0 and 1. If set, ``backup-pool`` must also be set.
     */
    @DependsOn("backup-pool")
    @Range(min = 0, max = 1)
    @Updatable
    public Float getFailoverRatio() {
        return failoverRatio;
    }

    public void setFailoverRatio(Float failoverRatio) {
        this.failoverRatio = failoverRatio;
    }

    /**
     * A list of instance resources serving this pool.
     */
    @Updatable
    public List<InstanceResource> getInstances() {
        if (instances == null) {
            instances = new ArrayList<>();
        }
        return instances;
    }

    public void setInstances(List<InstanceResource> instances) {
        this.instances = instances;
    }

    /**
     * A list of legacy http health checks monitoring this pool. Only one health check may be specified.
     */
    @CollectionMax(1)
    @Updatable
    public List<HttpHealthCheckResource> getHealthChecks() {
        if (healthChecks == null) {
            healthChecks = new ArrayList<>();
        }
        return healthChecks;
    }

    public void setHealthChecks(List<HttpHealthCheckResource> healthChecks) {
        this.healthChecks = healthChecks;
    }

    /**
     * The name of the target pool. The name must be 1-63 characters long and the first character must be a lowercase letter, and all following characters must be a dash, lowercase letter, or digit, except the last character, which cannot be a dash. (Required)
     */
    @Regex("[a-z](?:[-a-z0-9]{0,61}[a-z0-9])?")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * Session affinity option. Valid values are ``NONE``, ``CLIENT_IP``, or ``CLIENT_IP_PROTO``. Defaults to ``NONE``.
     */
    @ValidStrings({ "NONE", "CLIENT_IP", "CLIENT_IP_PROTO" })
    public String getSessionAffinity() {
        return sessionAffinity;
    }

    public void setSessionAffinity(String sessionAffinity) {
        this.sessionAffinity = sessionAffinity;
    }

    /**
     * Region where the target pool resides. (Required)
     */
    @Required
    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region != null ? region.substring(region.lastIndexOf("/") + 1) : null;
    }

    /**
     * Server-defined URL for the resource.
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
    public void copyFrom(TargetPool model) throws Exception {
        setBackupPool(findById(TargetPoolResource.class, model.getBackupPool()));
        setDescription(model.getDescription());
        setFailoverRatio(model.getFailoverRatio());
        setName(model.getName());
        setSessionAffinity(model.getSessionAffinity());
        setRegion(model.getRegion());
        setSelfLink(model.getSelfLink());

        List<String> instances = model.getInstances();
        setInstances(null);
        if (instances != null) {
            setInstances(instances.stream().map(e -> findById(InstanceResource.class, e)).collect(Collectors.toList()));
        }

        List<String> healthChecks = model.getHealthChecks();
        setHealthChecks(null);
        if (healthChecks != null) {
            setHealthChecks(healthChecks.stream()
                .map(e -> findById(HttpHealthCheckResource.class, e))
                .collect(Collectors.toList()));
        }
    }

    @Override
    protected boolean doRefresh() throws Exception {
        Compute client = createComputeClient();

        TargetPool targetPool = client.targetPools().get(getProjectId(), getRegion(), getName()).execute();
        copyFrom(targetPool);

        return true;
    }

    @Override
    protected void doCreate(GyroUI ui, State state) throws Exception {
        TargetPool targetPool = new TargetPool();
        targetPool.setBackupPool(getBackupPool() != null ? getBackupPool().getSelfLink() : null);
        targetPool.setDescription(getDescription());
        targetPool.setFailoverRatio(getFailoverRatio());
        targetPool.setName(getName());
        targetPool.setSessionAffinity(getSessionAffinity());
        targetPool.setInstances(
            getInstances().stream().map(InstanceResource::getSelfLink).collect(Collectors.toList()));
        targetPool.setHealthChecks(
            getHealthChecks().stream().map(HttpHealthCheckResource::getSelfLink).collect(Collectors.toList()));

        Compute client = createComputeClient();
        Operation operation = client.targetPools().insert(getProjectId(), getRegion(), targetPool).execute();
        waitForCompletion(client, operation);

        refresh();
    }

    @Override
    public void doUpdate(GyroUI ui, State state, Resource current, Set<String> changedFieldNames) throws Exception {
        TargetPoolResource currentTargetPool = (TargetPoolResource) current;
        Compute client = createComputeClient();

        if (changedFieldNames.contains("backup-pool") || changedFieldNames.contains("failover-ratio")) {
            TargetReference targetReference = new TargetReference();
            targetReference.setTarget(getBackupPool() != null ? getBackupPool().getSelfLink() : null);
            Operation response = client.targetPools()
                .setBackup(getProjectId(), getRegion(), getName(), targetReference)
                .setFailoverRatio(getFailoverRatio())
                .execute();
            waitForCompletion(client, response);
        }

        if (changedFieldNames.contains("instances")) {
            List<InstanceResource> removeInstances = currentTargetPool.getInstances();
            removeInstances.removeAll(getInstances());
            if (!removeInstances.isEmpty()) {
                TargetPoolsRemoveInstanceRequest removeInstanceRequest = new TargetPoolsRemoveInstanceRequest();
                removeInstanceRequest.setInstances(
                    removeInstances.stream()
                        .map(e -> new InstanceReference().setInstance(e.getSelfLink()))
                        .collect(Collectors.toList()));
                Operation response =
                    client.targetPools()
                        .removeInstance(getProjectId(), getRegion(), getName(), removeInstanceRequest)
                        .execute();
                waitForCompletion(client, response);
            }

            List<InstanceResource> newInstances = getInstances();
            newInstances.removeAll(currentTargetPool.getInstances());
            if (!newInstances.isEmpty()) {
                TargetPoolsAddInstanceRequest addInstanceRequest = new TargetPoolsAddInstanceRequest();
                addInstanceRequest.setInstances(
                    newInstances.stream()
                        .map(e -> new InstanceReference().setInstance(e.getSelfLink()))
                        .collect(Collectors.toList()));
                Operation response =
                    client.targetPools()
                        .addInstance(getProjectId(), getRegion(), getName(), addInstanceRequest)
                        .execute();
                waitForCompletion(client, response);
            }
        }

        if (changedFieldNames.contains("health-checks")) {
            List<HttpHealthCheckResource> removeHealthChecks = currentTargetPool.getHealthChecks();
            removeHealthChecks.removeAll(getHealthChecks());
            if (!removeHealthChecks.isEmpty()) {
                TargetPoolsRemoveHealthCheckRequest removeHealthCheckRequest = new TargetPoolsRemoveHealthCheckRequest();
                removeHealthCheckRequest.setHealthChecks(
                    removeHealthChecks.stream()
                        .map(e -> new HealthCheckReference().setHealthCheck(e.getSelfLink()))
                        .collect(Collectors.toList()));
                Operation response =
                    client.targetPools()
                        .removeHealthCheck(getProjectId(), getRegion(), getName(), removeHealthCheckRequest)
                        .execute();
                waitForCompletion(client, response);
            }

            List<HttpHealthCheckResource> newHealthChecks = getHealthChecks();
            newHealthChecks.removeAll(currentTargetPool.getHealthChecks());
            if (!newHealthChecks.isEmpty()) {
                TargetPoolsAddHealthCheckRequest addHealthCheckRequest = new TargetPoolsAddHealthCheckRequest();
                addHealthCheckRequest.setHealthChecks(
                    newHealthChecks.stream()
                        .map(e -> new HealthCheckReference().setHealthCheck(e.getSelfLink()))
                        .collect(Collectors.toList()));
                Operation response =
                    client.targetPools()
                        .addHealthCheck(getProjectId(), getRegion(), getName(), addHealthCheckRequest)
                        .execute();
                waitForCompletion(client, response);
            }
        }

        refresh();
    }

    @Override
    public void doDelete(GyroUI ui, State state) throws Exception {
        Compute client = createComputeClient();

        Operation operation = client.targetPools().delete(getProjectId(), getRegion(), getName()).execute();
        waitForCompletion(client, operation);
    }
}
