/*
 * Copyright 2023, Brightspot.
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

import com.google.cloud.compute.v1.SecurityPolicyRuleRedirectOptions;
import gyro.core.resource.Diffable;
import gyro.core.resource.Updatable;
import gyro.core.validation.Required;
import gyro.core.validation.ValidStrings;
import gyro.core.validation.ValidationError;
import gyro.google.Copyable;
import org.apache.commons.lang3.StringUtils;

public class SecurityPolicyRuleRedirectOptionsConfig extends Diffable
    implements Copyable<SecurityPolicyRuleRedirectOptions> {

    private String target;
    private String type;

    /**
     * The target of the redirect. Only valid when the redirect type is ``EXTERNAL_302``.
     */
    @Updatable
    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    /**
     * The type of the redirect.
     */
    @Required
    @Updatable
    @ValidStrings({"GOOGLE_RECAPTCHA", "EXTERNAL_302"})
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String primaryKey() {
        return null;
    }

    @Override
    public void copyFrom(SecurityPolicyRuleRedirectOptions model) {
        setType(model.getType());
        setTarget(model.getTarget());
    }

    SecurityPolicyRuleRedirectOptions toSecurityPolicyRuleRedirectOptions() {
        return SecurityPolicyRuleRedirectOptions.newBuilder()
            .setTarget(getTarget() == null ? "" : getTarget())
            .setType(getType())
            .build();
    }

    @Override
    public List<ValidationError> validate(Set<String> configuredFields) {
        List<ValidationError> errors = new ArrayList<>();

        if (configuredFields.contains("target") && !"EXTERNAL_302".equals(getType()) && getTarget() != null) {
            errors.add(new ValidationError(
                this,
                "target",
                "'target' is only valid when 'type' is set to 'EXTERNAL_302'!"));
        }

        if ("EXTERNAL_302".equals(getType()) && StringUtils.isBlank(getTarget())) {
            errors.add(new ValidationError(
                this,
                "target",
                "'target' is required when 'type' is set to 'EXTERNAL_302'!"));
        }

        return errors;
    }
}
