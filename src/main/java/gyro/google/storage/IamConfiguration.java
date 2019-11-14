package gyro.google.storage;

import com.google.api.services.storage.model.Bucket;
import gyro.core.resource.Diffable;
import gyro.core.resource.Updatable;
import gyro.google.Copyable;

/**
 * Subresource for setting the Bucket IamConfiguration configuration to a Bucket.
 *
 * Example
 * -------
 *
 * ..code-block:: gyro
 *
 *     iam-configuration
 *         uniform-bucket-level-access
 *             enabled: false
 *         end
 *     end
 */
public class IamConfiguration extends Diffable implements Copyable<Bucket.IamConfiguration> {

    private UniformBucketLevelAccess uniformBucketLevelAccess;

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
    public String primaryKey() {
        return getUniformBucketLevelAccess().primaryKey();
    }

    @Override
    public void copyFrom(Bucket.IamConfiguration model) {
        setUniformBucketLevelAccess(UniformBucketLevelAccess.fromIamConfigurationUniformBucketLevelAccess(model.getUniformBucketLevelAccess()));
    }

    public Bucket.IamConfiguration toBucketIamConfiguration() {
        return new Bucket.IamConfiguration()
                .setUniformBucketLevelAccess(getUniformBucketLevelAccess() == null ? null : getUniformBucketLevelAccess().toIamConfigurationUniformBucketLevelAccess());
    }

    public static IamConfiguration fromBucketIamConfiguration(Bucket.IamConfiguration model) {
        if (model != null) {
            IamConfiguration configuration = new IamConfiguration();
            configuration.setUniformBucketLevelAccess(UniformBucketLevelAccess.fromIamConfigurationUniformBucketLevelAccess(model.getUniformBucketLevelAccess()));
            return configuration;
        }
        return null;
    }
}
