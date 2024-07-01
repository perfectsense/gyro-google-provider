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

import java.util.Set;

import com.google.api.services.sqladmin.SQLAdmin;
import com.google.api.services.sqladmin.model.Operation;
import gyro.core.GyroUI;
import gyro.core.Type;
import gyro.core.resource.Output;
import gyro.core.resource.Resource;
import gyro.core.scope.State;
import gyro.core.validation.ValidStrings;
import gyro.google.Copyable;
import gyro.google.GoogleResource;

@Type("sql-operation")
public class OperationResource extends GoogleResource implements Copyable<Operation> {

    private AcquireSsrsLeaseContext acquireSsrsLeaseContext;

    private ApiWarning apiWarning;

    private BackupContext backupContext;

    private String endTime;

    private OperationErrors error;

    private ExportContext exportContext;

    private ImportContext importContext;

    private String insertTime;

    private String name;

    private String operationType;

    private String selfLink;

    private String startTime;

    private String status;

    private String targetId;

    private String targetLink;

    private String targetProject;

    private UserResource user;

    /**
     * The context for acquire SSRS lease operation, if applicable.
     *
     * @subresource gyro.google.sqladmin.base.AcquireSsrsLeaseContext
     */
    public AcquireSsrsLeaseContext getAcquireSsrsLeaseContext() {
        return acquireSsrsLeaseContext;
    }

    public void setAcquireSsrsLeaseContext(AcquireSsrsLeaseContext acquireSsrsLeaseContext) {
        this.acquireSsrsLeaseContext = acquireSsrsLeaseContext;
    }

    /**
     * An Admin API warning message.
     *
     * @subresource gyro.google.sqladmin.base.ApiWarning
     */
    public ApiWarning getApiWarning() {
        return apiWarning;
    }

    public void setApiWarning(ApiWarning apiWarning) {
        this.apiWarning = apiWarning;
    }

    /**
     * The context for backup operation, if applicable.
     *
     * @subresource gyro.google.sqladmin.base.BackupContext
     */
    public BackupContext getBackupContext() {
        return backupContext;
    }

    public void setBackupContext(BackupContext backupContext) {
        this.backupContext = backupContext;
    }

    /**
     * The time this operation finished in UTC timezone in [RFC 3339](https://tools.ietf.org/html/rfc3339) format, for example `2012-11-15T16:19:00.094Z`.
     */
    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    /**
     * If errors occurred during processing of this operation, this field will be populated.
     *
     * @subresource gyro.google.sqladmin.base.OperationErrors
     */
    public OperationErrors getError() {
        return error;
    }

    public void setError(OperationErrors error) {
        this.error = error;
    }

    /**
     * The context for export operation, if applicable.
     *
     * @subresource gyro.google.sqladmin.base.ExportContext
     */
    public ExportContext getExportContext() {
        return exportContext;
    }

    public void setExportContext(ExportContext exportContext) {
        this.exportContext = exportContext;
    }

    /**
     * The context for import operation, if applicable.
     *
     * @subresource gyro.google.sqladmin.base.ImportContext
     */
    public ImportContext getImportContext() {
        return importContext;
    }

    public void setImportContext(ImportContext importContext) {
        this.importContext = importContext;
    }

    /**
     * The time this operation was enqueued in UTC timezone in [RFC 3339](https://tools.ietf.org/html/rfc3339) format, for example `2012-11-15T16:19:00.094Z`.
     */
    public String getInsertTime() {
        return insertTime;
    }

    public void setInsertTime(String insertTime) {
        this.insertTime = insertTime;
    }

    /**
     * An identifier that uniquely identifies the operation. You can use this identifier to retrieve the Operations resource that has information about the operation.
     */
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * The type of the operation. Valid values are: * `CREATE` * `DELETE` * `UPDATE` * `RESTART` * `IMPORT` * `EXPORT` * `BACKUP_VOLUME` * `RESTORE_VOLUME` * `CREATE_USER` * `DELETE_USER` * `CREATE_DATABASE` * `DELETE_DATABASE`
     */
    @ValidStrings({
        "SQL_OPERATION_TYPE_UNSPECIFIED",
        "IMPORT",
        "EXPORT",
        "CREATE",
        "UPDATE",
        "DELETE",
        "RESTART",
        "BACKUP",
        "SNAPSHOT",
        "BACKUP_VOLUME",
        "DELETE_VOLUME",
        "RESTORE_VOLUME",
        "INJECT_USER",
        "CLONE",
        "STOP_REPLICA",
        "START_REPLICA",
        "PROMOTE_REPLICA",
        "CREATE_REPLICA",
        "CREATE_USER",
        "DELETE_USER",
        "UPDATE_USER",
        "CREATE_DATABASE",
        "DELETE_DATABASE",
        "UPDATE_DATABASE",
        "FAILOVER",
        "DELETE_BACKUP",
        "RECREATE_REPLICA",
        "TRUNCATE_LOG",
        "DEMOTE_MASTER",
        "MAINTENANCE",
        "ENABLE_PRIVATE_IP",
        "DEFER_MAINTENANCE",
        "CREATE_CLONE",
        "RESCHEDULE_MAINTENANCE",
        "START_EXTERNAL_SYNC",
        "LOG_CLEANUP",
        "AUTO_RESTART",
        "REENCRYPT",
        "SWITCHOVER",
        "ACQUIRE_SSRS_LEASE",
        "RELEASE_SSRS_LEASE",
        "RECONFIGURE_OLD_PRIMARY",
        "CLUSTER_MAINTENANCE",
        "SELF_SERVICE_MAINTENANCE",
        "SWITCHOVER_TO_REPLICA"
    })
    public String getOperationType() {
        return operationType;
    }

    public void setOperationType(String operationType) {
        this.operationType = operationType;
    }

    /**
     * The URI of this resource.
     */
    @Output
    public String getSelfLink() {
        return selfLink;
    }

    public void setSelfLink(String selfLink) {
        this.selfLink = selfLink;
    }

    /**
     * The time this operation actually started in UTC timezone in [RFC 3339](https://tools.ietf.org/html/rfc3339) format, for example `2012-11-15T16:19:00.094Z`.
     */
    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    /**
     * The status of an operation.
     */
    @ValidStrings({
        "SQL_OPERATION_STATUS_UNSPECIFIED",
        "PENDING",
        "RUNNING",
        "DONE"
    })
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Name of the database instance related to this operation.
     */
    public String getTargetId() {
        return targetId;
    }

    public void setTargetId(String targetId) {
        this.targetId = targetId;
    }

    /**
     * The URI of the instance related to this operation.
     */
    public String getTargetLink() {
        return targetLink;
    }

    public void setTargetLink(String targetLink) {
        this.targetLink = targetLink;
    }

    /**
     * The project ID of the target instance related to this operation.
     */
    public String getTargetProject() {
        return targetProject;
    }

    public void setTargetProject(String targetProject) {
        this.targetProject = targetProject;
    }

    /**
     * The email address of the user who initiated this operation.
     */
    public UserResource getUser() {
        return user;
    }

    public void setUser(UserResource user) {
        this.user = user;
    }

    @Override
    public void copyFrom(Operation model) {
        setEndTime(model.getEndTime());
        setInsertTime(model.getInsertTime());
        setName(model.getName());
        setOperationType(model.getOperationType());
        setSelfLink(model.getSelfLink());
        setStartTime(model.getStartTime());
        setStatus(model.getStatus());
        setTargetId(model.getTargetId());
        setTargetLink(model.getTargetLink());
        setTargetProject(model.getTargetProject());

        setAcquireSsrsLeaseContext(null);
        if (model.getAcquireSsrsLeaseContext() != null) {
            AcquireSsrsLeaseContext context = newSubresource(AcquireSsrsLeaseContext.class);
            context.copyFrom(model.getAcquireSsrsLeaseContext());
            setAcquireSsrsLeaseContext(context);
        }

        setApiWarning(null);
        if (model.getApiWarning() != null) {
            ApiWarning warning = newSubresource(ApiWarning.class);
            warning.copyFrom(model.getApiWarning());
            setApiWarning(warning);
        }

        setBackupContext(null);
        if (model.getBackupContext() != null) {
            BackupContext context = newSubresource(BackupContext.class);
            context.copyFrom(model.getBackupContext());
            setBackupContext(context);
        }

        setError(null);
        if (model.getError() != null) {
            OperationErrors error = newSubresource(OperationErrors.class);
            error.copyFrom(model.getError());
            setError(error);
        }

        setExportContext(null);
        if (model.getExportContext() != null) {
            ExportContext context = newSubresource(ExportContext.class);
            context.copyFrom(model.getExportContext());
            setExportContext(context);
        }

        setImportContext(null);
        if (model.getImportContext() != null) {
            ImportContext context = newSubresource(ImportContext.class);
            context.copyFrom(model.getImportContext());
            setImportContext(context);
        }

        setUser(null);
        if (model.getUser() != null) {
            setUser(findById(UserResource.class, model.getUser()));
        }
    }

    @Override
    protected boolean doRefresh() {
        SQLAdmin client = createClient(SQLAdmin.class);

        Operation operation = null;

        try {
            operation = client.operations().get(getProjectId(), getName()).execute();
        } catch (Exception ex) {
            // Operation not found
        }

        return false;
    }

    @Override
    protected void doCreate(GyroUI ui, State state) {

    }

    @Override
    protected void doUpdate(GyroUI ui, State state, Resource current, Set<String> changedFieldNames) {

    }

    @Override
    protected void doDelete(GyroUI ui, State state) {

    }
}
