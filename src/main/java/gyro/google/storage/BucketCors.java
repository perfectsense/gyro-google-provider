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

import com.google.api.services.storage.model.Bucket.Cors;
import gyro.core.resource.Diffable;
import gyro.core.resource.Updatable;
import gyro.core.validation.ValidStrings;
import gyro.google.Copyable;

import java.util.List;

/**
 * Subresource for setting of Bucket.Cors configuration for assets within a Bucket.
 */
public class BucketCors extends Diffable implements Copyable<Cors> {

    private Integer maxAgeSeconds;
    private List<String> method;
    private List<String> origin;
    private List<String> responseHeader;

    /**
     * The value quantified in seconds to be returned in the "Access-Control-Max-Age" header.
     */
    @Updatable
    public Integer getMaxAgeSeconds() {
        return maxAgeSeconds;
    }

    public void setMaxAgeSeconds(Integer maxAgeSeconds) {
        this.maxAgeSeconds = maxAgeSeconds;
    }

    /**
     * List of HTTP methods in which to include CORS response headers. Valid values are ``GET``, ``HEAD``, ``POST``, ``MATCH``, ``PUT``, ``DELETE``, ``CONNECT``, ``OPTIONS``, ``TRACE``, ``PATCH`` or ``*`` for all methods.
     */
    @Updatable
    @ValidStrings({"GET", "HEAD", "POST", "MATCH", "PUT", "DELETE", "CONNECT", "OPTIONS", "TRACE", "PATCH", "*"})
    public List<String> getMethod() {
        return method;
    }

    public void setMethod(List<String> method) {
        this.method = method;
    }

    /**
     * List of Origins able to receive CORS response headers. The ``*`` value is also permitted for allowing any origin.
     */
    @Updatable
    public List<String> getOrigin() {
        return origin;
    }

    public void setOrigin(List<String> origin) {
        this.origin = origin;
    }

    /**
     * List of HTTP headers other than the simple response headers giving permission for the user-agent to share across domains.
     */
    @Updatable
    public List<String> getResponseHeader() {
        return responseHeader;
    }

    public void setResponseHeader(List<String> responseHeader) {
        this.responseHeader = responseHeader;
    }

    @Override
    public void copyFrom(Cors model) {
        setMaxAgeSeconds(model.getMaxAgeSeconds());
        setMethod(model.getMethod());
        setOrigin(model.getOrigin());
        setResponseHeader(model.getResponseHeader());
    }

    public Cors toBucketCors() {
        return new Cors()
                .setMaxAgeSeconds(getMaxAgeSeconds())
                .setMethod(getMethod())
                .setOrigin(getOrigin())
                .setResponseHeader(getResponseHeader());
    }
}
