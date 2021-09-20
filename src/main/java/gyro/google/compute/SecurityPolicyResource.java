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

import com.google.api.services.compute.Compute;
import com.google.cloud.compute.v1.Operation;
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
 */

@Type("compute-security-policy")
public class SecurityPolicyResource extends ComputeResource implements Copyable<SecurityPolicy> {

    private String name;
    private String description;
    private List<SecurityPolicyRule> rule;
    private SecurityPolicyRule defaultRule;
    private String fingerprint;

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

    @Override
    protected boolean doRefresh() throws Exception {
        Compute client = createComputeClient();

        SecurityPolicy securityPolicy = client.securityPolicies().get(getProjectId(), getName()).execute();
        copyFrom(securityPolicy);

        return true;
    }

    @Override
    public void doUpdate(
        GyroUI ui, State state, Resource current, Set<String> changedFieldNames) throws Exception {
        Compute client = createComputeClient();

        Operation operation = client.securityPolicies().patch(getProjectId(), getName(), toSecurityPolicy()).execute();
        waitForCompletion(client, operation);

        refresh();
    }

    @Override
    protected void doCreate(GyroUI ui, State state) throws Exception {
        Compute client = createComputeClient();

        Operation operation = client.securityPolicies().insert(getProjectId(), toSecurityPolicy()).execute();
        waitForCompletion(client, operation);

        refresh();
    }

    @Override
    public void doDelete(GyroUI ui, State state) throws Exception {
        Compute client = createComputeClient();

        Operation operation = client.securityPolicies().delete(getProjectId(), getName()).execute();
        waitForCompletion(client, operation);
    }

    @Override
    public void copyFrom(SecurityPolicy securityPolicy) {
        setName(securityPolicy.getName());
        setDescription(securityPolicy.getDescription());
        setSelfLink(securityPolicy.getSelfLink());
        setFingerprint(securityPolicy.getFingerprint());
        getRule().clear();
        securityPolicy.getRules().forEach(rule -> {
            SecurityPolicyRule securityPolicyRule = newSubresource(SecurityPolicyRule.class);
            securityPolicyRule.copyFrom(rule);

            if (rule.getPriority() == 2147483647) {
                setDefaultRule(securityPolicyRule);
            } else {
                getRule().add(securityPolicyRule);
            }
        });
    }

    private SecurityPolicy toSecurityPolicy() {
        SecurityPolicy securityPolicy = new SecurityPolicy();
        securityPolicy.setName(getName());
        securityPolicy.setDescription(getDescription());
        securityPolicy.setSelfLink(getSelfLink());
        securityPolicy.setFingerprint(getFingerprint());

        return securityPolicy;
    }
}
