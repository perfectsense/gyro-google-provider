/*
 * Copyright 2021, Brightspot.
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

package gyro.google.gke;

import com.google.container.v1.ClientCertificateConfig;
import gyro.core.resource.Diffable;
import gyro.core.validation.Required;
import gyro.google.Copyable;

public class GkeClientCertificateConfig extends Diffable implements Copyable<ClientCertificateConfig> {

    public Boolean issueClientCertificate;

    /**
     * When set to ``true``, a client certificate is issued.
     */
    @Required
    public Boolean getIssueClientCertificate() {
        return issueClientCertificate;
    }

    public void setIssueClientCertificate(Boolean issueClientCertificate) {
        this.issueClientCertificate = issueClientCertificate;
    }

    @Override
    public String primaryKey() {
        return "";
    }

    @Override
    public void copyFrom(ClientCertificateConfig model) throws Exception {
        setIssueClientCertificate(model.getIssueClientCertificate());
    }

    ClientCertificateConfig toClientCertificateConfig() {
        return ClientCertificateConfig.newBuilder().setIssueClientCertificate(getIssueClientCertificate()).build();
    }
}
