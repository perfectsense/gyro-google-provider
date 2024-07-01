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

import java.util.ArrayList;
import java.util.List;

import gyro.core.resource.Diffable;
import gyro.core.validation.ValidStrings;
import gyro.google.Copyable;

public class ExportContext extends Diffable implements Copyable<com.google.api.services.sqladmin.model.ExportContext> {

    private ExportContextBakExportOptions bakExportOptions;

    private ExportContextCsvExportOptions csvExportOptions;

    private List<DatabaseResource> databases;

    private String fileType;

    private Boolean offload;

    private ExportContextSqlExportOptions sqlExportOptions;

    private String uri;

    /**
     * Options for exporting BAK files (SQL Server-only)
     *
     * @subresource gyro.google.sqladmin.base.ExportContextBakExportOptions
     */
    public ExportContextBakExportOptions getBakExportOptions() {
        return bakExportOptions;
    }

    public void setBakExportOptions(ExportContextBakExportOptions bakExportOptions) {
        this.bakExportOptions = bakExportOptions;
    }

    /**
     * Options for exporting data as CSV. `MySQL` and `PostgreSQL` instances only.
     *
     * @subresource gyro.google.sqladmin.base.ExportContextCsvExportOptions
     */
    public ExportContextCsvExportOptions getCsvExportOptions() {
        return csvExportOptions;
    }

    public void setCsvExportOptions(ExportContextCsvExportOptions csvExportOptions) {
        this.csvExportOptions = csvExportOptions;
    }

    /**
     * Databases to be exported. `MySQL instances:` If `fileType` is `SQL` and no database is specified,
     * all databases are exported, except for the `mysql` system database. If `fileType` is `CSV`,
     * you can specify one database, either by using this property or by using the
     * `csvExportOptions.selectQuery` property, which takes precedence over this property.
     * `PostgreSQL instances:` You must specify one database to be exported. If `fileType` is `CSV`,
     * this database must match the one specified in the `csvExportOptions.selectQuery` property.
     * `SQL Server instances:` You must specify one database to be exported, and the `fileType` must be `BAK`.
     *
     * @subresource gyro.google.sqladmin.base.AbstractDatabaseResource
     */
    public List<DatabaseResource> getDatabases() {
        if (databases == null) {
            databases = new ArrayList<>();
        }

        return databases;
    }

    public void setDatabases(List<DatabaseResource> databases) {
        this.databases = databases;
    }

    /**
     * The file type for the specified uri.
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
     * Option for export offload.
     */
    public Boolean getOffload() {
        return offload;
    }

    public void setOffload(Boolean offload) {
        this.offload = offload;
    }

    /**
     * Options for exporting data as SQL statements.
     *
     * @subresource gyro.google.sqladmin.base.ExportContextSqlExportOptions
     */
    public ExportContextSqlExportOptions getSqlExportOptions() {
        return sqlExportOptions;
    }

    public void setSqlExportOptions(ExportContextSqlExportOptions sqlExportOptions) {
        this.sqlExportOptions = sqlExportOptions;
    }

    /**
     * The path to the file in Google Cloud Storage where the export will be stored.
     * The URI is in the form `gs://bucketName/fileName`.
     * If the file already exists, the request succeeds, but the operation fails.
     * If `fileType` is `SQL` and the filename ends with .gz, the contents are compressed.
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
    public void copyFrom(com.google.api.services.sqladmin.model.ExportContext model) {
        setFileType(model.getFileType());
        setOffload(model.getOffload());
        setUri(model.getUri());

        setBakExportOptions(null);
        if (model.getBakExportOptions() != null) {
            ExportContextBakExportOptions options = newSubresource(ExportContextBakExportOptions.class);
            options.copyFrom(model.getBakExportOptions());
            setBakExportOptions(options);
        }

        setCsvExportOptions(null);
        if (model.getCsvExportOptions() != null) {
            ExportContextCsvExportOptions options = newSubresource(ExportContextCsvExportOptions.class);
            options.copyFrom(model.getCsvExportOptions());
            setCsvExportOptions(options);
        }

        setDatabases(null);
        if (model.getDatabases() != null) {
            List<DatabaseResource> databases = new ArrayList<>();
            for (String database : model.getDatabases()) {
                databases.add(findById(DatabaseResource.class, database));
            }

            setDatabases(databases);
        }

        setSqlExportOptions(null);
        if (model.getSqlExportOptions() != null) {
            ExportContextSqlExportOptions options = newSubresource(ExportContextSqlExportOptions.class);
            options.copyFrom(model.getSqlExportOptions());
            setSqlExportOptions(options);
        }
    }
}
