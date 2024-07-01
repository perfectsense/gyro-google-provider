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
import java.util.Set;

import com.google.api.services.sqladmin.SQLAdmin;
import com.google.api.services.sqladmin.model.Operation;
import com.google.api.services.sqladmin.model.SslCert;
import com.google.api.services.sqladmin.model.SslCertsInsertRequest;
import com.google.api.services.sqladmin.model.SslCertsInsertResponse;
import gyro.core.GyroUI;
import gyro.core.Type;
import gyro.core.resource.Id;
import gyro.core.resource.Output;
import gyro.core.resource.Resource;
import gyro.core.scope.State;
import gyro.google.Copyable;

@Type("c")
public class SslCertResource extends SqlAdminResource
    implements Copyable<com.google.api.services.sqladmin.model.SslCert> {

    private String cert;

    private String certSerialNumber;

    private String commonName;

    private String createTime;

    private String expirationTime;

    private String instance;

    private String selfLink;

    private String sha1Fingerprint;

    private String project;

    /**
     * PEM representation.
     */
    public String getCert() {
        return cert;
    }

    public void setCert(String cert) {
        this.cert = cert;
    }

    /**
     * Serial number, as extracted from the certificate.
     */
    public String getCertSerialNumber() {
        return certSerialNumber;
    }

    public void setCertSerialNumber(String certSerialNumber) {
        this.certSerialNumber = certSerialNumber;
    }

    /**
     * User supplied name. Constrained to [a-zA-Z.-_ ]+.
     */
    public String getCommonName() {
        return commonName;
    }

    public void setCommonName(String commonName) {
        this.commonName = commonName;
    }

    /**
     * The time when the certificate was created in [RFC 3339](https://tools.ietf.org/html/rfc3339) format, for example `2012-11-15T16:19:00.094Z`
     */
    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    /**
     * The time when the certificate expires in [RFC 3339](https://tools.ietf.org/html/rfc3339) format, for example `2012-11-15T16:19:00.094Z`.
     */
    public String getExpirationTime() {
        return expirationTime;
    }

    public void setExpirationTime(String expirationTime) {
        this.expirationTime = expirationTime;
    }

    /**
     * Name of the database instance.
     */
    public String getInstance() {
        return instance;
    }

    public void setInstance(String instance) {
        this.instance = instance;
    }

    /**
     * The URI of this resource.
     */
    @Output
    @Id
    public String getSelfLink() {
        return selfLink;
    }

    public void setSelfLink(String selfLink) {
        this.selfLink = selfLink;
    }

    /**
     * Sha1 Fingerprint.
     */
    public String getSha1Fingerprint() {
        return sha1Fingerprint;
    }

    public void setSha1Fingerprint(String sha1Fingerprint) {
        this.sha1Fingerprint = sha1Fingerprint;
    }

    /**
     * The project ID of the project containing the Cloud SQL database.
     * The Google apps domain is prefixed if applicable.
     */
    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    @Override
    public void copyFrom(com.google.api.services.sqladmin.model.SslCert model) {
        setCert(model.getCert());
        setCertSerialNumber(model.getCertSerialNumber());
        setCommonName(model.getCommonName());
        setCreateTime(model.getCreateTime());
        setExpirationTime(model.getExpirationTime());
        setInstance(model.getInstance());
        setSelfLink(model.getSelfLink());
        setSha1Fingerprint(model.getSha1Fingerprint());
        setProject(getProjectIdFromSelfLink(getSelfLink()));
    }

    @Override
    protected boolean doRefresh() {
        SQLAdmin client = createClient(SQLAdmin.class);

        SslCert sslCert = getSslCert(client);

        if (sslCert != null) {
            copyFrom(sslCert);

            return true;
        }

        return false;
    }

    @Override
    protected void doCreate(GyroUI ui, State state) throws IOException {
        SQLAdmin client = createClient(SQLAdmin.class);

        SslCertsInsertRequest sslCertsInsertRequest = new SslCertsInsertRequest();
        sslCertsInsertRequest.setCommonName(getCommonName());

        SslCertsInsertResponse response = client.sslCerts()
            .insert(getProject(), getInstance(), sslCertsInsertRequest)
            .execute();

        waitForCompletion(response.getOperation(), getProject(), client);
    }

    @Override
    protected void doUpdate(GyroUI ui, State state, Resource current, Set<String> changedFieldNames) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void doDelete(GyroUI ui, State state) throws IOException {
        SQLAdmin client = createClient(SQLAdmin.class);

        Operation operation = client.sslCerts().delete(getProject(), getInstance(), getCommonName()).execute();

        waitForCompletion(operation, getProject(), client);
    }

    SslCert getSslCert(SQLAdmin client) {
        SslCert cert = null;
        try {
            if (getProject() == null) {
                setProject(getProjectIdFromSelfLink(getSelfLink()));
                setCommonName(getCommonNameFromSelfLink(getSelfLink()));
                setInstance(getInstanceIdFromSelfLink(getSelfLink()));
            }

            cert = client.sslCerts().get(getProject(), getInstance(), getCommonName()).execute();
        } catch (IOException ex) {
            // ignore
        }

        return cert;
    }

    private String getCommonNameFromSelfLink(String selfLink) {
        return selfLink.split("/")[6];
    }

    private String getInstanceIdFromSelfLink(String selfLink) {
        return selfLink.split("/")[4];
    }

    private String getProjectIdFromSelfLink(String selfLink) {
        return selfLink.split("/")[1];
    }
}
