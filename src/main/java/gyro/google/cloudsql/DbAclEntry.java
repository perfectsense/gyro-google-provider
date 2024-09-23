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

import com.google.api.services.sqladmin.model.AclEntry;
import gyro.core.resource.Diffable;
import gyro.core.validation.Required;
import gyro.google.Copyable;

public class DbAclEntry extends Diffable implements Copyable<AclEntry> {

    private String expirationTime;
    private String name;
    private String value;

    /**
     * The time when this access control entry expires in RFC 3339 format, for example ``2012-11-15T16:19:00.094Z``
     */
    public String getExpirationTime() {
        return expirationTime;
    }

    public void setExpirationTime(String expirationTime) {
        this.expirationTime = expirationTime;
    }

    /**
     * The label to identify this entry.
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
    public void copyFrom(AclEntry model) {
        setExpirationTime(model.getExpirationTime());
        setName(model.getName());
        setValue(model.getValue());
    }

    @Override
    public String primaryKey() {
        return String.format(
            "Acl Entry [Value: %s, Expiration Time: %s, Name: %s]",
            getValue(),
            getExpirationTime(),
            getName());
    }

    public AclEntry toAclEntry() {
        AclEntry entry = new AclEntry();

        entry.setValue(getValue());

        if (getExpirationTime() != null) {
            entry.setExpirationTime(getExpirationTime());
        }

        if (getName() != null) {
            entry.setName(getName());
        }

        return entry;
    }
}
