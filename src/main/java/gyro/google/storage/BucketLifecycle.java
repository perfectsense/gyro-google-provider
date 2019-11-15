package gyro.google.storage;

import com.google.api.services.storage.model.Bucket;
import com.google.api.services.storage.model.Bucket.Lifecycle;
import gyro.core.resource.Diffable;
import gyro.core.resource.Updatable;
import gyro.google.Copyable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The Buckets lifecycle configuration.
 */
public class BucketLifecycle extends Diffable implements Copyable<Lifecycle> {

    private List<BucketLifecycleRule> rule;

    /**
     * A lifecycle management rule, which is made of an action to take and the condition(s) under which an action will be taken.
     *
     * @subresource gyro.google.storage.BucketLifecycleRule
     */
    @Updatable
    public List<BucketLifecycleRule> getRule() {
        if (rule == null) {
            return new ArrayList<>();
        }
        return rule;
    }

    public void setRule(List<BucketLifecycleRule> rule) {
        this.rule = rule;
    }

    @Override
    public void copyFrom(Bucket.Lifecycle model) {
        if (model != null && model.getRule() != null) {
            setRule(model.getRule().stream()
                    .map(r -> {
                        BucketLifecycleRule bucketLifecycleRule = newSubresource(BucketLifecycleRule.class);
                        bucketLifecycleRule.copyFrom(r);
                        return bucketLifecycleRule;
                    })
                    .collect(Collectors.toList())
            );
        }
    }

    @Override
    public String primaryKey() {
        return "lifecycle with " + getRule().size() + " rules";
    }

    public Lifecycle toLifecycle() {
        return new Bucket.Lifecycle()
                .setRule(getRule() == null ? null : getRule().stream().map(BucketLifecycleRule::toLifecycleRule)
                        .collect(Collectors.toList()));
    }
}
