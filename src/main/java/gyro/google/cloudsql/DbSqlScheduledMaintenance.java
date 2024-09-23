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

import com.google.api.services.sqladmin.model.SqlScheduledMaintenance;
import gyro.core.resource.Diffable;
import gyro.core.resource.Updatable;
import gyro.google.Copyable;

public class DbSqlScheduledMaintenance extends Diffable implements Copyable<SqlScheduledMaintenance> {

    private Boolean canReschedule;
    private String scheduleDeadlineTime;
    private String startTime;

    /**
     * When set to ``true``, the scheduled maintenance can be rescheduled.
     */
    @Updatable
    public Boolean getCanReschedule() {
        return canReschedule;
    }

    public void setCanReschedule(Boolean canReschedule) {
        this.canReschedule = canReschedule;
    }

    /**
     * The deadline after which maintenance cannot be rescheduled.
     */
    @Updatable
    public String getScheduleDeadlineTime() {
        return scheduleDeadlineTime;
    }

    public void setScheduleDeadlineTime(String scheduleDeadlineTime) {
        this.scheduleDeadlineTime = scheduleDeadlineTime;
    }

    /**
     * The start time of the scheduled maintenance in RFC 3339 UTC "Zulu" format, for e.g. "2014-10-02T15:01:23Z".
     */
    @Updatable
    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    @Override
    public void copyFrom(SqlScheduledMaintenance model) throws Exception {
        setCanReschedule(model.getCanReschedule());
        setScheduleDeadlineTime(model.getScheduleDeadlineTime());
        setStartTime(model.getStartTime());
    }

    @Override
    public String primaryKey() {
        return "";
    }

    public SqlScheduledMaintenance toSqlScheduledMaintenance() {
        SqlScheduledMaintenance config = new SqlScheduledMaintenance();

        config.setCanReschedule(getCanReschedule());
        config.setScheduleDeadlineTime(getScheduleDeadlineTime());
        config.setStartTime(getStartTime());

        return config;
    }
}
