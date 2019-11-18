package gyro.google.storage;

import com.google.api.services.storage.model.BucketAccessControl;
import gyro.core.resource.Diffable;
import gyro.core.resource.Updatable;
import gyro.core.validation.ValidStrings;
import gyro.core.validation.ValidationError;
import gyro.google.Copyable;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Access controls on the bucket.
 */
public class BucketAccessControlConfiguration extends Diffable implements Copyable<BucketAccessControl> {

    private static final String ENTITY_REGEX = "\\b(allUsers|allAuthenticatedUsers)\\b|^(user|group|domain|project)-.*?";
    private static final Pattern ENTITY_PATTERN = Pattern.compile(ENTITY_REGEX);

    private String bucket;
    private String domain;
    private String email;
    private String entity;
    private String entityId;
    private BucketAccessControlProjectTeam projectTeam;
    private String role;

    /**
     * The name of the bucket.
     */
    @Updatable
    public String getBucket() {
        return bucket;
    }

    public void setBucket(String bucket) {
        this.bucket = bucket;
    }

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
     * The entity holding the permission, in one of the following forms "user-*userId*", "user-*email*", "group-*groupId*", "group-*email*", "domain-*domain*", "project-*team-projectId*", "allUsers", or "allAuthenticatedUsers".
     */
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
     * The access permission for the entity. Valid values are ``OWNER``, ``READER``, or ``WRITER``
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
        if (model != null) {
            setBucket(model.getBucket());
            setDomain(model.getDomain());
            setEmail(model.getEmail());
            setEntity(model.getEntity());
            setEntityId(model.getEntityId());

            BucketAccessControlProjectTeam bucketAccessControlProjectTeam = newSubresource(BucketAccessControlProjectTeam.class);
            bucketAccessControlProjectTeam.copyFrom(model.getProjectTeam());
            setProjectTeam(getProjectTeam());
        }
    }

    public BucketAccessControl toBucketAccessControl() {
        return new BucketAccessControl()
                .setBucket(getBucket())
                .setDomain(getDomain())
                .setEmail(getEmail())
                .setEntity(getEntity())
                .setEntityId(getEntityId())
                .setProjectTeam(getProjectTeam() == null ? null : getProjectTeam().toBucketAccessControlProjectTeam())
                .setRole(getRole());
    }

    @Override
    public List<ValidationError> validate() {
        List<ValidationError> errors = new ArrayList<>();

        if (getEntity() != null) {
            Matcher keyMatcher = ENTITY_PATTERN.matcher(getEntity());
            if (!keyMatcher.find()) {
                errors.add(new ValidationError(
                        this,
                        "entity",
                        String.format("Invalid format '%s'", getEntity())));
            }
        }
        return errors;
    }
}
