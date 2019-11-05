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

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.services.json.AbstractGoogleJsonClient;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.compute.Compute;
import com.google.auth.http.HttpCredentialsAdapter;
import gyro.core.GyroException;
import gyro.core.GyroInputStream;
import gyro.core.auth.Credentials;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;

public class GoogleCredentials extends Credentials {

    private String projectId;
    private String credentialFilePath;

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getCredentialFilePath() {
        return credentialFilePath;
    }

    public void setCredentialFilePath(String credentialFilePath) {
        this.credentialFilePath = credentialFilePath;
    }

    @SuppressWarnings("unchecked")
    public <T extends AbstractGoogleJsonClient> T createClient(Class<T> clientClass) {
        try {
            HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();

            com.google.auth.oauth2.GoogleCredentials googleCredentials = null;

            try (GyroInputStream input = openInput(getCredentialFilePath())) {
                googleCredentials = com.google.auth.oauth2.GoogleCredentials.fromStream(input)
                    .createScoped(Collections.singleton("https://www.googleapis.com/auth/cloud-platform"));
            } catch (Exception ex) {
                throw new GyroException("Could not load credentials file.");
            }

            switch (clientClass.getSimpleName()) {
                case "Compute": return (T) new Compute.Builder(httpTransport, jsonFactory, new HttpCredentialsAdapter(googleCredentials))
                                .setApplicationName("gyro-google-provider").build();

                default: throw new GyroException(String.format("No client found for class %s", clientClass.getSimpleName()));
            }
        } catch (GeneralSecurityException | IOException e) {
            throw new GyroException(String.format("Unable to create %s client", clientClass.getSimpleName()));
        }
    }
}
