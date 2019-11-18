package gyro.google.storage;

import com.google.api.services.storage.model.Bucket.IamConfiguration;
import gyro.core.resource.Diffable;
import gyro.core.resource.Updatable;
import gyro.google.Copyable;

/**
 * Subresource for setting the Bucket IamConfiguration configuration to a Bucket.
 */
public class BucketIamConfiguration extends Diffable implements Copyable<IamConfiguration> {

    private BucketUniformBucketLevelAccess uniformBucketLevelAccess;

    /**
     * The bucket's uniform bucket-level access configuration.
     *
     * @subresource gyro.google.storage.BucketUniformBucketLevelAccess
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
    public void copyFrom(IamConfiguration model) {
        if (model != null) {
            BucketUniformBucketLevelAccess bucketUniformBucketLevelAccess = newSubresource(BucketUniformBucketLevelAccess.class);
            bucketUniformBucketLevelAccess.copyFrom(model.getUniformBucketLevelAccess());
            setUniformBucketLevelAccess(bucketUniformBucketLevelAccess);
        }
    }

    public IamConfiguration toBucketIamConfiguration() {
        return new IamConfiguration()
                .setUniformBucketLevelAccess(getUniformBucketLevelAccess() == null ? null : getUniformBucketLevelAccess().toIamConfigurationUniformBucketLevelAccess());
    }
}
