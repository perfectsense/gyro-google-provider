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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.container.v1.MaintenanceWindow;
import gyro.core.resource.Diffable;
import gyro.google.Copyable;

public class GkeMaintenanceWindow extends Diffable implements Copyable<MaintenanceWindow> {

    private List<GkeTimeWindow> maintainenceExclusion;
    private GkeDailyMaintenanceWindow dailyMaintenanceWindow;
    private GkeRecurringTimeWindow recurringTimeWindow;

    public List<GkeTimeWindow> getMaintainenceExclusion() {
        if (maintainenceExclusion == null) {
            maintainenceExclusion = new ArrayList<>();
        }

        return maintainenceExclusion;
    }

    public void setMaintainenceExclusion(List<GkeTimeWindow> maintainenceExclusion) {
        this.maintainenceExclusion = maintainenceExclusion;
    }

    public GkeDailyMaintenanceWindow getDailyMaintenanceWindow() {
        return dailyMaintenanceWindow;
    }

    public void setDailyMaintenanceWindow(GkeDailyMaintenanceWindow dailyMaintenanceWindow) {
        this.dailyMaintenanceWindow = dailyMaintenanceWindow;
    }

    public GkeRecurringTimeWindow getRecurringTimeWindow() {
        return recurringTimeWindow;
    }

    public void setRecurringTimeWindow(GkeRecurringTimeWindow recurringTimeWindow) {
        this.recurringTimeWindow = recurringTimeWindow;
    }

    @Override
    public String primaryKey() {
        return "";
    }

    @Override
    public void copyFrom(MaintenanceWindow model) throws Exception {
        setMaintainenceExclusion(null);
        if (model.getMaintenanceExclusionsCount() > 0) {
            setMaintainenceExclusion(model.getMaintenanceExclusionsMap().entrySet().stream().map(e -> {
                GkeTimeWindow window = newSubresource(GkeTimeWindow.class);
                window.copyFrom(e);
                return window;
            }).collect(Collectors.toList()));
        }

        setDailyMaintenanceWindow(null);
        if (model.hasDailyMaintenanceWindow()) {
            GkeDailyMaintenanceWindow window = newSubresource(GkeDailyMaintenanceWindow.class);
            window.copyFrom(model.getDailyMaintenanceWindow());
            setDailyMaintenanceWindow(window);
        }

        setRecurringTimeWindow(null);
        if (model.hasRecurringWindow()) {
            GkeRecurringTimeWindow window = newSubresource(GkeRecurringTimeWindow.class);
            window.copyFrom(model.getRecurringWindow());
            setRecurringTimeWindow(window);
        }
    }

    MaintenanceWindow toMaintenanceWindow() {
        MaintenanceWindow.Builder builder = MaintenanceWindow.newBuilder();

        if (!getMaintainenceExclusion().isEmpty()) {
            builder.putAllMaintenanceExclusions(getMaintainenceExclusion().stream()
                .map(GkeTimeWindow::toTimeWindowEntry)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));
        }

        if (getDailyMaintenanceWindow() != null) {
            builder.setDailyMaintenanceWindow(getDailyMaintenanceWindow().toDailyMaintenanceWindow());
        }

        if (getRecurringTimeWindow() != null) {
            builder.setRecurringWindow(getRecurringTimeWindow().toRecurringTimeWindow());
        }

        return builder.build();
    }
}
