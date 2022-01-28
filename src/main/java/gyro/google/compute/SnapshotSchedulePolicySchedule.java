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

import com.google.cloud.compute.v1.ResourcePolicySnapshotSchedulePolicySchedule;
import gyro.core.resource.Diffable;
import gyro.core.validation.ConflictsWith;
import gyro.google.Copyable;

public class SnapshotSchedulePolicySchedule extends Diffable
    implements Copyable<ResourcePolicySnapshotSchedulePolicySchedule> {

    private DailyCycle dailySchedule;
    private HourlyCycle hourlySchedule;
    private WeeklyCycle weeklySchedule;

    /**
     * Define a schedule that runs every nth day of the month.
     *
     * @subresource gyro.google.compute.DailyCycle
     */
    @ConflictsWith({ "hourly-schedule", "weekly-schedule" })
    public DailyCycle getDailySchedule() {
        return dailySchedule;
    }

    public void setDailySchedule(DailyCycle dailySchedule) {
        this.dailySchedule = dailySchedule;
    }

    /**
     * Define a schedule that runs every nth hour.
     *
     * @subresource gyro.google.compute.HourlyCycle
     */
    @ConflictsWith({ "daily-schedule", "weekly-schedule" })
    public HourlyCycle getHourlySchedule() {
        return hourlySchedule;
    }

    public void setHourlySchedule(HourlyCycle hourlySchedule) {
        this.hourlySchedule = hourlySchedule;
    }

    /**
     * Define a schedule that runs on specified days of the week.
     *
     * @subresource gyro.google.compute.WeeklyCycle
     */
    @ConflictsWith({ "daily-schedule", "hourly-schedule" })
    public WeeklyCycle getWeeklySchedule() {
        return weeklySchedule;
    }

    public void setWeeklySchedule(WeeklyCycle weeklySchedule) {
        this.weeklySchedule = weeklySchedule;
    }

    @Override
    public String primaryKey() {
        return "";
    }

    @Override
    public void copyFrom(ResourcePolicySnapshotSchedulePolicySchedule model) {
        setDailySchedule(null);
        if (model.getDailySchedule() != null) {
            DailyCycle currentDailySchedule = newSubresource(DailyCycle.class);
            currentDailySchedule.copyFrom(model.getDailySchedule());
            setDailySchedule(currentDailySchedule);
        }

        setHourlySchedule(null);
        if (model.getHourlySchedule() != null) {
            HourlyCycle currentHourlySchedule = newSubresource(HourlyCycle.class);
            currentHourlySchedule.copyFrom(model.getHourlySchedule());
            setHourlySchedule(currentHourlySchedule);
        }

        setWeeklySchedule(null);
        if (model.getWeeklySchedule() != null) {
            WeeklyCycle currentWeeklySchedule = newSubresource(WeeklyCycle.class);
            currentWeeklySchedule.copyFrom(model.getWeeklySchedule());
            setWeeklySchedule(currentWeeklySchedule);
        }
    }

    public ResourcePolicySnapshotSchedulePolicySchedule copyTo() {
        ResourcePolicySnapshotSchedulePolicySchedule.Builder builder = ResourcePolicySnapshotSchedulePolicySchedule.newBuilder();

        if (getDailySchedule() != null) {
            builder.setDailySchedule(getDailySchedule().copyTo());
        }

        if (getHourlySchedule() != null) {
            builder.setHourlySchedule(getHourlySchedule().copyTo());
        }

        if (getWeeklySchedule() != null) {
            builder.setWeeklySchedule(getWeeklySchedule().copyTo());
        }

        return builder.build();
    }
}
