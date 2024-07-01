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
import gyro.google.Copyable;

public class SqlScheduledMaintenance extends Diffable
    implements Copyable<com.google.api.services.sqladmin.model.SqlScheduledMaintenance> {

    private Boolean canDefer;

    private Boolean canReschedule;

    private String scheduleDeadlineTime;

    private String startTime;

    public Boolean getCanDefer() {
        return canDefer;
    }

    public void setCanDefer(Boolean canDefer) {
        this.canDefer = canDefer;
    }

    /**
     * If the scheduled maintenance can be rescheduled.
     */
    public Boolean getCanReschedule() {
        return canReschedule;
    }

    public void setCanReschedule(Boolean canReschedule) {
        this.canReschedule = canReschedule;
    }

    /**
     * Maintenance cannot be rescheduled to start beyond this deadline.
     */
    public String getScheduleDeadlineTime() {
        return scheduleDeadlineTime;
    }

    public void setScheduleDeadlineTime(String scheduleDeadlineTime) {
        this.scheduleDeadlineTime = scheduleDeadlineTime;
    }

    /**
     * The start time of any upcoming scheduled maintenance for this instance.
     */
    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    @Override
    public String primaryKey() {
        return "";
    }

    @Override
    public void copyFrom(com.google.api.services.sqladmin.model.SqlScheduledMaintenance model) {
        setCanDefer(model.getCanDefer());
        setCanReschedule(model.getCanReschedule());
        setScheduleDeadlineTime(model.getScheduleDeadlineTime());
        setStartTime(model.getStartTime());
    }

    com.google.api.services.sqladmin.model.SqlScheduledMaintenance toSqlScheduledMaintenance() {
        return new com.google.api.services.sqladmin.model.SqlScheduledMaintenance()
            .setCanDefer(getCanDefer())
            .setCanReschedule(getCanReschedule())
            .setScheduleDeadlineTime(getScheduleDeadlineTime())
            .setStartTime(getStartTime());
    }
}
