/*
 * Copyright 2020, Perfect Sense, Inc.
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

package gyro.google.compute.base;

import com.google.api.services.compute.model.InstanceGroupManagerActionsSummary;
import gyro.core.resource.Diffable;
import gyro.core.resource.Output;
import gyro.google.Copyable;

public class ComputeInstanceGroupManagerActionsSummary extends Diffable
    implements Copyable<InstanceGroupManagerActionsSummary> {

    private Integer abandoning;

    private Integer creating;

    private Integer creatingWithoutRetries;

    private Integer deleting;

    private Integer none;

    private Integer recreating;

    private Integer refreshing;

    private Integer restarting;

    private Integer verifying;

    /**
     * The total number of instances in the managed instance group that are scheduled to be abandoned. Abandoning an instance removes it from the managed instance group without deleting it.
     */
    @Output
    public Integer getAbandoning() {
        return abandoning;
    }

    public void setAbandoning(Integer abandoning) {
        this.abandoning = abandoning;
    }

    /**
     * The number of instances in the managed instance group that are scheduled to be created or are currently being created. If the group fails to create any of these instances, it tries again until it creates the instance successfully.If you have disabled creation retries, this field will not be populated; instead, the creatingWithoutRetries field will be populated.
     */
    @Output
    public Integer getCreating() {
        return creating;
    }

    public void setCreating(Integer creating) {
        this.creating = creating;
    }

    /**
     * The number of instances that the managed instance group will attempt to create. The group attempts to create each instance only once. If the group fails to create any of these instances, it decreases the group's targetSize value accordingly.
     */
    @Output
    public Integer getCreatingWithoutRetries() {
        return creatingWithoutRetries;
    }

    public void setCreatingWithoutRetries(Integer creatingWithoutRetries) {
        this.creatingWithoutRetries = creatingWithoutRetries;
    }

    /**
     * The number of instances in the managed instance group that are scheduled to be deleted or are currently being deleted.
     */
    @Output
    public Integer getDeleting() {
        return deleting;
    }

    public void setDeleting(Integer deleting) {
        this.deleting = deleting;
    }

    /**
     * The number of instances in the managed instance group that are running and have no scheduled actions.
     */
    @Output
    public Integer getNone() {
        return none;
    }

    public void setNone(Integer none) {
        this.none = none;
    }

    /**
     * The number of instances in the managed instance group that are scheduled to be recreated or are currently being being recreated. Recreating an instance deletes the existing root persistent disk and creates a new disk from the image that is defined in the instance template.
     */
    @Output
    public Integer getRecreating() {
        return recreating;
    }

    public void setRecreating(Integer recreating) {
        this.recreating = recreating;
    }

    /**
     * The number of instances in the managed instance group that are being reconfigured with properties that do not require a restart or a recreate action. For example, setting or removing target pools for the instance.
     */
    @Output
    public Integer getRefreshing() {
        return refreshing;
    }

    public void setRefreshing(Integer refreshing) {
        this.refreshing = refreshing;
    }

    /**
     * The number of instances in the managed instance group that are scheduled to be restarted or are currently being restarted.
     */
    @Output
    public Integer getRestarting() {
        return restarting;
    }

    public void setRestarting(Integer restarting) {
        this.restarting = restarting;
    }

    /**
     * The number of instances in the managed instance group that are being verified. See the managedInstances[].currentAction property in the listManagedInstances method documentation.
     */
    @Output
    public Integer getVerifying() {
        return verifying;
    }

    public void setVerifying(Integer verifying) {
        this.verifying = verifying;
    }

    @Override
    public void copyFrom(InstanceGroupManagerActionsSummary model) {
        setAbandoning(model.getAbandoning());
        setCreating(model.getCreating());
        setCreatingWithoutRetries(model.getCreatingWithoutRetries());
        setDeleting(model.getDeleting());
        setNone(model.getNone());
        setRecreating(model.getRecreating());
        setRefreshing(model.getRefreshing());
        setRestarting(model.getRestarting());
        setVerifying(model.getVerifying());
    }

    @Override
    public String primaryKey() {
        return "";
    }
}
