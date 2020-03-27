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
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.Optional;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.services.json.AbstractGoogleJsonClient;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.cloud.kms.v1.KeyManagementServiceClient;
import com.google.cloud.kms.v1.KeyManagementServiceSettings;
import gyro.core.GyroException;
import gyro.core.GyroInputStream;
import gyro.core.auth.Credentials;

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

    public <T> T createClient(Class<T> clientClass) {
        if (AbstractGoogleJsonClient.class.isAssignableFrom(clientClass)) {
            return Optional.of(createClientBuilder((Class<? extends AbstractGoogleJsonClient>) clientClass))
                .map(AbstractGoogleJsonClient.Builder::build)
                .filter(clientClass::isInstance)
                .map(clientClass::cast)
                .orElseThrow(() -> new GyroException(
                    String.format("Unable to create %s client", clientClass.getSimpleName())));
        } else {
            return getNonGeneralizedClient(clientClass);
        }
    }

    public AbstractGoogleJsonClient.Builder createClientBuilder(Class<? extends AbstractGoogleJsonClient> clientClass) {
        try {
            HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();

            com.google.auth.oauth2.GoogleCredentials googleCredentials = getGoogleCredentials();

            for (Class<?> declaredClass : clientClass.getDeclaredClasses()) {
                if (Modifier.isStatic(declaredClass.getModifiers())
                    && AbstractGoogleJsonClient.Builder.class.isAssignableFrom(declaredClass)) {
                    Constructor<?> builderConstructor = declaredClass.getConstructor(
                        HttpTransport.class, JsonFactory.class, HttpRequestInitializer.class);
                    Object builder = builderConstructor.newInstance(
                        httpTransport, jsonFactory, new HttpCredentialsAdapter(googleCredentials));
                    Method setApplicationNameMethod = declaredClass.getDeclaredMethod(
                        "setApplicationName",
                        String.class);
                    setApplicationNameMethod.invoke(builder, "gyro-google-provider");
                    return AbstractGoogleJsonClient.Builder.class.cast(builder);
                }
            }
        } catch (GeneralSecurityException
            | IOException
            | NoSuchMethodException
            | InstantiationException
            | IllegalAccessException
            | InvocationTargetException e) {
            throw new GyroException(String.format("Unable to create %s client", clientClass.getSimpleName()));
        }
        throw new GyroException(String.format("No client found for class %s", clientClass.getSimpleName()));
    }

    private com.google.auth.oauth2.GoogleCredentials getGoogleCredentials() {
        try (GyroInputStream input = openInput(getCredentialFilePath())) {
            return com.google.auth.oauth2.GoogleCredentials.fromStream(input)
                .createScoped(Collections.singleton("https://www.googleapis.com/auth/cloud-platform"));
        } catch (Exception ex) {
            throw new GyroException("Could not load credentials file.");
        }
    }

    private <T> T getNonGeneralizedClient(Class<T> clientClass) {
        if (clientClass.getSimpleName().equals("KeyManagementServiceClient")) {
            try {
                KeyManagementServiceSettings keyManagementServiceSettings =
                    KeyManagementServiceSettings.newBuilder()
                        .setCredentialsProvider(FixedCredentialsProvider.create(getGoogleCredentials()))
                        .build();

                return (T) KeyManagementServiceClient.create(keyManagementServiceSettings);
            } catch (IOException ex) {
                throw new GyroException(
                    String.format("Unable to create %s client", clientClass.getSimpleName()));
            }
        } else {
            throw new GyroException(
                String.format("Unable to create %s client", clientClass.getSimpleName()));
        }
    }
}
