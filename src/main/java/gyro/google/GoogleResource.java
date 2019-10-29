package gyro.google;

import com.google.api.client.googleapis.services.json.AbstractGoogleJsonClient;
import gyro.core.resource.Resource;

public abstract class GoogleResource extends Resource {

    protected static <T extends AbstractGoogleJsonClient> T creatClient(Class<T> clientClass, GoogleCredentials credentials) {
        return credentials.createClient(clientClass);
    }

    protected <T extends AbstractGoogleJsonClient> T creatClient(Class<T> clientClass) {
        return creatClient(clientClass, credentials(GoogleCredentials.class));
    }

    protected String getProjectId() {
        return credentials(GoogleCredentials.class).getProjectId();
    }

}
