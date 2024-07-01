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

public class ReplicaConfiguration extends Diffable
    implements Copyable<com.google.api.services.sqladmin.model.ReplicaConfiguration> {

    private Boolean cascadableReplica;

    private Boolean failoverTarget;

    private MySqlReplicaConfiguration mysqlReplicaConfiguration;

    /**
     * Optional. Specifies if a SQL Server replica is a cascadable replica.
     * A cascadable replica is a SQL Server cross region replica that supports replica(s) under it.
     */
    public Boolean getCascadableReplica() {
        return cascadableReplica;
    }

    public void setCascadableReplica(Boolean cascadableReplica) {
        this.cascadableReplica = cascadableReplica;
    }

    /**
     * Specifies if the replica is the failover target.
     * If the field is set to `true`, the replica will be designated as a failover replica.
     * In case the primary instance fails, the replica instance will be promoted as the new primary instance.
     * Only one replica can be specified as failover target, and the replica has to be in different zone with the primary instance.
     */
    public Boolean getFailoverTarget() {
        return failoverTarget;
    }

    public void setFailoverTarget(Boolean failoverTarget) {
        this.failoverTarget = failoverTarget;
    }

    /**
     * MySQL specific configuration when replicating from a MySQL on-premises primary instance.
     * Replication configuration information such as the username, password, certificates, and keys are not stored in the instance metadata.
     * The configuration information is used only to set up the replication connection and is stored by MySQL in a file named `master.info` in the data directory.
     *
     * @subresource gyro.google.sqladmin.base.MySqlReplicaConfiguration
     */
    public MySqlReplicaConfiguration getMysqlReplicaConfiguration() {
        return mysqlReplicaConfiguration;
    }

    public void setMysqlReplicaConfiguration(MySqlReplicaConfiguration mysqlReplicaConfiguration) {
        this.mysqlReplicaConfiguration = mysqlReplicaConfiguration;
    }

    @Override
    public String primaryKey() {
        return "";
    }

    @Override
    public void copyFrom(com.google.api.services.sqladmin.model.ReplicaConfiguration model) {
        setCascadableReplica(model.getCascadableReplica());
        setFailoverTarget(model.getFailoverTarget());

        setMysqlReplicaConfiguration(null);
        if (model.getMysqlReplicaConfiguration() != null) {
            MySqlReplicaConfiguration mysqlReplicaConfiguration = newSubresource(MySqlReplicaConfiguration.class);
            mysqlReplicaConfiguration.copyFrom(model.getMysqlReplicaConfiguration());
            setMysqlReplicaConfiguration(mysqlReplicaConfiguration);
        }
    }

    com.google.api.services.sqladmin.model.ReplicaConfiguration toReplicaConfiguration() {
        com.google.api.services.sqladmin.model.ReplicaConfiguration replicaConfiguration = new com.google.api.services.sqladmin.model.ReplicaConfiguration();
        replicaConfiguration.setCascadableReplica(getCascadableReplica());
        replicaConfiguration.setFailoverTarget(getFailoverTarget());

        if (getMysqlReplicaConfiguration() != null) {
            replicaConfiguration.setMysqlReplicaConfiguration(getMysqlReplicaConfiguration().toMySqlReplicaConfiguration());
        }

        return replicaConfiguration;
    }
}
