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

public class DenyMaintenancePeriod extends Diffable
    implements Copyable<com.google.api.services.sqladmin.model.DenyMaintenancePeriod> {

    private String endDate;

    private String startDate;

    private String time;

    /**
     * "deny maintenance period" end date. If the year of the end date is empty, the year of the start date also must be empty. In this case, it means the no maintenance interval recurs every year. The date is in format yyyy-mm-dd i.e., 2020-11-01, or mm-dd, i.e., 11-01
     */
    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    /**
     * "deny maintenance period" start date. If the year of the start date is empty, the year of the end date also must be empty. In this case, it means the deny maintenance period recurs every year. The date is in format yyyy-mm-dd i.e., 2020-11-01, or mm-dd, i.e., 11-01
     */
    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    /**
     * Time in UTC when the "deny maintenance period" starts on start_date and ends on end_date. The time is in format: HH:mm:SS, i.e., 00:00:00
     */
    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    @Override
    public String primaryKey() {
        StringBuilder sb = new StringBuilder();

        if (getStartDate() != null) {
            sb.append("Start Date: ").append(getStartDate()).append(" ");
        }

        if (getEndDate() != null) {
            sb.append("End Date: ").append(getEndDate()).append(" ");
        }

        if (getTime() != null) {
            sb.append("Time: ").append(getTime()).append(" ");
        }

        return sb.toString();
    }

    @Override
    public void copyFrom(com.google.api.services.sqladmin.model.DenyMaintenancePeriod model) {
        setEndDate(model.getEndDate());
        setStartDate(model.getStartDate());
        setTime(model.getTime());
    }

    com.google.api.services.sqladmin.model.DenyMaintenancePeriod toDenyMaintenancePeriod() {
        com.google.api.services.sqladmin.model.DenyMaintenancePeriod denyMaintenancePeriod = new com.google.api.services.sqladmin.model.DenyMaintenancePeriod();
        denyMaintenancePeriod.setEndDate(getEndDate());
        denyMaintenancePeriod.setStartDate(getStartDate());
        denyMaintenancePeriod.setTime(getTime());

        return denyMaintenancePeriod;
    }
}
