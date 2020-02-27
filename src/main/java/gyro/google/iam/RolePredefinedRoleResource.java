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

package gyro.google.iam;

import java.util.Set;

import gyro.core.GyroException;
import gyro.core.GyroUI;
import gyro.core.Type;
import gyro.core.resource.Resource;
import gyro.core.scope.State;

/**
 * This resource cannot be managed by Gyro and therefore can only be used via external query.
 */
@Type("iam-role-predefined")
public class RolePredefinedRoleResource extends AbstractRoleResource {

    @Override
    protected boolean doRefresh() throws Exception {
        return false;
    }

    @Override
    protected void doCreate(GyroUI ui, State state) throws Exception {
        throw new GyroException("You cannot create a new predefined role. Create a custom role instead.");
    }

    @Override
    protected void doUpdate(
        GyroUI ui, State state, Resource current, Set<String> changedFieldNames) throws Exception {
        // Intentionally not implemented because #doCreate() is prevented.
    }

    @Override
    protected void doDelete(GyroUI ui, State state) throws Exception {
        // Intentionally not implemented because #doCreate() is prevented.
    }
}
