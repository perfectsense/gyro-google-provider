/*
 * Copyright 2020, Perfect Sense, Inc.
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

package gyro.google.compute;

import gyro.core.resource.Diffable;
import gyro.core.resource.Updatable;
import gyro.core.validation.ValidStrings;
import gyro.google.Copyable;

public class RouterNatLogConfig extends Diffable
    implements Copyable<com.google.cloud.compute.v1.RouterNatLogConfig> {

    private Boolean enable;
    private String filter;

    /**
     * Indicates whether or not to export logs. Defaults to ``false``.
     */
    @Updatable
    public Boolean getEnable() {
        return enable;
    }

    public void setEnable(Boolean enable) {
        this.enable = enable;
    }

    /**
     * The desired filtering of logs for NAT gateway.
     */
    @ValidStrings({ "ERRORS_ONLY", "TRANSLATIONS_ONLY", "ALL" })
    @Updatable
    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    @Override
    public String primaryKey() {
        return null;
    }

    @Override
    public void copyFrom(com.google.cloud.compute.v1.RouterNatLogConfig model) throws Exception {
        setFilter(model.getFilter());
        setEnable(model.getEnable());
    }

    com.google.cloud.compute.v1.RouterNatLogConfig toRouterNatLogConfig() {
        com.google.cloud.compute.v1.RouterNatLogConfig.Builder builder = com.google.cloud.compute.v1.RouterNatLogConfig.newBuilder();

        if (getEnable() != null) {
            builder.setEnable(getEnable());
        }

        if (getFilter() != null) {
            builder.setFilter(getFilter());
        }

        return builder.build();
    }
}
