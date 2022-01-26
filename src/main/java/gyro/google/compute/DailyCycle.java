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

import com.google.cloud.compute.v1.ResourcePolicyDailyCycle;
import gyro.core.resource.Diffable;
import gyro.core.validation.Required;
import gyro.google.Copyable;

public class DailyCycle extends Diffable implements Copyable<ResourcePolicyDailyCycle> {

    private Integer daysInCycle;
    private String startTime;

    /**
     * Defines a schedule that runs every nth day of the month.
     */
    @Required
    public Integer getDaysInCycle() {
        return daysInCycle;
    }

    public void setDaysInCycle(Integer daysInCycle) {
        this.daysInCycle = daysInCycle;
    }

    /**
     *
     * Start time of the window. This must be in UTC format that resolves to one of 00:00, 04:00, 08:00, 12:00, 16:00, or 20:00. For example, both 13:00-5 and 08:00 are valid.
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
        return String.format("running on day %d of the month starting at %s", getDaysInCycle(), getStartTime());
    }

    @Override
    public void copyFrom(ResourcePolicyDailyCycle model) {
        setDaysInCycle(model.getDaysInCycle());
        setStartTime(model.getStartTime());
    }

    public ResourcePolicyDailyCycle copyTo() {
        return ResourcePolicyDailyCycle.newBuilder().setDaysInCycle(getDaysInCycle())
            .setStartTime(getStartTime()).build();
    }
}
