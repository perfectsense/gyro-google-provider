package gyro.google.storage;

import com.google.api.services.storage.model.Bucket;
import gyro.core.resource.Diffable;
import gyro.core.resource.Updatable;
import gyro.google.Copyable;

/**
 * Subresource for setting the {@link Bucket.IamConfiguration} configuration to a {@link Bucket}.
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
public class IamConfiguration extends Diffable implements Copyable<Bucket.IamConfiguration> {

    private BucketPolicyOnly bucketPolicyOnly;
    private UniformBucketLevelAccess uniformBucketLevelAccess;

    /**
     * The bucket's Bucket Policy Only configuration.
     *
     * @subresource gyro.google.storage.BucketPolicyOnly
     */
    @Updatable
    public BucketPolicyOnly getBucketPolicyOnly() {
        return bucketPolicyOnly;
    }

    public void setBucketPolicyOnly(BucketPolicyOnly bucketPolicyOnly) {
        this.bucketPolicyOnly = bucketPolicyOnly;
    }

    /**
     * The bucket's uniform bucket-level access configuration.
     *
     * @subresource gyro.google.storage.UniformBucketLevelAccess
     */
    @Updatable
    public UniformBucketLevelAccess getUniformBucketLevelAccess() {
        return uniformBucketLevelAccess;
    }

    public void setUniformBucketLevelAccess(UniformBucketLevelAccess uniformBucketLevelAccess) {
        this.uniformBucketLevelAccess = uniformBucketLevelAccess;
    }

    @Override
    public void copyFrom(Bucket.IamConfiguration model) {
        setBucketPolicyOnly(BucketPolicyOnly.fromIamConfigurationBucketPolicyOnly(model.getBucketPolicyOnly()));
        setUniformBucketLevelAccess(UniformBucketLevelAccess.fromIamConfigurationUniformBucketLevelAccess(model.getUniformBucketLevelAccess()));
    }

    public Bucket.IamConfiguration toBucketIamConfiguration() {
        return new Bucket.IamConfiguration()
                .setBucketPolicyOnly(getBucketPolicyOnly() == null ? null : getBucketPolicyOnly().toIamConfigurationBucketPolicyOnly())
                .setUniformBucketLevelAccess(getUniformBucketLevelAccess() == null ? null : getUniformBucketLevelAccess().toIamConfigurationUniformBucketLevelAccess());
    }

    public static IamConfiguration fromBucketIamConfiguration(Bucket.IamConfiguration model) {
        if (model != null) {
            IamConfiguration configuration = new IamConfiguration();
            configuration.setBucketPolicyOnly(BucketPolicyOnly.fromIamConfigurationBucketPolicyOnly(model.getBucketPolicyOnly()));
            configuration.setUniformBucketLevelAccess(UniformBucketLevelAccess.fromIamConfigurationUniformBucketLevelAccess(model.getUniformBucketLevelAccess()));
            return configuration;
        }
        return null;
    }
}
