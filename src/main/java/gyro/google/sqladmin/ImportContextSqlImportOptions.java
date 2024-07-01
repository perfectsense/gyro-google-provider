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

public class ImportContextSqlImportOptions extends Diffable implements Copyable<ImportContext.SqlImportOptions> {

    private Boolean parallel;

    private Integer threads;

    /**
     * Optional. Whether or not the import should be parallel.
     */
    public Boolean getParallel() {
        return parallel;
    }

    public void setParallel(Boolean parallel) {
        this.parallel = parallel;
    }

    /**
     * Optional. The number of threads to use for parallel import.
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
    public void copyFrom(ImportContext.SqlImportOptions model) {
        setParallel(model.getParallel());
        setThreads(model.getThreads());
    }
}
