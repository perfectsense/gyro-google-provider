/*
 * Copyright 2024, Brightspot.
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

package gyro.google.sqladmin;

import java.io.IOException;
import java.util.Set;

import com.google.api.services.sqladmin.SQLAdmin;
import com.google.api.services.sqladmin.model.Operation;
import com.google.api.services.sqladmin.model.User;
import gyro.core.GyroUI;
import gyro.core.Type;
import gyro.core.resource.Resource;
import gyro.core.scope.State;
import gyro.core.validation.ValidStrings;
import gyro.google.Copyable;

@Type("sql-user")
public class UserResource extends SqlAdminResource implements Copyable<User> {

    private String dualPasswordType;

    private String host;

    private String instance;

    private String name;

    private String password;

    private UserPasswordValidationPolicy passwordPolicy;

    private String project;

    private SqlServerUserDetails sqlserverUserDetails;

    private String type;

    /**
     * Dual password status for the user.
     */
    @ValidStrings({
        "DUAL_PASSWORD_TYPE_UNSPECIFIED",
        "NO_MODIFY_DUAL_PASSWORD",
        "NO_DUAL_PASSWORD",
        "DUAL_PASSWORD"
    })
    public String getDualPasswordType() {
        return dualPasswordType;
    }

    public void setDualPasswordType(String dualPasswordType) {
        this.dualPasswordType = dualPasswordType;
    }

    /**
     * Optional. The host from which the user can connect. For `insert` operations, host defaults to an empty string.
     * For `update` operations, host is specified as part of the request URL.
     * The host name cannot be updated after insertion.
     * For a MySQL instance, it's required; for a PostgreSQL or SQL Server instance, it's optional.
     */
    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    /**
     * The name of the Cloud SQL instance. This does not include the project ID. Can be omitted for `update` because it is already specified on the URL.
     */
    public String getInstance() {
        return instance;
    }

    public void setInstance(String instance) {
        this.instance = instance;
    }

    /**
     * The name of the user in the Cloud SQL instance. Can be omitted for `update` because it is already specified in the URL.
     */
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * The password for the user.
     */
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * User level password validation policy.
     *
     * @subresource gyro.google.sqladmin.base.UserPasswordValidationPolicy
     */
    public UserPasswordValidationPolicy getPasswordPolicy() {
        return passwordPolicy;
    }

    public void setPasswordPolicy(UserPasswordValidationPolicy passwordPolicy) {
        this.passwordPolicy = passwordPolicy;
    }

    /**
     * The project ID of the project containing the Cloud SQL database.
     * The Google apps domain is prefixed if applicable.
     */
    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    /**
     * The user details for the SQL Server user. Required for SQL Server users.
     *
     * @subresource gyro.google.sqladmin.base.SqlServerUserDetails
     */
    public SqlServerUserDetails getSqlserverUserDetails() {
        return sqlserverUserDetails;
    }

    public void setSqlserverUserDetails(SqlServerUserDetails sqlserverUserDetails) {
        this.sqlserverUserDetails = sqlserverUserDetails;
    }

    /**
     * The user type. It determines the method to authenticate the user during login.
     * The default is the database's built-in user type.
     */
    @ValidStrings({
        "BUILT_IN",
        "CLOUD_IAM_USER",
        "CLOUD_IAM_SERVICE_ACCOUNT",
        "CLOUD_IAM_GROUP",
        "CLOUD_IAM_GROUP_USER",
        "CLOUD_IAM_GROUP_SERVICE_ACCOUNT"
    })
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public void copyFrom(User model) {
        setDualPasswordType(model.getDualPasswordType());
        setHost(model.getHost());
        setInstance(model.getInstance());
        setName(model.getName());
        setPassword(model.getPassword());
        setProject(model.getProject());
        setType(model.getType());

        setSqlserverUserDetails(null);
        if (model.getSqlserverUserDetails() != null) {
            SqlServerUserDetails details = newSubresource(SqlServerUserDetails.class);
            details.copyFrom(model.getSqlserverUserDetails());
            setSqlserverUserDetails(details);
        }

        setPasswordPolicy(null);
        if (model.getPasswordPolicy() != null) {
            UserPasswordValidationPolicy policy = newSubresource(UserPasswordValidationPolicy.class);
            policy.copyFrom(model.getPasswordPolicy());
            setPasswordPolicy(policy);
        }
    }

    @Override
    protected boolean doRefresh() {
        SQLAdmin client = createClient(SQLAdmin.class);

        try {
            User user = client.users().get(getProject(), getInstance(), getName()).execute();

            if (user != null) {
                copyFrom(user);
            }

        } catch (Exception ex) {
            // ignore
        }

        return false;
    }

    @Override
    protected void doCreate(GyroUI ui, State state) throws IOException {
        SQLAdmin client = createClient(SQLAdmin.class);

        User user = createOrUpdate(null, null);

        Operation operation = client.users().insert(getProject(), getInstance(), user).execute();

        waitForCompletion(operation, getProject(), client);

        doRefresh();
    }

    @Override
    protected void doUpdate(GyroUI ui, State state, Resource current, Set<String> changedFieldNames) throws IOException {
        SQLAdmin client = createClient(SQLAdmin.class);

        User currentUser = client.users().get(getProject(), getInstance(), getName()).execute();

        User user = createOrUpdate(currentUser, changedFieldNames);

        Operation operation = client.users().update(getProject(), getInstance(), user).execute();

        waitForCompletion(operation, getProject(), client);
    }

    @Override
    protected void doDelete(GyroUI ui, State state) throws IOException {
        SQLAdmin client = createClient(SQLAdmin.class);

        Operation operation = client.users().delete(getProject(), getInstance()).setName(getName()).execute();

        waitForCompletion(operation, getProject(), client);
    }

    private User createOrUpdate(User current, Set<String> changedFieldNames) {
        if (current == null) {
            // new user
            current = new User();
        } else {
            // update user
        }

        return current;
    }
}
