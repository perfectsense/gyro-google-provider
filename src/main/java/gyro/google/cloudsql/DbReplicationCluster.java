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

import com.google.api.services.sqladmin.model.ReplicationCluster;
import gyro.core.resource.Diffable;
import gyro.core.resource.Output;
import gyro.core.resource.Updatable;
import gyro.google.Copyable;

public class DbReplicationCluster extends Diffable implements Copyable<ReplicationCluster> {

    private String failoverDrReplicaName;

    // Read-only
    private Boolean drReplica;
    private String psaWriteEndpoint;

    /**
     * The disaster recovery (DR) replica for a primary instance.
     * If the instance is a read replica, then the field is not set. Set this field to a replica name to designate a DR
     * replica for a primary instance. Remove the replica name to remove the DR replica designation.
     * Only for Enterprise Plus edition instances.
     */
    @Updatable
    public String getFailoverDrReplicaName() {
        return failoverDrReplicaName;
    }

    public void setFailoverDrReplicaName(String failoverDrReplicaName) {
        this.failoverDrReplicaName = failoverDrReplicaName;
    }

    /**
     * When set to ``true``, the replica is a DR replica.
     */
    @Output
    public Boolean getDrReplica() {
        return drReplica;
    }

    public void setDrReplica(Boolean drReplica) {
        this.drReplica = drReplica;
    }

    /**
     * The private service access (PSA) dns endpoint that points to the primary instance of the cluster.
     * If this instance is the primary, the dns should be pointing to this instance.
     * After Switchover or Replica failover, this DNS endpoint points to the promoted instance.
     */
    @Output
    public String getPsaWriteEndpoint() {
        return psaWriteEndpoint;
    }

    public void setPsaWriteEndpoint(String psaWriteEndpoint) {
        this.psaWriteEndpoint = psaWriteEndpoint;
    }

    @Override
    public void copyFrom(ReplicationCluster model) throws Exception {
        setFailoverDrReplicaName(getFailoverDrReplicaName());
        setDrReplica(getDrReplica());
        setPsaWriteEndpoint(getPsaWriteEndpoint());
    }

    @Override
    public String primaryKey() {
        return "";
    }

    public ReplicationCluster toReplicationCluster() {
        ReplicationCluster replicationCluster = new ReplicationCluster();
        replicationCluster.setFailoverDrReplicaName(getFailoverDrReplicaName());
        return replicationCluster;
    }
}
