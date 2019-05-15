package gyro.google;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.services.json.AbstractGoogleJsonClient;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.compute.Compute;
import gyro.core.GyroException;
import gyro.core.resource.Resource;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;

public abstract class GoogleResource extends Resource {
    private String projectId;

    protected <T extends AbstractGoogleJsonClient> T creatClient(Class<T> clientClass) {

        if (clientClass.getSimpleName().equals("Compute")) {
            return (T) getClient();
        }

        return null;
    }

    private GoogleCredential getCredentials() {
        try {
            gyro.google.GoogleCredentials credentials = (gyro.google.GoogleCredentials) resourceCredentials();

            return GoogleCredential.fromStream(
                new FileInputStream(credentials.getCredentialFilePath())
            ).createScoped(Collections.singleton("https://www.googleapis.com/auth/cloud-platform"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private Compute getClient() {
        try {
            gyro.google.GoogleCredentials credentials = (gyro.google.GoogleCredentials) resourceCredentials();

            HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();

            return new Compute.Builder(httpTransport, jsonFactory, credentials.findCredentials(true))
                .setApplicationName("gyro-google-provider")
                .build();
        } catch (GeneralSecurityException | IOException e) {
            throw new GyroException("Unable to create Compute client");
        }
    }

    protected String getProjectId() {
        if (projectId == null) {
            gyro.google.GoogleCredentials credentials = (gyro.google.GoogleCredentials) resourceCredentials();
            projectId = credentials.getProjectId();
        }

        return projectId;
    }

}
