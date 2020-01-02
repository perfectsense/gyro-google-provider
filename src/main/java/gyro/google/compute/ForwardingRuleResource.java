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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.google.api.services.compute.Compute;
import com.google.api.services.compute.model.ForwardingRule;
import com.google.api.services.compute.model.Operation;
import gyro.core.GyroUI;
import gyro.core.Type;
import gyro.core.resource.Resource;
import gyro.core.scope.State;
import gyro.core.validation.Required;
import gyro.core.validation.ValidationError;

@Type("compute-forwarding-rule")
public class ForwardingRuleResource extends AbstractForwardingRuleResource {

    private String region;
    // TODO: this can be any target resources.
    private RegionTargetHttpProxyResource regionTargetHttpProxy;

    @Required
    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    /**
     * The target resource to receive the matched traffic. For regional forwarding rules,
     * this target must live in the same region as the forwarding rule. For global forwarding rules,
     * this target must be a global load balancing resource. The forwarded traffic must be of a type
     * appropriate to the target object. For INTERNAL_SELF_MANAGED load balancing, only HTTP and HTTPS
     * targets are valid.
     */
    public RegionTargetHttpProxyResource getRegionTargetHttpProxy() {
        return regionTargetHttpProxy;
    }

    public void setRegionTargetHttpProxy(RegionTargetHttpProxyResource regionTargetHttpProxy) {
        this.regionTargetHttpProxy = regionTargetHttpProxy;
    }

    @Override
    public void copyFrom(ForwardingRule forwardingRule) {
        super.copyFrom(forwardingRule);
        setRegion(forwardingRule.getRegion());

        // Multiple type of targets needs to checked when multiple targets are defined.

        setRegionTargetHttpProxy(findById(RegionTargetHttpProxyResource.class, forwardingRule.getTarget()));
    }

    @Override
    protected boolean doRefresh() throws Exception {
        Compute client = createComputeClient();
        copyFrom(client.forwardingRules().get(getProjectId(), getRegion(), getName()).execute());
        return true;
    }

    @Override
    protected void doCreate(GyroUI ui, State state) throws Exception {
        Compute client = createComputeClient();
        Operation response = client.globalForwardingRules().insert(getProjectId(), toForwardingRule()).execute();
        waitForCompletion(client, response);
        refresh();
    }

    @Override
    public void doUpdate(
        GyroUI ui, State state, Resource current, Set<String> changedFieldNames) throws Exception {
        // TODO:
    }

    @Override
    public void doDelete(GyroUI ui, State state) throws Exception {
        Compute client = createComputeClient();
        Operation response = client.globalForwardingRules().delete(getProjectId(), getName()).execute();
        waitForCompletion(client, response);
    }

    @Override
    public List<ValidationError> validate() {
        List<ValidationError> errors = new ArrayList<>();

        // make 'region-target-http-proxy' effectively required.
        if (getRegionTargetHttpProxy() == null) {
            errors.add(new ValidationError(
                this,
                "region-target-http-proxy",
                "region-target-http-proxy is required!"));
        }

        //        if (getTargetHttpProxyResource() != null) {
        //            List<String> ports = getPorts();
        //
        //            if (ports.size() != 1 || !ports.contains("80")) {
        //                errors.add(new ValidationError(this, "ports", "Must be '80'"));
        //            }
        //        }
        return errors;
    }

    @Override
    ForwardingRule toForwardingRule() {
        ForwardingRule forwardingRule = super.toForwardingRule();

        Optional.ofNullable(getRegionTargetHttpProxy())
            .ifPresent(targetHttpProxyResource -> forwardingRule.setTarget(targetHttpProxyResource.getSelfLink()));

        return forwardingRule;
    }
}