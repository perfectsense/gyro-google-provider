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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.google.cloud.compute.v1.TargetHttpProxy;
import gyro.core.resource.Id;
import gyro.core.resource.Output;
import gyro.core.resource.Updatable;
import gyro.core.validation.ConflictsWith;
import gyro.core.validation.Regex;
import gyro.core.validation.Required;
import gyro.core.validation.ValidationError;
import gyro.google.Copyable;

public abstract class AbstractTargetHttpProxyResource extends ComputeResource implements Copyable<TargetHttpProxy> {

    private String name;
    private String description;
    private UrlMapResource urlMap;
    private RegionUrlMapResource regionUrlMap;

    // Read-only
    private String selfLink;

    /**
     * The name of the target proxy.
     */
    @Required
    @Regex(value = "^[a-z]([-a-z0-9]{1,61}[a-z0-9])?", message = "a string 1-63 characters long and the first character must be a lowercase letter, and all following characters must be a dash, lowercase letter, or digit, except the last character, which cannot be a dash")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * Description of the target proxy.
     */
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * The URL map resource that defines the mapping from URL to a backend service or bucket.
     */
    @Updatable
    @ConflictsWith("region-url-map")
    public UrlMapResource getUrlMap() {
        return urlMap;
    }

    public void setUrlMap(UrlMapResource urlMap) {
        this.urlMap = urlMap;
    }

    /**
     * The region URL map resource that defines the mapping from URL to a backend service or bucket.
     */
    @Updatable
    @ConflictsWith("url-map")
    public RegionUrlMapResource getRegionUrlMap() {
        return regionUrlMap;
    }

    public void setRegionUrlMap(RegionUrlMapResource regionUrlMap) {
        this.regionUrlMap = regionUrlMap;
    }

    /**
     * Server-defined URL for the target proxy.
     */
    @Id
    @Output
    public String getSelfLink() {
        return selfLink;
    }

    public void setSelfLink(String selfLink) {
        this.selfLink = selfLink;
    }

    @Override
    public void copyFrom(TargetHttpProxy model) {
        setName(model.getName());

        if (model.hasDescription()) {
            setDescription(model.getDescription());
        }

        if (model.hasSelfLink()) {
            setSelfLink(model.getSelfLink());
        }

        setUrlMap(null);
        if (model.hasUrlMap() && UrlMapResource.isUrlMap(model.getUrlMap())) {
            setUrlMap(findById(UrlMapResource.class, model.getUrlMap()));
        }
    }

    TargetHttpProxy toTargetHttpProxy() {
        TargetHttpProxy.Builder builder = TargetHttpProxy.newBuilder().setName(getName());

        if (getDescription() != null) {
            builder.setDescription(getDescription());
        }

        if (getUrlMap() != null) {
            builder.setUrlMap(getUrlMapSelfLink()).build();
        }

        return builder.build();
    }

    String getUrlMapSelfLink() {
        if (getUrlMap() != null) {
            return getUrlMap().getSelfLink();
        } else if (getRegionUrlMap() != null) {
            return getRegionUrlMap().getSelfLink();
        }
        return null;
    }

    @Override
    public List<ValidationError> validate(Set<String> configuredFields) {
        List<ValidationError> errors = new ArrayList<>();

        if (getUrlMap() == null && getRegionUrlMap() == null) {
            errors.add(new ValidationError(
                this,
                null,
                "Either 'url-map' or 'region-url-map' is required!"));
        }
        return errors;
    }
}
