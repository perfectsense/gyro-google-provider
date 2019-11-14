package gyro.google.storage;

import com.google.api.services.storage.model.Bucket.Lifecycle.Rule;
import gyro.core.resource.Diffable;
import gyro.core.resource.Updatable;
import gyro.core.validation.Required;
import gyro.google.Copyable;

/**
 * The Buckets lifecycle configuration.
 *
 * Example
 * -------
 *
 * ..code-block:: gyro
 *
 *    rule
 *        action
 *            type: 'Delete'
 *        end
 *        condition
 *            age: 15
 *        end
 *    end
 */
public class BucketLifecycleRule extends Diffable implements Copyable<Rule> {

    private BucketLifecycleRuleAction action;
    private BucketLifecycleRuleCondition condition;

    /**
     * The action to take for the Rule.
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
        setAction(BucketLifecycleRuleAction.fromGcpLifecycleRuleAction(model.getAction()));
        setCondition(BucketLifecycleRuleCondition.fromGcpLifecycleRuleCondition(model.getCondition()));
    }

    public Rule toGcpLifecycleRule() {
        return new Rule()
               .setAction(getAction() == null ? null : getAction().toGcpLifecycleRuleAction())
               .setCondition(getCondition() == null ? null : getCondition().toGcpLifecycleRuleCondition());
    }

    public static BucketLifecycleRule fromGcpLifecycleRule(Rule model) {
        if (model != null) {
            BucketLifecycleRule rule = new BucketLifecycleRule();
            rule.setAction(BucketLifecycleRuleAction.fromGcpLifecycleRuleAction(model.getAction()));
            rule.setCondition(BucketLifecycleRuleCondition.fromGcpLifecycleRuleCondition(model.getCondition()));
            return rule;
        }
        return null;
    }
}
