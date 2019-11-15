package gyro.google.storage;

import com.google.api.services.storage.model.Bucket.Lifecycle.Rule.Action;
import gyro.core.resource.Diffable;
import gyro.core.resource.Updatable;
import gyro.core.validation.ValidStrings;
import gyro.google.Copyable;

/**
 * The action to take.
 */
public class BucketLifecycleRuleAction extends Diffable implements Copyable<Action> {

    private String storageClass;
    private String type;

    /**
     * Target storage class. If the ``action`` is ``SetStorageClass`` it is required.
     */
    @Updatable
    public String getStorageClass() {
        return storageClass;
    }

    public void setStorageClass(String storageClass) {
        this.storageClass = storageClass;
    }

    /**
     * Type of the action to take on condition. Valid types are ``Delete`` or ``SetStorageClass``.
     */
    @Updatable
    @ValidStrings({"Delete", "SetStorageClass"})
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public void copyFrom(Action model) {
        if (model != null) {
            setStorageClass(model.getStorageClass());
            setType(model.getType());
        }
    }

    public Action toLifecycleRuleAction() {
        return new Action().setStorageClass(getStorageClass()).setType(getType());
    }
}
