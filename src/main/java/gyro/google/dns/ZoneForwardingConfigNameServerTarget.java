/*
 * Copyright 2019, Perfect Sense, Inc.
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

package gyro.google.dns;

import java.util.Optional;

import com.google.api.services.dns.model.ManagedZoneForwardingConfigNameServerTarget;
import gyro.core.resource.Diffable;
import gyro.google.Copyable;

public class ZoneForwardingConfigNameServerTarget extends Diffable
    implements Copyable<ManagedZoneForwardingConfigNameServerTarget> {

    private String ipv4Address;

    /**
     * IPv4 address of a target name server.
     */
    public String getIpv4Address() {
        return ipv4Address;
    }

    public void setIpv4Address(String ipv4Address) {
        this.ipv4Address = ipv4Address;
    }

    @Override
    public void copyFrom(ManagedZoneForwardingConfigNameServerTarget model) {
        setIpv4Address(model.getIpv4Address());
    }

    @Override
    public String primaryKey() {
        return getIpv4Address();
    }

    public ManagedZoneForwardingConfigNameServerTarget copyTo() {
        ManagedZoneForwardingConfigNameServerTarget managedZoneForwardingConfigNameServerTarget = new ManagedZoneForwardingConfigNameServerTarget();
        managedZoneForwardingConfigNameServerTarget.setIpv4Address(getIpv4Address());
        return managedZoneForwardingConfigNameServerTarget;
    }

    public boolean isEqualTo(ManagedZoneForwardingConfigNameServerTarget managedZoneForwardingConfigNameServerTarget) {
        return Optional.ofNullable(managedZoneForwardingConfigNameServerTarget)
            .map(ManagedZoneForwardingConfigNameServerTarget::getIpv4Address)
            .filter(ipv4Address -> ipv4Address.equals(getIpv4Address()))
            .isPresent();
    }
}
