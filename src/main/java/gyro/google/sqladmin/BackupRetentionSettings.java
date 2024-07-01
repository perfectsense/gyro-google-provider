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
import gyro.core.validation.ValidStrings;
import gyro.google.Copyable;

public class BackupRetentionSettings extends Diffable
    implements Copyable<com.google.api.services.sqladmin.model.BackupRetentionSettings> {

    private Integer retainedBackups;

    private String retentionUnit;

    /**
     * Depending on the value of retention_unit, this is used to determine if a backup needs to be deleted. If retention_unit is 'COUNT', we will retain this many backups.
     */
    public Integer getRetainedBackups() {
        return retainedBackups;
    }

    public void setRetainedBackups(Integer retainedBackups) {
        this.retainedBackups = retainedBackups;
    }

    /**
     * The unit that 'retained_backups' represents.
     */
    @ValidStrings({
        "RETENTION_UNIT_UNSPECIFIED",
        "COUNT"
    })
    public String getRetentionUnit() {
        return retentionUnit;
    }

    public void setRetentionUnit(String retentionUnit) {
        this.retentionUnit = retentionUnit;
    }

    @Override
    public String primaryKey() {
        return "";
    }

    @Override
    public void copyFrom(com.google.api.services.sqladmin.model.BackupRetentionSettings model) {
        setRetainedBackups(model.getRetainedBackups());
        setRetentionUnit(model.getRetentionUnit());
    }

    com.google.api.services.sqladmin.model.BackupRetentionSettings toBackupRetentionSettings() {
        com.google.api.services.sqladmin.model.BackupRetentionSettings backupRetentionSettings = new com.google.api.services.sqladmin.model.BackupRetentionSettings();
        backupRetentionSettings.setRetainedBackups(getRetainedBackups());
        backupRetentionSettings.setRetentionUnit(getRetentionUnit());

        return backupRetentionSettings;
    }
}
