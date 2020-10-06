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

import gyro.core.resource.Diffable;
import gyro.core.resource.Updatable;
import gyro.core.validation.Required;
import gyro.core.validation.ValidStrings;
import gyro.google.Copyable;

public class SecurityPolicyRuleMatcher extends Diffable
    implements Copyable<com.google.api.services.compute.model.SecurityPolicyRuleMatcher> {

    private SecurityPolicyRuleMatcherConfig config;
    private String versionedExpr;

    /**
     * The configuration for the security policy rule matcher. (Required)
     *
     * @subresource gyro.google.compute.SecurityPolicyRuleMatcherConfig
     */
    @Required
    @Updatable
    public SecurityPolicyRuleMatcherConfig getConfig() {
        return config;
    }

    public void setConfig(SecurityPolicyRuleMatcherConfig config) {
        this.config = config;
    }

    /**
     * The versioned expression of the security policy rule matcher. Currently only supported value is ``SRC_IPS_V1``. (Required)
     */
    @Required
    @Updatable
    @ValidStrings("SRC_IPS_V1")
    public String getVersionedExpr() {
        return versionedExpr;
    }

    public void setVersionedExpr(String versionedExpr) {
        this.versionedExpr = versionedExpr;
    }

    @Override
    public String primaryKey() {
        return "";
    }

    @Override
    public void copyFrom(com.google.api.services.compute.model.SecurityPolicyRuleMatcher matcher) {
        setVersionedExpr(matcher.getVersionedExpr());
        SecurityPolicyRuleMatcherConfig config = newSubresource(SecurityPolicyRuleMatcherConfig.class);
        config.copyFrom(matcher.getConfig());
        setConfig(config);
    }

    public com.google.api.services.compute.model.SecurityPolicyRuleMatcher toSecurityPolicyRuleMatcher() {
        com.google.api.services.compute.model.SecurityPolicyRuleMatcher matcher = new com.google.api.services.compute.model.SecurityPolicyRuleMatcher();
        matcher.setVersionedExpr(getVersionedExpr());
        matcher.setConfig(getConfig().toSecurityPolicyRuleMatcherConfig());

        return matcher;
    }
}
