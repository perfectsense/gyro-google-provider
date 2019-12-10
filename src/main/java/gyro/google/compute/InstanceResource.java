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

import java.util.List;
import java.util.Set;

import com.google.api.services.compute.model.Instance;
import gyro.core.GyroUI;
import gyro.core.resource.Resource;
import gyro.core.scope.State;
import gyro.core.validation.Regex;
import gyro.google.Copyable;

public class InstanceResource extends ComputeResource implements Copyable<Instance> {

    private String name;
    private String machineType;
    private List<InstanceNetworkInterface> networkInterfaces;
    private List<InstanceAttachedDisk> disks;

    /**
     * The name of the resource when initially creating the resource. Must be 1-63 characters, first character must be a lowercase letter and all following characters must be a dash, lowercase letter, or digit, except the last character, which cannot be a dash.
     */
    @Regex("[a-z]([-a-z0-9]*[a-z0-9])?")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * Full or partial URL of the machine type resource to use for this instance, in the format: zones/zone/machineTypes/machine-type. See also `creating custom machine types <https://cloud.google.com/compute/docs/instances/creating-instance-with-custom-machine-type#specifications/>`_.
     */
    public String getMachineType() {
        return machineType;
    }

    public void setMachineType(String machineType) {
        this.machineType = machineType;
    }

    /**
     * List of network configurations for this instance. These specify how interfaces are configured to interact with other network services, such as connecting to the internet. Multiple interfaces are supported.
     */
    public List<InstanceNetworkInterface> getNetworkInterfaces() {
        return networkInterfaces;
    }

    public void setNetworkInterfaces(List<InstanceNetworkInterface> networkInterfaces) {
        this.networkInterfaces = networkInterfaces;
    }

    /**
     * List of disks associated with this instance. Persistent disks must be created before you can assign them.
     */
    public List<InstanceAttachedDisk> getDisks() {
        return disks;
    }

    public void setDisks(List<InstanceAttachedDisk> disks) {
        this.disks = disks;
    }

    @Override
    public boolean doRefresh() throws Exception {
        return false;
    }

    @Override
    public void doCreate(GyroUI ui, State state) throws Exception {

    }

    @Override
    public void doUpdate(GyroUI ui, State state, Resource current, Set<String> changedFieldNames) throws Exception {

    }

    @Override
    public void doDelete(GyroUI ui, State state) throws Exception {

    }

    @Override
    public void copyFrom(Instance model) {

    }
}
