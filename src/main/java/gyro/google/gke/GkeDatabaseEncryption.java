/*
 * Copyright 2021, Brightspot.
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

package gyro.google.gke;

import com.google.container.v1.DatabaseEncryption;
import gyro.core.resource.Diffable;
import gyro.core.resource.Updatable;
import gyro.core.validation.ValidStrings;
import gyro.google.Copyable;
import gyro.google.kms.CryptoKeyResource;

public class GkeDatabaseEncryption extends Diffable implements Copyable<DatabaseEncryption> {

    private DatabaseEncryption.State state;
    private CryptoKeyResource key;

    /**
     * The state of etcd encryption.
     */
    @Updatable
    @ValidStrings({ "ENCRYPTED", "DECRYPTED" })
    public DatabaseEncryption.State getState() {
        if (state == null) {
            state = DatabaseEncryption.State.DECRYPTED;
        }

        return state;
    }

    public void setState(DatabaseEncryption.State state) {
        this.state = state;
    }

    /**
     * The CloudKMS key to use for the encryption of secrets in etcd.
     */
    @Updatable
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
    public void copyFrom(DatabaseEncryption model) throws Exception {
        setKey(findById(CryptoKeyResource.class, model.getKeyName()));
        setState(model.getState());
    }

    DatabaseEncryption toDatabaseEncryption() {
        return DatabaseEncryption.newBuilder().setState(getState())
            .setKeyName(getKey() == null ? null : getKey().getId()).build();
    }
}
