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

import com.google.api.services.compute.model.NamedPort;
import gyro.core.resource.Diffable;
import gyro.core.resource.Updatable;
import gyro.core.validation.Regex;
import gyro.core.validation.Required;
import gyro.google.Copyable;

public class InstanceGroupNamedPort extends Diffable implements Copyable<NamedPort> {

    private String name;
    private Integer port;

    /*
     * The name for this named port.
     */
    @Required
    @Regex("(?:(?:[-a-z0-9]{1,63}\\.)*(?:[a-z](?:[-a-z0-9]{0,61}[a-z0-9])?):)?(?:[0-9]{1,19}|(?:[a-z0-9](?:[-a-z0-9]{0,61}[a-z0-9])?))")
    @Updatable
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /*
     * The port number for this named port.
     */
    @Required
    @Updatable
    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    @Override
    public void copyFrom(NamedPort namedPort) {
        setName(namedPort.getName());
        setPort(namedPort.getPort());
    }

    NamedPort toNamedPort() {
        NamedPort namedPort = new NamedPort();
        namedPort.setName(getName());
        namedPort.setPort(getPort());
        return namedPort;
    }
}
