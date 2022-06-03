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

package gyro.google.compute;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;

import com.google.api.gax.rpc.NotFoundException;
import com.google.cloud.compute.v1.InstanceGroupManagersClient;
import com.psddev.dari.util.ObjectUtils;
import gyro.core.GyroException;
import gyro.core.resource.Diffable;
import gyro.core.validation.ConflictsWith;
import gyro.core.validation.ValidationError;
import gyro.google.Copyable;

public class ComputeBackendGroup extends Diffable implements Copyable<String> {

    private InstanceGroupResource instanceGroup;
    private InstanceGroupManagerResource instanceGroupManager;
    private RegionInstanceGroupManagerResource regionInstanceGroupManager;

    @ConflictsWith({ "instance-group-manager", "region-instance-group-manager" })
    public InstanceGroupResource getInstanceGroup() {
        return instanceGroup;
    }

    public void setInstanceGroup(InstanceGroupResource instanceGroup) {
        this.instanceGroup = instanceGroup;
    }

    @ConflictsWith({ "instance-group", "region-instance-group-manager" })
    public InstanceGroupManagerResource getInstanceGroupManager() {
        return instanceGroupManager;
    }

    public void setInstanceGroupManager(InstanceGroupManagerResource instanceGroupManager) {
        this.instanceGroupManager = instanceGroupManager;
    }

    @ConflictsWith({ "instance-group-manager", "instance-group" })
    public RegionInstanceGroupManagerResource getRegionInstanceGroupManager() {
        return regionInstanceGroupManager;
    }

    public void setRegionInstanceGroupManager(RegionInstanceGroupManagerResource regionInstanceGroupManager) {
        this.regionInstanceGroupManager = regionInstanceGroupManager;
    }

    @Override
    public String primaryKey() {
        String groupLink = "";

        if (getInstanceGroup() != null) {
            groupLink = ObjectUtils.isBlank(getInstanceGroup().getSelfLink())
                ? getInstanceGroup().getName()
                : getInstanceGroup().getSelfLink();
        } else if (getInstanceGroupManager() != null) {
            groupLink = ObjectUtils.isBlank(getInstanceGroupManager().getSelfLink())
                ? getInstanceGroupManager().getName()
                : getInstanceGroupManager().getSelfLink();
        } else if (getRegionInstanceGroupManager() != null) {
            groupLink = ObjectUtils.isBlank(getRegionInstanceGroupManager().getSelfLink())
                ? getRegionInstanceGroupManager().getName()
                : getRegionInstanceGroupManager().getSelfLink();
        }

        return groupLink;
    }

    @Override
    public void copyFrom(String group) {
        try {
            if (group.contains("/regions/")) {
                setRegionInstanceGroupManager(findById(
                    RegionInstanceGroupManagerResource.class,
                    group.replaceFirst("/instanceGroups/", "/instanceGroupManagers/")));
            } else {
                String[] groupData = group.split("/zones/");
                String[] groupDetailData = groupData[1].split("/instanceGroups/");

                if (isInstanceGroupManager(groupDetailData[0], groupDetailData[1])) {
                    setInstanceGroupManager(findById(
                        InstanceGroupManagerResource.class,
                        group.replaceFirst("/instanceGroups/", "/instanceGroupManagers/")));
                } else {
                    setInstanceGroup(findById(InstanceGroupResource.class, group));
                }
            }
        } catch (Exception ex) {
            throw new GyroException(ex);
        }
    }

    private boolean isInstanceGroupManager(String zone, String name) {
        ComputeBackend parent = (ComputeBackend) parent();
        String project = parent.getProject();

        try (InstanceGroupManagersClient client = parent.getClient()) {
            client.get(project, zone, name);
            return true;
        } catch (NotFoundException ex) {
            return false;
        } catch (GyroException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new GyroException(ex.getMessage(), ex.getCause());
        }
    }

    @Override
    public List<ValidationError> validate(Set<String> configuredFields) {
        List<ValidationError> errors = new ArrayList<>();

        if (Stream.of(getInstanceGroup(), getInstanceGroupManager(), getRegionInstanceGroupManager())
            .allMatch(Objects::isNull)) {
            errors.add(new ValidationError(this, null,
                "One of 'instance-group' or 'instance-group-manager' or 'region-instance-group-manager' is required!"));
        }

        return errors;
    }

    String referenceLink() {
        String referenceLink;

        if (getInstanceGroup() != null) {
            referenceLink = getInstanceGroup().getSelfLink();
        } else if (getInstanceGroupManager() != null) {
            referenceLink = getInstanceGroupManager().getInstanceGroupLink();
        } else {
            referenceLink = getRegionInstanceGroupManager().getInstanceGroupLink();
        }

        return referenceLink;
    }
}
