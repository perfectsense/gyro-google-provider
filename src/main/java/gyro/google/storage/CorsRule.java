package gyro.google.storage;

import com.google.api.services.storage.model.Bucket;
import gyro.core.resource.Diffable;
import gyro.core.validation.ValidStrings;
import gyro.google.Copyable;

import java.util.List;

/**
 * Configuration for setting of Cors configuration for assets within a Bucket.
 *
 * Examples
 * --------
 *
 * ..code-block:: gyro
 *
 *   cors-rule
 *        'max-age-seconds': 3600
 *        'method': ['GET', 'POST']
 *        'origin': ['*']
 *        'response-header': ['application-x-test']
 *   end
 */
public class CorsRule extends Diffable implements Copyable<Bucket.Cors> {

    private Integer maxAgeSeconds;
    private List<String> method;
    private List<String> origin;
    private List<String> responseHeader;

    /**
     * The value quantified in seconds to be returned in the "Access-Control-Max-Age" header.
     */
    public Integer getMaxAgeSeconds() {
        return maxAgeSeconds;
    }

    public void setMaxAgeSeconds(Integer maxAgeSeconds) {
        this.maxAgeSeconds = maxAgeSeconds;
    }

    /**
     * List of HTTP methods in which to include CORS response headers. Valid options are: "GET", "POST", ... in
     * addition to the "*" value for all methods.
     */
    @ValidStrings({"GET", "HEAD", "POST", "MATCH", "PUT", "DELETE", "CONNECT", "OPTIONS", "TRACE", "PATCH", "*"})
    public List<String> getMethod() {
        return method;
    }

    public void setMethod(List<String> method) {
        this.method = method;
    }

    /**
     * List of Origins able to receive CORS response headers. The "*" value is also permitted for allowing any origin.
     */
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
     * @return A native GCP Cors rule.
     */
    public Bucket.Cors toBucketCors() {
        Bucket.Cors cors = new Bucket.Cors();
        cors.setMaxAgeSeconds(getMaxAgeSeconds());
        cors.setMethod(getMethod());
        cors.setOrigin(getOrigin());
        cors.setResponseHeader(getResponseHeader());

        return cors;
    }

    /**
     *  Creates a new Gyro CorsRule instance populated from the configuration coming from GCP.
     *  
     * @param model The GCP Cors rule.
     * @return A Gyro CorsRule from the version coming from GCP.
     */
    public static CorsRule fromBucketCors(Bucket.Cors model) {
        CorsRule corsRule = new CorsRule();
        corsRule.setMaxAgeSeconds(model.getMaxAgeSeconds());
        corsRule.setMethod(model.getMethod());
        corsRule.setOrigin(model.getOrigin());
        corsRule.setResponseHeader(model.getResponseHeader());

        return corsRule;
    }
}
