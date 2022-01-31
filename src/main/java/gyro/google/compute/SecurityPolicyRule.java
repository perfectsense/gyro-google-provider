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
import gyro.google.Copyable;

public class SecurityPolicyRule extends ComputeResource
    implements Copyable<com.google.cloud.compute.v1.SecurityPolicyRule> {

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

    @Override
    public String primaryKey() {
        return "with priority " + getPriority();
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

            SecurityPolicyResource securityPolicyResource = (SecurityPolicyResource) this.parentResource();
            Operation operation = client.patchRuleCallable().call(
                PatchRuleSecurityPolicyRequest.newBuilder()
                    .setProject(getProjectId())
                    .setSecurityPolicy(securityPolicyResource.getName())
                    .setSecurityPolicyRuleResource(toSecurityPolicyRule())
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
}
