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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.cloud.compute.v1.ResourcePolicySnapshotSchedulePolicySnapshotProperties;
import gyro.core.resource.Diffable;
import gyro.google.Copyable;

public class SnapshotSchedulePolicySnapshotProperties extends Diffable
    implements Copyable<ResourcePolicySnapshotSchedulePolicySnapshotProperties> {

    private Boolean guestFlush;
    private Map<String, String> labels;
    private List<String> storageLocations;

    /**
     * Perform a guest aware snapshot.
     */
    public Boolean getGuestFlush() {
        return guestFlush;
    }

    public void setGuestFlush(Boolean guestFlush) {
        this.guestFlush = guestFlush;
    }

    /**
     * Labels to apply to scheduled snapshots.
     */
    public Map<String, String> getLabels() {
        if (labels == null) {
            labels = new HashMap<>();
        }

        return labels;
    }

    public void setLabels(Map<String, String> labels) {
        this.labels = labels;
    }

    /**
     * Cloud Storage bucket storage location of the auto snapshot (regional or multi-regional).
     */
    public List<String> getStorageLocations() {
        if (storageLocations == null) {
            storageLocations = new ArrayList<>();
        }

        return storageLocations;
    }

    public void setStorageLocations(List<String> storageLocations) {
        this.storageLocations = storageLocations;
    }

    @Override
    public String primaryKey() {
        return "";
    }

    @Override
    public void copyFrom(ResourcePolicySnapshotSchedulePolicySnapshotProperties model) {
        if (model.hasGuestFlush()) {
            setGuestFlush(model.getGuestFlush());
        }

        setLabels(model.getLabelsMap());
        setStorageLocations(model.getStorageLocationsList());
    }

    public ResourcePolicySnapshotSchedulePolicySnapshotProperties copyTo() {
        return ResourcePolicySnapshotSchedulePolicySnapshotProperties.newBuilder().setGuestFlush(getGuestFlush())
            .putAllLabels(getLabels()).addAllStorageLocations(getStorageLocations()).build();
    }
}
