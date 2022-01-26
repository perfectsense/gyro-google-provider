/*
 * Copyright 2019, Perfect Sense, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package gyro.google.compute;

import java.util.ArrayList;
import java.util.List;

import com.google.api.client.util.Data;
import com.google.cloud.compute.v1.CacheKeyPolicy;
import gyro.core.resource.Diffable;
import gyro.core.resource.Output;
import gyro.core.resource.Updatable;
import gyro.core.validation.Required;
import gyro.google.Copyable;

public class BackendServiceCdnPolicy extends Diffable
    implements Copyable<com.google.cloud.compute.v1.BackendServiceCdnPolicy> {

    private Long signedUrlMaxAge;
    private BackendServiceCdnCacheKeyPolicy cacheKeyPolicy;
    private List<String> signedUrlKeyNames;

    /**
     * Maximum number of seconds the response to a signed URL request will be considered fresh.
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
     * The cache key configuration for this backend service cdn configuration.
     *
     * @subresource gyro.google.compute.BackendServiceCdnCacheKeyPolicy
     */
    @Updatable
    public BackendServiceCdnCacheKeyPolicy getCacheKeyPolicy() {
        return cacheKeyPolicy;
    }

    public void setCacheKeyPolicy(BackendServiceCdnCacheKeyPolicy cacheKeyPolicy) {
        this.cacheKeyPolicy = cacheKeyPolicy;
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
    public void copyFrom(com.google.cloud.compute.v1.BackendServiceCdnPolicy backendServiceCdnPolicy) {
        setSignedUrlKeyNames(backendServiceCdnPolicy.getSignedUrlKeyNamesList());
        setSignedUrlMaxAge(backendServiceCdnPolicy.getSignedUrlCacheMaxAgeSec());
        setCacheKeyPolicy(null);
        if (backendServiceCdnPolicy.getCacheKeyPolicy() != null) {
            BackendServiceCdnCacheKeyPolicy cacheKeyPolicy = newSubresource(BackendServiceCdnCacheKeyPolicy.class);
            cacheKeyPolicy.copyFrom(backendServiceCdnPolicy.getCacheKeyPolicy());
            setCacheKeyPolicy(cacheKeyPolicy);
        }
    }

    com.google.cloud.compute.v1.BackendServiceCdnPolicy toBackendServiceCdnPolicy() {
        return com.google.cloud.compute.v1.BackendServiceCdnPolicy.newBuilder()
            .setSignedUrlCacheMaxAgeSec(getSignedUrlMaxAge())
            .setCacheKeyPolicy(getCacheKeyPolicy() != null ? getCacheKeyPolicy().toCacheKeyPolicy() : Data.nullOf(
                CacheKeyPolicy.class)).build();
    }
}
