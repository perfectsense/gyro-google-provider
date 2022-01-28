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

import com.google.api.gax.rpc.InvalidArgumentException;
import com.google.api.gax.rpc.NotFoundException;
import com.google.cloud.compute.v1.InstanceTemplate;
import com.google.cloud.compute.v1.InstanceTemplateList;
import com.google.cloud.compute.v1.InstanceTemplatesClient;
import com.google.cloud.compute.v1.ListInstanceTemplatesRequest;
import gyro.core.Type;
import gyro.google.GoogleFinder;

/**
 * Query an instance template.
 *
 * Example
 * -------
 *
 * .. code-block:: gyro
 *
 *    instance-template: $(external-query google::compute-instance-template { name: 'instance-template-example' })
 */
@Type("compute-instance-template")
public class InstanceTemplateFinder
    extends GoogleFinder<InstanceTemplatesClient, InstanceTemplate, InstanceTemplateResource> {

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
    protected List<InstanceTemplate> findAllGoogle(InstanceTemplatesClient client) throws Exception {
        //        List<InstanceTemplate> allInstanceTemplates = new ArrayList<>();
        //        InstanceTemplatesClient.InstanceTemplates.List request = client.instanceTemplates().list(getProjectId());
        //        String nextPageToken = null;
        //
        //        do {
        //            InstanceTemplateList response = request.execute();
        //            List<InstanceTemplate> items = response.getItems();
        //
        //            if (items == null) {
        //                break;
        //            }
        //            allInstanceTemplates.addAll(items);
        //            nextPageToken = response.getNextPageToken();
        //            request.setPageToken(nextPageToken);
        //        } while (nextPageToken != null);
        //        return allInstanceTemplates;
        List<InstanceTemplate> instanceTemplates = new ArrayList<>();
        InstanceTemplateList instanceTemplateList;
        String nextPageToken = null;

        try {
            do {
                ListInstanceTemplatesRequest.Builder builder = ListInstanceTemplatesRequest.newBuilder()
                    .setProject(getProjectId());

                if (nextPageToken != null) {
                    builder.setPageToken(nextPageToken);
                }

                instanceTemplateList = client.list(builder.build()).getPage().getResponse();
                nextPageToken = instanceTemplateList.getNextPageToken();

                if (instanceTemplateList.getItemsList() != null) {
                    instanceTemplates.addAll(instanceTemplateList.getItemsList());
                }
            } while (nextPageToken != null);

            return instanceTemplates;

        } finally {
            client.close();
        }
    }

    @Override
    protected List<InstanceTemplate> findGoogle(InstanceTemplatesClient client, Map<String, String> filters)
        throws Exception {
        try {
            return Collections.singletonList(client.get(getProjectId(), filters.get("name")));
        } catch (InvalidArgumentException | NotFoundException ex) {
            return Collections.emptyList();
        } finally {
            client.close();
        }
    }
}
