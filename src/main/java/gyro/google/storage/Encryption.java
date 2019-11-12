package gyro.google.storage;

import com.google.api.services.storage.model.Bucket;
import gyro.core.resource.Diffable;
import gyro.core.resource.Updatable;
import gyro.google.Copyable;

/**
 * Subresource for setting the {@link Bucket.Encryption} configuration to a {@link Bucket}.
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
public class Encryption extends Diffable implements Copyable<Bucket.Encryption> {

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
     * @return This as a {@link Bucket.Encryption} instance.
     */
    public Bucket.Encryption toBucketEncryption() {
       return new Bucket.Encryption().setDefaultKmsKeyName(getDefaultKmsKeyName());
    }

    /**
     * Converts a {@link Bucket.Encryption} into a new Bucket object.
     * 
     * @param model Instance of a {@link Bucket.Encryption} object.
     * @return A new Encryption object populated by data from ``model``.
     */
    public static Encryption fromBucketEncryption(Bucket.Encryption model) {
       Encryption encryption = new Encryption();
       encryption.setDefaultKmsKeyName(model.getDefaultKmsKeyName());
       return encryption;
    }
}
