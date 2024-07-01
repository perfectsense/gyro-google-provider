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

package gyro.google.sqladmin;

import gyro.core.resource.Diffable;
import gyro.google.Copyable;

public class LocationPreference extends Diffable
    implements Copyable<com.google.api.services.sqladmin.model.LocationPreference> {

    private String followGaeApplication;

    private String secondaryZone;

    private String zone;

    /**
     * The App Engine application to follow, it must be in the same region as the Cloud SQL instance. WARNING: Changing this might restart the instance.
     */
    public String getFollowGaeApplication() {
        return followGaeApplication;
    }

    public void setFollowGaeApplication(String followGaeApplication) {
        this.followGaeApplication = followGaeApplication;
    }

    /**
     * The preferred Compute Engine zone for the secondary/failover (for example: us-central1-a, us-central1-b, etc.). To disable this field, set it to 'no_secondary_zone'.
     */
    public String getSecondaryZone() {
        return secondaryZone;
    }

    public void setSecondaryZone(String secondaryZone) {
        this.secondaryZone = secondaryZone;
    }

    /**
     * The preferred Compute Engine zone (for example: us-central1-a, us-central1-b, etc.). WARNING: Changing this might restart the instance.
     */
    public String getZone() {
        return zone;
    }

    public void setZone(String zone) {
        this.zone = zone;
    }

    @Override
    public String primaryKey() {
        return "";
    }

    @Override
    public void copyFrom(com.google.api.services.sqladmin.model.LocationPreference model) {
        setFollowGaeApplication(model.getFollowGaeApplication());
        setSecondaryZone(model.getSecondaryZone());
        setZone(model.getZone());
    }

    com.google.api.services.sqladmin.model.LocationPreference copyTo() {
        com.google.api.services.sqladmin.model.LocationPreference locationPreference = new com.google.api.services.sqladmin.model.LocationPreference();
        locationPreference.setFollowGaeApplication(getFollowGaeApplication());
        locationPreference.setSecondaryZone(getSecondaryZone());
        locationPreference.setZone(getZone());

        return locationPreference;
    }
}
