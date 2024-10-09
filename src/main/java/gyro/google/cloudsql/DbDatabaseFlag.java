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

import com.google.api.services.sqladmin.model.DatabaseFlags;
import gyro.core.resource.Diffable;
import gyro.core.validation.Required;
import gyro.google.Copyable;

public class DbDatabaseFlag extends Diffable implements Copyable<DatabaseFlags> {

    private String name;
    private String value;

    /**
     * the name of the flag.
     */
    @Required
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * the value of the flag.
     */
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public void copyFrom(DatabaseFlags model) throws Exception {
        setName(model.getName());
        setValue(model.getValue());
    }

    @Override
    public String primaryKey() {
        return String.format("DbFlag [Name: %s, Value: %s]", getName(), getValue());
    }

    public DatabaseFlags toDatabaseFlags() {
        DatabaseFlags flag = new DatabaseFlags();
        flag.setName(getName());

        if (getValue() != null) {
            flag.setValue(getValue());
        }

        return flag;
    }
}
