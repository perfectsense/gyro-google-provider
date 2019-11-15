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
        setAction(BucketLifecycleRuleAction.fromLifecycleRuleAction(model.getAction()));
        setCondition(BucketLifecycleRuleCondition.fromLifecycleRuleCondition(model.getCondition()));
    }

    public Rule toLifecycleRule() {
        return new Rule()
               .setAction(getAction() == null ? null : getAction().toLifecycleRuleAction())
               .setCondition(getCondition() == null ? null : getCondition().toLifecycleRuleCondition());
    }

    public static BucketLifecycleRule fromLifecycleRule(Rule model) {
        if (model != null) {
            BucketLifecycleRule rule = new BucketLifecycleRule();
            rule.setAction(BucketLifecycleRuleAction.fromLifecycleRuleAction(model.getAction()));
            rule.setCondition(BucketLifecycleRuleCondition.fromLifecycleRuleCondition(model.getCondition()));
            return rule;
        }
        return null;
    }
}
