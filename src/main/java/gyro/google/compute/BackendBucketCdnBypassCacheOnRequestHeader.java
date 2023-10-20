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

import com.google.cloud.compute.v1.BackendBucketCdnPolicyBypassCacheOnRequestHeader;
import gyro.core.resource.Diffable;
import gyro.core.validation.Required;
import gyro.google.Copyable;

public class BackendBucketCdnBypassCacheOnRequestHeader extends Diffable implements Copyable<BackendBucketCdnPolicyBypassCacheOnRequestHeader> {

    private String headerName;

    /**
     * The header name to match on when bypassing cache.
     */
    @Required
    public String getHeaderName() {
        return headerName;
    }

    public void setHeaderName(String headerName) {
        this.headerName = headerName;
    }

    @Override
    public void copyFrom(BackendBucketCdnPolicyBypassCacheOnRequestHeader model) {
        setHeaderName(model.getHeaderName());
    }

    @Override
    public String primaryKey() {
        return headerName != null ? getHeaderName() : "";
    }

    BackendBucketCdnPolicyBypassCacheOnRequestHeader toBackendBucketCdnPolicyBypassCacheOnRequestHeader() {
        return BackendBucketCdnPolicyBypassCacheOnRequestHeader.newBuilder()
            .setHeaderName(getHeaderName())
            .build();
    }
}
