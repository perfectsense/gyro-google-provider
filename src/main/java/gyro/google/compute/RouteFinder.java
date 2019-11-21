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
import com.google.api.services.compute.model.Route;
import gyro.core.GyroException;
import gyro.core.Type;
import gyro.google.GoogleFinder;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Query route.
 *
 * Example
 * -------
 *
 * .. code-block:: gyro
 *
 *    route: $(external-query google::route { name: 'route-example'})
 */
@Type("route")
public class RouteFinder extends GoogleFinder<Compute, Route, RouteResource> {
    private String name;

    /**
     * The name of the route.
     */
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    protected List<Route> findAllGoogle(Compute client) {
        try {
            return client.routes().list(getProjectId()).execute().getItems();
        } catch (GoogleJsonResponseException je) {
            throw new GyroException(je.getDetails().getMessage());
        } catch (Exception ex) {
            throw new GyroException(ex.getMessage(), ex.getCause());
        }
    }

    @Override
    protected List<Route> findGoogle(Compute client, Map<String, String> filters) {
        Route route = null;

        try {
            route = client.routes().get(getProjectId(), filters.get("name")).execute();
        } catch (GoogleJsonResponseException je) {
            if (je.getDetails().getCode() != 404) {
                throw new GyroException(je.getDetails().getMessage());
            }
        } catch (Exception ex) {
            throw new GyroException(ex.getMessage(), ex.getCause());
        }

        if (route != null) {
            return Collections.singletonList(route);
        } else {
            return Collections.emptyList();
        }
    }
}
