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

package gyro.google.sqladmin;

import gyro.core.resource.Diffable;
import gyro.core.validation.ValidStrings;
import gyro.google.Copyable;

public class MaintenanceWindow extends Diffable
    implements Copyable<com.google.api.services.sqladmin.model.MaintenanceWindow> {

    private Integer day;

    private Integer hour;

    private String updateTrack;

    /**
     * day of week (1-7), starting on Monday.
     */
    public Integer getDay() {
        return day;
    }

    public void setDay(Integer day) {
        this.day = day;
    }

    /**
     * hour of day - 0 to 23.
     */
    public Integer getHour() {
        return hour;
    }

    public void setHour(Integer hour) {
        this.hour = hour;
    }

    /**
     * Maintenance timing setting: `canary` (Earlier) or `stable` (Later). [Learn more](https://cloud.google.com/sql/docs/mysql/instance-settings#maintenance-timing-2ndgen).
     */
    @ValidStrings({
        "SQL_UPDATE_TRACK_UNSPECIFIED",
        "canary",
        "stable",
        "week5"
    })
    public String getUpdateTrack() {
        return updateTrack;
    }

    public void setUpdateTrack(String updateTrack) {
        this.updateTrack = updateTrack;
    }

    @Override
    public String primaryKey() {
        return "";
    }

    @Override
    public void copyFrom(com.google.api.services.sqladmin.model.MaintenanceWindow model) {
        setDay(model.getDay());
        setHour(model.getHour());
        setUpdateTrack(model.getUpdateTrack());
    }

    com.google.api.services.sqladmin.model.MaintenanceWindow copyTo() {
        com.google.api.services.sqladmin.model.MaintenanceWindow maintenanceWindow = new com.google.api.services.sqladmin.model.MaintenanceWindow();
        maintenanceWindow.setDay(getDay());
        maintenanceWindow.setHour(getHour());
        maintenanceWindow.setUpdateTrack(getUpdateTrack());

        return maintenanceWindow;
    }
}
