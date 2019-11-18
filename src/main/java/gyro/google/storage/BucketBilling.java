package gyro.google.storage;

import com.google.api.services.storage.model.Bucket.Billing;
import gyro.core.resource.Diffable;
import gyro.core.resource.Updatable;
import gyro.google.Copyable;

/**
 * Subresource for setting the Bucket Billing configuration to a Bucket.
 */
public class BucketBilling extends Diffable implements Copyable<Billing> {

    private Boolean requesterPays;

    /**
     * When ``true`` the requester pays setting for this bucket.
     */
    @Updatable
    public Boolean getRequesterPays() {
        return requesterPays;
    }

    public void setRequesterPays(Boolean requesterPays) {
        this.requesterPays = requesterPays;
    }

    @Override
    public String primaryKey() {
        return Boolean.toString("true".equals(getRequesterPays()));
    }

    @Override
    public void copyFrom(Billing model) {
        if (model != null) {
            setRequesterPays(model.getRequesterPays());
        }
    }

    public Billing toBucketBilling() {
        return new Billing().setRequesterPays(getRequesterPays());
    }
}
