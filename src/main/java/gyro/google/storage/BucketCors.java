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

package gyro.google.storage;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.google.cloud.StringEnumValue;
import com.google.cloud.storage.Cors;
import com.google.cloud.storage.HttpMethod;
import gyro.core.resource.Diffable;
import gyro.core.validation.ValidStrings;
import gyro.google.Copyable;

/**
 * Subresource for setting of Bucket.Cors configuration for assets within a Bucket.
 */
public class BucketCors extends Diffable implements Copyable<Cors> {

    private Integer maxAgeSeconds;
    private List<String> method;
    private List<String> origin;
    private List<String> responseHeader;

    /**
     * The value quantified in seconds to be returned in the ``Access-Control-Max-Age`` header.
     */
    public Integer getMaxAgeSeconds() {
        return maxAgeSeconds;
    }

    public void setMaxAgeSeconds(Integer maxAgeSeconds) {
        this.maxAgeSeconds = maxAgeSeconds;
    }

    /**
     * List of HTTP methods to include CORS response headers.
     */
    @ValidStrings({ "GET", "HEAD", "POST", "MATCH", "PUT", "DELETE", "CONNECT", "OPTIONS", "TRACE", "PATCH", "*" })
    public List<String> getMethod() {
        return method;
    }

    public void setMethod(List<String> method) {
        this.method = method;
    }

    /**
     * List of Origins able to receive CORS response headers. The ``*`` value is also permitted for allowing any origin.
     */
    public List<String> getOrigin() {
        return origin;
    }

    public void setOrigin(List<String> origin) {
        this.origin = origin;
    }

    /**
     * List of HTTP headers other than the simple response headers giving permission for the user-agent to share across domains.
     */
    public List<String> getResponseHeader() {
        return responseHeader;
    }

    public void setResponseHeader(List<String> responseHeader) {
        this.responseHeader = responseHeader;
    }

    @Override
    public String primaryKey() {
        ArrayList<String> values = new ArrayList<>();

        if (getMaxAgeSeconds() != null) {
            values.add(String.format("max-age-seconds = %d", getMaxAgeSeconds()));
        }

        if (getMethod() != null) {
            values.add(String.format("method(s) = [%s]", String.join(", ", getMethod())));
        }

        if (getOrigin() != null) {
            values.add(String.format("origin(s) = [%s]", String.join(", ", getOrigin())));
        }

        if (getResponseHeader() != null) {
            values.add(String.format("response-header(s) = [%s]", String.join(", ", getResponseHeader())));
        }

        return String.join("; ", values);
    }

    @Override
    public void copyFrom(Cors model) {
        setMaxAgeSeconds(model.getMaxAgeSeconds());
        setMethod(model.getMethods().stream().map(StringEnumValue::name).collect(Collectors.toList()));
        setOrigin(model.getOrigins().stream().map(Cors.Origin::getValue).collect(Collectors.toList()));
        setResponseHeader(model.getResponseHeaders());
    }

    public Cors toBucketCors() {
        List<HttpMethod> methods = getMethod().stream()
            .map(HttpMethod::valueOf)
            .collect(Collectors.toList());

        List<com.google.cloud.storage.Cors.Origin> origins = getOrigin().stream()
            .map(com.google.cloud.storage.Cors.Origin::of)
            .collect(Collectors.toList());

        return com.google.cloud.storage.Cors.newBuilder()
            .setMaxAgeSeconds(getMaxAgeSeconds())
            .setMethods(methods)
            .setOrigins(origins)
            .setResponseHeaders(getResponseHeader())
            .build();
    }
}
