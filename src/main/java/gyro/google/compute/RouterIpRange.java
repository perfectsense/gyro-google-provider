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

import com.google.cloud.compute.v1.RouterAdvertisedIpRange;
import gyro.core.resource.Diffable;
import gyro.core.resource.Updatable;
import gyro.core.validation.Required;
import gyro.google.Copyable;

public class RouterIpRange extends Diffable implements Copyable<RouterAdvertisedIpRange> {

    private String range;
    private String description;

    /**
     * The IP range to advertise.
     */
    @Required
    public String getRange() {
        return range;
    }

    public void setRange(String range) {
        this.range = range;
    }

    /**
     * The description for the IP range.
     */
    @Updatable
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String primaryKey() {
        return getRange();
    }

    @Override
    public void copyFrom(RouterAdvertisedIpRange model) {
        if (model.hasRange()) {
            setRange(model.getRange());
        }

        if (model.hasDescription()) {
            setDescription(model.getDescription());
        }
    }

    RouterAdvertisedIpRange toRouterAdvertisedIpRange() {
        RouterAdvertisedIpRange.Builder builder = RouterAdvertisedIpRange.newBuilder();

        if (getRange() != null) {
            builder.setRange(getRange());
        }

        if (getDescription() != null) {
            builder.setDescription(getDescription());
        }

        return builder.build();
    }
}
