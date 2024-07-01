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
import gyro.google.Copyable;

public class SqlActiveDirectoryConfig extends Diffable
    implements Copyable<com.google.api.services.sqladmin.model.SqlActiveDirectoryConfig> {

    private String domain;

    /**
     * The name of the domain (e.g., mydomain.com).
     */
    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    @Override
    public String primaryKey() {
        return "";
    }

    @Override
    public void copyFrom(com.google.api.services.sqladmin.model.SqlActiveDirectoryConfig model) {
        setDomain(model.getDomain());
    }

    com.google.api.services.sqladmin.model.SqlActiveDirectoryConfig toSqlActiveDirectoryConfig() {
        com.google.api.services.sqladmin.model.SqlActiveDirectoryConfig sqlActiveDirectoryConfig = new com.google.api.services.sqladmin.model.SqlActiveDirectoryConfig();
        sqlActiveDirectoryConfig.setDomain(getDomain());

        return sqlActiveDirectoryConfig;
    }
}
