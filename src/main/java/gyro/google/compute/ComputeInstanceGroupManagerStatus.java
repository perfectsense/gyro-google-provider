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

import com.google.api.services.compute.model.InstanceGroupManagerStatus;
import gyro.core.resource.Diffable;
import gyro.core.resource.Output;
import gyro.google.Copyable;

public class ComputeInstanceGroupManagerStatus extends Diffable implements Copyable<InstanceGroupManagerStatus> {

    private Boolean isStable;

    // versionTarget is not available from Google SDK yet.
    //    private ComputeInstanceGroupManagerStatusVersionTarget versionTarget;

    /**
     * A bit indicating whether the managed instance group is in a stable state.
     * A stable state means that: none of the instances in the managed instance group is currently undergoing any type of change (for example, creation, restart, or deletion); no future changes are scheduled for instances in the managed instance group; and the managed instance group itself is not being modified.
     */
    @Output
    public Boolean getIsStable() {
        return isStable;
    }

    public void setIsStable(Boolean isStable) {
        this.isStable = isStable;
    }

    @Override
    public void copyFrom(InstanceGroupManagerStatus model) {
        setIsStable(model.getIsStable());
    }

    @Override
    public String primaryKey() {
        return "";
    }
}
