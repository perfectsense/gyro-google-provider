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

import com.google.container.v1.MasterAuth;
import gyro.core.resource.Diffable;
import gyro.core.resource.Output;
import gyro.core.resource.Updatable;
import gyro.core.validation.Required;
import gyro.google.Copyable;

public class GkeMasterAuth extends Diffable implements Copyable<MasterAuth> {

    private GkeClientCertificateConfig clientCertificateConfig;

    // Read-only
    private String clusterCaCertificate;
    private String clientCertificate;
    private String clientKey;

    /**
     * The configuration for client certificate authentication on the cluster.
     */
    @Required
    public GkeClientCertificateConfig getClientCertificateConfig() {
        return clientCertificateConfig;
    }

    public void setClientCertificateConfig(GkeClientCertificateConfig clientCertificateConfig) {
        this.clientCertificateConfig = clientCertificateConfig;
    }

    /**
     * The bse64-encoded public certificate that is the root of trust for the cluster.
     */
    @Output
    public String getClusterCaCertificate() {
        return clusterCaCertificate;
    }

    public void setClusterCaCertificate(String clusterCaCertificate) {
        this.clusterCaCertificate = clusterCaCertificate;
    }

    /**
     * The base64-encoded public certificate used by clients to authenticate to the cluster endpoint.
     */
    @Output
    public String getClientCertificate() {
        return clientCertificate;
    }

    public void setClientCertificate(String clientCertificate) {
        this.clientCertificate = clientCertificate;
    }

    /**
     * The base64-encoded private key used by clients to authenticate to the cluster endpoint.
     */
    @Output
    public String getClientKey() {
        return clientKey;
    }

    public void setClientKey(String clientKey) {
        this.clientKey = clientKey;
    }

    @Override
    public String primaryKey() {
        return "";
    }

    @Override
    public void copyFrom(MasterAuth model) throws Exception {
        setClientCertificate(model.getClientCertificate());
        setClusterCaCertificate(model.getClusterCaCertificate());
        setClientKey(model.getClientKey());

        GkeClientCertificateConfig config = newSubresource(GkeClientCertificateConfig.class);
        config.copyFrom(model.getClientCertificateConfig());
        setClientCertificateConfig(config);
    }

    MasterAuth toMasterAuth() {
        return MasterAuth.newBuilder()
            .setClientCertificateConfig(getClientCertificateConfig().toClientCertificateConfig()).build();
    }
}
