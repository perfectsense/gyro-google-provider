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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.devtools.artifactregistry.v1beta2.ArtifactRegistryClient;
import com.google.devtools.artifactregistry.v1beta2.Repository;
import gyro.core.Type;
import gyro.google.GoogleFinder;
import gyro.google.util.Utils;

/**
 * Query for a repository.
 *
 * Examples
 * --------
 *
 * .. code-block:: gyro
 *
 *    repository: $(external-query google::repository { location: 'us', name: 'example-repo' })
 */
@Type("repository")
public class RepositoryFinder extends GoogleFinder<ArtifactRegistryClient, Repository, RepositoryResource> {

    private String location;
    private String name;

    /**
     * The region in which the repository is located.
     */
    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    /**
     * The name of the repository.
     */
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    protected List<Repository> findAllGoogle(ArtifactRegistryClient client) throws Exception {
        throw new UnsupportedOperationException("Finding `repositories` without filters is not supported!!");
    }

    @Override
    protected List<Repository> findGoogle(ArtifactRegistryClient client, Map<String, String> filters) throws Exception {
        List<Repository> repositories = new ArrayList<>();

        if (filters.containsKey("location")) {
            repositories.addAll(client.listRepositories(String.format("projects/%s/locations/%s", getProjectId(),
                filters.get("location"))).getPage().getResponse().getRepositoriesList());
        }

        if (filters.containsKey("name")) {
            repositories.removeIf(r -> !Utils.getRepositoryNameFromId(r.getName()).equals(filters.get("name")));
        }

        client.close();

        return repositories;
    }
}
