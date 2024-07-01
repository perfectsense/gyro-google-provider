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

public class BackupConfiguration extends Diffable
    implements Copyable<com.google.api.services.sqladmin.model.BackupConfiguration> {

    private BackupRetentionSettings backupRetentionSettings;

    private Boolean binaryLogEnabled;

    private Boolean enabled;

    private String location;

    private Boolean pointInTimeRecoveryEnabled;

    private Boolean replicationLogArchivingEnabled;

    private String startTime;

    private String transactionalLogStorageState;

    private Integer transactionLogRetentionDays;

    /**
     * Backup retention settings.
     *
     * @subresource gyro.google.sqladmin.base.BackupRetentionSettings
     */
    public BackupRetentionSettings getBackupRetentionSettings() {
        return backupRetentionSettings;
    }

    public void setBackupRetentionSettings(BackupRetentionSettings backupRetentionSettings) {
        this.backupRetentionSettings = backupRetentionSettings;
    }

    /**
     * (MySQL only) Whether binary log is enabled. If backup configuration is disabled, binarylog must be disabled as well.
     */
    public Boolean getBinaryLogEnabled() {
        return binaryLogEnabled;
    }

    public void setBinaryLogEnabled(Boolean binaryLogEnabled) {
        this.binaryLogEnabled = binaryLogEnabled;
    }

    /**
     * Whether this configuration is enabled.
     */
    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * Location of the backup
     */
    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    /**
     * Whether point in time recovery is enabled.
     */
    public Boolean getPointInTimeRecoveryEnabled() {
        return pointInTimeRecoveryEnabled;
    }

    public void setPointInTimeRecoveryEnabled(Boolean pointInTimeRecoveryEnabled) {
        this.pointInTimeRecoveryEnabled = pointInTimeRecoveryEnabled;
    }

    /**
     * Reserved for future use.
     */
    public Boolean getReplicationLogArchivingEnabled() {
        return replicationLogArchivingEnabled;
    }

    public void setReplicationLogArchivingEnabled(Boolean replicationLogArchivingEnabled) {
        this.replicationLogArchivingEnabled = replicationLogArchivingEnabled;
    }

    /**
     * Start time for the daily backup configuration in UTC timezone in the 24 hour format - `HH:MM`.
     */
    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    /**
     * Output only. This value contains the storage location of transactional logs used to perform point-in-time recovery (PITR) for the database.
     */
    @ValidStrings({
        "TRANSACTIONAL_LOG_STORAGE_STATE_UNSPECIFIED",
        "DISK",
        "SWITCHING_TO_CLOUD_STORAGE",
        "SWITCHED_TO_CLOUD_STORAGE",
        "CLOUD_STORAGE"
    })
    public String getTransactionalLogStorageState() {
        return transactionalLogStorageState;
    }

    public void setTransactionalLogStorageState(String transactionalLogStorageState) {
        this.transactionalLogStorageState = transactionalLogStorageState;
    }

    /**
     * The number of days of transaction logs we retain for point in time restore, from 1-7.
     */
    public Integer getTransactionLogRetentionDays() {
        return transactionLogRetentionDays;
    }

    public void setTransactionLogRetentionDays(Integer transactionLogRetentionDays) {
        this.transactionLogRetentionDays = transactionLogRetentionDays;
    }

    @Override
    public String primaryKey() {
        return "";
    }

    @Override
    public void copyFrom(com.google.api.services.sqladmin.model.BackupConfiguration model) {
        setBackupRetentionSettings(null);
        if (model.getBackupRetentionSettings() != null) {
            BackupRetentionSettings settings = newSubresource(BackupRetentionSettings.class);
            settings.copyFrom(model.getBackupRetentionSettings());
            setBackupRetentionSettings(settings);
        }

        setBinaryLogEnabled(model.getBinaryLogEnabled());
        setEnabled(model.getEnabled());
        setLocation(model.getLocation());
        setPointInTimeRecoveryEnabled(model.getPointInTimeRecoveryEnabled());
        setReplicationLogArchivingEnabled(model.getReplicationLogArchivingEnabled());
        setStartTime(model.getStartTime());
        setTransactionalLogStorageState(model.getTransactionalLogStorageState());
        setTransactionLogRetentionDays(model.getTransactionLogRetentionDays());
    }

    com.google.api.services.sqladmin.model.BackupConfiguration toBackupConfiguration() {
        com.google.api.services.sqladmin.model.BackupConfiguration backupConfiguration = new com.google.api.services.sqladmin.model.BackupConfiguration();
        backupConfiguration.setBinaryLogEnabled(getBinaryLogEnabled());
        backupConfiguration.setEnabled(getEnabled());
        backupConfiguration.setLocation(getLocation());
        backupConfiguration.setPointInTimeRecoveryEnabled(getPointInTimeRecoveryEnabled());
        backupConfiguration.setReplicationLogArchivingEnabled(getReplicationLogArchivingEnabled());
        backupConfiguration.setStartTime(getStartTime());
        backupConfiguration.setTransactionalLogStorageState(getTransactionalLogStorageState());
        backupConfiguration.setTransactionLogRetentionDays(getTransactionLogRetentionDays());

        if (getBackupRetentionSettings() != null) {
            backupConfiguration.setBackupRetentionSettings(getBackupRetentionSettings().toBackupRetentionSettings());
        }

        return backupConfiguration;
    }
}
