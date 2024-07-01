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
import java.util.concurrent.TimeUnit;

import com.google.api.services.sqladmin.SQLAdmin;
import com.google.api.services.sqladmin.model.Database;
import com.google.api.services.sqladmin.model.Operation;
import gyro.core.GyroUI;
import gyro.core.Type;
import gyro.core.resource.Id;
import gyro.core.resource.Output;
import gyro.core.resource.Resource;
import gyro.core.scope.State;
import gyro.google.Copyable;

@Type("sql-database")
public class DatabaseResource extends SqlAdminResource implements Copyable<Database> {

    private String charset;

    private String collation;

    private String instance;

    private String name;

    private String project;

    private String selfLink;

    private SqlServerDatabaseDetails sqlserverDatabaseDetails;

    /**
     * The Cloud SQL charset value.
     */
    public String getCharset() {
        return charset;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }

    /**
     * The Cloud SQL collation value.
     */
    public String getCollation() {
        return collation;
    }

    public void setCollation(String collation) {
        this.collation = collation;
    }

    /**
     * The name of the Cloud SQL instance. This does not include the project ID.
     */
    public String getInstance() {
        return instance;
    }

    public void setInstance(String instance) {
        this.instance = instance;
    }

    /**
     * The name of the database in the Cloud SQL instance. This does not include the project ID or instance name.
     */
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * The project ID of the project containing the Cloud SQL database. The Google apps domain is prefixed if applicable.
     */
    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    /**
     * The URI of this resource.
     */
    @Id
    @Output
    public String getSelfLink() {
        return selfLink;
    }

    public void setSelfLink(String selfLink) {
        this.selfLink = selfLink;
    }

    /**
     * The details of the SQL Server database.
     *
     * @subresource gyro.google.sqladmin.base.SqlServerDatabaseDetails
     */
    public SqlServerDatabaseDetails getSqlserverDatabaseDetails() {
        return sqlserverDatabaseDetails;
    }

    public void setSqlserverDatabaseDetails(SqlServerDatabaseDetails sqlserverDatabaseDetails) {
        this.sqlserverDatabaseDetails = sqlserverDatabaseDetails;
    }

    @Override
    public void copyFrom(Database model) {
        setCharset(model.getCharset());
        setCollation(model.getCollation());
        setInstance(model.getInstance());
        setName(model.getName());
        setProject(model.getProject());
        setSelfLink(model.getSelfLink());

        setSqlserverDatabaseDetails(null);
        if (model.getSqlserverDatabaseDetails() != null) {
            SqlServerDatabaseDetails details = newSubresource(SqlServerDatabaseDetails.class);
            details.copyFrom(model.getSqlserverDatabaseDetails());
            setSqlserverDatabaseDetails(details);
        }
    }

    @Override
    protected boolean doRefresh() {
        SQLAdmin client = createClient(SQLAdmin.class);

        try {
            Database database = client.databases().get(getProject(), getInstance(), getName()).execute();

            if (database != null) {
                copyFrom(database);

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

        Database database = getOrCreateDatabase(null, null);

        Operation operation = client.databases().insert(getProject(), getInstance(), database).execute();

        waitForCompletion(operation, 30, TimeUnit.MINUTES, getProjectId(), this);

        doRefresh();
    }

    @Override
    protected void doUpdate(GyroUI ui, State state, Resource current, Set<String> changedFieldNames) throws IOException {
        SQLAdmin client = createClient(SQLAdmin.class);

        Database currentDatabase = client.databases().get(getProject(), getInstance(), getName()).execute();

        Database database = getOrCreateDatabase(currentDatabase, changedFieldNames);

        Operation operation = client.databases().patch(getProject(), getInstance(), getName(), database).execute();

        waitForCompletion(operation, 30, TimeUnit.MINUTES, getProjectId(), this);
    }

    @Override
    protected void doDelete(GyroUI ui, State state) throws IOException {
        SQLAdmin client = createClient(SQLAdmin.class);

        Operation operation = client.databases().delete(getProject(), getInstance(), getName()).execute();

        waitForCompletion(operation, 30, TimeUnit.MINUTES, getProjectId(), this);
    }

    private Database getOrCreateDatabase(Database current, Set<String> changedFieldNames) {
        if (current == null) {
            // new user
            current = new Database();

            current.setName(getName());
            current.setCharset(getCharset());
            current.setCollation(getCollation());
            current.setSqlserverDatabaseDetails(getSqlserverDatabaseDetails() != null ? getSqlserverDatabaseDetails().toSqlServerDatabaseDetails() : null);
            current.setProject(getProject());
            current.setInstance(getInstance());

        } else {
            // update the database
            if (changedFieldNames.contains("charset")) {
                current.setCharset(getCharset());
            }

            if (changedFieldNames.contains("collation")) {
                current.setCollation(getCollation());
            }

            if (changedFieldNames.contains("sqlserver-database-details")) {
                current.setSqlserverDatabaseDetails(getSqlserverDatabaseDetails() != null ? getSqlserverDatabaseDetails().toSqlServerDatabaseDetails() : null);
            }
        }

        return current;

    }
}
