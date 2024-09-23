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

import com.google.api.services.sqladmin.model.IpMapping;
import gyro.core.resource.Diffable;
import gyro.core.resource.Updatable;
import gyro.core.validation.Required;
import gyro.core.validation.ValidStrings;
import gyro.google.Copyable;

public class DbIpMapping extends Diffable implements Copyable<IpMapping> {

    private String ipAddress;
    private String timeToRetire;
    private String type;

    /**
     * The IP address assigned.
     */
    @Required
    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    /**
     * The due time for this IP to be retired in RFC 3339 format, for example ``2012-11-15T16:19:00.094Z``.
     */
    @Updatable
    public String getTimeToRetire() {
        return timeToRetire;
    }

    public void setTimeToRetire(String timeToRetire) {
        this.timeToRetire = timeToRetire;
    }

    /**
     * The type of this IP address.
     */
    @ValidStrings({ "PRIMARY", "PRIVATE", "OUTGOING" })
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public void copyFrom(IpMapping model) {
        setIpAddress(model.getIpAddress());
        setType(model.getType());
        setTimeToRetire(model.getTimeToRetire());
    }

    @Override
    public String primaryKey() {
        return getIpAddress();
    }

    public IpMapping toIpMapping() {
        IpMapping ipMapping = new IpMapping();

        ipMapping.setIpAddress(getIpAddress());
        ipMapping.setType(getType());
        ipMapping.setTimeToRetire(getTimeToRetire());

        return ipMapping;
    }
}
