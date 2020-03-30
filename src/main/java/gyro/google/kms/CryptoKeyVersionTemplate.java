/*
 * Copyright 2020, Perfect Sense, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package gyro.google.kms;

import com.google.cloud.kms.v1.CryptoKeyVersion.CryptoKeyVersionAlgorithm;
import com.google.cloud.kms.v1.ProtectionLevel;
import gyro.core.resource.Diffable;
import gyro.core.resource.Updatable;
import gyro.core.validation.Required;
import gyro.google.Copyable;

public class CryptoKeyVersionTemplate extends Diffable
    implements Copyable<com.google.cloud.kms.v1.CryptoKeyVersionTemplate> {

    private CryptoKeyVersionAlgorithm algorithm;
    private ProtectionLevel protectionLevel;

    /**
     * The algorithm to use when creating a crypto key version based on this resource. (Required)
     */
    @Required
    @Updatable
    public CryptoKeyVersionAlgorithm getAlgorithm() {
        return algorithm;
    }

    public void setAlgorithm(CryptoKeyVersionAlgorithm algorithm) {
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
