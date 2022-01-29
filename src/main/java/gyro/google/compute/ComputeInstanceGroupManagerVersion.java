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

import com.google.cloud.compute.v1.InstanceGroupManagerVersion;
import gyro.core.resource.Diffable;
import gyro.core.validation.Required;
import gyro.google.Copyable;

public class ComputeInstanceGroupManagerVersion extends Diffable implements Copyable<InstanceGroupManagerVersion> {

    private InstanceTemplateResource instanceTemplate;

    private String name;

    private ComputeFixedOrPercent targetSize;

    /**
     * The instance template that is specified for this managed instance group.
     * The group uses this template to create new instances in the managed instance group until the ``target-size`` for this version is reached.
     *
     * @resource gyro.google.compute.InstanceTemplateResource
     */
    @Required
    public InstanceTemplateResource getInstanceTemplate() {
        return instanceTemplate;
    }

    public void setInstanceTemplate(InstanceTemplateResource instanceTemplate) {
        this.instanceTemplate = instanceTemplate;
    }

    /**
     * Name of the version.
     * Unique among all versions in the scope of this managed instance group.
     */
    @Required
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * Specifies the intended number of instances to be created from the instanceTemplate.
     * The final number of instances created from the template will be equal to:
     *
     * - If expressed as a fixed number, the minimum of either ``target-size.fixed`` or ``instance-group-manager.target-size`` is used.
     * - if expressed as a percent, the ``target-size`` would be (``target-size.percent``/100 * ``instance-group-manager.target-size``) If there is a remainder, the number is rounded up.
     *
     * If unset, this version will update any remaining instances not updated by another version.
     *
     * @subresource gyro.google.compute.ComputeFixedOrPercent
     */
    public ComputeFixedOrPercent getTargetSize() {
        return targetSize;
    }

    public void setTargetSize(ComputeFixedOrPercent targetSize) {
        this.targetSize = targetSize;
    }

    public InstanceGroupManagerVersion copyTo() {
        InstanceGroupManagerVersion.Builder builder = InstanceGroupManagerVersion.newBuilder().setName(getName());

        if (getInstanceTemplate() != null) {
            builder.setInstanceTemplate(getInstanceTemplate().getSelfLink());
        }

        if (getTargetSize() != null) {
            builder.setTargetSize(getTargetSize().copyTo());
        }

        return builder.build();
    }

    @Override
    public void copyFrom(InstanceGroupManagerVersion model) {
        setName(model.getName());

        if (model.hasInstanceTemplate()) {
            setInstanceTemplate(findById(InstanceTemplateResource.class, model.getInstanceTemplate()));
        }

        setTargetSize(null);
        if (model.hasTargetSize()) {
            ComputeFixedOrPercent computeFixedOrPercent = newSubresource(ComputeFixedOrPercent.class);
            computeFixedOrPercent.copyFrom(model.getTargetSize());

            setTargetSize(computeFixedOrPercent);
        }
    }

    @Override
    public String primaryKey() {
        return getName();
    }
}
