/*
 * Copyright 2019, Perfect Sense, Inc.
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

import com.google.api.services.compute.model.Route;
import gyro.core.resource.Diffable;
import gyro.core.resource.Output;
import gyro.google.Copyable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RouteWarning extends Diffable implements Copyable<Route.Warnings> {
    private String code;
    private List<RouteWarningData> data;
    private String message;

    /**
     * The warning code.
     */
    @Output
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    /**
     * The details of the warning.
     *
     * @subresource gyro.google.compute.RouteWarningData
     */
    @Output
    public List<RouteWarningData> getData() {
        return data;
    }

    public void setData(List<RouteWarningData> data) {
        this.data = data;
    }

    /**
     * The warning message.
     */
    @Output
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String primaryKey() {
        return getCode();
    }

    @Override
    public void copyFrom(Route.Warnings warnings) {
        setMessage(warnings.getMessage());
        setCode(warnings.getCode());
        if (warnings.getData() != null && !warnings.getData().isEmpty()) {
            setData(warnings.getData().stream().map(data -> {
                RouteWarningData warningData = newSubresource(RouteWarningData.class);
                warningData.copyFrom(data);
                return warningData;
            }).collect(Collectors.toList()));
        }
    }
}
