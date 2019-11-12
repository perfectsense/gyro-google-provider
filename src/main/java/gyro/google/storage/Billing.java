package gyro.google.storage;

import com.google.api.services.storage.model.Bucket;
import gyro.core.resource.Diffable;
import gyro.core.resource.Updatable;
import gyro.google.Copyable;

/**
 * Subresource for setting the {@link Bucket.Billing} configuration to a {@link Bucket}.
 *
 * Example
 * -------
 *
 * ..code-block:: gyro
 *
 *     billing
 *         requestor-pays: true
 *     end
 */
public class Billing extends Diffable implements Copyable<Bucket.Billing> {

    private Boolean requestorPays;

    /**
     * Enables ``true`` the requester pays setting for this bucket.
     */
    @Updatable
    public Boolean getRequestorPays() {
        return requestorPays;
    }

    public void setRequestorPays(Boolean requestorPays) {
        this.requestorPays = requestorPays;
    }

    @Override
    public void copyFrom(Bucket.Billing model) {
        setRequestorPays(model.getRequesterPays());
    }

    /**
     * @return This as a {@link Bucket.Billing} instance.
     */
    public Bucket.Billing toBucketBilling() {
        return new Bucket.Billing().setRequesterPays(getRequestorPays());
    }

    /**
     * Create a new Billing from a GCP {@link Bucket.Billing} instance.
     * 
     * @param model The Billing instance to convert.
     * @return New Billing instance populated by data from ``model``.
     */
    public static Billing fromBucketBilling(Bucket.Billing model) {
        Billing billing = new Billing();
        billing.setRequestorPays(model.getRequesterPays());
        return billing;
    }
}
