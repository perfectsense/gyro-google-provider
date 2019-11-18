package gyro.google.storage;

import com.google.api.services.storage.model.Bucket.Encryption;
import gyro.core.resource.Diffable;
import gyro.core.resource.Updatable;
import gyro.google.Copyable;

/**
 * Subresource for setting the Bucket.Encryption configuration to a Bucket.
 */
public class BucketEncryption extends Diffable implements Copyable<Encryption> {

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
    public void copyFrom(Encryption model) {
        if (model != null) {
            setDefaultKmsKeyName(model.getDefaultKmsKeyName());
        }
    }

    public Encryption toBucketEncryption() {
       return new Encryption().setDefaultKmsKeyName(getDefaultKmsKeyName());
    }
}
