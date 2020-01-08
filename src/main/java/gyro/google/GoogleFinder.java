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

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.googleapis.services.json.AbstractGoogleJsonClient;
import com.psddev.dari.util.TypeDefinition;
import gyro.core.GyroException;
import gyro.core.finder.Finder;

public abstract class GoogleFinder<C extends AbstractGoogleJsonClient, M, R extends GoogleResource> extends Finder<R> {

    protected abstract List<M> findAllGoogle(C client) throws Exception;

    protected abstract List<M> findGoogle(C client, Map<String, String> filters) throws Exception;

    @Override
    public List<R> findAll() {
        try {
            return findAllGoogle(newClient()).stream()
                .map(this::newResource)
                .collect(Collectors.toList());
        } catch (GyroException ex) {
            throw ex;
        } catch (GoogleJsonResponseException je) {
            throw new GyroException(GoogleResource.formatGoogleExceptionMessage(je));
        } catch (Exception ex) {
            throw new GyroException(ex.getMessage(), ex.getCause());
        }
    }

    @Override
    public List<R> find(Map<String, Object> filters) {
        try {
            return findGoogle(newClient(), convertFilters(filters)).stream()
                .map(this::newResource)
                .collect(Collectors.toList());
        } catch (GyroException ex) {
            throw ex;
        } catch (GoogleJsonResponseException je) {
            if (je.getDetails().getCode() == 404) {
                return Collections.emptyList();
            } else {
                throw new GyroException(GoogleResource.formatGoogleExceptionMessage(je));
            }
        } catch (Exception ex) {
            throw new GyroException(ex.getMessage(), ex.getCause());
        }
    }

    protected C newClient() {
        @SuppressWarnings("unchecked")
        Class<C> clientClass = (Class<C>) TypeDefinition.getInstance(getClass())
            .getInferredGenericTypeArgumentClass(GoogleFinder.class, 0);

        return GoogleResource.createClient(clientClass, credentials(GoogleCredentials.class));
    }

    @SuppressWarnings("unchecked")
    private R newResource(M model) {
        R resource = newResource();

        if (resource instanceof Copyable) {
            try {
                ((Copyable<M>) resource).copyFrom(model);
            } catch (GyroException ex) {
                throw ex;
            } catch (GoogleJsonResponseException je) {
                if (je.getDetails().getCode() == 404) {
                    return null;
                } else {
                    throw new GyroException(GoogleResource.formatGoogleExceptionMessage(je));
                }
            } catch (Exception ex) {
                throw new GyroException(ex.getMessage(), ex.getCause());
            }
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
