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

import com.google.cloud.compute.v1.InstanceGroupManagerStatus;
import gyro.core.resource.Diffable;
import gyro.core.resource.Output;
import gyro.google.Copyable;

public class ComputeInstanceGroupManagerStatus extends Diffable implements Copyable<InstanceGroupManagerStatus> {

    private Boolean isStable;

    private ComputeInstanceGroupManagerStatusVersionTarget versionTarget;

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

    /**
     * A status of consistency of Instances' versions with their target version specified by version field on Instance Group Manager.
     *
     * @subresource gyro.google.compute.ComputeInstanceGroupManagerStatusVersionTarget
     */
    @Output
    public ComputeInstanceGroupManagerStatusVersionTarget getVersionTarget() {
        return versionTarget;
    }

    public void setVersionTarget(ComputeInstanceGroupManagerStatusVersionTarget versionTarget) {
        this.versionTarget = versionTarget;
    }

    @Override
    public void copyFrom(InstanceGroupManagerStatus model) {
        setIsStable(model.getIsStable());
        setVersionTarget(
            Optional.ofNullable(model.getVersionTarget())
                .map(e -> {
                    ComputeInstanceGroupManagerStatusVersionTarget versionTarget = newSubresource(
                        ComputeInstanceGroupManagerStatusVersionTarget.class);
                    versionTarget.copyFrom(e);

                    return versionTarget;
                })
                .orElse(null));
    }

    @Override
    public String primaryKey() {
        return "";
    }
}
