package gyro.google.kms;

import java.util.Set;

import com.google.cloud.kms.v1.KeyManagementServiceClient;
import com.google.cloud.kms.v1.KeyRing;
import com.google.cloud.kms.v1.LocationName;
import gyro.core.GyroUI;
import gyro.core.Type;
import gyro.core.resource.Id;
import gyro.core.resource.Output;
import gyro.core.resource.Resource;
import gyro.core.scope.State;
import gyro.core.validation.Regex;
import gyro.core.validation.Required;
import gyro.core.validation.ValidStrings;
import gyro.google.Copyable;
import gyro.google.GoogleResource;

/**
 * Create a key ring.
 *
 * Example
 * -------
 *
 * .. code-block:: gyro
 *
 *    google::key-ring example-key-ring
 *        location: "global"
 *        name: "example-key-ring"
 *    end
 */
@Type("key-ring")
public class KeyRingResource extends GoogleResource implements Copyable<KeyRing> {

    private String location;
    private String name;

    // Read-only
    private String id;

    /**
     * The location of the key ring. The valid values are ``asia-east1`` or ``asia-east2`` or ``asia-northeast1`` or ``asia-northeast2`` or ``asia-northeast3`` or ``asia-south1`` or ``asia-southeast1`` or ``australia-southeast1`` or ``europe-north1`` or ``europe-west1`` or ``europe-west2`` or ``europe-west3`` or ``europe-west4`` or ``europe-west6`` or ``northamerica-northeast1`` or ``us-central1`` or ``us-east1`` or ``us-east4`` or ``us-west1`` or ``us-west2`` or ``us-west3`` or ``southamerica-east1`` or ``eur4`` or ``nam4`` or ``global`` or ``asia`` or ``europe`` or ``us``. (Required)
     */
    @Required
    @ValidStrings({
        "asia-east1",
        "asia-east2",
        "asia-northeast1",
        "asia-northeast2",
        "asia-northeast3",
        "asia-south1",
        "asia-southeast1",
        "australia-southeast1",
        "europe-north1",
        "europe-west1",
        "europe-west2",
        "europe-west3",
        "europe-west4",
        "europe-west6",
        "northamerica-northeast1",
        "us-central1",
        "us-east1",
        "us-east4",
        "us-west1",
        "us-west2",
        "us-west3",
        "southamerica-east1",
        "eur4",
        "nam4",
        "global",
        "asia",
        "europe",
        "us"
    })
    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    /**
     * The name of the key ring. Can be letters, numbers, underscores or hyphens. (Required)
     */
    @Required
    @Regex("^([a-z]|[0-9]|-|_)*$")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * The ID of the key ring.
     */
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
        KeyManagementServiceClient client = createClient(KeyManagementServiceClient.class);

        KeyRing keyRing = client.getKeyRing(getId());

        if (keyRing == null) {
            return false;
        }

        copyFrom(keyRing);

        client.shutdownNow();

        return true;
    }

    @Override
    protected void doCreate(GyroUI ui, State state) throws Exception {
        KeyManagementServiceClient client = createClient(KeyManagementServiceClient.class);

        String parent = LocationName.format(getProjectId(), getLocation());
        KeyRing response = client.createKeyRing(parent, getName(), KeyRing.newBuilder().build());

        copyFrom(response);

        client.shutdownNow();
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
