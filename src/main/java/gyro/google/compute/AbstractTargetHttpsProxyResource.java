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

import com.google.api.services.compute.model.TargetHttpsProxy;
import gyro.core.resource.Id;
import gyro.core.resource.Output;
import gyro.core.validation.Regex;
import gyro.google.Copyable;

public abstract class AbstractTargetHttpsProxyResource extends ComputeResource implements Copyable<TargetHttpsProxy> {

    private String description;
    private String name;

    // Read-only
    private String selfLink;

    /**
     * An optional description of this target proxy.
     */
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * The name of the target proxy. Must be 1-63 characters long, and the first character must be a lowercase letter. All other characters must be a lowercase letter, digit, or ``-``, except the last character, which cannot be a ``-``. (Required)
     */
    @Regex("[a-z](?:[-a-z0-9]{0,61}[a-z0-9])?")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * Server-defined URL for the target proxy.
     */
    @Id
    @Output
    public String getSelfLink() {
        return selfLink;
    }

    public void setSelfLink(String selfLink) {
        this.selfLink = selfLink;
    }

    @Override
    public void copyFrom(TargetHttpsProxy model) {
        setDescription(model.getDescription());
        setName(model.getName());
        setSelfLink(model.getSelfLink());
    }

    TargetHttpsProxy toTargetHttpsProxy() {
        TargetHttpsProxy targetHttpsProxy = new TargetHttpsProxy();
        targetHttpsProxy.setDescription(getDescription());
        targetHttpsProxy.setName(getName());

        return targetHttpsProxy;
    }
}
