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

public class DataCacheConfig extends Diffable
    implements Copyable<com.google.api.services.sqladmin.model.DataCacheConfig> {

    private Boolean dataCacheEnabled;

    /**
     * Whether data cache is enabled for the instance.
     */
    public Boolean getDataCacheEnabled() {
        return dataCacheEnabled;
    }

    public void setDataCacheEnabled(Boolean dataCacheEnabled) {
        this.dataCacheEnabled = dataCacheEnabled;
    }

    @Override
    public String primaryKey() {
        return "";
    }

    @Override
    public void copyFrom(com.google.api.services.sqladmin.model.DataCacheConfig model) {
        setDataCacheEnabled(model.getDataCacheEnabled());
    }

    com.google.api.services.sqladmin.model.DataCacheConfig toDataCacheConfig() {
        com.google.api.services.sqladmin.model.DataCacheConfig dataCacheConfig = new com.google.api.services.sqladmin.model.DataCacheConfig();
        dataCacheConfig.setDataCacheEnabled(getDataCacheEnabled());

        return dataCacheConfig;
    }
}
