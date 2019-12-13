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

import com.google.api.services.compute.model.HttpHeaderAction;
import com.google.api.services.compute.model.WeightedBackendService;
import gyro.core.resource.Diffable;
import gyro.google.Copyable;

public class ComputeWeightedBackendService extends Diffable implements Copyable<WeightedBackendService> {

    /**
     * The full or partial URL to the default BackendService resource. Before forwarding the request
     * to backendService, the loadbalancer applies any relevant headerActions specified as part of
     * this backendServiceWeight.
     */
    private String backendService;

    /**
     * Specifies changes to request and response headers that need to take effect for the selected
     * backendService. headerAction specified here take effect before headerAction in the enclosing
     * HttpRouteRule, PathMatcher and UrlMap.
     */
    private HttpHeaderAction headerAction;

    /**
     * Specifies the fraction of traffic sent to backendService, computed as weight / (sum of all
     * weightedBackendService weights in routeAction) . The selection of a backend service is
     * determined only for new traffic. Once a user's request has been directed to a backendService,
     * subsequent requests will be sent to the same backendService as determined by the
     * BackendService's session affinity policy. The value must be between 0 and 1000
     */
    private Long weight;

    @Override
    public void copyFrom(WeightedBackendService model) {

    }
}
