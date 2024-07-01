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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.api.services.sqladmin.SQLAdmin;
import com.google.api.services.sqladmin.model.SslCert;
import gyro.core.Type;
import gyro.google.GoogleFinder;

/**
 * Query for ssl-cert.
 *
 * Example
 * -------
 *
 * .. code-block:: gyro
 *
 *    sql-ssl-cert: $(external-query google::sql-ssl-cert { instance: 'instance-name', name: 'ssl-cert-name'})
 */
@Type("sql-ssl-cert")
public class SslCertFinder extends GoogleFinder<SQLAdmin, SslCert, SslCertResource> {

    private String instance;
    private String name;

    /**
     * The instance ID of the Cloud SQL instance.
     */
    public String getInstance() {
        return instance;
    }

    public void setInstance(String instance) {
        this.instance = instance;
    }

    /**
     * The name of the SSL certificate.
     */
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    protected List<SslCert> findAllGoogle(SQLAdmin client) throws Exception {
        throw new UnsupportedOperationException("Finding all ssl certs without filters is not supported.");
    }

    @Override
    protected List<SslCert> findGoogle(SQLAdmin client, Map<String, String> filters) throws Exception {
        List<SslCert> sslCerts = new ArrayList<>();
        if (filters.containsKey("instance") && filters.containsKey("name")) {
            sslCerts.add(client.sslCerts().get(getProjectId(), filters.get("instance"), filters.get("name")).execute());
        } else if (filters.containsKey("instance")) {
            sslCerts.addAll(client.sslCerts().list(getProjectId(), filters.get("instance")).execute().getItems());
        } else {
            throw new IllegalArgumentException("'instance' is required to find an SSL certificate.");
        }

        return sslCerts;
    }
}
