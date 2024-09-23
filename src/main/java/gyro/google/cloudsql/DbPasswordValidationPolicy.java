/*
 * Copyright 2024, Brightspot.
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
package gyro.google.cloudsql;

import com.google.api.services.sqladmin.model.PasswordValidationPolicy;
import gyro.core.resource.Diffable;
import gyro.core.resource.Updatable;
import gyro.core.validation.Required;
import gyro.core.validation.ValidStrings;
import gyro.google.Copyable;

public class DbPasswordValidationPolicy extends Diffable implements Copyable<PasswordValidationPolicy> {

    private String complexity;
    private Boolean disallowUsernameSubstring;
    private Boolean enablePasswordPolicy;
    private Integer minLength;
    private String passwordChangeInterval;
    private Integer reuseInterval;

    /**
     * The complexity of the password.
     */
    @ValidStrings({ "COMPLEXITY_DEFAULT", "COMPLEXITY_UNSPECIFIED" })
    @Updatable
    public String getComplexity() {
        return complexity;
    }

    public void setComplexity(String complexity) {
        this.complexity = complexity;
    }

    /**
     * When set to ``true``, disallows username as a part of the password.
     */
    @Updatable
    public Boolean getDisallowUsernameSubstring() {
        return disallowUsernameSubstring;
    }

    public void setDisallowUsernameSubstring(Boolean disallowUsernameSubstring) {
        this.disallowUsernameSubstring = disallowUsernameSubstring;
    }

    /**
     * When set to ``true``, password policy is enabled.
     */
    @Updatable
    @Required
    public Boolean getEnablePasswordPolicy() {
        return enablePasswordPolicy;
    }

    public void setEnablePasswordPolicy(Boolean enablePasswordPolicy) {
        this.enablePasswordPolicy = enablePasswordPolicy;
    }

    /**
     * The minimum number of characters allowed.
     */
    @Updatable
    public Integer getMinLength() {
        return minLength;
    }

    public void setMinLength(Integer minLength) {
        this.minLength = minLength;
    }

    /**
     * The minimum interval after which the password can be changed.
     * Only for PostgresSQL.
     */
    @Updatable
    public String getPasswordChangeInterval() {
        return passwordChangeInterval;
    }

    public void setPasswordChangeInterval(String passwordChangeInterval) {
        this.passwordChangeInterval = passwordChangeInterval;
    }

    /**
     * The number of previous passwords that cannot be reused.
     */
    @Updatable
    public Integer getReuseInterval() {
        return reuseInterval;
    }

    public void setReuseInterval(Integer reuseInterval) {
        this.reuseInterval = reuseInterval;
    }

    @Override
    public void copyFrom(PasswordValidationPolicy model) throws Exception {
        setComplexity(model.getComplexity());
        setDisallowUsernameSubstring(model.getDisallowUsernameSubstring());
        setEnablePasswordPolicy(model.getEnablePasswordPolicy());
        setMinLength(model.getMinLength());
        setPasswordChangeInterval(model.getPasswordChangeInterval());
        setReuseInterval(model.getReuseInterval());
    }

    @Override
    public String primaryKey() {
        return "";
    }

    public PasswordValidationPolicy toPasswordValidationPolicy() {
        PasswordValidationPolicy policy = new PasswordValidationPolicy();
        policy.setEnablePasswordPolicy(getEnablePasswordPolicy());

        if (getComplexity() != null) {
            policy.setComplexity(getComplexity());
        }

        if (getDisallowUsernameSubstring() != null) {
            policy.setDisallowUsernameSubstring(getDisallowUsernameSubstring());
        }

        if (getMinLength() != null) {
            policy.setMinLength(getMinLength());
        }

        if (getPasswordChangeInterval() != null) {
            policy.setPasswordChangeInterval(getPasswordChangeInterval());
        }

        if (getReuseInterval() != null) {
            policy.setReuseInterval(getReuseInterval());
        }

        return policy;
    }
}
