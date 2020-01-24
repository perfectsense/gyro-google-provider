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

import com.google.api.services.compute.model.NamedPort;
import gyro.core.resource.Diffable;
import gyro.core.validation.Range;
import gyro.core.validation.Regex;
import gyro.core.validation.Required;
import gyro.google.Copyable;

public class ComputeNamedPort extends Diffable implements Copyable<NamedPort> {

    private String name;

    private Integer port;

    /**
     * The name for this named port. The name must be 1-63 characters long, and comply with RFC1035.
     */
    @Regex("[a-z](?:[-a-z0-9]{0,61}[a-z0-9])?")
    @Required
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * The port number, which can be a value between 1 and 65535.
     */
    @Range(min = 1, max = 65535)
    @Required
    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public NamedPort copyTo() {
        NamedPort namedPort = new NamedPort();
        namedPort.setName(getName());
        namedPort.setPort(getPort());
        return namedPort;
    }

    @Override
    public void copyFrom(NamedPort model) {
        setName(model.getName());
        setPort(model.getPort());
    }

    @Override
    public String primaryKey() {
        return String.format("%s:::%d", getName(), getPort());
    }
}
