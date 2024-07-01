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

public class InsightsConfig extends Diffable
    implements Copyable<com.google.api.services.sqladmin.model.InsightsConfig> {

    private Boolean queryInsightsEnabled;

    private Integer queryPlansPerMinute;

    private Integer queryStringLength;

    private Boolean recordApplicationTags;

    private Boolean recordClientAddress;

    /**
     * Whether Query Insights feature is enabled.
     */
    public Boolean getQueryInsightsEnabled() {
        return queryInsightsEnabled;
    }

    public void setQueryInsightsEnabled(Boolean queryInsightsEnabled) {
        this.queryInsightsEnabled = queryInsightsEnabled;
    }

    /**
     * Number of query execution plans captured by Insights per minute for all queries combined. Default is 5.
     */
    public Integer getQueryPlansPerMinute() {
        return queryPlansPerMinute;
    }

    public void setQueryPlansPerMinute(Integer queryPlansPerMinute) {
        this.queryPlansPerMinute = queryPlansPerMinute;
    }

    /**
     * Maximum query length stored in bytes. Default value: 1024 bytes. Range: 256-4500 bytes. Query length more than this field value will be truncated to this value. When unset, query length will be the default value. Changing query length will restart the database.
     */
    public Integer getQueryStringLength() {
        return queryStringLength;
    }

    public void setQueryStringLength(Integer queryStringLength) {
        this.queryStringLength = queryStringLength;
    }

    /**
     * Whether Query Insights will record application tags from query when enabled.
     */
    public Boolean getRecordApplicationTags() {
        return recordApplicationTags;
    }

    public void setRecordApplicationTags(Boolean recordApplicationTags) {
        this.recordApplicationTags = recordApplicationTags;
    }

    /**
     * Whether Query Insights will record client address when enabled.
     */
    public Boolean getRecordClientAddress() {
        return recordClientAddress;
    }

    public void setRecordClientAddress(Boolean recordClientAddress) {
        this.recordClientAddress = recordClientAddress;
    }

    @Override
    public String primaryKey() {
        return "";
    }

    @Override
    public void copyFrom(com.google.api.services.sqladmin.model.InsightsConfig model) {
        setQueryInsightsEnabled(model.getQueryInsightsEnabled());
        setQueryPlansPerMinute(model.getQueryPlansPerMinute());
        setQueryStringLength(model.getQueryStringLength());
        setRecordApplicationTags(model.getRecordApplicationTags());
        setRecordClientAddress(model.getRecordClientAddress());
    }

    com.google.api.services.sqladmin.model.InsightsConfig toInsightsConfig() {
        com.google.api.services.sqladmin.model.InsightsConfig insightsConfig = new com.google.api.services.sqladmin.model.InsightsConfig();
        insightsConfig.setQueryInsightsEnabled(getQueryInsightsEnabled());
        insightsConfig.setQueryPlansPerMinute(getQueryPlansPerMinute());
        insightsConfig.setQueryStringLength(getQueryStringLength());
        insightsConfig.setRecordApplicationTags(getRecordApplicationTags());
        insightsConfig.setRecordClientAddress(getRecordClientAddress());

        return insightsConfig;
    }
}
