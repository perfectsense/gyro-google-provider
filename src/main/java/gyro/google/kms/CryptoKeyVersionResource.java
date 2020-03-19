package gyro.google.kms;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;

import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.cloud.kms.v1.CryptoKeyName;
import com.google.cloud.kms.v1.CryptoKeyVersion;
import com.google.cloud.kms.v1.KeyManagementServiceClient;
import com.google.cloud.kms.v1.KeyManagementServiceSettings;
import com.google.protobuf.FieldMask;
import gyro.core.GyroException;
import gyro.core.GyroInputStream;
import gyro.core.GyroUI;
import gyro.core.Type;
import gyro.core.resource.Id;
import gyro.core.resource.Resource;
import gyro.core.resource.Updatable;
import gyro.core.scope.State;
import gyro.google.Copyable;
import gyro.google.GoogleCredentials;
import gyro.google.GoogleResource;

@Type("crypto-key-version")
public class CryptoKeyVersionResource extends GoogleResource implements Copyable<CryptoKeyVersion> {

    private CryptoKeyResource cryptoKey;
    private CryptoKeyVersion.CryptoKeyVersionState state;

    // Read-Only
    private String id;

    public CryptoKeyResource getCryptoKey() {
        return cryptoKey;
    }

    public void setCryptoKey(CryptoKeyResource cryptoKey) {
        this.cryptoKey = cryptoKey;
    }

    @Updatable
    public CryptoKeyVersion.CryptoKeyVersionState getState() {
        if (state == null) {
            state = CryptoKeyVersion.CryptoKeyVersionState.ENABLED;
        }
        return state;
    }

    public void setState(CryptoKeyVersion.CryptoKeyVersionState state) {
        this.state = state;
    }

    @Id
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public void copyFrom(CryptoKeyVersion model) throws Exception {
        setId(model.getName());
        setCryptoKey(findById(CryptoKeyResource.class, String.join("/", Arrays.copyOfRange(getId().split("/"), 0, 8))));
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

        CryptoKeyVersion cryptoKeyVersion = client.getCryptoKeyVersion(getId());

        if (cryptoKeyVersion == null) {
            return false;
        }

        copyFrom(cryptoKeyVersion);

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

        String parent = CryptoKeyName.format(
            getProjectId(),
            getCryptoKey().getLocationFromId(),
            getCryptoKey().getKeyRingNameFromId(),
            getCryptoKey().getNameFromId());

        CryptoKeyVersion response = client.createCryptoKeyVersion(
            parent,
            CryptoKeyVersion.newBuilder().setState(getState()).build());

        setId(response.getName());
    }

    @Override
    protected void doUpdate(
        GyroUI ui, State state, Resource current, Set<String> changedFieldNames) throws Exception {

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

        client.updateCryptoKeyVersion(
            CryptoKeyVersion.newBuilder().setName(getId()).setState(getState()).build(),
            FieldMask.newBuilder().addPaths("state").build());

    }

    @Override
    protected void doDelete(GyroUI ui, State state) throws Exception {
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

        client.destroyCryptoKeyVersion(getId());
    }
}
