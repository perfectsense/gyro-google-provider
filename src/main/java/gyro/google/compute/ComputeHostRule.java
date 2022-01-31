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

import com.google.cloud.compute.v1.HostRule;
import gyro.core.resource.Diffable;
import gyro.core.resource.Updatable;
import gyro.core.validation.Required;
import gyro.google.Copyable;

public class ComputeHostRule extends Diffable implements Copyable<HostRule> {

    private String description;
    private List<String> hosts;
    private String pathMatcher;

    /**
     * An optional description of this host rule.
     */
    @Updatable
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * The list of host patterns to match. They must be valid hostnames, except ``*`` will match any string of lowercase letters, numbers, ``-``, and ``.``. In that case, ``*`` must be the first character and must be followed in the pattern by either ``-`` or ``.``.
     */
    @Required
    @Updatable
    public List<String> getHosts() {
        if (hosts == null) {
            hosts = new ArrayList<>();
        }
        return hosts;
    }

    public void setHosts(List<String> hosts) {
        this.hosts = hosts;
    }

    /**
     * The name of the path matcher to use to match the path portion of the URL if the host rule matches the URL's host portion.
     */
    @Required
    @Updatable
    public String getPathMatcher() {
        return pathMatcher;
    }

    public void setPathMatcher(String pathMatcher) {
        this.pathMatcher = pathMatcher;
    }

    @Override
    public String primaryKey() {
        return String.format("%s;%s", String.join(";", getHosts()), getPathMatcher() == null ? "" : getPathMatcher());
    }

    @Override
    public void copyFrom(HostRule model) {
        if (model.hasDescription()) {
            setDescription(model.getDescription());
        }

        if (model.hasPathMatcher()) {
            setPathMatcher(model.getPathMatcher());
        }

        setHosts(model.getHostsList());
    }

    public HostRule copyTo() {
        HostRule.Builder builder = HostRule.newBuilder();
        builder.addAllHosts(getHosts());

        if (getPathMatcher() != null) {
            builder.setPathMatcher(getPathMatcher());
        }

        if (getDescription() != null) {
            builder.setDescription(getDescription());
        }

        return builder.build();
    }
}
