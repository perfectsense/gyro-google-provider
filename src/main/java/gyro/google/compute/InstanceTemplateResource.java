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

import com.google.api.services.compute.Compute;
import com.google.api.services.compute.model.InstanceProperties;
import com.google.api.services.compute.model.InstanceTemplate;
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
     * The name must be 1-63 characters long, and comply with RFC1035.
     * Specifically, the name must be 1-63 characters long and match the regular expression ``[a-z](?:[-a-z0-9]{0,61}[a-z0-9])?``
     * which means the first character must be a lowercase letter, and all following characters must be a dash,
     * lowercase letter, or digit, except the last character, which cannot be a dash.
     */
    @Required
    @Regex("[a-z](?:[-a-z0-9]{0,61}[a-z0-9])?")
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
        Compute client = createComputeClient();
        copyFrom(client.instanceTemplates().get(getProjectId(), getName()).execute());
        return true;
    }

    @Override
    protected void doCreate(GyroUI ui, State state) throws Exception {
        InstanceTemplate instanceTemplate = new InstanceTemplate();
        instanceTemplate.setDescription(getDescription());
        instanceTemplate.setName(getName());

        ComputeInstanceProperties property = getProperties();

        if (property != null) {
            instanceTemplate.setProperties(property.toInstanceProperties());
        }

        InstanceResource sourceInstance = getSourceInstance();

        if (sourceInstance != null) {
            instanceTemplate.setSourceInstance(sourceInstance.getSelfLink());
        }

        ComputeSourceInstanceParams sourceInstanceParam = getSourceInstanceParams();

        if (sourceInstanceParam != null) {
            instanceTemplate.setSourceInstanceParams(sourceInstanceParam.toSourceInstanceParams());
        }

        Compute client = createComputeClient();
        waitForCompletion(client, client.instanceTemplates().insert(getProjectId(), instanceTemplate).execute());
        refresh();
    }

    @Override
    public void doUpdate(GyroUI ui, State state, Resource current, Set<String> changedFieldNames) throws Exception {
        // No update supported.
    }

    @Override
    public void doDelete(GyroUI ui, State state) throws Exception {
        Compute client = createComputeClient();
        waitForCompletion(client, client.instanceTemplates().delete(getProjectId(), getName()).execute());
    }

    @Override
    public void copyFrom(InstanceTemplate model) {
        setDescription(model.getDescription());
        setName(model.getName());
        ComputeInstanceProperties diffableProperties = null;
        InstanceProperties properties = model.getProperties();

        if (properties != null) {
            diffableProperties = Optional.ofNullable(getProperties())
                .orElse(newSubresource(ComputeInstanceProperties.class));
            diffableProperties.copyFrom(properties);
        }
        setProperties(diffableProperties);
        setSelfLink(model.getSelfLink());
        String sourceInstance = model.getSourceInstance();

        if (sourceInstance != null) {
            // TODO: is it a full url?
            setSourceInstance(findById(InstanceResource.class, sourceInstance));
        }
        // Do NOT update `sourceInstanceParams` with the value from the server as it's always `null`.
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
}
