package gyro.google.iam;

import java.util.List;
import java.util.Map;

import com.google.api.services.iam.v1.Iam;
import com.google.api.services.iam.v1.model.ServiceAccount;
import gyro.core.Type;
import gyro.google.GoogleFinder;

/**
 * Query service account.
 *
 * Example
 * -------
 *
 * .. code-block:: gyro
 *
 *    service-account: $(external-query google::service-account {})
 */
@Type("service-account")
public class ServiceAccountFinder extends GoogleFinder<Iam, ServiceAccount, ServiceAccountResource> {

    @Override
    protected List<ServiceAccount> findAllGoogle(Iam client) throws Exception {
        return client.projects().serviceAccounts().list("projects/" + getProjectId()).execute().getAccounts();
    }

    @Override
    protected List<ServiceAccount> findGoogle(Iam client, Map<String, String> filters) throws Exception {
        throw new UnsupportedOperationException("Finding `service-accounts` with filters is not supported!!");
    }
}
