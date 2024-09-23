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

import com.google.api.services.sqladmin.model.InstanceReference;
import com.google.api.services.sqladmin.model.OnPremisesConfiguration;
import gyro.core.resource.Diffable;
import gyro.google.Copyable;

public class DbOnPremisesConfiguration extends Diffable implements Copyable<OnPremisesConfiguration> {

    private String caCertificate;
    private String clientCertificate;
    private String clientKey;
    private String dumpFilePath;
    private String hostPort;
    private String password;
    private DatabaseInstanceResource sourceInstance;
    private String username;

    /**
     * The PEM representation of the trusted CA's x509 certificate.
     */
    public String getCaCertificate() {
        return caCertificate;
    }

    public void setCaCertificate(String caCertificate) {
        this.caCertificate = caCertificate;
    }

    /**
     * The PEM representation of the replica's x509 certificate.
     */
    public String getClientCertificate() {
        return clientCertificate;
    }

    public void setClientCertificate(String clientCertificate) {
        this.clientCertificate = clientCertificate;
    }

    /**
     * The PEM representation of the replica's private key.
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
     * The Cloud SQL instance if the source is Cloud SQL.
     */
    public DatabaseInstanceResource getSourceInstance() {
        return sourceInstance;
    }

    public void setSourceInstance(DatabaseInstanceResource sourceInstance) {
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
    public void copyFrom(OnPremisesConfiguration model) throws Exception {
        setCaCertificate(model.getCaCertificate());
        setClientCertificate(model.getClientCertificate());
        setClientKey(model.getClientKey());
        setDumpFilePath(model.getDumpFilePath());
        setHostPort(model.getHostPort());
        setPassword(model.getPassword());
        setUsername(model.getUsername());

        setSourceInstance(null);
        if (model.getSourceInstance() != null) {
            setSourceInstance(findById(DatabaseInstanceResource.class, model.getSourceInstance().getName()));
        }
    }

    @Override
    public String primaryKey() {
        return "";
    }

    public OnPremisesConfiguration toOnPremisesConfiguration() {
        OnPremisesConfiguration config = new OnPremisesConfiguration();

        if (getCaCertificate() != null) {
            config.setCaCertificate(getCaCertificate());
        }

        if (getClientCertificate() != null) {
            config.setClientCertificate(getClientCertificate());
        }

        if (getClientKey() != null) {
            config.setClientKey(getClientKey());
        }

        if (getDumpFilePath() != null) {
            config.setDumpFilePath(getDumpFilePath());
        }

        if (getHostPort() != null) {
            config.setHostPort(getHostPort());
        }

        if (getPassword() != null) {
            config.setPassword(getPassword());
        }

        if (getSourceInstance() != null) {
            InstanceReference instanceReference = new InstanceReference();
            instanceReference.setName(getSourceInstance().getName());

            if (getSourceInstance().getRegion() != null) {
                instanceReference.setRegion(getSourceInstance().getRegion());
            }

            if (getSourceInstance().getProject() != null) {
                instanceReference.setProject(getSourceInstance().getProject());
            }

            config.setSourceInstance(instanceReference);
        }

        if (getUsername() != null) {
            config.setUsername(getUsername());
        }

        return config;
    }
}
