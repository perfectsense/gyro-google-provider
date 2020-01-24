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

import java.util.Optional;

import com.google.api.services.compute.model.InstanceGroupManagerVersion;
import gyro.core.resource.Diffable;
import gyro.core.validation.Required;
import gyro.google.Copyable;

public class ComputeInstanceGroupManagerVersion extends Diffable implements Copyable<InstanceGroupManagerVersion> {

    private InstanceTemplateResource instanceTemplate;

    private String name;

    private ComputeFixedOrPercent targetSize;

    /**
     * The instance template that is specified for this managed instance group. The group uses this template to create new instances in the managed instance group until the `targetSize` for this version is reached.
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
     * Name of the version. Unique among all versions in the scope of this managed instance group.
     */
    @Required
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * Specifies the intended number of instances to be created from the instanceTemplate. The final number of instances created from the template will be equal to:  - If expressed as a fixed number, the minimum of either targetSize.fixed or instanceGroupManager.targetSize is used. - if expressed as a percent, the targetSize would be (targetSize.percent/100 * InstanceGroupManager.targetSize) If there is a remainder, the number is rounded up.  If unset, this version will update any remaining instances not updated by another version. Read Starting a canary update for more information.
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
        InstanceGroupManagerVersion instanceGroupManagerVersion = new InstanceGroupManagerVersion();
        Optional.ofNullable(getInstanceTemplate())
            .map(InstanceTemplateResource::getSelfLink)
            .ifPresent(instanceGroupManagerVersion::setInstanceTemplate);
        instanceGroupManagerVersion.setName(getName());
        Optional.ofNullable(getTargetSize())
            .map(ComputeFixedOrPercent::copyTo)
            .ifPresent(instanceGroupManagerVersion::setTargetSize);
        return instanceGroupManagerVersion;
    }

    @Override
    public void copyFrom(InstanceGroupManagerVersion model) {
        setInstanceTemplate(Optional.ofNullable(model.getInstanceTemplate())
            .map(e -> findById(InstanceTemplateResource.class, e))
            .orElse(null));
        setName(model.getName());
        setTargetSize(Optional.ofNullable(model.getTargetSize())
            .map(e -> {
                ComputeFixedOrPercent computeFixedOrPercent = newSubresource(ComputeFixedOrPercent.class);
                computeFixedOrPercent.copyFrom(e);
                return computeFixedOrPercent;
            }).orElse(null));
    }

    @Override
    public String primaryKey() {
        return getName();
    }
}
