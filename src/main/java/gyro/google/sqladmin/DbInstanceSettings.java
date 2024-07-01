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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import gyro.core.resource.Diffable;
import gyro.core.validation.ValidStrings;
import gyro.google.Copyable;

public class DbInstanceSettings extends Diffable implements Copyable<com.google.api.services.sqladmin.model.Settings> {

    private String activationPolicy;

    private SqlActiveDirectoryConfig activeDirectoryConfig;

    private AdvancedMachineFeatures advancedMachineFeatures;

    private List<String> authorizedGaeApplications;

    private String availabilityType;

    private BackupConfiguration backupConfiguration;

    private String collation;

    private String connectorEnforcement;

    private Boolean crashSafeReplicationEnabled;

    private List<DatabaseFlags> databaseFlag;

    private Boolean databaseReplicationEnabled;

    private DataCacheConfig dataCacheConfig;

    private Long dataDiskSizeGb;

    private String dataDiskType;

    private Boolean deletionProtectionEnabled;

    private List<DenyMaintenancePeriod> denyMaintenancePeriod;

    private String edition;

    private Boolean enableGoogleMlIntegration;

    private InsightsConfig insightsConfig;

    private IpConfiguration ipConfiguration;

    private LocationPreference locationPreference;

    private MaintenanceWindow maintenanceWindow;

    private PasswordValidationPolicy passwordValidationPolicy;

    private String pricingPlan;

    private String replicationType;

    private Long settingsVersion;

    private SqlServerAuditConfig sqlServerAuditConfig;

    private Boolean storageAutoResize;

    private Long storageAutoResizeLimit;

    private String tier;

    private String timeZone;

    private Map<String, String> userLabels;

    /**
     * The activation policy specifies when the instance is activated; it is applicable only when the instance state is RUNNABLE. Valid values: * `ALWAYS`: The instance is on, and remains so even in the absence of connection requests. * `NEVER`: The instance is off; it is not activated, even if a connection request arrives.
     */
    @ValidStrings({
        "SQL_ACTIVATION_POLICY_UNSPECIFIED",
        "ALWAYS",
        "NEVER",
        "ON_DEMAND"
    })
    public String getActivationPolicy() {
        return activationPolicy;
    }

    public void setActivationPolicy(String activationPolicy) {
        this.activationPolicy = activationPolicy;
    }

    /**
     * Active Directory configuration, relevant only for Cloud SQL for SQL Server.
     *
     * @subresource gyro.google.sqladmin.SqlActiveDirectoryConfig
     */
    public SqlActiveDirectoryConfig getActiveDirectoryConfig() {
        return activeDirectoryConfig;
    }

    public void setActiveDirectoryConfig(SqlActiveDirectoryConfig activeDirectoryConfig) {
        this.activeDirectoryConfig = activeDirectoryConfig;
    }

    /**
     * Specifies advanced machine configuration for the instances relevant only for SQL Server.
     *
     * @subresource gyro.google.sqladmin.AdvancedMachineFeatures
     */
    public AdvancedMachineFeatures getAdvancedMachineFeatures() {
        return advancedMachineFeatures;
    }

    public void setAdvancedMachineFeatures(AdvancedMachineFeatures advancedMachineFeatures) {
        this.advancedMachineFeatures = advancedMachineFeatures;
    }

    /**
     * The App Engine app IDs that can access this instance. (Deprecated) Applied to First Generation instances only.
     */
    public List<String> getAuthorizedGaeApplications() {
        if (authorizedGaeApplications == null) {
            authorizedGaeApplications = new ArrayList<>();
        }

        return authorizedGaeApplications;
    }

    public void setAuthorizedGaeApplications(List<String> authorizedGaeApplications) {
        this.authorizedGaeApplications = authorizedGaeApplications;
    }

    /**
     * Availability type. Potential values: * `ZONAL`: The instance serves data from only one zone. Outages in that zone affect data accessibility. * `REGIONAL`: The instance can serve data from more than one zone in a region (it is highly available)./ For more information, see [Overview of the High Availability Configuration](https://cloud.google.com/sql/docs/mysql/high-availability).
     */
    @ValidStrings({
        "SQL_AVAILABILITY_TYPE_UNSPECIFIED",
        "ZONAL",
        "REGIONAL"
    })
    public String getAvailabilityType() {
        return availabilityType;
    }

    public void setAvailabilityType(String availabilityType) {
        this.availabilityType = availabilityType;
    }

    /**
     * The daily backup configuration for the instance.
     *
     * @subresource gyro.google.sqladmin.BackupConfiguration
     */
    public BackupConfiguration getBackupConfiguration() {
        return backupConfiguration;
    }

    public void setBackupConfiguration(BackupConfiguration backupConfiguration) {
        this.backupConfiguration = backupConfiguration;
    }

    /**
     * The name of server Instance collation.
     */
    public String getCollation() {
        return collation;
    }

    public void setCollation(String collation) {
        this.collation = collation;
    }

    /**
     * Specifies if connections must use Cloud SQL connectors. Option values include the following: `NOT_REQUIRED` (Cloud SQL instances can be connected without Cloud SQL Connectors) and `REQUIRED` (Only allow connections that use Cloud SQL Connectors). Note that using REQUIRED disables all existing authorized networks. If this field is not specified when creating a new instance, NOT_REQUIRED is used. If this field is not specified when patching or updating an existing instance, it is left unchanged in the instance.
     */
    @ValidStrings({
        "CONNECTOR_ENFORCEMENT_UNSPECIFIED",
        "NOT_REQUIRED",
        "REQUIRED"
    })
    public String getConnectorEnforcement() {
        return connectorEnforcement;
    }

    public void setConnectorEnforcement(String connectorEnforcement) {
        this.connectorEnforcement = connectorEnforcement;
    }

    /**
     * Configuration specific to read replica instances. Indicates whether database flags for crash-safe replication are enabled. This property was only applicable to First Generation instances.
     */
    public Boolean getCrashSafeReplicationEnabled() {
        return crashSafeReplicationEnabled;
    }

    public void setCrashSafeReplicationEnabled(Boolean crashSafeReplicationEnabled) {
        this.crashSafeReplicationEnabled = crashSafeReplicationEnabled;
    }

    /**
     * The database flags passed to the instance at startup.
     *
     * @subresource gyro.google.sqladmin.DatabaseFlags
     */
    public List<DatabaseFlags> getDatabaseFlag() {
        if (databaseFlag == null) {
            databaseFlag = new ArrayList<>();
        }

        return databaseFlag;
    }

    public void setDatabaseFlag(List<DatabaseFlags> databaseFlag) {
        this.databaseFlag = databaseFlag;
    }

    /**
     * Configuration specific to read replica instances. Indicates whether replication is enabled or not. WARNING: Changing this restarts the instance.
     */
    public Boolean getDatabaseReplicationEnabled() {
        return databaseReplicationEnabled;
    }

    public void setDatabaseReplicationEnabled(Boolean databaseReplicationEnabled) {
        this.databaseReplicationEnabled = databaseReplicationEnabled;
    }

    /**
     * Configuration for data cache.
     *
     * @subresource gyro.google.sqladmin.DataCacheConfig
     */
    public DataCacheConfig getDataCacheConfig() {
        return dataCacheConfig;
    }

    public void setDataCacheConfig(DataCacheConfig dataCacheConfig) {
        this.dataCacheConfig = dataCacheConfig;
    }

    /**
     * The size of data disk, in GB. The data disk size minimum is 10GB.
     */
    public Long getDataDiskSizeGb() {
        return dataDiskSizeGb;
    }

    public void setDataDiskSizeGb(Long dataDiskSizeGb) {
        this.dataDiskSizeGb = dataDiskSizeGb;
    }

    /**
     * The type of data disk: `PD_SSD` (default) or `PD_HDD`. Not used for First Generation instances.
     */
    @ValidStrings({
        "SQL_DATA_DISK_TYPE_UNSPECIFIED",
        "PD_SSD",
        "PD_HDD",
        "OBSOLETE_LOCAL_SSD"
    })
    public String getDataDiskType() {
        return dataDiskType;
    }

    public void setDataDiskType(String dataDiskType) {
        this.dataDiskType = dataDiskType;
    }

    /**
     * Configuration to protect against accidental instance deletion.
     */
    public Boolean getDeletionProtectionEnabled() {
        return deletionProtectionEnabled;
    }

    public void setDeletionProtectionEnabled(Boolean deletionProtectionEnabled) {
        this.deletionProtectionEnabled = deletionProtectionEnabled;
    }

    /**
     * A list of Deny maintenance periods
     *
     * @subresource gyro.google.sqladmin.DenyMaintenancePeriod
     */
    public List<DenyMaintenancePeriod> getDenyMaintenancePeriod() {
        if (denyMaintenancePeriod == null) {
            denyMaintenancePeriod = new ArrayList<>();
        }

        return denyMaintenancePeriod;
    }

    public void setDenyMaintenancePeriod(List<DenyMaintenancePeriod> denyMaintenancePeriod) {
        this.denyMaintenancePeriod = denyMaintenancePeriod;
    }

    /**
     * Optional. The edition of the instance.
     */
    @ValidStrings({
        "EDITION_UNSPECIFIED",
        "ENTERPRISE",
        "ENTERPRISE_PLUS"
    })
    public String getEdition() {
        return edition;
    }

    public void setEdition(String edition) {
        this.edition = edition;
    }

    /**
     * Optional. When this parameter is set to true, Cloud SQL instances can connect to Vertex AI to pass requests for real-time predictions and insights to the AI. The default value is false. This applies only to Cloud SQL for PostgreSQL instances.
     */
    public Boolean getEnableGoogleMlIntegration() {
        return enableGoogleMlIntegration;
    }

    public void setEnableGoogleMlIntegration(Boolean enableGoogleMlIntegration) {
        this.enableGoogleMlIntegration = enableGoogleMlIntegration;
    }

    /**
     * Insights configuration, for now relevant only for Postgres.
     */
    public InsightsConfig getInsightsConfig() {
        return insightsConfig;
    }

    public void setInsightsConfig(InsightsConfig insightsConfig) {
        this.insightsConfig = insightsConfig;
    }

    /**
     * The settings for IP Management. This allows to enable or disable the instance IP and manage which external networks can connect to the instance. The IPv4 address cannot be disabled for Second Generation instances.
     *
     * @subresource gyro.google.sqladmin.IpConfiguration
     */
    public IpConfiguration getIpConfiguration() {
        return ipConfiguration;
    }

    public void setIpConfiguration(IpConfiguration ipConfiguration) {
        this.ipConfiguration = ipConfiguration;
    }

    /**
     * The location preference settings. This allows the instance to be located as near as possible to either an App Engine app or Compute Engine zone for better performance. App Engine co-location was only applicable to First Generation instances.
     *
     * @subresource gyro.google.sqladmin.LocationPreference
     */
    public LocationPreference getLocationPreference() {
        return locationPreference;
    }

    public void setLocationPreference(LocationPreference locationPreference) {
        this.locationPreference = locationPreference;
    }

    /**
     * The maintenance window for this instance. This specifies when the instance can be restarted for maintenance purposes.
     *
     * @subresource gyro.google.sqladmin.MaintenanceWindow
     */
    public MaintenanceWindow getMaintenanceWindow() {
        return maintenanceWindow;
    }

    public void setMaintenanceWindow(MaintenanceWindow maintenanceWindow) {
        this.maintenanceWindow = maintenanceWindow;
    }

    /**
     * The local user password validation policy of the instance.
     *
     * @subresource gyro.google.sqladmin.PasswordValidationPolicy
     */
    public PasswordValidationPolicy getPasswordValidationPolicy() {
        return passwordValidationPolicy;
    }

    public void setPasswordValidationPolicy(PasswordValidationPolicy passwordValidationPolicy) {
        this.passwordValidationPolicy = passwordValidationPolicy;
    }

    /**
     * The pricing plan for this instance. This can be either `PER_USE` or `PACKAGE`. Only `PER_USE` is supported for Second Generation instances.
     */
    @ValidStrings({
        "SQL_PRICING_PLAN_UNSPECIFIED",
        "PACKAGE",
        "PER_USE"
    })
    public String getPricingPlan() {
        return pricingPlan;
    }

    public void setPricingPlan(String pricingPlan) {
        this.pricingPlan = pricingPlan;
    }

    /**
     * The type of replication this instance uses. This can be either `ASYNCHRONOUS` or `SYNCHRONOUS`. (Deprecated) This property was only applicable to First Generation instances.
     */
    @ValidStrings({
        "SQL_REPLICATION_TYPE_UNSPECIFIED",
        "SYNCHRONOUS",
        "ASYNCHRONOUS"
    })
    public String getReplicationType() {
        return replicationType;
    }

    public void setReplicationType(String replicationType) {
        this.replicationType = replicationType;
    }

    /**
     * The version of instance settings. This is a required field for update method to make sure concurrent updates are handled properly. During update, use the most recent settingsVersion value for this instance and do not try to update this value.
     */
    public Long getSettingsVersion() {
        return settingsVersion;
    }

    public void setSettingsVersion(Long settingsVersion) {
        this.settingsVersion = settingsVersion;
    }

    /**
     * SQL Server specific audit configuration.
     *
     * @subresource gyro.google.sqladmin.SqlServerAuditConfig
     */
    public SqlServerAuditConfig getSqlServerAuditConfig() {
        return sqlServerAuditConfig;
    }

    public void setSqlServerAuditConfig(SqlServerAuditConfig sqlServerAuditConfig) {
        this.sqlServerAuditConfig = sqlServerAuditConfig;
    }

    /**
     * Configuration to increase storage size automatically. The default value is true.
     */
    public Boolean getStorageAutoResize() {
        return storageAutoResize;
    }

    public void setStorageAutoResize(Boolean storageAutoResize) {
        this.storageAutoResize = storageAutoResize;
    }

    /**
     * The maximum size to which storage capacity can be automatically increased. The default value is 0, which specifies that there is no limit.
     */
    public Long getStorageAutoResizeLimit() {
        return storageAutoResizeLimit;
    }

    public void setStorageAutoResizeLimit(Long storageAutoResizeLimit) {
        this.storageAutoResizeLimit = storageAutoResizeLimit;
    }

    /**
     * The tier (or machine type) for this instance, for example `db-custom-1-3840`. WARNING: Changing this restarts the instance.
     */
    public String getTier() {
        return tier;
    }

    public void setTier(String tier) {
        this.tier = tier;
    }

    /**
     * Server timezone, relevant only for Cloud SQL for SQL Server.
     */
    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    /**
     * User-provided labels, represented as a dictionary where each label is a single key value pair.
     */
    public Map<String, String> getUserLabels() {
        if (userLabels == null) {
            userLabels = new HashMap<>();
        }

        return userLabels;
    }

    public void setUserLabels(Map<String, String> userLabels) {
        this.userLabels = userLabels;
    }

    @Override
    public String primaryKey() {
        return "";
    }

    @Override
    public void copyFrom(com.google.api.services.sqladmin.model.Settings model) {
        setActivationPolicy(model.getActivationPolicy());
        setAuthorizedGaeApplications(model.getAuthorizedGaeApplications());
        setAvailabilityType(model.getAvailabilityType());
        setCollation(model.getCollation());
        setConnectorEnforcement(model.getConnectorEnforcement());
        setCrashSafeReplicationEnabled(model.getCrashSafeReplicationEnabled());
        setDatabaseReplicationEnabled(model.getDatabaseReplicationEnabled());
        setDataDiskType(model.getDataDiskType());
        setDeletionProtectionEnabled(model.getDeletionProtectionEnabled());
        setEdition(model.getEdition());
        setEnableGoogleMlIntegration(model.getEnableGoogleMlIntegration());
        setPricingPlan(model.getPricingPlan());
        setReplicationType(model.getReplicationType());
        setStorageAutoResize(model.getStorageAutoResize());
        setTier(model.getTier());
        setTimeZone(model.getTimeZone());
        setUserLabels(model.getUserLabels());
        setDataDiskSizeGb(model.getDataDiskSizeGb());
        setSettingsVersion(model.getSettingsVersion());
        setStorageAutoResizeLimit(model.getStorageAutoResizeLimit());

        setAdvancedMachineFeatures(null);
        if (model.getAdvancedMachineFeatures() != null) {
            AdvancedMachineFeatures advancedMachineFeatures = new AdvancedMachineFeatures();
            advancedMachineFeatures.copyFrom(model.getAdvancedMachineFeatures());
            setAdvancedMachineFeatures(advancedMachineFeatures);
        }

        setActiveDirectoryConfig(null);
        if (model.getActiveDirectoryConfig() != null) {
            SqlActiveDirectoryConfig activeDirectoryConfig = new SqlActiveDirectoryConfig();
            activeDirectoryConfig.copyFrom(model.getActiveDirectoryConfig());
            setActiveDirectoryConfig(activeDirectoryConfig);
        }

        setBackupConfiguration(null);
        if (model.getBackupConfiguration() != null) {
            BackupConfiguration backupConfiguration = new BackupConfiguration();
            backupConfiguration.copyFrom(model.getBackupConfiguration());
            setBackupConfiguration(backupConfiguration);
        }

        setDatabaseFlag(null);
        if (model.getDatabaseFlags() != null) {
            model.getDatabaseFlags().forEach(databaseFlag -> {
                DatabaseFlags databaseFlags = new DatabaseFlags();
                databaseFlags.copyFrom(databaseFlag);
                getDatabaseFlag().add(databaseFlags);
            });
        }

        setDataCacheConfig(null);
        if (model.getDataCacheConfig() != null) {
            DataCacheConfig dataCacheConfig = new DataCacheConfig();
            dataCacheConfig.copyFrom(model.getDataCacheConfig());
            setDataCacheConfig(dataCacheConfig);
        }

        setInsightsConfig(null);
        if (model.getInsightsConfig() != null) {
            InsightsConfig insightsConfig = new InsightsConfig();
            insightsConfig.copyFrom(model.getInsightsConfig());
            setInsightsConfig(insightsConfig);
        }

        setIpConfiguration(null);
        if (model.getIpConfiguration() != null) {
            IpConfiguration ipConfiguration = new IpConfiguration();
            ipConfiguration.copyFrom(model.getIpConfiguration());
            setIpConfiguration(ipConfiguration);
        }

        setLocationPreference(null);
        if (model.getLocationPreference() != null) {
            LocationPreference locationPreference = new LocationPreference();
            locationPreference.copyFrom(model.getLocationPreference());
            setLocationPreference(locationPreference);
        }

        setMaintenanceWindow(null);
        if (model.getMaintenanceWindow() != null) {
            MaintenanceWindow maintenanceWindow = new MaintenanceWindow();
            maintenanceWindow.copyFrom(model.getMaintenanceWindow());
            setMaintenanceWindow(maintenanceWindow);
        }

        setPasswordValidationPolicy(null);
        if (model.getPasswordValidationPolicy() != null) {
            PasswordValidationPolicy passwordValidationPolicy = new PasswordValidationPolicy();
            passwordValidationPolicy.copyFrom(model.getPasswordValidationPolicy());
            setPasswordValidationPolicy(passwordValidationPolicy);
        }

        setSqlServerAuditConfig(null);
        if (model.getSqlServerAuditConfig() != null) {
            SqlServerAuditConfig sqlServerAuditConfig = new SqlServerAuditConfig();
            sqlServerAuditConfig.copyFrom(model.getSqlServerAuditConfig());
            setSqlServerAuditConfig(sqlServerAuditConfig);
        }

        setDenyMaintenancePeriod(null);
        if (model.getDenyMaintenancePeriods() != null) {
            model.getDenyMaintenancePeriods().forEach(denyMaintenancePeriod -> {
                DenyMaintenancePeriod denyMaintenancePeriod1 = new DenyMaintenancePeriod();
                denyMaintenancePeriod1.copyFrom(denyMaintenancePeriod);
                getDenyMaintenancePeriod().add(denyMaintenancePeriod1);
            });
        }
    }

    public com.google.api.services.sqladmin.model.Settings toSettings() {
        com.google.api.services.sqladmin.model.Settings settings = new com.google.api.services.sqladmin.model.Settings();

        settings.setActivationPolicy(getActivationPolicy());
        settings.setAuthorizedGaeApplications(getAuthorizedGaeApplications());
        settings.setAvailabilityType(getAvailabilityType());
        settings.setCollation(getCollation());
        settings.setConnectorEnforcement(getConnectorEnforcement());
        settings.setCrashSafeReplicationEnabled(getCrashSafeReplicationEnabled());
        settings.setDatabaseReplicationEnabled(getDatabaseReplicationEnabled());
        settings.setDataDiskType(getDataDiskType());
        settings.setDeletionProtectionEnabled(getDeletionProtectionEnabled());
        settings.setEdition(getEdition());
        settings.setEnableGoogleMlIntegration(getEnableGoogleMlIntegration());
        settings.setPricingPlan(getPricingPlan());
        settings.setReplicationType(getReplicationType());
        settings.setStorageAutoResize(getStorageAutoResize());
        settings.setTier(getTier());
        settings.setTimeZone(getTimeZone());
        settings.setUserLabels(getUserLabels());
        settings.setDataDiskSizeGb(getDataDiskSizeGb());
        settings.setSettingsVersion(getSettingsVersion());
        settings.setStorageAutoResizeLimit(getStorageAutoResizeLimit());

        if (getAdvancedMachineFeatures() != null) {
            settings.setAdvancedMachineFeatures(getAdvancedMachineFeatures().toAdvancedMachineFeatures());
        }

        if (getActiveDirectoryConfig() != null) {
            settings.setActiveDirectoryConfig(getActiveDirectoryConfig().toSqlActiveDirectoryConfig());
        }

        if (getBackupConfiguration() != null) {
            settings.setBackupConfiguration(getBackupConfiguration().toBackupConfiguration());
        }

        if (getDatabaseFlag() != null) {
            List<com.google.api.services.sqladmin.model.DatabaseFlags> databaseFlags = new ArrayList<>();
            getDatabaseFlag().forEach(databaseFlags1 -> databaseFlags.add(databaseFlags1.toDatabaseFlags()));
            settings.setDatabaseFlags(databaseFlags);
        }

        if (getDataCacheConfig() != null) {
            settings.setDataCacheConfig(getDataCacheConfig().toDataCacheConfig());
        }

        if (getInsightsConfig() != null) {
            settings.setInsightsConfig(getInsightsConfig().toInsightsConfig());
        }

        if (getIpConfiguration() != null) {
            settings.setIpConfiguration(getIpConfiguration().copyTo());
        }

        if (getLocationPreference() != null) {
            settings.setLocationPreference(getLocationPreference().copyTo());
        }

        if (getMaintenanceWindow() != null) {
            settings.setMaintenanceWindow(getMaintenanceWindow().copyTo());
        }

        if (getPasswordValidationPolicy() != null) {
            settings.setPasswordValidationPolicy(getPasswordValidationPolicy().toPasswordValidationPolicy());
        }

        if (getSqlServerAuditConfig() != null) {
            settings.setSqlServerAuditConfig(getSqlServerAuditConfig().copyTo());
        }

        if (getDenyMaintenancePeriod() != null) {
            List<com.google.api.services.sqladmin.model.DenyMaintenancePeriod> denyMaintenancePeriods = new ArrayList<>();
            getDenyMaintenancePeriod().forEach(denyMaintenancePeriod -> denyMaintenancePeriods.add(denyMaintenancePeriod.toDenyMaintenancePeriod()));
            settings.setDenyMaintenancePeriods(denyMaintenancePeriods);
        }

        return settings;
    }
}
