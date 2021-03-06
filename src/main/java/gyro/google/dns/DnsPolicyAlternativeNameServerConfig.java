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

import com.google.api.services.dns.model.PolicyAlternativeNameServerConfig;
import com.google.api.services.dns.model.PolicyAlternativeNameServerConfigTargetNameServer;
import gyro.core.resource.Diffable;
import gyro.core.resource.Updatable;
import gyro.core.validation.Required;
import gyro.google.Copyable;

public class DnsPolicyAlternativeNameServerConfig extends Diffable
    implements Copyable<PolicyAlternativeNameServerConfig> {

    private List<DnsPolicyTargetNameServer> targetNameServer;

    /**
     * Sets an alternative name server for the associated networks. When specified, all DNS queries are forwarded to a name server that you choose. Names such as .internal are not available when an alternative name server is specified.
     *
     * @subresource gyro.google.dns.DnsPolicyTargetNameServer
     */
    @Required
    @Updatable
    public List<DnsPolicyTargetNameServer> getTargetNameServer() {
        if (targetNameServer == null) {
            targetNameServer = new ArrayList<>();
        }
        return targetNameServer;
    }

    public void setTargetNameServer(List<DnsPolicyTargetNameServer> targetNameServer) {
        this.targetNameServer = targetNameServer;
    }

    @Override
    public String primaryKey() {
        return "";
    }

    @Override
    public void copyFrom(PolicyAlternativeNameServerConfig model) {
        List<PolicyAlternativeNameServerConfigTargetNameServer> targetNameServers = model
            .getTargetNameServers();

        getTargetNameServer().clear();
        if (targetNameServers != null && !targetNameServers.isEmpty()) {
            setTargetNameServer(targetNameServers
                .stream()
                .map(nameServerTarget -> {
                    DnsPolicyTargetNameServer nameServer = newSubresource(DnsPolicyTargetNameServer.class);
                    nameServer.copyFrom(nameServerTarget);
                    return nameServer;
                })
                .collect(Collectors.toList()));
        }

    }

    public PolicyAlternativeNameServerConfig toPolicyAlternativeNameServerConfig() {
        PolicyAlternativeNameServerConfig policyAlternativeNameServerConfig = new PolicyAlternativeNameServerConfig();
        List<DnsPolicyTargetNameServer> targetNameServers = getTargetNameServer();

        if (targetNameServers != null) {
            policyAlternativeNameServerConfig.setTargetNameServers(
                targetNameServers
                    .stream()
                    .map(DnsPolicyTargetNameServer::toPolicyAlternativeNameServerConfigTargetNameServer)
                    .collect(Collectors.toList()));
        }
        return policyAlternativeNameServerConfig;
    }
}
