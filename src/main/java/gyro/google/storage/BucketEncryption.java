package gyro.google.storage;

import com.google.api.services.storage.model.Bucket;
import gyro.core.resource.Diffable;
import gyro.core.resource.Updatable;
import gyro.google.Copyable;

/**
 * Subresource for setting the Bucket.Encryption configuration to a Bucket.
 *
 * Example
 * -------
 *
 * ..code-block:: gyro
 *
 *     encryption
 *         default-kms-key-name: 'saltMe4ever'
 *     end
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
       setDefaultKmsKeyName(model.getDefaultKmsKeyName());
    }

    /**
     * This as a Bucket.Encryption instance.
     */
    public Bucket.Encryption toGcpBucketEncryption() {
       return new Bucket.Encryption().setDefaultKmsKeyName(getDefaultKmsKeyName());
    }

    /**
     * Converts a Bucket.Encryption into a new Bucket object.
     */
    public static BucketEncryption fromGcpBucketEncryption(Bucket.Encryption model) {
        if (model != null) {
            BucketEncryption encryption = new BucketEncryption();
            encryption.setDefaultKmsKeyName(model.getDefaultKmsKeyName());
            return encryption;
        }
        return null;
    }
}
