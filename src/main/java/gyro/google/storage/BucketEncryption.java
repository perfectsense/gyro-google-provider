package gyro.google.storage;

import com.google.api.services.storage.model.Bucket;
import gyro.core.resource.Diffable;
import gyro.core.resource.Updatable;
import gyro.google.Copyable;

/**
 * Subresource for setting the Bucket.Encryption configuration to a Bucket.
 */
public class BucketEncryption extends Diffable implements Copyable<Bucket.Encryption> {

    private String defaultKmsKeyName;

    /**
     * Default KMS key used to encrypt objects inserted into the bucket when no encryption method is specified.
     */
    @Updatable
    public String getDefaultKmsKeyName() {
        return defaultKmsKeyName;
    }

    public void setDefaultKmsKeyName(String defaultKmsKeyName) {
        this.defaultKmsKeyName = defaultKmsKeyName;
    }

    @Override
    public void copyFrom(Bucket.Encryption model) {
        if (model != null) {
            setDefaultKmsKeyName(model.getDefaultKmsKeyName());
        }
    }

    public Bucket.Encryption toBucketEncryption() {
       return new Bucket.Encryption().setDefaultKmsKeyName(getDefaultKmsKeyName());
    }
}
