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

import com.google.api.services.sqladmin.model.DenyMaintenancePeriod;
import gyro.core.resource.Diffable;
import gyro.core.validation.DependsOn;
import gyro.core.validation.Regex;
import gyro.core.validation.Required;
import gyro.google.Copyable;

public class DbDenyMaintenancePeriod extends Diffable implements Copyable<DenyMaintenancePeriod> {

    private String endDate;
    private String startDate;
    private String time;

    /**
     * The end date of the maintenance deny period. The date is in format yyyy-mm-dd or mm-dd.
     * If the year is omitted, the period recurs every year.
     */
    @DependsOn("start-date")
    @Regex("([0-9]{4}\\-)?[0-1][0-9]\\-[0-3][0-9]")
    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    /**
     * The start date of the maintenance deny period. The date is in format yyyy-mm-dd or mm-dd.
     * If the year is omitted, the period recurs every year.
     */
    @Required
    @Regex("([0-9]{4}\\-)?[0-1][0-9]\\-[0-3][0-9]")
    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    /**
     * The time of the maintenance deny period in UTC. The time is in format: HH:mm:SS
     */
    @DependsOn("start-date")
    @Regex("[0-2][0-9]:[0-5][0-9]:[0-5][0-9]")
    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    @Override
    public void copyFrom(DenyMaintenancePeriod model) throws Exception {
        setEndDate(model.getEndDate());
        setStartDate(model.getStartDate());
        setTime(model.getTime());
    }

    @Override
    public String primaryKey() {
        return String.format(
            "Maintenance Period [Start: %s, End: %s, Time %s]",
            getStartDate(),
            getEndDate(),
            getTime());
    }

    public DenyMaintenancePeriod toDenyMaintenancePeriod() {
        DenyMaintenancePeriod period = new DenyMaintenancePeriod();

        period.setStartDate(getStartDate());

        if (getEndDate() != null) {
            period.setEndDate(getEndDate());
        }

        if (getTime() != null) {
            period.setTime(getTime());
        }

        return period;
    }
}
