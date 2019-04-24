package gyro.google;

import com.google.api.gax.core.BackgroundResource;
import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.http.HttpTransportFactory;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.compute.v1.NetworkClient;
import com.google.cloud.compute.v1.NetworkSettings;
import com.google.cloud.compute.v1.SubnetworkClient;
import com.google.cloud.compute.v1.SubnetworkSettings;
import com.google.cloud.http.HttpTransportOptions;
import gyro.core.resource.Resource;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public abstract class GoogleResource extends Resource {
    private String projectId;
    private static Map<String, ? extends BackgroundResource> clientMap = new HashMap<>();

    @Override
    public Class resourceCredentialsClass() {
        return gyro.google.GoogleCredentials.class;
    }

    protected <T extends BackgroundResource> T creatClient(Class<T> clientClass) {
        String clientSettingName = clientClass.getSimpleName().replace("Client", "Settings");
        if (!clientMap.containsKey(clientSettingName)) {
            clientMap.put(clientSettingName, getClient(clientSettingName));
        }

        return (T) clientMap.get(clientSettingName);
    }

    private <T extends BackgroundResource> T getClient(String clientSettingName) {
        if (clientSettingName.equals("NetworkSettings")) {
            return (T) getClient(getCredentials(), NetworkSettings.newBuilder());
        } else if (clientSettingName.equals("SubnetworkSettings")) {
            return (T) getClient(getCredentials(), SubnetworkSettings.newBuilder());
        }

        return null;
    }

    private GoogleCredentials getCredentials() {
        try {
            gyro.google.GoogleCredentials credentials = (gyro.google.GoogleCredentials) resourceCredentials();

            return GoogleCredentials.fromStream(
                new FileInputStream(credentials.getCredentialFilePath()),
                new HttpTransportOptions.DefaultHttpTransportFactory()
            ).createScoped(Collections.singleton("https://www.googleapis.com/auth/cloud-platform"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private NetworkClient getClient(GoogleCredentials googleCredentials, NetworkSettings.Builder builder) {
        try {
            String myEndpoint = NetworkSettings.getDefaultEndpoint();

            NetworkSettings networkSettings = builder
                .setCredentialsProvider(FixedCredentialsProvider.create(googleCredentials))
                .setTransportChannelProvider(
                    NetworkSettings.defaultHttpJsonTransportProviderBuilder()
                        .setEndpoint(myEndpoint)
                        .build()
                )
                .build();

            return NetworkClient.create(networkSettings);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }



    private SubnetworkClient getClient(GoogleCredentials googleCredentials, SubnetworkSettings.Builder builder) {
        try {
            String myEndpoint = NetworkSettings.getDefaultEndpoint();

            SubnetworkSettings subnetworkSettings = builder
                .setCredentialsProvider(FixedCredentialsProvider.create(googleCredentials))
                .setTransportChannelProvider(
                    NetworkSettings.defaultHttpJsonTransportProviderBuilder()
                        .setEndpoint(myEndpoint)
                        .build()
                )
                .build();

            return SubnetworkClient.create(subnetworkSettings);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    protected String getProjectId() {
        if (projectId == null) {
            gyro.google.GoogleCredentials credentials = (gyro.google.GoogleCredentials) resourceCredentials();
            projectId = credentials.getProjectId();
        }

        return projectId;
    }

}
