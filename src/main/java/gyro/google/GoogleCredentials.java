package gyro.google;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import gyro.core.resource.ResourceName;
import gyro.core.Credentials;
import com.google.common.collect.ImmutableMap;
import gyro.core.scope.FileScope;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Map;

@ResourceName("credentials")
public class GoogleCredentials extends Credentials<GoogleCredential> {

    private String projectId;

    private String credentialFilePath;

    @Override
    public String getCloudName() {
        return "google";
    }

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

    @Override
    public GoogleCredential findCredentials(boolean refresh) {
        return findCredentials(refresh, true);
    }

    @Override
    public GoogleCredential findCredentials(boolean refresh, boolean extended) {
        try {
            return GoogleCredential.fromStream(getRelativeCredentialsPath())
                .createScoped(Collections.singleton("https://www.googleapis.com/auth/cloud-platform"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private InputStream getRelativeCredentialsPath() throws Exception {
        FileScope fileScope = scope().getFileScope();

        return fileScope.getRootScope().getBackend().openInput(Paths.get(fileScope.getFile()).getParent().resolve(getCredentialFilePath()).toString());
    }
}
