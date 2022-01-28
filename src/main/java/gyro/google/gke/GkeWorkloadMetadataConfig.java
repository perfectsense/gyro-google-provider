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

import com.google.container.v1beta1.WorkloadMetadataConfig;
import gyro.core.resource.Diffable;
import gyro.core.resource.Updatable;
import gyro.core.validation.Required;
import gyro.core.validation.ValidStrings;
import gyro.google.Copyable;

public class GkeWorkloadMetadataConfig extends Diffable implements Copyable<WorkloadMetadataConfig> {

    private WorkloadMetadataConfig.Mode mode;

    /**
     * The mode is the configuration for how to expose metadata to workloads running on the node pool.
     */
    @Required
    @Updatable
    @ValidStrings({ "GCE_METADATA", "GKE_METADATA" })
    public WorkloadMetadataConfig.Mode getMode() {
        return mode;
    }

    public void setMode(WorkloadMetadataConfig.Mode mode) {
        this.mode = mode;
    }

    @Override
    public String primaryKey() {
        return "";
    }

    @Override
    public void copyFrom(WorkloadMetadataConfig model) {
        setMode(model.getMode());
    }

    WorkloadMetadataConfig toWorkloadMetadataConfig() {
        return WorkloadMetadataConfig.newBuilder().setMode(getMode()).build();
    }
}
