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

import com.google.cloud.storage.BucketInfo.LifecycleRule;
import gyro.core.resource.Diffable;
import gyro.core.resource.Updatable;
import gyro.core.validation.Required;
import gyro.google.Copyable;

/**
 * The Buckets lifecycle configuration.
 */
public class BucketLifecycleRule extends Diffable implements Copyable<LifecycleRule> {

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
    @Updatable
    public BucketLifecycleRuleCondition getCondition() {
        return condition;
    }

    public void setCondition(BucketLifecycleRuleCondition condition) {
        this.condition = condition;
    }

    @Override
    public String primaryKey() {
        return String.format(
            "action: %s and condition: %s",
            (getAction() == null ? "" : getAction().primaryKey()),
            (getCondition() == null ? "" : getCondition().primaryKey()));
    }

    @Override
    public void copyFrom(LifecycleRule model) {
        BucketLifecycleRuleAction bucketLifecycleRuleAction = newSubresource(BucketLifecycleRuleAction.class);
        bucketLifecycleRuleAction.copyFrom(model.getAction());
        setAction(bucketLifecycleRuleAction);

        BucketLifecycleRuleCondition bucketLifecycleRuleCondition = newSubresource(BucketLifecycleRuleCondition.class);
        bucketLifecycleRuleCondition.copyFrom(model.getCondition());
        setCondition(bucketLifecycleRuleCondition);
    }

    public LifecycleRule toLifecycleRule() {
        return new LifecycleRule(getAction().toLifecycleRuleAction(), getCondition().toLifecycleRuleCondition());
    }
}
