package gyro.google;

import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.compute.v1.Network;
import com.google.cloud.compute.v1.NetworkClient;
import com.google.cloud.compute.v1.NetworkRoutingConfig;
import com.google.cloud.compute.v1.NetworkSettings;
import com.google.cloud.compute.v1.Operation;
import com.google.cloud.http.HttpTransportOptions;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collections;

public class TestResource {

    public static void main(String[] args) {
        try {

            String myEndpoint = NetworkSettings.getDefaultEndpoint();

            HttpTransportOptions.DefaultHttpTransportFactory a = new HttpTransportOptions.DefaultHttpTransportFactory();

            GoogleCredentials googleCredentials = GoogleCredentials.fromStream(
                new FileInputStream("/Users/dbhattacharyya/Downloads/gyro-sandbox-google-cred.json"),
                a
            ).createScoped(Collections.singleton("https://www.googleapis.com/auth/cloud-platform"));
            NetworkSettings networkSettings = NetworkSettings.newBuilder()
                .setCredentialsProvider(FixedCredentialsProvider.create(googleCredentials))
                .setTransportChannelProvider(
                    NetworkSettings.defaultHttpJsonTransportProviderBuilder()
                        .setEndpoint(myEndpoint)
                        .build()
                )
                .build();

            NetworkClient networkClient = NetworkClient.create(networkSettings);

            Network network = Network.newBuilder()
                .setAutoCreateSubnetworks(false)
                .setName("vpc-example")
                .setDescription("vpc-example-desc")
                .setRoutingConfig(
                    NetworkRoutingConfig.newBuilder()
                        .setRoutingMode("REGIONAL")
                        .build()
                ).build();

            Operation operation = networkClient.insertNetwork("aerobic-lock-236714", network);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
