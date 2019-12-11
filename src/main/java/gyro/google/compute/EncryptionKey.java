package gyro.google.compute;

import com.google.api.services.compute.model.CustomerEncryptionKey;
import gyro.core.resource.Diffable;
import gyro.core.resource.Output;
import gyro.core.validation.ConflictsWith;
import gyro.google.Copyable;

/**
 * A customer supplied encryption key to encrypt a resource.
 */
public class EncryptionKey extends Diffable implements Copyable<CustomerEncryptionKey> {

    private String rawKey;
    private String kmsKeyName;

    // Read-only
    private String sha256;

    /**
     * The 256-bit encryption key, encoded in RFC 4648 base64, that protects this resource. Conflicts with ``kms-key-name``.
     */
    @ConflictsWith("kms-key-name")
    public String getRawKey() {
        return rawKey;
    }

    public void setRawKey(String rawKey) {
        this.rawKey = rawKey;
    }

    /**
     * The name of the encryption key that is stored in Google Cloud KMS. Conflicts with ``raw-key``.
     */
    @ConflictsWith("raw-key")
    public String getKmsKeyName() {
        return kmsKeyName;
    }

    public void setKmsKeyName(String kmsKeyName) {
        this.kmsKeyName = kmsKeyName;
    }

    /**
     * The RFC 4648 base64 encoded SHA-256 hash of the encryption key that protects this resource.
     */
    @Output
    public String getSha256() {
        return sha256;
    }

    public void setSha256(String sha256) {
        this.sha256 = sha256;
    }

    @Override
    public void copyFrom(CustomerEncryptionKey model) {
        setRawKey(model.getRawKey());
        setKmsKeyName(model.getKmsKeyName());
        setSha256(model.getSha256());
    }

    CustomerEncryptionKey toCustomerEncryptionKey() {
        return new CustomerEncryptionKey()
            .setRawKey(getRawKey())
            .setKmsKeyName(getKmsKeyName())
            .setSha256(getSha256());
    }
}
