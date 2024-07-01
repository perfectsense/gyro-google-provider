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

import com.google.api.services.sqladmin.model.ExportContext;
import gyro.core.resource.Diffable;
import gyro.google.Copyable;

public class ExportContextSqlExportOptions extends Diffable implements Copyable<ExportContext.SqlExportOptions> {

    private ExportContextSqlExportOptionsMysqlExportOptions mysqlExportOptions;

    private Boolean parallel;

    private Boolean schemaOnly;

    private List<String> tables;

    private Integer threads;

    /**
     * Options for exporting from MySQL.
     *
     * @subresource gyro.google.sqladmin.base.ExportContextSqlExportOptionsMysqlExportOptions
     */
    public ExportContextSqlExportOptionsMysqlExportOptions getMysqlExportOptions() {
        return mysqlExportOptions;
    }

    public void setMysqlExportOptions(
        ExportContextSqlExportOptionsMysqlExportOptions mysqlExportOptions) {
        this.mysqlExportOptions = mysqlExportOptions;
    }

    /**
     * Optional. Whether or not the export should be parallel.
     */
    public Boolean getParallel() {
        return parallel;
    }

    public void setParallel(Boolean parallel) {
        this.parallel = parallel;
    }

    /**
     * Export only schemas.
     */
    public Boolean getSchemaOnly() {
        return schemaOnly;
    }

    public void setSchemaOnly(Boolean schemaOnly) {
        this.schemaOnly = schemaOnly;
    }

    /**
     * Tables to export, or that were exported, from the specified database. If you specify tables, specify one and only one database. For PostgreSQL instances, you can specify only one table.
     */
    public List<String> getTables() {
        if (tables == null) {
            tables = new ArrayList<>();
        }

        return tables;
    }

    public void setTables(List<String> tables) {
        this.tables = tables;
    }

    /**
     * Optional. The number of threads to use for parallel export.
     */
    public Integer getThreads() {
        return threads;
    }

    public void setThreads(Integer threads) {
        this.threads = threads;
    }

    @Override
    public String primaryKey() {
        return "";
    }

    @Override
    public void copyFrom(ExportContext.SqlExportOptions model) {
        setParallel(model.getParallel());
        setSchemaOnly(model.getSchemaOnly());
        setTables(model.getTables());
        setThreads(model.getThreads());

        setMysqlExportOptions(null);
        if (model.getMysqlExportOptions() != null) {
            ExportContextSqlExportOptionsMysqlExportOptions options = new ExportContextSqlExportOptionsMysqlExportOptions();
            options.copyFrom(model.getMysqlExportOptions());
            setMysqlExportOptions(options);
        }
    }
}
