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

package gyro.google.sqladmin;

import com.google.api.services.sqladmin.model.ImportContext;
import gyro.core.resource.Diffable;
import gyro.google.Copyable;

public class ImportContextBakImportOptionsEncryptionOptions extends Diffable
    implements Copyable<ImportContext.BakImportOptions.EncryptionOptions> {

    private String certPath;

    private String pvkPassword;

    private String pvkPath;

    /**
     * Path to the Certificate (.cer) in Cloud Storage, in the form `gs://bucketName/fileName`. The instance must have write permissions to the bucket and read access to the file.
     */
    public String getCertPath() {
        return certPath;
    }

    public void setCertPath(String certPath) {
        this.certPath = certPath;
    }

    /**
     * Password that encrypts the private key
     */
    public String getPvkPassword() {
        return pvkPassword;
    }

    public void setPvkPassword(String pvkPassword) {
        this.pvkPassword = pvkPassword;
    }

    /**
     * Path to the Certificate Private Key (.pvk) in Cloud Storage, in the form `gs://bucketName/fileName`. The instance must have write permissions to the bucket and read access to the file.
     */
    public String getPvkPath() {
        return pvkPath;
    }

    public void setPvkPath(String pvkPath) {
        this.pvkPath = pvkPath;
    }

    @Override
    public String primaryKey() {
        return "";
    }

    @Override
    public void copyFrom(ImportContext.BakImportOptions.EncryptionOptions model) {
        setCertPath(model.getCertPath());
        setPvkPassword(model.getPvkPassword());
        setPvkPath(model.getPvkPath());
    }
}
