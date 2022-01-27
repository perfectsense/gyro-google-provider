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

package gyro.google.compute;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.api.gax.rpc.InvalidArgumentException;
import com.google.api.gax.rpc.NotFoundException;
import com.google.cloud.compute.v1.DeleteFirewallRequest;
import com.google.cloud.compute.v1.Firewall;
import com.google.cloud.compute.v1.FirewallLogConfig;
import com.google.cloud.compute.v1.FirewallsClient;
import com.google.cloud.compute.v1.InsertFirewallRequest;
import com.google.cloud.compute.v1.Operation;
import com.google.cloud.compute.v1.PatchFirewallRequest;
import gyro.core.GyroUI;
import gyro.core.Type;
import gyro.core.resource.Id;
import gyro.core.resource.Output;
import gyro.core.resource.Resource;
import gyro.core.resource.Updatable;
import gyro.core.scope.State;
import gyro.core.validation.ConflictsWith;
import gyro.core.validation.Range;
import gyro.core.validation.Required;
import gyro.core.validation.ValidStrings;
import gyro.core.validation.ValidationError;
import gyro.google.Copyable;

/**
 * Creates a firewall rule.
 *
 * Example
 * -------
 *
 * .. code-block:: gyro
 *
 *     google::compute-firewall-rule firewall-rule-example
 *         name: "firewall-rule-example"
 *         network: $(google::network network-example-firewall-rule)
 *         description: "firewall-rule-example-desc"
 *         direction: "ingress"
 *         priority: 1001
 *
 *         rule-type: "allow"
 *
 *         allowed
 *             protocol: "tcp"
 *             ports: ["95-96", "80-85"]
 *         end
 *
 *         source-tags: [
 *             "source-tag-example"
 *         ]
 *     end
 */
@Type("compute-firewall-rule")
public class FirewallResource extends ComputeResource implements Copyable<Firewall> {

    private String name;
    private NetworkResource network;
    private String description;
    private String ruleType;
    private List<FirewallAllowed> allowed;
    private List<FirewallDenied> denied;
    private Set<String> destinationRanges;
    private String direction;
    private Boolean disabled;
    private Integer priority;
    private Set<String> sourceRanges;
    private Set<String> sourceServiceAccounts;
    private Set<String> sourceTags;
    private Boolean logConfig;
    private Set<String> targetServiceAccounts;
    private Set<String> targetTags;

    // Read-only
    private String id;
    private String selfLink;

    /**
     * The name of the firewall rule. Needs to follow Google firewall rule naming convention. Must be 1-63 characters long consisting only of dash, lowercase letter, or digit. First character needs to be a letter and the last character can either be a letter or a digit.
     */
    @Required
    @Id
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * The network to create this firewall rule in.
     */
    @Required
    public NetworkResource getNetwork() {
        return network;
    }

    public void setNetwork(NetworkResource network) {
        this.network = network;
    }

    /**
     * The description of the firewall rule.
     */
    @Updatable
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Allow or Deny requests that matches the rules.
     */
    @Required
    @ValidStrings({ "ALLOW", "DENY" })
    @Updatable
    public String getRuleType() {
        return ruleType != null ? ruleType.toUpperCase() : null;
    }

    public void setRuleType(String ruleType) {
        this.ruleType = ruleType;
    }

    /**
     *  A set of rules that allow requests to pass that get matched. Required if 'rule-type' set to ``ALLOW``.
     *
     * @subresource gyro.google.compute.FirewallAllowed
     */
    @Updatable
    @ConflictsWith("denied")
    public List<FirewallAllowed> getAllowed() {
        if (allowed == null) {
            allowed = new ArrayList<>();
        }

        return allowed;
    }

    public void setAllowed(List<FirewallAllowed> allowed) {
        this.allowed = allowed;
    }

    /**
     * A set of rules that deny requests to pass that get matched. Required if 'rule-type' set to ``DENY``.
     *
     * @subresource gyro.google.cloud.FirewallDenied
     */
    @Updatable
    @ConflictsWith("allowed")
    public List<FirewallDenied> getDenied() {
        if (denied == null) {
            denied = new ArrayList<>();
        }

        return denied;
    }

    public void setDenied(List<FirewallDenied> denied) {
        this.denied = denied;
    }

    /**
     * A set of destination IP in cidr form that the firewall rule applies to. Can only be set when 'direction' set to 'EGRESS'.
     */
    @Updatable
    public Set<String> getDestinationRanges() {
        if (destinationRanges == null) {
            destinationRanges = new HashSet<>();
        }

        return destinationRanges;
    }

    public void setDestinationRanges(Set<String> destinationRanges) {
        this.destinationRanges = destinationRanges;
    }

    /**
     * The direction specifies the type of requests this rule applies to. INGRESS for incoming and EGRESS for outgoing requests.
     */
    @Required
    @ValidStrings({ "INGRESS", "EGRESS" })
    @Updatable
    public String getDirection() {
        return direction != null ? direction.toUpperCase() : null;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    /**
     * When true, disables the firewall rule. Defaults to ``false``.
     */
    @Updatable
    public Boolean getDisabled() {
        if (disabled == null) {
            disabled = false;
        }

        return disabled;
    }

    public void setDisabled(Boolean disabled) {
        this.disabled = disabled;
    }

    /**
     * The priority of the firewall rule, when there are multiple that match a certain requests. Lower the number higher the priority. Defaults to ``1000``.
     */
    @Updatable
    @Range(min = 0, max = 65535)
    public Integer getPriority() {
        if (priority == null) {
            priority = 1000;
        }

        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    /**
     * A set of source IP in cidr form that the firewall rule applies to. Can only be set when 'direction' set to 'INGRESS'.
     */
    @Updatable
    public Set<String> getSourceRanges() {
        if (sourceRanges == null) {
            sourceRanges = new HashSet<>();
        }

        return sourceRanges;
    }

    public void setSourceRanges(Set<String> sourceRanges) {
        this.sourceRanges = sourceRanges;
    }

    /**
     * A set of service accounts that the incoming requests are going to be matched with only if it originated from instances of the accounts specified.  Can only be set when 'direction' set to 'INGRESS'.
     */
    @Updatable
    @ConflictsWith({ "source-tags", "target-tags" })
    public Set<String> getSourceServiceAccounts() {
        if (sourceServiceAccounts == null) {
            sourceServiceAccounts = new HashSet<>();
        }

        return sourceServiceAccounts;
    }

    public void setSourceServiceAccounts(Set<String> sourceServiceAccounts) {
        this.sourceServiceAccounts = sourceServiceAccounts;
    }

    /**
     * A set of tags that the incoming requests are going to be matched with only if it originated from instances whose primary network interface has the same tags. Can only be set when 'direction' set to 'INGRESS'. Only one of 'source-service-accounts' or 'source-tags' can be set.
     */
    @Updatable
    public Set<String> getSourceTags() {
        if (sourceTags == null) {
            sourceTags = new HashSet<>();
        }

        return sourceTags;
    }

    public void setSourceTags(Set<String> sourceTags) {
        this.sourceTags = sourceTags;
    }

    /**
     * When true, enables logs for the firewall rule. Defaults to ``false``.
     */
    @Updatable
    public Boolean getLogConfig() {
        if (logConfig == null) {
            logConfig = false;
        }

        return logConfig;
    }

    public void setLogConfig(Boolean logConfig) {
        this.logConfig = logConfig;
    }

    /**
     * A set of service accounts that the outgoing requests are going to be matched with only if it is targeted from instances of the accounts specified.
     */
    @Updatable
    @ConflictsWith({ "target-tags", "source-tags" })
    public Set<String> getTargetServiceAccounts() {
        if (targetServiceAccounts == null) {
            targetServiceAccounts = new HashSet<>();
        }

        return targetServiceAccounts;
    }

    public void setTargetServiceAccounts(Set<String> targetServiceAccounts) {
        this.targetServiceAccounts = targetServiceAccounts;
    }

    /**
     * A set of tags that the outgoing requests are going to be matched with only if it is targeted from instances whose primary network interface has the same tags. Only one of 'target-service-accounts' or 'target-tags' can be set.
     */
    @Updatable
    public Set<String> getTargetTags() {
        if (targetTags == null) {
            targetTags = new HashSet<>();
        }

        return targetTags;
    }

    public void setTargetTags(Set<String> targetTags) {
        this.targetTags = targetTags;
    }

    /**
     * The ID of the firewall rule.
     */
    @Output
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    /**
     * The fully qualified url of the firewall rule.
     */
    @Output
    public String getSelfLink() {
        return selfLink;
    }

    public void setSelfLink(String selfLink) {
        this.selfLink = selfLink;
    }

    @Override
    public void copyFrom(Firewall firewall) {
        setName(firewall.getName());
        setNetwork(findById(
            NetworkResource.class,
            firewall.getNetwork()));
        setDescription(firewall.getDescription());

        setDestinationRanges(
            firewall.getDestinationRangesList() != null ? new HashSet<>(firewall.getDestinationRangesList()) : null);
        setDirection(firewall.getDirection().toString());
        setDisabled(firewall.getDisabled());
        setPriority(firewall.getPriority());
        setSourceRanges(firewall.getSourceRangesList() != null ? new HashSet<>(firewall.getSourceRangesList()) : null);
        setSourceServiceAccounts(firewall.getSourceServiceAccountsList() != null
            ? new HashSet<>(firewall.getSourceServiceAccountsList()) : null);
        setSourceTags(firewall.getSourceTagsList() != null ? new HashSet<>(firewall.getSourceTagsList()) : null);
        setLogConfig(firewall.getLogConfig().getEnable());
        setTargetServiceAccounts(firewall.getTargetServiceAccountsList() != null
            ? new HashSet<>(firewall.getTargetServiceAccountsList()) : null);
        setTargetTags(firewall.getTargetTagsList() != null ? new HashSet<>(firewall.getTargetTagsList()) : null);

        getAllowed().clear();
        if (firewall.getAllowedList() != null && !firewall.getAllowedList().isEmpty()) {
            setAllowed(firewall.getAllowedList().stream().map(rule -> {
                FirewallAllowed allowed = newSubresource(FirewallAllowed.class);
                allowed.copyFrom(rule);
                return allowed;
            }).collect(Collectors.toList()));
        }

        getDenied().clear();
        if (firewall.getDeniedList() != null && !firewall.getDeniedList().isEmpty()) {
            setDenied(firewall.getDeniedList().stream().map(rule -> {
                FirewallDenied denied = newSubresource(FirewallDenied.class);
                denied.copyFrom(rule);
                return denied;
            }).collect(Collectors.toList()));
        }

        setId(String.valueOf(firewall.getId()));
        setSelfLink(firewall.getSelfLink());
    }

    @Override
    public boolean doRefresh() throws Exception {
        try (FirewallsClient client = createClient(FirewallsClient.class)) {

            Firewall firewall = getFirewall(client);

            if (firewall == null) {
                return false;
            }

            copyFrom(firewall);

            return true;
        }
    }

    @Override
    public void doCreate(GyroUI ui, State state) throws Exception {
        try (FirewallsClient client = createClient(FirewallsClient.class)) {
            Operation operation = client.insertCallable().call(InsertFirewallRequest.newBuilder()
                .setProject(getProjectId())
                .setFirewallResource(toFirewall())
                .build());

            waitForCompletion(operation);
        }

        refresh();
    }

    @Override
    public void doUpdate(GyroUI ui, State state, Resource current, Set<String> changedFieldNames) throws Exception {
        try (FirewallsClient client = createClient(FirewallsClient.class)) {
            Operation operation = client.patchCallable().call(PatchFirewallRequest.newBuilder()
                .setProject(getProjectId())
                .setFirewall(getName())
                .setFirewallResource(toFirewall())
                .build());

            waitForCompletion(operation);
        }

        refresh();
    }

    @Override
    public void doDelete(GyroUI ui, State state) throws Exception {
        try (FirewallsClient client = createClient(FirewallsClient.class)) {
            Operation operation = client.deleteCallable().call(DeleteFirewallRequest.newBuilder()
                .setProject(getProjectId())
                .setFirewall(getName())
                .build());

            waitForCompletion(operation);
        }
    }

    @Override
    public List<ValidationError> validate(Set<String> configuredFields) {
        List<ValidationError> errors = new ArrayList<>();

        if (getRuleType().equals("ALLOW") && getAllowed().isEmpty()) {
            errors.add(new ValidationError(this, "allowed",
                "'allowed' needs to be set when 'rule-type' set to 'ALLOW'."));
        }

        if (getRuleType().equals("DENY") && getDenied().isEmpty()) {
            errors.add(new ValidationError(this, "denied", "'denied' needs to be set when 'rule-type' set to 'DENY'."));
        }

        if (getDirection().equals("INGRESS")) {
            if (!getDestinationRanges().isEmpty()) {
                errors.add(new ValidationError(this, "destination-ranges",
                    "'destination-ranges' cannot be set when 'direction' set to 'INGRESS'"));
            }

            if (getSourceServiceAccounts().isEmpty() && getSourceTags().isEmpty() && getSourceRanges().isEmpty()) {
                errors.add(new ValidationError(
                    this,
                    null,
                    "At least one of 'source-service-accounts', 'source-tags' or 'source-ranges' is required when 'direction' set to 'INGRESS'"));
            }

        } else if (getDirection().equals("EGRESS")) {
            if (!getSourceRanges().isEmpty()) {
                errors.add(new ValidationError(this, "source-ranges",
                    "'source-ranges' cannot be set when 'direction' set to 'EGRESS'"));
            }

            if (!getSourceTags().isEmpty()) {
                errors.add(new ValidationError(this, "source-tags",
                    "'source-tags' cannot be set when 'direction' set to 'EGRESS'"));
            }

            if (!getSourceServiceAccounts().isEmpty()) {
                errors.add(new ValidationError(this, "source-service-accounts",
                    "'source-service-accounts' cannot be set when 'direction' set to 'EGRESS'"));
            }

            if (getDestinationRanges().isEmpty()) {
                errors.add(new ValidationError(this, null,
                    "'destination-ranges' is required when 'direction' set to 'EGRESS'"));
            }
        }

        return errors;
    }

    private Firewall toFirewall() {
        Firewall.Builder builder = Firewall.newBuilder().setName(getName()).setNetwork(getNetwork().getSelfLink())
            .setDirection(getDirection()).setDisabled(getDisabled())
            .setPriority(getPriority());

        if (getDescription() != null) {
            builder.setDescription(getDescription());
        }

        if (getLogConfig() != null) {
            builder.setLogConfig(FirewallLogConfig.newBuilder().setEnable(getLogConfig()).build());
        }

        if (getDestinationRanges() != null) {
            builder.addAllDestinationRanges(!getDestinationRanges().isEmpty()
                ? new ArrayList<>(getDestinationRanges())
                : Collections.emptyList());
        }

        if (getTargetTags() != null) {
            builder.addAllTargetTags(!getTargetTags().isEmpty()
                ? new ArrayList<>(getTargetTags())
                : Collections.emptyList());
        }

        if (getTargetServiceAccounts() != null) {
            builder.addAllTargetServiceAccounts(!getTargetServiceAccounts().isEmpty() ? new ArrayList<>(
                getTargetServiceAccounts()) : Collections.emptyList());
        }

        if (getSourceServiceAccounts() != null) {
            builder.addAllSourceServiceAccounts(!getSourceServiceAccounts().isEmpty() ? new ArrayList<>(
                getSourceServiceAccounts()) : Collections.emptyList());
        }

        if (getSourceTags() != null) {
            builder.addAllSourceTags(!getSourceTags().isEmpty()
                ? new ArrayList<>(getSourceTags())
                : Collections.emptyList());
        }

        if (getSourceRanges() != null) {
            builder.addAllSourceRanges(!getSourceRanges().isEmpty()
                ? new ArrayList<>(getSourceRanges())
                : Collections.emptyList());
        }

        if (getRuleType().equals("ALLOW")) {
            builder.addAllAllowed(getAllowed().stream().map(FirewallAllowed::toAllowed).collect(Collectors.toList()));
            builder.addAllDenied(Collections.emptyList());
        } else if (getRuleType().equals("DENY")) {
            builder.addAllDenied(getDenied().stream().map(FirewallDenied::toDenied).collect(Collectors.toList()));
            builder.addAllAllowed(Collections.emptyList());
        }

        return builder.build();
    }

    private Firewall getFirewall(FirewallsClient client) throws Exception {
        Firewall firewall = null;

        try {
            firewall = client.get(getProjectId(), getName());
        } catch (NotFoundException | InvalidArgumentException ex) {
            // ignore
        }

        return firewall;
    }
}
