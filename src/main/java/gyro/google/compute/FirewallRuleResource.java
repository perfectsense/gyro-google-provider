package gyro.google.compute;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.compute.Compute;
import com.google.api.services.compute.model.Firewall;
import com.google.api.services.compute.model.FirewallLogConfig;
import com.google.api.services.compute.model.Operation;
import com.google.cloud.compute.v1.ProjectGlobalNetworkName;
import gyro.core.GyroException;
import gyro.core.GyroUI;
import gyro.core.Type;
import gyro.core.resource.Id;
import gyro.core.resource.Output;
import gyro.core.resource.Resource;
import gyro.core.resource.Updatable;
import gyro.core.scope.State;
import gyro.core.validation.Range;
import gyro.core.validation.Required;
import gyro.core.validation.ValidStrings;
import gyro.core.validation.ValidationError;
import gyro.google.Copyable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Creates a firewall rule.
 *
 * Example
 * -------
 *
 * .. code-block:: gyro
 *
 *     google::firewall-rule firewall-rule-example
 *         name: "firewall-rule-example"
 *         network: $(google::network network-example-firewall-rule)
 *         description: "firewall-rule-example-desc"
 *         direction: "ingress"
 *         priority: 1001
 *
 *         rule-type: "allow"
 *
 *         rule
 *             protocol: "tcp"
 *             ports: ["95-96", "80-85"]
 *         end
 *
 *         source-tags: [
 *             "source-tag-example"
 *         ]
 *     end
 */
@Type("firewall-rule")
public class FirewallRuleResource extends ComputeResource implements Copyable<Firewall> {
    private String name;
    private NetworkResource network;
    private String description;
    private String ruleType;
    private Set<FirewallAllowDenyRule> rule;
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
     * The name of the firewall rule. (Required)
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
     * The network to create this firewall rule in. (Required)
     */
    @Required
    public NetworkResource getNetwork() {
        return network;
    }

    public void setNetwork(NetworkResource network) {
        this.network = network;
    }

    /**
     * The name of the firewall rule. (Required)
     */
    @Updatable
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Allow or Deny requests that matches the rules. Valid values are ``ALLOW`` or ``DENY``. (Required)
     */
    @Required
    @ValidStrings({"ALLOW", "DENY"})
    public String getRuleType() {
        return ruleType != null ? ruleType.toUpperCase() : null;
    }

    public void setRuleType(String ruleType) {
        this.ruleType = ruleType;
    }

    /**
     * A set of rules that the requests are going to get matched with. (Required)
     */
    @Required
    @Updatable
    public Set<FirewallAllowDenyRule> getRule() {
        if (rule == null) {
            rule = new HashSet<>();
        }

        return rule;
    }

    public void setRule(Set<FirewallAllowDenyRule> rule) {
        this.rule = rule;
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
     * The direction specifies the type of requests this rule applies to. INGRESS for incoming and EGRESS for outgoing requests. Valid values are ``INGRESS`` or ``EGRESS``. (Required)
     */
    @Required
    @ValidStrings({"INGRESS","EGRESS"})
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
     * The priority of the firewall rule, when there are multiple that match a certain requests. Lower the number higher the priority. Defaults to ``1000``. Valid values are between ``0`` and ``65535``.
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
     * A set of service accounts that the incoming requests are going to be matched with only if it originated from instances of the accounts specified.  Can only be set when 'direction' set to 'INGRESS'. Only one of 'source-service-account' or 'source-tags' can be set.
     */
    @Updatable
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
     * A set of tags that the incoming requests are going to be matched with only if it originated from instances whose primary network interface has the same tags. Can only be set when 'direction' set to 'INGRESS'. Only one of 'source-service-account' or 'source-tags' can be set.
     */
    @Updatable
    public Set<String> getSourceTags() {
        if (sourceTags == null) {
            sourceTags = new HashSet<>();
        }

        return sourceTags;
    }

    @Updatable
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
     * A set of service accounts that the outgoing requests are going to be matched with only if it is targeted from instances of the accounts specified. Can only be set when 'direction' set to 'EGRESS'. Only one of 'target-service-account' or 'target-tags' can be set.
     */
    @Updatable
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
     * A set of tags that the outgoing requests are going to be matched with only if it is targeted from instances whose primary network interface has the same tags. Can only be set when 'direction' set to 'EGRESS'. Only one of 'target-service-account' or 'target-tags' can be set.
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
        setNetwork(findById(NetworkResource.class, firewall.getNetwork().substring(firewall.getNetwork().lastIndexOf("/") + 1)));
        setDescription(firewall.getDescription());

        setDestinationRanges(firewall.getDestinationRanges() != null ? new HashSet<>(firewall.getDestinationRanges()) : null);
        setDirection(firewall.getDirection());
        setDisabled(firewall.getDisabled());
        setPriority(firewall.getPriority());
        setSourceRanges(firewall.getSourceRanges() != null ? new HashSet<>(firewall.getSourceRanges()) : null);
        setSourceServiceAccounts(firewall.getSourceServiceAccounts() != null ? new HashSet<>(firewall.getSourceServiceAccounts()) : null);
        setSourceTags(firewall.getSourceTags() != null ? new HashSet<>(firewall.getSourceTags()) : null);
        setLogConfig(firewall.getLogConfig().getEnable());
        setTargetServiceAccounts(firewall.getTargetServiceAccounts() != null ? new HashSet<>(firewall.getTargetServiceAccounts()) : null);
        setTargetTags(firewall.getTargetTags() != null ? new HashSet<>(firewall.getTargetTags()) : null);

        if (firewall.getAllowed() != null && !firewall.getAllowed().isEmpty()) {
            setRule(firewall.getAllowed().stream().map(rule -> {
                FirewallAllowDenyRule allowedRule = new FirewallAllowDenyRule();
                allowedRule.copyFrom(rule);
                return allowedRule;
            }).collect(Collectors.toSet()));
        } else if (firewall.getDenied() != null && !firewall.getDenied().isEmpty()) {
            setRule(firewall.getDenied().stream().map(rule -> {
                FirewallAllowDenyRule deniedRule = new FirewallAllowDenyRule();
                deniedRule.copyFrom(rule);
                return deniedRule;
            }).collect(Collectors.toSet()));
        }

        setId(firewall.getId().toString());
        setSelfLink(firewall.getSelfLink());
    }

    @Override
    public boolean refresh() {
        Compute client = creatClient(Compute.class);

        try {
            Firewall firewall = client.firewalls().get(getProjectId(), getName()).execute();
            copyFrom(firewall);

            return true;
        } catch (GoogleJsonResponseException je) {
            if (je.getDetails().getMessage().matches("The resource (.*) was not found")) {
                return false;
            } else {
                throw new GyroException(je.getDetails().getMessage());
            }
        } catch (IOException ex) {
            throw new GyroException(ex.getMessage(), ex.getCause());
        }
    }

    @Override
    public void create(GyroUI ui, State state) {
        Compute client = creatClient(Compute.class);

        try {
            Compute.Firewalls.Insert insert = client.firewalls().insert(getProjectId(), toFirewall());
            Operation operation = insert.execute();
            Operation.Error error = waitForCompletion(client, operation);
            if (error != null) {
                throw new GyroException(error.toPrettyString());
            }

            refresh();
        } catch (Exception ex) {
            throw new GyroException(ex.getMessage(), ex.getCause());
        }
    }

    @Override
    public void update(GyroUI ui, State state, Resource current, Set<String> changedFieldNames) {
        Compute client = creatClient(Compute.class);

        try {
            client.firewalls().update(getProjectId(), getName(), toFirewall()).execute();
        } catch (IOException ex) {
            throw new GyroException(ex.getMessage(), ex.getCause());
        }
    }

    @Override
    public void delete(GyroUI ui, State state) {
        Compute client = creatClient(Compute.class);

        try {
            client.firewalls().delete(getProjectId(), getName()).execute();
        } catch (IOException ex) {
            throw new GyroException(ex.getMessage(), ex.getCause());
        }
    }

    private Firewall toFirewall() {
        Firewall firewall = new Firewall();

        firewall.setName(getName());
        firewall.setDescription(getDescription());
        firewall.setNetwork(ProjectGlobalNetworkName.format(getNetwork().getName(), getProjectId()));
        firewall.setDestinationRanges(!getDestinationRanges().isEmpty() ? new ArrayList<>(getDestinationRanges()) : null);
        firewall.setDirection(getDirection());
        firewall.setDisabled(getDisabled());
        firewall.setPriority(getPriority());
        firewall.setSourceRanges(!getSourceRanges().isEmpty() ? new ArrayList<>(getSourceRanges()) : null);
        firewall.setSourceServiceAccounts(!getSourceServiceAccounts().isEmpty() ? new ArrayList<>(getSourceServiceAccounts()) : null);
        firewall.setSourceTags(!getSourceTags().isEmpty() ? new ArrayList<>(getSourceTags()) : null);
        firewall.setLogConfig(new FirewallLogConfig().setEnable(getLogConfig()));
        firewall.setTargetServiceAccounts(!getTargetServiceAccounts().isEmpty() ? new ArrayList<>(getTargetServiceAccounts()) : null);
        firewall.setTargetTags(!getTargetTags().isEmpty() ? new ArrayList<>(getTargetTags()) : null);

        if (getRuleType().equals("ALLOW")) {
            firewall.setAllowed(getRule().stream().map(FirewallAllowDenyRule::toAllowed).collect(Collectors.toList()));
        } else {
            firewall.setDenied(getRule().stream().map(FirewallAllowDenyRule::toDenied).collect(Collectors.toList()));
        }

        return firewall;
    }

    @Override
    public List<ValidationError> validate() {
        List<ValidationError> errors = new ArrayList<>();

        if (getDirection().equals("INGRESS")) {
            if (!getDestinationRanges().isEmpty()) {
                errors.add(new ValidationError(this, "destination-ranges", "'destination-ranges' cannot be set when 'direction' set to 'INGRESS'"));
            }

            if (!getTargetTags().isEmpty()) {
                errors.add(new ValidationError(this, "target-tags", "'target-tags' cannot be set when 'direction' set to 'INGRESS'"));
            }

            if (!getTargetServiceAccounts().isEmpty()) {
                errors.add(new ValidationError(this, "target-service-account", "'target-service-account' cannot be set when 'direction' set to 'INGRESS'"));
            }

        } else if (getDirection().equals("EGRESS")) {
            if (!getSourceRanges().isEmpty()) {
                errors.add(new ValidationError(this, "source-ranges", "'source-ranges' cannot be set when 'direction' set to 'EGRESS'"));
            }

            if (!getSourceTags().isEmpty()) {
                errors.add(new ValidationError(this, "source-tags", "'source-tags' cannot be set when 'direction' set to 'EGRESS'"));
            }

            if (!getSourceServiceAccounts().isEmpty()) {
                errors.add(new ValidationError(this, "source-service-account", "'source-service-account' cannot be set when 'direction' set to 'EGRESS'"));
            }
        }

        if ((!getTargetTags().isEmpty() || !getSourceTags().isEmpty()) && !getTargetServiceAccounts().isEmpty()) {
            errors.add(new ValidationError(this, "target-service-accounts", "'target-service-accounts' cannot be set when 'target-tags' or 'source-tags' are set"));
        }

        return errors;
    }
}
