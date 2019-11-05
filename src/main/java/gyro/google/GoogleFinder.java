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

package gyro.google;

import com.google.api.client.googleapis.services.json.AbstractGoogleJsonClient;
import com.psddev.dari.util.TypeDefinition;
import gyro.core.finder.Finder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class GoogleFinder<C extends AbstractGoogleJsonClient, M, R extends GoogleResource> extends Finder<R> {
    protected abstract List<M> findAllGoogle(C client);
    protected abstract List<M> findGoogle(C client, Map<String, String> filters);

    @Override
    public List<R> findAll() {
        return findAllGoogle(newClient()).stream()
            .map(this::newResource)
            .collect(Collectors.toList());
    }

    @Override
    public List<R> find(Map<String, Object> filters) {
        return findGoogle(newClient(), convertFilters(filters)).stream()
            .map(this::newResource)
            .collect(Collectors.toList());
    }

    protected C newClient() {
        @SuppressWarnings("unchecked")
        Class<C> clientClass = (Class<C>) TypeDefinition.getInstance(getClass())
            .getInferredGenericTypeArgumentClass(GoogleFinder.class, 0);

        return GoogleResource.creatClient(clientClass, credentials(GoogleCredentials.class));
    }

    @SuppressWarnings("unchecked")
    private R newResource(M model) {
        R resource = newResource();

        if (resource instanceof Copyable) {
            ((Copyable<M>) resource).copyFrom(model);
        }

        return resource;
    }

    @SuppressWarnings("unchecked")
    private Map<String, String> convertFilters(Map<String, Object> query) {
        Map<String, String> filters = new HashMap<>();

        for (Map.Entry<String, Object> e : query.entrySet()) {
            filters.put(e.getKey(), e.getValue().toString());
        }

        return filters;
    }

    public String getProjectId() {
        return credentials(GoogleCredentials.class).getProjectId();
    }
}