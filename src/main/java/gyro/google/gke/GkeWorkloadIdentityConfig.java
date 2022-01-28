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

import com.google.container.v1beta1.WorkloadIdentityConfig;
import gyro.core.resource.Diffable;
import gyro.core.resource.Updatable;
import gyro.core.validation.Required;
import gyro.google.Copyable;

public class GkeWorkloadIdentityConfig extends Diffable implements Copyable<WorkloadIdentityConfig> {

    private String workloadPool;

    /**
     * The workload pool to attach all Kubernetes service accounts to.
     */
    @Required
    @Updatable
    public String getWorkloadPool() {
        return workloadPool;
    }

    public void setWorkloadPool(String workloadPool) {
        this.workloadPool = workloadPool;
    }

    @Override
    public String primaryKey() {
        return "";
    }

    @Override
    public void copyFrom(WorkloadIdentityConfig model) throws Exception {
        setWorkloadPool(model.getWorkloadPool());
    }

    WorkloadIdentityConfig toWorkloadIdentityConfig() {
        return WorkloadIdentityConfig.newBuilder().setWorkloadPool(getWorkloadPool()).build();
    }
}
