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

import java.util.List;

import com.google.api.services.compute.model.HttpRetryPolicy;
import gyro.core.resource.Diffable;
import gyro.google.Copyable;

public class ComputeHttpRetryPolicy extends Diffable implements Copyable<HttpRetryPolicy> {

    /**
     * Specifies the allowed number retries. This number must be > 0.
     */
    private Long numRetries;

    /**
     * Specifies a non-zero timeout per retry attempt.
     */
    private ComputeDuration perTryTimeout;

    /**
     * Specfies one or more conditions when this retry rule applies. Valid values are: - 5xx:
     * Loadbalancer will attempt a retry if the backend service responds with any 5xx response code,
     * or if the backend service does not respond at all, example: disconnects, reset, read timeout,
     * connection failure, and refused streams.  - gateway-error: Similar to 5xx, but only applies to
     * response codes 502, 503 or 504. -  - connect-failure: Loadbalancer will retry on failures
     * connecting to backend services, for example due to connection timeouts.  - retriable-4xx:
     * Loadbalancer will retry for retriable 4xx response codes. Currently the only retriable error
     * supported is 409.  - refused-stream:Loadbalancer will retry if the backend service resets the
     * stream with a REFUSED_STREAM error code. This reset type indicates that it is safe to retry.  -
     * cancelledLoadbalancer will retry if the gRPC status code in the response header is set to
     * cancelled  - deadline-exceeded: Loadbalancer will retry if the gRPC status code in the response
     * header is set to deadline-exceeded  - resource-exhausted: Loadbalancer will retry if the gRPC
     * status code in the response header is set to resource-exhausted  - unavailable: Loadbalancer
     * will retry if the gRPC status code in the response header is set to unavailable
     */
    private List<String> retryConditions;

    @Override
    public void copyFrom(HttpRetryPolicy model) {

    }
}
