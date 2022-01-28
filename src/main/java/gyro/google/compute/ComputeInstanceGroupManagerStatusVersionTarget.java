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

import com.google.cloud.compute.v1.InstanceGroupManagerStatusVersionTarget;
import gyro.core.resource.Diffable;
import gyro.core.resource.Output;
import gyro.google.Copyable;

public class ComputeInstanceGroupManagerStatusVersionTarget extends Diffable
    implements Copyable<InstanceGroupManagerStatusVersionTarget> {

    private Boolean isReached;

    /**
     * A bit indicating whether version target has been reached in this managed instance group, i.e. all instances are in their target version.
     * Instances' target versions are specified by version field on Instance Group Manager.
     */
    @Output
    public Boolean getIsReached() {
        return isReached;
    }

    public void setIsReached(Boolean isReached) {
        this.isReached = isReached;
    }

    @Override
    public void copyFrom(InstanceGroupManagerStatusVersionTarget model) {
        setIsReached(model.getIsReached());
    }

    @Override
    public String primaryKey() {
        return "";
    }
}
