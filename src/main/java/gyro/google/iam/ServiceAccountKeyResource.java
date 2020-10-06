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

package gyro.google.iam;

import java.util.Set;

import com.google.api.services.iam.v1.Iam;
import com.google.api.services.iam.v1.model.CreateServiceAccountKeyRequest;
import com.google.api.services.iam.v1.model.ServiceAccountKey;
import gyro.core.GyroUI;
import gyro.core.Type;
import gyro.core.resource.Id;
import gyro.core.resource.Output;
import gyro.core.resource.Resource;
import gyro.core.scope.State;
import gyro.core.validation.Required;
import gyro.google.Copyable;
import gyro.google.GoogleResource;
import gyro.google.util.Utils;

/**
 * Creates a service account key.
 *
 * Example
 * -------
 *
 * .. code-block:: gyro
 *
 *      google::service-account-key example-service-account-key
 *          service-account: $(google::service-account example-service-account)
 *      end
 */
@Type("service-account-key")
public class ServiceAccountKeyResource extends GoogleResource implements Copyable<ServiceAccountKey> {

    private ServiceAccountResource serviceAccount;

    // Read-only
    private String id;

    /**
     * The service account for which a key should be a created.
     */
    @Required
    public ServiceAccountResource getServiceAccount() {
        return serviceAccount;
    }

    public void setServiceAccount(ServiceAccountResource serviceAccount) {
        this.serviceAccount = serviceAccount;
    }

    /**
     * The ID of the service account key.
     */
    @Id
    @Output
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public void copyFrom(ServiceAccountKey model) throws Exception {
        setId(model.getName());
        setServiceAccount(findById(
            ServiceAccountResource.class,
            Utils.getServiceAccountIdFromName(Utils.getServiceAccountNameFromId(model.getName()), getProjectId())));
    }

    @Override
    protected boolean doRefresh() throws Exception {
        Iam client = createClient(Iam.class);

        ServiceAccountKey serviceAccountKey = client.projects()
            .serviceAccounts()
            .keys()
            .list(getServiceAccount().getId())
            .execute()
            .getKeys().stream().filter(r -> r.getName().equals(getId())).findFirst().orElse(null);

        if (serviceAccountKey == null) {
            return false;
        }

        copyFrom(serviceAccountKey);

        return true;
    }

    @Override
    protected void doCreate(GyroUI ui, State state) throws Exception {
        Iam client = createClient(Iam.class);

        ServiceAccountKey response = client.projects()
            .serviceAccounts()
            .keys()
            .create(getServiceAccount().getId(), new CreateServiceAccountKeyRequest())
            .execute();

        copyFrom(response);
    }

    @Override
    protected void doUpdate(
        GyroUI ui, State state, Resource current, Set<String> changedFieldNames) throws Exception {

    }

    @Override
    protected void doDelete(GyroUI ui, State state) throws Exception {
        Iam client = createClient(Iam.class);

        client.projects().serviceAccounts().keys().delete(getId()).execute();
    }
}
