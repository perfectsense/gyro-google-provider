package gyro.google.storage;

import com.google.api.services.storage.model.Bucket;
import gyro.core.resource.Diffable;
import gyro.core.resource.Updatable;
import gyro.google.Copyable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The Buckets lifecycle configuration.
 * 
 * Example
 * -------
 *
 * ..code-block:: gyro
 * 
 *     lifecycle
 *         rule
 *             action
 *                 type: 'Delete'
 *             end
 *             condition
 *                 age: 15
 *            end
 *         end
 *         rule
 *             action
 *                 type: 'Delete'
 *             end
 *             condition
 *                 num-newer-versions: 2
 *             end
 *           end
 *         rule
 *             action
 *                type: 'Delete'
 *             end
 *             condition
 *                 is-live: true
 *                 age: 20
 *             end
 *         end
 *     end
 */
public class Lifecycle extends Diffable implements Copyable<Bucket.Lifecycle> {

    private List<LifecycleRule> rule;

    /**
     * A lifecycle management rule, which is made of an action to take and the condition(s) under which an action
     * will be taken.
     */
    @Updatable
    public List<LifecycleRule> getRule() {
        if (rule == null) {
            return new ArrayList<>();
        }
        return rule;
    }

    public void setRule(List<LifecycleRule> rule) {
        this.rule = rule;
    }

    @Override
    public void copyFrom(Bucket.Lifecycle model) {
        setRule(model.getRule() == null ? null : model.getRule().stream().map(r -> LifecycleRule.fromGcpLifecycleRule(r)).collect(Collectors.toList()));
    }

    @Override
    public String primaryKey() {
        return "lifecycle with " + getRule().size() + " rules";
    }

    public Bucket.Lifecycle toGcpLifecycle() {
        return new Bucket.Lifecycle()
                .setRule(getRule() == null ? null : getRule().stream().map(LifecycleRule::toGcpLifecycleRule)
                        .collect(Collectors.toList()));
    }

    public static Lifecycle fromGcpLifecycle(Bucket.Lifecycle model) {
        if (model != null) {
            Lifecycle lifecycle = new Lifecycle();
            lifecycle.setRule(model.getRule() == null ? null : model.getRule().stream().map(r -> LifecycleRule.fromGcpLifecycleRule(r)).collect(Collectors.toList()));
            return lifecycle;
        }
        return null;
    }
}
