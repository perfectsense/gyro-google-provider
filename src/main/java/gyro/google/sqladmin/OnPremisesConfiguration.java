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

public class OnPremisesConfiguration extends Diffable
    implements Copyable<com.google.api.services.sqladmin.model.OnPremisesConfiguration> {

    private String caCertificate;

    private String clientCertificate;

    private String clientKey;

    private String dumpFilePath;

    private String hostPort;

    private String password;

    private InstanceReference sourceInstance;

    private String username;

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
     * The dump file to create the Cloud SQL replica.
     */
    public String getDumpFilePath() {
        return dumpFilePath;
    }

    public void setDumpFilePath(String dumpFilePath) {
        this.dumpFilePath = dumpFilePath;
    }

    /**
     * The host and port of the on-premises instance in host:port format
     */
    public String getHostPort() {
        return hostPort;
    }

    public void setHostPort(String hostPort) {
        this.hostPort = hostPort;
    }

    /**
     * The password for connecting to on-premises instance.
     */
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * The reference to Cloud SQL instance if the source is Cloud SQL.
     *
     * @subresource gyro.google.sqladmin.InstanceReference
     */
    public InstanceReference getSourceInstance() {
        return sourceInstance;
    }

    public void setSourceInstance(InstanceReference sourceInstance) {
        this.sourceInstance = sourceInstance;
    }

    /**
     * The username for connecting to on-premises instance.
     */
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String primaryKey() {
        return "";
    }

    @Override
    public void copyFrom(com.google.api.services.sqladmin.model.OnPremisesConfiguration model) {
        setCaCertificate(model.getCaCertificate());
        setClientCertificate(model.getClientCertificate());
        setClientKey(model.getClientKey());
        setDumpFilePath(model.getDumpFilePath());
        setHostPort(model.getHostPort());
        setPassword(model.getPassword());

        setSourceInstance(null);
        if (model.getSourceInstance() != null) {
            InstanceReference instanceReference = new InstanceReference();
            instanceReference.copyFrom(model.getSourceInstance());
            setSourceInstance(instanceReference);
        }

        setUsername(model.getUsername());
    }

    com.google.api.services.sqladmin.model.OnPremisesConfiguration toOnPremisesConfiguration() {
        com.google.api.services.sqladmin.model.OnPremisesConfiguration onPremisesConfiguration = new com.google.api.services.sqladmin.model.OnPremisesConfiguration();
        onPremisesConfiguration.setCaCertificate(getCaCertificate());
        onPremisesConfiguration.setClientCertificate(getClientCertificate());
        onPremisesConfiguration.setClientKey(getClientKey());
        onPremisesConfiguration.setDumpFilePath(getDumpFilePath());
        onPremisesConfiguration.setHostPort(getHostPort());
        onPremisesConfiguration.setPassword(getPassword());
        onPremisesConfiguration.setSourceInstance(getSourceInstance() != null ? getSourceInstance().toInstanceReference() : null);
        onPremisesConfiguration.setUsername(getUsername());

        return onPremisesConfiguration;
    }
}
