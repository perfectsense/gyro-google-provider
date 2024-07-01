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

import com.google.api.services.sqladmin.model.DatabaseInstance;
import gyro.core.resource.Diffable;
import gyro.google.Copyable;

public class DatabaseInstanceFailoverReplica extends Diffable implements Copyable<DatabaseInstance.FailoverReplica> {

    private Boolean available;

    private String name;

    /**
     * The availability status of the failover replica. A false status indicates that the failover replica is out of sync. The primary instance can only failover to the failover replica when the status is true.
     */
    public Boolean getAvailable() {
        return available;
    }

    public void setAvailable(Boolean available) {
        this.available = available;
    }

    /**
     * The name of the failover replica. If specified at instance creation, a failover replica is created for the instance. The name doesn't include the project ID.
     */
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String primaryKey() {
        return "";
    }

    @Override
    public void copyFrom(DatabaseInstance.FailoverReplica model) {
        setAvailable(model.getAvailable());
        setName(model.getName());
    }

    DatabaseInstance.FailoverReplica toFailoverReplica() {
        return new DatabaseInstance.FailoverReplica()
            .setAvailable(getAvailable())
            .setName(getName());
    }
}
