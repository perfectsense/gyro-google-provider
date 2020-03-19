package gyro.google.kms;

import java.util.Collections;
import java.util.Set;

import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.cloud.kms.v1.KeyManagementServiceClient;
import com.google.cloud.kms.v1.KeyManagementServiceSettings;
import com.google.cloud.kms.v1.KeyRing;
import com.google.cloud.kms.v1.LocationName;
import gyro.core.GyroException;
import gyro.core.GyroInputStream;
import gyro.core.GyroUI;
import gyro.core.Type;
import gyro.core.resource.Id;
import gyro.core.resource.Output;
import gyro.core.resource.Resource;
import gyro.core.scope.State;
import gyro.core.validation.Required;
import gyro.google.Copyable;
import gyro.google.GoogleCredentials;
import gyro.google.GoogleResource;

@Type("key-ring")
public class KeyRingResource extends GoogleResource implements Copyable<KeyRing> {

    private String location;
    private String name;

    // Read-only
    private String id;

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    @Required
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Output
    @Id
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public void copyFrom(KeyRing model) throws Exception {
        setId(model.getName());
        setName(getNameFromId());
        setLocation(getLocationFromId());
    }

    @Override
    protected boolean doRefresh() throws Exception {
        com.google.auth.oauth2.GoogleCredentials googleCredentials = null;

        try (GyroInputStream input = openInput(credentials(GoogleCredentials.class).getCredentialFilePath())) {
            googleCredentials = com.google.auth.oauth2.GoogleCredentials.fromStream(input)
                .createScoped(Collections.singleton("https://www.googleapis.com/auth/cloud-platform"));
        } catch (Exception ex) {
            throw new GyroException("Could not load credentials file.");
        }

        KeyManagementServiceSettings keyManagementServiceSettings =
            KeyManagementServiceSettings.newBuilder()
                .setCredentialsProvider(FixedCredentialsProvider.create(googleCredentials))
                .build();
        KeyManagementServiceClient client =
            KeyManagementServiceClient.create(keyManagementServiceSettings);

        KeyRing keyRing = client.getKeyRing(getId());

        if (keyRing == null) {
            return false;
        }

        copyFrom(keyRing);

        return true;
    }

    @Override
    protected void doCreate(GyroUI ui, State state) throws Exception {

        com.google.auth.oauth2.GoogleCredentials googleCredentials = null;

        try (GyroInputStream input = openInput(credentials(GoogleCredentials.class).getCredentialFilePath())) {
            googleCredentials = com.google.auth.oauth2.GoogleCredentials.fromStream(input)
                .createScoped(Collections.singleton("https://www.googleapis.com/auth/cloud-platform"));
        } catch (Exception ex) {
            throw new GyroException("Could not load credentials file.");
        }

        KeyManagementServiceSettings keyManagementServiceSettings =
            KeyManagementServiceSettings.newBuilder()
                .setCredentialsProvider(FixedCredentialsProvider.create(googleCredentials))
                .build();
        KeyManagementServiceClient client =
            KeyManagementServiceClient.create(keyManagementServiceSettings);

        String parent = LocationName.format(getProjectId(), getLocation());
        KeyRing response = client.createKeyRing(parent, getName(), KeyRing.newBuilder().build());

        copyFrom(response);
    }

    @Override
    protected void doUpdate(
        GyroUI ui, State state, Resource current, Set<String> changedFieldNames) throws Exception {

    }

    @Override
    protected void doDelete(GyroUI ui, State state) throws Exception {

    }

    public String getLocationFromId() {
        return getId().split("/")[3];
    }

    public String getNameFromId() {
        return getId().split("/")[5];
    }
}
