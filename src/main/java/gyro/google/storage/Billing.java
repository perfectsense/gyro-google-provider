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
public class BillingSubresource extends Diffable implements Copyable<Bucket.Billing> {

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
     * @return A GCP {@link Bucket.Billing} instance.
     */
    public Bucket.Billing toBucketBilling() {
        return new Bucket.Billing().setRequesterPays(getRequestorPays());
    }

    /**
     * Create a new BillingRule from a GCP {@link Bucket.Billing} instance.
     * @param model The Billing instance to convert.
     * @return New BillingRule populated by data from model.
     */
    public static BillingSubresource fromBucketBilling(Bucket.Billing model) {
        BillingSubresource billingSubresource = new BillingSubresource();
        billingSubresource.setRequestorPays(model.getRequesterPays());
        return billingSubresource;
    }
}
