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

import gyro.core.resource.Diffable;
import gyro.core.resource.Updatable;
import gyro.core.validation.Required;
import gyro.google.Copyable;

public class SecurityPolicyRuleMatcherConfig extends Diffable
    implements Copyable<com.google.cloud.compute.v1.SecurityPolicyRuleMatcherConfig> {

    private List<String> srcIpRanges;

    /**
     * The ip ranges for this security policy rule matcher configuration.
     */
    @Required
    @Updatable
    public List<String> getSrcIpRanges() {
        if (srcIpRanges == null) {
            srcIpRanges = new ArrayList<>();
        }
        return srcIpRanges;
    }

    public void setSrcIpRanges(List<String> srcIpRanges) {
        this.srcIpRanges = srcIpRanges;
    }

    @Override
    public String primaryKey() {
        return "";
    }

    @Override
    public void copyFrom(com.google.cloud.compute.v1.SecurityPolicyRuleMatcherConfig config) {
        setSrcIpRanges(config.getSrcIpRanges());
    }

    public com.google.cloud.compute.v1.SecurityPolicyRuleMatcherConfig toSecurityPolicyRuleMatcherConfig() {
        com.google.cloud.compute.v1.SecurityPolicyRuleMatcherConfig matcherConfig = new com.google.cloud.compute.v1.SecurityPolicyRuleMatcherConfig();
        matcherConfig.setSrcIpRanges(getSrcIpRanges());
        return matcherConfig;
    }
}
