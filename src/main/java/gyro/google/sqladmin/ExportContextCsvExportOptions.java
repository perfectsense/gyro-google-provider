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

import com.google.api.services.sqladmin.model.ExportContext;
import gyro.core.resource.Diffable;
import gyro.google.Copyable;

public class ExportContextCsvExportOptions extends Diffable implements Copyable<ExportContext.CsvExportOptions> {

    private String escapeCharacter;

    private String fieldsTerminatedBy;

    private String linesTerminatedBy;

    private String quoteCharacter;

    private String selectQuery;

    /**
     * Specifies the character that should appear before a data character that needs to be escaped.
     */
    public String getEscapeCharacter() {
        return escapeCharacter;
    }

    public void setEscapeCharacter(String escapeCharacter) {
        this.escapeCharacter = escapeCharacter;
    }

    /**
     * Specifies the character that separates columns within each row (line) of the file.
     */
    public String getFieldsTerminatedBy() {
        return fieldsTerminatedBy;
    }

    public void setFieldsTerminatedBy(String fieldsTerminatedBy) {
        this.fieldsTerminatedBy = fieldsTerminatedBy;
    }

    /**
     * This is used to separate lines. If a line does not contain all fields, the rest of the columns are set to their default values.
     */
    public String getLinesTerminatedBy() {
        return linesTerminatedBy;
    }

    public void setLinesTerminatedBy(String linesTerminatedBy) {
        this.linesTerminatedBy = linesTerminatedBy;
    }

    /**
     * Specifies the quoting character to be used when a data value is quoted.
     */
    public String getQuoteCharacter() {
        return quoteCharacter;
    }

    public void setQuoteCharacter(String quoteCharacter) {
        this.quoteCharacter = quoteCharacter;
    }

    /**
     * The select query used to extract the data.
     */
    public String getSelectQuery() {
        return selectQuery;
    }

    public void setSelectQuery(String selectQuery) {
        this.selectQuery = selectQuery;
    }

    @Override
    public String primaryKey() {
        return "";
    }

    @Override
    public void copyFrom(ExportContext.CsvExportOptions model) {
        setEscapeCharacter(model.getEscapeCharacter());
        setFieldsTerminatedBy(model.getFieldsTerminatedBy());
        setLinesTerminatedBy(model.getLinesTerminatedBy());
        setQuoteCharacter(model.getQuoteCharacter());
        setSelectQuery(model.getSelectQuery());
    }
}
