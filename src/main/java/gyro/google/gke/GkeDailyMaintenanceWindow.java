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

import com.google.container.v1.DailyMaintenanceWindow;
import gyro.core.resource.Diffable;
import gyro.core.resource.Output;
import gyro.core.validation.Regex;
import gyro.core.validation.Required;
import gyro.google.Copyable;

public class GkeDailyMaintenanceWindow extends Diffable implements Copyable<DailyMaintenanceWindow> {

    private String startTime;
    private String duration;

    @Required
    @Regex(value = "^([01]?[0-9]|2[0-3]):[0-5][0-9]$", message = "Time format should be in RFC3339 format ``HH:MM``, where ``HH : [00-23]`` and ``MM : [00-59]`` GMT.")
    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    @Output
    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    @Override
    public String primaryKey() {
        return "";
    }

    @Override
    public void copyFrom(DailyMaintenanceWindow model) throws Exception {
        setDuration(model.getDuration());
        setStartTime(model.getStartTime());
    }

    DailyMaintenanceWindow toDailyMaintenanceWindow() {
        return DailyMaintenanceWindow.newBuilder().setStartTime(getStartTime()).build();
    }
}
