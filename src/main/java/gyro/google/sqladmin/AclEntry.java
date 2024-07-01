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
import gyro.core.validation.Required;
import gyro.google.Copyable;

public class AclEntry extends Diffable implements Copyable<com.google.api.services.sqladmin.model.AclEntry> {

    private String expirationTime;

    private String name;

    private String value;

    /**
     * The time when this access control entry expires in [RFC 3339](https://tools.ietf.org/html/rfc3339) format, for example `2012-11-15T16:19:00.094Z`.
     */
    @Required
    public String getExpirationTime() {
        return expirationTime;
    }

    public void setExpirationTime(String expirationTime) {
        this.expirationTime = expirationTime;
    }

    /**
     * Optional. A label to identify this entry.
     */
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * The allowlisted value for the access control list.
     */
    @Required
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String primaryKey() {
        // Formatted string to represent the primary key of the resource
        // Making sure that the getter is not returning null
        StringBuilder sb = new StringBuilder();
        if (getName() != null) {
            sb.append("name: ").append(getName());
        }

        if (getValue() != null) {
            sb.append(" value: ").append(getValue());
        }

        if (getExpirationTime() != null) {
            sb.append(" expiration-time: ").append(getExpirationTime());
        }

        return sb.toString();
    }

    @Override
    public void copyFrom(com.google.api.services.sqladmin.model.AclEntry model) {
        setExpirationTime(model.getExpirationTime());
        setName(model.getName());
        setValue(model.getValue());
    }

    com.google.api.services.sqladmin.model.AclEntry copyTo() {
        com.google.api.services.sqladmin.model.AclEntry aclEntry = new com.google.api.services.sqladmin.model.AclEntry();
        aclEntry.setExpirationTime(getExpirationTime());
        aclEntry.setName(getName());
        aclEntry.setValue(getValue());

        return aclEntry;
    }
}
