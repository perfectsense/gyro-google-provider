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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.google.api.services.dns.model.ManagedZoneForwardingConfig;
import com.google.api.services.dns.model.ManagedZoneForwardingConfigNameServerTarget;
import gyro.core.resource.Diffable;
import gyro.core.resource.Updatable;
import gyro.core.validation.Required;
import gyro.google.Copyable;

public class ZoneForwardingConfig extends Diffable implements Copyable<ManagedZoneForwardingConfig> {

    private List<ZoneForwardingConfigNameServerTarget> targetNameServer;

    /**
     * List of target name servers to forward to. Cloud DNS will select the best available name server if more than one target is given.
     *
     * @subresource gyro.google.dns.ZoneForwardingConfigNameServerTarget
     */
    @Required
    @Updatable
    public List<ZoneForwardingConfigNameServerTarget> getTargetNameServer() {
        if (targetNameServer == null) {
            targetNameServer = new ArrayList<>();
        }
        return targetNameServer;
    }

    public void setTargetNameServer(List<ZoneForwardingConfigNameServerTarget> targetNameServer) {
        this.targetNameServer = targetNameServer;
    }

    @Override
    public String primaryKey() {
        return "";
    }

    @Override
    public void copyFrom(ManagedZoneForwardingConfig model) {
        List<ZoneForwardingConfigNameServerTarget> diffableTargetNameServers = null;
        List<ManagedZoneForwardingConfigNameServerTarget> targetNameServers = model.getTargetNameServers();

        if (targetNameServers != null && !targetNameServers.isEmpty()) {
            diffableTargetNameServers = targetNameServers
                .stream()
                .map(nameServerTarget -> {
                    ZoneForwardingConfigNameServerTarget diffableNameServerTarget = newSubresource(
                        ZoneForwardingConfigNameServerTarget.class);
                    diffableNameServerTarget.copyFrom(nameServerTarget);
                    return diffableNameServerTarget;
                })
                .collect(Collectors.toList());
        }
        setTargetNameServer(diffableTargetNameServers);
    }

    public ManagedZoneForwardingConfig copyTo() {
        ManagedZoneForwardingConfig managedZoneForwardingConfig = new ManagedZoneForwardingConfig();
        List<ZoneForwardingConfigNameServerTarget> targetNameServers = getTargetNameServer();

        if (!targetNameServers.isEmpty()) {
            managedZoneForwardingConfig.setTargetNameServers(
                targetNameServers
                    .stream()
                    .map(ZoneForwardingConfigNameServerTarget::copyTo)
                    .collect(Collectors.toList()));
        }
        return managedZoneForwardingConfig;
    }
}
