/*
 * Copyright 2021, Brightspot.
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

package gyro.google.artifactregistry;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import com.google.api.gax.rpc.InvalidArgumentException;
import com.google.api.gax.rpc.NotFoundException;
import com.google.devtools.artifactregistry.v1beta2.ArtifactRegistryClient;
import com.google.devtools.artifactregistry.v1beta2.CreateRepositoryRequest;
import com.google.devtools.artifactregistry.v1beta2.Repository;
import com.google.devtools.artifactregistry.v1beta2.UpdateRepositoryRequest;
import com.google.protobuf.FieldMask;
import com.psddev.dari.util.StringUtils;
import gyro.core.GyroUI;
import gyro.core.Type;
import gyro.core.resource.Id;
import gyro.core.resource.Output;
import gyro.core.resource.Resource;
import gyro.core.resource.Updatable;
import gyro.core.scope.State;
import gyro.core.validation.Required;
import gyro.core.validation.ValidStrings;
import gyro.google.Copyable;
import gyro.google.GoogleResource;
import gyro.google.kms.CryptoKeyResource;
import gyro.google.util.Utils;

/**
 * Add a repository.
 *
 * Example
 * -------
 *
 * .. code-block:: gyro
 *
 *    google::repository example
 *        name: "example-repo"
 *        location: "us-east1"
 *        description: "example-description"
 *        format: DOCKER
 *
 *        labels:{
 *            name: "example-repo"
 *        }
 *    end
 */
@Type("repository")
public class RepositoryResource extends GoogleResource implements Copyable<Repository> {

    private String name;
    private String location;
    private String description;
    private Repository.Format format;
    private CryptoKeyResource key;
    private Map<String, String> labels;

    // Read-only
    private String id;

    /**
     * The name of the repository.
     */
    @Required
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * The region where the repository should be located.
     */
    @Required
    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    /**
     * The user-provided description of the repository.
     */
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * The format of packages that are stored in the repository.
     */
    @Required
    @ValidStrings("DOCKER")
    public Repository.Format getFormat() {
        return format;
    }

    public void setFormat(Repository.Format format) {
        this.format = format;
    }

    /**
     * The KMS key that’s used to encrypt the contents of the repository.
     */
    public CryptoKeyResource getKey() {
        return key;
    }

    public void setKey(CryptoKeyResource key) {
        this.key = key;
    }

    /**
     * The set of labels for this repository.
     */
    @Updatable
    public Map<String, String> getLabels() {
        if (labels == null) {
            labels = new HashMap<>();
        }

        return labels;
    }

    public void setLabels(Map<String, String> labels) {
        this.labels = labels;
    }

    /**
     * The ID of the repository.
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
    public void copyFrom(Repository model) throws Exception {
        setId(model.getName());
        setDescription(model.getDescription());
        setFormat(model.getFormat());
        setKey(findById(CryptoKeyResource.class, model.getKmsKeyName()));
        setName(Utils.getRepositoryNameFromId(model.getName()));
        setLocation(Utils.getLocationFromId(model.getName()));
        setLabels(model.getLabelsMap());
    }

    @Override
    protected boolean doRefresh() throws Exception {
        ArtifactRegistryClient client = createClient(ArtifactRegistryClient.class);

        Repository repository = getRepository(client);

        if (repository == null) {
            return false;
        }

        copyFrom(repository);

        client.close();

        return true;
    }

    @Override
    protected void doCreate(GyroUI ui, State state) throws Exception {
        ArtifactRegistryClient client = createClient(ArtifactRegistryClient.class);

        Repository.Builder builder = Repository.newBuilder().setName(getName())
            .setFormat(Repository.Format.DOCKER).putAllLabels(getLabels());

        if (!StringUtils.isEmpty(getDescription())) {
            builder.setDescription(getDescription());
        }

        if (getKey() != null) {
            builder.setKmsKeyName(getKey().getId());
        }

        try {
            Repository repository = client.createRepositoryAsync(CreateRepositoryRequest.newBuilder()
                .setParent(getParent())
                .setRepositoryId(getName()).setRepository(builder.build()).build()).get();
            setId(repository.getName());

        } catch (ExecutionException ex) {
            if (ex.getMessage().contains("does not match the service location")) {
                client.awaitTermination(10, TimeUnit.SECONDS);
                Repository repository = client.listRepositories(getParent()).getPage()
                    .getResponse().getRepositoriesList().stream()
                    .filter(r -> Utils.getRepositoryNameFromId(r.getName()).equals(getName())).findFirst().orElse(null);
                if (repository == null) {
                    throw ex;
                } else {
                    setId(repository.getName());
                }
            } else {
                throw ex;
            }
        }

        client.close();
    }

    @Override
    protected void doUpdate(GyroUI ui, State state, Resource current, Set<String> changedFieldNames) throws Exception {
        ArtifactRegistryClient client = createClient(ArtifactRegistryClient.class);

        Repository.Builder builder = getRepository(client).toBuilder();
        builder.clearLabels();
        builder.putAllLabels(getLabels());

        client.updateRepository(UpdateRepositoryRequest.newBuilder()
            .setRepository(builder.build())
            .setUpdateMask(FieldMask.newBuilder().addPaths("labels").build())
            .build());

        client.close();
    }

    @Override
    protected void doDelete(GyroUI ui, State state) throws Exception {
        ArtifactRegistryClient client = createClient(ArtifactRegistryClient.class);

        client.deleteRepositoryAsync(getId()).getName();

        client.close();
    }

    private Repository getRepository(ArtifactRegistryClient client) {
        Repository repository = null;

        try {
            repository = client.getRepository(getId());
        } catch (NotFoundException | InvalidArgumentException ex) {
            // not found
        }

        return repository;
    }

    private String getParent() {
        return String.format("projects/%s/locations/%s", getProjectId(), getLocation());
    }
}
