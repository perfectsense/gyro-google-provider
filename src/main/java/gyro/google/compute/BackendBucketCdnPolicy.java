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
import java.util.stream.Collectors;

import gyro.core.resource.Diffable;
import gyro.core.resource.Output;
import gyro.core.resource.Updatable;
import gyro.core.validation.Required;
import gyro.core.validation.ValidStrings;
import gyro.google.Copyable;

public class BackendBucketCdnPolicy extends Diffable
    implements Copyable<com.google.cloud.compute.v1.BackendBucketCdnPolicy> {

    private Long signedUrlMaxAge;
    private List<String> signedUrlKeyNames;
    private String cacheMode;
    private BackendBucketCdnCacheKeyPolicy cacheKeyPolicy;
    private List<BackendBucketCdnBypassCacheOnRequestHeader> bypassCacheOnRequestHeader;
    private Boolean negativeCaching;
    private List<BackendBucketCdnNegativeCachingPolicy> negativeCachingPolicy;
    private Integer clientTtl;
    private Integer defaultTtl;
    private Integer maxTtl;
    private Boolean requestCoalescing;
    private Integer serveWhileStale;

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

    /**
     * Specifies the cache mode for all responses from this backend bucket. Defaults to ``CACHE_ALL_STATIC``.
     */
    @ValidStrings({"CACHE_ALL_STATIC", "FORCE_CACHE_ALL", "INVALID_CACHE_MODE", "USE_ORIGIN_HEADERS"})
    @Updatable
    public String getCacheMode() {
        if (cacheMode == null) {
            cacheMode = "CACHE_ALL_STATIC";
        }

        return cacheMode;
    }

    public void setCacheMode(String cacheMode) {
        this.cacheMode = cacheMode;
    }

    /**
     * Specifies the cache key configuration.
     *
     * @subresource gyro.google.compute.BackendBucketCdnCacheKeyPolicy
     */
    @Updatable
    public BackendBucketCdnCacheKeyPolicy getCacheKeyPolicy() {
        return cacheKeyPolicy;
    }

    public void setCacheKeyPolicy(BackendBucketCdnCacheKeyPolicy cacheKeyPolicy) {
        this.cacheKeyPolicy = cacheKeyPolicy;
    }

    /**
     * Specifies a list of request headers that will be used to bypass cache.
     *
     * @subresource gyro.google.compute.BackendBucketCdnBypassCacheOnRequestHeader
     */
    @Updatable
    public List<BackendBucketCdnBypassCacheOnRequestHeader> getBypassCacheOnRequestHeader() {
        if (bypassCacheOnRequestHeader == null) {
            bypassCacheOnRequestHeader = new ArrayList<>();
        }

        return bypassCacheOnRequestHeader;
    }

    public void setBypassCacheOnRequestHeader(List<BackendBucketCdnBypassCacheOnRequestHeader> bypassCacheOnRequestHeader) {
        this.bypassCacheOnRequestHeader = bypassCacheOnRequestHeader;
    }

    /**
     * Specifies whether to follow redirects from the origin. Defaults to ``false``.
     */
    @Updatable
    public Boolean getNegativeCaching() {
        if (negativeCaching == null) {
            negativeCaching = false;
        }

        return negativeCaching;
    }

    public void setNegativeCaching(Boolean negativeCaching) {
        this.negativeCaching = negativeCaching;
    }

    /**
     * Specifies the negative caching configuration.
     *
     * @subresource gyro.google.compute.BackendBucketCdnNegativeCachingPolicy
     */
    @Updatable
    public List<BackendBucketCdnNegativeCachingPolicy> getNegativeCachingPolicy() {
        if (negativeCachingPolicy == null) {
            negativeCachingPolicy = new ArrayList<>();
        }

        return negativeCachingPolicy;
    }

    public void setNegativeCachingPolicy(List<BackendBucketCdnNegativeCachingPolicy> negativeCachingPolicy) {
        this.negativeCachingPolicy = negativeCachingPolicy;
    }

    /**
     * Specifies the TTL for cached content served to clients. Defaults to ``0``.
     */
    @Updatable
    public Integer getClientTtl() {
        if (clientTtl == null) {
            clientTtl = 0;
        }

        return clientTtl;
    }

    public void setClientTtl(Integer clientTtl) {
        this.clientTtl = clientTtl;
    }

    /**
     * Specifies the TTL for cached responses served to clients that do not send a max-age, min-fresh, or s-max-age directive. Defaults to ``0``.
     */
    @Updatable
    public Integer getDefaultTtl() {
        if (defaultTtl == null) {
            defaultTtl = 0;
        }

        return defaultTtl;
    }

    public void setDefaultTtl(Integer defaultTtl) {
        this.defaultTtl = defaultTtl;
    }

    /**
     * Specifies the maximum allowed TTL for cached content served to clients. Defaults to ``0``.
     */
    @Updatable
    public Integer getMaxTtl() {
        if (maxTtl == null) {
            maxTtl = 0;
        }

        return maxTtl;
    }

    public void setMaxTtl(Integer maxTtl) {
        this.maxTtl = maxTtl;
    }

    /**
     * Specifies whether to enable coalescing of multiple concurrent cache-fill requests into a single request. Defaults to ``false``.
     */
    @Updatable
    public Boolean getRequestCoalescing() {
        if (requestCoalescing == null) {
            requestCoalescing = false;
        }

        return requestCoalescing;
    }

    public void setRequestCoalescing(Boolean requestCoalescing) {
        this.requestCoalescing = requestCoalescing;
    }

    /**
     * Specifies the number of seconds to serve the content without contacting the origin. Defaults to ``0``.
     */
    @Updatable
    public Integer getServeWhileStale() {
        if (serveWhileStale == null) {
            serveWhileStale = 0;
        }

        return serveWhileStale;
    }

    public void setServeWhileStale(Integer serveWhileStale) {
        this.serveWhileStale = serveWhileStale;
    }

    @Override
    public String primaryKey() {
        return "";
    }

    @Override
    public void copyFrom(com.google.cloud.compute.v1.BackendBucketCdnPolicy backendBucketCdnPolicy) {
        setSignedUrlKeyNames(backendBucketCdnPolicy.getSignedUrlKeyNamesList());
        setSignedUrlMaxAge(backendBucketCdnPolicy.getSignedUrlCacheMaxAgeSec());
        setCacheMode(backendBucketCdnPolicy.getCacheMode());

        setCacheKeyPolicy(null);
        if (backendBucketCdnPolicy.getCacheKeyPolicy() != null) {
            BackendBucketCdnCacheKeyPolicy cacheKeyPolicy = newSubresource(BackendBucketCdnCacheKeyPolicy.class);
            cacheKeyPolicy.copyFrom(backendBucketCdnPolicy.getCacheKeyPolicy());
            setCacheKeyPolicy(cacheKeyPolicy);
        }

        setBypassCacheOnRequestHeader(null);
        if (!backendBucketCdnPolicy.getBypassCacheOnRequestHeadersList().isEmpty()) {
            setBypassCacheOnRequestHeader(backendBucketCdnPolicy.getBypassCacheOnRequestHeadersList().stream()
                .map(o -> {
                    BackendBucketCdnBypassCacheOnRequestHeader bypassCacheOnRequestHeader = newSubresource(BackendBucketCdnBypassCacheOnRequestHeader.class);
                    bypassCacheOnRequestHeader.copyFrom(o);
                    return bypassCacheOnRequestHeader;
                })
                .collect(Collectors.toList()));
        }

        setNegativeCaching(backendBucketCdnPolicy.getNegativeCaching());
        setNegativeCachingPolicy(null);
        if (!backendBucketCdnPolicy.getNegativeCachingPolicyList().isEmpty()) {
            setNegativeCachingPolicy(backendBucketCdnPolicy.getNegativeCachingPolicyList().stream()
                .map(o -> {
                    BackendBucketCdnNegativeCachingPolicy negativeCachingPolicy = newSubresource(BackendBucketCdnNegativeCachingPolicy.class);
                    negativeCachingPolicy.copyFrom(o);
                    return negativeCachingPolicy;
                })
                .collect(Collectors.toList()));
        }

        setClientTtl(backendBucketCdnPolicy.getClientTtl());
        setDefaultTtl(backendBucketCdnPolicy.getDefaultTtl());
        setMaxTtl(backendBucketCdnPolicy.getMaxTtl());
        setRequestCoalescing(backendBucketCdnPolicy.getRequestCoalescing());
        setServeWhileStale(backendBucketCdnPolicy.getServeWhileStale());
    }

    com.google.cloud.compute.v1.BackendBucketCdnPolicy toBackendBucketCdnPolicy() {
        return com.google.cloud.compute.v1.BackendBucketCdnPolicy.newBuilder()
            .setSignedUrlCacheMaxAgeSec(getSignedUrlMaxAge())
            .setCacheMode(getCacheMode())
            .setCacheKeyPolicy(getCacheKeyPolicy() != null ? getCacheKeyPolicy().toBackendBucketCdnPolicyCacheKeyPolicy() : new BackendBucketCdnCacheKeyPolicy().toBackendBucketCdnPolicyCacheKeyPolicy())
            .addAllBypassCacheOnRequestHeaders(getBypassCacheOnRequestHeader().stream()
                .map(BackendBucketCdnBypassCacheOnRequestHeader::toBackendBucketCdnPolicyBypassCacheOnRequestHeader)
                .collect(Collectors.toList()))
            .setClientTtl(getClientTtl())
            .setDefaultTtl(getDefaultTtl())
            .setMaxTtl(getMaxTtl())
            .setNegativeCaching(getNegativeCaching())
            .addAllNegativeCachingPolicy(getNegativeCachingPolicy().stream()
                .map(BackendBucketCdnNegativeCachingPolicy::toBackendBucketCdnPolicyNegativeCachingPolicy)
                .collect(Collectors.toList()))
            .setRequestCoalescing(getRequestCoalescing())
            .setServeWhileStale(getServeWhileStale())
            .addAllSignedUrlKeyNames(getSignedUrlKeyNames())
            .build();
            System.out.println("\nnegative caching disabled !!\n");
            builder.setNegativeCaching(false);
    }
}
