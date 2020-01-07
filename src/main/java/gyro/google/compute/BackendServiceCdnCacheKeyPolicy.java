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

import com.google.api.services.compute.model.CacheKeyPolicy;
import gyro.core.resource.Diffable;
import gyro.core.resource.Updatable;
import gyro.google.Copyable;

public class BackendServiceCdnCacheKeyPolicy extends Diffable implements Copyable<CacheKeyPolicy> {

    private Boolean includeHost;
    private Boolean includeProtocol;
    private Boolean includeQueryString;
    private List<String> queryStringBlacklist;
    private List<String> queryStringWhitelist;

    /**
     * If true, requests to different hosts will be cached separately.
     */
    @Updatable
    public Boolean getIncludeHost() {
        return includeHost;
    }

    public void setIncludeHost(Boolean includeHost) {
        this.includeHost = includeHost;
    }

    /**
     * If true, http and https requests will be cached separately.
     */
    @Updatable
    public Boolean getIncludeProtocol() {
        return includeProtocol;
    }

    public void setIncludeProtocol(Boolean includeProtocol) {
        this.includeProtocol = includeProtocol;
    }

    /**
     * If true, include query string parameters in the cache key according to query_string_whitelist or query_string_blacklist
     */
    @Updatable
    public Boolean getIncludeQueryString() {
        return includeQueryString;
    }

    public void setIncludeQueryString(Boolean includeQueryString) {
        this.includeQueryString = includeQueryString;
    }

    /**
     * Names of query string parameters to exclude from cache keys. Conflicts with ``query-string-white-list``.
     */
    @Updatable
    public List<String> getQueryStringBlacklist() {
        if (queryStringBlacklist == null) {
            queryStringBlacklist = new ArrayList<>();
        }

        return queryStringBlacklist;
    }

    public void setQueryStringBlacklist(List<String> queryStringBlacklist) {
        this.queryStringBlacklist = queryStringBlacklist;
    }

    /**
     * Names of query string parameters to include in cache keys. Conflicts with ``query-string-black-list``.
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
    public String primaryKey() {
        return "";
    }

    @Override
    public void copyFrom(CacheKeyPolicy policy) {
        setIncludeHost(policy.getIncludeHost());
        setIncludeProtocol(policy.getIncludeProtocol());
        setIncludeQueryString(policy.getIncludeQueryString());
        setQueryStringBlacklist(policy.getQueryStringBlacklist());
        setQueryStringWhitelist(policy.getQueryStringWhitelist());
    }

    CacheKeyPolicy toCacheKeyPolicy() {
        return new CacheKeyPolicy().setIncludeHost(getIncludeHost())
            .setIncludeProtocol(getIncludeProtocol())
            .setIncludeQueryString(getIncludeQueryString())
            .setQueryStringBlacklist(getQueryStringBlacklist())
            .setQueryStringWhitelist(getQueryStringWhitelist());
    }
}
