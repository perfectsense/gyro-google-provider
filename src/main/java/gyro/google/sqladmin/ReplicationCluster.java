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

import gyro.core.resource.Diffable;
import gyro.google.Copyable;

public class ReplicationCluster extends Diffable
    implements Copyable<com.google.api.services.sqladmin.model.ReplicationCluster> {

    private Boolean drReplica;

    private String failoverDrReplicaName;

    private String psaWriteEndpoint;

    /**
     * Output only. Read-only field that indicates whether the replica is a DR replica. This field is not set if the instance is a primary instance.
     */
    public Boolean getDrReplica() {
        return drReplica;
    }

    public void setDrReplica(Boolean drReplica) {
        this.drReplica = drReplica;
    }

    /**
     * Optional. If the instance is a primary instance, then this field identifies the disaster recovery (DR) replica. A DR replica is an optional configuration for Enterprise Plus edition instances. If the instance is a read replica, then the field is not set. Set this field to a replica name to designate a DR replica for a primary instance. Remove the replica name to remove the DR replica designation.
     */
    public String getFailoverDrReplicaName() {
        return failoverDrReplicaName;
    }

    public void setFailoverDrReplicaName(String failoverDrReplicaName) {
        this.failoverDrReplicaName = failoverDrReplicaName;
    }

    /**
     * Output only. If set, it indicates this instance has a private service access (PSA) dns endpoint that is pointing to the primary instance of the cluster. If this instance is the primary, the dns should be pointing to this instance. After Switchover or Replica failover, this DNS endpoint points to the promoted instance. This is a read-only field, returned to the user as information. This field can exist even if a standalone instance does not yet have a replica, or had a DR replica that was deleted.
     */
    public String getPsaWriteEndpoint() {
        return psaWriteEndpoint;
    }

    public void setPsaWriteEndpoint(String psaWriteEndpoint) {
        this.psaWriteEndpoint = psaWriteEndpoint;
    }

    @Override
    public String primaryKey() {
        return "";
    }

    @Override
    public void copyFrom(com.google.api.services.sqladmin.model.ReplicationCluster model) {
        setDrReplica(model.getDrReplica());
        setFailoverDrReplicaName(model.getFailoverDrReplicaName());
        setPsaWriteEndpoint(model.getPsaWriteEndpoint());
    }

    com.google.api.services.sqladmin.model.ReplicationCluster toReplicationCluster() {
        return new com.google.api.services.sqladmin.model.ReplicationCluster()
            .setDrReplica(getDrReplica())
            .setFailoverDrReplicaName(getFailoverDrReplicaName())
            .setPsaWriteEndpoint(getPsaWriteEndpoint());
    }
}
