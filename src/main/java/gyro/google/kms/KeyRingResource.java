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
import gyro.core.validation.Required;
import gyro.google.Copyable;
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
        KeyManagementServiceClient client = createClient(KeyManagementServiceClient.class);

        KeyRing keyRing = client.getKeyRing(getId());

        if (keyRing == null) {
            return false;
        }

        copyFrom(keyRing);

        return true;
    }

    @Override
    protected void doCreate(GyroUI ui, State state) throws Exception {
        KeyManagementServiceClient client = createClient(KeyManagementServiceClient.class);

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
