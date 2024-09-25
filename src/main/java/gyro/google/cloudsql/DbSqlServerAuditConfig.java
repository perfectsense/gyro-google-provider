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

import com.google.api.services.sqladmin.model.SqlServerAuditConfig;
import gyro.core.resource.Diffable;
import gyro.core.resource.Updatable;
import gyro.core.validation.Required;
import gyro.google.Copyable;
import gyro.google.storage.BucketResource;

public class DbSqlServerAuditConfig extends Diffable implements Copyable<SqlServerAuditConfig> {

    private BucketResource bucket;
    private String retentionInterval;
    private String uploadInterval;

    /**
     * The destination bucket for the audit files.
     */
    @Required
    @Updatable
    public BucketResource getBucket() {
        return bucket;
    }

    public void setBucket(BucketResource bucket) {
        this.bucket = bucket;
    }

    /**
     * The number of days for which to keep generated audit files.
     */
    @Updatable
    public String getRetentionInterval() {
        return retentionInterval;
    }

    public void setRetentionInterval(String retentionInterval) {
        this.retentionInterval = retentionInterval;
    }

    /**
     * The interval in minutes at which to upload generated audit files.
     */
    @Updatable
    public String getUploadInterval() {
        return uploadInterval;
    }

    public void setUploadInterval(String uploadInterval) {
        this.uploadInterval = uploadInterval;
    }

    @Override
    public void copyFrom(SqlServerAuditConfig model) throws Exception {
        setBucket(null);
        if (model.getBucket() != null) {
            setBucket(findById(BucketResource.class, model.getBucket()));
        }
        setRetentionInterval(model.getRetentionInterval());
        setUploadInterval(model.getUploadInterval());
    }

    @Override
    public String primaryKey() {
        return "";
    }

    public SqlServerAuditConfig toSqlServerAuditConfig() {
        SqlServerAuditConfig config = new SqlServerAuditConfig();

        config.setBucket(getBucket().getName());

        if (getRetentionInterval() != null) {
            config.setRetentionInterval(getRetentionInterval());
        }

        if (getUploadInterval() != null) {
            config.setUploadInterval(getUploadInterval());
        }

        return config;
    }
}
