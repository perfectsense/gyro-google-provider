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

import com.google.api.services.sqladmin.model.BackupRetentionSettings;
import gyro.core.resource.Diffable;
import gyro.core.resource.Updatable;
import gyro.core.validation.DependsOn;
import gyro.core.validation.ValidStrings;
import gyro.google.Copyable;

public class DbBackupRetentionSettings extends Diffable implements Copyable<BackupRetentionSettings> {

    private Integer retainedBackups;
    private String retentionUnit;

    /**
     * The number of backups to retain.
     */
    @Updatable
    public Integer getRetainedBackups() {
        return retainedBackups;
    }

    public void setRetainedBackups(Integer retainedBackups) {
        this.retainedBackups = retainedBackups;
    }

    /**
     * The unit that the retained backups represents.
     */
    @DependsOn("retained-backups")
    @ValidStrings("COUNT")
    @Updatable
    public String getRetentionUnit() {
        return retentionUnit;
    }

    public void setRetentionUnit(String retentionUnit) {
        this.retentionUnit = retentionUnit;
    }

    @Override
    public void copyFrom(BackupRetentionSettings model) throws Exception {
        setRetainedBackups(model.getRetainedBackups());
        setRetentionUnit(model.getRetentionUnit());
    }

    @Override
    public String primaryKey() {
        return "";
    }

    public BackupRetentionSettings toBackupRetentionSettings() {
        BackupRetentionSettings settings = new BackupRetentionSettings();

        settings.setRetainedBackups(getRetainedBackups());
        settings.setRetentionUnit(getRetentionUnit());

        return settings;
    }
}
