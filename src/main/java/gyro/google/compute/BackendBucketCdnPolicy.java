package gyro.google.compute;

import java.util.ArrayList;
import java.util.List;

import gyro.core.resource.Diffable;
import gyro.core.resource.Output;
import gyro.core.resource.Updatable;
import gyro.core.validation.Required;
import gyro.google.Copyable;

public class BackendBucketCdnPolicy extends Diffable
    implements Copyable<com.google.api.services.compute.model.BackendBucketCdnPolicy> {

    private Long signedUrlMaxAge;
    private List<String> signedUrlKeyNames;

    /**
     * Maximum number of seconds the response to a signed URL request will be considered fresh. (Required)
     */
    @Required
    @Updatable
    public Long getSignedUrlMaxAge() {
        return signedUrlMaxAge;
    }

    public void setSignedUrlMaxAge(Long signedUrlMaxAge) {
        this.signedUrlMaxAge = signedUrlMaxAge;
    }

    /**
     * Names of the keys for signing request URLs.
     */
    @Output
    public List<String> getSignedUrlKeyNames() {
        if (signedUrlKeyNames == null) {
            signedUrlKeyNames = new ArrayList<>();
        }

        return signedUrlKeyNames;
    }

    public void setSignedUrlKeyNames(List<String> signedUrlKeyNames) {
        this.signedUrlKeyNames = signedUrlKeyNames;
    }

    @Override
    public String primaryKey() {
        return "";
    }

    @Override
    public void copyFrom(com.google.api.services.compute.model.BackendBucketCdnPolicy backendBucketCdnPolicy) {
        setSignedUrlKeyNames(backendBucketCdnPolicy.getSignedUrlKeyNames());
        setSignedUrlMaxAge(backendBucketCdnPolicy.getSignedUrlCacheMaxAgeSec());
    }

    com.google.api.services.compute.model.BackendBucketCdnPolicy toBackendBucketCdnPolicy() {
        com.google.api.services.compute.model.BackendBucketCdnPolicy backendBucketCdnPolicy = new com.google.api.services.compute.model.BackendBucketCdnPolicy();
        backendBucketCdnPolicy.setSignedUrlCacheMaxAgeSec(getSignedUrlMaxAge());
        return backendBucketCdnPolicy;
    }
}
