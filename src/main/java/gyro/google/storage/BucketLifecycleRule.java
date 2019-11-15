package gyro.google.storage;

import com.google.api.services.storage.model.Bucket.Lifecycle.Rule;
import gyro.core.resource.Diffable;
import gyro.core.resource.Updatable;
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
    @Updatable
    public BucketLifecycleRuleAction getAction() {
        return action;
    }

    public void setAction(BucketLifecycleRuleAction action) {
        this.action = action;
    }

    /**
     * The condition(s) under which the action will be taken.
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
        return getAction().primaryKey() + " where " + getCondition().primaryKey();
    }

    @Override
    public void copyFrom(Rule model) {
        if (model != null) {
            BucketLifecycleRuleAction bucketLifecycleRuleAction = newSubresource(BucketLifecycleRuleAction.class);
            bucketLifecycleRuleAction.copyFrom(model.getAction());
            setAction(bucketLifecycleRuleAction);

            BucketLifecycleRuleCondition bucketLifecycleRuleCondition = newSubresource(BucketLifecycleRuleCondition.class);
            bucketLifecycleRuleCondition.copyFrom(model.getCondition());
            setCondition(bucketLifecycleRuleCondition);
        }
    }

    public Rule toLifecycleRule() {
        return new Rule()
               .setAction(getAction() == null ? null : getAction().toLifecycleRuleAction())
               .setCondition(getCondition() == null ? null : getCondition().toLifecycleRuleCondition());
    }
}
