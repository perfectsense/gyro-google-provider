/*
 * Copyright 2024, Brightspot.
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

package gyro.google.sqladmin;

import java.util.ArrayList;
import java.util.List;

import gyro.core.resource.Diffable;
import gyro.google.Copyable;

public class SqlServerUserDetails extends Diffable
    implements Copyable<com.google.api.services.sqladmin.model.SqlServerUserDetails> {

    private Boolean disabled;

    private List<String> serverRoles;

    /**
     * If the user has been disabled
     */
    public Boolean getDisabled() {
        return disabled;
    }

    public void setDisabled(Boolean disabled) {
        this.disabled = disabled;
    }

    /**
     * The server roles for this user
     */
    public List<String> getServerRoles() {
        if (serverRoles == null) {
            serverRoles = new ArrayList<>();
        }

        return serverRoles;
    }

    public void setServerRoles(List<String> serverRoles) {
        this.serverRoles = serverRoles;
    }

    @Override
    public String primaryKey() {
        return "";
    }

    @Override
    public void copyFrom(com.google.api.services.sqladmin.model.SqlServerUserDetails model) {

    }
}
