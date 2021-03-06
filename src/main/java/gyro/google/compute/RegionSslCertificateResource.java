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

import java.util.Set;

import com.google.api.services.compute.Compute;
import com.google.api.services.compute.model.Operation;
import com.google.api.services.compute.model.SslCertificate;
import gyro.core.GyroUI;
import gyro.core.Type;
import gyro.core.resource.Resource;
import gyro.core.scope.State;
import gyro.core.validation.Required;
import gyro.google.util.Utils;

/**
 * Creates a regional SSL certificate resource.
 *
 * Example
 * -------
 *
 * .. code-block:: gyro
 *
 *     google::compute-region-ssl-certificate region-ssl-certificate-example
 *         name: "region-ssl-certificate-example"
 *         description: "region-ssl-certificate-example-desc"
 *         certificate-path: "../path/to/certificate-file.pem"
 *         private-key-path: "../path/to/private-key-file.pem"
 *         region: "us-east1"
 *     end
 */
@Type("compute-region-ssl-certificate")
public class RegionSslCertificateResource extends AbstractSslCertificateResource {

    private String region;

    /**
     * The region for the SSL certificate.
     */
    @Required
    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = Utils.extractName(region);
    }

    @Override
    public void copyFrom(SslCertificate model) throws Exception {
        super.copyFrom(model);

        setRegion(model.getRegion());
    }

    @Override
    protected boolean doRefresh() throws Exception {
        Compute client = createComputeClient();

        SslCertificate sslCertificate =
            client.regionSslCertificates().get(getProjectId(), getRegion(), getName()).execute();
        copyFrom(sslCertificate);

        return true;
    }

    @Override
    protected void doCreate(GyroUI ui, State state) throws Exception {
        Compute client = createComputeClient();

        SslCertificate certificate = new SslCertificate();
        certificate.setName(getName());
        certificate.setDescription(getDescription());
        certificate.setCertificate(readCertificateFile());
        certificate.setPrivateKey(readPrivateKeyFile());

        Operation operation = client.regionSslCertificates().insert(getProjectId(), getRegion(), certificate).execute();
        waitForCompletion(client, operation);

        refresh();
    }

    @Override
    protected void doUpdate(
        GyroUI ui, State state, Resource current, Set<String> changedFieldNames) throws Exception {

    }

    @Override
    protected void doDelete(GyroUI ui, State state) throws Exception {
        Compute client = createComputeClient();

        Operation operation = client.regionSslCertificates().delete(getProjectId(), getRegion(), getName()).execute();
        waitForCompletion(client, operation);
    }
}
