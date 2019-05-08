package gyro.google;

import gyro.core.resource.ResourceType;
import gyro.core.Credentials;
import com.google.common.collect.ImmutableMap;

import java.util.Map;

@ResourceType("credentials")
public class GoogleCredentials extends Credentials {

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
    public Map<String, String> findCredentials(boolean refresh) {
        return findCredentials(refresh, true);
    }

    @Override
    public Map<String, String> findCredentials(boolean refresh, boolean extended) {
        ImmutableMap.Builder<String, String> mapBuilder = new ImmutableMap.Builder<>();

        return mapBuilder.build();
    }

}
