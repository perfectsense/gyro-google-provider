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

import com.google.api.services.compute.model.HttpHeaderMatch;
import com.google.api.services.compute.model.Int64RangeMatch;
import gyro.core.resource.Diffable;
import gyro.google.Copyable;

public class ComputeHttpHeaderMatch extends Diffable implements Copyable<HttpHeaderMatch> {

    /**
     * The value should exactly match contents of exactMatch. Only one of exactMatch, prefixMatch,
     * suffixMatch, regexMatch, presentMatch or rangeMatch must be set.
     */
    private java.lang.String exactMatch;

    /**
     * The name of the HTTP header to match. For matching against the HTTP request's authority, use a
     * headerMatch with the header name ":authority". For matching a request's method, use the
     * headerName ":method".
     */
    private java.lang.String headerName;

    /**
     * If set to false, the headerMatch is considered a match if the match criteria above are met. If
     * set to true, the headerMatch is considered a match if the match criteria above are NOT met. The
     * default setting is false.
     */
    private java.lang.Boolean invertMatch;

    /**
     * The value of the header must start with the contents of prefixMatch. Only one of exactMatch,
     * prefixMatch, suffixMatch, regexMatch, presentMatch or rangeMatch must be set.
     */
    private java.lang.String prefixMatch;

    /**
     * A header with the contents of headerName must exist. The match takes place whether or not the
     * request's header has a value or not. Only one of exactMatch, prefixMatch, suffixMatch,
     * regexMatch, presentMatch or rangeMatch must be set.
     */
    private java.lang.Boolean presentMatch;

    /**
     * The header value must be an integer and its value must be in the range specified in rangeMatch.
     * If the header does not contain an integer, number or is empty, the match fails. For example for
     * a range [-5, 0]   - -3 will match.  - 0 will not match.  - 0.25 will not match.  - -3someString
     * will not match.   Only one of exactMatch, prefixMatch, suffixMatch, regexMatch, presentMatch or
     * rangeMatch must be set.
     */
    private Int64RangeMatch rangeMatch;

    /**
     * The value of the header must match the regualar expression specified in regexMatch. For regular
     * expression grammar, please see:  en.cppreference.com/w/cpp/regex/ecmascript For matching
     * against a port specified in the HTTP request, use a headerMatch with headerName set to PORT and
     * a regular expression that satisfies the RFC2616 Host header's port specifier. Only one of
     * exactMatch, prefixMatch, suffixMatch, regexMatch, presentMatch or rangeMatch must be set.
     */
    private java.lang.String regexMatch;

    /**
     * The value of the header must end with the contents of suffixMatch. Only one of exactMatch,
     * prefixMatch, suffixMatch, regexMatch, presentMatch or rangeMatch must be set.
     */
    private java.lang.String suffixMatch;

    @Override
    public void copyFrom(HttpHeaderMatch model) {

    }
}
