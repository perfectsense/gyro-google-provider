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

import com.google.api.services.compute.model.ResourcePolicyWeeklyCycle;
import gyro.core.resource.Diffable;
import gyro.google.Copyable;

public class WeeklyCycle extends Diffable implements Copyable<ResourcePolicyWeeklyCycle> {

    private List<WeeklyCycleDayOfWeek> dayOfWeeks;

    /**
     * Up to 7 intervals/windows, one for each day of the week.
     */
    public List<WeeklyCycleDayOfWeek> getDayOfWeeks() {
        if (dayOfWeeks == null) {
            return new ArrayList<>();
        }
        return dayOfWeeks;
    }

    public void setDayOfWeeks(List<WeeklyCycleDayOfWeek> dayOfWeeks) {
        this.dayOfWeeks = dayOfWeeks;
    }

    @Override
    public String primaryKey() {
        return "";
    }

    @Override
    public void copyFrom(ResourcePolicyWeeklyCycle model) {
        getDayOfWeeks().clear();
        if (model.getDayOfWeeks() != null) {
            setDayOfWeeks(model.getDayOfWeeks().stream().map(item -> {
                WeeklyCycleDayOfWeek currentDayOfWeek = newSubresource(WeeklyCycleDayOfWeek.class);
                currentDayOfWeek.copyFrom(item);
                return currentDayOfWeek;
            }).collect(Collectors.toList()));
        }
    }

    public ResourcePolicyWeeklyCycle copyTo() {
        return new ResourcePolicyWeeklyCycle()
            .setDayOfWeeks(getDayOfWeeks().stream().map(WeeklyCycleDayOfWeek::copyTo).collect(Collectors.toList()));
    }
}
