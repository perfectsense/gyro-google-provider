package gyro.google.compute;

import com.google.cloud.compute.v1.SecurityPolicyAdaptiveProtectionConfig;
import com.google.cloud.compute.v1.SecurityPolicyAdaptiveProtectionConfigLayer7DdosDefenseConfig;
import gyro.core.resource.Diffable;
import gyro.core.resource.Updatable;
import gyro.core.validation.Required;
import gyro.core.validation.ValidStrings;
import gyro.google.Copyable;

public class SecurityPolicyAdaptiveProtection extends Diffable implements Copyable<SecurityPolicyAdaptiveProtectionConfig> {

    private Boolean enabled;
    private String ruleVisibility;

    /**
     * Weather to enable adaptive protection. Defaults to ``false``.
     */
    @Required
    @Updatable
    public Boolean getEnabled() {
        if (enabled == null) {
            enabled = false;
        }

        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * The visibility of the rules.
     */
    @Updatable
    @ValidStrings({"STANDARD", "PREMIUM"})
    public String getRuleVisibility() {
        return ruleVisibility;
    }

    public void setRuleVisibility(String ruleVisibility) {
        this.ruleVisibility = ruleVisibility;
    }

    @Override
    public void copyFrom(SecurityPolicyAdaptiveProtectionConfig model) {
        if (model.hasLayer7DdosDefenseConfig()) {
            SecurityPolicyAdaptiveProtectionConfigLayer7DdosDefenseConfig defenseConfig = model.getLayer7DdosDefenseConfig();
            setRuleVisibility(defenseConfig.getRuleVisibility());
            setEnabled(defenseConfig.getEnable());
        }
    }

    @Override
    public String primaryKey() {
        return "";
    }

    public SecurityPolicyAdaptiveProtectionConfig toAdaptiveProtection() {
        return SecurityPolicyAdaptiveProtectionConfig.newBuilder()
            .setLayer7DdosDefenseConfig(SecurityPolicyAdaptiveProtectionConfigLayer7DdosDefenseConfig.newBuilder()
                .setEnable(getEnabled())
                .setRuleVisibility(getRuleVisibility())
                .build())
            .build();
    }
}
