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

import gyro.core.resource.Diffable;
import gyro.core.resource.Updatable;
import gyro.core.validation.ConflictsWith;
import gyro.core.validation.DependsOn;
import gyro.core.validation.ValidStrings;
import gyro.core.validation.ValidationError;
import gyro.google.Copyable;

public class SecurityPolicyRuleMatcher extends Diffable
    implements Copyable<com.google.cloud.compute.v1.SecurityPolicyRuleMatcher> {

    private SecurityPolicyRuleMatcherConfig config;
    private String versionedExpr;

    private SecurityMatcherRuleExpressionConfig expressionConfig;

    /**
     * The configuration for the security policy rule matcher.
     *
     * @subresource gyro.google.compute.SecurityPolicyRuleMatcherConfig
     */
    @Updatable
    @DependsOn("versioned-expr")
    public SecurityPolicyRuleMatcherConfig getConfig() {
        return config;
    }

    public void setConfig(SecurityPolicyRuleMatcherConfig config) {
        this.config = config;
    }

    /**
     * The versioned expression of the security policy rule matcher. Currently only supported value is ``SRC_IPS_V1``.
     */
    @Updatable
    @DependsOn("config")
    @ValidStrings("SRC_IPS_V1")
    public String getVersionedExpr() {
        return versionedExpr;
    }

    public void setVersionedExpr(String versionedExpr) {
        this.versionedExpr = versionedExpr;
    }

    /**
     * The configuration for the security policy rule matcher expression.
     *
     * @subresource gyro.google.compute.SecurityMatcherRuleExpressionConfig
     */
    @Updatable
    @ConflictsWith({"config", "versioned-expr"})
    public SecurityMatcherRuleExpressionConfig getExpressionConfig() {
        return expressionConfig;
    }

    public void setExpressionConfig(SecurityMatcherRuleExpressionConfig expressionConfig) {
        this.expressionConfig = expressionConfig;
    }

    @Override
    public String primaryKey() {
        return "";
    }

    @Override
    public void copyFrom(com.google.cloud.compute.v1.SecurityPolicyRuleMatcher model) {
        if (model.hasVersionedExpr()) {
            setVersionedExpr(model.getVersionedExpr());
        }

        setConfig(null);
        if (model.hasConfig()) {
            SecurityPolicyRuleMatcherConfig config = newSubresource(SecurityPolicyRuleMatcherConfig.class);
            config.copyFrom(model.getConfig());

            setConfig(config);
        }

        if (model.hasExpr()) {
            SecurityMatcherRuleExpressionConfig exprConfig = newSubresource(SecurityMatcherRuleExpressionConfig.class);
            exprConfig.copyFrom(model.getExpr());

            setExpressionConfig(exprConfig);
        }
    }

    public com.google.cloud.compute.v1.SecurityPolicyRuleMatcher toSecurityPolicyRuleMatcher() {
        com.google.cloud.compute.v1.SecurityPolicyRuleMatcher.Builder builder = com.google.cloud.compute.v1.SecurityPolicyRuleMatcher
            .newBuilder();

        if (getConfig() != null) {
            builder.setVersionedExpr(getVersionedExpr());
            builder.setConfig(getConfig().toSecurityPolicyRuleMatcherConfig());
        }

        if (getExpressionConfig() != null) {
            builder.setExpr(getExpressionConfig().toExpr());
        }

        return builder.build();
    }

    @Override
    public List<ValidationError> validate(Set<String> configuredFields) {
        List<ValidationError> errors = new ArrayList<>();

        if (getConfig() == null && getExpressionConfig() == null) {
            errors.add(new ValidationError(this, null, "Either 'config' or 'expression-config' is required."));
        }

        return errors;
    }
}
