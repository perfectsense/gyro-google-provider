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

import java.util.List;

import gyro.core.resource.Diffable;
import gyro.core.resource.Updatable;
import gyro.core.validation.Required;
import gyro.google.Copyable;

public class SecurityPolicyRuleMatcherConfig extends Diffable
    implements Copyable<com.google.api.services.compute.model.SecurityPolicyRuleMatcherConfig> {

    private List<String> srcIpRanges;

    /**
     * The ip ranges for this security policy rule matcher configuration.
     */
    @Required
    @Updatable
    public List getSrcIpRanges() {
        return srcIpRanges;
    }

    public void setSrcIpRanges(List srcIpRanges) {
        this.srcIpRanges = srcIpRanges;
    }

    @Override
    public void copyFrom(com.google.api.services.compute.model.SecurityPolicyRuleMatcherConfig config) {
        config.setSrcIpRanges(getSrcIpRanges());
    }

    public com.google.api.services.compute.model.SecurityPolicyRuleMatcherConfig toSecurityPolicyRuleMatcherConfig() {
        com.google.api.services.compute.model.SecurityPolicyRuleMatcherConfig matcherConfig = new com.google.api.services.compute.model.SecurityPolicyRuleMatcherConfig();
        matcherConfig.setSrcIpRanges(getSrcIpRanges());
        return matcherConfig;
    }
}
