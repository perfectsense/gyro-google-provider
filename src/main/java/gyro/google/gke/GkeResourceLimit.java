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

package gyro.google.gke;

import com.google.container.v1.ResourceLimit;
import gyro.core.resource.Diffable;
import gyro.core.resource.Updatable;
import gyro.core.validation.Required;
import gyro.google.Copyable;

public class GkeResourceLimit extends Diffable implements Copyable<ResourceLimit> {

    private String resourceType;
    private Long minimum;
    private Long maximum;

    /**
     * The resource name.
     */
    @Required
    public String getResourceType() {
        return resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    /**
     * The minimum amount of the resource in the cluster.
     */
    @Updatable
    public Long getMinimum() {
        return minimum;
    }

    public void setMinimum(Long minimum) {
        this.minimum = minimum;
    }

    /**
     * The maximum amount of the resource in the cluster.
     */
    @Updatable
    public Long getMaximum() {
        return maximum;
    }

    public void setMaximum(Long maximum) {
        this.maximum = maximum;
    }

    @Override
    public String primaryKey() {
        return getResourceType();
    }

    @Override
    public void copyFrom(ResourceLimit model) {
        setResourceType(model.getResourceType());
        setMinimum(model.getMinimum());
        setMaximum(model.getMaximum());
    }

    ResourceLimit toResourceLimit() {
        return ResourceLimit.newBuilder().setResourceType(getResourceType())
            .setMinimum(getMinimum()).setMaximum(getMaximum()).build();
    }
}
