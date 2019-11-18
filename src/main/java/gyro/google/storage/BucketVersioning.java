package gyro.google.storage;

import com.google.api.services.storage.model.Bucket.Versioning;
import gyro.core.resource.Diffable;
import gyro.core.resource.Updatable;
import gyro.google.Copyable;

/**
 * The bucket's versioning configuration.
 */
public class BucketVersioning extends Diffable implements Copyable<Versioning> {

    private Boolean enabled;

    /**
     * When ``true`` versioning is enabled for this bucket.
     */
    @Updatable
    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public void copyFrom(Versioning model) {
        if (model != null) {
            setEnabled(model.getEnabled());
        }
    }

    public Versioning toBucketVersioning() {
        return new Versioning().setEnabled(getEnabled());
    }
}
