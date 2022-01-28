/*
 * Copyright 2020, Perfect Sense, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package gyro.google.kms;

import java.util.Set;

import com.google.api.gax.rpc.NotFoundException;
import com.google.api.gax.rpc.StatusCode;
import com.google.cloud.kms.v1.KeyManagementServiceClient;
import com.google.cloud.kms.v1.KeyRing;
import com.google.cloud.kms.v1.LocationName;
import gyro.core.GyroException;
import gyro.core.GyroUI;
import gyro.core.Type;
import gyro.core.resource.Id;
import gyro.core.resource.Output;
import gyro.core.resource.Resource;
import gyro.core.scope.State;
import gyro.core.validation.Regex;
import gyro.core.validation.Required;
import gyro.google.Copyable;
import gyro.google.GoogleResource;
import gyro.google.util.Utils;

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
     * The location of the key ring.
     */
    @Required
    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    /**
     * The name of the key ring.
     */
    @Required
    @Regex(value = "^(\\w|-)+$", message = "a string containing letters, numbers, underscores or hyphens")
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
        setName(Utils.getKmsKeyRingNameFromId(getId()));
        setLocation(Utils.getLocationFromId(getId()));
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

        try {
            client.listKeyRings(LocationName.format(getProjectId(), getLocation()));

        } catch (NotFoundException ex) {
            if (ex.getStatusCode().getCode().equals(StatusCode.Code.NOT_FOUND)) {
                throw new GyroException(String.format(
                    "Invalid value: %s, for 'location'", getLocation()));
            }
        }

        String location = LocationName.format(getProjectId(), getLocation());
        KeyRing response = client.createKeyRing(location, getName(), KeyRing.newBuilder().build());

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
}
