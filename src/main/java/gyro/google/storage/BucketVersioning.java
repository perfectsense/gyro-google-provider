package gyro.google.storage;

import com.google.api.services.storage.model.Bucket;
import gyro.core.resource.Diffable;
import gyro.core.resource.Updatable;
import gyro.google.Copyable;

/**
 * The bucket's versioning configuration.
 */
public class BucketVersioning extends Diffable implements Copyable<Bucket.Versioning> {

    private Boolean enabled;

    /**
     * When true versioning is enabled for this bucket.
     */
    @Updatable
    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public void copyFrom(Bucket.Versioning model) {
        setEnabled(model.getEnabled());
    }

    public Bucket.Versioning toBucketVersioning() {
        return new Bucket.Versioning().setEnabled(getEnabled());
    }

    public static BucketVersioning fromBucketVersioning(Bucket.Versioning model) {
        if (model != null) {
            BucketVersioning versioning = new BucketVersioning();
            versioning.setEnabled(model.getEnabled());
            return versioning;
        }
        return null;
    }
}
