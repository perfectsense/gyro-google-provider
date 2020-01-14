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

import java.util.Set;

import com.google.api.services.compute.Compute;
import com.google.api.services.compute.model.Operation;
import gyro.core.GyroUI;
import gyro.core.resource.Id;
import gyro.core.resource.Resource;
import gyro.core.resource.Updatable;
import gyro.core.scope.State;
import gyro.core.validation.Required;
import gyro.core.validation.ValidStrings;
import gyro.google.Copyable;

public class SecurityPolicyRule extends ComputeResource
    implements Copyable<com.google.api.services.compute.model.SecurityPolicyRule> {

    private String description;
    private Integer priority;
    private String action;
    private Boolean preview;
    private SecurityPolicyRuleMatcher match;

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
    @Id
    @Updatable
    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    /**
     * The action to take for this rule. (Required)
     */
    @Updatable
    @Required
    @ValidStrings({ "allow", "deny(403)", "deny(404)", "deny(502)" })
    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    /**
     * The preview flag indicates that this rule is not enforced.
     */
    @Updatable
    public Boolean getPreview() {
        return preview;
    }

    public void setPreview(Boolean preview) {
        this.preview = preview;
    }

    /**
     * The match for this rule. (Required).
     */
    @Updatable
    @Required
    public SecurityPolicyRuleMatcher getMatch() {
        return match;
    }

    public void setMatch(SecurityPolicyRuleMatcher match) {
        this.match = match;
    }

    @Override
    public String primaryKey() {
        return "security-policy-rule-" + getPriority();
    }

    com.google.api.services.compute.model.SecurityPolicyRule toSecurityPolicyRule() {
        com.google.api.services.compute.model.SecurityPolicyRule policyRule = new com.google.api.services.compute.model.SecurityPolicyRule();
        policyRule.setAction(getAction());
        policyRule.setDescription(getDescription());
        policyRule.setPriority(getPriority());
        policyRule.setPreview(getPreview());

        if (getMatch() != null) {
            policyRule.setMatch(match.toSecurityPolicyRuleMatcher());
        }

        return policyRule;
    }

    @Override
    public void copyFrom(com.google.api.services.compute.model.SecurityPolicyRule securityPolicyRule) {
        setPriority(securityPolicyRule.getPriority());
        setDescription(securityPolicyRule.getDescription());
        setAction(securityPolicyRule.getAction());
        setPreview(securityPolicyRule.getPreview());

        if (securityPolicyRule.getMatch() != null) {
            SecurityPolicyRuleMatcher matcher = newSubresource(SecurityPolicyRuleMatcher.class);
            matcher.copyFrom(securityPolicyRule.getMatch());
            setMatch(matcher);
        }
    }

    @Override
    protected boolean doRefresh() throws Exception {
        Compute client = createComputeClient();
        SecurityPolicyResource securityPolicyResource = (SecurityPolicyResource) this.parentResource();
        com.google.api.services.compute.model.SecurityPolicyRule securityPolicyRule = client.securityPolicies()
            .getRule(getProjectId(), securityPolicyResource.getName()).set("priority", getPriority())
            .execute();
        copyFrom(securityPolicyRule);

        return true;
    }

    @Override
    protected void doCreate(GyroUI ui, State state) throws Exception {
        Compute client = createComputeClient();

        SecurityPolicyResource securityPolicyResource = (SecurityPolicyResource) this.parentResource();
        Operation operation = client.securityPolicies()
            .addRule(getProjectId(), securityPolicyResource.getName(), toSecurityPolicyRule())
            .execute();
        waitForCompletion(client, operation);

        state.save();

        refresh();
    }

    @Override
    public void doUpdate(
        GyroUI ui, State state, Resource current, Set<String> changedFieldNames) throws Exception {
        Compute client = createComputeClient();

        com.google.api.services.compute.model.SecurityPolicyRule rule = new com.google.api.services.compute.model.SecurityPolicyRule();

        for (String changedFieldName : changedFieldNames) {
            if (changedFieldName.equals("description")) {
                rule.setDescription(getDescription());
            }

            if (changedFieldName.equals("preview")) {
                rule.setPreview(getPreview());
            }
        }

        if (getPriority() != null) {
            rule.setPriority(getPriority());
        }

        SecurityPolicyResource securityPolicyResource = (SecurityPolicyResource) this.parentResource();
        Operation operation = client.securityPolicies()
            .patchRule(getProjectId(), securityPolicyResource.getName(), rule)
            .execute();
        waitForCompletion(client, operation);

        refresh();
    }

    @Override
    public void doDelete(GyroUI ui, State state) throws Exception {
        Compute client = createComputeClient();

        SecurityPolicyResource securityPolicyResource = (SecurityPolicyResource) this.parentResource();
        Compute.SecurityPolicies.RemoveRule removeOperation = client.securityPolicies()
            .removeRule(getProjectId(), securityPolicyResource.getName());
        removeOperation.setPriority(getPriority());
        removeOperation.execute();
    }
}
