package gyro.google.compute;

import com.google.cloud.compute.v1.CustomerEncryptionKey;
import gyro.core.resource.Diffable;
import gyro.core.resource.Output;
import gyro.core.validation.Required;
import gyro.google.Copyable;

/**
 * A customer supplied encryption key to encrypt a resource.
 */
public class EncryptionKey extends Diffable implements Copyable<CustomerEncryptionKey> {

    private String rawKey;

    // Read-only
    private String sha256;

    /**
     * The 256-bit encryption key, encoded in RFC 4648 base64, that protects this resource. See `Encrypt disks with customer-supplied encryption keys <https://cloud.google.com/compute/docs/disks/customer-supplied-encryption>`_.
     */
    @Required
    public String getRawKey() {
        return rawKey;
    }

    public void setRawKey(String rawKey) {
        this.rawKey = rawKey;
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
        if (model.hasRawKey()) {
            setRawKey(model.getRawKey());
        }

        if (model.hasSha256()) {
            setSha256(model.getSha256());
        }
    }

    @Override
    public String primaryKey() {
        return "";
    }

    CustomerEncryptionKey toCustomerEncryptionKey() {
        return CustomerEncryptionKey.newBuilder().setRawKey(getRawKey()).build();
    }
}
