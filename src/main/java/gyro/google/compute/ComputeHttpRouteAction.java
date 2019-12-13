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

import com.google.api.services.compute.model.HttpRouteAction;
import gyro.core.resource.Diffable;
import gyro.google.Copyable;

public class ComputeHttpRouteAction extends Diffable implements Copyable<HttpRouteAction> {

    /**
     * The specification for allowing client side cross-origin requests. Please see W3C Recommendation
     * for Cross Origin Resource Sharing
     */
    private ComputeCorsPolicy corsPolicy;

    /**
     * The specification for fault injection introduced into traffic to test the resiliency of clients
     * to backend service failure. As part of fault injection, when clients send requests to a backend
     * service, delays can be introduced by Loadbalancer on a percentage of requests before sending
     * those request to the backend service. Similarly requests from clients can be aborted by the
     * Loadbalancer for a percentage of requests. timeout and retry_policy will be ignored by clients
     * that are configured with a fault_injection_policy.
     *
     private HttpFaultInjection faultInjectionPolicy;
     */
    /**
     * Specifies the policy on how requests intended for the route's backends are shadowed to a
     * separate mirrored backend service. Loadbalancer does not wait for responses from the shadow
     * service. Prior to sending traffic to the shadow service, the host / authority header is
     * suffixed with -shadow.
     *
     private RequestMirrorPolicy requestMirrorPolicy;
     */
    /**
     * Specifies the retry policy associated with this route.
     */
    private ComputeHttpRetryPolicy retryPolicy;

    /**
     * Specifies the timeout for the selected route. Timeout is computed from the time the request is
     * has been fully processed (i.e. end-of-stream) up until the response has been completely
     * processed. Timeout includes all retries. If not specified, the default value is 15 seconds.
     */
    private ComputeDuration timeout;

    /**
     * The spec to modify the URL of the request, prior to forwarding the request to the matched
     * service
     */
    private ComputeUrlRewrite urlRewrite;

    /**
     * A list of weighted backend services to send traffic to when a route match occurs. The weights
     * determine the fraction of traffic that flows to their corresponding backend service. If all
     * traffic needs to go to a single backend service, there must be one  weightedBackendService with
     * weight set to a non 0 number. Once a backendService is identified and before forwarding the
     * request to the backend service, advanced routing actions like Url rewrites and header
     * transformations are applied depending on additional settings specified in this HttpRouteAction.
     */
    private List<ComputeWeightedBackendService> weightedBackendServices;

    @Override
    public void copyFrom(HttpRouteAction model) {

    }
}
