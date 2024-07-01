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
import gyro.core.validation.ValidStrings;
import gyro.google.Copyable;

public class ImportContextBakImportOptions extends Diffable implements Copyable<ImportContext.BakImportOptions> {

    private String bakType;

    private ImportContextBakImportOptionsEncryptionOptions encryptionOptions;

    private Boolean noRecovery;

    private Boolean recoveryOnly;

    private String stopAt;

    private String stopAtMark;

    private Boolean striped;

    /**
     * Type of the bak content, FULL or DIFF
     */
    @ValidStrings({
        "BAK_TYPE_UNSPECIFIED",
        "FULL",
        "DIFF",
        "TLOG"
    })
    public String getBakType() {
        return bakType;
    }

    public void setBakType(String bakType) {
        this.bakType = bakType;
    }

    public ImportContextBakImportOptionsEncryptionOptions getEncryptionOptions() {
        return encryptionOptions;
    }

    public void setEncryptionOptions(
        ImportContextBakImportOptionsEncryptionOptions encryptionOptions) {
        this.encryptionOptions = encryptionOptions;
    }

    /**
     * Whether or not the backup importing will restore database with NORECOVERY option Applies only to Cloud SQL for SQL Server.
     */
    public Boolean getNoRecovery() {
        return noRecovery;
    }

    public void setNoRecovery(Boolean noRecovery) {
        this.noRecovery = noRecovery;
    }

    /**
     * Whether or not the backup importing request will just bring database online without downloading Bak content only one of "no_recovery" and "recovery_only" can be true otherwise error will return. Applies only to Cloud SQL for SQL Server.
     */
    public Boolean getRecoveryOnly() {
        return recoveryOnly;
    }

    public void setRecoveryOnly(Boolean recoveryOnly) {
        this.recoveryOnly = recoveryOnly;
    }

    /**
     * Optional. The timestamp when the import should stop. This timestamp is in the [RFC 3339](https://tools.ietf.org/html/rfc3339) format (for example, `2023-10-01T16:19:00.094`). This field is equivalent to the STOPAT keyword and applies to Cloud SQL for SQL Server only.
     */
    public String getStopAt() {
        return stopAt;
    }

    public void setStopAt(String stopAt) {
        this.stopAt = stopAt;
    }

    /**
     * Optional. The marked transaction where the import should stop. This field is equivalent to the STOPATMARK keyword and applies to Cloud SQL for SQL Server only.
     */
    public String getStopAtMark() {
        return stopAtMark;
    }

    public void setStopAtMark(String stopAtMark) {
        this.stopAtMark = stopAtMark;
    }

    /**
     * Whether or not the backup set being restored is striped. Applies only to Cloud SQL for SQL Server.
     */
    public Boolean getStriped() {
        return striped;
    }

    public void setStriped(Boolean striped) {
        this.striped = striped;
    }

    @Override
    public String primaryKey() {
        return "";
    }

    @Override
    public void copyFrom(ImportContext.BakImportOptions model) {
        setBakType(model.getBakType());
        setNoRecovery(model.getNoRecovery());
        setRecoveryOnly(model.getRecoveryOnly());
        setStopAt(model.getStopAt());
        setStopAtMark(model.getStopAtMark());
        setStriped(model.getStriped());

        if (model.getEncryptionOptions() != null) {
            ImportContextBakImportOptionsEncryptionOptions encryptionOptions = new ImportContextBakImportOptionsEncryptionOptions();
            encryptionOptions.copyFrom(model.getEncryptionOptions());
            setEncryptionOptions(encryptionOptions);
        }
    }
}
