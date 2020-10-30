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

import java.util.Set;

import com.google.api.services.storage.Storage;
import com.google.api.services.storage.model.BucketAccessControl;
import gyro.core.GyroUI;
import gyro.core.Type;
import gyro.core.resource.Output;
import gyro.core.resource.Resource;
import gyro.core.resource.Updatable;
import gyro.core.scope.State;
import gyro.core.validation.Regex;
import gyro.core.validation.Required;
import gyro.core.validation.ValidStrings;
import gyro.google.Copyable;
import gyro.google.GoogleResource;

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
     * The access permission for the entity.
     */
    @Required
    @Updatable
    @ValidStrings({ "OWNER", "READER", "WRITER" })
    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    /**
     * The entity holding the permission, in one of the following forms ``user-<userId>``, ``user-<email>``, ``group-<groupId>``, ``group-<email>``, ``domain-<domain>``, ``project-<team-projectId>``, ``allUsers``, or ``allAuthenticatedUsers``.
     * 
     * @no-doc Regex
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
    @Output
    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    /**
     * Unique ID for the resource.
     */
    @Output
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    /**
     * The link to this access-control entry.
     */
    @Output
    public String getSelfLink() {
        return selfLink;
    }

    public void setSelfLink(String selfLink) {
        this.selfLink = selfLink;
    }

    /**
     * The email address associated with the entity.
     */
    @Output
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * The ID for the entity.
     */
    @Output
    public String getEntityId() {
        return entityId;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    @Override
    public boolean doRefresh() throws Exception {
        Storage storage = createClient(Storage.class);
        BucketAccessControl acl = storage.bucketAccessControls()
            .get(getBucket().getName(), getEntity())
            .setUserProject(getUserProject())
            .execute();

        if (acl == null) {
            return false;
        }

        copyFrom(acl);

        return true;
    }

    @Override
    public void doCreate(GyroUI ui, State state) throws Exception {
        Storage storage = createClient(Storage.class);
        BucketAccessControl acl = new BucketAccessControl();

        acl.setBucket(getBucket().getName());
        acl.setEntity(getEntity());
        acl.setRole(getRole());

        copyFrom(storage.bucketAccessControls().insert(getBucket().getName(), acl)
            .setUserProject(getUserProject())
            .execute());
    }

    @Override
    public void doUpdate(GyroUI ui, State state, Resource current, Set<String> changedFieldNames) throws Exception {
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

        copyFrom(storage.bucketAccessControls().patch(getBucket().getName(), getEntity(), acl)
            .setUserProject(getUserProject())
            .execute());
    }

    @Override
    public void doDelete(GyroUI ui, State state) throws Exception {
        Storage storage = createClient(Storage.class);
        storage.bucketAccessControls().delete(getBucket().getName(), getEntity())
            .execute();
    }

    @Override
    public void copyFrom(BucketAccessControl model) {
        setRole(model.getRole());
        setId(model.getId());
        setSelfLink(model.getSelfLink());
        setDomain(model.getDomain());
        setEmail(model.getEmail());
        setEntityId(model.getEntityId());
    }
}
