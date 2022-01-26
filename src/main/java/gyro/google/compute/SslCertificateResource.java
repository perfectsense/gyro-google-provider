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

import com.google.api.gax.rpc.InvalidArgumentException;
import com.google.api.gax.rpc.NotFoundException;
import com.google.cloud.compute.v1.GetSslCertificateRequest;
import com.google.cloud.compute.v1.Operation;
import com.google.cloud.compute.v1.SslCertificate;
import com.google.cloud.compute.v1.SslCertificatesClient;
import gyro.core.GyroUI;
import gyro.core.Type;
import gyro.core.resource.Resource;
import gyro.core.scope.State;

/**
 * Creates an SSL certificate resource.
 *
 * Example
 * -------
 *
 * .. code-block:: gyro
 *
 *     google::compute-ssl-certificate ssl-certificate-example
 *         name: "ssl-certificate-example"
 *         description: "ssl-certificate-example-desc"
 *         certificate-path: "../path/to/certificate-file.pem"
 *         private-key-path: "../path/to/private-key-file.pem"
 *     end
 */
@Type("compute-ssl-certificate")
public class SslCertificateResource extends AbstractSslCertificateResource {

    @Override
    protected boolean doRefresh() throws Exception {
        try (SslCertificatesClient client = createClient(SslCertificatesClient.class)) {
            SslCertificate sslCertificate = getSslCertificate(client);

            if (sslCertificate == null) {
                return false;
            }

            copyFrom(sslCertificate);

            return true;
        }
    }

    @Override
    protected void doCreate(GyroUI ui, State state) throws Exception {
        try (SslCertificatesClient client = createClient(SslCertificatesClient.class)) {
            SslCertificate.Builder builder = SslCertificate.newBuilder();
            builder.setName(getName());
            builder.setCertificate(readCertificateFile());
            builder.setPrivateKey(readPrivateKeyFile());

            if (getDescription() != null) {
                builder.setDescription(getDescription());
            }

            Operation operation = client.insert(getProjectId(), builder.build());
            waitForCompletion(operation);
        }
        refresh();
    }

    @Override
    protected void doUpdate(
        GyroUI ui, State state, Resource current, Set<String> changedFieldNames) throws Exception {

    }

    @Override
    protected void doDelete(GyroUI ui, State state) throws Exception {
        try (SslCertificatesClient client = createClient(SslCertificatesClient.class)) {
            Operation operation = client.delete(getProjectId(), getName());
            waitForCompletion(operation);
        }
    }

    private SslCertificate getSslCertificate(SslCertificatesClient client) {
        SslCertificate sslCertificate = null;

        try {
            sslCertificate = client.get(GetSslCertificateRequest.newBuilder()
                .setProject(getProjectId())
                .setSslCertificate(getName())
                .build());

        } catch (NotFoundException | InvalidArgumentException ex) {
            // ignore
        }

        return sslCertificate;
    }
}
