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

import java.util.Optional;

import com.google.cloud.compute.v1.InstanceGroupManagerAutoHealingPolicy;
import gyro.core.resource.Diffable;
import gyro.core.validation.Range;
import gyro.core.validation.Required;
import gyro.google.Copyable;

public class ComputeInstanceGroupManagerAutoHealingPolicy extends Diffable
    implements Copyable<InstanceGroupManagerAutoHealingPolicy> {

    private HealthCheckResource healthCheck;

    private Integer initialDelaySec;

    /**
     * The health check that signals autohealing.
     *
     * @resource gyro.google.compute.HealthCheckResource
     */
    @Required
    public HealthCheckResource getHealthCheck() {
        return healthCheck;
    }

    public void setHealthCheck(HealthCheckResource healthCheck) {
        this.healthCheck = healthCheck;
    }

    /**
     * The number of seconds that the managed instance group waits before it applies autohealing policies to new instances or recently recreated instances.
     * This initial delay allows instances to initialize and run their startup scripts before the instance group determines that they are UNHEALTHY. This prevents the managed instance group from recreating its instances prematurely. This value must be from range [0, 3600].
     */
    @Range(min = 0, max = 3600)
    public Integer getInitialDelaySec() {
        return initialDelaySec;
    }

    public void setInitialDelaySec(Integer initialDelaySec) {
        this.initialDelaySec = initialDelaySec;
    }

    public InstanceGroupManagerAutoHealingPolicy copyTo() {
        InstanceGroupManagerAutoHealingPolicy.Builder builder = InstanceGroupManagerAutoHealingPolicy.newBuilder();

        if (getHealthCheck() != null) {
            builder.setHealthCheck(getHealthCheck().getSelfLink());
        }

        if (getInitialDelaySec() != null) {
            builder.setInitialDelaySec(getInitialDelaySec());
        }

        return builder.build();
    }

    @Override
    public void copyFrom(InstanceGroupManagerAutoHealingPolicy model) {
        if (model.hasInitialDelaySec()) {
            setInitialDelaySec(model.getInitialDelaySec());
        }

        setHealthCheck(null);
        if (model.hasHealthCheck()) {
            setHealthCheck(findById(HealthCheckResource.class, model.getHealthCheck()));
        }
    }

    @Override
    public String primaryKey() {
        return Optional.ofNullable(getHealthCheck()).map(HealthCheckResource::getSelfLink).orElse("");
    }
}
