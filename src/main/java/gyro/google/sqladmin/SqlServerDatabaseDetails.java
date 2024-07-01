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

public class SqlServerDatabaseDetails extends Diffable
    implements Copyable<com.google.api.services.sqladmin.model.SqlServerDatabaseDetails> {

    private Integer compatibilityLevel;

    private String recoveryModel;

    /**
     * The version of SQL Server with which the database is to be made compatible
     */
    public Integer getCompatibilityLevel() {
        return compatibilityLevel;
    }

    public void setCompatibilityLevel(Integer compatibilityLevel) {
        this.compatibilityLevel = compatibilityLevel;
    }

    /**
     * The recovery model of a SQL Server database
     */
    public String getRecoveryModel() {
        return recoveryModel;
    }

    public void setRecoveryModel(String recoveryModel) {
        this.recoveryModel = recoveryModel;
    }

    @Override
    public String primaryKey() {
        return "";
    }

    @Override
    public void copyFrom(com.google.api.services.sqladmin.model.SqlServerDatabaseDetails model) {
        setCompatibilityLevel(model.getCompatibilityLevel());
        setRecoveryModel(model.getRecoveryModel());
    }

    com.google.api.services.sqladmin.model.SqlServerDatabaseDetails toSqlServerDatabaseDetails() {
        return new com.google.api.services.sqladmin.model.SqlServerDatabaseDetails()
            .setCompatibilityLevel(getCompatibilityLevel())
            .setRecoveryModel(getRecoveryModel());
    }
}
