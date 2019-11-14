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
public class BucketIamConfiguration extends Diffable implements Copyable<Bucket.IamConfiguration> {

    private BucketUniformBucketLevelAccess uniformBucketLevelAccess;

    /**
     * The bucket's uniform bucket-level access configuration.
     *
     * @subresource gyro.google.storage.UniformBucketLevelAccess
     */
    @Updatable
    public BucketUniformBucketLevelAccess getUniformBucketLevelAccess() {
        return uniformBucketLevelAccess;
    }

    public void setUniformBucketLevelAccess(BucketUniformBucketLevelAccess uniformBucketLevelAccess) {
        this.uniformBucketLevelAccess = uniformBucketLevelAccess;
    }

    @Override
    public String primaryKey() {
        return getUniformBucketLevelAccess().primaryKey();
    }

    @Override
    public void copyFrom(Bucket.IamConfiguration model) {
        setUniformBucketLevelAccess(BucketUniformBucketLevelAccess.fromIamConfigurationUniformBucketLevelAccess(model.getUniformBucketLevelAccess()));
    }

    public Bucket.IamConfiguration toGcpBucketIamConfiguration() {
        return new Bucket.IamConfiguration()
                .setUniformBucketLevelAccess(getUniformBucketLevelAccess() == null ? null : getUniformBucketLevelAccess().toIamConfigurationUniformBucketLevelAccess());
    }

    public static BucketIamConfiguration fromGcpBucketIamConfiguration(Bucket.IamConfiguration model) {
        if (model != null) {
            BucketIamConfiguration configuration = new BucketIamConfiguration();
            configuration.setUniformBucketLevelAccess(BucketUniformBucketLevelAccess.fromIamConfigurationUniformBucketLevelAccess(model.getUniformBucketLevelAccess()));
            return configuration;
        }
        return null;
    }
}
