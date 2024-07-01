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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.google.api.services.sqladmin.SQLAdmin;
import com.google.api.services.sqladmin.model.DatabaseInstance;
import com.google.api.services.sqladmin.model.Operation;
import gyro.core.GyroUI;
import gyro.core.Type;
import gyro.core.resource.Id;
import gyro.core.resource.Output;
import gyro.core.resource.Resource;
import gyro.core.scope.State;
import gyro.core.validation.ValidStrings;
import gyro.google.Copyable;

@Type("sql-database-instance")
public class DatabaseInstanceResource extends SqlAdminResource implements Copyable<DatabaseInstance> {

    private List<String> availableMaintenanceVersions;

    private String backendType;

    private String connectionName;

    private String createTime;

    private Long currentDiskSize;

    private String databaseInstalledVersion;

    private String databaseVersion;

    private DiskEncryptionConfiguration diskEncryptionConfiguration;

    private DiskEncryptionStatus diskEncryptionStatus;

    private String dnsName;

    private DatabaseInstanceFailoverReplica failoverReplica;

    private String gceZone;

    private GeminiInstanceConfig geminiConfig;

    private String instanceType;

    private List<IpMapping> ipAddresse;

    private String ipv6Address;

    private String maintenanceVersion;

    private String masterInstanceName;

    private Long maxDiskSize;

    private String name;

    private OnPremisesConfiguration onPremisesConfiguration;

    private SqlOutOfDiskReport outOfDiskReport;

    private String primaryDnsName;

    private String project;

    private String pscServiceAttachmentLink;

    private String region;

    private ReplicaConfiguration replicaConfiguration;

    private List<String> replicaNames;

    private ReplicationCluster replicationCluster;

    private String rootPassword;

    private Boolean satisfiesPzs;

    private SqlScheduledMaintenance scheduledMaintenance;

    private String secondaryGceZone;

    private String selfLink;

    private SslCertResource serverCaCert;

    private String serviceAccountEmailAddress;

    private DbInstanceSettings settings;

    private String sqlNetworkArchitecture;

    private String state;

    private List<String> suspensionReason;

    private List<AvailableDatabaseVersion> upgradableDatabaseVersion;

    private String writeEndpoint;

    /**
     *  List all maintenance versions applicable on the instance.
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
     * The backend type. `SECOND_GEN`: Cloud SQL database instance. `EXTERNAL`: A database server that is not managed by Google. This property is read-only; use the `tier` property in the `settings` object to determine the database type.
     */
    @ValidStrings({
        "SQL_BACKEND_TYPE_UNSPECIFIED",
        "FIRST_GEN",
        "SECOND_GEN",
        "EXTERNAL"
    })
    public String getBackendType() {
        return backendType;
    }

    public void setBackendType(String backendType) {
        this.backendType = backendType;
    }

    /**
     * Connection name of the Cloud SQL instance used in connection strings.
     */
    public String getConnectionName() {
        return connectionName;
    }

    public void setConnectionName(String connectionName) {
        this.connectionName = connectionName;
    }

    /**
     * The time when the instance was created in [RFC 3339](https://tools.ietf.org/html/rfc3339) format, for example `2012-11-15T16:19:00.094Z`.
     */
    @Output
    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    /**
     * The current disk usage of the instance in bytes. This property has been deprecated.
     * Use the "cloudsql.googleapis.com/database/disk/bytes_used" metric in Cloud Monitoring API instead.
     * Please see [this announcement](https://groups.google.com/d/msg/google-cloud-sql-announce/I_7-F9EBhT0/BtvFtdFeAgAJ) for details.
     */
    public Long getCurrentDiskSize() {
        return currentDiskSize;
    }

    public void setCurrentDiskSize(Long currentDiskSize) {
        this.currentDiskSize = currentDiskSize;
    }

    /**
     * Stores the current database version running on the instance including minor version such as `MYSQL_8_0_18`.
     */
    @Output
    public String getDatabaseInstalledVersion() {
        return databaseInstalledVersion;
    }

    public void setDatabaseInstalledVersion(String databaseInstalledVersion) {
        this.databaseInstalledVersion = databaseInstalledVersion;
    }

    /**
     * The database engine type and version. The `databaseVersion` field cannot be changed after instance creation.
     */
    @ValidStrings({
        "SQL_DATABASE_VERSION_UNSPECIFIED",
        "MYSQL_5_1",
        "MYSQL_5_5",
        "MYSQL_5_6",
        "MYSQL_5_7",
        "SQLSERVER_2017_STANDARD",
        "SQLSERVER_2017_ENTERPRISE",
        "SQLSERVER_2017_EXPRESS",
        "SQLSERVER_2017_WEB",
        "POSTGRES_9_6",
        "POSTGRES_10",
        "POSTGRES_11",
        "POSTGRES_12",
        "POSTGRES_13",
        "POSTGRES_14",
        "POSTGRES_15",
        "POSTGRES_16",
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
        "MYSQL_8_0_38",
        "MYSQL_8_0_39",
        "MYSQL_8_0_40",
        "MYSQL_8_4",
        "MYSQL_8_4_0",
        "SQLSERVER_2019_STANDARD",
        "SQLSERVER_2019_ENTERPRISE",
        "SQLSERVER_2019_EXPRESS",
        "SQLSERVER_2019_WEB",
        "SQLSERVER_2022_STANDARD",
        "SQLSERVER_2022_ENTERPRISE",
        "SQLSERVER_2022_EXPRESS",
        "SQLSERVER_2022_WEB"
    })
    public String getDatabaseVersion() {
        return databaseVersion;
    }

    public void setDatabaseVersion(String databaseVersion) {
        this.databaseVersion = databaseVersion;
    }

    /**
     * Disk encryption configuration specific to an instance.
     *
     * @subresource gyro.google.sqladmin.base.DiskEncryptionConfiguration
     */
    public DiskEncryptionConfiguration getDiskEncryptionConfiguration() {
        return diskEncryptionConfiguration;
    }

    public void setDiskEncryptionConfiguration(
        DiskEncryptionConfiguration diskEncryptionConfiguration) {
        this.diskEncryptionConfiguration = diskEncryptionConfiguration;
    }

    /**
     * Disk encryption status specific to an instance.
     *
     * @subresource gyro.google.sqladmin.base.DiskEncryptionStatus
     */
    public DiskEncryptionStatus getDiskEncryptionStatus() {
        return diskEncryptionStatus;
    }

    public void setDiskEncryptionStatus(DiskEncryptionStatus diskEncryptionStatus) {
        this.diskEncryptionStatus = diskEncryptionStatus;
    }

    /**
     * The dns name of the instance.
     */
    @Output
    public String getDnsName() {
        return dnsName;
    }

    public void setDnsName(String dnsName) {
        this.dnsName = dnsName;
    }

    /**
     * The name and status of the failover replica.
     *
     * @subresource gyro.google.sqladmin.base.DatabaseInstanceFailoverReplica
     */
    public DatabaseInstanceFailoverReplica getFailoverReplica() {
        return failoverReplica;
    }

    public void setFailoverReplica(DatabaseInstanceFailoverReplica failoverReplica) {
        this.failoverReplica = failoverReplica;
    }

    /**
     * The Compute Engine zone that the instance is currently serving from. T
     * his value could be different from the zone that was specified when the instance was created if the
     * instance has failed over to its secondary zone. WARNING: Changing this might restart the instance.
     */
    public String getGceZone() {
        return gceZone;
    }

    public void setGceZone(String gceZone) {
        this.gceZone = gceZone;
    }

    /**
     * Gemini instance configuration.
     *
     * @subresource gyro.google.sqladmin.base.GeminiInstanceConfig
     */
    public GeminiInstanceConfig getGeminiConfig() {
        return geminiConfig;
    }

    public void setGeminiConfig(GeminiInstanceConfig geminiConfig) {
        this.geminiConfig = geminiConfig;
    }

    /**
     * The instance type.
     */
    @ValidStrings({
        "SQL_INSTANCE_TYPE_UNSPECIFIED",
        "CLOUD_SQL_INSTANCE",
        "ON_PREMISES_INSTANCE",
        "READ_REPLICA_INSTANCE"
    })
    public String getInstanceType() {
        return instanceType;
    }

    public void setInstanceType(String instanceType) {
        this.instanceType = instanceType;
    }

    /**
     * The assigned IP addresses for the instance.
     *
     * @subresource gyro.google.sqladmin.base.IpMapping
     */
    public List<IpMapping> getIpAddresse() {
        if (ipAddresse == null) {
            ipAddresse = new ArrayList<>();
        }

        return ipAddresse;
    }

    public void setIpAddresse(List<IpMapping> ipAddresse) {
        this.ipAddresse = ipAddresse;
    }

    /**
     * The IPv6 address assigned to the instance.
     */
    public String getIpv6Address() {
        return ipv6Address;
    }

    public void setIpv6Address(String ipv6Address) {
        this.ipv6Address = ipv6Address;
    }

    /**
     * The current software version on the instance.
     */
    public String getMaintenanceVersion() {
        return maintenanceVersion;
    }

    public void setMaintenanceVersion(String maintenanceVersion) {
        this.maintenanceVersion = maintenanceVersion;
    }

    /**
     * The name of the instance which will act as primary in the replication setup.
     */
    public String getMasterInstanceName() {
        return masterInstanceName;
    }

    public void setMasterInstanceName(String masterInstanceName) {
        this.masterInstanceName = masterInstanceName;
    }

    /**
     * The maximum disk size of the instance in bytes.
     */
    public Long getMaxDiskSize() {
        return maxDiskSize;
    }

    public void setMaxDiskSize(Long maxDiskSize) {
        this.maxDiskSize = maxDiskSize;
    }

    /**
     * Name of the Cloud SQL instance. This does not include the project ID.
     */
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * Configuration specific to on-premises instances.
     *
     * @subresource gyro.google.sqladmin.base.OnPremisesConfiguration
     */
    public OnPremisesConfiguration getOnPremisesConfiguration() {
        return onPremisesConfiguration;
    }

    public void setOnPremisesConfiguration(OnPremisesConfiguration onPremisesConfiguration) {
        this.onPremisesConfiguration = onPremisesConfiguration;
    }

    /**
     * This field represents the report generated by the proactive database wellness job for OutOfDisk issues.
     * * Writers: * the proactive database wellness job for OOD. * Readers: * the proactive database wellness job
     *
     * @subresource gyro.google.sqladmin.base.SqlOutOfDiskReport
     */
    @Output
    public SqlOutOfDiskReport getOutOfDiskReport() {
        return outOfDiskReport;
    }

    public void setOutOfDiskReport(SqlOutOfDiskReport outOfDiskReport) {
        this.outOfDiskReport = outOfDiskReport;
    }

    /**
     * The primary dns name of the instance.
     */
    @Output
    public String getPrimaryDnsName() {
        return primaryDnsName;
    }

    public void setPrimaryDnsName(String primaryDnsName) {
        this.primaryDnsName = primaryDnsName;
    }

    /**
     * The project ID of the project containing the Cloud SQL instance. The Google apps domain is prefixed if applicable.
     */
    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    /**
     * The link to service attachment of PSC instance.
     */
    @Output
    public String getPscServiceAttachmentLink() {
        return pscServiceAttachmentLink;
    }

    public void setPscServiceAttachmentLink(String pscServiceAttachmentLink) {
        this.pscServiceAttachmentLink = pscServiceAttachmentLink;
    }

    /**
     * The geographical region of the Cloud SQL instance.
     */
    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    /**
     * Configuration specific to failover replicas and read replicas.
     *
     * @subresource gyro.google.sqladmin.base.ReplicaConfiguration
     */
    public ReplicaConfiguration getReplicaConfiguration() {
        return replicaConfiguration;
    }

    public void setReplicaConfiguration(ReplicaConfiguration replicaConfiguration) {
        this.replicaConfiguration = replicaConfiguration;
    }

    /**
     * The replicas of the instance.
     */
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
     * A primary instance and disaster recovery (DR) replica pair.
     * A DR replica is a cross-region replica that you designate for failover in
     * the event that the primary instance experiences regional failure. Only applicable to MySQL.
     *
     * @subresource gyro.google.sqladmin.base.ReplicationCluster
     */
    public ReplicationCluster getReplicationCluster() {
        return replicationCluster;
    }

    public void setReplicationCluster(ReplicationCluster replicationCluster) {
        this.replicationCluster = replicationCluster;
    }

    /**
     * Initial root password. Use only on creation.
     * You must set root passwords before you can connect to PostgreSQL instances.
     */
    public String getRootPassword() {
        return rootPassword;
    }

    public void setRootPassword(String rootPassword) {
        this.rootPassword = rootPassword;
    }

    /**
     * This status indicates whether the instance satisfies PZS. The status is reserved for future use.
     */
    public Boolean getSatisfiesPzs() {
        return satisfiesPzs;
    }

    public void setSatisfiesPzs(Boolean satisfiesPzs) {
        this.satisfiesPzs = satisfiesPzs;
    }

    /**
     * The start time of any upcoming scheduled maintenance for this instance.
     *
     * @subresource gyro.google.sqladmin.base.SqlScheduledMaintenance
     */
    public SqlScheduledMaintenance getScheduledMaintenance() {
        return scheduledMaintenance;
    }

    public void setScheduledMaintenance(SqlScheduledMaintenance scheduledMaintenance) {
        this.scheduledMaintenance = scheduledMaintenance;
    }

    /**
     * The Compute Engine zone that the failover instance is currently serving from for a regional instance. This value could be different from the zone that was specified when the instance was created if the instance has failed over to its secondary/failover zone.
     */
    public String getSecondaryGceZone() {
        return secondaryGceZone;
    }

    public void setSecondaryGceZone(String secondaryGceZone) {
        this.secondaryGceZone = secondaryGceZone;
    }

    /**
     * The URI of this resource.
     */
    @Id
    public String getSelfLink() {
        return selfLink;
    }

    public void setSelfLink(String selfLink) {
        this.selfLink = selfLink;
    }

    /**
     * SSL configuration.
     */
    public SslCertResource getServerCaCert() {
        return serverCaCert;
    }

    public void setServerCaCert(SslCertResource serverCaCert) {
        this.serverCaCert = serverCaCert;
    }

    /**
     * The service account email address assigned to the instance.\This property is read-only.
     */
    public String getServiceAccountEmailAddress() {
        return serviceAccountEmailAddress;
    }

    public void setServiceAccountEmailAddress(String serviceAccountEmailAddress) {
        this.serviceAccountEmailAddress = serviceAccountEmailAddress;
    }

    /**
     * The user settings.
     */
    public DbInstanceSettings getSettings() {
        return settings;
    }

    public void setSettings(DbInstanceSettings settings) {
        this.settings = settings;
    }

    @ValidStrings({
        "SQL_NETWORK_ARCHITECTURE_UNSPECIFIED",
        "NEW_NETWORK_ARCHITECTURE",
        "OLD_NETWORK_ARCHITECTURE"
    })
    public String getSqlNetworkArchitecture() {
        return sqlNetworkArchitecture;
    }

    public void setSqlNetworkArchitecture(String sqlNetworkArchitecture) {
        this.sqlNetworkArchitecture = sqlNetworkArchitecture;
    }

    /**
     * The current serving state of the Cloud SQL instance.
     */
    @ValidStrings({
        "SQL_INSTANCE_STATE_UNSPECIFIED",
        "RUNNABLE",
        "SUSPENDED",
        "PENDING_DELETE",
        "PENDING_CREATE",
        "MAINTENANCE",
        "FAILED",
        "ONLINE_MAINTENANCE"
    })
    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    /**
     * If the instance state is SUSPENDED, the reason for the suspension.
     */
    public List<String> getSuspensionReason() {
        if (suspensionReason == null) {
            suspensionReason = new ArrayList<>();
        }

        return suspensionReason;
    }

    public void setSuspensionReason(List<String> suspensionReason) {
        this.suspensionReason = suspensionReason;
    }

    /**
     * All database versions that are available for upgrade.
     *
     * @subresource gyro.google.sqladmin.base.AvailableDatabaseVersion
     */
    @Output
    public List<AvailableDatabaseVersion> getUpgradableDatabaseVersion() {
        if (upgradableDatabaseVersion == null) {
            upgradableDatabaseVersion = new ArrayList<>();
        }

        return upgradableDatabaseVersion;
    }

    public void setUpgradableDatabaseVersion(
        List<AvailableDatabaseVersion> upgradableDatabaseVersion) {
        this.upgradableDatabaseVersion = upgradableDatabaseVersion;
    }

    /**
     * The dns name of the primary instance in a replication group.
     */
    @Output
    public String getWriteEndpoint() {
        return writeEndpoint;
    }

    public void setWriteEndpoint(String writeEndpoint) {
        this.writeEndpoint = writeEndpoint;
    }

    @Override
    public void copyFrom(DatabaseInstance model) {
        setAvailableMaintenanceVersions(model.getAvailableMaintenanceVersions());
        setBackendType(model.getBackendType());
        setConnectionName(model.getConnectionName());
        setCreateTime(model.getCreateTime());
        setCurrentDiskSize(model.getCurrentDiskSize());
        setDatabaseInstalledVersion(model.getDatabaseInstalledVersion());
        setDatabaseVersion(model.getDatabaseVersion());
        setDnsName(model.getDnsName());
        setGceZone(model.getGceZone());
        setInstanceType(model.getInstanceType());
        setIpv6Address(model.getIpv6Address());
        setMaintenanceVersion(model.getMaintenanceVersion());
        setMasterInstanceName(model.getMasterInstanceName());
        setMaxDiskSize(model.getMaxDiskSize());
        setName(model.getName());
        setPrimaryDnsName(model.getPrimaryDnsName());

        setDiskEncryptionConfiguration(null);
        if (model.getDiskEncryptionConfiguration() != null) {
            DiskEncryptionConfiguration diskEncryptionConfiguration = newSubresource(DiskEncryptionConfiguration.class);
            diskEncryptionConfiguration.copyFrom(model.getDiskEncryptionConfiguration());
            setDiskEncryptionConfiguration(diskEncryptionConfiguration);
        }

        setDiskEncryptionStatus(null);
        if (model.getDiskEncryptionStatus() != null) {
            DiskEncryptionStatus diskEncryptionStatus = newSubresource(DiskEncryptionStatus.class);
            diskEncryptionStatus.copyFrom(model.getDiskEncryptionStatus());
            setDiskEncryptionStatus(diskEncryptionStatus);
        }

        setFailoverReplica(null);
        if (model.getFailoverReplica() != null) {
            DatabaseInstanceFailoverReplica failoverReplica = newSubresource(DatabaseInstanceFailoverReplica.class);
            failoverReplica.copyFrom(model.getFailoverReplica());
            setFailoverReplica(failoverReplica);
        }

        setGeminiConfig(null);
        if (model.getGeminiConfig() != null) {
            GeminiInstanceConfig geminiConfig = newSubresource(GeminiInstanceConfig.class);
            geminiConfig.copyFrom(model.getGeminiConfig());
            setGeminiConfig(geminiConfig);
        }

        setOnPremisesConfiguration(null);
        if (model.getOnPremisesConfiguration() != null) {
            OnPremisesConfiguration onPremisesConfiguration = newSubresource(OnPremisesConfiguration.class);
            onPremisesConfiguration.copyFrom(model.getOnPremisesConfiguration());
            setOnPremisesConfiguration(onPremisesConfiguration);
        }

        setOutOfDiskReport(null);
        if (model.getOutOfDiskReport() != null) {
            SqlOutOfDiskReport outOfDiskReport = newSubresource(SqlOutOfDiskReport.class);
            outOfDiskReport.copyFrom(model.getOutOfDiskReport());
            setOutOfDiskReport(outOfDiskReport);
        }

    }

    @Override
    protected boolean doRefresh() {
        SQLAdmin client = createClient(SQLAdmin.class);

        try {
            DatabaseInstance instance = client.instances().get(getProject(), getName()).execute();
            copyFrom(instance);

            return true;
        } catch (IOException ex) {
            // ignore
        }

        return false;
    }

    @Override
    protected void doCreate(GyroUI ui, State state) throws IOException {
        SQLAdmin client = createClient(SQLAdmin.class);

        DatabaseInstance instance = createOrUpdate(null, null, client);

        Operation operation = client.instances().insert(getProject(), instance).execute();

        waitForCompletion(operation, getProject(), client);

        doRefresh();
    }

    @Override
    protected void doUpdate(GyroUI ui, State state, Resource current, Set<String> changedFieldNames) throws IOException {
        SQLAdmin client = createClient(SQLAdmin.class);

        DatabaseInstance instance = createOrUpdate(null, changedFieldNames, client);

        Operation operation = client.instances().update(getProject(), getName(), instance).execute();

        waitForCompletion(operation, getProject(), client);
    }

    @Override
    protected void doDelete(GyroUI ui, State state) throws IOException {
        SQLAdmin client = createClient(SQLAdmin.class);

        Operation operation = client.instances().delete(getProject(), getName()).execute();

        waitForCompletion(operation, getProject(), client);
    }

    private DatabaseInstance createOrUpdate(DatabaseInstance current, Set<String> changedFieldNames, SQLAdmin client) {
        if (current == null) {
            // new instance
            current = new DatabaseInstance();
            current.setName(getName());
            current.setDatabaseVersion(getDatabaseVersion());
            current.setInstanceType(getInstanceType());
            current.setRegion(getRegion());
            current.setRootPassword(getRootPassword());
            current.setSettings(getSettings() != null ? getSettings().toSettings() : null);
            current.setDiskEncryptionConfiguration(getDiskEncryptionConfiguration() != null
                ? getDiskEncryptionConfiguration().toDiskEncryptionConfiguration() : null);
            current.setDiskEncryptionStatus(getDiskEncryptionStatus() != null
                ? getDiskEncryptionStatus().toDiskEncryptionStatus() : null);
            current.setReplicaConfiguration(getReplicaConfiguration() != null
                ? getReplicaConfiguration().toReplicaConfiguration() : null);
            current.setGeminiConfig(getGeminiConfig() != null ? getGeminiConfig().toGeminiInstanceConfig() : null);
            current.setOnPremisesConfiguration(getOnPremisesConfiguration() != null
                ? getOnPremisesConfiguration().toOnPremisesConfiguration() : null);
            current.setFailoverReplica(getFailoverReplica() != null
                ? getFailoverReplica().toFailoverReplica() : null);
            current.setReplicaNames(getReplicaNames());
            current.setReplicationCluster(getReplicationCluster() != null
                ? getReplicationCluster().toReplicationCluster() : null);
            current.setServerCaCert(getServerCaCert() != null ? getServerCaCert().getSslCert(client) : null);

        } else {
            // update existing
            if (changedFieldNames.contains("settings")) {
                current.setSettings(getSettings() != null ? getSettings().toSettings() : null);
            }

            if (changedFieldNames.contains("disk-encryption-configuration")) {
                current.setDiskEncryptionConfiguration(getDiskEncryptionConfiguration() != null
                    ? getDiskEncryptionConfiguration().toDiskEncryptionConfiguration() : null);
            }

            if (changedFieldNames.contains("disk-encryption-status")) {
                current.setDiskEncryptionStatus(getDiskEncryptionStatus() != null
                    ? getDiskEncryptionStatus().toDiskEncryptionStatus() : null);
            }

            if (changedFieldNames.contains("replica-configuration")) {
                current.setReplicaConfiguration(getReplicaConfiguration() != null
                    ? getReplicaConfiguration().toReplicaConfiguration() : null);
            }

            if (changedFieldNames.contains("gemini-config")) {
                current.setGeminiConfig(getGeminiConfig() != null ? getGeminiConfig().toGeminiInstanceConfig() : null);
            }

            if (changedFieldNames.contains("on-premises-configuration")) {
                current.setOnPremisesConfiguration(getOnPremisesConfiguration() != null
                    ? getOnPremisesConfiguration().toOnPremisesConfiguration() : null);
            }

            if (changedFieldNames.contains("root-password")) {
                current.setRootPassword(getRootPassword());
            }

            if (changedFieldNames.contains("database-version")) {
                current.setDatabaseVersion(getDatabaseVersion());
            }

            if (changedFieldNames.contains("instance-type")) {
                current.setInstanceType(getInstanceType());
            }

            if (changedFieldNames.contains("max-disk-size")) {
                current.setMaxDiskSize(getMaxDiskSize());
            }

            if (changedFieldNames.contains("current-disk-size")) {
                current.setCurrentDiskSize(getCurrentDiskSize());
            }

            if (changedFieldNames.contains("connection-name")) {
                current.setConnectionName(getConnectionName());
            }

            if (changedFieldNames.contains("backend-type")) {
                current.setBackendType(getBackendType());
            }

            if (changedFieldNames.contains("available-maintenance-versions")) {
                current.setAvailableMaintenanceVersions(getAvailableMaintenanceVersions());
            }

            if (changedFieldNames.contains("gce-zone")) {
                current.setGceZone(getGceZone());
            }

            if (changedFieldNames.contains("ipv6-address")) {
                current.setIpv6Address(getIpv6Address());
            }

            if (changedFieldNames.contains("primary-dns-name")) {
                current.setPrimaryDnsName(getPrimaryDnsName());
            }

            if (changedFieldNames.contains("dns-name")) {
                current.setDnsName(getDnsName());
            }

            if (changedFieldNames.contains("write-endpoint")) {
                current.setWriteEndpoint(getWriteEndpoint());
            }

            if (changedFieldNames.contains("service-account-email-address")) {
                current.setServiceAccountEmailAddress(getServiceAccountEmailAddress());
            }

            if (changedFieldNames.contains("sql-network-architecture")) {
                current.setSqlNetworkArchitecture(getSqlNetworkArchitecture());
            }

            if (changedFieldNames.contains("state")) {
                current.setState(getState());
            }

            if (changedFieldNames.contains("suspension-reason")) {
                current.setSuspensionReason(getSuspensionReason());
            }

            if (changedFieldNames.contains("replica-names")) {
                current.setReplicaNames(getReplicaNames());
            }

            if (changedFieldNames.contains("failover-replica")) {
                current.setFailoverReplica(getFailoverReplica() != null
                    ? getFailoverReplica().toFailoverReplica() : null);
            }

            if (changedFieldNames.contains("replication-cluster")) {
                current.setReplicationCluster(getReplicationCluster() != null
                    ? getReplicationCluster().toReplicationCluster() : null);
            }

            if (changedFieldNames.contains("scheduled-maintenance")) {
                current.setScheduledMaintenance(getScheduledMaintenance() != null
                    ? getScheduledMaintenance().toSqlScheduledMaintenance() : null);
            }

            if (changedFieldNames.contains("server-ca-cert")) {
                current.setServerCaCert(getServerCaCert() != null
                    ? getServerCaCert().getSslCert(client) : null);
            }
        }

        return current;
    }
}
