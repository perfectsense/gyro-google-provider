/*
 * Copyright 2024, Brightspot.
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
package gyro.google.cloudsql;

import com.google.api.services.sqladmin.model.LocationPreference;
import gyro.core.resource.Diffable;
import gyro.core.resource.Updatable;
import gyro.google.Copyable;

public class DbLocationPreference extends Diffable implements Copyable<LocationPreference> {

    private String secondaryZone;
    private String zone;

    /**
     * The preferred Compute Engine zone for the secondary/failover
     * To disable this field, set it to ``no_secondary_zone``.
     */
    @Updatable
    public String getSecondaryZone() {
        return secondaryZone;
    }

    public void setSecondaryZone(String secondaryZone) {
        this.secondaryZone = secondaryZone;
    }

    /**
     * The preferred Compute Engine zone.
     * Changing this might restart the instance.
     */
    @Updatable
    public String getZone() {
        return zone;
    }

    public void setZone(String zone) {
        this.zone = zone;
    }

    @Override
    public void copyFrom(LocationPreference model) throws Exception {
        setSecondaryZone(model.getSecondaryZone());
        setZone(model.getZone());
    }

    @Override
    public String primaryKey() {
        return "";
    }

    public LocationPreference toLocationPreference() {
        LocationPreference preference = new LocationPreference();

        if (getSecondaryZone() != null) {
            preference.setSecondaryZone(getSecondaryZone());
        }
        if (getZone() != null) {
            preference.setZone(getZone());
        }

        return preference;
    }
}
