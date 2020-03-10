/*
 * Copyright 2019, Perfect Sense, Inc.
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

package gyro.google.compute;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.compute.Compute;
import com.google.api.services.compute.model.Metadata;
import com.google.api.services.compute.model.Operation;
import com.google.api.services.compute.model.Project;
import gyro.core.GyroException;
import gyro.core.GyroUI;
import gyro.core.Type;
import gyro.core.resource.Resource;
import gyro.core.resource.Updatable;
import gyro.core.scope.State;
import gyro.core.validation.Required;
import gyro.google.Copyable;

/**
 * Creates a project-wide metadata item. Set project-wide SSH keys by creating an item with the key ``ssh-keys``.
 *
 * Example
 * -------
 *
 * .. code-block:: gyro
 *
 *     google::compute-project-metadata-item project-metadata-item-example
 *         key: "example-key"
 *         value: "example-value"
 *     end
 */
@Type("compute-project-metadata-item")
public class ProjectMetadataItemResource extends ComputeResource implements Copyable<Metadata.Items> {

    private String key;
    private String value;

    /**
     * The key of the metadata item. Allowed characters include letters, digits, ``-``, and ``_``. (Required)
     */
    @Required
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    /**
     * The value of the metadata item.
     */
    @Updatable
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public void copyFrom(Metadata.Items metadataItem) {
        setKey(metadataItem.getKey());
        setValue(metadataItem.getValue());
    }

    @Override
    public boolean doRefresh() throws Exception {
        Compute client = createComputeClient();

        Metadata metadata = getMetadata(client);
        Metadata.Items item = metadata.getItems()
            .stream()
            .filter(r -> getKey().equals(r.getKey()))
            .findFirst()
            .orElse(null);

        if (item != null) {
            copyFrom(item);

            return true;
        }

        return false;
    }

    @Override
    public void doCreate(GyroUI ui, State state) throws Exception {
        Compute client = createComputeClient();

        Metadata.Items item = new Metadata.Items();
        item.setKey(getKey());
        item.setValue(getValue());

        Metadata metadata = getMetadata(client);
        List<Metadata.Items> items = metadata.getItems();

        if (items == null) {
            items = new ArrayList<>();
        }

        items.add(item);
        metadata.setItems(items);

        setMetadata(client, metadata);
    }

    @Override
    public void doUpdate(GyroUI ui, State state, Resource current, Set<String> changedFieldNames) throws Exception {
        Compute client = createComputeClient();

        Metadata metadata = getMetadata(client);
        Metadata.Items item = metadata.getItems()
            .stream()
            .filter(r -> getKey().equals(r.getKey()))
            .findFirst()
            .orElse(null);

        if (item != null) {
            item.setValue(getValue());
            setMetadata(client, metadata);
        }
    }

    @Override
    public void doDelete(GyroUI ui, State state) throws Exception {
        Compute client = createComputeClient();

        Metadata metadata = getMetadata(client);
        metadata.getItems().removeIf(r -> getKey().equals(r.getKey()));

        setMetadata(client, metadata);
    }

    private Metadata getMetadata(Compute client) throws Exception {
        Compute.Projects.Get projectRequest = client.projects().get(getProjectId());
        Project project = projectRequest.execute();

        return project.getCommonInstanceMetadata();
    }

    private void setMetadata(Compute client, Metadata metadata) throws Exception {
        try {
            Compute.Projects.SetCommonInstanceMetadata metadataRequest =
                client.projects().setCommonInstanceMetadata(getProjectId(), metadata);
            Operation operation = metadataRequest.execute();

            waitForCompletion(client, operation);
        } catch (GoogleJsonResponseException je) {
            String message = je.getDetails().getMessage();
            if (message.contains("Metadata has duplicate keys")) {
                throw new GyroException(String.format("Duplicate keys: %s", getKey()));
            }

            throw je;
        }
    }
}
