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

package gyro.google.sqladmin;

import gyro.core.resource.Diffable;
import gyro.core.validation.ValidStrings;
import gyro.google.Copyable;

public class PasswordValidationPolicy extends Diffable
    implements Copyable<com.google.api.services.sqladmin.model.PasswordValidationPolicy> {

    private String complexity;

    private Boolean disallowCompromisedCredentials;

    private Boolean disallowUsernameSubstring;

    private Boolean enablePasswordPolicy;

    private Integer minLength;

    private String passwordChangeInterval;

    private Integer reuseInterval;

    /**
     * The complexity of the password.
     */
    @ValidStrings({
        "COMPLEXITY_UNSPECIFIED",
        "COMPLEXITY_DEFAULT"
    })
    public String getComplexity() {
        return complexity;
    }

    public void setComplexity(String complexity) {
        this.complexity = complexity;
    }

    /**
     * This field is deprecated and will be removed in a future version of the API.
     */
    public Boolean getDisallowCompromisedCredentials() {
        return disallowCompromisedCredentials;
    }

    public void setDisallowCompromisedCredentials(Boolean disallowCompromisedCredentials) {
        this.disallowCompromisedCredentials = disallowCompromisedCredentials;
    }

    /**
     * Disallow username as a part of the password.
     */
    public Boolean getDisallowUsernameSubstring() {
        return disallowUsernameSubstring;
    }

    public void setDisallowUsernameSubstring(Boolean disallowUsernameSubstring) {
        this.disallowUsernameSubstring = disallowUsernameSubstring;
    }

    /**
     * Whether the password policy is enabled or not.
     */
    public Boolean getEnablePasswordPolicy() {
        return enablePasswordPolicy;
    }

    public void setEnablePasswordPolicy(Boolean enablePasswordPolicy) {
        this.enablePasswordPolicy = enablePasswordPolicy;
    }

    /**
     * Minimum number of characters allowed.
     */
    public Integer getMinLength() {
        return minLength;
    }

    public void setMinLength(Integer minLength) {
        this.minLength = minLength;
    }

    /**
     * Minimum interval after which the password can be changed. This flag is only supported for PostgreSQL.
     */
    public String getPasswordChangeInterval() {
        return passwordChangeInterval;
    }

    public void setPasswordChangeInterval(String passwordChangeInterval) {
        this.passwordChangeInterval = passwordChangeInterval;
    }

    /**
     * Number of previous passwords that cannot be reused.
     */
    public Integer getReuseInterval() {
        return reuseInterval;
    }

    public void setReuseInterval(Integer reuseInterval) {
        this.reuseInterval = reuseInterval;
    }

    @Override
    public String primaryKey() {
        return "";
    }

    @Override
    public void copyFrom(com.google.api.services.sqladmin.model.PasswordValidationPolicy model) {
        setComplexity(model.getComplexity());
        setDisallowCompromisedCredentials(model.getDisallowCompromisedCredentials());
        setDisallowUsernameSubstring(model.getDisallowUsernameSubstring());
        setEnablePasswordPolicy(model.getEnablePasswordPolicy());
        setMinLength(model.getMinLength());
        setPasswordChangeInterval(model.getPasswordChangeInterval());
        setReuseInterval(model.getReuseInterval());
    }

    com.google.api.services.sqladmin.model.PasswordValidationPolicy toPasswordValidationPolicy() {
        com.google.api.services.sqladmin.model.PasswordValidationPolicy passwordValidationPolicy = new com.google.api.services.sqladmin.model.PasswordValidationPolicy();
        passwordValidationPolicy.setComplexity(getComplexity());
        passwordValidationPolicy.setDisallowCompromisedCredentials(getDisallowCompromisedCredentials());
        passwordValidationPolicy.setDisallowUsernameSubstring(getDisallowUsernameSubstring());
        passwordValidationPolicy.setEnablePasswordPolicy(getEnablePasswordPolicy());
        passwordValidationPolicy.setMinLength(getMinLength());
        passwordValidationPolicy.setPasswordChangeInterval(getPasswordChangeInterval());
        passwordValidationPolicy.setReuseInterval(getReuseInterval());

        return passwordValidationPolicy;
    }
}
