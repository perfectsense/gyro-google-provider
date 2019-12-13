/*
 * Copyright 2019, Perfect Sense, Inc.
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

package gyro.google.compute;

import com.google.api.services.compute.model.CustomerEncryptionKey;
import gyro.core.resource.Diffable;
import gyro.google.Copyable;

public class InstanceCustomerEncryptionKey extends Diffable implements Copyable<CustomerEncryptionKey> {

    private String kmsKeyName;
    private String rawKey;
    private String sha256;

    /**
     * Name of the encryption key that is stored in Google Cloud KMS.
     */
    public String getKmsKeyName() {
        return kmsKeyName;
    }

    public void setKmsKeyName(String kmsKeyName) {
        this.kmsKeyName = kmsKeyName;
    }

    /**
     * Specifies a 256-bit customer-supplied encryption key, encoded in RFC 4648 base64 to either encrypt or decrypt this resource.
     */
    public String getRawKey() {
        return rawKey;
    }

    public void setRawKey(String rawKey) {
        this.rawKey = rawKey;
    }

    /**
     * The RFC 4648 base64 encoded SHA-256 hash of the customer-supplied encryption key that protects this resource.
     */
    public String getSha256() {
        return sha256;
    }

    public void setSha256(String sha256) {
        this.sha256 = sha256;
    }

    @Override
    public String primaryKey() {
        return "";
    }

    @Override
    public void copyFrom(CustomerEncryptionKey model) {
        setKmsKeyName(model.getKmsKeyName());
        setRawKey(model.getRawKey());
        setSha256(model.getSha256());
    }

    public CustomerEncryptionKey copyTo() {
        return new CustomerEncryptionKey()
            .setKmsKeyName(getKmsKeyName())
            .setRawKey(getRawKey())
            .setSha256(getSha256());
    }
}
