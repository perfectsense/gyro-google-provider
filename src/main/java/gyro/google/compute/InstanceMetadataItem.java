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

import com.google.api.services.compute.model.Metadata;
import gyro.core.resource.Diffable;
import gyro.core.resource.Updatable;
import gyro.core.validation.Regex;
import gyro.core.validation.Required;
import gyro.google.Copyable;

public class InstanceMetadataItem extends Diffable implements Copyable<Metadata.Items> {

    private String key;
    private String value;

    /**
     * Key for the metadata entry. Keys must conform to the following regexp: ``[a-zA-Z0-9-_]+``, and be
     * less than 128 bytes in length. Must be unique across all metadata keys in the instance.
     */
    @Regex(value = "[a-zA-Z0-9-_]{1,128}", message = "only alphanumeric characters, dashes, and underscores, and must be within 128 characters in length.")
    @Required
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    /**
     * Value for the metadata entry. The size must be less than or equal to 262144 bytes (256 KiB).
     */
    @Regex(value = ".{0,262144}", message = "a length within 262144 characters.")
    @Updatable
    public String getValue() {
        if (value == null) {
            return "";
        }

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
    public void copyFrom(Metadata.Items model) {
        setKey(model.getKey());
        setValue(model.getValue());
    }

    public Metadata.Items copyTo() {
        Metadata.Items item = new Metadata.Items();
        item.setKey(getKey());
        item.setValue(getValue());
        return item;
    }
}
