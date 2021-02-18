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

package gyro.google.pubsub;

import java.util.ArrayList;
import java.util.List;

import gyro.core.resource.Diffable;
import gyro.core.validation.Required;
import gyro.google.Copyable;

public class MessageStoragePolicy extends Diffable implements Copyable<com.google.pubsub.v1.MessageStoragePolicy> {

    private List<String> allowedPersistenceRegions;

    /**
     * The list of IDs of GCP regions where messages that are published to the topic may be persisted in storage.
     */
    @Required
    public List<String> getAllowedPersistenceRegions() {
        if (allowedPersistenceRegions == null) {
            allowedPersistenceRegions = new ArrayList<>();
        }

        return allowedPersistenceRegions;
    }

    public void setAllowedPersistenceRegions(List<String> allowedPersistenceRegions) {
        this.allowedPersistenceRegions = allowedPersistenceRegions;
    }

    @Override
    public String primaryKey() {
        return "";
    }

    @Override
    public void copyFrom(com.google.pubsub.v1.MessageStoragePolicy model) throws Exception {
        getAllowedPersistenceRegions().clear();
        setAllowedPersistenceRegions(new ArrayList<>(model.getAllowedPersistenceRegionsList()));
    }

    protected com.google.pubsub.v1.MessageStoragePolicy toMessageStoragePolicy() {
        return com.google.pubsub.v1.MessageStoragePolicy.newBuilder()
            .addAllAllowedPersistenceRegions(getAllowedPersistenceRegions())
            .build();
    }
}
