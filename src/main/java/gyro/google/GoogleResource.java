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

import java.io.IOException;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.api.client.googleapis.json.GoogleJsonError;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpResponseException;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.JsonParser;
import com.google.api.client.json.JsonToken;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.gax.rpc.InvalidArgumentException;
import gyro.core.GyroException;
import gyro.core.GyroUI;
import gyro.core.resource.Resource;
import gyro.core.scope.State;

public abstract class GoogleResource extends Resource {

    private static final String OAUTH_ERROR = "OAuth2Credentials instance does not support refreshing the access token";

    public static <T> T createClient(
        Class<T> clientClass,
        GoogleCredentials credentials) {
        return credentials.createClient(clientClass);
    }

    public <T> T createClient(Class<T> clientClass) {
        return createClient(clientClass, credentials(GoogleCredentials.class));
    }

    protected String getProjectId() {
        return credentials(GoogleCredentials.class).getProjectId();
    }

    protected abstract boolean doRefresh() throws Exception;

    @Override
    public final boolean refresh() {
        RETRY:
        try {
            return doRefresh();
        } catch (IllegalStateException ise) {
            if (ise.getMessage().contains(OAUTH_ERROR)) {
                credentials(GoogleCredentials.class).refresh();
                break RETRY;
            }

            throw ise;
        } catch (GyroException ex) {
            throw ex;
        } catch (GoogleJsonResponseException je) {
            if (je.getStatusCode() == 404 || (je.getDetails() != null && je.getDetails().getCode() == 404)) {
                return false;
            } else {
                throw new GyroException(formatGoogleExceptionMessage(je));
            }
        } catch (Exception ex) {
            throw new GyroException(ex);
        }

        return false;
    }

    protected abstract void doCreate(GyroUI ui, State state) throws Exception;

    private boolean handleApiExceptions(final Throwable throwable) {
        Throwable cause = throwable;
        while (cause.getCause() != null) {
            cause = cause.getCause();
        }

        if (cause instanceof HttpResponseException) {
            HttpResponseException httpResponseException = (HttpResponseException) cause;
            throw new GyroException(formatHttpExceptionMessage(httpResponseException));
        } else if (cause instanceof InvalidArgumentException) {
            throw new GyroException(cause.getMessage());
        } else if (cause instanceof IllegalStateException) {
            if (cause.getMessage().contains(OAUTH_ERROR)) {
                credentials(GoogleCredentials.class).refresh();
                return true;
            }
        }

        return false;
    }

    @Override
    public final void create(GyroUI ui, State state) {
        RETRY:
        try {
            doCreate(ui, state);
        } catch (Exception ex) {
            if (handleApiExceptions(ex)) {
                break RETRY;
            }

            throw new GyroException(ex);
        }
    }

    protected abstract void doUpdate(GyroUI ui, State state, Resource current, Set<String> changedFieldNames)
        throws Exception;

    @Override
    public final void update(GyroUI ui, State state, Resource current, Set<String> changedFieldNames) {
        RETRY:
        try {
            doUpdate(ui, state, current, changedFieldNames);
        } catch (Exception ex) {
            if (handleApiExceptions(ex)) {
                break RETRY;
            }

            throw new GyroException(ex);
        }
    }

    protected abstract void doDelete(GyroUI ui, State state) throws Exception;

    @Override
    public final void delete(GyroUI ui, State state) {
        RETRY:
        try {
            doDelete(ui, state);
        } catch (Exception ex) {
            if (handleApiExceptions(ex)) {
                break RETRY;
            }

            throw new GyroException(ex);
        }
    }

    protected static String formatGoogleExceptionMessage(GoogleJsonResponseException je) {
        return je.getDetails().getErrors().stream()
            .map(GoogleJsonError.ErrorInfo::getMessage)
            .collect(Collectors.joining("\n"));
    }

    protected static String formatResource(String projectId, String resource) {
        return resource.contains("projects/")
            ? resource.substring(resource.indexOf("projects/") + 9)
            : resource.startsWith("global/")
                ? projectId + "/" + resource
                : resource;
    }

    protected static String formatHttpExceptionMessage(HttpResponseException exception) {
        JsonFactory jsonFactory = new GsonFactory();
        GoogleJsonError details = null;
        try {
            try (JsonParser parser = jsonFactory.createJsonParser(exception.getContent())) {
                JsonToken currentToken = parser.getCurrentToken();

                // token is null at start, so get next token
                if (currentToken == null) {
                    currentToken = parser.nextToken();
                }

                // check for empty content
                if (currentToken != null) {
                    // make sure there is an "error" key
                    parser.skipToKey("error");

                    if (parser.getCurrentToken() == JsonToken.START_OBJECT) {
                        details = parser.parseAndClose(GoogleJsonError.class);
                    }
                }
            }
        } catch (IOException ex) {
            // ignore
        }

        if (details != null) {
            return details.getErrors().stream()
                .map(GoogleJsonError.ErrorInfo::getMessage)
                .collect(Collectors.joining("\n"));
        }

        return exception.getContent();
    }
}
