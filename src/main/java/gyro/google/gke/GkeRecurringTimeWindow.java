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

import java.text.ParseException;

import com.google.container.v1.RecurringTimeWindow;
import gyro.core.resource.Diffable;
import gyro.google.Copyable;

public class GkeRecurringTimeWindow extends Diffable implements Copyable<RecurringTimeWindow> {

    private GkeTimeWindow window;
    private String recurrence;

    public GkeTimeWindow getWindow() {
        return window;
    }

    public void setWindow(GkeTimeWindow window) {
        this.window = window;
    }

    public String getRecurrence() {
        return recurrence;
    }

    public void setRecurrence(String recurrence) {
        this.recurrence = recurrence;
    }

    @Override
    public String primaryKey() {
        return "";
    }

    @Override
    public void copyFrom(RecurringTimeWindow model) throws Exception {
        setRecurrence(model.getRecurrence());

        setWindow(null);
        if (model.hasWindow()) {
            GkeTimeWindow gkeTimeWindow = newSubresource(GkeTimeWindow.class);
            gkeTimeWindow.copyFrom(model.getWindow());
            setWindow(gkeTimeWindow);
        }
    }

    RecurringTimeWindow toRecurringTimeWindow() {
        return RecurringTimeWindow.newBuilder()
            .setRecurrence(getRecurrence())
            .setWindow(getWindow().toTimeWindow())
            .build();
    }
}
