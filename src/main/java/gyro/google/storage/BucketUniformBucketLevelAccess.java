package gyro.google.storage;

import com.google.api.services.storage.model.Bucket;
import gyro.core.resource.Diffable;
import gyro.core.resource.Updatable;
import gyro.google.Copyable;

/**
 * Subresource for setting the Bucket IamConfiguration UniformBucketLevelAccess configuration to a Bucket.
 *
 * Example
 * -------
 *
 * ..code-block:: gyro
 *
 *     iam-configuration
 *         uniform-bucket-level-access
 *             enabled: true
 *         end
 *     end
 *
 */
public class BucketUniformBucketLevelAccess extends Diffable implements Copyable<Bucket.IamConfiguration.UniformBucketLevelAccess> {

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
    public void copyFrom(Bucket.IamConfiguration.UniformBucketLevelAccess model) {
        setEnabled(model.getEnabled());

        if (model.getLockedTime() != null) {
            setLockedTime(model.getLockedTime().toStringRfc3339());
        }
    }

    public Bucket.IamConfiguration.UniformBucketLevelAccess toIamConfigurationUniformBucketLevelAccess() {
        return new Bucket.IamConfiguration.UniformBucketLevelAccess().setEnabled(getEnabled());
    }

    public static BucketUniformBucketLevelAccess fromIamConfigurationUniformBucketLevelAccess(Bucket.IamConfiguration.UniformBucketLevelAccess model) {
        if (model != null) {
            BucketUniformBucketLevelAccess policy = new BucketUniformBucketLevelAccess();
            policy.setEnabled(model.getEnabled());
            policy.setLockedTime(model.getLockedTime() == null ? null : model.getLockedTime().toStringRfc3339());
            return policy;
        }
        return null;
    }
}
