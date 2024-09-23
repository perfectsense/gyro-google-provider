/*
 * Copyright 2024, Brightspot.
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

package gyro.google.cloudsql;

import com.google.api.services.sqladmin.model.DiskEncryptionStatus;
import gyro.core.resource.Diffable;
import gyro.core.validation.Required;
import gyro.google.Copyable;
import gyro.google.kms.CryptoKeyVersionResource;

public class DbDiskEncryptionStatus extends Diffable implements Copyable<DiskEncryptionStatus> {

    private CryptoKeyVersionResource keyVersion;

    /**
     * The KMS key version used to encrypt the Cloud SQL instance resource
     */
    @Required
    public CryptoKeyVersionResource getKeyVersion() {
        return keyVersion;
    }

    public void setKeyVersion(CryptoKeyVersionResource keyVersion) {
        this.keyVersion = keyVersion;
    }

    @Override
    public String primaryKey() {
        return "";
    }

    @Override
    public void copyFrom(DiskEncryptionStatus model) throws Exception {
        setKeyVersion(findById(CryptoKeyVersionResource.class, model.getKmsKeyVersionName()));
    }

    public DiskEncryptionStatus toDiskEncryptionStatus() {
        DiskEncryptionStatus diskEncryptionStatus = new DiskEncryptionStatus();
        diskEncryptionStatus.setKmsKeyVersionName(getKeyVersion().getId());
        return diskEncryptionStatus;
    }
}
