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

import com.google.api.services.sqladmin.model.DatabaseInstance;
import gyro.core.resource.Diffable;
import gyro.core.resource.Output;
import gyro.core.resource.Updatable;
import gyro.google.Copyable;

public class DbFailoverReplica extends Diffable implements Copyable<DatabaseInstance.FailoverReplica> {

    private String name;

    // Read-only
    private Boolean available;

    /**
     * The name of the failover replica.
     */
    @Updatable
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * When set to ``true``, the replica is available to be failed over to.
     */
    @Output
    public Boolean getAvailable() {
        return available;
    }

    public void setAvailable(Boolean available) {
        this.available = available;
    }

    @Override
    public void copyFrom(DatabaseInstance.FailoverReplica model) throws Exception {
        setAvailable(model.getAvailable());
        setName(model.getName());
    }

    @Override
    public String primaryKey() {
        return "";
    }

    public DatabaseInstance.FailoverReplica toFailoverReplica() {
        DatabaseInstance.FailoverReplica replica = new DatabaseInstance.FailoverReplica();
        replica.setAvailable(getAvailable());
        replica.setName(getName());

        return replica;
    }
}
