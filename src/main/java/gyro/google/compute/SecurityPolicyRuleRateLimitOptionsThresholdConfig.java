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

import com.google.cloud.compute.v1.SecurityPolicyRuleRateLimitOptionsThreshold;
import gyro.core.resource.Diffable;
import gyro.core.resource.Updatable;
import gyro.core.validation.Required;
import gyro.google.Copyable;

public class SecurityPolicyRuleRateLimitOptionsThresholdConfig extends Diffable implements Copyable<SecurityPolicyRuleRateLimitOptionsThreshold> {

    private Integer count;
    private Integer intervalSec;

    /**
     * The number of requests per `interval_sec` that triggers a ban.
     */
    @Required
    @Updatable
    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    /**
     * The interval in seconds over which the `count` applies.
     */
    @Required
    @Updatable
    public Integer getIntervalSec() {
        return intervalSec;
    }

    public void setIntervalSec(Integer intervalSec) {
        this.intervalSec = intervalSec;
    }

    @Override
    public void copyFrom(SecurityPolicyRuleRateLimitOptionsThreshold model) {
        setCount(model.getCount());
        setIntervalSec(model.getIntervalSec());
    }

    @Override
    public String primaryKey() {
        return "";
    }

    SecurityPolicyRuleRateLimitOptionsThreshold toSecurityPolicyRuleRateLimitOptionsThreshold() {
        return SecurityPolicyRuleRateLimitOptionsThreshold.newBuilder()
            .setCount(getCount())
            .setIntervalSec(getIntervalSec())
            .build();
    }
}
