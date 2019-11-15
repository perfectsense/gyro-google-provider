package gyro.google.storage;

import com.google.api.services.storage.model.Bucket;
import com.google.api.services.storage.model.Bucket.IamConfiguration.UniformBucketLevelAccess;
import gyro.core.resource.Diffable;
import gyro.core.resource.Updatable;
import gyro.google.Copyable;

/**
 * UniformBucketLevelAccess configuration for a Bucket.
 */
public class BucketUniformBucketLevelAccess extends Diffable implements Copyable<UniformBucketLevelAccess> {

    private Boolean enabled;
    private String lockedTime;

    /**
     * When ``true`` access is controlled only by bucket-level or above IAM policies.
     */
    @Updatable
    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * Deadline date for changing ``enabled`` from ``true`` to ``false``.
     */
    public String getLockedTime() {
        return lockedTime;
    }

    public void setLockedTime(String lockedTime) {
        this.lockedTime = lockedTime;
    }

    @Override
    public void copyFrom(UniformBucketLevelAccess model) {
        if (model != null) {
            setEnabled(model.getEnabled());

            if (model.getLockedTime() != null) {
                setLockedTime(model.getLockedTime().toStringRfc3339());
            }
        }
    }

    public UniformBucketLevelAccess toIamConfigurationUniformBucketLevelAccess() {
        return new Bucket.IamConfiguration.UniformBucketLevelAccess().setEnabled(getEnabled());
    }
}
