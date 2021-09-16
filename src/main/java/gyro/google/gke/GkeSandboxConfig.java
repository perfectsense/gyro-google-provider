/*
 * Copyright 2021, Brightspot.
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

package gyro.google.gke;

import com.google.container.v1.SandboxConfig;
import gyro.core.resource.Diffable;
import gyro.core.validation.Required;
import gyro.core.validation.ValidStrings;
import gyro.google.Copyable;

public class GkeSandboxConfig extends Diffable implements Copyable<SandboxConfig> {

    private SandboxConfig.Type type;

    /**
     * The type of the sandbox to use for the node.
     */
    @Required
    @ValidStrings("GVISOR")
    public SandboxConfig.Type getType() {
        return type;
    }

    public void setType(SandboxConfig.Type type) {
        this.type = type;
    }

    @Override
    public String primaryKey() {
        return "";
    }

    @Override
    public void copyFrom(SandboxConfig model) {
        setType(model.getType());
    }

    SandboxConfig toSandboxConfig() {
        return SandboxConfig.newBuilder().setType(getType()).build();
    }
}
