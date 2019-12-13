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

import com.google.api.services.compute.model.CorsPolicy;
import gyro.core.resource.Diffable;
import gyro.google.Copyable;

public class ComputeCorsPolicy extends Diffable implements Copyable<CorsPolicy> {

    /**
     * In response to a preflight request, setting this to true indicates that the actual request can
     * include user credentials. This translates to the Access-Control-Allow-Credentials header.
     * Default is false.
     */
    private Boolean allowCredentials;

    /**
     * Specifies the content for the Access-Control-Allow-Headers header.
     */
    private List<String> allowHeaders;

    /**
     * Specifies the content for the Access-Control-Allow-Methods header.
     */
    private List<String> allowMethods;

    /**
     * Specifies the regualar expression patterns that match allowed origins. For regular expression
     * grammar please see en.cppreference.com/w/cpp/regex/ecmascript An origin is allowed if it
     * matches either allow_origins or allow_origin_regex.
     */
    private List<String> allowOriginRegexes;

    /**
     * Specifies the list of origins that will be allowed to do CORS requests. An origin is allowed if
     * it matches either allow_origins or allow_origin_regex.
     */
    private List<String> allowOrigins;

    /**
     * If true, specifies the CORS policy is disabled. The default value of false, which indicates
     * that the CORS policy is in effect.
     */
    private Boolean disabled;

    /**
     * Specifies the content for the Access-Control-Expose-Headers header.
     */
    private List<String> exposeHeaders;

    /**
     * Specifies how long the results of a preflight request can be cached. This translates to the
     * content for the Access-Control-Max-Age header.
     */
    private Integer maxAge;

    @Override
    public void copyFrom(CorsPolicy model) {

    }
}
