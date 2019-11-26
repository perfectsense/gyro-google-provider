/*
 * Copyright 2019, Perfect Sense, Inc.
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

package gyro.google.storage;

import com.google.api.services.storage.model.BucketAccessControl;
import gyro.core.resource.Diffable;
import gyro.core.resource.Updatable;
import gyro.core.validation.Regex;
import gyro.core.validation.ValidStrings;
import gyro.google.Copyable;

/**
 * Access controls on the bucket.
 */
public class BucketAccessControlConfiguration extends Diffable implements Copyable<BucketAccessControl> {

    private static final String ENTITY_REGEX = "\\b(allUsers|allAuthenticatedUsers)\\b|^(user|group|domain|project)-.*?";

    private String domain;
    private String email;
    private String entity;
    private String entityId;
    private BucketAccessControlProjectTeam projectTeam;
    private String role;

    /**
     * The domain associated with the entity.
     */
    @Updatable
    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    /**
     * The email address associated with the entity.
     */
    @Updatable
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * The entity holding the permission, in one of the following forms ``user-<userId>``, ``user-<email>``, ``group-<groupId>``, ``group-<email>``, ``domain-<domain>``, ``project-<team-projectId>``, ``allUsers``, or ``allAuthenticatedUsers``.
     */
    @Regex(ENTITY_REGEX)
    @Updatable
    public String getEntity() {
        return entity;
    }

    public void setEntity(String entity) {
        this.entity = entity;
    }

    /**
     * The ID for the entity.
     */
    @Updatable
    public String getEntityId() {
        return entityId;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    /**
     * The project team associated with the entity.
     */
    @Updatable
    public BucketAccessControlProjectTeam getProjectTeam() {
        return projectTeam;
    }

    public void setProjectTeam(BucketAccessControlProjectTeam projectTeam) {
        this.projectTeam = projectTeam;
    }

    /**
     * The access permission for the entity. Valid values are ``OWNER``, ``READER``, or ``WRITER``.
     */
    @Updatable
    @ValidStrings({"OWNER", "READER", "WRITER"})
    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    @Override
    public void copyFrom(BucketAccessControl model) {
        setDomain(model.getDomain());
        setEmail(model.getEmail());
        setEntity(model.getEntity());
        setEntityId(model.getEntityId());

        if (model.getProjectTeam() != null) {
            BucketAccessControlProjectTeam bucketAccessControlProjectTeam = newSubresource(BucketAccessControlProjectTeam.class);
            bucketAccessControlProjectTeam.copyFrom(model.getProjectTeam());
            setProjectTeam(getProjectTeam());
        }
    }

    public BucketAccessControl toBucketAccessControl() {
        return new BucketAccessControl()
                .setDomain(getDomain())
                .setEmail(getEmail())
                .setEntity(getEntity())
                .setEntityId(getEntityId())
                .setProjectTeam(getProjectTeam() == null ? null : getProjectTeam().toBucketAccessControlProjectTeam())
                .setRole(getRole());
    }
}
