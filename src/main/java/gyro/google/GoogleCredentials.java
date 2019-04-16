package gyro.google;

import gyro.core.resource.ResourceName;
import gyro.core.Credentials;
import com.google.common.collect.ImmutableMap;

import java.util.Map;

@ResourceName("credentials")
public class GoogleCredentials extends Credentials {

    @Override
    public String getCloudName() {
        return "google";
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
