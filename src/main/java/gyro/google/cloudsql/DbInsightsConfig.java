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

import com.google.api.services.sqladmin.model.InsightsConfig;
import gyro.core.resource.Diffable;
import gyro.core.resource.Updatable;
import gyro.core.validation.Range;
import gyro.core.validation.Required;
import gyro.google.Copyable;

public class DbInsightsConfig extends Diffable implements Copyable<InsightsConfig> {

    private Boolean queryInsightsEnabled;
    private Integer queryPlansPerMinute;
    private Integer queryStringLength;
    private Boolean recordApplicationTags;
    private Boolean recordClientAddress;

    /**
     * When set to ``true`` query insights in enabled.
     */
    @Required
    @Updatable
    public Boolean getQueryInsightsEnabled() {
        return queryInsightsEnabled;
    }

    public void setQueryInsightsEnabled(Boolean queryInsightsEnabled) {
        this.queryInsightsEnabled = queryInsightsEnabled;
    }

    /**
     * The number of query execution plans captured by Insights per minute for all queries combined. Default is 5.
     */
    @Updatable
    public Integer getQueryPlansPerMinute() {
        return queryPlansPerMinute;
    }

    public void setQueryPlansPerMinute(Integer queryPlansPerMinute) {
        this.queryPlansPerMinute = queryPlansPerMinute;
    }

    /**
     * The maximum query length stored in bytes. Default value: 1024 bytes.
     * Changing query length will restart the database.
     */
    @Range(min = 256, max = 4500)
    @Updatable
    public Integer getQueryStringLength() {
        return queryStringLength;
    }

    public void setQueryStringLength(Integer queryStringLength) {
        this.queryStringLength = queryStringLength;
    }

    /**
     * When set to ``true``, query Insights will record application tags from query.
     */
    @Updatable
    public Boolean getRecordApplicationTags() {
        return recordApplicationTags;
    }

    public void setRecordApplicationTags(Boolean recordApplicationTags) {
        this.recordApplicationTags = recordApplicationTags;
    }

    /**
     * When set to ``true``, query Insights will record client address from query.
     */
    @Updatable
    public Boolean getRecordClientAddress() {
        return recordClientAddress;
    }

    public void setRecordClientAddress(Boolean recordClientAddress) {
        this.recordClientAddress = recordClientAddress;
    }

    @Override
    public void copyFrom(InsightsConfig model) throws Exception {
        setQueryInsightsEnabled(model.getQueryInsightsEnabled());
        setQueryPlansPerMinute(model.getQueryPlansPerMinute());
        setQueryStringLength(model.getQueryStringLength());
        setRecordApplicationTags(model.getRecordApplicationTags());
        setRecordClientAddress(model.getRecordClientAddress());
    }

    @Override
    public String primaryKey() {
        return "";
    }

    public InsightsConfig toInsightsConfig() {
        InsightsConfig config = new InsightsConfig();

        if (getQueryInsightsEnabled() != null) {
            config.setQueryInsightsEnabled(getQueryInsightsEnabled());
        }

        if (getQueryPlansPerMinute() != null) {
            config.setQueryPlansPerMinute(getQueryPlansPerMinute());
        }

        if (getQueryStringLength() != null) {
            config.setQueryStringLength(getQueryStringLength());
        }

        if (getRecordApplicationTags() != null) {
            config.setRecordApplicationTags(getRecordApplicationTags());
        }

        if (getRecordClientAddress() != null) {
            config.setRecordClientAddress(getRecordClientAddress());
        }

        return config;
    }
}
