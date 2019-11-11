package gyro.google.storage;

import com.google.api.services.storage.model.Bucket;
import gyro.core.resource.Diffable;
import gyro.google.Copyable;

/**
 * Configuration for setting adding {@link Bucket.Billing} configuration to a {@link Bucket}.
 *
 * Example
 * -------
 *
 * ..code-block:: gyro
 *
 *     billing-rule
 *         requestor-pays: true
 *     end
 */
public class BillingRule extends Diffable implements Copyable<Bucket.Billing> {

    private Boolean requestorPays;

    /**
     * If true the requester pays is enabled for this bucket.
     * @return
     */
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
    public static BillingRule fromBucketBilling(Bucket.Billing model) {
        BillingRule billingRule = new BillingRule();
        billingRule.setRequestorPays(model.getRequesterPays());
        return billingRule;
    }
}
