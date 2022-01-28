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

import com.google.cloud.compute.v1.ResourcePolicyHourlyCycle;
import gyro.core.resource.Diffable;
import gyro.core.validation.Range;
import gyro.core.validation.Required;
import gyro.google.Copyable;

public class HourlyCycle extends Diffable implements Copyable<ResourcePolicyHourlyCycle> {

    private Integer hoursInCycle;
    private String startTime;

    /**
     * Defines a schedule that runs every nth hour, [1-23].
     */
    @Range(min = 1, max = 23)
    @Required
    public Integer getHoursInCycle() {
        return hoursInCycle;
    }

    public void setHoursInCycle(Integer hoursInCycle) {
        this.hoursInCycle = hoursInCycle;
    }

    /**
     * Time within the window to start the operations. Must be in format "HH:MM", where HH equals [00-23] and MM equals [00-00] GMT.
     */
    @Required
    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    @Override
    public String primaryKey() {
        return String.format("running every %d hours starting at %s", getHoursInCycle(), getStartTime());
    }

    @Override
    public void copyFrom(ResourcePolicyHourlyCycle model) {
        setHoursInCycle(model.getHoursInCycle());
        setStartTime(model.getStartTime());
    }

    public ResourcePolicyHourlyCycle copyTo() {
        return ResourcePolicyHourlyCycle.newBuilder().setHoursInCycle(getHoursInCycle()).setStartTime(getStartTime())
            .build();
    }
}
