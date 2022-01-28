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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.AbstractMap;
import java.util.Date;
import java.util.Map;

import com.google.container.v1beta1.TimeWindow;
import com.google.protobuf.Timestamp;
import gyro.core.GyroException;
import gyro.core.resource.Diffable;
import gyro.core.validation.Regex;
import gyro.google.Copyable;

public class GkeTimeWindow extends Diffable implements Copyable<TimeWindow> {

    private String startTime;
    private String endTime;
    private String name;

    /**
     * The start time of the time window.
     */
    @Regex(value = "^(1[0-2]|0[1-9])/(3[01]|[012][0-9]|)/[0-9]{4}$", message = "a string matching the @|bold mm/dd/yyyy|@ format")
    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    /**
     * The end time of the time window.
     */
    @Regex(value = "^(1[0-2]|0[1-9])/(3[01]|[012][0-9]|)/[0-9]{4}$", message = "a string matching the @|bold mm/dd/yyyy|@ format")
    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    /**
     * The name of the time window.
     */
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String primaryKey() {
        return String.format("Name: %s, startTime: %s, endTime: %s", getName(), getStartTime(), getEndTime());
    }

    @Override
    public void copyFrom(TimeWindow model) throws Exception {
        if (model.hasStartTime()) {
            DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
            Date date = new Date(model.getStartTime().getSeconds() * 1000L);
            setStartTime(dateFormat.format(date));
        }

        if (model.hasEndTime()) {
            DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
            Date date = new Date(model.getEndTime().getSeconds() * 1000L);
            setEndTime(dateFormat.format(date));
        }
    }

    public void copyFrom(Map.Entry<String, TimeWindow> model) {
        if (model.getValue().hasStartTime()) {
            DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
            Date date = new Date(model.getValue().getStartTime().getSeconds() * 1000L);
            setStartTime(dateFormat.format(date));
        }

        if (model.getValue().hasEndTime()) {
            DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
            Date date = new Date(model.getValue().getEndTime().getSeconds() * 1000L);
            setEndTime(dateFormat.format(date));
        }

        setName(model.getKey());
    }

    TimeWindow toTimeWindow() {
        TimeWindow.Builder builder = TimeWindow.newBuilder();
        try {
            if (getStartTime() != null) {
                SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");
                builder.setStartTime(Timestamp.newBuilder()
                    .setSeconds(format.parse(getStartTime()).getTime() / 1000)
                    .build());
            }

            if (getEndTime() != null) {
                SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");
                builder.setEndTime(Timestamp.newBuilder()
                    .setSeconds(format.parse(getEndTime()).getTime() / 1000)
                    .build());
            }
        } catch (ParseException e) {
            throw new GyroException(e.getMessage());
        }

        return builder.build();
    }

    Map.Entry<String, TimeWindow> toTimeWindowEntry() {
        TimeWindow.Builder builder = TimeWindow.newBuilder();
        try {
            if (getStartTime() != null) {
                SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");
                builder.setStartTime(Timestamp.newBuilder()
                    .setSeconds(format.parse(getStartTime()).getTime() / 1000)
                    .build());
            }

            if (getEndTime() != null) {
                SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");
                builder.setEndTime(Timestamp.newBuilder()
                    .setSeconds(format.parse(getEndTime()).getTime() / 1000)
                    .build());
            }
        } catch (ParseException e) {
            throw new GyroException(e.getMessage());
        }

        return new AbstractMap.SimpleEntry<>(getName(), builder.build());
    }
}
