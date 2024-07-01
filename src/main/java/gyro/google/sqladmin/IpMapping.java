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
import gyro.core.validation.ValidStrings;
import gyro.google.Copyable;

public class IpMapping extends Diffable implements Copyable<com.google.api.services.sqladmin.model.IpMapping> {

    private String ipAddress;

    private String timeToRetire;

    private String type;

    /**
     * The IP address assigned.
     */
    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    /**
     * The due time for this IP to be retired in [RFC 3339](https://tools.ietf.org/html/rfc3339) format, for example `2012-11-15T16:19:00.094Z`. This field is only available when the IP is scheduled to be retired.
     */
    public String getTimeToRetire() {
        return timeToRetire;
    }

    public void setTimeToRetire(String timeToRetire) {
        this.timeToRetire = timeToRetire;
    }

    /**
     * The type of this IP address. A `PRIMARY` address is a public address that can accept incoming connections. A `PRIVATE` address is a private address that can accept incoming connections. An `OUTGOING` address is the source address of connections originating from the instance, if supported.
     */
    @ValidStrings({
        "SQL_IP_ADDRESS_TYPE_UNSPECIFIED",
        "PRIMARY",
        "OUTGOING",
        "PRIVATE",
        "MIGRATED_1ST_GEN"
    })
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String primaryKey() {
        return "";
    }

    @Override
    public void copyFrom(com.google.api.services.sqladmin.model.IpMapping model) {
        setIpAddress(model.getIpAddress());
        setTimeToRetire(model.getTimeToRetire());
        setType(model.getType());
    }
}
