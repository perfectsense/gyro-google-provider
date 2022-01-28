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
import java.util.Objects;
import java.util.stream.Collectors;

import com.google.cloud.compute.v1.Image;
import com.google.cloud.compute.v1.ImageList;
import com.google.cloud.compute.v1.ImagesClient;
import com.google.cloud.compute.v1.ListImagesRequest;
import com.psddev.dari.util.StringUtils;
import gyro.core.Type;
import gyro.google.GoogleFinder;

/**
 * Query image.
 *
 * Example
 * -------
 *
 * .. code-block:: gyro
 *
 *    compute-image: $(external-query google::compute-image { name: 'image-example' })
 *
 * .. code-block:: gyro
 *
 *    compute-image: $(external-query google::compute-image { family: 'image-family-example' })
 *
 * .. code-block:: gyro
 *
 *    compute-image: $(external-query google::compute-image { project: 'centos-cloud', family: 'centos-6' })
 */
@Type("compute-image")
public class ImageFinder extends GoogleFinder<ImagesClient, Image, ImageResource> {

    private String name;
    private String family;
    private String project;

    /**
     * The name of the image.
     */
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * The family of the image.
     */
    public String getFamily() {
        return family;
    }

    public void setFamily(String family) {
        this.family = family;
    }

    /**
     * The project where the image resides. Defaults to current project.
     */
    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    @Override
    protected List<Image> findAllGoogle(ImagesClient client) throws Exception {
        try {
            return listImages(client, getProjectId());
        } finally {
            client.close();
        }
    }

    @Override
    protected List<Image> findGoogle(ImagesClient client, Map<String, String> filters) throws Exception {
        List<Image> images;

        if (filters.containsKey("name")) {
            images = Collections.singletonList(client.get(
                filters.containsKey("project") ? filters.get("project") : getProjectId(), filters.get("name")));
        } else if (filters.containsKey("family")) {
            images = Collections.singletonList(client.getFromFamily(
                filters.containsKey("project") ? filters.get("project") : getProjectId(), filters.get("family")));
        } else {
            images = listImages(client, filters.get("project"));
        }

        return images;
    }

    private static List<Image> listImages(ImagesClient client, String projectId) throws Exception {
        List<Image> images = new ArrayList<>();
        ImageList imageList;
        String nextPageToken = null;

        do {
            ListImagesRequest.Builder builder = ListImagesRequest.newBuilder().setProject(projectId);

            if (nextPageToken != null) {
                builder.setPageToken(nextPageToken);
            }

            ImagesClient.ListPagedResponse listPagedResponse = client.list(builder.build());
            imageList = listPagedResponse.getPage().getResponse();
            nextPageToken = listPagedResponse.getNextPageToken();

            if (imageList.getItemsList() != null) {
                images.addAll(imageList.getItemsList().stream()
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList()));
            }

        } while (!StringUtils.isEmpty(nextPageToken));

        return images;
    }
}
