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

import java.util.ArrayList;
import java.util.List;

import com.google.api.services.compute.model.Tags;
import gyro.core.resource.Diffable;
import gyro.core.resource.Output;
import gyro.core.resource.Updatable;
import gyro.core.validation.CollectionMax;
import gyro.core.validation.Regex;
import gyro.google.Copyable;

public class InstanceTags extends Diffable implements Copyable<Tags> {

    public List<String> items;
    public String fingerprint;

    /**
     * A set of tags. Each tag must be 1-63 characters, first character must be a lowercase letter and all following characters must be a dash, lowercase letter, or digit, except the last character, which cannot be a dash.
     * All tags for an instance must be unique. You can assign up to 64 different tags per instance.
     */
    @Updatable
    @CollectionMax(64)
    @Regex(value = "^[a-z]([-a-z0-9]{0,61}[a-z0-9]$)?", message = "only dashes, lowercase letters, or digits. The first character must be a lowercase letter, and the last character cannot be a dash. Each tag must be 1-63 characters.")
    public List<String> getItems() {
        if (items == null) {
            items = new ArrayList<>();
        }

        return items;
    }

    public void setItems(List<String> items) {
        this.items = items;
    }

    /**
     * A string which is essentially a hash of the tags' contents and changes after every request to modify or update tags.
     * An up-to-date fingerprint must always be provided in order to update or change tags.
     */
    @Output
    public String getFingerprint() {
        return fingerprint;
    }

    public void setFingerprint(String fingerprint) {
        this.fingerprint = fingerprint;
    }

    @Override
    public String primaryKey() {
        return "";
    }

    @Override
    public void copyFrom(Tags model) {
        setItems(model.getItems());
        setFingerprint(model.getFingerprint());
    }

    public Tags copyTo() {
        Tags tags = new Tags();

        tags.setItems(
            !getItems().isEmpty()
                ? getItems()
                : null
        );
        tags.setFingerprint(getFingerprint());
        return tags;
    }
}
