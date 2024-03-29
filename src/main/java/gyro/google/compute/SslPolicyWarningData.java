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

import com.google.cloud.compute.v1.Data;
import gyro.core.resource.Diffable;
import gyro.core.resource.Output;
import gyro.google.Copyable;

public class SslPolicyWarningData extends Diffable implements Copyable<Data> {

    private String key;
    private String value;

    /**
     * A key that provides more detail on the warning being returned.
     */
    @Output
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    /**
     * A warning data value corresponding to the key.
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
        return "";
    }

    @Override
    public void copyFrom(Data model) {
        if (model.hasKey()) {
            setKey(model.getKey());
        }

        if (model.hasValue()) {
            setValue(model.getValue());
        }
    }
}
