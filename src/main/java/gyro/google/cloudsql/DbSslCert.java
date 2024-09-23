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

import com.google.api.services.sqladmin.model.SslCert;
import gyro.core.resource.Diffable;
import gyro.core.resource.Output;
import gyro.google.Copyable;

public class DbSslCert extends Diffable implements Copyable<SslCert> {

    private String cert;
    private String certSerialNumber;
    private String commonName;
    private String createTime;
    private String expirationTime;
    private String instance;
    private String selfLink;
    private String sha1Fingerprint;

    /**
     * The PEM representation of the certificate.
     */
    @Output
    public String getCert() {
        return cert;
    }

    public void setCert(String cert) {
        this.cert = cert;
    }

    /**
     * The serial number, as extracted from the certificate.
     */
    @Output
    public String getCertSerialNumber() {
        return certSerialNumber;
    }

    public void setCertSerialNumber(String certSerialNumber) {
        this.certSerialNumber = certSerialNumber;
    }

    /**
     * The user supplied name of the cert.
     */
    @Output
    public String getCommonName() {
        return commonName;
    }

    public void setCommonName(String commonName) {
        this.commonName = commonName;
    }

    /**
     * The time when the certificate was created
     */
    @Output
    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    /**
     * The time when the certificate expires
     */
    @Output
    public String getExpirationTime() {
        return expirationTime;
    }

    public void setExpirationTime(String expirationTime) {
        this.expirationTime = expirationTime;
    }

    /**
     * Name of the database instance.
     */
    @Output
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
    public String getSelfLink() {
        return selfLink;
    }

    public void setSelfLink(String selfLink) {
        this.selfLink = selfLink;
    }

    /**
     * The Sha1 Fingerprint.
     */
    @Output
    public String getSha1Fingerprint() {
        return sha1Fingerprint;
    }

    public void setSha1Fingerprint(String sha1Fingerprint) {
        this.sha1Fingerprint = sha1Fingerprint;
    }

    @Override
    public void copyFrom(SslCert model) throws Exception {
        setCert(model.getCert());
        setCertSerialNumber(model.getCertSerialNumber());
        setCommonName(model.getCommonName());
        setCreateTime(model.getCreateTime());
        setExpirationTime(model.getExpirationTime());
        setInstance(model.getInstance());
        setSelfLink(model.getSelfLink());
        setSha1Fingerprint(model.getSha1Fingerprint());
    }

    @Override
    public String primaryKey() {
        return "";
    }
}
