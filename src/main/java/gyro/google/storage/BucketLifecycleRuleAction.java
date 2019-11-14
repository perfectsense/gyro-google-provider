package gyro.google.storage;

import com.google.api.services.storage.model.Bucket.Lifecycle.Rule.Action;
import gyro.core.resource.Diffable;
import gyro.core.resource.Updatable;
import gyro.core.validation.ValidStrings;
import gyro.google.Copyable;

/**
 * The action to take.
 *
 * Example
 * -------
 *
 * ..code-block:: gyro
 *
 *      action
 *          type: 'Delete'
 *      end
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
     * Type of the action to take on condition. Valid types are ``Delete`` and ``SetStorageClass``.
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
    public String primaryKey() {
        StringBuilder key = new StringBuilder(getType());

        if ("SetStorageClass".equals(getStorageClass())) {
            key.append(" [").append(getStorageClass()).append("]");
        }

        return key.toString();
    }

    @Override
    public void copyFrom(Action model) {
         setStorageClass(model.getStorageClass());
         setType(model.getType());
    }

    public Action toGcpLifecycleRuleAction() {
        return new Action().setStorageClass(getStorageClass()).setType(getType());
    }

    public static BucketLifecycleRuleAction fromGcpLifecycleRuleAction(Action model) {
        if (model != null) {
            BucketLifecycleRuleAction action = new BucketLifecycleRuleAction();
            action.setStorageClass(model.getStorageClass());
            action.setType(model.getType());
            return action;
        }
        return null;
    }
}
