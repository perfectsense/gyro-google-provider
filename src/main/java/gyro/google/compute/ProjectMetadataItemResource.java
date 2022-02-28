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
import java.util.stream.Collectors;

import com.google.api.gax.rpc.NotFoundException;
import com.google.cloud.compute.v1.Items;
import com.google.cloud.compute.v1.Metadata;
import com.google.cloud.compute.v1.Operation;
import com.google.cloud.compute.v1.ProjectsClient;
import com.google.cloud.compute.v1.SetCommonInstanceMetadataProjectRequest;
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
public class ProjectMetadataItemResource extends ComputeResource implements Copyable<Items> {

    private String key;
    private String value;

    /**
     * The key of the metadata item. Allowed characters include letters, digits, ``-``, and ``_``.
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
    public void copyFrom(Items metadataItem) {
        if (metadataItem.hasKey()) {
            setKey(metadataItem.getKey());
        }

        if (metadataItem.hasValue()) {
            setValue(metadataItem.getValue());
        }
    }

    @Override
    public boolean doRefresh() throws Exception {
        try (ProjectsClient client = createClient(ProjectsClient.class)) {
            Items item = getItems(client);

            if (item == null) {
                return false;
            }

            copyFrom(item);

            return true;
        }
    }

    @Override
    public void doCreate(GyroUI ui, State state) throws Exception {
        try (ProjectsClient client = createClient(ProjectsClient.class)) {
            Items.Builder builder = Items.newBuilder();
            builder.setKey(getKey());
            builder.setValue(getValue());

            Metadata.Builder metadata = getMetadata(client).toBuilder();
            metadata.addItems(builder.build());

            setMetadata(client, metadata.build());
        }
    }

    @Override
    public void doUpdate(GyroUI ui, State state, Resource current, Set<String> changedFieldNames) throws Exception {
        try (ProjectsClient client = createClient(ProjectsClient.class)) {
            Metadata.Builder metadataBuilder = getMetadata(client).toBuilder();
            List<Items> itemsList = new ArrayList<>(metadataBuilder.getItemsList());
            itemsList.removeIf(i -> i.getKey().equals(getKey()));
            itemsList.add(Items.newBuilder().setKey(getKey()).setValue(getValue()).build());

            setMetadata(client, metadataBuilder.clearItems().addAllItems(itemsList).build());
        }
    }

    @Override
    public void doDelete(GyroUI ui, State state) throws Exception {
        try (ProjectsClient client = createClient(ProjectsClient.class)) {
            Metadata metadata = getMetadata(client);
            metadata = metadata.toBuilder()
                .clearItems()
                .addAllItems(metadata.getItemsList().stream()
                .filter(r -> !getKey().equals(r.getKey()))
                .collect(Collectors.toList()))
                .build();

            setMetadata(client, metadata);
        }
    }

    private Metadata getMetadata(ProjectsClient client) {
        return client.get(getProjectId()).getCommonInstanceMetadata();
    }

    private void setMetadata(ProjectsClient client, Metadata metadata) {
        Operation operation = client.setCommonInstanceMetadataCallable().call(SetCommonInstanceMetadataProjectRequest.newBuilder()
            .setProject(getProjectId())
            .setMetadataResource(metadata)
            .build());

        waitForCompletion(operation);
    }

    private Items getItems(ProjectsClient client) {
        Items items = null;

        try {
            Metadata metadata = getMetadata(client);
            items = metadata.getItemsList().stream().filter(r -> getKey().equals(r.getKey()))
                .findFirst().orElse(null);

        } catch (NotFoundException ex) {
            // ignore
        }

        return items;
    }
}
