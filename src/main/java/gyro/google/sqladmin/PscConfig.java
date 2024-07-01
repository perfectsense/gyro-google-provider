/*
 * Copyright 2024, Brightspot.
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

package gyro.google.sqladmin;

import java.util.ArrayList;
import java.util.List;

import gyro.core.resource.Diffable;
import gyro.google.Copyable;

public class PscConfig extends Diffable implements Copyable<com.google.api.services.sqladmin.model.PscConfig> {

    private List<String> allowedConsumerProjects;

    private Boolean pscEnabled;

    /**
     * Optional. The list of consumer projects that are allow-listed for PSC connections to this instance. This instance can be connected to with PSC from any network in these projects. Each consumer project in this list may be represented by a project number (numeric) or by a project id (alphanumeric).
     */
    public List<String> getAllowedConsumerProjects() {
        if (allowedConsumerProjects == null) {
            allowedConsumerProjects = new ArrayList<>();
        }

        return allowedConsumerProjects;
    }

    public void setAllowedConsumerProjects(List<String> allowedConsumerProjects) {
        this.allowedConsumerProjects = allowedConsumerProjects;
    }

    /**
     * Whether PSC connectivity is enabled for this instance.
     */
    public Boolean getPscEnabled() {
        return pscEnabled;
    }

    public void setPscEnabled(Boolean pscEnabled) {
        this.pscEnabled = pscEnabled;
    }

    @Override
    public String primaryKey() {
        return "";
    }

    @Override
    public void copyFrom(com.google.api.services.sqladmin.model.PscConfig model) {
        setPscEnabled(model.getPscEnabled());
        setAllowedConsumerProjects(model.getAllowedConsumerProjects());
    }

    com.google.api.services.sqladmin.model.PscConfig copyTo() {
        com.google.api.services.sqladmin.model.PscConfig pscConfig = new com.google.api.services.sqladmin.model.PscConfig();
        pscConfig.setPscEnabled(getPscEnabled());
        pscConfig.setAllowedConsumerProjects(getAllowedConsumerProjects());

        return pscConfig;
    }
}
