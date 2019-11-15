package gyro.google.storage;

import com.google.api.services.storage.model.Bucket;
import gyro.core.resource.Diffable;
import gyro.core.resource.Updatable;
import gyro.core.validation.ValidStrings;
import gyro.google.Copyable;

import java.util.List;

/**
 * Subresource for setting of Bucket.Cors configuration for assets within a Bucket.
 */
public class BucketCors extends Diffable implements Copyable<Bucket.Cors> {

    private Integer maxAgeSeconds;
    private List<String> method;
    private List<String> origin;
    private List<String> responseHeader;

    /**
     * The value quantified in seconds to be returned in the "Access-Control-Max-Age" header.
     */
    @Updatable
    public Integer getMaxAgeSeconds() {
        return maxAgeSeconds;
    }

    public void setMaxAgeSeconds(Integer maxAgeSeconds) {
        this.maxAgeSeconds = maxAgeSeconds;
    }

    /**
     * List of HTTP methods in which to include CORS response headers. Valid options are: ``GET``, ``POST``, ... in
     * addition to the ``*`` value for all methods.
     */
    @Updatable
    @ValidStrings({"GET", "HEAD", "POST", "MATCH", "PUT", "DELETE", "CONNECT", "OPTIONS", "TRACE", "PATCH", "*"})
    public List<String> getMethod() {
        return method;
    }

    public void setMethod(List<String> method) {
        this.method = method;
    }

    /**
     * List of Origins able to receive CORS response headers. The ``*`` value is also permitted for allowing any origin.
     */
    @Updatable
    public List<String> getOrigin() {
        return origin;
    }

    public void setOrigin(List<String> origin) {
        this.origin = origin;
    }

    /**
     * List of HTTP headers other than the simple response headers giving permission for the user-agent to share across
     * domains.
     */
    @Updatable
    public List<String> getResponseHeader() {
        return responseHeader;
    }

    public void setResponseHeader(List<String> responseHeader) {
        this.responseHeader = responseHeader;
    }

    @Override
    public void copyFrom(Bucket.Cors model) {
        setMaxAgeSeconds(model.getMaxAgeSeconds());
        setMethod(model.getMethod());
        setOrigin(model.getOrigin());
        setResponseHeader(model.getResponseHeader());
    }

    /**
     * This as a Bucket Cors instance.
     */
    public Bucket.Cors toBucketCors() {
        return new Bucket.Cors()
                .setMaxAgeSeconds(getMaxAgeSeconds())
                .setMethod(getMethod())
                .setOrigin(getOrigin())
                .setResponseHeader(getResponseHeader());
    }

    /**
     *  Creates a new Gyro Cors instance populated from the configuration coming from GCP.
     */
    public static BucketCors fromBucketCors(Bucket.Cors model) {
        BucketCors cors = new BucketCors();
        cors.setMaxAgeSeconds(model.getMaxAgeSeconds());
        cors.setMethod(model.getMethod());
        cors.setOrigin(model.getOrigin());
        cors.setResponseHeader(model.getResponseHeader());

        return cors;
    }
}
