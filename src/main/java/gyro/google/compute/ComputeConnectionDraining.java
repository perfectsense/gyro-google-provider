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

import java.util.Optional;

import com.google.api.services.compute.model.ConnectionDraining;
import gyro.core.resource.Diffable;
import gyro.core.resource.DiffableType;
import gyro.google.Copyable;

public class ComputeConnectionDraining extends Diffable implements Copyable<ConnectionDraining> {

    private Integer drainingTimeoutSec;

    /**
     * The amount of time in seconds to allow existing connections to persist while on unhealthy
     * backend VMs. Only applicable if the protocol is not UDP. The valid range is [0, 3600].
     */
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
        return String.format(
            "%s::%s",
            DiffableType.getInstance(getClass()).getName(),
            Optional.ofNullable(getDrainingTimeoutSec()).map(e -> e.toString()).orElse(""));
    }

    public ConnectionDraining copyTo() {
        ConnectionDraining connectionDraining = new ConnectionDraining();
        connectionDraining.setDrainingTimeoutSec(getDrainingTimeoutSec());
        return connectionDraining;
    }
}
