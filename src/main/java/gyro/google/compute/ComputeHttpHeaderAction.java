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

import com.google.api.services.compute.model.HttpHeaderAction;
import gyro.core.resource.Diffable;
import gyro.google.Copyable;

public class ComputeHttpHeaderAction extends Diffable implements Copyable<HttpHeaderAction> {

    /**
     * Headers to add to a matching request prior to forwarding the request to the backendService.
     */
    private List<ComputeHttpHeaderOption> requestHeadersToAdd;

    /**
     * A list of header names for headers that need to be removed from the request prior to forwarding
     * the request to the backendService.
     */
    private List<String> requestHeadersToRemove;

    /**
     * Headers to add the response prior to sending the response back to the client.
     */
    private List<ComputeHttpHeaderOption> responseHeadersToAdd;

    /**
     * A list of header names for headers that need to be removed from the response prior to sending
     * the response back to the client.
     */
    private List<String> responseHeadersToRemove;

    @Override
    public void copyFrom(HttpHeaderAction model) {

    }
}
