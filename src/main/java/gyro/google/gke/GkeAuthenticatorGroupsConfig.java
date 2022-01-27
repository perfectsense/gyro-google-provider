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

import com.google.container.v1.AuthenticatorGroupsConfig;
import gyro.core.resource.Diffable;
import gyro.core.validation.DependsOn;
import gyro.core.validation.Required;
import gyro.google.Copyable;

public class GkeAuthenticatorGroupsConfig extends Diffable implements Copyable<AuthenticatorGroupsConfig> {

    private Boolean enabled;
    private String securityGroup;

    /**
     * When set to ``true`` this cluster returns group membership lookups during authentication using a group of security groups.
     */
    @Required
    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * The name of the security group-of-groups to be used.
     */
    @DependsOn("enabled")
    public String getSecurityGroup() {
        return securityGroup;
    }

    public void setSecurityGroup(String securityGroup) {
        this.securityGroup = securityGroup;
    }

    @Override
    public String primaryKey() {
        return "";
    }

    @Override
    public void copyFrom(AuthenticatorGroupsConfig model) throws Exception {
        setEnabled(model.getEnabled());
        setSecurityGroup(model.getSecurityGroup());
    }

    AuthenticatorGroupsConfig toAuthenticatorGroupsConfig() {
        AuthenticatorGroupsConfig.Builder builder = AuthenticatorGroupsConfig.newBuilder().setEnabled(getEnabled());

        if (getSecurityGroup() != null) {
            builder.setSecurityGroup(getSecurityGroup());
        }

        return builder.build();
    }
}
