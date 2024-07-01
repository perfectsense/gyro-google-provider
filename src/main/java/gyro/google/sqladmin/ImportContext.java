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

import gyro.core.resource.Diffable;
import gyro.core.validation.ValidStrings;
import gyro.google.Copyable;

public class ImportContext extends Diffable implements Copyable<com.google.api.services.sqladmin.model.ImportContext> {

    private ImportContextBakImportOptions bakImportOptions;

    private ImportContextCsvImportOptions csvImportOptions;

    private DatabaseResource database;

    private String fileType;

    private String importUser;

    private ImportContextSqlImportOptions sqlImportOptions;

    private String uri;

    /**
     * Import parameters specific to SQL Server .BAK files
     *
     * @subresource gyro.google.sqladmin.base.ImportContextBakImportOptions
     */
    public ImportContextBakImportOptions getBakImportOptions() {
        return bakImportOptions;
    }

    public void setBakImportOptions(ImportContextBakImportOptions bakImportOptions) {
        this.bakImportOptions = bakImportOptions;
    }

    /**
     * Options for importing data as CSV.
     *
     * @subresource gyro.google.sqladmin.base.ImportContextCsvImportOptions
     */
    public ImportContextCsvImportOptions getCsvImportOptions() {
        return csvImportOptions;
    }

    public void setCsvImportOptions(ImportContextCsvImportOptions csvImportOptions) {
        this.csvImportOptions = csvImportOptions;
    }

    /**
     * The target database for the import. If `fileType` is `SQL`, this field is required only if the import file
     * does not specify a database, and is overridden by any database specification in the import file.
     * If `fileType` is `CSV`, one database must be specified.
     */
    public DatabaseResource getDatabase() {
        return database;
    }

    public void setDatabase(DatabaseResource database) {
        this.database = database;
    }

    /**
     * The file type for the specified uri.\`SQL`: The file contains SQL statements. \`CSV`: The file contains CSV data.
     */
    @ValidStrings({
        "SQL_FILE_TYPE_UNSPECIFIED",
        "SQL",
        "CSV",
        "BAK"
    })
    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    /**
     * The PostgreSQL user for this import operation. PostgreSQL instances only.
     */
    public String getImportUser() {
        return importUser;
    }

    public void setImportUser(String importUser) {
        this.importUser = importUser;
    }

    /**
     * Optional. Options for importing data from SQL statements.
     *
     * @subresource gyro.google.sqladmin.base.ImportContextSqlImportOptions
     */
    public ImportContextSqlImportOptions getSqlImportOptions() {
        return sqlImportOptions;
    }

    public void setSqlImportOptions(ImportContextSqlImportOptions sqlImportOptions) {
        this.sqlImportOptions = sqlImportOptions;
    }

    /**
     * Path to the import file in Cloud Storage, in the form `gs://bucketName/fileName`. Compressed gzip files (.gz) are supported when `fileType` is `SQL`. The instance must have write permissions to the bucket and read access to the file.
     */
    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    @Override
    public String primaryKey() {
        return "";
    }

    @Override
    public void copyFrom(com.google.api.services.sqladmin.model.ImportContext model) {
        setFileType(model.getFileType());
        setImportUser(model.getImportUser());
        setUri(model.getUri());

        setBakImportOptions(null);
        if (model.getBakImportOptions() != null) {
            ImportContextBakImportOptions options = newSubresource(ImportContextBakImportOptions.class);
            options.copyFrom(model.getBakImportOptions());
            setBakImportOptions(options);
        }

        setCsvImportOptions(null);
        if (model.getCsvImportOptions() != null) {
            ImportContextCsvImportOptions options = newSubresource(ImportContextCsvImportOptions.class);
            options.copyFrom(model.getCsvImportOptions());
            setCsvImportOptions(options);
        }

        setDatabase(null);
        if (model.getDatabase() != null) {
            setDatabase(findById(DatabaseResource.class, model.getDatabase()));
        }

        setSqlImportOptions(null);
        if (model.getSqlImportOptions() != null) {
            ImportContextSqlImportOptions options = newSubresource(ImportContextSqlImportOptions.class);
            options.copyFrom(model.getSqlImportOptions());
            setSqlImportOptions(options);
        }
    }
}
