/*
 * Copyright 2023, Brightspot.
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
import java.util.List;
import java.util.Set;

import com.google.cloud.compute.v1.SecurityPolicyRuleRateLimitOptions;
import com.google.cloud.compute.v1.SecurityPolicyRuleRateLimitOptionsThreshold;
import com.google.cloud.compute.v1.SecurityPolicyRuleRedirectOptions;
import gyro.core.resource.Diffable;
import gyro.core.resource.Updatable;
import gyro.core.validation.Required;
import gyro.core.validation.ValidStrings;
import gyro.core.validation.ValidationError;
import gyro.google.Copyable;

public class SecurityPolicyRuleRateLimitOptionsConfig extends Diffable
    implements Copyable<SecurityPolicyRuleRateLimitOptions> {

    private SecurityPolicyRuleRateLimitOptionsThresholdConfig banThreshold;
    private Integer banDurationSec;
    private SecurityPolicyRuleRateLimitOptionsThresholdConfig rateLimitThreshold;
    private String conformAction;
    private String exceedAction;

    // TODO: Not supported by the api yet
    private String enforceOnKey;
    private String enforceOnKeyName;

    // TODO: Not supported in the UI yet
    private SecurityPolicyRuleRedirectOptionsConfig exceedRedirectConfig;

    /**
     * The ban threshold config. Only valid when rule `action` is set to `rate_based_ban`.
     *
     * @subresource gyro.google.compute.SecurityPolicyRuleRateLimitOptionsThresholdConfig
     */
    @Updatable
    public SecurityPolicyRuleRateLimitOptionsThresholdConfig getBanThreshold() {
        return banThreshold;
    }

    public void setBanThreshold(SecurityPolicyRuleRateLimitOptionsThresholdConfig banThreshold) {
        this.banThreshold = banThreshold;
    }

    /**
     * The ban duration in seconds. Only valid when rule `action` is set to `rate_based_ban`.
     */
    @Updatable
    public Integer getBanDurationSec() {
        return banDurationSec;
    }

    public void setBanDurationSec(Integer banDurationSec) {
        this.banDurationSec = banDurationSec;
    }

    /**
     * The rate limit threshold config.
     *
     * @subresource gyro.google.compute.SecurityPolicyRuleRateLimitOptionsThresholdConfig
     */
    @Required
    @Updatable
    public SecurityPolicyRuleRateLimitOptionsThresholdConfig getRateLimitThreshold() {
        return rateLimitThreshold;
    }

    public void setRateLimitThreshold(SecurityPolicyRuleRateLimitOptionsThresholdConfig rateLimitThreshold) {
        this.rateLimitThreshold = rateLimitThreshold;
    }

    /**
     * The action to take when requests are under the threshold. Default is ``allow``.
     */
    @Required
    @ValidStrings("allow")
    public String getConformAction() {
        if (conformAction == null) {
            conformAction = "allow";
        }

        return conformAction;
    }

    public void setConformAction(String conformAction) {
        this.conformAction = conformAction;
    }

    /**
     * The action to take when requests are over the threshold.
     */
    @Required
    @Updatable
    @ValidStrings({"deny(403)", "deny(404)", "deny(429)", "deny(502)", "redirect"})
    public String getExceedAction() {
        return exceedAction;
    }

    public void setExceedAction(String exceedAction) {
        this.exceedAction = exceedAction;
    }

    /**
     * The key to enforce the rate limit on.
     */
    @Updatable
    public String getEnforceOnKey() {
        return enforceOnKey;
    }

    public void setEnforceOnKey(String enforceOnKey) {
        this.enforceOnKey = enforceOnKey;
    }

    /**
     * The name of the key to enforce the rate limit on.
     */
    @Updatable
    public String getEnforceOnKeyName() {
        return enforceOnKeyName;
    }

    public void setEnforceOnKeyName(String enforceOnKeyName) {
        this.enforceOnKeyName = enforceOnKeyName;
    }

    /**
     * The redirect config when requests exceed the threshold. Only valid when ``exceed-action`` is set to ``redirect``.
     *
     * @subresource gyro.google.compute.SecurityPolicyRuleRedirectOptionsConfig
     */
    @Updatable
    public SecurityPolicyRuleRedirectOptionsConfig getExceedRedirectConfig() {
        return exceedRedirectConfig;
    }

    public void setExceedRedirectConfig(SecurityPolicyRuleRedirectOptionsConfig exceedRedirectConfig) {
        this.exceedRedirectConfig = exceedRedirectConfig;
    }

    @Override
    public void copyFrom(SecurityPolicyRuleRateLimitOptions model) {
        setBanDurationSec(model.getBanDurationSec());
        setConformAction(model.getConformAction());
        setExceedAction(model.getExceedAction());
        setEnforceOnKey(model.getEnforceOnKey());
        setEnforceOnKeyName(model.getEnforceOnKeyName());

        setBanThreshold(null);
        if (model.hasBanThreshold()) {
            SecurityPolicyRuleRateLimitOptionsThreshold banThreshold = model.getBanThreshold();
            SecurityPolicyRuleRateLimitOptionsThresholdConfig banThresholdConfig = newSubresource(
                SecurityPolicyRuleRateLimitOptionsThresholdConfig.class);
            banThresholdConfig.copyFrom(banThreshold);
            setBanThreshold(banThresholdConfig);
        }

        setRateLimitThreshold(null);
        if (model.hasRateLimitThreshold()) {
            SecurityPolicyRuleRateLimitOptionsThreshold rateLimitThreshold = model.getRateLimitThreshold();
            SecurityPolicyRuleRateLimitOptionsThresholdConfig rateLimitThresholdConfig = newSubresource(
                SecurityPolicyRuleRateLimitOptionsThresholdConfig.class);
            rateLimitThresholdConfig.copyFrom(rateLimitThreshold);
            setRateLimitThreshold(rateLimitThresholdConfig);
        }

        setExceedRedirectConfig(null);
        if (model.hasExceedRedirectOptions()) {
            SecurityPolicyRuleRedirectOptions exceedRedirectOptions = model.getExceedRedirectOptions();
            SecurityPolicyRuleRedirectOptionsConfig exceedRedirectOptionsConfig = newSubresource(
                SecurityPolicyRuleRedirectOptionsConfig.class);
            exceedRedirectOptionsConfig.copyFrom(exceedRedirectOptions);
            setExceedRedirectConfig(exceedRedirectOptionsConfig);
        }
    }

    @Override
    public String primaryKey() {
        return "";
    }

    SecurityPolicyRuleRateLimitOptions toSecurityPolicyRuleRateLimitOptions() {
        SecurityPolicyRuleRateLimitOptions.Builder builder = SecurityPolicyRuleRateLimitOptions.newBuilder();

        if (getBanThreshold() != null) {
            builder.setBanThreshold(getBanThreshold().toSecurityPolicyRuleRateLimitOptionsThreshold());
        }

        if (getBanDurationSec() != null) {
            builder.setBanDurationSec(getBanDurationSec());
        }

        if (getRateLimitThreshold() != null) {
            builder.setRateLimitThreshold(getRateLimitThreshold().toSecurityPolicyRuleRateLimitOptionsThreshold());
        }

        if (getConformAction() != null) {
            builder.setConformAction(getConformAction());
        }

        if (getEnforceOnKey() != null) {
            builder.setEnforceOnKey(getEnforceOnKey());
        }

        if (getExceedAction() != null) {
            builder.setExceedAction(getExceedAction());
        }

        if (getEnforceOnKeyName() != null) {
            builder.setEnforceOnKeyName(getEnforceOnKeyName());
        }

        if (getExceedRedirectConfig() != null) {
            builder.setExceedRedirectOptions(getExceedRedirectConfig().toSecurityPolicyRuleRedirectOptions());
        }

        return builder.build();
    }

    @Override
    public List<ValidationError> validate(Set<String> configuredFields) {
        List<ValidationError> errors = new ArrayList<>();

        SecurityPolicyRule rule = (SecurityPolicyRule) parent();

        if (!rule.getAction().matches("rate_based_ban|throttle")) {
            errors.add(new ValidationError(
                this,
                null,
                "'rate-limit-config' is only valid when 'action' is set to 'rate_based_ban' or 'throttle'."
            ));
        }

        if ("rate_based_ban".equals(rule.getAction())) {
            if (getBanThreshold() == null) {
                errors.add(new ValidationError(
                    this,
                    null,
                    "When 'action' is set to 'rate_based_ban', 'ban-threshold' is required."
                ));
            }

            if (configuredFields.contains("ban-duration-sec") && getBanDurationSec() == null) {
                errors.add(new ValidationError(
                    this,
                    null,
                    "When 'action' is set to 'rate_based_ban', 'ban-duration-sec' is required."
                ));
            }
        } else {
            if (getBanThreshold() != null) {
                errors.add(new ValidationError(
                    this,
                    null,
                    "'ban-threshold' is only valid when 'action' is set to 'rate_based_ban'."
                ));
            }

            if (configuredFields.contains("ban-duration-sec") && getBanDurationSec() != null) {
                errors.add(new ValidationError(
                    this,
                    null,
                    "'ban-duration-sec' is only valid when 'action' is set to 'rate_based_ban'."
                ));
            }
        }

        if ("redirect".equals(getExceedAction()) && getExceedRedirectConfig() == null) {
            errors.add(new ValidationError(
                this,
                null,
                "When 'exceed-action' is set to redirect, exceed-redirect-options is required."
            ));
        } else if (!"redirect".equals(getExceedAction()) && getExceedRedirectConfig() != null) {
            errors.add(new ValidationError(
                this,
                null,
                "'exceed-redirect-options' is only valid when 'exceed-action' is set to 'redirect'."
            ));
        }

        return errors;
    }
}
