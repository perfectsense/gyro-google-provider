package gyro.google.iam;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.api.services.iam.v1.Iam;
import com.google.api.services.iam.v1.model.ServiceAccount;
import gyro.core.Type;
import gyro.google.GoogleFinder;
import gyro.google.util.Utils;

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

    private String name;
    private String displayName;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    @Override
    protected List<ServiceAccount> findAllGoogle(Iam client) throws Exception {
        return client.projects().serviceAccounts().list("projects/" + getProjectId()).execute().getAccounts();
    }

    @Override
    protected List<ServiceAccount> findGoogle(Iam client, Map<String, String> filters) throws Exception {
        List<ServiceAccount> accounts = client.projects()
            .serviceAccounts()
            .list("projects/" + getProjectId())
            .execute()
            .getAccounts();

        if (filters.containsKey("name")) {
            accounts = accounts.stream()
                .filter(a -> Utils.getServiceAccountNameFromId(a.getName()).equals(filters.get("name")))
                .collect(
                    Collectors.toList());
        }

        if (filters.containsKey("display-name")) {
            accounts = accounts.stream()
                .filter(a -> a.getDisplayName().equals(filters.get("display-name"))).collect(Collectors.toList());
        }

        return accounts;
    }
}
