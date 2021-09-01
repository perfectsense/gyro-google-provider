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
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.api.client.util.Data;
import com.google.api.services.compute.model.HostRule;
import com.google.api.services.compute.model.PathMatcher;
import com.google.api.services.compute.model.UrlMap;
import gyro.core.resource.Id;
import gyro.core.resource.Output;
import gyro.core.resource.Updatable;
import gyro.core.validation.Regex;
import gyro.core.validation.Required;
import gyro.google.Copyable;

public abstract class AbstractUrlMapResource extends ComputeResource implements Copyable<UrlMap> {

    private String name;
    private String description;
    private List<ComputeHostRule> hostRule;
    private List<ComputePathMatcher> pathMatcher;
    private HttpRedirectAction defaultHttpRedirectAction;

    // Read-only
    private String selfLink;
    private String fingerprint;

    /**
     * The name of the URL map.
     */
    @Required
    @Regex(value = "(?:(?:[-a-z0-9]{1,63}\\.)*(?:[a-z](?:[-a-z0-9]{0,61}[a-z0-9])?):)?(?:[0-9]{1,19}|(?:[a-z0-9](?:[-a-z0-9]{0,61}[a-z0-9])?))", message = "a string 1-63 characters long and the first character must be a lowercase letter, and all following characters must be a dash, lowercase letter, or digit, except the last character, which cannot be a dash")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * An optional description of this URL map.
     */
    @Updatable
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * The list of host rules to use against the URL.
     *
     * @subresource gyro.google.compute.ComputeHostRule
     */
    @Updatable
    public List<ComputeHostRule> getHostRule() {
        if (hostRule == null) {
            hostRule = new ArrayList<>();
        }
        return hostRule;
    }

    public void setHostRule(List<ComputeHostRule> hostRule) {
        this.hostRule = hostRule;
    }

    /**
     * The list of named path matchers to use against the URL.
     *
     * @subresource gyro.google.compute.ComputePathMatcher
     */
    @Updatable
    public List<ComputePathMatcher> getPathMatcher() {
        if (pathMatcher == null) {
            pathMatcher = new ArrayList<>();
        }
        return pathMatcher;
    }

    public void setPathMatcher(List<ComputePathMatcher> pathMatcher) {
        this.pathMatcher = pathMatcher;
    }

    /**
     * The default http redirect action configuration.
     *
     * @subresource gyro.google.compute.HttpRedirectAction
     */
    public HttpRedirectAction getDefaultHttpRedirectAction() {
        return defaultHttpRedirectAction;
    }

    public void setDefaultHttpRedirectAction(HttpRedirectAction defaultHttpRedirectAction) {
        this.defaultHttpRedirectAction = defaultHttpRedirectAction;
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
     * The fingerprint for the URL map, which is a hash of the contents of the resource.
     */
    @Output
    public String getFingerprint() {
        return fingerprint;
    }

    public void setFingerprint(String fingerprint) {
        this.fingerprint = fingerprint;
    }

    @Override
    public void copyFrom(UrlMap urlMap) {
        setName(urlMap.getName());
        setDescription(urlMap.getDescription());
        setSelfLink(urlMap.getSelfLink());
        setFingerprint(urlMap.getFingerprint());

        List<ComputeHostRule> computeHostRules = null;
        List<HostRule> hostRules = urlMap.getHostRules();

        if (hostRules != null && !hostRules.isEmpty()) {
            computeHostRules = hostRules
                .stream()
                .map(hostRule -> {
                    ComputeHostRule computeHostRule = newSubresource(ComputeHostRule.class);
                    computeHostRule.copyFrom(hostRule);
                    return computeHostRule;
                })
                .collect(Collectors.toList());
        }
        setHostRule(computeHostRules);

        List<ComputePathMatcher> computePathMatchers = null;
        List<PathMatcher> pathMatchers = urlMap.getPathMatchers();

        if (pathMatchers != null && !pathMatchers.isEmpty()) {
            computePathMatchers = pathMatchers
                .stream()
                .map(pathMatcher -> {
                    ComputePathMatcher computePathMatcher = newSubresource(ComputePathMatcher.class);
                    computePathMatcher.copyFrom(pathMatcher);
                    return computePathMatcher;
                })
                .collect(Collectors.toList());
        }
        setPathMatcher(computePathMatchers);

        setDefaultHttpRedirectAction(null);
        if (urlMap.getDefaultUrlRedirect() != null) {
            HttpRedirectAction redirectAction = newSubresource(HttpRedirectAction.class);
            redirectAction.copyFrom(urlMap.getDefaultUrlRedirect());
            setDefaultHttpRedirectAction(redirectAction);
        }
    }

    protected UrlMap toUrlMap(Set<String> changedFieldNames) {
        boolean isUpdate = changedFieldNames != null && (changedFieldNames.size() > 0);

        UrlMap urlMap = new UrlMap();

        if (!isUpdate) {
            urlMap.setName(getName());
        }

        if (!isUpdate || changedFieldNames.contains("description")) {
            urlMap.setDescription(getDescription());
        }

        urlMap.setHostRules(Collections.singletonList(Data.nullOf(HostRule.class)));
        List<ComputeHostRule> hostRules = getHostRule();
        if (!hostRules.isEmpty()) {
            urlMap.setHostRules(hostRules.stream().map(ComputeHostRule::copyTo).collect(Collectors.toList()));
        }

        urlMap.setPathMatchers(Collections.singletonList(Data.nullOf(PathMatcher.class)));
        List<ComputePathMatcher> pathMatchers = getPathMatcher();
        if (!pathMatchers.isEmpty()) {
            urlMap.setPathMatchers(pathMatchers.stream().map(ComputePathMatcher::copyTo).collect(Collectors.toList()));
        }

        if (getDefaultHttpRedirectAction() != null) {
            urlMap.setDefaultUrlRedirect(getDefaultHttpRedirectAction().toHttpRedirectAction());
        }

        return urlMap;
    }
}
