/*
 * Copyright 2019, Perfect Sense, Inc.
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

package gyro.google.storage;

import com.google.api.services.storage.model.Bucket.Lifecycle.Rule;
import gyro.core.resource.Diffable;
import gyro.core.validation.Required;
import gyro.google.Copyable;

/**
 * The Buckets lifecycle configuration.
 */
public class BucketLifecycleRule extends Diffable implements Copyable<Rule> {

    private BucketLifecycleRuleAction action;
    private BucketLifecycleRuleCondition condition;

    /**
     * The action to take for the Rule.
     *
     * @subresource gyro.google.storage.BucketLifecycleRuleAction
     */
    @Required
    public BucketLifecycleRuleAction getAction() {
        return action;
    }

    public void setAction(BucketLifecycleRuleAction action) {
        this.action = action;
    }

    /**
     * The condition under which the action will be taken.
     *
     * @subresource gyro.google.storage.BucketLifecycleRuleCondition
     */
    @Required
    public BucketLifecycleRuleCondition getCondition() {
        return condition;
    }

    public void setCondition(BucketLifecycleRuleCondition condition) {
        this.condition = condition;
    }

    @Override
    public String primaryKey() {
        return String.format(
            "rule-config action: %s and condition: %s",
            (getAction() == null ? "" : getAction().primaryKey()),
            (getCondition() == null ? "" : getCondition().primaryKey()));
    }

    @Override
    public void copyFrom(Rule model) {
        BucketLifecycleRuleAction bucketLifecycleRuleAction = newSubresource(BucketLifecycleRuleAction.class);
        bucketLifecycleRuleAction.copyFrom(model.getAction());
        setAction(bucketLifecycleRuleAction);

        BucketLifecycleRuleCondition bucketLifecycleRuleCondition = newSubresource(BucketLifecycleRuleCondition.class);
        bucketLifecycleRuleCondition.copyFrom(model.getCondition());
        setCondition(bucketLifecycleRuleCondition);
    }

    public Rule toLifecycleRule() {
        return new Rule()
            .setAction(getAction().toLifecycleRuleAction())
            .setCondition(getCondition().toLifecycleRuleCondition());
    }
}
