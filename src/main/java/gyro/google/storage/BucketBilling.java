package gyro.google.storage;

import com.google.api.services.storage.model.Bucket;
import gyro.core.resource.Diffable;
import gyro.core.resource.Updatable;
import gyro.google.Copyable;

/**
 * Subresource for setting the Bucket Billing configuration to a Bucket.
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
public class BucketBilling extends Diffable implements Copyable<Bucket.Billing> {

    private Boolean requesterPays;

    /**
     * Enables ``true`` the requester pays setting for this bucket.
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
    public void copyFrom(Bucket.Billing model) {
        setRequesterPays(model.getRequesterPays());
    }

    /**
     * This as a Bucket Billing instance.
     */
    public Bucket.Billing toGcpBucketBilling() {
        return new Bucket.Billing().setRequesterPays(getRequesterPays());
    }

    /**
     * Create a new Billing from a GCP Bucket Billing instance.
     */
    public static BucketBilling fromGcpBucketBilling(Bucket.Billing model) {
        if (model != null) {
            BucketBilling bucketBilling = new BucketBilling();
            bucketBilling.setRequesterPays(model.getRequesterPays());
            return bucketBilling;
        }
        return null;
    }
}
