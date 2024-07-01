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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.api.services.sqladmin.SQLAdmin;
import com.google.api.services.sqladmin.model.Database;
import gyro.core.Type;
import gyro.google.GoogleFinder;

/**
 * Query for sql-database.
 *
 * Example
 * -------
 *
 * .. code-block:: gyro
 *
 *    database: $(external-query google::sql-database { name: 'database-name', instance: 'instance-name'})
 */
@Type("sql-database")
public class DatabaseFinder extends GoogleFinder<SQLAdmin, Database, DatabaseResource> {

    private String instance;

    private String name;

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
     * The name of the database in the Cloud SQL instance.
     */
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    protected List<Database> findAllGoogle(SQLAdmin client) throws Exception {
        throw new UnsupportedOperationException("Finding all databases without filters is not supported.");
    }

    @Override
    protected List<Database> findGoogle(SQLAdmin client, Map<String, String> filters) throws Exception {
        List<Database> databases = new ArrayList<>();
        try {
            if (filters.containsKey("name") && filters.containsKey("instance")) {
                databases.add(client.databases().get(getProjectId(), filters.get("instance"), filters.get("name")).execute());
            } else if (filters.containsKey("instance")) {
                databases.addAll(client.databases().list(getProjectId(), filters.get("instance")).execute().getItems());
            }

        } catch (IOException ex) {
            // ignore
        }

        return databases;
    }
}
