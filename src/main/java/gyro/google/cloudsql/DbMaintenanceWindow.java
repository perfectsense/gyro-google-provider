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

import com.google.api.services.sqladmin.model.MaintenanceWindow;
import gyro.core.resource.Diffable;
import gyro.core.resource.Updatable;
import gyro.core.validation.Range;
import gyro.core.validation.ValidStrings;
import gyro.google.Copyable;

public class DbMaintenanceWindow extends Diffable implements Copyable<MaintenanceWindow> {

    private Integer day;
    private Integer hour;
    private String updateTrack;

    /**
     * The day of week in positive integers where 1 = Monday and 7 = Sunday.
     * In the UTC timezone
     */
    @Range(min = 1, max = 7)
    @Updatable
    public Integer getDay() {
        return day;
    }

    public void setDay(Integer day) {
        this.day = day;
    }

    /**
     * The hour of day from 0 to 23.
     */
    @Range(min = 0, max = 23)
    @Updatable
    public Integer getHour() {
        return hour;
    }

    public void setHour(Integer hour) {
        this.hour = hour;
    }

    /**
     * The maintenance timing settings.
     * See also `About maintenance on Cloud SQL instances <https://cloud.google.com/sql/docs/mysql/maintenance>`_.
     */
    @ValidStrings({ "canary", "stable", "week5" })
    @Updatable
    public String getUpdateTrack() {
        return updateTrack;
    }

    public void setUpdateTrack(String updateTrack) {
        this.updateTrack = updateTrack;
    }

    @Override
    public void copyFrom(MaintenanceWindow model) throws Exception {
        setDay(model.getDay());
        setHour(model.getHour());
        setUpdateTrack(model.getUpdateTrack());
    }

    @Override
    public String primaryKey() {
        return "";
    }

    public MaintenanceWindow toMaintenanceWindow() {
        MaintenanceWindow window = new MaintenanceWindow();

        if (getDay() != null) {
            window.setDay(getDay());
        }

        if (getHour() != null) {
            window.setHour(getHour());
        }

        if (getUpdateTrack() != null) {
            window.setUpdateTrack(getUpdateTrack());
        }

        return window;
    }
}
