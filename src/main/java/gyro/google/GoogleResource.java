package gyro.google;

import com.google.api.client.googleapis.services.json.AbstractGoogleJsonClient;
import gyro.core.resource.Resource;

public abstract class GoogleResource extends Resource {

    protected <T extends AbstractGoogleJsonClient> T creatClient(Class<T> clientClass) {
        return credentials(GoogleCredentials.class).createClient(clientClass);
    }

    protected String getProjectId() {
        return credentials(GoogleCredentials.class).getProjectId();
    }

}
