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
import com.google.api.services.sqladmin.model.DatabaseInstance;
import gyro.core.Type;
import gyro.google.GoogleFinder;

/**
 * Query for sql-database-instance.
 *
 * Example
 * -------
 *
 * .. code-block:: gyro
 *
 *    database-instance: $(external-query google::sql-database-instance { name: 'database-instance-name'})
 */
@Type("sql-database-instance")
public class DatabaseInstanceFinder extends GoogleFinder<SQLAdmin, DatabaseInstance, DatabaseInstanceResource> {

    private String name;

    /**
     * The name of the Cloud SQL instance.
     */
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    protected List<DatabaseInstance> findAllGoogle(SQLAdmin client) throws Exception {
        return client.instances().list(getProjectId()).execute().getItems();
    }

    @Override
    protected List<DatabaseInstance> findGoogle(SQLAdmin client, Map<String, String> filters) throws Exception {
        List<DatabaseInstance> databaseInstances = new ArrayList<>();

        try {
            if (filters.containsKey("name")) {
                databaseInstances.add(client.instances().get(getProjectId(), filters.get("name")).execute());
            }
        } catch (IOException ex) {
            //Ignore
        }

        return databaseInstances;
    }
}
