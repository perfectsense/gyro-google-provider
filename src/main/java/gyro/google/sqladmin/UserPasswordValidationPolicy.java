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
import gyro.core.resource.Output;
import gyro.google.Copyable;

public class UserPasswordValidationPolicy extends Diffable
    implements Copyable<com.google.api.services.sqladmin.model.UserPasswordValidationPolicy> {

    private Integer allowedFailedAttempts;

    private Boolean enableFailedAttemptsCheck;

    private Boolean enablePasswordVerification;

    private String passwordExpirationDuration;

    private PasswordStatus status;

    /**
     * Number of failed login attempts allowed before user get locked.
     */
    public Integer getAllowedFailedAttempts() {
        return allowedFailedAttempts;
    }

    public void setAllowedFailedAttempts(Integer allowedFailedAttempts) {
        this.allowedFailedAttempts = allowedFailedAttempts;
    }

    /**
     * If true, failed login attempts check will be enabled.
     */
    public Boolean getEnableFailedAttemptsCheck() {
        return enableFailedAttemptsCheck;
    }

    public void setEnableFailedAttemptsCheck(Boolean enableFailedAttemptsCheck) {
        this.enableFailedAttemptsCheck = enableFailedAttemptsCheck;
    }

    /**
     * If true, the user must specify the current password before changing the password. This flag is supported only for MySQL.
     */
    public Boolean getEnablePasswordVerification() {
        return enablePasswordVerification;
    }

    public void setEnablePasswordVerification(Boolean enablePasswordVerification) {
        this.enablePasswordVerification = enablePasswordVerification;
    }

    /**
     * Expiration duration after password is updated.
     */
    public String getPasswordExpirationDuration() {
        return passwordExpirationDuration;
    }

    public void setPasswordExpirationDuration(String passwordExpirationDuration) {
        this.passwordExpirationDuration = passwordExpirationDuration;
    }

    /**
     * Read-only password status.
     *
     * @subresource gyro.google.sqladmin.base.PasswordStatus
     */
    @Output
    public PasswordStatus getStatus() {
        return status;
    }

    public void setStatus(PasswordStatus status) {
        this.status = status;
    }

    @Override
    public String primaryKey() {
        return "";
    }

    @Override
    public void copyFrom(com.google.api.services.sqladmin.model.UserPasswordValidationPolicy model) {
        setAllowedFailedAttempts(model.getAllowedFailedAttempts());
        setEnableFailedAttemptsCheck(model.getEnableFailedAttemptsCheck());
        setEnablePasswordVerification(model.getEnablePasswordVerification());
        setPasswordExpirationDuration(model.getPasswordExpirationDuration());

        setStatus(null);
        if (model.getStatus() != null) {
            PasswordStatus status = newSubresource(PasswordStatus.class);
            status.copyFrom(model.getStatus());
            setStatus(status);
        }
    }
}
