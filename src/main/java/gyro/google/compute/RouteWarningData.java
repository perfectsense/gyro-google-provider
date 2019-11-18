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

import com.google.api.services.compute.model.Route.Warnings.Data;
import gyro.core.resource.Diffable;
import gyro.core.resource.Output;
import gyro.google.Copyable;

public class RouteWarningData extends Diffable implements Copyable<Data> {
    private String key;
    private String value;

    /**
     * The key of the warning providing more details of the scope of the warning.
     */
    @Output
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    /**
     * The data value corresponding to the key.
     */
    @Output
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String primaryKey() {
        return getKey();
    }

    @Override
    public void copyFrom(Data data) {
        setKey(data.getKey());
        setValue(data.getValue());
    }
}
