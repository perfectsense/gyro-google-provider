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

import java.io.IOException;
import java.util.Set;

import com.google.api.services.sqladmin.SQLAdmin;
import com.google.api.services.sqladmin.model.BackupRun;
import com.google.api.services.sqladmin.model.Operation;
import gyro.core.GyroUI;
import gyro.core.Type;
import gyro.core.resource.Output;
import gyro.core.resource.Resource;
import gyro.core.scope.State;
import gyro.core.validation.ValidStrings;
import gyro.google.Copyable;

@Type("sql-backup-run")
public class BackupRunResource extends SqlAdminResource implements Copyable<BackupRun> {

    private String backupKind;

    private String description;

    private DiskEncryptionConfiguration diskEncryptionConfiguration;

    private DiskEncryptionStatus diskEncryptionStatus;

    private String endTime;

    private String enqueuedTime;

    private OperationError error;

    private Long backupRunId;

    private String instance;

    private String location;

    private String selfLink;

    private String startTime;

    private String status;

    private String timeZone;

    private String type;

    private String windowStartTime;

    /**
     * Specifies the kind of backup, PHYSICAL or DEFAULT_SNAPSHOT.
     */
    @ValidStrings({
        "SQL_BACKUP_KIND_UNSPECIFIED",
        "SNAPSHOT",
        "PHYSICAL"
    })
    public String getBackupKind() {
        return backupKind;
    }

    public void setBackupKind(String backupKind) {
        this.backupKind = backupKind;
    }

    /**
     * The description of this run, only applicable to on-demand backups.
     */
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Encryption configuration specific to a backup.
     *
     * @subresource gyro.google.sqladmin.base.DiskEncryptionConfiguration
     */
    public DiskEncryptionConfiguration getDiskEncryptionConfiguration() {
        return diskEncryptionConfiguration;
    }

    public void setDiskEncryptionConfiguration(
        DiskEncryptionConfiguration diskEncryptionConfiguration) {
        this.diskEncryptionConfiguration = diskEncryptionConfiguration;
    }

    /**
     * Encryption status specific to a backup.
     *
     * @subresource gyro.google.sqladmin.base.DiskEncryptionStatus
     */
    public DiskEncryptionStatus getDiskEncryptionStatus() {
        return diskEncryptionStatus;
    }

    public void setDiskEncryptionStatus(DiskEncryptionStatus diskEncryptionStatus) {
        this.diskEncryptionStatus = diskEncryptionStatus;
    }

    /**
     * The time the backup operation completed in UTC timezone in [RFC 3339](https://tools.ietf.org/html/rfc3339) format, for example `2012-11-15T16:19:00.094Z`.
     */
    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    /**
     * The time the run was enqueued in UTC timezone in [RFC 3339](https://tools.ietf.org/html/rfc3339) format, for example `2012-11-15T16:19:00.094Z`.
     */
    public String getEnqueuedTime() {
        return enqueuedTime;
    }

    public void setEnqueuedTime(String enqueuedTime) {
        this.enqueuedTime = enqueuedTime;
    }

    /**
     * Information about why the backup operation failed. This is only present if the run has the FAILED status.
     *
     * @subresource gyro.google.sqladmin.base.OperationError
     */
    @Output
    public OperationError getError() {
        return error;
    }

    public void setError(OperationError error) {
        this.error = error;
    }

    /**
     * The identifier for this backup run. Unique only for a specific Cloud SQL instance.
     */
    @Output
    public Long getBackupRunId() {
        return backupRunId;
    }

    public void setBackupRunId(Long backupRunId) {
        this.backupRunId = backupRunId;
    }

    /**
     * Name of the database instance.
     */
    public String getInstance() {
        return instance;
    }

    public void setInstance(String instance) {
        this.instance = instance;
    }

    /**
     * Location of the backups.
     */
    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    /**
     * The URI of this resource.
     */
    public String getSelfLink() {
        return selfLink;
    }

    public void setSelfLink(String selfLink) {
        this.selfLink = selfLink;
    }

    /**
     * The time the backup operation actually started in UTC timezone in [RFC 3339](https://tools.ietf.org/html/rfc3339) format, for example `2012-11-15T16:19:00.094Z`.
     */
    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    /**
     * The status of this run.
     */
    @ValidStrings({
        "SQL_BACKUP_RUN_STATUS_UNSPECIFIED",
        "ENQUEUED",
        "OVERDUE",
        "RUNNING",
        "FAILED",
        "SUCCESSFUL",
        "SKIPPED",
        "DELETION_PENDING",
        "DELETION_FAILED",
        "DELETED"
    })
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Backup time zone to prevent restores to an instance with a different time zone. Now relevant only for SQL Server.
     */
    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    /**
     * The type of this run; can be either "AUTOMATED" or "ON_DEMAND" or "FINAL". This field defaults to "ON_DEMAND" and is ignored, when specified for insert requests.
     */
    @ValidStrings({
        "SQL_BACKUP_RUN_TYPE_UNSPECIFIED",
        "AUTOMATED",
        "ON_DEMAND"
    })
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    /**
     * The start time of the backup window during which this the backup was attempted in [RFC 3339](https://tools.ietf.org/html/rfc3339) format, for example `2012-11-15T16:19:00.094Z`.
     */
    public String getWindowStartTime() {
        return windowStartTime;
    }

    public void setWindowStartTime(String windowStartTime) {
        this.windowStartTime = windowStartTime;
    }

    @Override
    public void copyFrom(BackupRun model) {
        setBackupKind(model.getBackupKind());
        setDescription(model.getDescription());
        setEndTime(model.getEndTime());
        setEnqueuedTime(model.getEnqueuedTime());
        setBackupRunId(model.getId());
        setLocation(model.getLocation());
        setSelfLink(model.getSelfLink());
        setStartTime(model.getStartTime());
        setStatus(model.getStatus());
        setTimeZone(model.getTimeZone());
        setType(model.getType());
        setWindowStartTime(model.getWindowStartTime());

        setDiskEncryptionConfiguration(null);
        if (model.getDiskEncryptionConfiguration() != null) {
            DiskEncryptionConfiguration configuration = newSubresource(DiskEncryptionConfiguration.class);
            configuration.copyFrom(model.getDiskEncryptionConfiguration());
            setDiskEncryptionConfiguration(configuration);
        }

        setDiskEncryptionStatus(null);
        if (model.getDiskEncryptionStatus() != null) {
            DiskEncryptionStatus status = newSubresource(DiskEncryptionStatus.class);
            status.copyFrom(model.getDiskEncryptionStatus());
            setDiskEncryptionStatus(status);
        }

        setError(null);
        if (model.getError() != null) {
            OperationError error = newSubresource(OperationError.class);
            error.copyFrom(model.getError());
            setError(error);
        }
    }

    @Override
    protected boolean doRefresh() {
        SQLAdmin client = createClient(SQLAdmin.class);

        try {
            BackupRun backupRun = client.backupRuns().get(getProjectId(), getInstance(), getBackupRunId()).execute();

            if (backupRun != null) {
                copyFrom(backupRun);

                return true;
            }
        } catch (IOException ex) {
            // ignore
        }

        return false;
    }

    @Override
    protected void doCreate(GyroUI ui, State state) throws IOException {
        SQLAdmin client = createClient(SQLAdmin.class);

        BackupRun backupRun = new BackupRun();

        Operation operation = client.backupRuns().insert(getProjectId(), getInstance(), backupRun).execute();

        waitForCompletion(operation, getProjectId(), client);

        doRefresh();
    }

    @Override
    protected void doUpdate(GyroUI ui, State state, Resource current, Set<String> changedFieldNames) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void doDelete(GyroUI ui, State state) throws IOException {
        SQLAdmin client = createClient(SQLAdmin.class);

        Operation operation = client.backupRuns().delete(getProjectId(), getInstance(), getBackupRunId()).execute();

        waitForCompletion(operation, getProjectId(), client);
    }
}
