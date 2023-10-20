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

import com.google.cloud.compute.v1.BackendBucketCdnPolicyNegativeCachingPolicy;
import gyro.core.resource.Diffable;
import gyro.core.validation.Required;
import gyro.google.Copyable;

public class BackendBucketCdnNegativeCachingPolicy extends Diffable implements Copyable<BackendBucketCdnPolicyNegativeCachingPolicy> {

    private Integer code;
    private Integer ttl;

    /**
     * The HTTP status code to define custom set of rules.
     */
    @Required
    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    /**
     * The TTL for the negative response.
     */
    @Required
    public Integer getTtl() {
        return ttl;
    }

    public void setTtl(Integer ttl) {
        this.ttl = ttl;
    }

    @Override
    public void copyFrom(BackendBucketCdnPolicyNegativeCachingPolicy model) {
        setCode(model.getCode());
        setTtl(model.getTtl());
    }

    @Override
    public String primaryKey() {
        return String.format("Code: %s, TTL: %s", getCode() != null ? getCode() : 0, getTtl() != null ? getTtl() : 0);
    }

    BackendBucketCdnPolicyNegativeCachingPolicy toBackendBucketCdnPolicyNegativeCachingPolicy() {
        return BackendBucketCdnPolicyNegativeCachingPolicy.newBuilder()
            .setCode(getCode())
            .setTtl(getTtl())
            .build();
    }
}
