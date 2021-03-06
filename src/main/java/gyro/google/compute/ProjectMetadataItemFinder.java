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
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.google.api.services.compute.Compute;
import com.google.api.services.compute.model.Metadata;
import com.google.api.services.compute.model.Project;
import gyro.core.Type;
import gyro.google.GoogleFinder;

/**
 * Query a project-wide metadata item.
 *
 * Example
 * -------
 *
 * .. code-block:: gyro
 *
 *    project-metadata-item: $(external-query google::compute-project-metadata-item { key: 'example-key'})
 */
@Type("compute-project-metadata-item")
public class ProjectMetadataItemFinder extends GoogleFinder<Compute, Metadata.Items, ProjectMetadataItemResource> {

    private String key;

    /**
     * The key of the metadata item.
     */
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    @Override
    protected List<Metadata.Items> findAllGoogle(Compute client) throws Exception {
        return Optional.ofNullable(client.projects()
            .get(getProjectId())
            .execute()
            .getCommonInstanceMetadata()
            .getItems()).orElse(new ArrayList<>());
    }

    @Override
    protected List<Metadata.Items> findGoogle(Compute client, Map<String, String> filters) throws Exception {
        Project project = client.projects()
            .get(getProjectId())
            .execute();

        Metadata.Items item = null;

        if (project.getCommonInstanceMetadata() != null && project.getCommonInstanceMetadata().getItems() != null) {
            item = project
                .getCommonInstanceMetadata()
                .getItems()
                .stream()
                .filter(r -> filters.get("key").equals(r.getKey()))
                .findFirst()
                .orElse(null);
        }

        if (item != null) {
            return Collections.singletonList(item);
        } else {
            return Collections.emptyList();
        }
    }
}
