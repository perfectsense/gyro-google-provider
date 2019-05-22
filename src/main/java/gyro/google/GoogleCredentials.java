package gyro.google;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.services.json.AbstractGoogleJsonClient;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.compute.Compute;
import gyro.core.GyroException;
import gyro.core.auth.Credentials;

import java.io.IOException;
import java.io.InputStream;
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
        if (clientClass.getSimpleName().equals("Compute")) {
            try {
                HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
                JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();

                try (InputStream input = openInput(getCredentialFilePath())) {
                    GoogleCredential googleCredential = GoogleCredential.fromStream(input)
                        .createScoped(Collections.singleton("https://www.googleapis.com/auth/cloud-platform"));

                    return (T) new Compute.Builder(httpTransport, jsonFactory, googleCredential)
                        .setApplicationName("gyro-google-provider")
                        .build();
                }

            } catch (GeneralSecurityException | IOException e) {
                throw new GyroException("Unable to create Compute client");
            }
        }

        return null;
    }

}
