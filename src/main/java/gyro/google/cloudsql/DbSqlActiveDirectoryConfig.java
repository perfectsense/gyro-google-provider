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

package gyro.google.cloudsql;

import com.google.api.services.sqladmin.model.SqlActiveDirectoryConfig;
import gyro.core.resource.Diffable;
import gyro.core.resource.Updatable;
import gyro.core.validation.Required;
import gyro.google.Copyable;

public class DbSqlActiveDirectoryConfig extends Diffable implements Copyable<SqlActiveDirectoryConfig> {

    public String domain;

    /**
     * The name of the domain.
     */
    @Required
    @Updatable
    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    @Override
    public void copyFrom(SqlActiveDirectoryConfig model) throws Exception {
        setDomain(model.getDomain());
    }

    @Override
    public String primaryKey() {
        return "";
    }

    public SqlActiveDirectoryConfig toSqlActiveDirectoryConfig() {
        return new SqlActiveDirectoryConfig().setDomain(getDomain());
    }
}
