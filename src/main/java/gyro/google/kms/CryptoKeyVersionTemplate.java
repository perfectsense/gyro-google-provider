package gyro.google.kms;

import com.google.cloud.kms.v1.CryptoKeyVersion;
import com.google.cloud.kms.v1.ProtectionLevel;
import gyro.core.resource.Diffable;
import gyro.core.resource.Updatable;
import gyro.core.validation.Required;
import gyro.google.Copyable;

public class CryptoKeyVersionTemplate extends Diffable
    implements Copyable<com.google.cloud.kms.v1.CryptoKeyVersionTemplate> {

    private CryptoKeyVersion.CryptoKeyVersionAlgorithm algorithm;
    private ProtectionLevel protectionLevel;

    /**
     * The algorithm to use when creating a crypto key version based on this resource. (Required)
     */
    @Required
    @Updatable
    public CryptoKeyVersion.CryptoKeyVersionAlgorithm getAlgorithm() {
        return algorithm;
    }

    public void setAlgorithm(CryptoKeyVersion.CryptoKeyVersionAlgorithm algorithm) {
        this.algorithm = algorithm;
    }

    /**
     * The protection level to use when creating a crypto key version based on this resource. (Required)
     */
    @Required
    public ProtectionLevel getProtectionLevel() {
        return protectionLevel;
    }

    public void setProtectionLevel(ProtectionLevel protectionLevel) {
        this.protectionLevel = protectionLevel;
    }

    @Override
    public String primaryKey() {
        return "";
    }

    @Override
    public void copyFrom(com.google.cloud.kms.v1.CryptoKeyVersionTemplate model) throws Exception {
        setAlgorithm(model.getAlgorithm());
        setProtectionLevel(model.getProtectionLevel());
    }

    com.google.cloud.kms.v1.CryptoKeyVersionTemplate toCryptoKeyVersionTemplate() {
        return com.google.cloud.kms.v1.CryptoKeyVersionTemplate.newBuilder()
            .setAlgorithm(getAlgorithm())
            .setProtectionLevel(getProtectionLevel())
            .build();
    }

    com.google.cloud.kms.v1.CryptoKeyVersionTemplate updateCryptoKeyVersionAlgorithm() {
        return com.google.cloud.kms.v1.CryptoKeyVersionTemplate.newBuilder()
            .setAlgorithm(getAlgorithm())
            .build();
    }
}
