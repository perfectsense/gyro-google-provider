package gyro.google.storage;

import com.google.api.services.storage.model.Bucket.RetentionPolicy;
import gyro.core.resource.Diffable;
import gyro.core.resource.Output;
import gyro.core.resource.Updatable;
import gyro.core.validation.Max;
import gyro.core.validation.Min;
import gyro.google.Copyable;

/**
 * Defines the minimum age an object in the bucket must reach before it can be deleted or overwritten.
 */
public class BucketRetentionPolicy extends Diffable implements Copyable<RetentionPolicy> {

    private String effectiveTime;
    private Boolean isLocked;
    private Long retentionPeriod;

    /**
     * GCP-determined value that indicates the time from which policy was enforced and effective.
     */
    @Output
    public String getEffectiveTime() {
        return effectiveTime;
    }

    public void setEffectiveTime(String effectiveTime) {
        this.effectiveTime = effectiveTime;
    }

    /**
     * Once locked, an object retention policy cannot be modified.
     */
    @Updatable
    public Boolean getIsLocked() {
        return isLocked;
    }

    public void setIsLocked(Boolean locked) {
        isLocked = locked;
    }

    /**
     * The duration in seconds that objects need to be retained. Must be greater than ``0`` and less than ``3,155,760,000`` (100 years).
     */
    @Updatable
    @Max(3155759999D)
    @Min(1)
    public Long getRetentionPeriod() {
        return retentionPeriod;
    }

    public void setRetentionPeriod(Long retentionPeriod) {
        this.retentionPeriod = retentionPeriod;
    }

    @Override
    public void copyFrom(RetentionPolicy model) {
        if (model != null) {
            setEffectiveTime(model.getEffectiveTime() == null ? null : model.getEffectiveTime().toStringRfc3339());
            setIsLocked(model.getIsLocked());
            setRetentionPeriod(model.getRetentionPeriod());
        }
    }

    public RetentionPolicy toBucketRententionPolicy() {
        return new RetentionPolicy()
                .setIsLocked(getIsLocked())
                .setRetentionPeriod(getRetentionPeriod());
    }
}
