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

import gyro.core.resource.Diffable;
import gyro.google.Copyable;

public class InstanceReference extends Diffable
    implements Copyable<com.google.api.services.sqladmin.model.InstanceReference> {

    private String name;

    private String project;

    private String region;

    /**
     * The name of the Cloud SQL instance being referenced. This does not include the project ID.
     */
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * The project ID of the Cloud SQL instance being referenced. The default is the same project ID as the instance references it.
     */
    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    /**
     * The region of the Cloud SQL instance being referenced.
     */
    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    @Override
    public String primaryKey() {
        return "";
    }

    @Override
    public void copyFrom(com.google.api.services.sqladmin.model.InstanceReference model) {
        setName(model.getName());
        setProject(model.getProject());
        setRegion(model.getRegion());
    }

    com.google.api.services.sqladmin.model.InstanceReference toInstanceReference() {
        com.google.api.services.sqladmin.model.InstanceReference instanceReference = new com.google.api.services.sqladmin.model.InstanceReference();
        instanceReference.setName(getName());
        instanceReference.setProject(getProject());
        instanceReference.setRegion(getRegion());

        return instanceReference;
    }
}
