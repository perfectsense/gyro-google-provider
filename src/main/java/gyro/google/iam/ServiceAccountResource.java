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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.api.services.cloudresourcemanager.CloudResourceManager;
import com.google.api.services.cloudresourcemanager.model.Binding;
import com.google.api.services.cloudresourcemanager.model.GetIamPolicyRequest;
import com.google.api.services.cloudresourcemanager.model.Policy;
import com.google.api.services.cloudresourcemanager.model.SetIamPolicyRequest;
import com.google.api.services.iam.v1.Iam;
import com.google.api.services.iam.v1.model.CreateServiceAccountRequest;
import com.google.api.services.iam.v1.model.DisableServiceAccountRequest;
import com.google.api.services.iam.v1.model.EnableServiceAccountRequest;
import com.google.api.services.iam.v1.model.ServiceAccount;
import gyro.core.GyroUI;
import gyro.core.Type;
import gyro.core.resource.Id;
import gyro.core.resource.Output;
import gyro.core.resource.Resource;
import gyro.core.resource.Updatable;
import gyro.core.scope.State;
import gyro.core.validation.Regex;
import gyro.core.validation.Required;
import gyro.google.Copyable;
import gyro.google.GoogleResource;
import gyro.google.util.Utils;

/**
 * Creates a service account.
 *
 * Example
 * -------
 *
 * .. code-block:: gyro
 *
 *      google::service-account example-service-account
 *          name: "example-service-account"
 *          display-name: "example-service-account"
 *          description: "example service account"
 *      end
 */
@Type("service-account")
public class ServiceAccountResource extends GoogleResource implements Copyable<ServiceAccount> {

    private String displayName;
    private String description;
    private String name;
    private Boolean enableAccount;
    private List<ServiceAccountRole> serviceAccountRole;

    // Read-only
    private String id;
    private String email;

    /**
     * The friendly display name for the service account. (Required)
     */
    @Updatable
    @Required
    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    /**
     * The optional description of the service account.
     */
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * The unique name of the service account. It can be a string starting with a lowercase letter followed by lower case alphanumerical characters or hyphens and between 6 and 30 characters. (Required)
     */
    @Required
    @Regex(value = "^[a-z]([a-z]|[0-9]|-){5,29}$", message = "a string starting with a lowercase letter followed by lower case alphanumerical characters or hyphens and between 6 and 30 characters.")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * Enable or disable the service account. Defaults to ``enable``.
     */
    @Updatable
    public Boolean getEnableAccount() {
        return enableAccount;
    }

    public void setEnableAccount(Boolean enableAccount) {
        this.enableAccount = enableAccount;
    }

    /**
     * A list of roles to attach to the service account.
     */
    @Updatable
    public List<ServiceAccountRole> getServiceAccountRole() {
        if (serviceAccountRole == null) {
            serviceAccountRole = new ArrayList<>();
        }

        return serviceAccountRole;
    }

    public void setServiceAccountRole(List<ServiceAccountRole> serviceAccountRole) {
        this.serviceAccountRole = serviceAccountRole;
    }

    /**
     * The ID of the service account.
     */
    @Id
    @Output
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    /**
     * The email address of the service account.
     */
    @Output
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public void copyFrom(ServiceAccount model) throws Exception {
        copyFrom(model, true);
    }

    @Override
    protected boolean doRefresh() throws Exception {
        Iam client = createClient(Iam.class);

        ServiceAccount serviceAccount = client.projects()
            .serviceAccounts()
            .list(String.format("projects/%s", getProjectId()))
            .execute()
            .getAccounts().stream().filter(r -> r.getName().equals(getId())).findFirst().orElse(null);

        if (serviceAccount == null) {
            return false;
        }

        copyFrom(serviceAccount);

        return true;
    }

    @Override
    protected void doCreate(GyroUI ui, State state) throws Exception {
        Iam client = createClient(Iam.class);

        ServiceAccount serviceAccount = new ServiceAccount();
        serviceAccount.setDisplayName(getDisplayName());

        if (getDescription() != null) {
            serviceAccount.setDescription(getDescription());
        }

        CreateServiceAccountRequest request = new CreateServiceAccountRequest();
        request.setAccountId(getName());
        request.setServiceAccount(serviceAccount);

        ServiceAccount response = client.projects()
            .serviceAccounts()
            .create(String.format("projects/%s", getProjectId()), request)
            .execute();

        copyFrom(response, false);
        state.save();

        if (getEnableAccount() != null && getEnableAccount().equals(Boolean.FALSE)) {
            changeServiceAccountStatus(client);
        }

        state.save();

        if (!getServiceAccountRole().isEmpty()) {
            manageIamPolicies();
        }
    }

    @Override
    protected void doUpdate(
        GyroUI ui, State state, Resource current, Set<String> changedFieldNames) throws Exception {
        Iam client = createClient(Iam.class);

        if (changedFieldNames.contains("enable-account")) {
            changeServiceAccountStatus(client);
        }

        if (changedFieldNames.contains("display-name")) {
            ServiceAccount serviceAccount =
                client.projects()
                    .serviceAccounts()
                    .get(getId())
                    .execute();

            serviceAccount.setDisplayName(getDisplayName());

            client.projects().serviceAccounts().update(getId(), serviceAccount).execute();
        }

        if (changedFieldNames.contains("service-account-role")) {
            manageIamPolicies();
        }
    }

    @Override
    protected void doDelete(GyroUI ui, State state) throws Exception {
        Iam client = createClient(Iam.class);

        getServiceAccountRole().clear();

        // Revoke granted roles
        manageIamPolicies();

        client.projects()
            .serviceAccounts()
            .delete(getId())
            .execute();
    }

    private void changeServiceAccountStatus(Iam client) throws IOException {
        if (getEnableAccount() == null || getEnableAccount().equals(Boolean.TRUE)) {
            client.projects()
                .serviceAccounts()
                .enable(getId(), new EnableServiceAccountRequest())
                .execute();

        } else {
            client.projects()
                .serviceAccounts()
                .disable(getId(), new DisableServiceAccountRequest())
                .execute();
        }
    }

    private void manageIamPolicies() throws IOException {
        CloudResourceManager client = createClient(CloudResourceManager.class);

        Policy policy = client.projects().getIamPolicy(getProjectId(), new GetIamPolicyRequest()).execute();
        List<Binding> currentBindings = policy.getBindings();
        List<Binding> newBindings = new ArrayList<>();
        String member = String.format("serviceAccount:%s", getEmail());

        for (Binding b : currentBindings) {
            if (b.getMembers().contains(member) && !b.getRole().equals("roles/owner")) {
                List<String> members = b.getMembers()
                    .stream()
                    .filter(m -> !m.equals(member))
                    .collect(Collectors.toList());

                if (!members.isEmpty()) {
                    Binding binding = new Binding();
                    binding.setMembers(members);
                    binding.setRole(b.getRole());
                    binding.setCondition(b.getCondition());
                    newBindings.add(binding);
                }

            } else {
                newBindings.add(b);
            }
        }

        for (ServiceAccountRole r : getServiceAccountRole()) {
            Binding binding = new Binding();
            binding.setMembers(Collections.singletonList(member));
            binding.setRole(r.getRoleName());

            if (r.getCondition() != null) {
                binding.setCondition(r.getCondition().toExpr());
            }

            newBindings.add(binding);
        }

        policy.setBindings(newBindings);
        policy.setVersion(3);
        SetIamPolicyRequest setIamPolicyRequest = new SetIamPolicyRequest();
        setIamPolicyRequest.setPolicy(policy);

        client.projects().setIamPolicy(getProjectId(), setIamPolicyRequest).execute();
    }

    private void copyFrom(ServiceAccount model, Boolean refreshRolesAndStatus) throws Exception {
        setId(model.getName());
        setEmail(model.getEmail());
        setName(Utils.getServiceAccountNameFromId(model.getName()));
        setDescription(model.getDescription());
        setDisplayName(model.getDisplayName());

        if (refreshRolesAndStatus) {
            setEnableAccount((model.getDisabled() == null || model.getDisabled().equals(Boolean.FALSE))
                ? Boolean.TRUE
                : Boolean.FALSE);
            refreshRoles();
        }
    }

    private void refreshRoles() throws Exception {
        CloudResourceManager client = createClient(CloudResourceManager.class);
        Policy policy = client.projects().getIamPolicy(getProjectId(), new GetIamPolicyRequest()).execute();

        List<ServiceAccountRole> tempRoles = new ArrayList<>(getServiceAccountRole());

        getServiceAccountRole().clear();

        String member = String.format("serviceAccount:%s", getEmail());

        List<Binding> bindings = policy.getBindings()
            .stream()
            .filter(b -> b.getMembers().contains(member) && !b.getRole().equals("roles/owner"))
            .collect(Collectors.toList());

        for (Binding b : bindings) {
            ServiceAccountRole role = newSubresource(ServiceAccountRole.class);
            String roleId = Utils.removeConditionFromRoleId(b.getRole());

            if (Utils.isRoleIdForCustomRole(roleId)) {
                role.setCustomRole(findById(RoleCustomProjectRoleResource.class, roleId));

            } else {
                role.setPredefinedRole(findById(RolePredefinedRoleResource.class, roleId));
            }

            if (b.getCondition() != null) {
                Expr expr = newSubresource(Expr.class);
                expr.copyFrom(b.getCondition());
                role.setCondition(expr);

            } else {
                ServiceAccountRole roleToAdd = tempRoles.stream()
                    .filter(s -> s.getRoleName().equals(roleId))
                    .findFirst()
                    .orElse(null);

                if (roleToAdd != null && roleToAdd.getCondition() != null) {
                    role.setCondition(roleToAdd.getCondition());
                }
            }

            getServiceAccountRole().add(role);
        }

    }

    public com.google.api.services.compute.model.ServiceAccount toComputeServiceAccount() {
        com.google.api.services.compute.model.ServiceAccount serviceAccount = new com.google.api.services.compute.model.ServiceAccount();
        serviceAccount.setEmail(Utils.getServiceAccountEmailFromId(getId()));

        return serviceAccount;
    }
}
