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

import com.google.cloud.compute.v1.ConnectionDraining;
import gyro.core.resource.Diffable;
import gyro.core.resource.Updatable;
import gyro.core.validation.Range;
import gyro.google.Copyable;

public class ComputeConnectionDraining extends Diffable implements Copyable<ConnectionDraining> {

    private Integer drainingTimeoutSec;

    /**
     * The amount of time in seconds to allow existing connections to persist while on unhealthy backend instances. Only applicable if the protocol is not ``UDP``.
     */
    @Updatable
    @Range(min = 0, max = 3600)
    public Integer getDrainingTimeoutSec() {
        return drainingTimeoutSec;
    }

    public void setDrainingTimeoutSec(Integer drainingTimeoutSec) {
        this.drainingTimeoutSec = drainingTimeoutSec;
    }

    @Override
    public void copyFrom(ConnectionDraining model) {
        setDrainingTimeoutSec(model.getDrainingTimeoutSec());
    }

    @Override
    public String primaryKey() {
        return "";
    }

    public ConnectionDraining toConnectionDraining() {
        ConnectionDraining connectionDraining = new ConnectionDraining();
        connectionDraining.setDrainingTimeoutSec(getDrainingTimeoutSec());
        return connectionDraining;
    }
}
