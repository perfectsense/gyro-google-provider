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

import com.google.api.services.iam.v1.model.Role;
import gyro.core.resource.Id;
import gyro.core.resource.Output;
import gyro.core.resource.Updatable;
import gyro.core.validation.ValidStrings;
import gyro.google.Copyable;
import gyro.google.compute.ComputeResource;

public abstract class AbstractRoleResource extends ComputeResource implements Copyable<Role> {

    private String roleId;
    private String title;
    private String description;
    private List<String> includedPermissions;
    private String stage;

    // Read-only
    private String name;
    private Boolean deleted;

    /**
     * The role ID to use for this role. (Required)
     */
    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    /**
     * The name of the role.
     */
    @Id
    @Output
    public String getName() {
        return (this.name != null) ? this.name.substring(this.name.lastIndexOf("roles/")) : null;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * An optional title of this role.
     */
    @Updatable
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * An optional description of this role.
     */
    @Updatable
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * The names of the permissions this role grants when bound in an IAM policy.
     */
    @Updatable
    public List<String> getIncludedPermissions() {
        if (includedPermissions == null) {
            includedPermissions = new ArrayList<>();
        }
        return includedPermissions;
    }

    public void setIncludedPermissions(List<String> includedPermissions) {
        this.includedPermissions = includedPermissions;
    }

    /**
     * The current launch stage of the role. Valid values are: ``ALPHA``, ``BETA``, ``GA``, ``DEPRECATED`` or ``EAP``. Defaults to ``ALPHA``.
     */
    @Updatable
    @ValidStrings({ "ALPHA", "BETA", "GA", "DEPRECATED", "DISABLED", "EAP" })
    public String getStage() {
        return stage;
    }

    public void setStage(String stage) {
        this.stage = stage;
    }

    /**
     * The current deleted state of the role.
     */
    @Output
    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    @Override
    public void copyFrom(Role model) throws Exception {
        setName(model.getName());
        setDeleted(model.getDeleted());
        setDescription(model.getDescription());
        setIncludedPermissions(model.getIncludedPermissions());
        setTitle(model.getTitle());

        if (model.getStage() != null) {
            setStage(model.getStage());
        }
    }

    public Role toRole() {
        return new Role()
            .setName(getName() != null ? "projects/" + getProjectId() + "/" + getName() : null)
            .setDescription(getDescription())
            .setTitle(getTitle())
            .setIncludedPermissions(getIncludedPermissions())
            .setDeleted(getDeleted())
            .setStage(getStage());
    }
}
