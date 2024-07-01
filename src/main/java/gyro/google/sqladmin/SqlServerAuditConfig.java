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

public class SqlServerAuditConfig extends Diffable
    implements Copyable<com.google.api.services.sqladmin.model.SqlServerAuditConfig> {

    private String bucket;

    private String retentionInterval;

    private String uploadInterval;

    /**
     * The name of the destination bucket (e.g., gs://mybucket).
     */
    public String getBucket() {
        return bucket;
    }

    public void setBucket(String bucket) {
        this.bucket = bucket;
    }

    /**
     * How long to keep generated audit files.
     */
    public String getRetentionInterval() {
        return retentionInterval;
    }

    public void setRetentionInterval(String retentionInterval) {
        this.retentionInterval = retentionInterval;
    }

    /**
     * How often to upload generated audit files.
     */
    public String getUploadInterval() {
        return uploadInterval;
    }

    public void setUploadInterval(String uploadInterval) {
        this.uploadInterval = uploadInterval;
    }

    @Override
    public String primaryKey() {
        return "";
    }

    @Override
    public void copyFrom(com.google.api.services.sqladmin.model.SqlServerAuditConfig model) {
        setBucket(model.getBucket());
        setRetentionInterval(model.getRetentionInterval());
        setUploadInterval(model.getUploadInterval());
    }

    com.google.api.services.sqladmin.model.SqlServerAuditConfig copyTo() {
        com.google.api.services.sqladmin.model.SqlServerAuditConfig sqlServerAuditConfig = new com.google.api.services.sqladmin.model.SqlServerAuditConfig();
        sqlServerAuditConfig.setBucket(getBucket());
        sqlServerAuditConfig.setRetentionInterval(getRetentionInterval());
        sqlServerAuditConfig.setUploadInterval(getUploadInterval());

        return sqlServerAuditConfig;
    }
}
