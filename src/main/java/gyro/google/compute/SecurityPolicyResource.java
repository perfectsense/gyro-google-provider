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

import com.google.api.gax.rpc.NotFoundException;
import com.google.cloud.compute.v1.DeleteSecurityPolicyRequest;
import com.google.cloud.compute.v1.GetSecurityPolicyRequest;
import com.google.cloud.compute.v1.InsertSecurityPolicyRequest;
import com.google.cloud.compute.v1.Operation;
import com.google.cloud.compute.v1.PatchSecurityPolicyRequest;
import com.google.cloud.compute.v1.SecurityPoliciesClient;
import com.google.cloud.compute.v1.SecurityPolicy;
import gyro.core.GyroUI;
import gyro.core.Type;
import gyro.core.resource.Id;
import gyro.core.resource.Output;
import gyro.core.resource.Resource;
import gyro.core.resource.Updatable;
import gyro.core.scope.State;
import gyro.core.validation.Regex;
import gyro.core.validation.Required;
import gyro.core.validation.ValidationError;
import gyro.google.Copyable;

/**
 * Creates a security policy.
 *
 * Example
 * -------
 *
 * .. code-block:: gyro
 *
 *  google::compute-security-policy security-policy-example
 *      name: "security-policy-example"
 *      description: "security-policy-example-desc"
 *
 *      rule
 *          description: "security-policy-example-rule-desc"
 *          priority: 2
 *          action: 'allow'
 *          preview: true
 *
 *          match
 *              versioned-expr: 'SRC_IPS_V1'
 *              config
 *                  src-ip-ranges: ['*']
 *              end
 *          end
 *      end
 *  end
 *
 * Example full scope
 * ------------------
 *
 * .. code-block:: gyro
 *
 *  google::compute-security-policy security-policy-example
 *     name: "security-policy-example"
 *     description: "security-policy-example-desc"
 *
 *     adaptive-protection-config
 *         enabled: true
 *         rule-visibility: 'STANDARD'
 *     end
 *
 *     rule
 *         description: "allow-rule-match-ip-example"
 *         priority: 2
 *         action: 'allow'
 *         preview: true
 *
 *         match
 *             versioned-expr: 'SRC_IPS_V1'
 *             config
 *                 src-ip-ranges: ['1.1.1.0/24']
 *             end
 *         end
 *     end
 *
 *     rule
 *         description: "allow-rule-match-expression-example"
 *         priority: 3
 *         action: 'allow'
 *
 *         match
 *             expression-config
 *                 expression: "origin.asn == 1234"
 *             end
 *         end
 *     end
 *
 *     rule
 *         description: "allow-rule-match-expression-with-headers-example"
 *         priority: 4
 *         action: 'allow'
 *
 *         match
 *             expression-config
 *                 expression: "origin.asn == 1234"
 *             end
 *         end
 *
 *         header-action
 *             headers: {
 *                 'X-Goog-Test' : 'test',
 *                 'X-Goog-Test2' : 'test2'
 *             }
 *         end
 *     end
 *
 *     rule
 *         description: "redirect-rule-google-captcha-example"
 *         priority: 5
 *         action: 'redirect'
 *
 *         match
 *             expression-config
 *                 expression: "origin.asn == 1234"
 *             end
 *         end
 *
 *         redirect-config
 *             type: 'GOOGLE_RECAPTCHA'
 *         end
 *     end
 *
 *     rule
 *         description: "redirect-rule-external-address-example"
 *         priority: 6
 *         action: 'redirect'
 *
 *         match
 *             expression-config
 *                 expression: "origin.asn == 1234"
 *             end
 *         end
 *
 *         redirect-config
 *             type: 'EXTERNAL_302'
 *             target: 'https://www.google.com'
 *         end
 *     end
 *
 *     rule
 *         description: "throttle-rule-example"
 *         priority: 7
 *         action: 'throttle'
 *
 *         match
 *             versioned-expr: 'SRC_IPS_V1'
 *             config
 *                 src-ip-ranges: ['1.1.1.0/24']
 *             end
 *         end
 *
 *         rate-limit-config
 *             rate-limit-threshold
 *                 count: 10
 *                 interval-sec: 120
 *             end
 *
 *             exceed-action: 'deny(403)'
 *         end
 *     end
 *
 *     rule
 *         description: "rate-based-ban-rule-example"
 *         priority: 8
 *         action: 'rate_based_ban'
 *
 *         match
 *             versioned-expr: 'SRC_IPS_V1'
 *             config
 *                 src-ip-ranges: ['1.1.1.0/24']
 *             end
 *         end
 *
 *         rate-limit-config
 *             rate-limit-threshold
 *                 count: 10
 *                 interval-sec: 60
 *             end
 *
 *             ban-threshold
 *                 count: 10
 *                 interval-sec: 60
 *             end
 *
 *             ban-duration-sec: 120
 *
 *             exceed-action: 'deny(429)'
 *         end
 *     end
 * end
 *
 */

@Type("compute-security-policy")
public class SecurityPolicyResource extends ComputeResource implements Copyable<SecurityPolicy> {

    private String name;
    private String description;
    private List<SecurityPolicyRule> rule;
    private SecurityPolicyRule defaultRule;
    private String fingerprint;
    private SecurityPolicyAdaptiveProtection adaptiveProtectionConfig;
    private String securityPolicyType;

    // Not yet supported in UI
    private SecurityPolicyAdvancedOptions advancedOptionsConfig;

    // Read-only
    private String selfLink;

    /**
     * The name of the security policy.
     */
    @Required
    @Regex(value = "(?:(?:[-a-z0-9]{1,63}\\.)*(?:[a-z](?:[-a-z0-9]{0,61}[a-z0-9])?):)?(?:[0-9]{1,19}|(?:[a-z0-9](?:[-a-z0-9]{0,61}[a-z0-9])?))", message = "a string 1-63 characters long and the first character must be a lowercase letter, and all following characters must be a dash, lowercase letter, or digit, except the last character, which cannot be a dash")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * The description of the security policy.
     */
    @Updatable
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * The fully-qualified URL of the security policy.
     */
    @Id
    @Output
    public String getSelfLink() {
        return selfLink;
    }

    public void setSelfLink(String selfLink) {
        this.selfLink = selfLink;
    }

    /**
     * The rule of the security policy.
     *
     * @subresource gyro.google.compute.SecurityPolicyRule
     */
    @Updatable
    public List<SecurityPolicyRule> getRule() {
        if (rule == null) {
            rule = new ArrayList<>();
        }
        return rule;
    }

    public void setRule(List<SecurityPolicyRule> rule) {
        this.rule = rule;
    }

    /**
     * The fingerprint for this security policy.
     */
    public String getFingerprint() {
        return fingerprint;
    }

    public void setFingerprint(String fingerprint) {
        this.fingerprint = fingerprint;
    }

    /**
     * Adaptive protection config for this security policy.
     *
     * @subresource gyro.google.compute.SecurityPolicyAdaptiveProtection
     */
    @Updatable
    public SecurityPolicyAdaptiveProtection getAdaptiveProtectionConfig() {
        return adaptiveProtectionConfig;
    }

    public void setAdaptiveProtectionConfig(SecurityPolicyAdaptiveProtection adaptiveProtectionConfig) {
        this.adaptiveProtectionConfig = adaptiveProtectionConfig;
    }

    /**
     * Advanced option config for this security policy.
     *
     * @subresource gyro.google.compute.SecurityPolicyAdvancedOptions
     */
    @Updatable
    public SecurityPolicyAdvancedOptions getAdvancedOptionsConfig() {
        return advancedOptionsConfig;
    }

    public void setAdvancedOptionsConfig(SecurityPolicyAdvancedOptions advancedOptionsConfig) {
        this.advancedOptionsConfig = advancedOptionsConfig;
    }

    /**
     * The default rule for this security policy.
     *
     * @subresource gyro.google.compute.SecurityPolicyRule
     */
    @Output
    public SecurityPolicyRule getDefaultRule() {
        return defaultRule;
    }

    public void setDefaultRule(SecurityPolicyRule defaultRule) {
        this.defaultRule = defaultRule;
    }

    /**
     * The type of the security policy.
     */
    @Required
    public String getSecurityPolicyType() {
        return securityPolicyType;
    }

    public void setSecurityPolicyType(String securityPolicyType) {
        this.securityPolicyType = securityPolicyType;
    }

    @Override
    protected boolean doRefresh() throws Exception {
        try (SecurityPoliciesClient client = createClient(SecurityPoliciesClient.class)) {

            SecurityPolicy securityPolicy = getSecurityPolicy(client);

            if (securityPolicy == null) {
                return false;
            }

            copyFrom(securityPolicy);

            return true;
        }
    }

    @Override
    public void doUpdate(GyroUI ui, State state, Resource current, Set<String> changedFieldNames) throws Exception {
        try (SecurityPoliciesClient client = createClient(SecurityPoliciesClient.class)) {
            Operation operation = client.patchCallable().call(PatchSecurityPolicyRequest.newBuilder()
                .setProject(getProjectId())
                .setSecurityPolicy(getName())
                .setSecurityPolicyResource(toSecurityPolicy())
                .build());
            waitForCompletion(operation);
        }

        refresh();
    }

    @Override
    protected void doCreate(GyroUI ui, State state) throws Exception {
        try (SecurityPoliciesClient client = createClient(SecurityPoliciesClient.class)) {
            Operation operation = client.insertCallable().call(InsertSecurityPolicyRequest.newBuilder()
                .setProject(getProjectId())
                .setSecurityPolicyResource(toSecurityPolicy())
                .build());
            waitForCompletion(operation);
        }

        refresh();
    }

    @Override
    public void doDelete(GyroUI ui, State state) throws Exception {
        try (SecurityPoliciesClient client = createClient(SecurityPoliciesClient.class)) {
            Operation operation = client.deleteCallable().call(DeleteSecurityPolicyRequest.newBuilder()
                .setProject(getProjectId())
                .setSecurityPolicy(getName())
                .build());
            waitForCompletion(operation);
        }
    }

    @Override
    public void copyFrom(SecurityPolicy model) {
        setName(model.getName());
        setDescription(model.getDescription());
        setSelfLink(model.getSelfLink());
        setFingerprint(model.getFingerprint());
        setSecurityPolicyType(model.getType());

        getRule().clear();
        model.getRulesList().forEach(rule -> {
            SecurityPolicyRule securityPolicyRule = newSubresource(SecurityPolicyRule.class);
            securityPolicyRule.copyFrom(rule);

            if (rule.getPriority() == 2147483647) {
                setDefaultRule(securityPolicyRule);
            } else {
                getRule().add(securityPolicyRule);
            }
        });

        setAdaptiveProtectionConfig(null);
        SecurityPolicyAdaptiveProtection adaptiveProtection = newSubresource(
            SecurityPolicyAdaptiveProtection.class);
        if (model.hasAdaptiveProtectionConfig()) {
            adaptiveProtection.copyFrom(model.getAdaptiveProtectionConfig());
            setAdaptiveProtectionConfig(adaptiveProtection);
        }

        setAdvancedOptionsConfig(null);
        SecurityPolicyAdvancedOptions advancedOptions = newSubresource(
            SecurityPolicyAdvancedOptions.class);
        if (model.hasAdvancedOptionsConfig()) {
            advancedOptions.copyFrom(model.getAdvancedOptionsConfig());
            setAdvancedOptionsConfig(advancedOptions);
        }
    }

    private SecurityPolicy toSecurityPolicy() {
        SecurityPolicy.Builder builder = SecurityPolicy.newBuilder();
        builder.setName(getName()).setType(getSecurityPolicyType());

        if (getDescription() != null) {
            builder.setDescription(getDescription());
        }

        if (getSelfLink() != null) {
            builder.setSelfLink(getSelfLink());
        }

        if (getFingerprint() != null) {
            builder.setFingerprint(getFingerprint());
        }

        if (getAdaptiveProtectionConfig() != null) {
            builder.setAdaptiveProtectionConfig(getAdaptiveProtectionConfig().toAdaptiveProtection());
        }

        if (getAdvancedOptionsConfig() != null) {
            builder.setAdvancedOptionsConfig(getAdvancedOptionsConfig().toAdvancedOptions());
        }

        return builder.build();
    }

    private SecurityPolicy getSecurityPolicy(SecurityPoliciesClient client) {
        SecurityPolicy route = null;

        try {
            route = client.get(GetSecurityPolicyRequest.newBuilder().setProject(getProjectId())
                .setSecurityPolicy(getName()).build());

        } catch (NotFoundException ex) {
            // ignore
        }

        return route;
    }

    @Override
    public List<ValidationError> validate(Set<String> configuredFields) {
        List<ValidationError> errors = new ArrayList<>();

        if (configuredFields.contains("adaptive-protection-config")) {
            if (getAdaptiveProtectionConfig() != null && !getSecurityPolicyType().equals("CLOUD_ARMOR")) {
                errors.add(new ValidationError(this, "adaptive-protection-config", "'adaptive-protection-config' is not allowed when security-policy-type is not set to 'CLOUD_ARMOR'."));
            }
        }

        return errors;
    }
}
