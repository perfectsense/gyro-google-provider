/*
 * Copyright 2019, Perfect Sense, Inc.
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

package gyro.google.compute;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.compute.Compute;
import com.google.api.services.compute.model.HealthCheck;
import gyro.core.GyroException;
import gyro.core.Type;
import gyro.google.GoogleFinder;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Query health check.
 *
 * Example
 * -------
 *
 * .. code-block:: gyro
 *
 *    compute-healthcheck: $(external-query google::healthcheck { name: 'healthcheck-example'})
 */
@Type("compute-healthcheck")
public class HealthCheckFinder extends GoogleFinder<Compute, HealthCheck, HealthCheckResource> {
    private String name;

    /**
     * The name of the health check.
     */
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    protected List<HealthCheck> findAllGoogle(Compute client) {
        try {
            return client.healthChecks().list(getProjectId()).execute().getItems();
        } catch (GoogleJsonResponseException je) {
            throw new GyroException(je.getDetails().getMessage());
        } catch (IOException ex) {
            throw new GyroException(ex);
        }
    }

    @Override
    protected List<HealthCheck> findGoogle(Compute client, Map<String, String> filters) {
        HealthCheck healthcheck = null;

        try {
            healthcheck = client.healthChecks().get(getProjectId(), filters.get("name")).execute();
        } catch (GoogleJsonResponseException je) {
            if (!je.getDetails().getMessage().matches("The resource (.*) was not found")) {
                throw new GyroException(je.getDetails().getMessage());
            }
        } catch (IOException ex) {
            throw new GyroException(ex);
        }

        if (healthcheck != null) {
            return Collections.singletonList(healthcheck);
        } else {
            return Collections.emptyList();
        }
    }
}
