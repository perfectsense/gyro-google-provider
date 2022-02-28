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

import com.google.api.gax.rpc.NotFoundException;
import com.google.cloud.compute.v1.DeleteRegionSslCertificateRequest;
import com.google.cloud.compute.v1.GetRegionSslCertificateRequest;
import com.google.cloud.compute.v1.InsertRegionSslCertificateRequest;
import com.google.cloud.compute.v1.Operation;
import com.google.cloud.compute.v1.RegionSslCertificatesClient;
import com.google.cloud.compute.v1.SslCertificate;
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

        if (model.hasRegion()) {
            setRegion(model.getRegion());
        }
    }

    @Override
    protected boolean doRefresh() throws Exception {
        try (RegionSslCertificatesClient client = createClient(RegionSslCertificatesClient.class)) {
            SslCertificate certificates = getRegionSslCertificate(client);

            if (certificates == null) {
                return false;
            }

            copyFrom(certificates);

            return true;
        }
    }

    @Override
    protected void doCreate(GyroUI ui, State state) throws Exception {
        try (RegionSslCertificatesClient client = createClient(RegionSslCertificatesClient.class)) {
            SslCertificate.Builder builder = SslCertificate.newBuilder();
            builder.setName(getName());
            builder.setCertificate(readCertificateFile());
            builder.setPrivateKey(readPrivateKeyFile());

            if (getDescription() != null) {
                builder.setDescription(getDescription());
            }

            Operation operation = client.insertCallable().call(InsertRegionSslCertificateRequest.newBuilder()
                .setProject(getProjectId())
                .setRegion(getRegion())
                .setSslCertificateResource(builder)
                .build());

            waitForCompletion(operation);
        }

        refresh();
    }

    @Override
    protected void doUpdate(GyroUI ui, State state, Resource current, Set<String> changedFieldNames) throws Exception {

    }

    @Override
    protected void doDelete(GyroUI ui, State state) throws Exception {
        try (RegionSslCertificatesClient client = createClient(RegionSslCertificatesClient.class)) {
            Operation operation = client.deleteCallable().call(DeleteRegionSslCertificateRequest.newBuilder()
                .setProject(getProjectId())
                .setRegion(getRegion())
                .setSslCertificate(getName())
                .build());

            waitForCompletion(operation);
        }
    }

    private SslCertificate getRegionSslCertificate(RegionSslCertificatesClient client) {
        SslCertificate certificates = null;

        try {
            certificates = client.get(GetRegionSslCertificateRequest.newBuilder()
                .setProject(getProjectId())
                .setRegion(getRegion())
                .setSslCertificate(getName())
                .build());

        } catch (NotFoundException ex) {
            // ignore
        }

        return certificates;
    }
}
