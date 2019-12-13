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

import com.google.api.services.compute.model.HttpRedirectAction;
import gyro.core.resource.Diffable;
import gyro.google.Copyable;

public class ComputeHttpRedirectAction extends Diffable implements Copyable<HttpRedirectAction> {

    /**
     * The host that will be used in the redirect response instead of the one that was supplied in the
     * request. The value must be between 1 and 255 characters.
     */
    private String hostRedirect;

    /**
     * If set to true, the URL scheme in the redirected request is set to https. If set to false, the
     * URL scheme of the redirected request will remain the same as that of the request. This must
     * only be set for UrlMaps used in TargetHttpProxys. Setting this true for TargetHttpsProxy is not
     * permitted. The default is set to false.
     */
    private Boolean httpsRedirect;

    /**
     * The path that will be used in the redirect response instead of the one that was supplied in the
     * request. Only one of pathRedirect or prefixRedirect must be specified. The value must be
     * between 1 and 1024 characters.
     */
    private String pathRedirect;

    /**
     * The prefix that replaces the prefixMatch specified in the HttpRouteRuleMatch, retaining the
     * remaining portion of the URL before redirecting the request.
     */
    private String prefixRedirect;

    /**
     * The HTTP Status code to use for this RedirectAction. Supported values are:   -
     * MOVED_PERMANENTLY_DEFAULT, which is the default value and corresponds to 301.  - FOUND, which
     * corresponds to 302.  - SEE_OTHER which corresponds to 303.  - TEMPORARY_REDIRECT, which
     * corresponds to 307. In this case, the request method will be retained.  - PERMANENT_REDIRECT,
     * which corresponds to 308. In this case, the request method will be retained.
     */
    private String redirectResponseCode;

    /**
     * If set to true, any accompanying query portion of the original URL is removed prior to
     * redirecting the request. If set to false, the query portion of the original URL is retained.
     * The default is set to false.
     */
    private Boolean stripQuery;

    @Override
    public void copyFrom(HttpRedirectAction model) {

    }
}
