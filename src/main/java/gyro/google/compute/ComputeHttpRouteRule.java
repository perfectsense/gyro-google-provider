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

import com.google.api.services.compute.model.HttpRouteRule;
import gyro.core.resource.Diffable;
import gyro.google.Copyable;

public class ComputeHttpRouteRule extends Diffable implements Copyable<HttpRouteRule> {

    /**
     * Specifies changes to request and response headers that need to take effect for the selected
     * backendService. The headerAction specified here are applied before the matching
     * pathMatchers[].headerAction and after pathMatchers[].routeRules[].routeAction.weightedBackendSe
     * rvice.backendServiceWeightAction[].headerAction
     */
    private ComputeHttpHeaderAction headerAction;

    /**
     */
    private List<ComputeHttpRouteRuleMatch> matchRules;

    /**
     * In response to a matching matchRule, the load balancer performs advanced routing actions like
     * URL rewrites, header transformations, etc. prior to forwarding the request to the selected
     * backend. If  routeAction specifies any  weightedBackendServices, service must not be set.
     * Conversely if service is set, routeAction cannot contain any  weightedBackendServices. Only one
     * of routeAction or urlRedirect must be set.
     */
    private ComputeHttpRouteAction routeAction;

    /**
     * The full or partial URL of the backend service resource to which traffic is directed if this
     * rule is matched. If routeAction is additionally specified, advanced routing actions like URL
     * Rewrites, etc. take effect prior to sending the request to the backend. However, if service is
     * specified, routeAction cannot contain any weightedBackendService s. Conversely, if routeAction
     * specifies any  weightedBackendServices, service must not be specified. Only one of urlRedirect,
     * service or routeAction.weightedBackendService must be set.
     */
    private String service;

    /**
     * When this rule is matched, the request is redirected to a URL specified by urlRedirect. If
     * urlRedirect is specified, service or routeAction must not be set.
     */
    private ComputeHttpRedirectAction urlRedirect;

    @Override
    public void copyFrom(HttpRouteRule model) {

    }
}
