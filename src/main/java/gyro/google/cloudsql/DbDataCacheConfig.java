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

import com.google.api.services.sqladmin.model.DataCacheConfig;
import gyro.core.resource.Diffable;
import gyro.core.resource.Updatable;
import gyro.core.validation.Required;
import gyro.google.Copyable;

public class DbDataCacheConfig extends Diffable implements Copyable<DataCacheConfig> {

    private Boolean dataCacheEnabled;

    /**
     * When set to ``true``, data cache is enabled.
     */
    @Required
    @Updatable
    public Boolean getDataCacheEnabled() {
        return dataCacheEnabled;
    }

    public void setDataCacheEnabled(Boolean dataCacheEnabled) {
        this.dataCacheEnabled = dataCacheEnabled;
    }

    @Override
    public void copyFrom(DataCacheConfig model) throws Exception {
        setDataCacheEnabled(model.getDataCacheEnabled());
    }

    @Override
    public String primaryKey() {
        return "";
    }

    public DataCacheConfig toDataCacheConfig() {
        return new DataCacheConfig().setDataCacheEnabled(getDataCacheEnabled());
    }
}
