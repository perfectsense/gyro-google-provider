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

import com.google.api.services.sqladmin.model.ReplicaConfiguration;
import gyro.core.resource.Diffable;
import gyro.core.resource.Updatable;
import gyro.google.Copyable;

public class DbReplicaConfiguration extends Diffable implements Copyable<ReplicaConfiguration> {

    private Boolean cascadableReplica;
    private Boolean failoverTarget;
    private DbMySqlReplicaConfiguration mysqlReplicaConfiguration;

    /**
     * When set to ``true``, the SQL Server replica is a cascadable replica. A cascadable replica is a SQL Server cross
     * region replica that supports replica(s) under it.
     */
    @Updatable
    public Boolean getCascadableReplica() {
        return cascadableReplica;
    }

    public void setCascadableReplica(Boolean cascadableReplica) {
        this.cascadableReplica = cascadableReplica;
    }

    /**
     * When set to ``true``, the replica is the failover target.
     */
    @Updatable
    public Boolean getFailoverTarget() {
        return failoverTarget;
    }

    public void setFailoverTarget(Boolean failoverTarget) {
        this.failoverTarget = failoverTarget;
    }

    /**
     * The MySQL specific configuration when replicating from a MySQL on-premises primary instance.
     *
     * @subresource gyro.google.cloudsql.DbMySqlReplicaConfiguration
     */
    public DbMySqlReplicaConfiguration getMysqlReplicaConfiguration() {
        return mysqlReplicaConfiguration;
    }

    public void setMysqlReplicaConfiguration(DbMySqlReplicaConfiguration mysqlReplicaConfiguration) {
        this.mysqlReplicaConfiguration = mysqlReplicaConfiguration;
    }

    @Override
    public void copyFrom(ReplicaConfiguration model) throws Exception {
        setCascadableReplica(model.getCascadableReplica());
        setFailoverTarget(model.getFailoverTarget());

        setMysqlReplicaConfiguration(null);
        if (model.getMysqlReplicaConfiguration() != null) {
            DbMySqlReplicaConfiguration config = new DbMySqlReplicaConfiguration();
            config.copyFrom(model.getMysqlReplicaConfiguration());
            setMysqlReplicaConfiguration(config);
        }
    }

    @Override
    public String primaryKey() {
        return "";
    }

    public ReplicaConfiguration toReplicaConfiguration() {
        ReplicaConfiguration config = new ReplicaConfiguration();

        config.setCascadableReplica(getCascadableReplica());
        config.setFailoverTarget(getFailoverTarget());

        if (getMysqlReplicaConfiguration() != null) {
            config.setMysqlReplicaConfiguration(getMysqlReplicaConfiguration().toMySqlReplicaConfiguration());
        }

        return config;
    }
}
