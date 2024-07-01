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
import gyro.core.resource.Updatable;
import gyro.core.validation.Required;
import gyro.google.Copyable;

public class DatabaseFlags extends Diffable implements Copyable<com.google.api.services.sqladmin.model.DatabaseFlags> {

    private String name;

    private String value;

    /**
     * The name of the flag. These flags are passed at instance startup, so include both server options and system variables. Flags are specified with underscores, not hyphens. For more information, see [Configuring Database Flags](https://cloud.google.com/sql/docs/mysql/flags) in the Cloud SQL documentation.
     */
    @Required
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * The value of the flag. Boolean flags are set to `on` for true and `off` for false. This field must be omitted if the flag doesn't take a value.
     */
    @Updatable
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String primaryKey() {
        return getName();
    }

    @Override
    public void copyFrom(com.google.api.services.sqladmin.model.DatabaseFlags model) {
        setName(model.getName());
        setValue(model.getValue());
    }

    com.google.api.services.sqladmin.model.DatabaseFlags toDatabaseFlags() {
        com.google.api.services.sqladmin.model.DatabaseFlags databaseFlags = new com.google.api.services.sqladmin.model.DatabaseFlags();
        databaseFlags.setName(getName());
        databaseFlags.setValue(getValue());

        return databaseFlags;
    }
}
