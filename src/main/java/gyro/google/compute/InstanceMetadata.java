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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.google.api.services.compute.model.Metadata;
import gyro.core.resource.Diffable;
import gyro.core.resource.Output;
import gyro.core.resource.Updatable;
import gyro.google.Copyable;

public class InstanceMetadata extends Diffable implements Copyable<Metadata> {

    private String fingerprint;
    private List<InstanceMetadataItem> item;

    /**
     * The list of metadata entries consisting of key/value pairs.
     *
     * @subresource gyro.google.compute.InstanceMetadataItem
     */
    @Updatable
    public List<InstanceMetadataItem> getItem() {
        if (item == null) {
            item = new ArrayList<>();
        }

        return item;
    }

    public void setItem(List<InstanceMetadataItem> item) {
        this.item = item;
    }

    /**
     * A string which is essentially a hash of the metadata's contents and changes after every request to modify or update metadata.
     * An up-to-date fingerprint must always be provided in order to update or change metadata.
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
    public void copyFrom(Metadata model) {
        setItem(
            model.getItems().stream().map(
                i -> {
                    InstanceMetadataItem item = newSubresource(InstanceMetadataItem.class);
                    item.copyFrom(i);
                    return item;
                }).collect(Collectors.toList())
        );
        setFingerprint(model.getFingerprint());
    }

    public Metadata copyTo() {
        Metadata metadata = new Metadata();
        metadata.setItems(
            !getItem().isEmpty()
                ? getItem().stream()
                .map(InstanceMetadataItem::copyTo)
                .collect(Collectors.toList())
                : null
        );
        metadata.setFingerprint(getFingerprint());

        return metadata;
    }
}
