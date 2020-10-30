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

import com.google.api.services.compute.model.ResourcePolicyWeeklyCycleDayOfWeek;
import gyro.core.resource.Diffable;
import gyro.core.validation.Required;
import gyro.core.validation.ValidStrings;
import gyro.google.Copyable;

public class WeeklyCycleDayOfWeek extends Diffable implements Copyable<ResourcePolicyWeeklyCycleDayOfWeek> {

    private String day;
    private String startTime;

    /**
     * Define schedule that runs on a specified day of the week.
     */
    @Required
    @ValidStrings({"FRIDAY", "MONDAY", "SATURDAY", "SUNDAY", "THURSDAY", "TUESDAY", "WEDNESDAY"})
    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
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
        return String.format("running every %s starting at %s", getDay(), getStartTime());
    }

    @Override
    public void copyFrom(ResourcePolicyWeeklyCycleDayOfWeek model) {
        setDay(model.getDay());
        setStartTime(model.getStartTime());
    }

    public ResourcePolicyWeeklyCycleDayOfWeek copyTo() {
        return new ResourcePolicyWeeklyCycleDayOfWeek()
            .setDay(getDay())
            .setStartTime(getStartTime());
    }
}
