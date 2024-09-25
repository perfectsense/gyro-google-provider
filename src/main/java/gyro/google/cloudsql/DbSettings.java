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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.api.services.sqladmin.model.DatabaseFlags;
import com.google.api.services.sqladmin.model.DenyMaintenancePeriod;
import com.google.api.services.sqladmin.model.Settings;
import gyro.core.resource.Diffable;
import gyro.core.resource.Output;
import gyro.core.resource.Updatable;
import gyro.core.validation.Range;
import gyro.core.validation.Required;
import gyro.core.validation.ValidStrings;
import gyro.google.Copyable;

public class DbSettings extends Diffable implements Copyable<Settings> {

    private String activationPolicy;
    private DbSqlActiveDirectoryConfig activeDirectoryConfig;
    private DbAdvancedMachineFeatures advancedMachineFeatures;
    private String availabilityType;
    private DbBackupConfiguration backupConfiguration;
    private String collation;
    private String connectorEnforcement;
    private Boolean crashSafeReplicationEnabled;
    private DbDataCacheConfig dataCacheConfig;
    private Long dataDiskSizeGb;
    private String dataDiskType;
    private List<DbDatabaseFlag> databaseFlags;
    private Boolean databaseReplicationEnabled;
    private Boolean deletionProtectionEnabled;
    private List<DbDenyMaintenancePeriod> denyMaintenancePeriods;
    private String edition;
    private Boolean enableDataplexIntegration;
    private Boolean enableGoogleMlIntegration;
    private DbInsightsConfig insightsConfig;
    private DbIpConfiguration ipConfiguration;
    private DbLocationPreference locationPreference;
    private DbMaintenanceWindow maintenanceWindow;
    private DbPasswordValidationPolicy passwordValidationPolicy;
    private String pricingPlan;
    private Long settingsVersion;
    private DbSqlServerAuditConfig sqlServerAuditConfig;
    private Boolean storageAutoResize;
    private Long storageAutoResizeLimit;
    private String tier;
    private String timeZone;
    private Map<String, String> userLabels;

    /**
     * The activation policy specifies when the instance is activated.
     */
    @Updatable
    @ValidStrings({ "ALWAYS", "NEVER" })
    public String getActivationPolicy() {
        return activationPolicy;
    }

    public void setActivationPolicy(String activationPolicy) {
        this.activationPolicy = activationPolicy;
    }

    /**
     * The active Directory configuration, relevant only for Cloud SQL for SQL Server.
     *
     * @subresource gyro.google.cloudsql.DbSqlActiveDirectoryConfig
     */
    @Updatable
    public DbSqlActiveDirectoryConfig getActiveDirectoryConfig() {
        return activeDirectoryConfig;
    }

    public void setActiveDirectoryConfig(DbSqlActiveDirectoryConfig activeDirectoryConfig) {
        this.activeDirectoryConfig = activeDirectoryConfig;
    }

    /**
     * The advanced machine configuration for the instances relevant only for SQL Server.
     *
     * @subresource gyro.google.cloudsql.DbAdvancedMachineFeatures
     */
    @Updatable
    public DbAdvancedMachineFeatures getAdvancedMachineFeatures() {
        return advancedMachineFeatures;
    }

    public void setAdvancedMachineFeatures(DbAdvancedMachineFeatures advancedMachineFeatures) {
        this.advancedMachineFeatures = advancedMachineFeatures;
    }

    /**
     * The availability type of the given Cloud SQL instance.
     */
    @Updatable
    @ValidStrings({ "ZONAL", "REGIONAL" })
    public String getAvailabilityType() {
        return availabilityType;
    }

    public void setAvailabilityType(String availabilityType) {
        this.availabilityType = availabilityType;
    }

    /**
     * The daily backup configuration for the instance.
     *
     * @subresource gyro.google.cloudsql.DbBackupConfiguration
     */
    @Updatable
    public DbBackupConfiguration getBackupConfiguration() {
        return backupConfiguration;
    }

    public void setBackupConfiguration(DbBackupConfiguration backupConfiguration) {
        this.backupConfiguration = backupConfiguration;
    }

    /**
     * The name of server Instance collation.
     */
    @Updatable
    public String getCollation() {
        return collation;
    }

    public void setCollation(String collation) {
        this.collation = collation;
    }

    /**
     * Specifies if connections must use Cloud SQL connectors.
     */
    @Updatable
    @ValidStrings({ "NOT_REQUIRED", "REQUIRED" })
    public String getConnectorEnforcement() {
        return connectorEnforcement;
    }

    public void setConnectorEnforcement(String connectorEnforcement) {
        this.connectorEnforcement = connectorEnforcement;
    }

    /**
     * When set to ``true``, database flags for crash- safe replication are enabled.
     */
    @Updatable
    public Boolean getCrashSafeReplicationEnabled() {
        return crashSafeReplicationEnabled;
    }

    public void setCrashSafeReplicationEnabled(Boolean crashSafeReplicationEnabled) {
        this.crashSafeReplicationEnabled = crashSafeReplicationEnabled;
    }

    /**
     * The configuration for data cache.
     *
     * @subresource gyro.google.cloudsql.DbDataCacheConfig
     */
    @Updatable
    public DbDataCacheConfig getDataCacheConfig() {
        return dataCacheConfig;
    }

    public void setDataCacheConfig(DbDataCacheConfig dataCacheConfig) {
        this.dataCacheConfig = dataCacheConfig;
    }

    /**
     * The size of data disk, in GB.
     */
    @Range(min = 10, max = 65536)
    @Updatable
    public Long getDataDiskSizeGb() {
        return dataDiskSizeGb;
    }

    public void setDataDiskSizeGb(Long dataDiskSizeGb) {
        this.dataDiskSizeGb = dataDiskSizeGb;
    }

    /**
     * The type of data disk.
     */
    @ValidStrings({ "PD_SSD", "PD_HDD" })
    public String getDataDiskType() {
        return dataDiskType;
    }

    public void setDataDiskType(String dataDiskType) {
        this.dataDiskType = dataDiskType;
    }

    /**
     * The database flags passed to the instance at startup.
     *
     * @subresource gyro.google.cloudsql.DbDatabaseFlag
     */
    @Updatable
    public List<DbDatabaseFlag> getDatabaseFlags() {
        if (databaseFlags == null) {
            databaseFlags = new ArrayList<>();
        }

        return databaseFlags;
    }

    public void setDatabaseFlags(List<DbDatabaseFlag> databaseFlags) {
        this.databaseFlags = databaseFlags;
    }

    /**
     * When set to ``true``, replication is enabled for databases.
     */
    @Updatable
    public Boolean getDatabaseReplicationEnabled() {
        return databaseReplicationEnabled;
    }

    public void setDatabaseReplicationEnabled(Boolean databaseReplicationEnabled) {
        this.databaseReplicationEnabled = databaseReplicationEnabled;
    }

    /**
     * When set to ``true``, protects against accidental instance deletion.
     */
    @Updatable
    public Boolean getDeletionProtectionEnabled() {
        return deletionProtectionEnabled;
    }

    public void setDeletionProtectionEnabled(Boolean deletionProtectionEnabled) {
        this.deletionProtectionEnabled = deletionProtectionEnabled;
    }

    /**
     * The date ranges during when all CSA rollout will be denied.
     *
     * @subresource gyro.google.cloudsql.DbDenyMaintenancePeriod
     */
    @Updatable
    public List<DbDenyMaintenancePeriod> getDenyMaintenancePeriods() {
        if (denyMaintenancePeriods == null) {
            denyMaintenancePeriods = new ArrayList<>();
        }

        return denyMaintenancePeriods;
    }

    public void setDenyMaintenancePeriods(List<DbDenyMaintenancePeriod> denyMaintenancePeriods) {
        this.denyMaintenancePeriods = denyMaintenancePeriods;
    }

    /**
     * The edition of the instance.
     */
    @ValidStrings({ "ENTERPRISE_PLUS", "ENTERPRISE" })
    @Updatable
    public String getEdition() {
        return edition;
    }

    public void setEdition(String edition) {
        this.edition = edition;
    }

    /**
     * When set to ``true``, schema extraction for Dataplex on Cloud SQL instances is activated.
     */
    @Updatable
    public Boolean getEnableDataplexIntegration() {
        return enableDataplexIntegration;
    }

    public void setEnableDataplexIntegration(Boolean enableDataplexIntegration) {
        this.enableDataplexIntegration = enableDataplexIntegration;
    }

    /**
     * When set to ``true``, Cloud SQL instances can connect to Vertex AI to pass requests for real-time predictions and
     * insights to the AI
     */
    @Updatable
    public Boolean getEnableGoogleMlIntegration() {
        return enableGoogleMlIntegration;
    }

    public void setEnableGoogleMlIntegration(Boolean enableGoogleMlIntegration) {
        this.enableGoogleMlIntegration = enableGoogleMlIntegration;
    }

    /**
     * The insights config.
     *
     * @subresource gyro.google.cloudsql.DbInsightsConfig
     */
    @Updatable
    public DbInsightsConfig getInsightsConfig() {
        return insightsConfig;
    }

    public void setInsightsConfig(DbInsightsConfig insightsConfig) {
        this.insightsConfig = insightsConfig;
    }

    /**
     * The settings for IP Management. This allows to enable or disable the instance IP and manage which external
     * networks can connect to the instance.
     *
     * @subresource gyro.google.cloudsql.DbIpConfiguration
     */
    @Updatable
    public DbIpConfiguration getIpConfiguration() {
        return ipConfiguration;
    }

    public void setIpConfiguration(DbIpConfiguration ipConfiguration) {
        this.ipConfiguration = ipConfiguration;
    }

    /**
     * The location preference settings.
     *
     * @subresource gyro.google.cloudsql.DbLocationPreference
     */
    @Updatable
    public DbLocationPreference getLocationPreference() {
        return locationPreference;
    }

    public void setLocationPreference(DbLocationPreference locationPreference) {
        this.locationPreference = locationPreference;
    }

    /**
     * The maintenance window for this instance. This specifies when the instance can be restarted for maintenance
     * purposes.
     *
     * @subresource gyro.google.cloudsql.DbMaintenanceWindow
     */
    @Updatable
    public DbMaintenanceWindow getMaintenanceWindow() {
        return maintenanceWindow;
    }

    public void setMaintenanceWindow(DbMaintenanceWindow maintenanceWindow) {
        this.maintenanceWindow = maintenanceWindow;
    }

    /**
     * The local user password validation policy of the instance.
     *
     * @subresource gyro.google.cloudsql.DbPasswordValidationPolicy
     */
    @Updatable
    public DbPasswordValidationPolicy getPasswordValidationPolicy() {
        return passwordValidationPolicy;
    }

    public void setPasswordValidationPolicy(DbPasswordValidationPolicy passwordValidationPolicy) {
        this.passwordValidationPolicy = passwordValidationPolicy;
    }

    /**
     * The pricing plan for this instance.
     */
    @ValidStrings({ "PER_USE", "PACKAGE" })
    @Updatable
    public String getPricingPlan() {
        return pricingPlan;
    }

    public void setPricingPlan(String pricingPlan) {
        this.pricingPlan = pricingPlan;
    }

    /**
     * The configuration for SQL Server audit logging.
     *
     * @subresource gyro.google.cloudsql.DbSqlServerAuditConfig
     */
    @Updatable
    public DbSqlServerAuditConfig getSqlServerAuditConfig() {
        return sqlServerAuditConfig;
    }

    public void setSqlServerAuditConfig(DbSqlServerAuditConfig sqlServerAuditConfig) {
        this.sqlServerAuditConfig = sqlServerAuditConfig;
    }

    /**
     * When set to ``true``, storage size increases automatically.
     */
    @Updatable
    public Boolean getStorageAutoResize() {
        return storageAutoResize;
    }

    public void setStorageAutoResize(Boolean storageAutoResize) {
        this.storageAutoResize = storageAutoResize;
    }

    /**
     * The maximum size to which storage capacity can be automatically increased.
     */
    @Updatable
    public Long getStorageAutoResizeLimit() {
        return storageAutoResizeLimit;
    }

    public void setStorageAutoResizeLimit(Long storageAutoResizeLimit) {
        this.storageAutoResizeLimit = storageAutoResizeLimit;
    }

    /**
     * The tier (or machine type) for this instance.
     */
    @Updatable
    @Required
    public String getTier() {
        return tier;
    }

    public void setTier(String tier) {
        this.tier = tier;
    }

    /**
     * The server timezone of the instance.
     */
    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    /**
     * The user labels for the instance.
     */
    @Updatable
    public Map<String, String> getUserLabels() {
        if (userLabels == null) {
            userLabels = new HashMap<>();
        }

        return userLabels;
    }

    public void setUserLabels(Map<String, String> userLabels) {
        this.userLabels = userLabels;
    }

    /**
     * The settings version of the instance.
     */
    @Output
    public Long getSettingsVersion() {
        return settingsVersion;
    }

    public void setSettingsVersion(Long settingsVersion) {
        this.settingsVersion = settingsVersion;
    }

    @Override
    public void copyFrom(Settings model) throws Exception {
        setActivationPolicy(model.getActivationPolicy());
        setAvailabilityType(model.getAvailabilityType());
        setCollation(model.getCollation());
        setConnectorEnforcement(model.getConnectorEnforcement());
        setCrashSafeReplicationEnabled(model.getCrashSafeReplicationEnabled());
        setDataDiskSizeGb(model.getDataDiskSizeGb());
        setDataDiskType(model.getDataDiskType());
        setDatabaseReplicationEnabled(model.getDatabaseReplicationEnabled());
        setDeletionProtectionEnabled(model.getDeletionProtectionEnabled());
        setEdition(model.getEdition());
        setEnableDataplexIntegration(model.getEnableDataplexIntegration());
        setEnableGoogleMlIntegration(model.getEnableGoogleMlIntegration());
        setPricingPlan(model.getPricingPlan());
        setStorageAutoResize(model.getStorageAutoResize());
        setStorageAutoResizeLimit(model.getStorageAutoResizeLimit());
        setTier(model.getTier());
        setTimeZone(model.getTimeZone());
        setUserLabels(model.getUserLabels());
        setSettingsVersion(model.getSettingsVersion());

        setSqlServerAuditConfig(null);
        if (model.getSqlServerAuditConfig() != null) {
            DbSqlServerAuditConfig config = newSubresource(DbSqlServerAuditConfig.class);
            config.copyFrom(model.getSqlServerAuditConfig());
            setSqlServerAuditConfig(config);
        }

        setActiveDirectoryConfig(null);
        if (model.getActiveDirectoryConfig() != null) {
            DbSqlActiveDirectoryConfig config = newSubresource(DbSqlActiveDirectoryConfig.class);
            config.copyFrom(model.getActiveDirectoryConfig());
            setActiveDirectoryConfig(config);
        }

        setAdvancedMachineFeatures(null);
        if (model.getAdvancedMachineFeatures() != null) {
            DbAdvancedMachineFeatures config = newSubresource(DbAdvancedMachineFeatures.class);
            config.copyFrom(model.getAdvancedMachineFeatures());
            setAdvancedMachineFeatures(config);
        }

        setBackupConfiguration(null);
        if (model.getBackupConfiguration() != null) {
            DbBackupConfiguration config = newSubresource(DbBackupConfiguration.class);
            config.copyFrom(model.getBackupConfiguration());
            setBackupConfiguration(config);
        }

        setDataCacheConfig(null);
        if (model.getDataCacheConfig() != null) {
            DbDataCacheConfig config = newSubresource(DbDataCacheConfig.class);
            config.copyFrom(model.getDataCacheConfig());
            setDataCacheConfig(config);
        }

        setInsightsConfig(null);
        if (model.getInsightsConfig() != null) {
            DbInsightsConfig config = newSubresource(DbInsightsConfig.class);
            config.copyFrom(model.getInsightsConfig());
            setInsightsConfig(config);
        }

        setIpConfiguration(null);
        if (model.getIpConfiguration() != null) {
            DbIpConfiguration config = newSubresource(DbIpConfiguration.class);
            config.copyFrom(model.getIpConfiguration());
            setIpConfiguration(config);
        }

        setLocationPreference(null);
        if (model.getLocationPreference() != null) {
            DbLocationPreference config = newSubresource(DbLocationPreference.class);
            config.copyFrom(model.getLocationPreference());
            setLocationPreference(config);
        }

        setMaintenanceWindow(null);
        if (model.getMaintenanceWindow() != null) {
            DbMaintenanceWindow config = newSubresource(DbMaintenanceWindow.class);
            config.copyFrom(model.getMaintenanceWindow());
            setMaintenanceWindow(config);
        }

        setPasswordValidationPolicy(null);
        if (model.getPasswordValidationPolicy() != null) {
            DbPasswordValidationPolicy config = newSubresource(DbPasswordValidationPolicy.class);
            config.copyFrom(model.getPasswordValidationPolicy());
            setPasswordValidationPolicy(config);
        }

        getDatabaseFlags().clear();
        if (model.getDatabaseFlags() != null) {
            for (DatabaseFlags flag : model.getDatabaseFlags()) {
                DbDatabaseFlag databaseFlag = newSubresource(DbDatabaseFlag.class);
                databaseFlag.copyFrom(flag);
                getDatabaseFlags().add(databaseFlag);
            }
        }

        getDenyMaintenancePeriods().clear();
        if (model.getDenyMaintenancePeriods() != null) {
            for (DenyMaintenancePeriod period : model.getDenyMaintenancePeriods()) {
                DbDenyMaintenancePeriod denyMaintenancePeriod = newSubresource(DbDenyMaintenancePeriod.class);
                denyMaintenancePeriod.copyFrom(period);
                getDenyMaintenancePeriods().add(denyMaintenancePeriod);
            }
        }
    }

    @Override
    public String primaryKey() {
        return "";
    }

    public Settings toSettings() {
        Settings settings = new Settings();
        settings.setSettingsVersion(getSettingsVersion());

        if (getActivationPolicy() != null) {
            settings.setActivationPolicy(getActivationPolicy());
        }

        if (getActiveDirectoryConfig() != null) {
            settings.setActiveDirectoryConfig(getActiveDirectoryConfig().toSqlActiveDirectoryConfig());
        }

        if (getAdvancedMachineFeatures() != null) {
            settings.setAdvancedMachineFeatures(getAdvancedMachineFeatures().toAdvancedMachineFeatures());
        }

        if (getAvailabilityType() != null) {
            settings.setAvailabilityType(getAvailabilityType());
        }

        if (getBackupConfiguration() != null) {
            settings.setBackupConfiguration(getBackupConfiguration().toBackupConfiguration());
        }

        if (getCollation() != null) {
            settings.setCollation(getCollation());
        }

        if (getConnectorEnforcement() != null) {
            settings.setConnectorEnforcement(getConnectorEnforcement());
        }

        if (getCrashSafeReplicationEnabled() != null) {
            settings.setCrashSafeReplicationEnabled(getCrashSafeReplicationEnabled());
        }

        if (getDataCacheConfig() != null) {
            settings.setDataCacheConfig(getDataCacheConfig().toDataCacheConfig());
        }

        if (getDataDiskSizeGb() != null) {
            settings.setDataDiskSizeGb(getDataDiskSizeGb());
        }

        if (getDataDiskType() != null) {
            settings.setDataDiskType(getDataDiskType());
        }

        if (getDatabaseFlags() != null) {
            List<DatabaseFlags> flags = new ArrayList<>();
            for (DbDatabaseFlag flag : getDatabaseFlags()) {
                flags.add(flag.toDatabaseFlags());
            }

            settings.setDatabaseFlags(flags);
        }

        if (getDatabaseReplicationEnabled() != null) {
            settings.setDatabaseReplicationEnabled(getDatabaseReplicationEnabled());
        }

        if (getDeletionProtectionEnabled() != null) {
            settings.setDeletionProtectionEnabled(getDeletionProtectionEnabled());
        }

        if (getDenyMaintenancePeriods() != null) {
            List<DenyMaintenancePeriod> periods = new ArrayList<>();
            for (DbDenyMaintenancePeriod period : getDenyMaintenancePeriods()) {
                periods.add(period.toDenyMaintenancePeriod());
            }

            settings.setDenyMaintenancePeriods(periods);
        }

        if (getEdition() != null) {
            settings.setEdition(getEdition());
        }

        if (getEnableDataplexIntegration() != null) {
            settings.setEnableDataplexIntegration(getEnableDataplexIntegration());
        }

        if (getEnableGoogleMlIntegration() != null) {
            settings.setEnableGoogleMlIntegration(getEnableGoogleMlIntegration());
        }

        if (getInsightsConfig() != null) {
            settings.setInsightsConfig(getInsightsConfig().toInsightsConfig());
        }

        if (getIpConfiguration() != null) {
            settings.setIpConfiguration(getIpConfiguration().toIpConfiguration());
        }

        if (getLocationPreference() != null) {
            settings.setLocationPreference(getLocationPreference().toLocationPreference());
        }

        if (getMaintenanceWindow() != null) {
            settings.setMaintenanceWindow(getMaintenanceWindow().toMaintenanceWindow());
        }

        if (getPasswordValidationPolicy() != null) {
            settings.setPasswordValidationPolicy(getPasswordValidationPolicy().toPasswordValidationPolicy());
        }

        if (getPricingPlan() != null) {
            settings.setPricingPlan(getPricingPlan());
        }

        if (getSqlServerAuditConfig() != null) {
            settings.setSqlServerAuditConfig(getSqlServerAuditConfig().toSqlServerAuditConfig());
        }

        if (getStorageAutoResize() != null) {
            settings.setStorageAutoResize(getStorageAutoResize());
        }

        if (getStorageAutoResizeLimit() != null) {
            settings.setStorageAutoResizeLimit(getStorageAutoResizeLimit());
        }

        if (getTier() != null) {
            settings.setTier(getTier());
        }

        if (getTimeZone() != null) {
            settings.setTimeZone(getTimeZone());
        }

        if (getUserLabels() != null) {
            settings.setUserLabels(getUserLabels());
        }

        return settings;
    }
}
