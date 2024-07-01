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

public class ExportContextSqlExportOptionsMysqlExportOptions extends Diffable
    implements Copyable<ExportContext.SqlExportOptions.MysqlExportOptions> {

    private Integer masterData;

    /**
     * Option to include SQL statement required to set up replication. If set to `1`, the dump file includes a CHANGE MASTER TO statement with the binary log coordinates, and --set-gtid-purged is set to ON. If set to `2`, the CHANGE MASTER TO statement is written as a SQL comment and has no effect. If set to any value other than `1`, --set-gtid-purged is set to OFF.
     */
    public Integer getMasterData() {
        return masterData;
    }

    public void setMasterData(Integer masterData) {
        this.masterData = masterData;
    }

    @Override
    public String primaryKey() {
        return "";
    }

    @Override
    public void copyFrom(ExportContext.SqlExportOptions.MysqlExportOptions model) {
        setMasterData(model.getMasterData());
    }
}
