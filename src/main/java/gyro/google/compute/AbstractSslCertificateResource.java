/*
 * Copyright 2020, Perfect Sense, Inc.
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

package gyro.google.compute;

import java.io.InputStreamReader;

import com.google.api.services.compute.model.SslCertificate;
import com.google.common.io.CharStreams;
import gyro.core.GyroException;
import gyro.core.resource.Id;
import gyro.core.resource.Output;
import gyro.core.validation.Regex;
import gyro.core.validation.Required;
import gyro.google.Copyable;

import static java.nio.charset.StandardCharsets.*;

public abstract class AbstractSslCertificateResource extends ComputeResource implements Copyable<SslCertificate> {

    private String certificatePath;
    private String description;
    private String name;
    private String privateKeyPath;

    // Read-only
    private String selfLink;

    /**
     * The full path to the file containing the certificate(s) (in PEM format). The certificate chain must be no greater than 5 certs long. The chain must include at least one intermediate cert. (Required)
     */
    @Required
    public String getCertificatePath() {
        return certificatePath;
    }

    public void setCertificatePath(String certificatePath) {
        this.certificatePath = certificatePath;
    }

    /**
     * An optional description of this SSL certificate.
     */
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * The name of the SSL certificate. Must be 1-63 characters long, and the first character must be a lowercase letter. All other characters must be a lowercase letter, digit, or ``-``, except the last character, which cannot be a ``-``. (Required)
     */
    @Required
    @Regex("[a-z](?:[-a-z0-9]{0,61}[a-z0-9])?")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * The full path to the file containing the private key (in PEM format). (Required)
     */
    @Required
    public String getPrivateKeyPath() {
        return privateKeyPath;
    }

    public void setPrivateKeyPath(String privateKeyPath) {
        this.privateKeyPath = privateKeyPath;
    }

    /**
     * Server-defined URL for the SSL certificate.
     */
    @Id
    @Output
    public String getSelfLink() {
        return selfLink;
    }

    public void setSelfLink(String selfLink) {
        this.selfLink = selfLink;
    }

    @Override
    public void copyFrom(SslCertificate model) throws Exception {
        setDescription(model.getDescription());
        setName(model.getName());
        setSelfLink(model.getSelfLink());
    }

    protected String readCertificateFile() {
        try {
            return CharStreams.toString(new InputStreamReader(openInput(getCertificatePath()), UTF_8));
        } catch (Exception ex) {
            throw new GyroException("Failed reading certificate file: " + getCertificatePath());
        }
    }

    protected String readPrivateKeyFile() {
        try {
            return CharStreams.toString(new InputStreamReader(openInput(getPrivateKeyPath()), UTF_8));
        } catch (Exception ex) {
            throw new GyroException("Failed reading private key file: " + getPrivateKeyPath());
        }
    }
}
