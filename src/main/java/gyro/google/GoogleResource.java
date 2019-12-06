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

import java.util.Set;
import java.util.stream.Collectors;

import com.google.api.client.googleapis.json.GoogleJsonError;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.googleapis.services.json.AbstractGoogleJsonClient;
import gyro.core.GyroException;
import gyro.core.GyroUI;
import gyro.core.resource.Resource;
import gyro.core.scope.State;

public abstract class GoogleResource extends Resource {

    protected static <T extends AbstractGoogleJsonClient> T createClient(
        Class<T> clientClass,
        GoogleCredentials credentials) {
        return credentials.createClient(clientClass);
    }

    protected <T extends AbstractGoogleJsonClient> T createClient(Class<T> clientClass) {
        return createClient(clientClass, credentials(GoogleCredentials.class));
    }

    protected String getProjectId() {
        return credentials(GoogleCredentials.class).getProjectId();
    }

    protected abstract boolean doRefresh() throws Exception;

    @Override
    public final boolean refresh() {
        try {
            return doRefresh();
        } catch (GyroException ex) {
            throw ex;
        } catch (GoogleJsonResponseException je) {
            if (je.getDetails().getCode() != 404) {
                return false;
            } else {
                throw new GyroException(formatGoogleExceptionMessage(je));
            }
        } catch (Exception ex) {
            throw new GyroException(ex.getMessage(), ex.getCause());
        }
    }

    protected abstract void doCreate(GyroUI ui, State state) throws Exception;

    @Override
    public final void create(GyroUI ui, State state) {
        try {
            doCreate(ui, state);
        } catch (GyroException ex) {
            throw ex;
        } catch (GoogleJsonResponseException je) {
            throw new GyroException(formatGoogleExceptionMessage(je));
        } catch (Exception ex) {
            throw new GyroException(ex.getMessage(), ex.getCause());
        }
    }

    public abstract void doUpdate(GyroUI ui, State state, Resource current, Set<String> changedFieldNames)
        throws Exception;

    @Override
    public final void update(GyroUI ui, State state, Resource current, Set<String> changedFieldNames) {
        try {
            doUpdate(ui, state, current, changedFieldNames);
        } catch (GyroException ex) {
            throw ex;
        } catch (GoogleJsonResponseException je) {
            throw new GyroException(formatGoogleExceptionMessage(je));
        } catch (Exception ex) {
            throw new GyroException(ex.getMessage(), ex.getCause());
        }
    }

    public abstract void doDelete(GyroUI ui, State state) throws Exception;

    @Override
    public final void delete(GyroUI ui, State state) {
        try {
            doDelete(ui, state);
        } catch (GyroException ex) {
            throw ex;
        } catch (GoogleJsonResponseException je) {
            throw new GyroException(formatGoogleExceptionMessage(je));
        } catch (Exception ex) {
            throw new GyroException(ex.getMessage(), ex.getCause());
        }
    }

    protected static String formatGoogleExceptionMessage(GoogleJsonResponseException je) {
        return je.getDetails().getErrors().stream()
            .map(GoogleJsonError.ErrorInfo::getMessage)
            .collect(Collectors.joining("\n"));
    }
}
