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

public class MySqlReplicaConfiguration extends Diffable
    implements Copyable<com.google.api.services.sqladmin.model.MySqlReplicaConfiguration> {

    private String caCertificate;

    private String clientCertificate;

    private String clientKey;

    private Integer connectRetryInterval;

    private String dumpFilePath;

    private Long masterHeartbeatPeriod;

    private String password;

    private String sslCipher;

    private String username;

    private Boolean verifyServerCertificate;

    /**
     * PEM representation of the trusted CA's x509 certificate.
     */
    public String getCaCertificate() {
        return caCertificate;
    }

    public void setCaCertificate(String caCertificate) {
        this.caCertificate = caCertificate;
    }

    /**
     * PEM representation of the replica's x509 certificate.
     */
    public String getClientCertificate() {
        return clientCertificate;
    }

    public void setClientCertificate(String clientCertificate) {
        this.clientCertificate = clientCertificate;
    }

    /**
     * PEM representation of the replica's private key. The corresponsing public key is encoded in the client's certificate.
     */
    public String getClientKey() {
        return clientKey;
    }

    public void setClientKey(String clientKey) {
        this.clientKey = clientKey;
    }

    /**
     * Seconds to wait between connect retries. MySQL's default is 60 seconds.
     */
    public Integer getConnectRetryInterval() {
        return connectRetryInterval;
    }

    public void setConnectRetryInterval(Integer connectRetryInterval) {
        this.connectRetryInterval = connectRetryInterval;
    }

    /**
     * Path to a SQL dump file in Google Cloud Storage from which the replica instance is to be created. The URI is in the form gs://bucketName/fileName. Compressed gzip files (.gz) are also supported. Dumps have the binlog co-ordinates from which replication begins. This can be accomplished by setting --master-data to 1 when using mysqldump.
     */
    public String getDumpFilePath() {
        return dumpFilePath;
    }

    public void setDumpFilePath(String dumpFilePath) {
        this.dumpFilePath = dumpFilePath;
    }

    /**
     * Interval in milliseconds between replication heartbeats.
     */
    public Long getMasterHeartbeatPeriod() {
        return masterHeartbeatPeriod;
    }

    public void setMasterHeartbeatPeriod(Long masterHeartbeatPeriod) {
        this.masterHeartbeatPeriod = masterHeartbeatPeriod;
    }

    /**
     * The password for the replication connection.
     */
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * A list of permissible ciphers to use for SSL encryption.
     */
    public String getSslCipher() {
        return sslCipher;
    }

    public void setSslCipher(String sslCipher) {
        this.sslCipher = sslCipher;
    }

    /**
     * The username for the replication connection.
     */
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Whether or not to check the primary instance's Common Name value in the certificate that it sends during the SSL handshake.
     */
    public Boolean getVerifyServerCertificate() {
        return verifyServerCertificate;
    }

    public void setVerifyServerCertificate(Boolean verifyServerCertificate) {
        this.verifyServerCertificate = verifyServerCertificate;
    }

    @Override
    public String primaryKey() {
        return "";
    }

    @Override
    public void copyFrom(com.google.api.services.sqladmin.model.MySqlReplicaConfiguration model) {
        setCaCertificate(model.getCaCertificate());
        setClientCertificate(model.getClientCertificate());
        setClientKey(model.getClientKey());
        setConnectRetryInterval(model.getConnectRetryInterval());
        setDumpFilePath(model.getDumpFilePath());
        setMasterHeartbeatPeriod(model.getMasterHeartbeatPeriod());
        setPassword(model.getPassword());
        setSslCipher(model.getSslCipher());
        setUsername(model.getUsername());
        setVerifyServerCertificate(model.getVerifyServerCertificate());
    }

    com.google.api.services.sqladmin.model.MySqlReplicaConfiguration toMySqlReplicaConfiguration() {
        com.google.api.services.sqladmin.model.MySqlReplicaConfiguration mysqlReplicaConfiguration = new com.google.api.services.sqladmin.model.MySqlReplicaConfiguration();
        mysqlReplicaConfiguration.setCaCertificate(getCaCertificate());
        mysqlReplicaConfiguration.setClientCertificate(getClientCertificate());
        mysqlReplicaConfiguration.setClientKey(getClientKey());
        mysqlReplicaConfiguration.setConnectRetryInterval(getConnectRetryInterval());
        mysqlReplicaConfiguration.setDumpFilePath(getDumpFilePath());
        mysqlReplicaConfiguration.setMasterHeartbeatPeriod(getMasterHeartbeatPeriod());
        mysqlReplicaConfiguration.setPassword(getPassword());
        mysqlReplicaConfiguration.setSslCipher(getSslCipher());
        mysqlReplicaConfiguration.setUsername(getUsername());
        mysqlReplicaConfiguration.setVerifyServerCertificate(getVerifyServerCertificate());

        return mysqlReplicaConfiguration;
    }
}
