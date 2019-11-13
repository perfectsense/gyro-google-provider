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
public class LifecycleRule extends Diffable implements Copyable<Rule> {

    private LifecycleRuleAction action;
    private LifecycleRuleCondition condition;

    /**
     * The action to take for the Rule.
     */
    @Required
    @Updatable
    public LifecycleRuleAction getAction() {
        return action;
    }

    public void setAction(LifecycleRuleAction action) {
        this.action = action;
    }

    /**
     * The condition(s) under which the action will be taken.
     */
    @Required
    @Updatable
    public LifecycleRuleCondition getCondition() {
        return condition;
    }

    public void setCondition(LifecycleRuleCondition condition) {
        this.condition = condition;
    }

    @Override
    public String primaryKey() {
        return getAction().primaryKey() + " where " + getCondition().primaryKey();
    }

    @Override
    public void copyFrom(Rule model) {
        setAction(LifecycleRuleAction.fromGcpLifecycleRuleAction(model.getAction()));
        setCondition(LifecycleRuleCondition.fromGcpLifecycleRuleCondition(model.getCondition()));
    }

    public Rule toGcpLifecycleRule() {
        return new Rule()
               .setAction(getAction() == null ? null : getAction().toGcpLifecycleRuleAction())
               .setCondition(getCondition() == null ? null : getCondition().toGcpLifecycleRuleCondition());
    }

    public static LifecycleRule fromGcpLifecycleRule(Rule model) {
        if (model != null) {
            LifecycleRule rule = new LifecycleRule();
            rule.setAction(LifecycleRuleAction.fromGcpLifecycleRuleAction(model.getAction()));
            rule.setCondition(LifecycleRuleCondition.fromGcpLifecycleRuleCondition(model.getCondition()));
            return rule;
        }
        return null;
    }
}
