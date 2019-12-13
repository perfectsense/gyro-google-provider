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

import java.util.Set;

import com.google.api.services.compute.model.ForwardingRule;
import gyro.core.GyroUI;
import gyro.core.Type;
import gyro.core.resource.Output;
import gyro.core.resource.Resource;
import gyro.core.scope.State;

@Type("compute-forwarding-rule")
public class RegionForwardingRuleResource extends ForwardingRuleResource {

    private String region;

    /**
     * URL of the region where the regional forwarding rule resides. This field is not
     * applicable to global forwarding rules. You must specify this field as part of the HTTP request
     * URL. It is not settable as a field in the request body.
     */
    @Output
    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    @Override
    protected boolean doRefresh() throws Exception {
        return false;
    }

    @Override
    protected void doCreate(GyroUI ui, State state) throws Exception {

    }

    @Override
    public void doUpdate(
        GyroUI ui, State state, Resource current, Set<String> changedFieldNames) throws Exception {

    }

    @Override
    public void doDelete(GyroUI ui, State state) throws Exception {

    }

    @Override
    protected void doCopyFrom(ForwardingRule model) {

    }
}
