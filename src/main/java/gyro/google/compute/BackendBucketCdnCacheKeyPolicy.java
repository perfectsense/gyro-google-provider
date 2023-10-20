/*
 * Copyright 2023, Brightspot.
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

import com.google.cloud.compute.v1.BackendBucketCdnPolicyCacheKeyPolicy;
import gyro.core.resource.Diffable;
import gyro.core.resource.Updatable;
import gyro.google.Copyable;

public class BackendBucketCdnCacheKeyPolicy extends Diffable implements Copyable<BackendBucketCdnPolicyCacheKeyPolicy> {

    private List<String> includeHttpHeaders;
    private List<String> queryStringWhitelist;

    /**
     * Headers to include in cache keys according to include_http_headers.
     */
    @Updatable
    public List<String> getIncludeHttpHeaders() {
        if (includeHttpHeaders == null) {
            includeHttpHeaders = new ArrayList<>();
        }

        return includeHttpHeaders;
    }

    public void setIncludeHttpHeaders(List<String> includeHttpHeaders) {
        this.includeHttpHeaders = includeHttpHeaders;
    }

    /**
     * Names of query string parameters to include in cache keys.
     */
    @Updatable
    public List<String> getQueryStringWhitelist() {
        if (queryStringWhitelist == null) {
            queryStringWhitelist = new ArrayList<>();
        }

        return queryStringWhitelist;
    }

    public void setQueryStringWhitelist(List<String> queryStringWhitelist) {
        this.queryStringWhitelist = queryStringWhitelist;
    }

    @Override
    public void copyFrom(BackendBucketCdnPolicyCacheKeyPolicy model) {
        setIncludeHttpHeaders(model.getIncludeHttpHeadersList());
        setQueryStringWhitelist(model.getQueryStringWhitelistList());
    }

    BackendBucketCdnPolicyCacheKeyPolicy toBackendBucketCdnPolicyCacheKeyPolicy() {
        BackendBucketCdnPolicyCacheKeyPolicy.Builder builder = BackendBucketCdnPolicyCacheKeyPolicy.newBuilder();
        builder.addAllIncludeHttpHeaders(getIncludeHttpHeaders());
        builder.addAllQueryStringWhitelist(getQueryStringWhitelist());
        return builder.build();
    }

    @Override
    public String primaryKey() {
        return "";
    }
}
