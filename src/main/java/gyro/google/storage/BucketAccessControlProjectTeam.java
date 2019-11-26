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

import com.google.api.services.storage.model.BucketAccessControl.ProjectTeam;
import gyro.core.resource.Diffable;
import gyro.core.resource.Updatable;
import gyro.core.validation.ValidStrings;
import gyro.google.Copyable;

/**
 * The project team associated with the entity.
 */
public class BucketAccessControlProjectTeam extends Diffable implements Copyable<ProjectTeam> {

    private String projectNumber;
    private String team;

    /**
     * The project number.
     */
    @Updatable
    public String getProjectNumber() {
        return projectNumber;
    }

    public void setProjectNumber(String projectNumber) {
        this.projectNumber = projectNumber;
    }

    /**
     * The team. Valid values are ``editors``, ``owners`` or ``viewers``.
     */
    @Updatable
    @ValidStrings({"editors", "owners", "viewers"})
    public String getTeam() {
        return team;
    }

    public void setTeam(String team) {
        this.team = team;
    }

    @Override
    public void copyFrom(ProjectTeam model) {
        setProjectNumber(model.getProjectNumber());
        setTeam(model.getTeam());
    }

    public ProjectTeam toBucketAccessControlProjectTeam() {
        return new ProjectTeam().setProjectNumber(getProjectNumber()).setTeam(getTeam());
    }
}
