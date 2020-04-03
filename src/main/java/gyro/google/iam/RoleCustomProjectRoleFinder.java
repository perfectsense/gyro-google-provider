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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.api.services.iam.v1.Iam;
import com.google.api.services.iam.v1.model.ListRolesResponse;
import com.google.api.services.iam.v1.model.Role;
import gyro.core.Type;
import gyro.google.GoogleFinder;

/**
 * Query role.
 *
 * Example
 * -------
 *
 * .. code-block:: gyro
 *
 *    role-custom-project: $(external-query google::iam-role-custom-project { name: 'projects/[PROJECT_NAME]/roles/role.custom.example' })
 */
@Type("iam-role-custom-project")
public class RoleCustomProjectRoleFinder extends GoogleFinder<Iam, Role, RoleCustomProjectRoleResource> {

    private String name;

    /**
     * The name of the role. Should follow the pattern: ``roles/ROLE_NAME``.
     */
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    protected List<Role> findAllGoogle(Iam client) throws Exception {
        List<Role> roles = new ArrayList<>();
        ListRolesResponse rolesResponse;
        String nextPageToken = null;

        do {
            rolesResponse = client.projects()
                .roles()
                .list("projects/" + getProjectId()).set("view", "FULL")
                .set("showDeleted", true)
                .setPageToken(nextPageToken).execute();

            nextPageToken = rolesResponse.getNextPageToken();
            if (rolesResponse.getRoles() != null) {
                roles.addAll(rolesResponse.getRoles());
            }
        } while (nextPageToken != null);

        return roles;
    }

    @Override
    protected List<Role> findGoogle(Iam client, Map<String, String> filters) throws Exception {
        List<Role> roles = new ArrayList<>();
        if (filters.containsKey("name")) {
            roles.add(client.projects()
                .roles()
                .get(filters.get("name")).execute());
        }

        return roles;
    }
}
