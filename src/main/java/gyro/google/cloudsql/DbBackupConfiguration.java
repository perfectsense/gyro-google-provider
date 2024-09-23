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

import com.google.api.services.sqladmin.model.BackupConfiguration;
import gyro.core.resource.Diffable;
import gyro.core.resource.Output;
import gyro.core.resource.Updatable;
import gyro.core.validation.Range;
import gyro.core.validation.Regex;
import gyro.core.validation.Required;
import gyro.google.Copyable;

public class DbBackupConfiguration extends Diffable implements Copyable<BackupConfiguration> {

    private DbBackupRetentionSettings backupRetentionSettings;
    private Boolean binaryLogEnabled;
    private Boolean enabled;
    private String location;
    private Boolean pointInTimeRecoveryEnabled;
    private String startTime;
    private Integer transactionLogRetentionDays;

    // Read-only
    private String transactionalLogStorageState;

    /**
     * The backup retention settings.
     */
    @Updatable
    public DbBackupRetentionSettings getBackupRetentionSettings() {
        return backupRetentionSettings;
    }

    public void setBackupRetentionSettings(DbBackupRetentionSettings backupRetentionSettings) {
        this.backupRetentionSettings = backupRetentionSettings;
    }

    /**
     * When set to ``true``, binary log is enabled.
     */
    @Updatable
    public Boolean getBinaryLogEnabled() {
        return binaryLogEnabled;
    }

    public void setBinaryLogEnabled(Boolean binaryLogEnabled) {
        this.binaryLogEnabled = binaryLogEnabled;
    }

    /**
     * When set to ``true``, the backup configuration is enabled.
     */
    @Required
    @Updatable
    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * The location of the backup.
     */
    @Updatable
    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    /**
     * When set to ``true``, point in time recovery is enabled.
     */
    @Updatable
    public Boolean getPointInTimeRecoveryEnabled() {
        return pointInTimeRecoveryEnabled;
    }

    public void setPointInTimeRecoveryEnabled(Boolean pointInTimeRecoveryEnabled) {
        this.pointInTimeRecoveryEnabled = pointInTimeRecoveryEnabled;
    }

    /**
     * The start time for the daily backup configuration in UTC timezone in the 24 hour format - `HH:MM`.
     */
    @Updatable
    @Regex("[0-2][0-9]:[0-5][0-9]")
    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    /**
     * The number of days of transaction logs we retain for point in time restore, from 1-7.
     */
    @Updatable
    @Range(min = 1, max = 7)
    public Integer getTransactionLogRetentionDays() {
        return transactionLogRetentionDays;
    }

    public void setTransactionLogRetentionDays(Integer transactionLogRetentionDays) {
        this.transactionLogRetentionDays = transactionLogRetentionDays;
    }

    /**
     * The storage location of transactional logs for the database for point-in-time recovery.
     */
    @Output
    public String getTransactionalLogStorageState() {
        return transactionalLogStorageState;
    }

    public void setTransactionalLogStorageState(String transactionalLogStorageState) {
        this.transactionalLogStorageState = transactionalLogStorageState;
    }

    @Override
    public void copyFrom(BackupConfiguration model) throws Exception {
        setBinaryLogEnabled(model.getBinaryLogEnabled());
        setEnabled(model.getEnabled());
        setLocation(model.getLocation());
        setPointInTimeRecoveryEnabled(model.getPointInTimeRecoveryEnabled());
        setStartTime(model.getStartTime());
        setTransactionLogRetentionDays(model.getTransactionLogRetentionDays());
        setTransactionalLogStorageState(model.getTransactionalLogStorageState());

        setBackupRetentionSettings(null);
        if (model.getBackupRetentionSettings() != null) {
            DbBackupRetentionSettings settings = new DbBackupRetentionSettings();
            settings.copyFrom(model.getBackupRetentionSettings());
            setBackupRetentionSettings(settings);
        }
    }

    @Override
    public String primaryKey() {
        return "";
    }

    public BackupConfiguration toBackupConfiguration() {
        BackupConfiguration backupConfiguration = new BackupConfiguration();

        backupConfiguration.setBinaryLogEnabled(getBinaryLogEnabled());
        backupConfiguration.setEnabled(getEnabled());
        backupConfiguration.setLocation(getLocation());
        backupConfiguration.setPointInTimeRecoveryEnabled(getPointInTimeRecoveryEnabled());
        backupConfiguration.setStartTime(getStartTime());
        backupConfiguration.setTransactionLogRetentionDays(getTransactionLogRetentionDays());

        if (getBackupRetentionSettings() != null) {
            backupConfiguration.setBackupRetentionSettings(getBackupRetentionSettings().toBackupRetentionSettings());
        }

        return backupConfiguration;
    }
}
