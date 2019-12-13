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

import com.google.api.services.compute.model.UrlRewrite;
import gyro.core.resource.Diffable;
import gyro.google.Copyable;

public class ComputeUrlRewrite extends Diffable implements Copyable<UrlRewrite> {

    /**
     * Prior to forwarding the request to the selected service, the request's host header is replaced
     * with contents of hostRewrite. The value must be between 1 and 255 characters.
     */
    private String hostRewrite;

    /**
     * Prior to forwarding the request to the selected backend service, the matching portion of the
     * request's path is replaced by pathPrefixRewrite. The value must be between 1 and 1024
     * characters.
     */
    private String pathPrefixRewrite;

    @Override
    public void copyFrom(UrlRewrite model) {

    }
}
