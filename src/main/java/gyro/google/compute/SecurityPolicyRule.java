/*
 * Copyright 2020, Perfect Sense, Inc.
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

import com.google.cloud.compute.v1.AddRuleSecurityPolicyRequest;
import com.google.cloud.compute.v1.Operation;
import com.google.cloud.compute.v1.PatchRuleSecurityPolicyRequest;
import com.google.cloud.compute.v1.RemoveRuleSecurityPolicyRequest;
import com.google.cloud.compute.v1.SecurityPoliciesClient;
import gyro.core.GyroUI;
import gyro.core.resource.Resource;
import gyro.core.resource.Updatable;
import gyro.core.scope.State;
import gyro.core.validation.Required;
import gyro.core.validation.ValidStrings;
import gyro.core.validation.ValidationError;
import gyro.google.Copyable;
import org.apache.commons.lang3.StringUtils;

public class SecurityPolicyRule extends ComputeResource
    implements Copyable<com.google.cloud.compute.v1.SecurityPolicyRule> {

    private String description;
    private Integer priority;
    private String action;
    private Boolean preview;
    private SecurityPolicyRuleMatcher match;
    private SecurityPolicyRuleHttpHeaderActionConfig headerAction;
    private SecurityPolicyRuleRedirectOptionsConfig redirectConfig;
    private SecurityPolicyRuleRateLimitOptionsConfig rateLimitConfig;

    /**
     * The description of the security policy rule.
     */
    @Updatable
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * The priority of the security policy rule.
     */
    @Required
    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    /**
     * The action to take for this rule.
     */
    @Updatable
    @Required
    @ValidStrings({ "allow", "deny(403)", "deny(404)", "deny(502)", "rate_based_ban", "redirect", "throttle" })
    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    /**
     * The preview flag indicates that this rule is not enforced. Defaults to ``false``.
     */
    @Updatable
    public Boolean getPreview() {
        if (preview == null) {
            preview = false;
        }

        return preview;
    }

    public void setPreview(Boolean preview) {
        this.preview = preview;
    }

    /**
     * The match condition that incoming traffic is evaluated against for this rule.
     *
     * @subresource gyro.google.compute.SecurityPolicyRuleMatcher
     */
    @Updatable
    @Required
    public SecurityPolicyRuleMatcher getMatch() {
        return match;
    }

    public void setMatch(SecurityPolicyRuleMatcher match) {
        this.match = match;
    }

    /**
     * The header action to take for this rule.
     *
     * @subresource gyro.google.compute.SecurityPolicyRuleHttpHeaderActionConfig
     */
    @Updatable
    public SecurityPolicyRuleHttpHeaderActionConfig getHeaderAction() {
        return headerAction;
    }

    public void setHeaderAction(SecurityPolicyRuleHttpHeaderActionConfig headerAction) {
        this.headerAction = headerAction;
    }

    /**
     * The redirect configuration for this rule.
     *
     * @subresource gyro.google.compute.SecurityPolicyRuleRedirectOptionsConfig
     */
    @Updatable
    public SecurityPolicyRuleRedirectOptionsConfig getRedirectConfig() {
        return redirectConfig;
    }

    public void setRedirectConfig(SecurityPolicyRuleRedirectOptionsConfig redirectConfig) {
        this.redirectConfig = redirectConfig;
    }

    /**
     * The rate limit configuration for this rule.
     *
     * @subresource gyro.google.compute.SecurityPolicyRuleRateLimitOptionsConfig
     */
    @Updatable
    public SecurityPolicyRuleRateLimitOptionsConfig getRateLimitConfig() {
        return rateLimitConfig;
    }

    public void setRateLimitConfig(SecurityPolicyRuleRateLimitOptionsConfig rateLimitConfig) {
        this.rateLimitConfig = rateLimitConfig;
    }

    @Override
    public String primaryKey() {
        return String.format("%swith priority %s",
            (StringUtils.isBlank(getDescription()) ? "" : String.format("(%s) ", getDescription())), getPriority());
    }

    com.google.cloud.compute.v1.SecurityPolicyRule toSecurityPolicyRule() {
        com.google.cloud.compute.v1.SecurityPolicyRule.Builder builder =
            com.google.cloud.compute.v1.SecurityPolicyRule.newBuilder();

        if (getAction() != null) {
            builder.setAction(getAction());
        }

        if (getPriority() != null) {
            builder.setPriority(getPriority());
        }

        if (getMatch() != null) {
            builder.setMatch(getMatch().toSecurityPolicyRuleMatcher());
        }

        if (getDescription() != null) {
            builder.setDescription(getDescription());
        }

        if (getPreview() != null) {
            builder.setPreview(getPreview());
        }

        if (getHeaderAction() != null) {
            builder.setHeaderAction(getHeaderAction().toSecurityPolicyRuleHttpHeaderAction());
        }

        if (getRedirectConfig() != null) {
            builder.setRedirectOptions(getRedirectConfig().toSecurityPolicyRuleRedirectOptions());
        }

        if (getRateLimitConfig() != null) {
            builder.setRateLimitOptions(getRateLimitConfig().toSecurityPolicyRuleRateLimitOptions());
        }

        return builder.build();
    }

    @Override
    public void copyFrom(com.google.cloud.compute.v1.SecurityPolicyRule model) {
        if (model.hasPriority()) {
            setPriority(model.getPriority());
        }

        if (model.hasDescription()) {
            setDescription(model.getDescription());
        }

        if (model.hasAction()) {
            setAction(model.getAction());
        }

        if (model.hasPriority()) {
            setPreview(model.getPreview());
        }

        setMatch(null);
        if (model.hasMatch()) {
            SecurityPolicyRuleMatcher matcher = newSubresource(SecurityPolicyRuleMatcher.class);
            matcher.copyFrom(model.getMatch());

            setMatch(matcher);
        }

        setHeaderAction(null);
        if (model.hasHeaderAction()) {
            SecurityPolicyRuleHttpHeaderActionConfig headerActionConfig = newSubresource(
                SecurityPolicyRuleHttpHeaderActionConfig.class);
            headerActionConfig.copyFrom(model.getHeaderAction());

            setHeaderAction(headerActionConfig);
        }

        setRedirectConfig(null);
        if (model.hasRedirectOptions()) {
            SecurityPolicyRuleRedirectOptionsConfig redirectOptionsConfig = newSubresource(
                SecurityPolicyRuleRedirectOptionsConfig.class);
            redirectOptionsConfig.copyFrom(model.getRedirectOptions());

            setRedirectConfig(redirectOptionsConfig);
        }

        setRateLimitConfig(null);
        if (model.hasRateLimitOptions()) {
            SecurityPolicyRuleRateLimitOptionsConfig rateLimitOptionsConfig = newSubresource(
                SecurityPolicyRuleRateLimitOptionsConfig.class);
            rateLimitOptionsConfig.copyFrom(model.getRateLimitOptions());

            setRateLimitConfig(rateLimitOptionsConfig);
        }
    }

    @Override
    protected boolean doRefresh() throws Exception {
        return true;
    }

    @Override
    protected void doCreate(GyroUI ui, State state) throws Exception {
        try (SecurityPoliciesClient client = createClient(SecurityPoliciesClient.class)) {
            SecurityPolicyResource securityPolicyResource = (SecurityPolicyResource) this.parentResource();
            Operation operation = client.addRuleCallable().call(AddRuleSecurityPolicyRequest.newBuilder()
                .setProject(getProjectId())
                .setSecurityPolicy(securityPolicyResource.getName())
                .setSecurityPolicyRuleResource(toSecurityPolicyRule())
                .build());

            waitForCompletion(operation);
        }
    }

    @Override
    public void doUpdate(
        GyroUI ui, State state, Resource current, Set<String> changedFieldNames) throws Exception {
        try (SecurityPoliciesClient client = createClient(SecurityPoliciesClient.class)) {
            com.google.cloud.compute.v1.SecurityPolicyRule.Builder builder = com.google.cloud.compute.v1.SecurityPolicyRule
                .newBuilder();

            if (changedFieldNames.contains("description")) {
                builder.setDescription(getDescription());
            }

            if (changedFieldNames.contains("action")) {
                builder.setAction(getAction());
            }

            if (changedFieldNames.contains("preview")) {
                builder.setPreview(getPreview());
            }

            if (changedFieldNames.contains("match")) {
                builder.setMatch(getMatch().toSecurityPolicyRuleMatcher());
            }

            if (changedFieldNames.contains("header-action")) {
                if (getHeaderAction() == null) {
                    builder.clearHeaderAction();
                } else {
                    builder.setHeaderAction(getHeaderAction().toSecurityPolicyRuleHttpHeaderAction());
                }
            }

            if (changedFieldNames.contains("redirect-config")) {
                if (getRedirectConfig() == null) {
                    builder.clearRedirectOptions();
                } else {
                    builder.setRedirectOptions(getRedirectConfig().toSecurityPolicyRuleRedirectOptions());
                }
            }

            if (changedFieldNames.contains("rate-limit-config")) {
                if (getRateLimitConfig() == null) {
                    builder.clearRateLimitOptions();
                } else {
                    builder.setRateLimitOptions(getRateLimitConfig().toSecurityPolicyRuleRateLimitOptions());
                }
            }

            SecurityPolicyResource securityPolicyResource = (SecurityPolicyResource) this.parentResource();
            Operation operation = client.patchRuleCallable().call(
                PatchRuleSecurityPolicyRequest.newBuilder()
                    .setProject(getProjectId())
                    .setSecurityPolicy(securityPolicyResource.getName())
                    .setSecurityPolicyRuleResource(toSecurityPolicyRule())
                    .setPriority(getPriority())
                    .build());

            waitForCompletion(operation);
        }
        refresh();
    }

    @Override
    public void doDelete(GyroUI ui, State state) throws Exception {
        try (SecurityPoliciesClient client = createClient(SecurityPoliciesClient.class)) {
            SecurityPolicyResource securityPolicyResource = (SecurityPolicyResource) this.parentResource();
            if (getPriority() != 2147483647) {
                Operation operation = client.removeRuleCallable().call(RemoveRuleSecurityPolicyRequest.newBuilder()
                    .setProject(getProjectId())
                    .setSecurityPolicy(securityPolicyResource.getName())
                    .setPriority(getPriority())
                    .build());

                waitForCompletion(operation);
            }
        }
    }

    @Override
    public List<ValidationError> validate(Set<String> configuredFields) {
        List<ValidationError> errors = new ArrayList<>();

        if (!"allow".equals(getAction()) && getHeaderAction() != null) {
            errors.add(new ValidationError(
                this,
                null,
                "'header-action' can only be set when 'action' is set to 'allow'."));
        }

        if (!"redirect".equals(getAction()) && getRedirectConfig() != null) {
            errors.add(new ValidationError(
                this,
                null,
                "'redirect-config' can only be set when 'action' is set to 'redirect'."));
        } else if ("redirect".equals(getAction()) && getRedirectConfig() == null) {
            errors.add(new ValidationError(
                this,
                null,
                "'redirect-config' is required when 'action' is set to 'redirect'."));
        }

        if ((!"rate_based_ban".equals(getAction()) && !"throttle".equals(getAction())) && getRateLimitConfig() != null) {
            errors.add(new ValidationError(
                this,
                null,
                "'rate-limit-config' can only be set when 'action' is set to 'rate_based_ban' or 'throttle'."));
        } else if (getAction().matches("rate_based_ban|throttle") && getRateLimitConfig() == null) {
            errors.add(new ValidationError(
                this,
                null,
                "'rate-limit-config' is required when 'action' is set to 'rate_based_ban' or 'throttle'."));
        }

        return errors;
    }
}
