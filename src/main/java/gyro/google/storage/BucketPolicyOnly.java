package gyro.google.storage;

import com.google.api.client.util.DateTime;
import com.google.api.services.storage.model.Bucket;
import gyro.core.resource.Diffable;
import gyro.core.resource.Updatable;
import gyro.google.Copyable;

/**
 * Subresource for setting the {@link Bucket.IamConfiguration.BucketPolicyOnly} configuration to a {@link Bucket}.
 *
 * Example
 * -------
 *
 * ..code-block:: gyro
 *
 *     iam-configuration
 *         bucket-policy-only
 *             enabled: true
 *             locked-time: '2022-02-10T11:11:11.453Z'
 *         end
 *     end
 */
public class BucketPolicyOnly extends Diffable implements Copyable<Bucket.IamConfiguration.BucketPolicyOnly> {

    private Boolean enabled;
    private String lockedTime;

    /**
     * When ``true`` access is controlled only by bucket-level or IAM policies.
     */
    @Updatable
    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * Deadline date for changing ``enabled`` from ``true`` to ``false``. As a RFC-3339 format string.
     */
    @Updatable
    public String getLockedTime() {
        return lockedTime;
    }

    public void setLockedTime(String lockedTime) {
        this.lockedTime = lockedTime;
    }

    @Override
    public void copyFrom(Bucket.IamConfiguration.BucketPolicyOnly model) {
        setEnabled(model.getEnabled());

        if (model.getLockedTime() != null) {
            setLockedTime(model.getLockedTime().toStringRfc3339());
        }
    }

    public Bucket.IamConfiguration.BucketPolicyOnly toIamConfigurationBucketPolicyOnly() {
        return new Bucket.IamConfiguration.BucketPolicyOnly()
                .setEnabled(getEnabled())
                .setLockedTime(getLockedTime() == null ? null : DateTime.parseRfc3339(getLockedTime()));
    }

    public static BucketPolicyOnly fromIamConfigurationBucketPolicyOnly(Bucket.IamConfiguration.BucketPolicyOnly model) {
        if (model != null) {
            BucketPolicyOnly policy = new BucketPolicyOnly();
            policy.setEnabled(model.getEnabled());
            policy.setLockedTime(model.getLockedTime() == null ? null : model.getLockedTime().toStringRfc3339());
            return policy;
        }
        return null;
    }
}
