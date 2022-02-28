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

import com.google.api.gax.rpc.NotFoundException;
import com.google.cloud.compute.v1.AddHealthCheckTargetPoolRequest;
import com.google.cloud.compute.v1.AddInstanceTargetPoolRequest;
import com.google.cloud.compute.v1.DeleteTargetPoolRequest;
import com.google.cloud.compute.v1.GetTargetPoolRequest;
import com.google.cloud.compute.v1.HealthCheckReference;
import com.google.cloud.compute.v1.InsertTargetPoolRequest;
import com.google.cloud.compute.v1.InstanceReference;
import com.google.cloud.compute.v1.Operation;
import com.google.cloud.compute.v1.RemoveHealthCheckTargetPoolRequest;
import com.google.cloud.compute.v1.RemoveInstanceTargetPoolRequest;
import com.google.cloud.compute.v1.SetBackupTargetPoolRequest;
import com.google.cloud.compute.v1.TargetPool;
import com.google.cloud.compute.v1.TargetPoolsAddHealthCheckRequest;
import com.google.cloud.compute.v1.TargetPoolsAddInstanceRequest;
import com.google.cloud.compute.v1.TargetPoolsClient;
import com.google.cloud.compute.v1.TargetPoolsRemoveHealthCheckRequest;
import com.google.cloud.compute.v1.TargetPoolsRemoveInstanceRequest;
import com.google.cloud.compute.v1.TargetReference;
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
     * The backup target pool handles traffic if the health of this target pool falls below the failover ratio.
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
     * The percentage of healthy instances below which a failover to the backup target pool is triggered.
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
     * A list of legacy http health checks monitoring this pool.
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
     * The name of the target pool.
     */
    @Required
    @Regex(value = "[a-z](?:[-a-z0-9]{0,61}[a-z0-9])?", message = "a string 1-63 characters long and the first character must be a lowercase letter, and all following characters must be a dash, lowercase letter, or digit, except the last character, which cannot be a dash")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * Session affinity option. Defaults to ``NONE``.
     */
    @ValidStrings({ "NONE", "CLIENT_IP", "CLIENT_IP_PROTO" })
    public String getSessionAffinity() {
        return sessionAffinity;
    }

    public void setSessionAffinity(String sessionAffinity) {
        this.sessionAffinity = sessionAffinity;
    }

    /**
     * Region where the target pool resides.
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
        setName(model.getName());

        if (model.hasSelfLink()) {
            setSelfLink(model.getSelfLink());
        }

        if (model.hasDescription()) {
            setDescription(model.getDescription());
        }

        if (model.hasBackupPool()) {
            setBackupPool(findById(TargetPoolResource.class, model.getBackupPool()));
        }

        if (model.hasFailoverRatio()) {
            setFailoverRatio(model.getFailoverRatio());
        }

        if (model.hasSessionAffinity()) {
            setSessionAffinity(model.getSessionAffinity());
        }

        if (model.hasRegion()) {
            setRegion(model.getRegion());
        }

        setInstances(null);
        if (!model.getInstancesList().isEmpty()) {
            setInstances(model.getInstancesList().stream()
                .map(e -> findById(InstanceResource.class, e))
                .collect(Collectors.toList()));
        }

        setHealthChecks(null);
        if (!model.getHealthChecksList().isEmpty()) {
            setHealthChecks(model.getHealthChecksList().stream()
                .map(e -> findById(HttpHealthCheckResource.class, e))
                .collect(Collectors.toList()));
        }
    }

    @Override
    protected boolean doRefresh() throws Exception {
        try (TargetPoolsClient client = createClient(TargetPoolsClient.class)) {
            TargetPool targetPool = getTargetPool(client);

            if (targetPool == null) {
                return false;
            }

            copyFrom(targetPool);

            return true;
        }
    }

    @Override
    protected void doCreate(GyroUI ui, State state) throws Exception {
        TargetPool.Builder builder = TargetPool.newBuilder();
        builder.setName(getName());
        builder.addAllInstances(
            getInstances().stream().map(InstanceResource::getSelfLink).collect(Collectors.toList()));
        builder.addAllHealthChecks(
            getHealthChecks().stream().map(HttpHealthCheckResource::getSelfLink).collect(Collectors.toList()));

        if (getBackupPool() != null) {
            builder.setBackupPool(getBackupPool().getSelfLink());
        }

        if (getDescription() != null) {
            builder.setDescription(getDescription());
        }

        if (getFailoverRatio() != null) {
            builder.setFailoverRatio(getFailoverRatio());
        }

        if (getSessionAffinity() != null) {
            builder.setSessionAffinity(getSessionAffinity());
        }

        try (TargetPoolsClient client = createClient(TargetPoolsClient.class)) {
            Operation operation = client.insertCallable().call(InsertTargetPoolRequest.newBuilder()
                    .setProject(getProjectId())
                    .setRegion(getRegion())
                    .setTargetPoolResource(builder)
                .build());

            waitForCompletion(operation);
        }

        refresh();
    }

    @Override
    public void doUpdate(GyroUI ui, State state, Resource current, Set<String> changedFieldNames) throws Exception {
        TargetPoolResource currentTargetPool = (TargetPoolResource) current;
        try (TargetPoolsClient client = createClient(TargetPoolsClient.class)) {

            if (changedFieldNames.contains("backup-pool") || changedFieldNames.contains("failover-ratio")) {
                TargetReference.Builder builder = TargetReference.newBuilder();

                if (getBackupPool() != null) {
                    builder.setTarget(getBackupPool().getSelfLink());
                }

                Operation response = client.setBackupCallable().call(SetBackupTargetPoolRequest.newBuilder()
                    .setProject(getProjectId())
                    .setRegion(getRegion())
                    .setTargetPool(getName())
                    .setFailoverRatio(getFailoverRatio())
                    .build());
                waitForCompletion(response);
            }

            if (changedFieldNames.contains("instances")) {
                List<InstanceResource> removeInstances = currentTargetPool.getInstances();
                removeInstances.removeAll(getInstances());
                if (!removeInstances.isEmpty()) {
                    TargetPoolsRemoveInstanceRequest.Builder builder = TargetPoolsRemoveInstanceRequest.newBuilder();
                    builder.addAllInstances(removeInstances.stream()
                        .map(e -> InstanceReference.newBuilder().setInstance(e.getSelfLink()).build())
                        .collect(Collectors.toList()));

                    Operation response = client.removeInstanceOperationCallable()
                        .call(RemoveInstanceTargetPoolRequest.newBuilder()
                            .setProject(getProjectId())
                            .setRegion(getRegion())
                            .setTargetPool(getName())
                            .setTargetPoolsRemoveInstanceRequestResource(builder)
                            .build());

                    waitForCompletion(response);
                }

                List<InstanceResource> newInstances = getInstances();
                newInstances.removeAll(currentTargetPool.getInstances());
                if (!newInstances.isEmpty()) {
                    TargetPoolsAddInstanceRequest.Builder builder = TargetPoolsAddInstanceRequest.newBuilder();
                    builder.addAllInstances(newInstances.stream()
                        .map(e -> InstanceReference.newBuilder().setInstance(e.getSelfLink()).build())
                        .collect(Collectors.toList()));

                    Operation response = client.addInstanceCallable()
                        .call(AddInstanceTargetPoolRequest.newBuilder()
                            .setProject(getProjectId())
                            .setRegion(getRegion())
                            .setTargetPool(getName())
                            .setTargetPoolsAddInstanceRequestResource(builder)
                            .build());

                    waitForCompletion(response);
                }
            }

            if (changedFieldNames.contains("health-checks")) {
                List<HttpHealthCheckResource> removeHealthChecks = currentTargetPool.getHealthChecks();
                removeHealthChecks.removeAll(getHealthChecks());
                if (!removeHealthChecks.isEmpty()) {
                    TargetPoolsRemoveHealthCheckRequest.Builder builder = TargetPoolsRemoveHealthCheckRequest.newBuilder();
                    builder.addAllHealthChecks(removeHealthChecks.stream()
                        .map(e -> HealthCheckReference.newBuilder().setHealthCheck(e.getSelfLink()).build())
                        .collect(Collectors.toList()));

                    Operation response = client.removeHealthCheckCallable().call(
                        RemoveHealthCheckTargetPoolRequest.newBuilder()
                            .setProject(getProjectId())
                            .setRegion(getRegion())
                            .setTargetPool(getName())
                            .setTargetPoolsRemoveHealthCheckRequestResource(builder)
                            .build());

                    waitForCompletion(response);
                }

                List<HttpHealthCheckResource> newHealthChecks = getHealthChecks();
                newHealthChecks.removeAll(currentTargetPool.getHealthChecks());
                if (!newHealthChecks.isEmpty()) {
                    TargetPoolsAddHealthCheckRequest.Builder builder = TargetPoolsAddHealthCheckRequest.newBuilder();
                    builder.addAllHealthChecks(newHealthChecks.stream()
                        .map(e -> HealthCheckReference.newBuilder().setHealthCheck(e.getSelfLink()).build())
                        .collect(Collectors.toList()));

                    Operation response = client.addHealthCheckCallable()
                        .call(AddHealthCheckTargetPoolRequest.newBuilder()
                            .setProject(getProjectId())
                            .setRegion(getRegion())
                            .setTargetPool(getName())
                            .setTargetPoolsAddHealthCheckRequestResource(builder)
                            .build());

                    waitForCompletion(response);
                }
            }
        }

        refresh();
    }

    @Override
    public void doDelete(GyroUI ui, State state) throws Exception {
        try (TargetPoolsClient client = createClient(TargetPoolsClient.class)) {
            Operation operation = client.deleteCallable().call(DeleteTargetPoolRequest.newBuilder()
                    .setProject(getProjectId())
                    .setRegion(getRegion())
                    .setTargetPool(getName())
                .build());

            waitForCompletion(operation);
        }
    }

    private TargetPool getTargetPool(TargetPoolsClient client) {
        TargetPool targetHttpProxy = null;

        try {
            targetHttpProxy = client.get(GetTargetPoolRequest.newBuilder()
                .setProject(getProjectId())
                .setTargetPool(getName())
                .setRegion(getRegion())
                .build());

        } catch (NotFoundException ex) {
            // ignore
        }

        return targetHttpProxy;
    }
}
