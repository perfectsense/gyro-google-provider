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
import com.google.api.services.sqladmin.model.ConnectSettings;
import gyro.core.GyroUI;
import gyro.core.Type;
import gyro.core.resource.Resource;
import gyro.core.scope.State;
import gyro.core.validation.ValidStrings;
import gyro.google.Copyable;

@Type("sql-connect-settings")
public class ConnectSettingsResource extends SqlAdminResource implements Copyable<ConnectSettings> {

    private String backendType;

    private String databaseVersion;

    private String dnsName;

    private List<IpMapping> ipAddresse;

    private Boolean pscEnabled;

    private String region;

    private SslCertResource serverCaCert;

    /**
     * `SECOND_GEN`: Cloud SQL database instance. `EXTERNAL`: A database server that is not managed by Google. This property is read-only; use the `tier` property in the `settings` object to determine the database type.
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
     * The database engine type and version. The `databaseVersion` field cannot be changed after instance creation. MySQL instances: `MYSQL_8_0`, `MYSQL_5_7` (default), or `MYSQL_5_6`. PostgreSQL instances: `POSTGRES_9_6`, `POSTGRES_10`, `POSTGRES_11`, `POSTGRES_12` (default), `POSTGRES_13`, or `POSTGRES_14`. SQL Server instances: `SQLSERVER_2017_STANDARD` (default), `SQLSERVER_2017_ENTERPRISE`, `SQLSERVER_2017_EXPRESS`, `SQLSERVER_2017_WEB`, `SQLSERVER_2019_STANDARD`, `SQLSERVER_2019_ENTERPRISE`, `SQLSERVER_2019_EXPRESS`, or `SQLSERVER_2019_WEB`.
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
     * The dns name of the instance.
     */
    public String getDnsName() {
        return dnsName;
    }

    public void setDnsName(String dnsName) {
        this.dnsName = dnsName;
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
     * Whether PSC connectivity is enabled for this instance.
     */
    public Boolean getPscEnabled() {
        return pscEnabled;
    }

    public void setPscEnabled(Boolean pscEnabled) {
        this.pscEnabled = pscEnabled;
    }

    /**
     * The cloud region for the instance. For example, `us-central1`, `europe-west1`. The region cannot be changed after instance creation.
     */
    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
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

    @Override
    public void copyFrom(ConnectSettings model) {
        setBackendType(model.getBackendType());
        setDatabaseVersion(model.getDatabaseVersion());
        setDnsName(model.getDnsName());
        setPscEnabled(model.getPscEnabled());
        setRegion(model.getRegion());

        setServerCaCert(null);
        if (model.getServerCaCert() != null) {
            setServerCaCert(findById(SslCertResource.class, model.getServerCaCert().getSelfLink()));
        }

        setIpAddresse(null);
        if (model.getIpAddresses() != null) {
            for (com.google.api.services.sqladmin.model.IpMapping ip : model.getIpAddresses()) {
                IpMapping ipMapping = newSubresource(IpMapping.class);
                ipMapping.copyFrom(ip);
                getIpAddresse().add(ipMapping);
            }
        }
    }

    @Override
    protected boolean doRefresh() {
        SQLAdmin client = createClient(SQLAdmin.class);

        try {
            ConnectSettings connectSettings = client.connect().get("", "").execute();
            copyFrom(connectSettings);

            return true;
        } catch (IOException ex) {
            // ignore
        }

        return false;
    }

    @Override
    protected void doCreate(GyroUI ui, State state) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void doUpdate(GyroUI ui, State state, Resource current, Set<String> changedFieldNames) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void doDelete(GyroUI ui, State state) {
        throw new UnsupportedOperationException();
    }
}
