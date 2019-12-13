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

import com.google.api.services.compute.model.TargetHttpProxy;
import gyro.core.resource.Id;
import gyro.core.resource.Output;
import gyro.core.validation.Required;
import gyro.google.Copyable;

public abstract class TargetHttpProxyResource extends ComputeResource implements Copyable<TargetHttpProxy> {

    private String description;

    private String name;

    private String region;

    private String selfLink;

    private UrlMapResource urlMap;

    /**
     * An optional description of this resource. Provide this property when you create the resource.
     */
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Name of the resource. Provided by the client when the resource is created. The name must be
     * 1-63 characters long, and comply with RFC1035. Specifically, the name must be 1-63 characters
     * long and match the regular expression `[a-z]([-a-z0-9]*[a-z0-9])?` which means the first
     * character must be a lowercase letter, and all following characters must be a dash, lowercase
     * letter, or digit, except the last character, which cannot be a dash.
     */
    @Required
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * URL of the region where the regional Target HTTP Proxy resides. This field is not
     * applicable to global Target HTTP Proxies.
     */
    @Output
    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    /**
     * Server-defined URL for the resource.
     */
    @Id
    @Output
    public String getSelfLink() {
        return selfLink;
    }

    public void setSelfLink(String selfLink) {
        this.selfLink = selfLink;
    }

    /**
     * URL to the UrlMap resource that defines the mapping from URL to the BackendService.
     */
    public UrlMapResource getUrlMap() {
        return urlMap;
    }

    public void setUrlMap(UrlMapResource urlMap) {
        this.urlMap = urlMap;
    }

    @Override
    public void copyFrom(TargetHttpProxy model) {
        setDescription(model.getDescription());
        setName(model.getName());
        setRegion(model.getRegion());
        setSelfLink(model.getSelfLink());
        UrlMapResource urlMapResource = null;
        String urlMap = model.getUrlMap();

        if (urlMap != null) {
            urlMapResource = findById(UrlMapResource.class, urlMap);
        }
        setUrlMap(urlMapResource);
    }
}
