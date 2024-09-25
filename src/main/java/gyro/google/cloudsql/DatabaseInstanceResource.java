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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.sqladmin.SQLAdmin;
import com.google.api.services.sqladmin.model.DatabaseInstance;
import com.google.api.services.sqladmin.model.Operation;
import com.google.api.services.sqladmin.model.OperationError;
import com.google.api.services.sqladmin.model.OperationErrors;
import com.google.api.services.sqladmin.model.Settings;
import com.psddev.dari.util.StringUtils;
import gyro.core.GyroException;
import gyro.core.GyroUI;
import gyro.core.TimeoutSettings;
import gyro.core.Type;
import gyro.core.Waiter;
import gyro.core.resource.Id;
import gyro.core.resource.Output;
import gyro.core.resource.Resource;
import gyro.core.resource.Updatable;
import gyro.core.scope.State;
import gyro.core.validation.Required;
import gyro.core.validation.ValidStrings;
import gyro.google.Copyable;
import gyro.google.GoogleResource;

/**
 * Creates a database instance.
 *
 * Example
 * -------
 *
 * .. code-block:: gyro
 *
 *    google::database-instance database-instance-example
 *        name: "gyro-db-test"
 *
 *        settings
 *            activation-policy: "ALWAYS"
 *            availability-type: "REGIONAL"
 *            connector-enforcement: "NOT_REQUIRED"
 *            data-disk-size-gb: 20
 *            data-disk-type: 'PD_SSD'
 *            deletion-protection-enabled: false
 *            pricing-plan: "PER_USE"
 *            storage-auto-resize: true
 *            storage-auto-resize-limit: 100
 *            tier: "db-perf-optimized-N-2"
 *            edition: "ENTERPRISE_PLUS"
 *
 *            data-cache-config
 *                data-cache-enabled: true
 *            end
 *
 *            backup-configuration
 *                enabled: true
 *                start-time: "09:00"
 *                transaction-log-retention-days: 14
 *                binary-log-enabled: true
 *
 *                backup-retention-settings
 *                    retention-unit: "COUNT"
 *                    retained-backups: 15
 *                end
 *            end
 *
 *            user-labels: {
 *                "example": "example"
 *            }
 *
 *            ip-configuration
 *                ipv4-enabled: true
 *                server-ca-mode: 'GOOGLE_MANAGED_INTERNAL_CA'
 *                ssl-mode: 'ALLOW_UNENCRYPTED_AND_ENCRYPTED'
 *
 *                authorized-networks
 *                    name: "example-QA-enviroment"
 *                    value: "3.131.207.174/32"
 *                end
 *            end
 *
 *            location-preference
 *                zone: 'us-central1-c'
 *                secondary-zone: 'us-central1-b'
 *            end
 *        end
 *
 *        database-version: "MYSQL_8_0_31"
 *        gce-zone: "us-central1-c"
 *        secondary-gce-zone: "us-central1-b"
 *        instance-type: "CLOUD_SQL_INSTANCE"
 *        region: "us-central1"
 *        backend-type: "SECOND_GEN"
 *    end
 *
 *    google::database-instance database-instance-example-replica
 *        name: "gyro-db-test-replica"
 *        master-instance: $(google::database-instance database-instance-example)
 *
 *        settings
 *            edition: "ENTERPRISE_PLUS"
 *            tier: "db-perf-optimized-N-2"
 *
 *            ip-configuration
 *                ipv4-enabled: true
 *            end
 *
 *            location-preference
 *                zone: 'us-central1-c'
 *                secondary-zone: 'us-central1-b'
 *            end
 *        end
 *    end
 */
@Type("database-instance")
public class DatabaseInstanceResource extends GoogleResource implements Copyable<DatabaseInstance> {

    private String backendType;
    private String databaseVersion;
    private DbDiskEncryptionConfiguration diskEncryptionConfiguration;
    private DbDiskEncryptionStatus diskEncryptionStatus;
    private DbFailoverReplica failoverReplica;
    private String region;
    private String gceZone;
    private String secondaryGceZone;
    private String instanceType;
    private DatabaseInstanceResource masterInstance;
    private String name;
    private DbOnPremisesConfiguration onPremisesConfiguration;
    private DbReplicaConfiguration replicaConfiguration;
    private DbReplicationCluster replicationCluster;
    private String rootPassword;
    private DbSqlScheduledMaintenance scheduledMaintenance;
    private DbSettings settings;
    private Boolean switchTransactionLogsToCloudStorageEnabled;

    // Read-only
    private List<String> suspensionReason;
    private String selfLink;
    private String serviceAccountEmailAddress;
    private DbSslCert serverCaCert;
    private String state;
    private String maintenanceVersion;
    private List<String> availableMaintenanceVersions;
    private String createTime;
    private String databaseInstalledVersion;
    private String dnsName;
    private String pscServiceAttachmentLink;
    private List<DbAvailableDatabaseVersion> upgradableDatabaseVersions;
    private String writeEndpoint;
    private String connectionName;
    private DbGeminiInstanceConfig geminiConfig;
    private DbSqlOutOfDiskReport outOfDiskReport;
    private List<String> replicaNames;
    private Boolean satisfiesPzi;
    private Boolean satisfiesPzs;
    private String sqlNetworkArchitecture;
    private List<DbIpMapping> ipAddresses;

    /**
     * The type of the backend that is used for this instance.
     */
    @ValidStrings({ "SECOND_GEN", "EXTERNAL" })
    public String getBackendType() {
        return backendType;
    }

    public void setBackendType(String backendType) {
        this.backendType = backendType;
    }

    /**
     * The database version for this instance.
     */
    @ValidStrings({
        "MYSQL_5_6",
        "MYSQL_5_7",
        "MYSQL_8_0",
        "MYSQL_8_0_18",
        "MYSQL_8_0_26",
        "MYSQL_8_0_27",
        "MYSQL_8_0_28",
        "MYSQL_8_0_29",
        "MYSQL_8_0_30",
        "MYSQL_8_0_31",
        "MYSQL_8_0_32",
        "MYSQL_8_0_33",
        "MYSQL_8_0_34",
        "MYSQL_8_0_35",
        "MYSQL_8_0_36",
        "MYSQL_8_0_37",
        "POSTGRES_9_6",
        "POSTGRES_10",
        "POSTGRES_11",
        "POSTGRES_12",
        "POSTGRES_13",
        "POSTGRES_14",
        "POSTGRES_15",
        "SQLSERVER_2017_STANDARD",
        "SQLSERVER_2017_ENTERPRISE",
        "SQLSERVER_2017_EXPRESS",
        "SQLSERVER_2017_WEB",
        "SQLSERVER_2019_STANDARD",
        "SQLSERVER_2019_ENTERPRISE",
        "SQLSERVER_2019_EXPRESS",
        "SQLSERVER_2019_WEB",
        "SQLSERVER_2022_STANDARD",
        "SQLSERVER_2022_ENTERPRISE",
        "SQLSERVER_2022_EXPRESS",
        "SQLSERVER_2022_WEB" })
    @Updatable
    public String getDatabaseVersion() {
        return databaseVersion;
    }

    public void setDatabaseVersion(String databaseVersion) {
        this.databaseVersion = databaseVersion;
    }

    /**
     * The disk encryption configuration for this instance.
     *
     * @subresource gyro.google.cloudsql.DbDiskEncryptionConfiguration
     */
    public DbDiskEncryptionConfiguration getDiskEncryptionConfiguration() {
        return diskEncryptionConfiguration;
    }

    public void setDiskEncryptionConfiguration(DbDiskEncryptionConfiguration diskEncryptionConfiguration) {
        this.diskEncryptionConfiguration = diskEncryptionConfiguration;
    }

    /**
     * The disk encryption status for this instance.
     *
     * @subresource gyro.google.cloudsql.DbDiskEncryptionStatus
     */
    public DbDiskEncryptionStatus getDiskEncryptionStatus() {
        return diskEncryptionStatus;
    }

    public void setDiskEncryptionStatus(DbDiskEncryptionStatus diskEncryptionStatus) {
        this.diskEncryptionStatus = diskEncryptionStatus;
    }

    /**
     * The failover replica for this instance.
     *
     * @subresource gyro.google.cloudsql.DbFailoverReplica
     */
    @Updatable
    public DbFailoverReplica getFailoverReplica() {
        return failoverReplica;
    }

    public void setFailoverReplica(DbFailoverReplica failoverReplica) {
        this.failoverReplica = failoverReplica;
    }

    /**
     * The region for this instance. Defaults to ``us-central1``.
     */
    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    /**
     * The GCE zone for this instance.
     */
    @Updatable
    public String getGceZone() {
        return gceZone;
    }

    public void setGceZone(String gceZone) {
        this.gceZone = gceZone;
    }

    /**
     * The secondary GCE zone for this instance.
     */
    @Updatable
    public String getSecondaryGceZone() {
        return secondaryGceZone;
    }

    public void setSecondaryGceZone(String secondaryGceZone) {
        this.secondaryGceZone = secondaryGceZone;
    }

    /**
     * The type of the instance.
     */
    @Updatable
    @ValidStrings({ "CLOUD_SQL_INSTANCE", "ON_PREMISES_INSTANCE", "READ_REPLICA_INSTANCE" })
    public String getInstanceType() {
        return instanceType;
    }

    public void setInstanceType(String instanceType) {
        this.instanceType = instanceType;
    }

    /**
     * The instance which will act as primary in the replication setup.
     *
     * @resource gyro.google.cloudsql.DatabaseInstanceResource
     */
    @Updatable
    public DatabaseInstanceResource getMasterInstance() {
        return masterInstance;
    }

    public void setMasterInstance(DatabaseInstanceResource masterInstance) {
        this.masterInstance = masterInstance;
    }

    /**
     * The name of the instance.
     */
    @Required
    @Id
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * The on-premises configuration for this instance.
     *
     * @subresource gyro.google.cloudsql.DbOnPremisesConfiguration
     */
    public DbOnPremisesConfiguration getOnPremisesConfiguration() {
        return onPremisesConfiguration;
    }

    public void setOnPremisesConfiguration(DbOnPremisesConfiguration onPremisesConfiguration) {
        this.onPremisesConfiguration = onPremisesConfiguration;
    }

    /**
     * The configuration specific to failover replicas and read replicas.
     *
     * @subresource gyro.google.cloudsql.DbReplicaConfiguration
     */
    @Updatable
    public DbReplicaConfiguration getReplicaConfiguration() {
        return replicaConfiguration;
    }

    public void setReplicaConfiguration(DbReplicaConfiguration replicaConfiguration) {
        this.replicaConfiguration = replicaConfiguration;
    }

    /**
     * The replication cluster for this instance.
     * This field can not be set on creation.
     *
     * @subresource gyro.google.cloudsql.DbReplicationCluster
     */
    @Updatable
    public DbReplicationCluster getReplicationCluster() {
        return replicationCluster;
    }

    public void setReplicationCluster(DbReplicationCluster replicationCluster) {
        this.replicationCluster = replicationCluster;
    }

    /**
     * The initial root password for this instance.
     */
    public String getRootPassword() {
        return rootPassword;
    }

    public void setRootPassword(String rootPassword) {
        this.rootPassword = rootPassword;
    }

    /**
     * The scheduled maintenance period for this instance.
     *
     * @subresource gyro.google.cloudsql.DbSqlScheduledMaintenance
     */
    @Updatable
    public DbSqlScheduledMaintenance getScheduledMaintenance() {
        return scheduledMaintenance;
    }

    public void setScheduledMaintenance(DbSqlScheduledMaintenance scheduledMaintenance) {
        this.scheduledMaintenance = scheduledMaintenance;
    }

    /**
     * The settings for this instance.
     *
     * @subresource gyro.google.cloudsql.DbSettings
     */
    @Updatable
    @Required
    public DbSettings getSettings() {
        return settings;
    }

    public void setSettings(DbSettings settings) {
        this.settings = settings;
    }

    /**
     * When set to ``true``, the point-in-time recovery log files are switched from a sata disk to Cloud Storage.
     */
    @Updatable
    public Boolean getSwitchTransactionLogsToCloudStorageEnabled() {
        return switchTransactionLogsToCloudStorageEnabled;
    }

    public void setSwitchTransactionLogsToCloudStorageEnabled(Boolean switchTransactionLogsToCloudStorageEnabled) {
        this.switchTransactionLogsToCloudStorageEnabled = switchTransactionLogsToCloudStorageEnabled;
    }

    /**
     * The reason for suspension of this instance.
     */
    @Output
    @ValidStrings({ "BILLING_ISSUE", "LEGAL_ISSUE", "OPERATIONAL_ISSUE", "KMS_KEY_ISSUE" })
    public List<String> getSuspensionReason() {
        return suspensionReason;
    }

    public void setSuspensionReason(List<String> suspensionReason) {
        this.suspensionReason = suspensionReason;
    }

    /**
     * The URI for this instance.
     */
    @Output
    public String getSelfLink() {
        return selfLink;
    }

    public void setSelfLink(String selfLink) {
        this.selfLink = selfLink;
    }

    /**
     * The service account email address for this instance.
     */
    @Output
    public String getServiceAccountEmailAddress() {
        return serviceAccountEmailAddress;
    }

    public void setServiceAccountEmailAddress(String serviceAccountEmailAddress) {
        this.serviceAccountEmailAddress = serviceAccountEmailAddress;
    }

    /**
     * The server CA certificate for this instance.
     *
     * @subresource gyro.google.cloudsql.DbSslCert
     */
    @Output
    public DbSslCert getServerCaCert() {
        return serverCaCert;
    }

    public void setServerCaCert(DbSslCert serverCaCert) {
        this.serverCaCert = serverCaCert;
    }

    /**
     * The state of this instance.
     */
    @Output
    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    /**
     * The maintenance version for this instance.
     */
    @Output
    public String getMaintenanceVersion() {
        return maintenanceVersion;
    }

    public void setMaintenanceVersion(String maintenanceVersion) {
        this.maintenanceVersion = maintenanceVersion;
    }

    /**
     * The list of available maintenance versions for this instance.
     */
    @Output
    public List<String> getAvailableMaintenanceVersions() {
        if (availableMaintenanceVersions == null) {
            availableMaintenanceVersions = new ArrayList<>();
        }

        return availableMaintenanceVersions;
    }

    public void setAvailableMaintenanceVersions(List<String> availableMaintenanceVersions) {
        this.availableMaintenanceVersions = availableMaintenanceVersions;
    }

    /**
     * The current create time for this instance.
     */
    @Output
    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    /**
     * The current database version running on the instance including minor version
     */
    @Output
    public String getDatabaseInstalledVersion() {
        return databaseInstalledVersion;
    }

    public void setDatabaseInstalledVersion(String databaseInstalledVersion) {
        this.databaseInstalledVersion = databaseInstalledVersion;
    }

    /**
     * The DNS name for this instance.
     */
    @Output
    public String getDnsName() {
        return dnsName;
    }

    public void setDnsName(String dnsName) {
        this.dnsName = dnsName;
    }

    /**
     * The link to the PSC service attachment for this instance.
     */
    @Output
    public String getPscServiceAttachmentLink() {
        return pscServiceAttachmentLink;
    }

    public void setPscServiceAttachmentLink(String pscServiceAttachmentLink) {
        this.pscServiceAttachmentLink = pscServiceAttachmentLink;
    }

    /**
     * The list of upgradable database versions for this instance.
     *
     * @subresource gyro.google.cloudsql.DbAvailableDatabaseVersion
     */
    @Output
    public List<DbAvailableDatabaseVersion> getUpgradableDatabaseVersions() {
        if (upgradableDatabaseVersions == null) {
            upgradableDatabaseVersions = new ArrayList<>();
        }

        return upgradableDatabaseVersions;
    }

    public void setUpgradableDatabaseVersions(List<DbAvailableDatabaseVersion> upgradableDatabaseVersions) {
        this.upgradableDatabaseVersions = upgradableDatabaseVersions;
    }

    /**
     * The write endpoint for this instance.
     */
    @Output
    public String getWriteEndpoint() {
        return writeEndpoint;
    }

    public void setWriteEndpoint(String writeEndpoint) {
        this.writeEndpoint = writeEndpoint;
    }

    /**
     * The connection name of the instance to be used in connection strings.
     */
    @Output
    public String getConnectionName() {
        return connectionName;
    }

    public void setConnectionName(String connectionName) {
        this.connectionName = connectionName;
    }

    /**
     * The Gemini instance configuration for this instance.
     *
     * @subresource gyro.google.cloudsql.DbGeminiInstanceConfig
     */
    @Output
    public DbGeminiInstanceConfig getGeminiConfig() {
        return geminiConfig;
    }

    public void setGeminiConfig(DbGeminiInstanceConfig geminiConfig) {
        this.geminiConfig = geminiConfig;
    }

    /**
     * The out of disk report for this instance.
     *
     * @subresource gyro.google.cloudsql.DbSqlOutOfDiskReport
     */
    @Output
    public DbSqlOutOfDiskReport getOutOfDiskReport() {
        return outOfDiskReport;
    }

    public void setOutOfDiskReport(DbSqlOutOfDiskReport outOfDiskReport) {
        this.outOfDiskReport = outOfDiskReport;
    }

    /**
     * The list of replica names for this instance.
     */
    @Output
    public List<String> getReplicaNames() {
        if (replicaNames == null) {
            replicaNames = new ArrayList<>();
        }

        return replicaNames;
    }

    public void setReplicaNames(List<String> replicaNames) {
        this.replicaNames = replicaNames;
    }

    /**
     * When set to ``true``, the instance satisfies PZI.
     */
    @Output
    public Boolean getSatisfiesPzi() {
        return satisfiesPzi;
    }

    public void setSatisfiesPzi(Boolean satisfiesPzi) {
        this.satisfiesPzi = satisfiesPzi;
    }

    /**
     * When set to ``true``, the instance satisfies PZS.
     */
    @Output
    public Boolean getSatisfiesPzs() {
        return satisfiesPzs;
    }

    public void setSatisfiesPzs(Boolean satisfiesPzs) {
        this.satisfiesPzs = satisfiesPzs;
    }

    /**
     * The SQL network architecture for this instance.
     */
    @Output
    public String getSqlNetworkArchitecture() {
        return sqlNetworkArchitecture;
    }

    public void setSqlNetworkArchitecture(String sqlNetworkArchitecture) {
        this.sqlNetworkArchitecture = sqlNetworkArchitecture;
    }

    /**
     * The list of IP addresses for this instance.
     *
     * @subresource gyro.google.cloudsql.DbIpMapping
     */
    @Output
    public List<DbIpMapping> getIpAddresses() {
        if (ipAddresses == null) {
            ipAddresses = new ArrayList<>();
        }

        return ipAddresses;
    }

    public void setIpAddresses(List<DbIpMapping> ipAddresses) {
        this.ipAddresses = ipAddresses;
    }

    @Override
    public void copyFrom(DatabaseInstance model) throws Exception {
        setBackendType(model.getBackendType());
        setDatabaseVersion(model.getDatabaseVersion());
        setRegion(model.getRegion());
        setGceZone(model.getGceZone());
        setSecondaryGceZone(model.getSecondaryGceZone());
        setInstanceType(model.getInstanceType());
        setName(model.getName());
        setRootPassword(model.getRootPassword());
        setSwitchTransactionLogsToCloudStorageEnabled(model.getSwitchTransactionLogsToCloudStorageEnabled());
        setSelfLink(model.getSelfLink());
        setServiceAccountEmailAddress(model.getServiceAccountEmailAddress());
        setState(model.getState());
        setMaintenanceVersion(model.getMaintenanceVersion());
        setAvailableMaintenanceVersions(model.getAvailableMaintenanceVersions());
        setCreateTime(model.getCreateTime());
        setDatabaseInstalledVersion(model.getDatabaseInstalledVersion());
        setDnsName(model.getDnsName());
        setPscServiceAttachmentLink(model.getPscServiceAttachmentLink());
        setWriteEndpoint(model.getWriteEndpoint());
        setConnectionName(model.getConnectionName());
        setReplicaNames(model.getReplicaNames());
        setSatisfiesPzi(model.getSatisfiesPzi());
        setSatisfiesPzs(model.getSatisfiesPzs());
        setSqlNetworkArchitecture(model.getSqlNetworkArchitecture());

        setDiskEncryptionConfiguration(null);
        if (model.getDiskEncryptionConfiguration() != null) {
            DbDiskEncryptionConfiguration config = newSubresource(DbDiskEncryptionConfiguration.class);
            config.copyFrom(model.getDiskEncryptionConfiguration());
            setDiskEncryptionConfiguration(config);
        }

        setDiskEncryptionStatus(null);
        if (model.getDiskEncryptionStatus() != null) {
            DbDiskEncryptionStatus config = newSubresource(DbDiskEncryptionStatus.class);
            config.copyFrom(model.getDiskEncryptionStatus());
            setDiskEncryptionStatus(config);
        }

        setFailoverReplica(null);
        if (model.getFailoverReplica() != null) {
            DbFailoverReplica config = newSubresource(DbFailoverReplica.class);
            config.copyFrom(model.getFailoverReplica());
            setFailoverReplica(config);
        }

        setIpAddresses(null);
        if (model.getIpAddresses() != null && !model.getIpAddresses().isEmpty()) {
            setIpAddresses(model.getIpAddresses().stream()
                .map(ipMapping -> {
                    DbIpMapping config = newSubresource(DbIpMapping.class);
                    config.copyFrom(ipMapping);
                    return config;
                })
                .collect(java.util.stream.Collectors.toList()));
        }

        setMasterInstance(null);
        if (model.getMasterInstanceName() != null) {
            setMasterInstance(findById(DatabaseInstanceResource.class, model.getMasterInstanceName()));
        }

        setOnPremisesConfiguration(null);
        if (model.getOnPremisesConfiguration() != null) {
            DbOnPremisesConfiguration config = newSubresource(DbOnPremisesConfiguration.class);
            config.copyFrom(model.getOnPremisesConfiguration());
            setOnPremisesConfiguration(config);
        }

        setReplicaConfiguration(null);
        if (model.getReplicaConfiguration() != null) {
            DbReplicaConfiguration config = newSubresource(DbReplicaConfiguration.class);
            config.copyFrom(model.getReplicaConfiguration());
            setReplicaConfiguration(config);
        }

        setReplicationCluster(null);
        if (model.getReplicationCluster() != null) {
            DbReplicationCluster config = newSubresource(DbReplicationCluster.class);
            config.copyFrom(model.getReplicationCluster());
            setReplicationCluster(config);
        }

        setScheduledMaintenance(null);
        if (model.getScheduledMaintenance() != null) {
            DbSqlScheduledMaintenance config = newSubresource(DbSqlScheduledMaintenance.class);
            config.copyFrom(model.getScheduledMaintenance());
            setScheduledMaintenance(config);
        }

        setSettings(null);
        if (model.getSettings() != null) {
            DbSettings config = newSubresource(DbSettings.class);
            config.copyFrom(model.getSettings());
            setSettings(config);
        }

        setSuspensionReason(null);
        if (model.getSuspensionReason() != null && !model.getSuspensionReason().isEmpty()) {
            setSuspensionReason(model.getSuspensionReason());
        }

        setServerCaCert(null);
        if (model.getServerCaCert() != null) {
            DbSslCert config = newSubresource(DbSslCert.class);
            config.copyFrom(model.getServerCaCert());
            setServerCaCert(config);
        }

        setUpgradableDatabaseVersions(null);
        if (model.getUpgradableDatabaseVersions() != null) {
            setUpgradableDatabaseVersions(model.getUpgradableDatabaseVersions().stream()
                .map(upgradableDatabaseVersion -> {
                    DbAvailableDatabaseVersion config = newSubresource(DbAvailableDatabaseVersion.class);
                    config.copyFrom(upgradableDatabaseVersion);
                    return config;
                })
                .collect(java.util.stream.Collectors.toList()));
        }

        setGeminiConfig(null);
        if (model.getGeminiConfig() != null) {
            DbGeminiInstanceConfig config = newSubresource(DbGeminiInstanceConfig.class);
            config.copyFrom(model.getGeminiConfig());
            setGeminiConfig(config);
        }

        setOutOfDiskReport(null);
        if (model.getOutOfDiskReport() != null) {
            DbSqlOutOfDiskReport config = newSubresource(DbSqlOutOfDiskReport.class);
            config.copyFrom(model.getOutOfDiskReport());
            setOutOfDiskReport(config);
        }

    }

    @Override
    protected boolean doRefresh() throws Exception {
        SQLAdmin client = createClient(SQLAdmin.class);
        DatabaseInstance instance = client.instances().get(getProjectId(), getName()).execute();

        if (instance == null) {
            return false;
        }

        copyFrom(instance);

        return true;
    }

    @Override
    protected void doCreate(GyroUI ui, State state) throws Exception {
        SQLAdmin client = createClient(SQLAdmin.class);

        if (getReplicationCluster() != null) {
            throw new GyroException(
                "The 'replication-cluster' field is not supported for creating a new instance. It can only be configured in an update call.");
        }

        try {
            Operation execute = client.instances().insert(getProjectId(), getDatabaseInstance()).execute();
            waitForCompletion(execute,
                15, TimeUnit.MINUTES, TimeoutSettings.Action.CREATE);
        } catch (GoogleJsonResponseException ex) {
            if (ex.getStatusCode() == 409) {
                throw new GyroException("Ran into error", ex);
            } else {
                throw ex;
            }
        }
    }

    @Override
    protected void doUpdate(GyroUI ui, State state, Resource current, Set<String> changedFieldNames) throws Exception {
        SQLAdmin client = createClient(SQLAdmin.class);

        DatabaseInstanceResource currentInstance = (DatabaseInstanceResource) current;
        if (changedFieldNames.contains("settings") && !StringUtils.equals(
            currentInstance.getSettings().getEdition(), getSettings().getEdition())) {
            Settings newSettings = new Settings();
            newSettings.setEdition(getSettings().getEdition());
            newSettings.setSettingsVersion(getSettings().getSettingsVersion());

            if (getSettings().getTier() != null) {
                newSettings.setTier(getSettings().getTier());
            }

            waitForCompletion(client.instances()
                    .patch(getProjectId(), getName(), new DatabaseInstance().setSettings(newSettings)).execute(),
                15, TimeUnit.MINUTES, TimeoutSettings.Action.UPDATE);
        }

        waitForCompletion(client.instances().update(getProjectId(), getName(), getDatabaseInstance()).execute(),
            15, TimeUnit.MINUTES, TimeoutSettings.Action.UPDATE);
    }

    @Override
    protected void doDelete(GyroUI ui, State state) throws Exception {
        if (getSettings() != null && getSettings().getDeletionProtectionEnabled()) {
            throw new GyroException("Deletion protection is enabled for this instance.");
        }

        SQLAdmin client = createClient(SQLAdmin.class);
        waitForCompletion(client.instances().delete(getProjectId(), getName()).execute(),
            10, TimeUnit.MINUTES, TimeoutSettings.Action.DELETE);
    }

    private DatabaseInstance getDatabaseInstance() throws IOException {
        DatabaseInstance databaseInstance;
        try {
            databaseInstance = createClient(SQLAdmin.class).instances().get(getProjectId(), getName()).execute();

        } catch (GoogleJsonResponseException ex) {
            if (ex.getStatusCode() == 404) {
                databaseInstance = new DatabaseInstance();

            } else {
                throw ex;
            }
        }

        databaseInstance.setName(getName());
        databaseInstance.setSettings(getSettings().toSettings());
        databaseInstance.setBackendType(getBackendType() == null ? null : getBackendType());
        databaseInstance.setDatabaseVersion(getDatabaseVersion() == null ? null : getDatabaseVersion());
        databaseInstance.setDiskEncryptionConfiguration(getDiskEncryptionConfiguration() == null
            ? null : getDiskEncryptionConfiguration().toDiskEncryptionConfiguration());
        databaseInstance.setDiskEncryptionStatus(
            getDiskEncryptionStatus() == null ? null : getDiskEncryptionStatus().toDiskEncryptionStatus());
        databaseInstance.setFailoverReplica(
            getFailoverReplica() == null ? null : getFailoverReplica().toFailoverReplica());
        databaseInstance.setGceZone(getGceZone() == null ? null : getGceZone());
        databaseInstance.setInstanceType(getInstanceType() == null ? null : getInstanceType());
        databaseInstance.setMasterInstanceName(
            getMasterInstance() == null ? null : getMasterInstance().getName());
        databaseInstance.setOnPremisesConfiguration(
            getOnPremisesConfiguration() == null ? null : getOnPremisesConfiguration().toOnPremisesConfiguration());
        databaseInstance.setProject(getProject() == null ? null : getProject());
        databaseInstance.setRegion(getRegion() == null ? null : getRegion());
        databaseInstance.setReplicaConfiguration(
            getReplicaConfiguration() == null ? null : getReplicaConfiguration().toReplicaConfiguration());
        databaseInstance.setRootPassword(getRootPassword() == null ? null : getRootPassword());
        databaseInstance.setScheduledMaintenance(
            getScheduledMaintenance() == null ? null : getScheduledMaintenance().toSqlScheduledMaintenance());
        databaseInstance.setSecondaryGceZone(getSecondaryGceZone() == null ? null : getSecondaryGceZone());
        databaseInstance.setSwitchTransactionLogsToCloudStorageEnabled(
            getSwitchTransactionLogsToCloudStorageEnabled() == null
                ? null : getSwitchTransactionLogsToCloudStorageEnabled());
        databaseInstance.setReplicationCluster(
            getReplicationCluster() == null ? null : getReplicationCluster().toReplicationCluster());

        if (getIpAddresses() != null && !getIpAddresses().isEmpty()) {
            databaseInstance.setIpAddresses(getIpAddresses().stream()
                .map(DbIpMapping::toIpMapping)
                .collect(java.util.stream.Collectors.toList()));
        }

        return databaseInstance;
    }

    public String getProject() {
        return getProjectId();
    }

    public void waitForCompletion(Operation operation, long duration, TimeUnit unit, TimeoutSettings.Action action) {
        if (operation != null) {
            Waiter waiter = new Waiter().prompt(false);
            waiter.atMost(duration, unit);
            waiter.resourceOverrides(this, action);

            waiter.until(() -> {
                Operation response = createClient(SQLAdmin.class).operations()
                    .get(getProjectId(), operation.getName())
                    .execute();

                if (response != null && response.getError() != null && !response.getError().isEmpty()) {
                    throw new GyroException(formatOperationErrorMessage(response.getError()));
                }

                return response != null && response.getStatus().equals("DONE");
            });
        }
    }

    protected static String formatOperationErrorMessage(OperationErrors error) {
        return error.getErrors().stream()
            .map(OperationError::getMessage)
            .collect(Collectors.joining("\n"));
    }
}
