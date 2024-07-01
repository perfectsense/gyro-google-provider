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

public class AvailableDatabaseVersion extends Diffable
    implements Copyable<com.google.api.services.sqladmin.model.AvailableDatabaseVersion> {

    private String displayName;

    private String majorVersion;

    private String name;

    /**
     * The database version's display name.
     */
    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    /**
     * The version's major version name.
     */
    public String getMajorVersion() {
        return majorVersion;
    }

    public void setMajorVersion(String majorVersion) {
        this.majorVersion = majorVersion;
    }

    /**
     * The database version name. For MySQL 8.0, this string provides the database major and minor version.
     */
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String primaryKey() {
        return "";
    }

    @Override
    public void copyFrom(com.google.api.services.sqladmin.model.AvailableDatabaseVersion model) {
        setDisplayName(model.getDisplayName());
        setMajorVersion(model.getMajorVersion());
        setName(model.getName());
    }
}
