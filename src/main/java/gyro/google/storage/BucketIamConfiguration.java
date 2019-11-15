package gyro.google.storage;

import com.google.api.services.storage.model.Bucket;
import gyro.core.resource.Diffable;
import gyro.core.resource.Updatable;
import gyro.google.Copyable;

/**
 * Subresource for setting the Bucket IamConfiguration configuration to a Bucket.
 */
public class BucketIamConfiguration extends Diffable implements Copyable<Bucket.IamConfiguration> {

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
    public void copyFrom(Bucket.IamConfiguration model) {
        if (model != null) {
            BucketUniformBucketLevelAccess bucketUniformBucketLevelAccess = newSubresource(BucketUniformBucketLevelAccess.class);
            bucketUniformBucketLevelAccess.copyFrom(model.getUniformBucketLevelAccess());
            setUniformBucketLevelAccess(bucketUniformBucketLevelAccess);
        }
    }

    public Bucket.IamConfiguration toBucketIamConfiguration() {
        return new Bucket.IamConfiguration()
                .setUniformBucketLevelAccess(getUniformBucketLevelAccess() == null ? null : getUniformBucketLevelAccess().toIamConfigurationUniformBucketLevelAccess());
    }
}
