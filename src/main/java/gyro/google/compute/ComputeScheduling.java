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
import java.util.stream.Collectors;

import com.google.cloud.compute.v1.Scheduling;
import gyro.core.resource.Diffable;
import gyro.google.Copyable;

public class ComputeScheduling extends Diffable implements Copyable<Scheduling> {

    private Boolean automaticRestart;

    private List<ComputeSchedulingNodeAffinity> nodeAffinity;

    private String onHostMaintenance;

    private Boolean preemptible;

    /**
     * Specifies whether the instance should be automatically restarted if it is terminated by Compute Engine (not terminated by a user).
     * You can only set the automatic restart option for standard instances. Preemptible instances cannot be automatically restarted.
     * By default, this is set to ``true`` so an instance is automatically restarted if it is terminated by Compute Engine.
     */
    public Boolean getAutomaticRestart() {
        return automaticRestart;
    }

    public void setAutomaticRestart(Boolean automaticRestart) {
        this.automaticRestart = automaticRestart;
    }

    /**
     * List of node affinity and anti-affinity configurations.
     *
     * @subresource gyro.google.compute.ComputeSchedulingNodeAffinity
     */
    public List<ComputeSchedulingNodeAffinity> getNodeAffinity() {
        if (nodeAffinity == null) {
            nodeAffinity = new ArrayList<>();
        }
        return nodeAffinity;
    }

    public void setNodeAffinity(List<ComputeSchedulingNodeAffinity> nodeAffinity) {
        this.nodeAffinity = nodeAffinity;
    }

    /**
     * Defines the maintenance behavior for this instance.
     * For standard instances, the default behavior is ``MIGRATE``.
     * For preemptible instances, the default and only possible behavior is ``TERMINATE``.
     */
    public String getOnHostMaintenance() {
        return onHostMaintenance;
    }

    public void setOnHostMaintenance(String onHostMaintenance) {
        this.onHostMaintenance = onHostMaintenance;
    }

    /**
     * Defines whether the instance is preemptible.
     * This can only be set during instance creation, it cannot be set or changed after the instance has been created.
     */
    public Boolean getPreemptible() {
        return preemptible;
    }

    public void setPreemptible(Boolean preemptible) {
        this.preemptible = preemptible;
    }

    @Override
    public void copyFrom(Scheduling model) {
        if (model.hasAutomaticRestart()) {
            setAutomaticRestart(model.getAutomaticRestart());
        }

        if (model.hasOnHostMaintenance()) {
            setOnHostMaintenance(model.getOnHostMaintenance());
        }

        if (model.hasPreemptible()) {
            setPreemptible(model.getPreemptible());
        }

        setNodeAffinity(null);
        if (!model.getNodeAffinitiesList().isEmpty()) {
            List<ComputeSchedulingNodeAffinity> diffableNodeAffinities = model.getNodeAffinitiesList()
                .stream()
                .map(nodeAffinity -> {
                    ComputeSchedulingNodeAffinity diffableNodeAffinity =
                        newSubresource(ComputeSchedulingNodeAffinity.class);
                    diffableNodeAffinity.copyFrom(nodeAffinity);

                    return diffableNodeAffinity;
                })
                .collect(Collectors.toList());

            setNodeAffinity(diffableNodeAffinities);
        }
    }

    public Scheduling toScheduling() {
        Scheduling.Builder builder = Scheduling.newBuilder();

        if (getAutomaticRestart() != null) {
            builder.setAutomaticRestart(getAutomaticRestart());
        }

        if (getPreemptible() != null) {
            builder.setPreemptible(getPreemptible());
        }

        List<ComputeSchedulingNodeAffinity> nodeAffinity = getNodeAffinity();

        if (!nodeAffinity.isEmpty()) {
            builder.addAllNodeAffinities(nodeAffinity.stream()
                .map(ComputeSchedulingNodeAffinity::toSchedulingNodeAffinity)
                .collect(Collectors.toList()));
        }

        if (getOnHostMaintenance() != null) {
            builder.setOnHostMaintenance(getOnHostMaintenance());
        }

        return builder.build();
    }

    @Override
    public String primaryKey() {
        return "";
    }
}
