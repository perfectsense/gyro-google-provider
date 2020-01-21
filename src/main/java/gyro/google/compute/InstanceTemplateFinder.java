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

package gyro.google.compute;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.google.api.services.compute.Compute;
import com.google.api.services.compute.model.InstanceTemplate;
import com.google.api.services.compute.model.InstanceTemplateList;
import gyro.core.Type;
import gyro.google.GoogleFinder;

/**
 * Query Instance Template.
 *
 * Example
 * -------
 *
 * .. code-block:: gyro
 *
 *    instance-template: $(external-query google::compute-instance-template { name: 'instance-template-example' })
 */
@Type("compute-instance-template")
public class InstanceTemplateFinder extends GoogleFinder<Compute, InstanceTemplate, InstanceTemplateResource> {

    private String name;

    /**
     * User assigned name for the instance template.
     */
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    protected List<InstanceTemplate> findAllGoogle(Compute client) throws Exception {
        List<InstanceTemplate> allInstanceTemplates = new ArrayList<>();
        Compute.InstanceTemplates.List request = client.instanceTemplates().list(getProjectId());
        String nextPageToken = null;

        do {
            InstanceTemplateList response = request.execute();
            allInstanceTemplates.addAll(response.getItems());
            nextPageToken = response.getNextPageToken();
            request.setPageToken(nextPageToken);
        } while (nextPageToken != null);
        return allInstanceTemplates;
    }

    @Override
    protected List<InstanceTemplate> findGoogle(Compute client, Map<String, String> filters) throws Exception {
        return Collections.singletonList(client.instanceTemplates().get(getProjectId(), filters.get("name")).execute());
    }
}
