/*
 * Copyright 2024, Brightspot.
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
package gyro.google.cloudsql;

import com.google.api.services.sqladmin.model.AdvancedMachineFeatures;
import gyro.core.resource.Diffable;
import gyro.core.resource.Updatable;
import gyro.core.validation.Required;
import gyro.google.Copyable;

public class DbAdvancedMachineFeatures extends Diffable implements Copyable<AdvancedMachineFeatures> {

    private Integer threadsPerCore;

    /**
     * The number of threads per physical core.
     */
    @Required
    @Updatable
    public Integer getThreadsPerCore() {
        return threadsPerCore;
    }

    public void setThreadsPerCore(Integer threadsPerCore) {
        this.threadsPerCore = threadsPerCore;
    }

    @Override
    public void copyFrom(AdvancedMachineFeatures model) throws Exception {
        setThreadsPerCore(model.getThreadsPerCore());
    }

    @Override
    public String primaryKey() {
        return "";
    }

    public AdvancedMachineFeatures toAdvancedMachineFeatures() {
        return new AdvancedMachineFeatures().setThreadsPerCore(getThreadsPerCore());
    }
}
