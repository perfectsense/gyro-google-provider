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

import com.google.api.services.sqladmin.model.DiskEncryptionConfiguration;
import gyro.core.resource.Diffable;
import gyro.core.validation.Required;
import gyro.google.Copyable;
import gyro.google.kms.CryptoKeyResource;

public class DbDiskEncryptionConfiguration extends Diffable implements Copyable<DiskEncryptionConfiguration> {

    private CryptoKeyResource key;

    /**
     * The KMS key for disk encryption
     */
    @Required
    public CryptoKeyResource getKey() {
        return key;
    }

    public void setKey(CryptoKeyResource key) {
        this.key = key;
    }

    @Override
    public String primaryKey() {
        return "";
    }

    @Override
    public void copyFrom(DiskEncryptionConfiguration model) throws Exception {
        setKey(null);
        if (model.getKmsKeyName() != null) {
            setKey(findById(CryptoKeyResource.class, model.getKmsKeyName()));
        }
    }

    public DiskEncryptionConfiguration toDiskEncryptionConfiguration() {
        DiskEncryptionConfiguration config = new DiskEncryptionConfiguration();
        config.setKmsKeyName(getKey().getId());
        return config;
    }
}
