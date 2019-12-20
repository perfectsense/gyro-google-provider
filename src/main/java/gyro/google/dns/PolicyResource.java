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
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.util.Data;
import com.google.api.services.dns.Dns;
import com.google.api.services.dns.model.PoliciesPatchResponse;
import com.google.api.services.dns.model.Policy;
import com.google.api.services.dns.model.PolicyAlternativeNameServerConfig;
import com.google.api.services.dns.model.PolicyNetwork;
import gyro.core.GyroUI;
import gyro.core.Type;
import gyro.core.resource.Resource;
import gyro.core.resource.Updatable;
import gyro.core.scope.State;
import gyro.core.validation.Required;
import gyro.google.Copyable;
import gyro.google.GoogleResource;

/**
 * Creates a dns policy.
 *
 * Example
 * -------
 *
 * .. code-block:: gyro
 *
 *     google::dns-policy dns-policy-example
 *         name: "dns-policy-example"
 *         description: "DNS Policy Example"
 *
 *         network
 *             network: $(google::compute-network managed-zone-network-example)
 *         end
 *
 *         alternative-name-server-config
 *             target-name-server
 *                 ipv4-address: "10.0.0.1"
 *             end
 *         end
 *     end
 */
@Type("dns-policy")
public class PolicyResource extends GoogleResource implements Copyable<Policy> {

    private DnsPolicyAlternativeNameServerConfig alternativeNameServerConfig;

    private String description;

    private Boolean enableInboundForwarding;

    private Boolean enableLogging;

    private String name;

    private List<DnsPolicyNetwork> network;

    /**
     * Sets an alternative name server for the associated networks. When specified, all DNS queries are forwarded to a name server that you choose. Names such as .internal are not available when an alternative name server is specified.
     *
     * @subresource gyro.google.dns.DnsPolicyAlternativeNameServerConfig
     */
    @Updatable
    public DnsPolicyAlternativeNameServerConfig getAlternativeNameServerConfig() {
        return alternativeNameServerConfig;
    }

    public void setAlternativeNameServerConfig(DnsPolicyAlternativeNameServerConfig alternativeNameServerConfig) {
        this.alternativeNameServerConfig = alternativeNameServerConfig;
    }

    /**
     * A mutable string of at most 1024 characters associated with this resource for the user's convenience. Has no effect on the policy's function.
     */
    @Required
    @Updatable
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Allows networks bound to this policy to receive DNS queries sent by VMs or applications over VPN connections. When enabled, a virtual IP address will be allocated from each of the sub- networks that are bound to this policy.
     */
    @Updatable
    public Boolean getEnableInboundForwarding() {
        return enableInboundForwarding;
    }

    public void setEnableInboundForwarding(Boolean enableInboundForwarding) {
        this.enableInboundForwarding = enableInboundForwarding;
    }

    /**
     * Controls whether logging is enabled for the networks bound to this policy. Defaults to no logging if not set.
     */
    @Updatable
    public Boolean getEnableLogging() {
        return enableLogging;
    }

    public void setEnableLogging(Boolean enableLogging) {
        this.enableLogging = enableLogging;
    }

    /**
     * User assigned name for this policy.
     */
    @Required
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * List of network names specifying networks to which this policy is applied.
     *
     * @subresource gyro.google.dns.DnsPolicyNetwork
     */
    @Updatable
    public List<DnsPolicyNetwork> getNetwork() {
        if (network == null) {
            network = new ArrayList<>();
        }
        return network;
    }

    public void setNetwork(List<DnsPolicyNetwork> network) {
        this.network = network;
    }

    @Override
    public boolean doRefresh() throws Exception {
        Dns client = createClient(Dns.class);
        Policy policy = client.policies().get(getProjectId(), getName()).execute();
        copyFrom(policy);
        return true;
    }

    @Override
    public void doCreate(GyroUI ui, State state) throws Exception {
        Policy policy = new Policy();
        DnsPolicyAlternativeNameServerConfig alternativeNameServerConfig = getAlternativeNameServerConfig();

        if (alternativeNameServerConfig != null) {
            policy.setAlternativeNameServerConfig(alternativeNameServerConfig.toPolicyAlternativeNameServerConfig());
        }
        policy.setDescription(getDescription());
        policy.setEnableInboundForwarding(getEnableInboundForwarding());
        policy.setEnableLogging(getEnableLogging());
        policy.setName(getName());
        List<DnsPolicyNetwork> network = getNetwork();

        if (!network.isEmpty()) {
            policy.setNetworks(network
                .stream()
                .map(DnsPolicyNetwork::toPolicyNetwork)
                .collect(Collectors.toList()));
        }
        Dns client = createClient(Dns.class);
        Policy response = client.policies().create(getProjectId(), policy).execute();
        copyFrom(response);
    }

    @Override
    public void doUpdate(
        GyroUI ui, State state, Resource current, Set<String> changedFieldNames) throws Exception {
        Dns client = createClient(Dns.class);
        Policy policy = new Policy();

        for (String changedFieldName : changedFieldNames) {
            if (changedFieldName.equals("description")) {
                policy.setDescription(getDescription());
            } else if (changedFieldName.equals("alternative-name-server-config")) {
                DnsPolicyAlternativeNameServerConfig alternativeNameServerConfig = getAlternativeNameServerConfig();
                policy.setAlternativeNameServerConfig(alternativeNameServerConfig == null
                    ? Data.nullOf(PolicyAlternativeNameServerConfig.class)
                    : alternativeNameServerConfig.toPolicyAlternativeNameServerConfig());
            } else if (changedFieldName.equals("enable-inbound-forwarding")) {
                policy.setEnableInboundForwarding(getEnableInboundForwarding());
            } else if (changedFieldName.equals("enable-logging")) {
                policy.setEnableLogging(getEnableLogging());
            } else if (changedFieldName.equals("network")) {
                List<DnsPolicyNetwork> networks = getNetwork();
                policy.setNetworks(networks == null
                    ? Data.nullOf(ArrayList.class)
                    : networks.stream()
                        .map(DnsPolicyNetwork::toPolicyNetwork)
                        .collect(Collectors.toList()));
            }
        }

        PoliciesPatchResponse response = client.policies().patch(getProjectId(), getName(), policy).execute();
        copyFrom(response.getPolicy());
    }

    @Override
    public void doDelete(GyroUI ui, State state) throws Exception {
        Dns client = createClient(Dns.class);

        try {
            Policy policy = client.policies().get(getProjectId(), getName()).execute();

            // Check for networks attached to this policy
            // Remove them prior to deleting
            // Handles error when deleting a policy with attached network
            if (!policy.getNetworks().isEmpty()) {
                Policy updatePolicy = new Policy();

                updatePolicy.setNetworks(Data.nullOf(ArrayList.class));
                client.policies().patch(getProjectId(), getName(), updatePolicy).execute();
            }

            client.policies().delete(getProjectId(), getName()).execute();
        } catch (GoogleJsonResponseException je) {
            if (je.getDetails().getCode() != 404) {
                throw je;
            }
        }
    }

    @Override
    public void copyFrom(Policy model) {
        PolicyAlternativeNameServerConfig policyAlternativeNameServerConfig = model.getAlternativeNameServerConfig();

        if (policyAlternativeNameServerConfig != null) {
            DnsPolicyAlternativeNameServerConfig alternativeNameServerConfig =
                Optional.ofNullable(getAlternativeNameServerConfig())
                    .orElse(newSubresource(DnsPolicyAlternativeNameServerConfig.class));
            alternativeNameServerConfig.copyFrom(policyAlternativeNameServerConfig);
        }

        setDescription(model.getDescription());
        setEnableInboundForwarding(model.getEnableInboundForwarding());
        setEnableLogging(model.getEnableLogging());
        setName(model.getName());

        List<DnsPolicyNetwork> diffablePolicyNetworks = null;
        List<PolicyNetwork> networks = model.getNetworks();

        if (networks != null && !networks.isEmpty()) {
            diffablePolicyNetworks = networks
                .stream()
                .map(network -> {
                    DnsPolicyNetwork policyNetwork = newSubresource(DnsPolicyNetwork.class);
                    policyNetwork.copyFrom(network);
                    return policyNetwork;
                })
                .collect(Collectors.toList());
        }
        setNetwork(diffablePolicyNetworks);
    }
}
