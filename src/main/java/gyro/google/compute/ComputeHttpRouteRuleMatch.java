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

import com.google.api.services.compute.model.HttpRouteRuleMatch;
import gyro.core.resource.Diffable;
import gyro.google.Copyable;

public class ComputeHttpRouteRuleMatch extends Diffable implements Copyable<HttpRouteRuleMatch> {

    /**
     * For satifying the matchRule condition, the path of the request must exactly match the value
     * specified in fullPathMatch after removing any query parameters and anchor that may be part of
     * the original URL. FullPathMatch must be between 1 and 1024 characters. Only one of prefixMatch,
     * fullPathMatch or regexMatch must be specified.
     */
    private java.lang.String fullPathMatch;

    /**
     * Specifies a list of header match criteria, all of which must match corresponding headers in the
     * request.
     */
    private java.util.List<ComputeHttpHeaderMatch> headerMatches;

    //    static {
    //        // hack to force ProGuard to consider HttpHeaderMatch used, since otherwise it would be stripped out
    //        // see https://github.com/google/google-api-java-client/issues/543
    //        com.google.api.client.util.Data.nullOf(HttpHeaderMatch.class);
    //    }

    /**
     * Specifies that prefixMatch and fullPathMatch matches are case sensitive. The default value is
     * false. caseSensitive must not be used with regexMatch.
     */
    private java.lang.Boolean ignoreCase;

    /**
     * Opaque filter criteria used by Loadbalancer to restrict routing configuration to a limited set
     * xDS compliant clients. In their xDS requests to Loadbalancer, xDS clients present node
     * metadata. If a match takes place, the relevant routing configuration is made available to those
     * proxies. For each metadataFilter in this list, if its filterMatchCriteria is set to MATCH_ANY,
     * at least one of the filterLabels must match the corresponding label provided in the metadata.
     * If its filterMatchCriteria is set to MATCH_ALL, then all of its filterLabels must match with
     * corresponding labels in the provided metadata. metadataFilters specified here can be overrides
     * those specified in ForwardingRule that refers to this UrlMap. metadataFilters only applies to
     * Loadbalancers that have their loadBalancingScheme set to INTERNAL_SELF_MANAGED.
     */
    private java.util.List<ComputeMetadataFilter> metadataFilters;

    /**
     * For satifying the matchRule condition, the request's path must begin with the specified
     * prefixMatch. prefixMatch must begin with a /. The value must be between 1 and 1024 characters.
     * Only one of prefixMatch, fullPathMatch or regexMatch must be specified.
     */
    private java.lang.String prefixMatch;

    /**
     * Specifies a list of query parameter match criteria, all of which must match corresponding query
     * parameters in the request.
     */
    private java.util.List<ComputeHttpQueryParameterMatch> queryParameterMatches;

    //    static {
    //        // hack to force ProGuard to consider HttpQueryParameterMatch used, since otherwise it would be stripped out
    //        // see https://github.com/google/google-api-java-client/issues/543
    //        com.google.api.client.util.Data.nullOf(HttpQueryParameterMatch.class);
    //    }

    /**
     * For satifying the matchRule condition, the path of the request must satisfy the regular
     * expression specified in regexMatch after removing any query parameters and anchor supplied with
     * the original URL. For regular expression grammar please see
     * en.cppreference.com/w/cpp/regex/ecmascript Only one of prefixMatch, fullPathMatch or regexMatch
     * must be specified.
     */
    private java.lang.String regexMatch;

    @Override
    public void copyFrom(HttpRouteRuleMatch model) {

    }
}
