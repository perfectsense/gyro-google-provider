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
import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.google.api.gax.rpc.NotFoundException;
import com.google.cloud.compute.v1.DeleteInstanceTemplateRequest;
import com.google.cloud.compute.v1.GetInstanceTemplateRequest;
import com.google.cloud.compute.v1.InsertInstanceTemplateRequest;
import com.google.cloud.compute.v1.InstanceTemplate;
import com.google.cloud.compute.v1.InstanceTemplatesClient;
import gyro.core.GyroUI;
import gyro.core.Type;
import gyro.core.resource.Id;
import gyro.core.resource.Output;
import gyro.core.resource.Resource;
import gyro.core.scope.State;
import gyro.core.validation.ConflictsWith;
import gyro.core.validation.DependsOn;
import gyro.core.validation.Regex;
import gyro.core.validation.Required;
import gyro.core.validation.ValidationError;
import gyro.google.Copyable;

/**
 * Creates an instance template.
 *
 * Example
 * -------
 *
 * .. code-block:: gyro
 *
 *     google::compute-instance-template instance-template-example
 *         name: "instance-template-example"
 *         description: "Instance template example"
 *
 *         properties
 *             disk
 *                 auto-delete: true
 *                 boot: true
 *
 *                 initialize-params
 *                     disk-name: "instance-template-example-disk-1"
 *                     source-image: "projects/debian-cloud/global/images/family/debian-9"
 *                 end
 *             end
 *
 *             disk
 *                 auto-delete: true
 *                 boot: false
 *
 *                 initialize-params
 *                     disk-name: "instance-template-example-disk-2"
 *                     source-image: "projects/debian-cloud/global/images/family/debian-9"
 *                 end
 *             end
 *
 *             machine-type: "f1-micro"
 *
 *             network-interface
 *                 network: $(external-query google::compute-network {name: "default"})
 *
 *                 access-config
 *                     name: "External NAT"
 *                     type: "ONE_TO_ONE_NAT"
 *                     network-tier: "PREMIUM"
 *                 end
 *             end
 *
 *             scheduling
 *                 automatic-restart: true
 *                 on-host-maintenance: "MIGRATE"
 *                 preemptible: false
 *             end
 *
 *             metadata: {
 *                 test-key: "test-value"
 *             }
 *
 *             tags: ["test-tag"]
 *         end
 *     end
 */
@Type("compute-instance-template")
public class InstanceTemplateResource extends ComputeResource implements Copyable<InstanceTemplate> {

    private String description;

    private String name;

    private ComputeInstanceProperties properties;

    private String selfLink;

    private InstanceResource sourceInstance;

    private ComputeSourceInstanceParams sourceInstanceParams;

    /**
     * Description of this resource.
     */
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Name of the resource.
     */
    @Required
    @Regex(value = "[a-z](?:[-a-z0-9]{0,61}[a-z0-9])?", message = "a string 1-63 characters long and the first character must be a lowercase letter, and all following characters must be a dash, lowercase letter, or digit, except the last character, which cannot be a dash")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * The instance properties for this instance template.
     *
     * @subresource gyro.google.compute.ComputeInstanceProperties
     */
    @ConflictsWith("source-instance")
    public ComputeInstanceProperties getProperties() {
        return properties;
    }

    public void setProperties(ComputeInstanceProperties properties) {
        this.properties = properties;
    }

    /**
     * The URL for this instance template. The server defines this URL.
     */
    @Id
    @Output
    public String getSelfLink() {
        return selfLink;
    }

    public void setSelfLink(String selfLink) {
        this.selfLink = selfLink;
    }

    /**
     * The source instance used to create the template.
     *
     * @resource gyro.google.compute.InstanceResource
     */
    @ConflictsWith("properties")
    public InstanceResource getSourceInstance() {
        return sourceInstance;
    }

    public void setSourceInstance(InstanceResource sourceInstance) {
        this.sourceInstance = sourceInstance;
    }

    /**
     * The source instance params to use to create this instance template.
     *
     * @subresource gyro.google.compute.ComputeSourceInstanceParams
     */
    @DependsOn("source-instance")
    public ComputeSourceInstanceParams getSourceInstanceParams() {
        return sourceInstanceParams;
    }

    public void setSourceInstanceParams(ComputeSourceInstanceParams sourceInstanceParams) {
        this.sourceInstanceParams = sourceInstanceParams;
    }

    @Override
    protected boolean doRefresh() throws Exception {
        try (InstanceTemplatesClient client = createClient(InstanceTemplatesClient.class)) {
            InstanceTemplate instanceTemplate = getInstanceTemplate(client);

            if (instanceTemplate == null) {
                return false;
            }

            copyFrom(instanceTemplate, false);

            return true;
        }
    }

    @Override
    protected void doCreate(GyroUI ui, State state) throws Exception {
        InstanceTemplate.Builder builder = InstanceTemplate.newBuilder();
        builder.setName(getName());

        if (getDescription() != null) {
            builder.setDescription(getDescription());
        }

        if (getProperties() != null) {
            builder.setProperties(getProperties().toInstanceProperties());
        }

        if (getSourceInstance() != null) {
            builder.setSourceInstance(getSourceInstance().getSelfLink());
        }

        if (getSourceInstanceParams() != null) {
            builder.setSourceInstanceParams(getSourceInstanceParams().toSourceInstanceParams());
        }

        try (InstanceTemplatesClient client = createClient(InstanceTemplatesClient.class)) {
            waitForCompletion(client.insertCallable().call(InsertInstanceTemplateRequest.newBuilder()
                    .setProject(getProjectId())
                    .setInstanceTemplateResource(builder)
                .build()));
        }

        refresh();
    }

    @Override
    public void doUpdate(GyroUI ui, State state, Resource current, Set<String> changedFieldNames) throws Exception {
        // No update supported.
    }

    @Override
    public void doDelete(GyroUI ui, State state) throws Exception {
        try (InstanceTemplatesClient client = createClient(InstanceTemplatesClient.class)) {
            waitForCompletion(client.deleteCallable().call(DeleteInstanceTemplateRequest.newBuilder()
                    .setProject(getProjectId())
                    .setInstanceTemplate(getName())
                .build()));
        }
    }

    @Override
    public void copyFrom(InstanceTemplate model) {
        copyFrom(model, true);
    }

    public void copyFrom(InstanceTemplate model, boolean refreshProperties) {
        setName(model.getName());

        if (model.hasSelfLink()) {
            setSelfLink(model.getSelfLink());
        }

        if (model.hasDescription()) {
            setDescription(model.getDescription());
        }

        if (refreshProperties) {
            ComputeInstanceProperties diffableProperties = Optional.ofNullable(getProperties())
                .orElse(newSubresource(ComputeInstanceProperties.class));
            diffableProperties.copyFrom(model.getProperties());

            setProperties(diffableProperties);
        }

        if (model.hasSourceInstance()) {
            setSourceInstance(findById(InstanceResource.class, model.getSourceInstance()));
        }
    }

    @Override
    public List<ValidationError> validate(Set<String> configuredFields) {
        List<ValidationError> errors = new ArrayList<>();

        if ((!configuredFields.contains("properties") && !configuredFields.contains("source-instance"))
            || (getProperties() == null && getSourceInstance() == null)) {
            errors.add(new ValidationError(
                this,
                "properties",
                "Either 'properties' or 'source-instance' is required!"));
            errors.add(new ValidationError(
                this,
                "source-instance",
                "Either 'properties' or 'source-instance' is required!"));
        }
        return errors;
    }

    private InstanceTemplate getInstanceTemplate(InstanceTemplatesClient client) {
        InstanceTemplate instanceTemplate = null;

        try {
            instanceTemplate = client.get(GetInstanceTemplateRequest.newBuilder()
                .setProject(getProjectId())
                .setInstanceTemplate(getName())
                .build());

        } catch (NotFoundException ex) {
            // ignore
        }

        return instanceTemplate;
    }
}
