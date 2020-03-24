package gyro.google.compute;

import java.util.Set;

import com.google.api.services.compute.Compute;
import com.google.api.services.compute.model.Operation;
import com.google.api.services.compute.model.SslCertificate;
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
        Compute client = createComputeClient();

        SslCertificate sslCertificate = client.sslCertificates().get(getProjectId(), getName()).execute();
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

        Operation operation = client.sslCertificates().insert(getProjectId(), certificate).execute();
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

        Operation operation = client.sslCertificates().delete(getProjectId(), getName()).execute();
        waitForCompletion(client, operation);
    }
}
