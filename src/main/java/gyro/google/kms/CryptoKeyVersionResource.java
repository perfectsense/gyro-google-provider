package gyro.google.kms;

import java.util.Arrays;
import java.util.Set;

import com.google.cloud.kms.v1.CryptoKeyName;
import com.google.cloud.kms.v1.CryptoKeyVersion;
import com.google.cloud.kms.v1.CryptoKeyVersion.CryptoKeyVersionState;
import com.google.cloud.kms.v1.KeyManagementServiceClient;
import com.google.protobuf.FieldMask;
import gyro.core.GyroUI;
import gyro.core.Type;
import gyro.core.resource.Id;
import gyro.core.resource.Output;
import gyro.core.resource.Resource;
import gyro.core.resource.Updatable;
import gyro.core.scope.State;
import gyro.core.validation.Required;
import gyro.google.Copyable;
import gyro.google.GoogleResource;
import gyro.google.util.Utils;

/**
 * Create a new version for the crypto key.
 *
 * Example
 * -------
 *
 * .. code-block:: gyro
 *
 *    google::crypto-key-version crypto-key-version-example
 *        crypto-key: $(google::crypto-key example-crypto-key)
 *        state: ENABLED
 *    end
 */
@Type("crypto-key-version")
public class CryptoKeyVersionResource extends GoogleResource implements Copyable<CryptoKeyVersion> {

    private CryptoKeyResource cryptoKey;
    private CryptoKeyVersionState state;

    // Read-only
    private String id;

    /**
     * The crypto key for which to create the new version. (Required)
     */
    @Required
    public CryptoKeyResource getCryptoKey() {
        return cryptoKey;
    }

    public void setCryptoKey(CryptoKeyResource cryptoKey) {
        this.cryptoKey = cryptoKey;
    }

    /**
     * The state of the crypto key version. Defaults to ``ENABLED``.
     */
    @Updatable
    public CryptoKeyVersionState getState() {
        if (state == null) {
            state = CryptoKeyVersionState.ENABLED;
        }
        return state;
    }

    public void setState(CryptoKeyVersionState state) {
        this.state = state;
    }

    /**
     * The ID of the crypto key version.
     */
    @Id
    @Output
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
        KeyManagementServiceClient client = createClient(KeyManagementServiceClient.class);

        CryptoKeyVersion cryptoKeyVersion = client.getCryptoKeyVersion(getId());

        if (cryptoKeyVersion == null) {
            return false;
        }

        copyFrom(cryptoKeyVersion);

        client.shutdownNow();

        return true;
    }

    @Override
    protected void doCreate(GyroUI ui, State state) throws Exception {
        KeyManagementServiceClient client = createClient(KeyManagementServiceClient.class);

        String parent = CryptoKeyName.format(
            getProjectId(),
            Utils.getKmsLocationFromId(getCryptoKey().getId()),
            Utils.getKmsKeyRingNameFromId(getCryptoKey().getId()),
            Utils.getKmsKeyNameFromId(getCryptoKey().getId()));

        CryptoKeyVersion response = client.createCryptoKeyVersion(
            parent,
            CryptoKeyVersion.newBuilder().setState(getState()).build());

        setId(response.getName());

        client.shutdownNow();
    }

    @Override
    protected void doUpdate(
        GyroUI ui, State state, Resource current, Set<String> changedFieldNames) throws Exception {
        KeyManagementServiceClient client = createClient(KeyManagementServiceClient.class);

        client.updateCryptoKeyVersion(
            CryptoKeyVersion.newBuilder().setName(getId()).setState(getState()).build(),
            FieldMask.newBuilder().addPaths("state").build());

        client.shutdownNow();
    }

    @Override
    protected void doDelete(GyroUI ui, State state) throws Exception {
        KeyManagementServiceClient client = createClient(KeyManagementServiceClient.class);

        client.destroyCryptoKeyVersion(getId());

        client.shutdownNow();
    }
}
