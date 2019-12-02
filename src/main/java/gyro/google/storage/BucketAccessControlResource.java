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

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.storage.Storage;
import com.google.api.services.storage.model.BucketAccessControl;
import gyro.core.GyroException;
import gyro.core.GyroUI;
import gyro.core.Type;
import gyro.core.resource.Id;
import gyro.core.resource.Resource;
import gyro.core.resource.Updatable;
import gyro.core.scope.State;
import gyro.core.validation.Regex;
import gyro.core.validation.Required;
import gyro.core.validation.ValidStrings;
import gyro.google.Copyable;
import gyro.google.GoogleResource;

import java.io.IOException;
import java.util.Set;

/**
 *
 * Example
 * -------
 *
 * .. code-block:: gyro
 *
 *     google::bucket 'acl-bucket'
 *         name: 'acl-example'
 *         location: 'US-CENTRAL1'
 *     end
 *
 *     google::acl 'acl-one'
 *        bucket: $(google::bucket acl-bucket)
 *        entity: 'domain-brightspot.com'
 *        role: 'OWNER'
 *     end
 *
 *     google::acl 'acl-two'
 *        bucket: $(google::bucket acl-bucket)
 *        entity: 'domain-sample.com'
 *        role: 'READER'
 *     end
 */
@Type("acl")
public class BucketAccessControlResource extends GoogleResource implements Copyable<BucketAccessControl> {
    
    private static final String ENTITY_REGEX = "\\b(allUsers|allAuthenticatedUsers)\\b|^(user|group|domain|project)-.*?";

    private BucketResource bucket;
    private String role;
    private String entity;
    private String userProject;
    private String id;
    private String selfLink;
    private String domain;
    private String email;
    private String entityId;
    private BucketAccessControlProjectTeam projectTeam;

    /**
     * The associated Bucket.
     */
    @Required
    public BucketResource getBucket() {
        return bucket;
    }

    public void setBucket(BucketResource bucket) {
        this.bucket = bucket;
    }
    
    /**
     * The access permission for the entity. Valid values are ``OWNER``, ``READER``, or ``WRITER``.
     */
    @Required
    @Updatable
    @ValidStrings({"OWNER", "READER", "WRITER"})
    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    /**
     * The entity holding the permission, in one of the following forms ``user-<userId>``, ``user-<email>``, ``group-<groupId>``, ``group-<email>``, ``domain-<domain>``, ``project-<team-projectId>``, ``allUsers``, or ``allAuthenticatedUsers``.
     */
    @Regex(ENTITY_REGEX)
    @Required
    @Updatable
    public String getEntity() {
        return entity;
    }

    public void setEntity(String entity) {
        this.entity = entity;
    }

    /**
     * The project to be billed for this request.
     */
    @Updatable
    public String getUserProject() {
        return userProject;
    }

    public void setUserProject(String userProject) {
        this.userProject = userProject;
    }

    /**
     * The domain associated with the entity.
     */
    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    /**
     * Unique ID for the resource.
     */
    @Id
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    
    /**
     * The link to this access-control entry.
     */
    public String getSelfLink() {
        return selfLink;
    }

    public void setSelfLink(String selfLink) {
        this.selfLink = selfLink;
    }
    
    /**
     * The email address associated with the entity.
     */
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * The ID for the entity.
     */
    public String getEntityId() {
        return entityId;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    /**
     * The project team configuration associated with the entity.
     */
    public BucketAccessControlProjectTeam getProjectTeam() {
        return projectTeam;
    }

    public void setProjectTeam(BucketAccessControlProjectTeam projectTeam) {
        this.projectTeam = projectTeam;
    }

    @Override
    public boolean refresh() {
        Storage storage = createClient(Storage.class);

        try {
            BucketAccessControl acl = storage.bucketAccessControls()
                    .get(getBucket().getName(), getEntity())
                    .setUserProject(getUserProject())
                    .execute();

            if (acl == null) {
                return false;
            }

            copyFrom(acl);

            return true;
        } catch (GoogleJsonResponseException e) {
            if (e.getDetails().getCode() == 404) {
                return false;
            } else {
                throw new GyroException(e.getDetails().getMessage());
            }
        } catch (IOException e) {
            throw new GyroException(e.getMessage());
        }
    }

    @Override
    public void create(GyroUI ui, State state) throws Exception {
        Storage storage = createClient(Storage.class);

        BucketAccessControl acl = new BucketAccessControl();
        acl.setBucket(getBucket().getName());
        acl.setEntity(getEntity());
        acl.setRole(getRole());

        try {
            copyFrom(storage.bucketAccessControls().insert(getBucket().getName(), acl).execute());
        } catch (GoogleJsonResponseException e) {
            throw new GyroException(e.getDetails().getMessage());
        } catch (Exception e) {
            throw new GyroException(e.getMessage());
        }
    }

    @Override
    public void update(GyroUI ui, State state, Resource current, Set<String> changedFieldNames) throws Exception {
        Storage storage = createClient(Storage.class);
        BucketAccessControl acl = new BucketAccessControl();

        if (changedFieldNames.contains("bucket")) {
            acl.setRole(getBucket().getName());
        }

        if (changedFieldNames.contains("entity")) {
            acl.setEntity(getEntity());
        }

        if (changedFieldNames.contains("role")) {
            acl.setRole(getRole());
        }

        try {
            copyFrom(storage.bucketAccessControls().patch(getBucket().getName(), getEntity(), acl).execute());
        } catch (GoogleJsonResponseException e) {
            throw new GyroException(e.getDetails().getMessage());
        }
    }

    @Override
    public void delete(GyroUI ui, State state) throws Exception {
        Storage storage = createClient(Storage.class);

        try {
            storage.bucketAccessControls().delete(getBucket().getName(), getEntity()).execute();
        } catch (GoogleJsonResponseException e) {
            throw new GyroException(e.getDetails().getMessage());
        }
    }

    @Override
    public void copyFrom(BucketAccessControl model) {
        setRole(model.getRole());
        setId(model.getId());
        setSelfLink(model.getSelfLink());
        setDomain(model.getDomain());
        setEmail(model.getEmail());
        setEntityId(model.getEntityId());

        if (model.getProjectTeam() != null) {
            BucketAccessControlProjectTeam projectTeam = newSubresource(BucketAccessControlProjectTeam.class);
            projectTeam.copyFrom(model.getProjectTeam());
            setProjectTeam(projectTeam);
        }
    }
}
