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
import com.google.api.services.iam.v1.model.CreateRoleRequest;
import com.google.api.services.iam.v1.model.Role;
import gyro.core.GyroUI;
import gyro.core.Type;
import gyro.core.resource.Resource;
import gyro.core.scope.State;

/**
 * Creates a custom project role.
 *
 * Example
 * -------
 *
 * .. code-block:: gyro
 *
 *      google::iam-role-custom-project 'iam-role-custom-project-example'
 *          role-id: "role.custom.proj.example"
 *          title: "role-custom-proj-example-title"
 *          description: "role-custom-proj-example-desc"
 *          included-permissions: ['accessapproval.requests.approve', 'accessapproval.requests.get']
 *          stage: "GA"
 *      end
 */
@Type("iam-role-custom-project")
public class RoleCustomProjectRoleResource extends AbstractRoleResource {

    @Override
    protected void doCreate(GyroUI ui, State state) throws Exception {
        Iam client = createClient(Iam.class);
        Role role = toRole();
        CreateRoleRequest request = new CreateRoleRequest();
        request.setRole(role);
        request.setRoleId(getRoleId());
        client.projects().roles().create("projects/" + getProjectId(), request).execute();

        refresh();
    }

    @Override
    protected void doUpdate(
        GyroUI ui, State state, Resource current, Set<String> changedFieldNames) throws Exception {
        Iam client = createClient(Iam.class);
        client.projects()
            .roles()
            .patch(
                "projects/" + getProjectId() + "/roles/" + getRoleId(),
                toRole())
            .execute();
    }

    @Override
    protected boolean doRefresh() throws Exception {
        Iam client = createClient(Iam.class);
        copyFrom(client.projects()
            .roles()
            .get("projects/" + getProjectId() + "/roles/" + getRoleId())
            .execute());
        return true;
    }

    @Override
    protected void doDelete(GyroUI ui, State state) throws Exception {
        Iam client = createClient(Iam.class);
        client.projects()
            .roles()
            .delete("projects/" + getProjectId() + "/roles/" + getRoleId())
            .execute();
    }
}
