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

package gyro.google.cloudsql;

import java.util.ArrayList;
import java.util.List;

import com.google.api.services.sqladmin.model.PscConfig;
import gyro.core.resource.Diffable;
import gyro.core.resource.Updatable;
import gyro.core.validation.DependsOn;
import gyro.core.validation.Required;
import gyro.core.validation.ValidationError;
import gyro.google.Copyable;

public class DbPscConfig extends Diffable implements Copyable<PscConfig> {

    private List<String> allowedConsumerProjects;
    private Boolean pscEnabled;

    /**
     * The list of consumer projects that are allow-listed for PSC connections to this instance.
     */
    @Updatable
    @DependsOn("psc-enabled")
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
     * When set to ``true``, PSC is enabled.
     */
    @Required
    @Updatable
    public Boolean getPscEnabled() {
        return pscEnabled;
    }

    public void setPscEnabled(Boolean pscEnabled) {
        this.pscEnabled = pscEnabled;
    }

    @Override
    public void copyFrom(PscConfig model) throws Exception {
        setAllowedConsumerProjects(model.getAllowedConsumerProjects());
        setPscEnabled(model.getPscEnabled());
    }

    @Override
    public String primaryKey() {
        return "";
    }

    public PscConfig toPscConfig() {
        PscConfig config = new PscConfig();
        config.setPscEnabled(getPscEnabled());

        if (getAllowedConsumerProjects() != null) {
            config.setAllowedConsumerProjects(getAllowedConsumerProjects());
        }

        return config;
    }

    @Override
    public List<ValidationError> validate() {
        List<ValidationError> errors = new ArrayList<>();

        if (!getPscEnabled() && !getAllowedConsumerProjects().isEmpty()) {
            errors.add(new ValidationError(
                this,
                "allowed-consumer-projects",
                "'psc-enabled' should be set to 'true' in order to set 'allowed-consumer-projects'."));
        }

        return errors;
    }
}
